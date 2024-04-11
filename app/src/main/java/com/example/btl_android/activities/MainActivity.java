package com.example.btl_android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.btl_android.R;
import com.example.btl_android.adapters.NotesAdapter;
import com.example.btl_android.databinding.ActivityMainBinding;
import com.example.btl_android.listeners.NoteListener;
import com.example.btl_android.models.AttachableNote;
import com.example.btl_android.models.PrivateNote;
import com.example.btl_android.models.ReminderNote;
import com.example.btl_android.models.TaskNote;
import com.example.btl_android.models.TodoListNote;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.Helpers;
import com.example.btl_android.utilities.NoteComparator;
import com.example.btl_android.utilities.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NoteListener
{
	//      TODO: Sort Notes into Categories View

	private ActivityMainBinding binding;
	private List<_DefaultNote> noteList;
	private NotesAdapter listAdapter;
	private PreferenceManager preferenceManager;
	private boolean isEditing = false;
	private boolean isReversed = true;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//      Pref Manager
		this.preferenceManager = new PreferenceManager(this);

		//      Night/Light/System Mode
		String appTheme = this.preferenceManager.getString(Constants.SETTINGS_APP_THEME);

		if (appTheme == null)
		{
			this.preferenceManager.putString(Constants.SETTINGS_APP_THEME, "0");

			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		}
		else
		{
			int position = Integer.parseInt(appTheme);

			if (position == 0)
			{
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
			}
			else if (position == 1)
			{
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			}
			else if (position == 2)
			{
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			}
		}

		//      Set up Binding
		this.binding = ActivityMainBinding.inflate(getLayoutInflater());
		this.setContentView(this.binding.getRoot());

		//      Set up Note List
		this.noteList = new ArrayList<>();
		this.listAdapter = new NotesAdapter(this.noteList, this, this);
		this.binding.noteRecyclerView.setAdapter(this.listAdapter);

		//      Set up Spinner
		ArrayAdapter<CharSequence> arrayAdapter =
				ArrayAdapter.createFromResource(this, R.array.sortType, R.layout.spinner_item_right);
		arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
		this.binding.spinnerSort.setAdapter(arrayAdapter);
		this.binding.spinnerSort.setSelection(0);

		//      Is Reversed
		this.isReversed = this.preferenceManager.getBoolean(Constants.SETTINGS_SORT_IS_REVERSED);
		if (this.isReversed)
		{
			this.binding.buttonSort.setImageResource(R.drawable.icon_arrowup);
		}
		else
		{
			this.binding.buttonSort.setImageResource(R.drawable.icon_arrowdown);
		}

		//      Set Up NoteComparator
		NoteComparator.isReversed = this.isReversed;
		NoteComparator.taskNoteSortToBottomOnCompletion = this.preferenceManager.getBoolean(
				Constants.TASK_NOTE_SETTINGS_SORT_TO_BOTTOM_ON_COMPLETION);
		NoteComparator.taskNoteDeleteOnCompletion = this.preferenceManager.getBoolean(
				Constants.TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION);

		//      Check if this is new Start Up
		if (!this.preferenceManager.getBoolean(Constants.APP_NOT_FIRST_START_UP))
		{
			Intent intent = new Intent(this, StartUpSettingsActivity.class);

			startActivity(intent);

			this.preferenceManager.putBoolean(Constants.APP_NOT_FIRST_START_UP, true);
		}
		else
		{
			this.ReadFiles();
			this.ReadFilesByHelpers();
			this.SortNotes();
		}

		this.SetListeners();
	}

	private void SetListeners()
	{
		if (this.isEditing)
		{
			this.binding.newButton.setOnClickListener(view ->
			{
				this.isEditing = false;

				this.ChangeEditingLayout();
			});

			this.binding.settingsButton.setOnClickListener(view ->
			{
				PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.settingsButton);

				popupMenu.getMenuInflater().inflate(R.menu.menu_activity_main_note, popupMenu.getMenu());

				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					@Override public boolean onMenuItemClick(MenuItem menuItem)
					{
						int id = menuItem.getItemId();

						if (id == R.id.menuItemDelete)
						{
							MainActivity.this.onNoteDelete();
						}
						else if (id == R.id.menuItemDuplicate)
						{
							MainActivity.this.onNoteDuplicate();
						}
						else if (id == R.id.menuItemFavorite)
						{
							MainActivity.this.onNoteFavorite();
						}

						return true;
					}
				});

				// Showing the popup menu
				popupMenu.show();
			});
		}
		else
		{
			//      No Dir!
			this.binding.noDirLayout.setOnClickListener(view ->
			{
				Intent intent = new Intent(this, StartUpSettingsActivity.class);

				startActivity(intent);

				this.preferenceManager.putBoolean(Constants.APP_NOT_FIRST_START_UP, true);
			});

			//      Sort Button!
			this.binding.buttonSort.setOnClickListener(view ->
			{
				this.isReversed = !this.isReversed;

				if (this.isReversed)
				{
					this.binding.buttonSort.setImageResource(R.drawable.icon_arrowup);
				}
				else
				{
					this.binding.buttonSort.setImageResource(R.drawable.icon_arrowdown);
				}

				this.preferenceManager.putBoolean(Constants.SETTINGS_SORT_IS_REVERSED,
						this.isReversed
				);
				NoteComparator.isReversed = this.isReversed;

				this.SortNotes();
			});

			//      Spinner!
			this.binding.spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id
				)
				{
					switch (position)
					{
						case 0:
							_DefaultNote.sortType = _DefaultNote.SortType.ByTitle;

							break;
						case 1:
							_DefaultNote.sortType = _DefaultNote.SortType.ByDateCreated;

							break;
						case 2:
							_DefaultNote.sortType = _DefaultNote.SortType.ByDateModified;

							break;
					}

					MainActivity.this.preferenceManager.putString(Constants.SETTINGS_SORT_TYPE, _DefaultNote.sortType.toString());

					MainActivity.this.SortNotes();

					MainActivity.this.listAdapter.notifyDataSetChanged();
				}

				@Override public void onNothingSelected(AdapterView<?> parent)
				{

				}
			});

			//      New Note Button!
			this.binding.newButton.setOnClickListener(view ->
			{
				Intent intent = new Intent(this, CreateNoteActivity.class);

				startActivity(intent);
			});

			//      Settings!
			this.binding.settingsButton.setOnClickListener(view ->
			{
				Intent intent = new Intent(this, SettingsActivity.class);

				startActivity(intent);
			});
		}
	}

	@Override protected void onResume()
	{
		super.onResume();

		this.noteList.clear();

		this.ReadFiles();
		this.ReadFilesByHelpers();
		this.SortNotes();
	}

	@Override public void onBackPressed()
	{
		if (this.isEditing)
		{
			this.isEditing = false;

			this.ChangeEditingLayout();
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK || data == null)
		{
			return;
		}
		else
		{
			this.ReadFiles();
			this.ReadFilesByHelpers();
			this.SortNotes();
		}
	}

	@Override public void onNoteClick(int position)
	{
		if (this.isEditing)
		{
			//      Nah I am editting

			return;
		}

		//      Clicking!
		_DefaultNote defaultNote = this.noteList.get(position);

		//      TODO: Các bạn bổ sung
		if (defaultNote instanceof AttachableNote)
		{
			Intent intent = new Intent(this, AttachableNoteActivity.class);

			intent.putExtra(Constants.BUNDLE_FILENAME_KEY, defaultNote.getFileName());

			startActivity(intent);
		}
		else if (defaultNote instanceof TaskNote)
		{
			Intent intent = new Intent(this, TaskNoteActivity.class);

			intent.putExtra(Constants.BUNDLE_FILENAME_KEY, defaultNote.getFileName());

			startActivity(intent);
		}
		else if (defaultNote instanceof PrivateNote)
		{
			Intent intent = new Intent(this, EnterPassWordActivity.class);

			intent.putExtra(Constants.BUNDLE_FILENAME_KEY, defaultNote.getFileName());

			startActivity(intent);
		}
		else if (defaultNote instanceof ReminderNote)
		{
			Intent intent = new Intent(this, ReminderNoteActivity.class);

			intent.putExtra(Constants.BUNDLE_FILENAME_KEY, defaultNote.getFileName());

			startActivity(intent);
		}
		else if (defaultNote instanceof TodoListNote)
		{
			Intent intent = new Intent(this, TodoNoteActivity.class);

			//			Log.d("TodoNote intent 1", "onCreate: " + defaultNote.getTitle());

			intent.putExtra(Constants.TODO_NOTE_KEY, (TodoListNote) defaultNote);
			intent.putExtra(Constants.IS_TODO_NOTE_EDITING_KEY,
					this.preferenceManager.getBoolean(Constants.SETTINGS_NOTE_DEFAULT_IS_EDITING)
			);

			startActivity(intent);
		}
		else if (defaultNote instanceof _DefaultNote)
		{
			Intent intent = new Intent(this, DefaultNoteActivity.class);

			intent.putExtra(Constants.BUNDLE_FILENAME_KEY, defaultNote.getFileName());

			startActivity(intent);
		}
		else
		{
			Log.d("MainActivityTemp", "onNoteClick: Unknown Note");

			Toast.makeText(this, "Note Type not Found", Toast.LENGTH_SHORT).show();
		}
	}

	@Override public void onNoteLongClick()
	{
		if (this.isEditing)
		{
			return;
		}

		this.isEditing = true;

		this.ChangeEditingLayout();
	}


	private void onNoteDelete()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Delete Confirmation");
		builder.setMessage("Are you sure you want to delete the Note(s)?");

		builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener()
		{
			@Override public void onClick(DialogInterface dialog, int which)
			{
				for (int i = 0; i < MainActivity.this.noteList.size(); i++)
				{
					_DefaultNote defaultNote = MainActivity.this.noteList.get(i);

					if (defaultNote.isChecked() && defaultNote instanceof TodoListNote)
					{
						Log.d("Delete Confirmation", "onClick: :>>");
						MainActivity.this.noteList.remove(i--);

						MainActivity.this.DeleteTodoListNoteById(((TodoListNote) defaultNote).getId());
					}
					else if (defaultNote.isChecked())
					{
						String fileName = defaultNote.getFileName();

						if (_DefaultNote.DeleteFromStorage(MainActivity.this, fileName))
						{
							MainActivity.this.noteList.remove(i--);
						}
						else
						{
							Log.d("MainActivity.onNoteDelete()", "FileName = " + fileName);

							Toast.makeText(MainActivity.this, "Failed to delete note",
									Toast.LENGTH_SHORT
							).show();
						}
					}
				}

				MainActivity.this.SortNotes();

				MainActivity.this.isEditing = false;
				MainActivity.this.ChangeEditingLayout();
			}
		});

		builder.setNegativeButton("Nah", null);

		builder.create().show();
	}


	private void onNoteDuplicate()
	{
		for (int i = 0; i < this.noteList.size(); i++)
		{
			_DefaultNote defaultNote = MainActivity.this.noteList.get(i);

			if (defaultNote.isChecked())
			{
				//      TODO: Các bạn bổ sung

				//				Toast.makeText(this, defaultNote.toString(), Toast.LENGTH_SHORT).show();

				if (defaultNote instanceof AttachableNote)
				{
					AttachableNote attachableNote = new AttachableNote((AttachableNote) defaultNote);

					if (!attachableNote.WriteToStorage(this, true))
					{
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();

						continue;
					}

					this.noteList.add(i++ + 1, attachableNote);
				}
				else if (defaultNote instanceof TaskNote)
				{
					TaskNote taskNote = (TaskNote) defaultNote;

					if (!taskNote.WriteToStorage(this, true))
					{
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();

						continue;
					}

					this.noteList.add(i++ + 1, taskNote);
				}
				else if (defaultNote instanceof PrivateNote)
				{
					PrivateNote privateNote = (PrivateNote) defaultNote;

					if (!privateNote.WriteToStorage(this, true))
					{
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();
						continue;
					}

					this.noteList.add(i++ + 1, privateNote);
				}
				else if (defaultNote instanceof ReminderNote)
				{
					ReminderNote reminderNote = (ReminderNote) defaultNote;

					if (!reminderNote.WriteToStorage(this, true))
					{
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();
						continue;
					}

					this.noteList.add(i++ + 1, reminderNote);
				}
				else if (defaultNote instanceof _DefaultNote)
				{

					if (!defaultNote.WriteToStorage(this, true))
					{
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();

						continue;
					}

					this.noteList.add(i++ + 1, defaultNote);
				}
				else if (defaultNote instanceof TodoListNote)
				{
					TodoListNote todoListNote = (TodoListNote) defaultNote;
					TodoListNote newTodoListNote = new TodoListNote(todoListNote);
					newTodoListNote.setRandomId();

					Helpers<TodoListNote> helpers_1 = new Helpers<>();
					Helpers<ArrayList<TodoListNote>> helpers_2 = new Helpers<>();

					ArrayList<TodoListNote> todoListNotes =
							helpers_1.ReadFile(this, Constants.JSON_TODO_NOTE_NAME_FILE,
									TodoListNote.class
							);
					todoListNotes.add(newTodoListNote);

					String json = helpers_2.transferObjectToJson(todoListNotes);
					boolean isWritten = helpers_2.WriteFile(this, Constants.JSON_TODO_NOTE_NAME_FILE, json);


					if (!isWritten)
					{
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();
					}

					this.noteList.add(i++ + 1, newTodoListNote);
				}
				else
				{
					Log.d("MainActivityTemp", "onNoteClick: Unknown Note");

					Toast.makeText(this, "Note Type not Found", Toast.LENGTH_SHORT).show();
				}
			}
		}

		this.SortNotes();

		this.isEditing = false;
		this.ChangeEditingLayout();
	}

	private void onNoteFavorite()
	{
		for (int i = 0; i < this.noteList.size(); i++)
		{
			_DefaultNote defaultNote = MainActivity.this.noteList.get(i);

			if (defaultNote.isChecked())
			{
				defaultNote.setFavorite(!defaultNote.isFavorite());

				//      TODO: Các bạn bổ sung
				if (defaultNote instanceof AttachableNote)
				{
					AttachableNote attachableNote = (AttachableNote) defaultNote;

					if (!attachableNote.WriteToStorage(MainActivity.this, false))
					{
						Log.d("AttachableNoteViewHolderTemp", "Failed to write to storage");

						Toast.makeText(MainActivity.this, "Failed to favorite the Note",
								Toast.LENGTH_SHORT
						).show();
					}
				}
				else if (defaultNote instanceof TaskNote)
				{
					TaskNote taskNote = (TaskNote) defaultNote;

					if (!taskNote.WriteToStorage(MainActivity.this, false))
					{
						Log.d("AttachableNoteViewHolderTemp", "Failed to write to storage");

						Toast.makeText(MainActivity.this, "Failed to favorite the Note",
								Toast.LENGTH_SHORT
						).show();
					}
				}
				else if (defaultNote instanceof PrivateNote)
				{
					PrivateNote privateNote = (PrivateNote) defaultNote;

					if (!privateNote.WriteToStorage(MainActivity.this, false))
					{
						Log.d("PrivateNoteViewHolderTemp", "Failed to write to storage");

						Toast.makeText(MainActivity.this, "Failed to favorite the Note",
								Toast.LENGTH_SHORT
						).show();
					}
				}
				else if (defaultNote instanceof ReminderNote)
				{
					ReminderNote reminderNote = (ReminderNote) defaultNote;

					if (!reminderNote.WriteToStorage(MainActivity.this, false))
					{
						Log.d("PrivateNoteViewHolderTemp", "Failed to write to storage");

						Toast.makeText(MainActivity.this, "Failed to favorite the Note",
								Toast.LENGTH_SHORT
						).show();
					}
				}
				else if (defaultNote instanceof _DefaultNote)
				{
					if (!defaultNote.WriteToStorage(MainActivity.this, false))
					{
						Log.d("DefaultNoteViewHolderTemp", "Failed to write to storage");

						Toast.makeText(MainActivity.this, "Failed to favorite the Note",
								Toast.LENGTH_SHORT
						).show();
					}
				}
				else if (defaultNote instanceof TodoListNote)
				{
					TodoListNote todoListNote = (TodoListNote) defaultNote;

					Helpers<TodoListNote> helpers_1 = new Helpers<>();
					Helpers<ArrayList<TodoListNote>> helpers_2 = new Helpers<>();

					ArrayList<TodoListNote> todoListNotes =
							helpers_1.ReadFile(this, Constants.JSON_TODO_NOTE_NAME_FILE,
									TodoListNote.class
							);
					Log.d("favorite the Note", "onNoteFavorite: " + todoListNotes.size());
					todoListNotes.forEach(todoListNote1 ->
					{
						if (todoListNote1.getId().equals(todoListNote.getId()))
						{
							Log.d("favorite the Note", "onNoteFavorite: ");
							todoListNote1.setFavorite(todoListNote.isFavorite());
						}
					});

					String json = helpers_2.transferObjectToJson(todoListNotes);
					boolean isWritten = helpers_2.WriteFile(this, Constants.JSON_TODO_NOTE_NAME_FILE, json);

					if (!isWritten)
					{
						Toast.makeText(MainActivity.this, "Failed to favorite the Note",
								Toast.LENGTH_SHORT
						).show();
					}
				}
				else
				{
					Log.d("MainActivityTemp", "onNoteFav: Unknown Note");

					Toast.makeText(this, "Note Type not Found", Toast.LENGTH_SHORT).show();
				}
			}
		}

		this.isEditing = false;
		this.ChangeEditingLayout();

		this.SortNotes();
	}

	private void ChangeEditingLayout()
	{
		Collections.sort(this.noteList, new NoteComparator());
		this.listAdapter.SetEditing(this.isEditing);

		if (this.isEditing)
		{
			this.binding.newButton.setImageResource(R.drawable.icon_back);
			this.binding.settingsButton.setImageResource(R.drawable.icon_more);

			this.binding.sortBar.setVisibility(View.GONE);

			this.SetListeners();
		}
		else
		{
			this.binding.newButton.setImageResource(R.drawable.icon_add);
			this.binding.settingsButton.setImageResource(R.drawable.icon_settings);

			this.binding.sortBar.setVisibility(View.VISIBLE);

			this.SetListeners();
		}
	}

	public void ReadFiles()
	{
		//      Initiate
		String directory = this.preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
		this.noteList.clear();

		//      Check if no Directory or unreadable Directory
		if (directory == null)
		{
			this.binding.noDirLayout.setVisibility(View.VISIBLE);

			this.listAdapter.notifyDataSetChanged();

			return;
		}
		else
		{
			this.binding.noDirLayout.setVisibility(View.GONE);
		}

		//      Get Files
		File dir = new File(directory);
		File[] files = dir.listFiles();

		//      Check if any Files scanned
		if (files == null || files.length == 0)
		{
			this.binding.noNoteLayout.setVisibility(View.VISIBLE);

			this.listAdapter.notifyDataSetChanged();
		}
		else
		{
			this.binding.noNoteLayout.setVisibility(View.GONE);

			for (File file : files)
			{
				String noteName = file.getName();

				if (noteName.contains("AttachableNote"))
				{
					AttachableNote attachableNote = AttachableNote.ReadFromStorage(this, file.getName());

					if (attachableNote != null)
					{
						this.noteList.add(attachableNote);
					}
				}
				else if (noteName.contains("TaskNote"))
				{
					TaskNote taskNote = TaskNote.ReadFromStorage(this, file.getName());

					if (taskNote != null)
					{
						this.noteList.add(taskNote);
					}
				}
				else if (noteName.contains("PrivateNote"))
				{
					PrivateNote privateNote = PrivateNote.ReadFromStorage(this, file.getName());

					if (privateNote != null)
					{
						this.noteList.add(privateNote);
					}
				}
				else if (noteName.contains("ReminderNote"))
				{
					ReminderNote reminderNote = ReminderNote.ReadFromStorage(this, file.getName());

					if (reminderNote != null)
					{
						this.noteList.add(reminderNote);
					}
				}
				else if (noteName.contains("DefaultNote"))
				{
					_DefaultNote defaultNote = _DefaultNote.ReadFromStorage(this, file.getName());

					if (defaultNote != null)
					{
						this.noteList.add(defaultNote);
					}
				}
				else if (!noteName.contains("Note"))
				{
					//      is File even a Note?

					Toast.makeText(this, "Note Type unidentified, can not Read " + noteName,
							Toast.LENGTH_SHORT
					).show();
				}
			}

			//      Sort Type...
			String sortType = this.preferenceManager.getString(Constants.SETTINGS_SORT_TYPE);

			_DefaultNote.sortType =
					_DefaultNote.SortType.valueOf(sortType != null ? sortType : _DefaultNote.SortType.ByTitle.toString());

			switch (_DefaultNote.sortType)
			{
				case ByTitle:
					this.binding.spinnerSort.setSelection(0);

					break;

				case ByDateCreated:
					this.binding.spinnerSort.setSelection(1);

					break;
				case ByDateModified:
					this.binding.spinnerSort.setSelection(2);

					break;
			}
		}
	}

	private void ReadFilesByHelpers()
	{
		Helpers<TodoListNote> helpers = new Helpers<>();
		ArrayList<TodoListNote> todoListNotes = helpers.ReadFile(this, Constants.JSON_TODO_NOTE_NAME_FILE, TodoListNote.class);

		if (todoListNotes != null && todoListNotes.size() != 0)
		{
			//			todoListNotes.forEach(note -> Log.d("ReadFilesByHelpers",
			//					"ReadFilesByHelpers: " + note.getTodoNotes().size()));

			this.noteList.addAll(todoListNotes);
			this.SortNotes();
		}
	}

	public void SortNotes() {
		//      Necessities
		NoteComparator noteComparator = new NoteComparator();

		Collections.sort(this.noteList, noteComparator);

		this.listAdapter.notifyDataSetChanged();
	}

	private void DeleteTodoListNoteById(UUID id)
	{
		Helpers<TodoListNote> helpers_1 = new Helpers<>();
		Helpers<ArrayList<TodoListNote>> helpers_2 = new Helpers<>();
		ArrayList<TodoListNote> todoListNotes = helpers_1.ReadFile(this, Constants.JSON_TODO_NOTE_NAME_FILE, TodoListNote.class);

		if (todoListNotes != null && todoListNotes.size() != 0)
		{
			todoListNotes.removeIf(note -> note.getId().equals(id));

			String json = helpers_2.transferObjectToJson(todoListNotes);
			boolean isWritten = helpers_2.WriteFile(this, Constants.JSON_TODO_NOTE_NAME_FILE, json);

			if (!isWritten)
			{
				Log.d("DeleteTodoListNoteById", "Failed to delete note");
				Toast.makeText(MainActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public boolean isEditing()
	{
		return this.isEditing;
	}
}