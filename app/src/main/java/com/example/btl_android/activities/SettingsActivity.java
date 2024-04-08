package com.example.btl_android.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivitySettingsBinding;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.NoteComparator;
import com.example.btl_android.utilities.PreferenceManager;

public class SettingsActivity
		extends AppCompatActivity
{
	private ActivitySettingsBinding binding;
	private PreferenceManager preferenceManager;
	private final int REQUEST_GENERAL_SETTINGS_STORAGE_LOCATION = 0;
	private final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 0;
	private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
	private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;
	private final int REQUEST_CODE_POST_NOTIFICATIONS = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.binding = ActivitySettingsBinding.inflate(getLayoutInflater());
		this.setContentView(this.binding.getRoot());

		this.preferenceManager = new PreferenceManager(this);

		this.LoadToActivity();
		this.SetListeners();
	}

	private void LoadToActivity()
	{
		//      Initiate
		TypedValue typedValueTertiary = new TypedValue(), typedValueSecondary = new TypedValue()
				, typedValuePrimary = new TypedValue();
		Resources.Theme theme = this.getTheme();
		theme.resolveAttribute(
				com.google.android.material.R.attr.colorOnPrimary,
				typedValuePrimary,
				true
		);
		theme.resolveAttribute(
				com.google.android.material.R.attr.colorOnSecondary,
				typedValueSecondary,
				true
		);
		theme.resolveAttribute(
				com.google.android.material.R.attr.colorOnTertiary,
				typedValueTertiary,
				true
		);

		//      General Settings
		//          Default View Type
		this.binding.generalSettingsIsEditingCurrent.setText(
				"Current default view type: " +
				(this.preferenceManager.getBoolean(Constants.SETTINGS_NOTE_DEFAULT_IS_EDITING) ? "Editing" : "Reading")
		);

		//          Storage Location
		String storageLocation =
				this.preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);

		this.binding.generalSettingsStorageLocationCurrent.setText(
				storageLocation == null ? "None" : storageLocation
		);

		//          Theme Spinners
		ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(
				this,
				R.array.theme,
				R.layout.spinner_item_left
		);
		arrayAdapter1.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
		this.binding.generalSettingsDarkModeSpinner.setAdapter(arrayAdapter1);

		String appTheme = this.preferenceManager.getString(Constants.SETTINGS_APP_THEME);

		if (appTheme == null)
		{
			this.preferenceManager.putString(Constants.SETTINGS_APP_THEME, "0");

			this.binding.generalSettingsDarkModeSpinner.setSelection(0);
		}
		else
		{
			this.binding.generalSettingsDarkModeSpinner.setSelection(Integer.parseInt(appTheme));
		}

		//      TaskNote!
		//          Delete On Completion
		boolean deleteOnCompletion = this.preferenceManager.getBoolean(Constants.TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION);

		if (deleteOnCompletion)
		{
			this.binding.taskNoteSettingsDeleteOnCompletionCheckBox.setChecked(true);

			//      Delete on Completion After
			this.binding.taskNoteSettingsDeleteOnCompletionAfterTitle.setEnabled(true);
			this.binding.taskNoteSettingsDeleteOnCompletionAfterDesc.setEnabled(true);
			this.binding.taskNoteSettingsDeleteOnCompletionAfterSpinner.setEnabled(true);

			this.binding.taskNoteSettingsDeleteOnCompletionAfterTitle.setTextColor(typedValuePrimary.data);
			this.binding.taskNoteSettingsDeleteOnCompletionAfterDesc.setTextColor(typedValueSecondary.data);

			//      Sort to Bottom
			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterTitle.setEnabled(false);
			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterDesc.setEnabled(false);
			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterCheckBox.setEnabled(false);

			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterTitle.setTextColor(typedValueSecondary.data);
			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterDesc.setTextColor(typedValueTertiary.data);
		}
		else
		{
			this.binding.taskNoteSettingsDeleteOnCompletionCheckBox.setChecked(false);

			//      Delete on Completion After
			this.binding.taskNoteSettingsDeleteOnCompletionAfterTitle.setEnabled(false);
			this.binding.taskNoteSettingsDeleteOnCompletionAfterDesc.setEnabled(false);
			this.binding.taskNoteSettingsDeleteOnCompletionAfterSpinner.setEnabled(false);

			this.binding.taskNoteSettingsDeleteOnCompletionAfterTitle.setTextColor(typedValueSecondary.data);
			this.binding.taskNoteSettingsDeleteOnCompletionAfterDesc.setTextColor(typedValueTertiary.data);

			//      Sort to Bottom
			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterTitle.setEnabled(true);
			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterDesc.setEnabled(true);
			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterCheckBox.setEnabled(true);

			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterTitle.setTextColor(typedValuePrimary.data);
			this.binding.taskNoteSettingsSortToBottomOnCompletionAfterDesc.setTextColor(typedValueSecondary.data);
		}

		//          Delete After Spinner
		ArrayAdapter<CharSequence> arrayAdapter2 = ArrayAdapter.createFromResource(
				this,
				R.array.deleteAfter,
				R.layout.spinner_item_left
		);
		arrayAdapter2.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
		this.binding.taskNoteSettingsDeleteOnCompletionAfterSpinner.setAdapter(arrayAdapter2);

		String deleteAfterSeconds = this.preferenceManager.getString(Constants.TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION_AFTER);

		if (deleteAfterSeconds == null)
		{
			this.preferenceManager.putString(Constants.TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION_AFTER, "2");

			this.binding.taskNoteSettingsDeleteOnCompletionAfterSpinner.setSelection(2);
		}
		else
		{
			this.binding.taskNoteSettingsDeleteOnCompletionAfterSpinner.setSelection(Integer.parseInt(deleteAfterSeconds));
		}

		//          Sort to Bottom
		this.binding.taskNoteSettingsSortToBottomOnCompletionAfterCheckBox.setChecked(
				this.preferenceManager.getBoolean(Constants.TASK_NOTE_SETTINGS_SORT_TO_BOTTOM_ON_COMPLETION)
		);
	}

	private void SetListeners()
	{
		//      Initiate
		TypedValue typedValueTertiary = new TypedValue(), typedValueSecondary = new TypedValue()
				, typedValuePrimary = new TypedValue();
		Resources.Theme theme = this.getTheme();
		theme.resolveAttribute(
				com.google.android.material.R.attr.colorOnPrimary,
				typedValuePrimary,
				true
		);
		theme.resolveAttribute(
				com.google.android.material.R.attr.colorOnSecondary,
				typedValueSecondary,
				true
		);
		theme.resolveAttribute(
				com.google.android.material.R.attr.colorOnTertiary,
				typedValueTertiary,
				true
		);

		//      General
		this.binding.backButton.setOnClickListener(view -> {
			this.finish();
		});

		//      General Settings!
		//          Is Editing
		this.binding.generalSettingsIsEditing.setOnClickListener(view -> {
			boolean isEditing = this.preferenceManager.getBoolean(Constants.SETTINGS_NOTE_DEFAULT_IS_EDITING);

			isEditing = !isEditing;

			this.preferenceManager.putBoolean(Constants.SETTINGS_NOTE_DEFAULT_IS_EDITING, isEditing);

			this.binding.generalSettingsIsEditingCurrent.setText(
					"Current default view type: " +
							(isEditing ? "Editing" : "Reading")
			);
		});

		//          Storage Location
		this.binding.generalSettingsStorageLocation.setOnClickListener(view -> {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

			startActivityForResult(intent, REQUEST_GENERAL_SETTINGS_STORAGE_LOCATION);
		});

		//          Request Permission
		this.binding.generalSettingsRequestPermission.setOnClickListener(view ->
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
					{
						if (this.CheckPermission(null, 0))
						{
							Intent getPermission = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);

							startActivity(getPermission);
						}
					}
					else
					{
						if (this.CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
								REQUEST_CODE_WRITE_EXTERNAL_STORAGE
						))
						{
							ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
									REQUEST_CODE_WRITE_EXTERNAL_STORAGE
							);
						}

						if (this.CheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
								REQUEST_CODE_READ_EXTERNAL_STORAGE
						))
						{
							ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
									REQUEST_CODE_READ_EXTERNAL_STORAGE
							);
						}

						if (this.CheckPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE,
								REQUEST_CODE_MANAGE_EXTERNAL_STORAGE
						))
						{
							ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
									REQUEST_CODE_MANAGE_EXTERNAL_STORAGE
							);
						}
					}

					if (this.CheckPermission(Manifest.permission.POST_NOTIFICATIONS,
							REQUEST_CODE_POST_NOTIFICATIONS
					))
					{
						ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
								REQUEST_CODE_POST_NOTIFICATIONS
						);
					}
				});

		//          Dark Mode
		this.binding.generalSettingsDarkModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				SettingsActivity.this.preferenceManager.putString(Constants.SETTINGS_APP_THEME, String.valueOf(position));

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

			@Override public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		//      Private Note
		//          Reset Password
		this.binding.privateNoteSettingsResetPassword.setOnClickListener(view -> {
			//      TODO: Điền đây!
		});

		//      Task Note!
		//          Delete On Completion
		this.binding.taskNoteSettingsDeleteOnCompletionCheckBox.setOnClickListener(view -> {
			boolean deleteOnCompletion = this.binding.taskNoteSettingsDeleteOnCompletionCheckBox.isChecked();

			this.preferenceManager.putBoolean(Constants.TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION, deleteOnCompletion);

			if (deleteOnCompletion)
			{
				this.binding.taskNoteSettingsDeleteOnCompletionCheckBox.setChecked(true);

				//      Delete on Completion After
				this.binding.taskNoteSettingsDeleteOnCompletionAfterTitle.setEnabled(true);
				this.binding.taskNoteSettingsDeleteOnCompletionAfterDesc.setEnabled(true);
				this.binding.taskNoteSettingsDeleteOnCompletionAfterSpinner.setEnabled(true);

				this.binding.taskNoteSettingsDeleteOnCompletionAfterTitle.setTextColor(typedValuePrimary.data);
				this.binding.taskNoteSettingsDeleteOnCompletionAfterDesc.setTextColor(typedValueSecondary.data);

				//      Sort to Bottom
				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterTitle.setEnabled(false);
				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterDesc.setEnabled(false);
				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterCheckBox.setEnabled(false);

				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterTitle.setTextColor(typedValueSecondary.data);
				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterDesc.setTextColor(typedValueTertiary.data);
			}
			else
			{
				this.binding.taskNoteSettingsDeleteOnCompletionCheckBox.setChecked(false);

				//      Delete on Completion After
				this.binding.taskNoteSettingsDeleteOnCompletionAfterTitle.setEnabled(false);
				this.binding.taskNoteSettingsDeleteOnCompletionAfterDesc.setEnabled(false);
				this.binding.taskNoteSettingsDeleteOnCompletionAfterSpinner.setEnabled(false);

				this.binding.taskNoteSettingsDeleteOnCompletionAfterTitle.setTextColor(typedValueSecondary.data);
				this.binding.taskNoteSettingsDeleteOnCompletionAfterDesc.setTextColor(typedValueTertiary.data);

				//      Sort to Bottom
				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterTitle.setEnabled(true);
				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterDesc.setEnabled(true);
				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterCheckBox.setEnabled(true);

				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterTitle.setTextColor(typedValuePrimary.data);
				this.binding.taskNoteSettingsSortToBottomOnCompletionAfterDesc.setTextColor(typedValueSecondary.data);
			}
		});

		//          Delete on Completion After
		this.binding.taskNoteSettingsDeleteOnCompletionAfterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				SettingsActivity.this.preferenceManager.putString(Constants.TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION_AFTER, String.valueOf(position));
			}

			@Override public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		//          Sort to Bottom on Completion
		this.binding.taskNoteSettingsSortToBottomOnCompletionAfterCheckBox.setOnClickListener(view ->
		{
			boolean isChecked =
					this.binding.taskNoteSettingsSortToBottomOnCompletionAfterCheckBox.isChecked();

			this.preferenceManager.putBoolean(
					Constants.TASK_NOTE_SETTINGS_SORT_TO_BOTTOM_ON_COMPLETION,
					isChecked
			);
			NoteComparator.taskNoteSortToBottomOnCompletion = isChecked;
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_GENERAL_SETTINGS_STORAGE_LOCATION && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();

				if (uri != null) {
					String directoryPath = Uri.decode(uri.toString());

					String finalizedUri = Environment.getExternalStorageDirectory() + "/" + directoryPath.substring(directoryPath.lastIndexOf(":") + 1);

					this.preferenceManager.putString(Constants.SETTINGS_STORAGE_LOCATION, finalizedUri);
					this.binding.generalSettingsStorageLocationCurrent.setText(finalizedUri);
				}
			}
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
		else if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS)
		{
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				Toast.makeText(this, "POST_NOTIFICATIONS Permission Granted", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "POST_NOTIFICATIONS Permission Denied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean CheckPermission(String permission, int requestCode)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
		{
			return Environment.isExternalStorageManager();
		}
		else
		{
			return ContextCompat.checkSelfPermission(this, permission) !=
					PackageManager.PERMISSION_DENIED;
		}
	}
}