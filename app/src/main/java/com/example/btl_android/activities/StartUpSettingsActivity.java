package com.example.btl_android.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityStartUpSettingsBinding;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;

public class StartUpSettingsActivity
		extends AppCompatActivity
{

	private ActivityStartUpSettingsBinding binding;
	private PreferenceManager preferenceManager;
	private final int REQUEST_GENERAL_SETTINGS_STORAGE_LOCATION = 0;
	private final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 0;
	private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
	private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;
	private final int REQUEST_CODE_POST_NOTIFICATIONS = 3;
	private final int REQUEST_CODE_RECEIVE_BOOT_COMPLETED = 4;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.binding = ActivityStartUpSettingsBinding.inflate(getLayoutInflater());
		setContentView(this.binding.getRoot());

		this.preferenceManager = new PreferenceManager(this);

		this.LoadToActivity();
		this.SetListeners();
	}

	private void LoadToActivity() {
		//          Storage Location
		String storageLocation =
				this.preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);

		this.binding.generalSettingsStorageLocationCurrent.setText(
				storageLocation == null ? "None" : storageLocation
		);
	}

	private void SetListeners() {
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

		//          Done Button
		this.binding.doneButton.setOnClickListener(view -> {
			boolean permissionGranted = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ?
					this.CheckPermission(null) &&
							this.CheckPermission(Manifest.permission.POST_NOTIFICATIONS) :
					this.CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
							this.CheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE) &&
							this.CheckPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) &&
					this.CheckPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED);

			boolean storageLocation = this.preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION) != null;

			if (!permissionGranted || !storageLocation)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(StartUpSettingsActivity.this);

				builder.setTitle("Leave Confirmation");
				builder.setMessage("App won't be able to run normally, do you still want to leave?");

				builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StartUpSettingsActivity.this.finish();

						return;
					}
				});

				builder.setNegativeButton("Nah", new DialogInterface.OnClickListener()
				{
					@Override public void onClick(DialogInterface dialog, int which)
					{

					}
				});

				builder.create().show();
			}
			else
			{
				this.finish();
			}
		});

		//          Storage Location
		this.binding.generalSettingsStorageLocation.setOnClickListener(view -> {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

			startActivityForResult(intent, REQUEST_GENERAL_SETTINGS_STORAGE_LOCATION);
		});

		//          Request Permission
		this.binding.generalSettingsRequestPermission.setOnClickListener(view -> {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
			{
				boolean allPermissionsGranted = true;

				if (!this.CheckPermission(null))
				{
					allPermissionsGranted = false;

					Intent getPermission = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);

					startActivity(getPermission);
				}

				if (!this.CheckPermission(Manifest.permission.POST_NOTIFICATIONS))
				{
					allPermissionsGranted = false;

					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
							REQUEST_CODE_POST_NOTIFICATIONS
					);
				}

				if (!this.CheckPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
				{
					allPermissionsGranted = false;

					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
				}

				if (!this.CheckPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED))
				{
					allPermissionsGranted = false;

					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
							REQUEST_CODE_RECEIVE_BOOT_COMPLETED
					);
				}

				if (allPermissionsGranted)
				{
					Toast.makeText(this, "Permissions Already Granted", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				boolean allPermissionsGranted = true;

				if (!this.CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
				{
					allPermissionsGranted = false;

					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
				}

				if (!this.CheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
				{
					allPermissionsGranted = false;

					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
				}

				if (!this.CheckPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED))
				{
					allPermissionsGranted = false;

					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
							REQUEST_CODE_RECEIVE_BOOT_COMPLETED
					);
				}

				if (allPermissionsGranted)
				{
					Toast.makeText(this, "Permissions Already Granted", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		boolean permissionGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ?
				this.CheckPermission(null) :
				this.CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
				this.CheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE) &&
				this.CheckPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE);

		boolean storageLocation = this.preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION) != null;

		if (!permissionGranted || !storageLocation)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(StartUpSettingsActivity.this);

			builder.setTitle("Leave Confirmation");
			builder.setMessage("App won't be able to run normally, do you still want to leave?");

			builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					StartUpSettingsActivity.this.finish();

					return;
				}
			});

			builder.setNegativeButton("Nah", new DialogInterface.OnClickListener()
			{
				@Override public void onClick(DialogInterface dialog, int which)
				{

				}
			});

			builder.create().show();
		}
		else
		{
			super.onBackPressed();
		}
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
		else if (requestCode == REQUEST_CODE_RECEIVE_BOOT_COMPLETED)
		{
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				Toast.makeText(this, "RECEIVE_BOOT_COMPLETED Permission Granted", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "RECEIVE_BOOT_COMPLETED Permission Denied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean CheckPermission(String permission)
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