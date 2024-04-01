package com.example.btl_android.activities;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
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
import com.example.btl_android.models.TaskNote;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.NoteComparator;
import com.example.btl_android.utilities.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener
{
	//      TODO: Sort Notes into Categories View

	private ActivityMainBinding binding;
	private List<_DefaultNote> noteList;
	private NotesAdapter listAdapter;
	private PreferenceManager preferenceManager;
	private boolean isEditting = false;
	private boolean isAscending = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.preferenceManager = new PreferenceManager(this);
		//		this.preferenceManager.putString(Constants.PREFERENCE_STORAGE_DIRECTORY, Environment.getExternalStorageDirectory() + "/EvaNote/");


		//      TODO: Settings and Applying them
		String appTheme = this.preferenceManager.getString(Constants.SETTINGS_APP_THEME);

		if (appTheme == null)
		{
			this.preferenceManager.putString(Constants.SETTINGS_APP_THEME, "0");

			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		}
		else {
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

		this.binding = ActivityMainBinding.inflate(getLayoutInflater());
		this.setContentView(this.binding.getRoot());

		this.noteList = new ArrayList<>();

		this.listAdapter = new NotesAdapter(this.noteList, this, this);
		this.binding.noteRecyclerView.setAdapter(this.listAdapter);

		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
				this,
				R.array.sortType,
				R.layout.spinner_item
		);
		arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
		this.binding.spinnerSort.setAdapter(arrayAdapter);
		this.binding.spinnerSort.setSelection(0);

		this.ReadFiles();
		this.SetListeners();
	}

	private void SetListeners()
	{
		if (this.isEditting) {
			this.binding.newButton.setOnClickListener(view ->
			{
				this.isEditting = false;

				this.ChangeEdittingLayout();
			});

			this.binding.settingsButton.setOnClickListener(view ->
			{
				PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.settingsButton);

				popupMenu.getMenuInflater().inflate(R.menu.menu_activity_main_note, popupMenu.getMenu());

				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					@Override
					public boolean onMenuItemClick(MenuItem menuItem)
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
		else {
			//      Sort Button!
			this.binding.buttonSort.setOnClickListener(view -> {
				this.isAscending = !this.isAscending;

				if (this.isAscending)
				{
					this.binding.buttonSort.setImageResource(R.drawable.icon_arrowup);
				}
				else
				{
					this.binding.buttonSort.setImageResource(R.drawable.icon_arrowdown);
				}

				this.preferenceManager.putBoolean(Constants.SETTINGS_SORT_ASCENDING, this.isAscending);

				Collections.reverse(MainActivity.this.noteList);

				this.listAdapter.notifyDataSetChanged();
			});

			//      Spinner!
			this.binding.spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
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

					Collections.sort(MainActivity.this.noteList, new NoteComparator());

					if (!MainActivity.this.isAscending)
					{
						Collections.reverse(MainActivity.this.noteList);
					}

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
//			{
//				PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.settingsButton);
//
//				popupMenu.getMenuInflater().inflate(R.menu.menu_activity_main, popupMenu.getMenu());
//
//				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
//				{
//					@Override
//					public boolean onMenuItemClick(MenuItem menuItem)
//					{
//						int id = menuItem.getItemId();
//
//						if (id == R.id.menuItemReqRES)
//						{
//							CheckPermission(READ_EXTERNAL_STORAGE, REQUEST_CODE_READ_EXTERNAL_STORAGE);
//						}
//						else if (id == R.id.menuItemReqWES)
//						{
//							CheckPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
//						}
//						else if (id == R.id.menuItemReqMES)
//						{
//							CheckPermission(MANAGE_EXTERNAL_STORAGE, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
//						}
//
//						return true;
//					}
//				});
//
//				// Showing the popup menu
//				popupMenu.show();
//			});
			{
				Intent intent = new Intent(this, SettingsActivity.class);

				startActivity(intent);
			});
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		this.noteList.clear();

		this.ReadFiles();
	}

	@Override public void onBackPressed()
	{
		if (this.isEditting)
		{
			this.isEditting = false;

			this.ChangeEdittingLayout();
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
		else {
			this.ReadFiles();
		}
	}

	@Override
	public void onNoteClick(int position)
	{
		if (this.isEditting) {
			//      Nah I am editting

			return;
		}

		//      Clicking!
		_DefaultNote defaultNote = this.noteList.get(position);

		//      TODO: Các bạn bổ sung
		if (defaultNote instanceof AttachableNote) {
			Intent intent = new Intent(this, AttachableNoteActivity.class);

			intent.putExtra(Constants.BUNDLE_FILENAME_KEY, defaultNote.getFileName());

			startActivity(intent);
		}
		else if (defaultNote instanceof TaskNote) {
			Intent intent = new Intent(this, TaskNoteActivity.class);

			intent.putExtra(Constants.BUNDLE_FILENAME_KEY, defaultNote.getFileName());

			startActivity(intent);
		}
		else {
			Log.d("MainActivityTemp", "onNoteClick: Unknown Note");

			Toast.makeText(this, "Note Type not Found", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNoteLongClick() {
		if (this.isEditting)
		{
			return;
		}

		this.isEditting = true;

		this.ChangeEdittingLayout();
	}


	private void onNoteDelete()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Delete Confirmation");
		builder.setMessage("Are you sure you want to delete the Note(s)?");

		builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (int i = 0; i < MainActivity.this.noteList.size(); i++)
				{
					_DefaultNote defaultNote = MainActivity.this.noteList.get(i);

					if (defaultNote.isChecked())
					{
						String fileName = defaultNote.getFileName();

						if (_DefaultNote.DeleteFromStorage(MainActivity.this, fileName)) {
							MainActivity.this.noteList.remove(i--);
						}
						else {
							Log.d("AttachableNoteActivityTemp", "FileName = " + fileName);

							Toast.makeText(MainActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
						}
					}
				}

				MainActivity.this.isEditting = false;
				MainActivity.this.ChangeEdittingLayout();
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

			if (defaultNote.isChecked()) {
				//      TODO: Các bạn bổ sung

//				Toast.makeText(this, defaultNote.toString(), Toast.LENGTH_SHORT).show();

				if (defaultNote instanceof AttachableNote) {
					AttachableNote attachableNote = new AttachableNote((AttachableNote) defaultNote);

					if (!attachableNote.WriteToStorage(this, true)) {
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();

						continue;
					}

					this.noteList.add(i++ + 1, attachableNote);
				}
				else if (defaultNote instanceof TaskNote) {
					TaskNote taskNote = (TaskNote) defaultNote;

					if (!taskNote.WriteToStorage(this, true)) {
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();

						continue;
					}

					this.noteList.add(i++ + 1, taskNote);
				}
				else {
					Log.d("MainActivityTemp", "onNoteClick: Unknown Note");

					Toast.makeText(this, "Note Type not Found", Toast.LENGTH_SHORT).show();
				}
			}
		}

		MainActivity.this.isEditting = false;
		MainActivity.this.ChangeEdittingLayout();
	}

	private void onNoteFavorite() {
		for (int i = 0; i < this.noteList.size(); i++)
		{
			_DefaultNote defaultNote = MainActivity.this.noteList.get(i);

			if (defaultNote.isChecked())
			{
				defaultNote.setFavorite(!defaultNote.isFavorite());

				//      TODO: Các bạn bổ sung
				if (defaultNote instanceof AttachableNote) {
					AttachableNote attachableNote = (AttachableNote) defaultNote;

					if (!attachableNote.WriteToStorage(MainActivity.this, false))
					{
						Log.d("AttachableNoteViewHolderTemp", "Failed to write to storage");

						Toast.makeText(MainActivity.this, "Failed to favorite the Note", Toast.LENGTH_SHORT).show();
					}
				}
				else if (defaultNote instanceof TaskNote) {
					TaskNote taskNote = (TaskNote) defaultNote;

					if (!taskNote.WriteToStorage(MainActivity.this, false))
					{
						Log.d("AttachableNoteViewHolderTemp", "Failed to write to storage");

						Toast.makeText(MainActivity.this, "Failed to favorite the Note", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}

		MainActivity.this.isEditting = false;
		MainActivity.this.ChangeEdittingLayout();
	}

	private void ChangeEdittingLayout() {
		this.listAdapter.SetEditing(this.isEditting);

		if (this.isEditting) {
			this.binding.newButton.setImageResource(R.drawable.icon_back);
			this.binding.settingsButton.setImageResource(R.drawable.icon_more);

			this.binding.sortBar.setVisibility(View.GONE);

			this.SetListeners();
		}
		else {
			this.binding.newButton.setImageResource(R.drawable.icon_add);
			this.binding.settingsButton.setImageResource(R.drawable.icon_settings);

			this.binding.sortBar.setVisibility(View.VISIBLE);

			this.SetListeners();
		}
	}

	private void ReadFiles() {
		String directory = this.preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
		File dir = new File(directory);
		File[] files = dir.listFiles();

		if (files == null || files.length == 0) {
			this.binding.noNoteLayout.setVisibility(View.VISIBLE);

			this.listAdapter.notifyDataSetChanged();
			this.binding.noteRecyclerView.scrollToPosition(0);
		}
		else {
			this.binding.noNoteLayout.setVisibility(View.GONE);

			for (File file : files)
			{
				String noteName = file.getName();

				if (noteName.contains("AttachableNote")) {
					AttachableNote attachableNote = AttachableNote.ReadFromStorage(this, file.getName());

					if (attachableNote != null)
					{
						this.noteList.add(attachableNote);
					}
				}
				else if (noteName.contains("TaskNote")) {
					TaskNote taskNote = TaskNote.ReadFromStorage(this, file.getName());

					if (taskNote != null)
					{
						this.noteList.add(taskNote);
					}
				}
			}

			String sortType = this.preferenceManager.getString(Constants.SETTINGS_SORT_TYPE);

			_DefaultNote.sortType =
					_DefaultNote.SortType.valueOf(
							sortType != null ?
									sortType :
									_DefaultNote.SortType.ByTitle.toString()
					);

			//      Sort Type
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

			//      Sort Button
			this.isAscending =
					this.preferenceManager.getBoolean(Constants.SETTINGS_SORT_ASCENDING);
			if (this.isAscending)
			{
				this.binding.buttonSort.setImageResource(R.drawable.icon_arrowup);
			}
			else
			{
				this.binding.buttonSort.setImageResource(R.drawable.icon_arrowdown);
			}

			Collections.sort(this.noteList, new NoteComparator());

			if (!this.isAscending)
			{
				Collections.reverse(this.noteList);
			}

			this.listAdapter.notifyDataSetChanged();
			this.binding.noteRecyclerView.scrollToPosition(0);
		}
	}

	public boolean isEditting() {
		return this.isEditting;
	}
}