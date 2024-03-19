package com.example.btl_android.activities;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.btl_android.R;
import com.example.btl_android.adapters.NotesAdapter;
import com.example.btl_android.databinding.ActivityMainBinding;
import com.example.btl_android.listeners.NoteListener;
import com.example.btl_android.models.AttachableNote;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener
{
	//      TODO: Sort Notes into Categories View

	private ActivityMainBinding binding;
	private List<_DefaultNote> noteList;
	private NotesAdapter listAdapter;
	private PreferenceManager preferenceManager;
	private boolean isEditting = false;
	private final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 0;
	private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
	private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//      TEST
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

		this.binding = ActivityMainBinding.inflate(getLayoutInflater());
		this.setContentView(this.binding.getRoot());

		this.noteList = new ArrayList<>();

		this.listAdapter = new NotesAdapter(this.noteList, this);
		this.binding.noteRecyclerView.setAdapter(this.listAdapter);

		this.preferenceManager = new PreferenceManager(this);
//		this.preferenceManager.putString(Constants.PREFERENCE_STORAGE_DIRECTORY, Environment.getExternalStorageDirectory() + "/EvaNote/");

		String directory = this.preferenceManager.getString(Constants.PREFERENCE_STORAGE_DIRECTORY);

		//      TODO: Make an EvaNoteConfig
		//          List of FileNames -> NoteTypes

		File dir = new File(directory);
		File[] files = dir.listFiles();

		if (files == null || files.length == 0) {
			//      TODO: Make a View of Create a Note :3 or something


		}
		else {
			for (File file : files)
			{
				AttachableNote attachableNote = AttachableNote.ReadFromStorage(this, file.getName());

				if (attachableNote != null)
				{
					this.noteList.add(attachableNote);
				}
			}

			this.listAdapter.notifyItemRangeInserted(0, this.noteList.size());
			this.binding.noteRecyclerView.scrollToPosition(0);
		}

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
			this.binding.newButton.setOnClickListener(view ->
			{
				Intent intent = new Intent(this, CreateNoteActivity.class);

				startActivity(intent);
			});

			this.binding.settingsButton.setOnClickListener(view ->
			{
				PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.settingsButton);

				popupMenu.getMenuInflater().inflate(R.menu.menu_activity_main, popupMenu.getMenu());

				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					@Override
					public boolean onMenuItemClick(MenuItem menuItem)
					{
						int id = menuItem.getItemId();

						if (id == R.id.menuItemReqRES)
						{
							CheckPermission(READ_EXTERNAL_STORAGE, REQUEST_CODE_READ_EXTERNAL_STORAGE);
						}
						else if (id == R.id.menuItemReqWES)
						{
							CheckPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
						}
						else if (id == R.id.menuItemReqMES)
						{
							CheckPermission(MANAGE_EXTERNAL_STORAGE, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
						}

						return true;
					}
				});

				// Showing the popup menu
				popupMenu.show();
			});
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		this.noteList.clear();
		String directory = this.preferenceManager.getString(Constants.PREFERENCE_STORAGE_DIRECTORY);
		File dir = new File(directory);
		File[] files = dir.listFiles();

		if (files == null || files.length == 0) {
			//      TODO: Make a View of Create a Note :3 or something

			this.listAdapter.notifyDataSetChanged();
			this.binding.noteRecyclerView.scrollToPosition(0);
		}
		else {
			for (File file : files)
			{
				AttachableNote attachableNote = AttachableNote.ReadFromStorage(this, file.getName());

				if (attachableNote != null)
				{
					this.noteList.add(attachableNote);
				}
			}

			this.listAdapter.notifyDataSetChanged();
			this.binding.noteRecyclerView.scrollToPosition(0);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == this.REQUEST_CODE_READ_EXTERNAL_STORAGE)
		{
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				Toast.makeText(this, "READ_EXTERNAL_STORAGE Permission Granted", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "READ_EXTERNAL_STORAGE Permission Denied", Toast.LENGTH_SHORT).show();
			}
		}
		else if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
		{
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				Toast.makeText(this, "WRITE_EXTERNAL_STORAGE Permission Granted", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "WRITE_EXTERNAL_STORAGE Permission Denied", Toast.LENGTH_SHORT).show();
			}
		}
		else if (requestCode == REQUEST_CODE_MANAGE_EXTERNAL_STORAGE)
		{
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				Toast.makeText(this, "MANAGE_EXTERNAL_STORAGE Permission Granted", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "MANAGE_EXTERNAL_STORAGE Permission Denied", Toast.LENGTH_SHORT).show();
			}
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
			this.noteList.clear();
			String directory = this.preferenceManager.getString(Constants.PREFERENCE_STORAGE_DIRECTORY);

			File dir = new File(directory);
			File[] files = dir.listFiles();

			if (files.length == 0) {
				//      TODO: Make a View of Create a Note :3 or something

			}
			else {
				for (File file : files)
				{
					AttachableNote attachableNote = AttachableNote.ReadFromStorage(this, file.getName());

					if (attachableNote != null)
					{
						this.noteList.add(attachableNote);
					}
				}

				this.listAdapter.notifyItemRangeInserted(0, this.noteList.size());
				this.binding.noteRecyclerView.scrollToPosition(0);
			}
		}
	}

	private void CheckPermission(String permission, int requestCode)
	{
		if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
			{
				if (!Environment.isExternalStorageManager()){
					Intent getPermission = new Intent();

					getPermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);

					startActivity(getPermission);
				}
			}
			else
			{
				// Requesting the permission
				ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
			}
		}
		else
		{
			Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
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
		else {
			Log.d("MainActivityTemp", "onNoteClick: Unknown Note");

			Toast.makeText(this, "Note Type not Found", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNoteLongClick(int position) {
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
					if (MainActivity.this.noteList.get(i).isChecked())
					{
						String fileName = MainActivity.this.noteList.get(i).getFileName();

						if (_DefaultNote.DeleteFromStorage(MainActivity.this, fileName)) {
							MainActivity.this.noteList.remove(i);
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
				if (defaultNote instanceof AttachableNote) {
					AttachableNote attachableNote = new AttachableNote((AttachableNote) defaultNote);

					if (!attachableNote.WriteToStorage(this, true)) {
						Toast.makeText(this, "Failed to duplicate note", Toast.LENGTH_SHORT).show();

						continue;
					}

					this.noteList.add(i++ + 1, attachableNote);
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

					if (attachableNote.WriteToStorage(MainActivity.this, false))
					{
						boolean isFavorite = defaultNote.isFavorite();

						this.listAdapter.notifyItemChanged(i);
					}
					else
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

			this.SetListeners();
		}
		else {
			this.binding.newButton.setImageResource(R.drawable.icon_add);
			this.binding.settingsButton.setImageResource(R.drawable.icon_settings);

			this.SetListeners();
		}
	}

	public boolean isEditting() {
		return this.isEditting;
	}
}