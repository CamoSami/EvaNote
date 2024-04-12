package com.example.btl_android.activities;

import static android.provider.DocumentsContract.Document.MIME_TYPE_DIR;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.btl_android.R;
import com.example.btl_android.adapters.AttachableNote_ContainerAdapter;
import com.example.btl_android.databinding.ActivityAttachableNoteBinding;
import com.example.btl_android.listeners.AttachableNoteListener;
import com.example.btl_android.models.AttachableNote;
import com.example.btl_android.models.AttachableNote_Container;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.NewlineInputFilter;
import com.example.btl_android.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttachableNoteActivity extends AppCompatActivity implements AttachableNoteListener
{
	private ActivityAttachableNoteBinding binding;
	private boolean isEditing = true;
	private boolean isFavorite = false;
	private String fileName = null;
	private Date dateCreated = null;
	private List<AttachableNote_Container> containersList;
	private AttachableNote_ContainerAdapter fileNoteFileAdapter;
	private int currentContainerPosition = -1;
	public static final int REQUEST_CODE_ADD_FILE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.binding = ActivityAttachableNoteBinding.inflate(getLayoutInflater());
		this.setContentView(this.binding.getRoot());

		//      Filters
		this.binding.titleEditText.setFilters(new InputFilter[]{new NewlineInputFilter()});

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		//      Listeners
		this.SetListeners();
		this.ReadToActivity(bundle);
		this.SetEditting(this.isEditing);
	}

	@Override
	public void onBackPressed()
	{
		View view = new View(this);

		if (this.SaveNote(view)) {
			super.onBackPressed();
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Leave Confirmation");
			builder.setMessage("Note can not be saved, do you still want to leave?");

			builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			});

			builder.setNegativeButton("Nah", null);

			builder.create().show();

			return;
		}
	}

	private void ReadToActivity(Bundle bundle) {
		if (bundle == null)
		{
			//      If New
			this.containersList = new ArrayList<>();
			this.fileNoteFileAdapter = new AttachableNote_ContainerAdapter(this.containersList, this);
			this.binding.fileRecyclerView.setAdapter(this.fileNoteFileAdapter);
		}
		else
		{
			//      If Edit
			PreferenceManager preferenceManager = new PreferenceManager(this);
			this.isEditing = preferenceManager.getBoolean(Constants.SETTINGS_NOTE_DEFAULT_IS_EDITING);

			String fileName = bundle.getString(Constants.BUNDLE_FILENAME_KEY);

			AttachableNote attachableNote = AttachableNote.ReadFromStorage(this, fileName);

			this.containersList = attachableNote.getContainers();
			if (this.containersList == null) {
				this.containersList = new ArrayList<>();
			}

			this.fileNoteFileAdapter = new AttachableNote_ContainerAdapter
					(
							this.containersList,
							this
					);
			this.binding.fileRecyclerView.setAdapter(this.fileNoteFileAdapter);
			this.fileNoteFileAdapter.notifyItemRangeInserted(0, this.containersList.size());

			this.fileName = fileName;
			this.binding.titleEditText.setText(attachableNote.getTitle());
			this.binding.contentEditText.setText(attachableNote.getContent());
			this.isFavorite = attachableNote.isFavorite();
			this.dateCreated = attachableNote.getDateCreated();
		}
	}

	private void SetListeners()
	{
		//      Back Button
		this.binding.backButton.setOnClickListener(view ->
		{
			if (this.SaveNote(view))
			{
				setResult(Activity.RESULT_OK);

				finish();
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setTitle("Leave Confirmation");
				builder.setMessage("Note can not be saved, do you still want to leave?");

				builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});

				builder.setNegativeButton("Nah", null);

				builder.create().show();

				return;
			}
		});

		//      Menu Button!
		this.binding.settingsButton.setOnClickListener(view ->
		{
			PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.settingsButton);

			popupMenu.getMenuInflater().inflate(R.menu.menu_attachable_note, popupMenu.getMenu());

			if (this.isFavorite)
			{
				popupMenu.getMenu().findItem(R.id.menuItemAddToFavorite).setTitle("Remove from Favorites");
			}
			else
			{
				popupMenu.getMenu().findItem(R.id.menuItemAddToFavorite).setTitle("Add to Favorites");
			}

			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
			{
				@Override
				public boolean onMenuItemClick(MenuItem menuItem)
				{
					int id = menuItem.getItemId();

					if (id == R.id.menuItemAddToFavorite)
					{
						AttachableNoteActivity.this.isFavorite = !AttachableNoteActivity.this.isFavorite;
					}
					else if (id == R.id.menuItemDelete)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(AttachableNoteActivity.this);

						builder.setTitle("Delete Confirmation");
						builder.setMessage("Are you sure you want to delete the Note?");

						builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (fileName == null) {
									finish();

									return;
								}

								if (_DefaultNote.DeleteFromStorage(AttachableNoteActivity.this, fileName)) {
									AttachableNoteActivity.this.finish();
								}
								else {
									Log.d("AttachableNoteActivityTemp", "FileName = " + fileName);

									Toast.makeText(AttachableNoteActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
								}
							}
						});

						builder.setNegativeButton("Nah", null);

						builder.create().show();
					}

					return true;
				}
			});

			// Showing the popup menu
			popupMenu.show();
		});

		//      The Pencil / Book Button
		this.binding.editButton.setOnClickListener(view ->
		{
			this.SetEditting(!this.isEditing);
		});

		//      The Add File Button
		this.binding.addFileButton.setOnClickListener(view ->
		{
			view.requestFocus();

			this.OnAddOrEditContainer(-1);
		});

		//      The Add Link Button
		this.binding.addLinkButton.setOnClickListener(view ->
		{
			view.requestFocus();

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			View viewInflated = LayoutInflater.from(this).inflate(R.layout.item_container_attachable_note_alert_link_container, null);

			builder.setTitle("Enter your link:");

			builder.setView(viewInflated);

			final EditText Url = (EditText) viewInflated.findViewById(R.id.urlEditText);
			final EditText NameOfTheUrl = (EditText) viewInflated.findViewById(R.id.nameEditText);

			builder.setPositiveButton("Done~", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					//                    Log.d("FileNoteActivityTemp", "Url: " + Url.getText().toString());
					//                    Log.d("FileNoteActivityTemp", "Name: " + NameOfTheUrl.getText().toString());

					containersList.add(
							new AttachableNote_Container(
									Url.getText().toString(),
									NameOfTheUrl.getText().toString(),
									"",
									AttachableNote_ContainerAdapter.LINK_CONTAINER
							)
					);
					fileNoteFileAdapter.notifyItemInserted(containersList.size() - 1);
				}
			});

			builder.setNegativeButton("Nah", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					// Canceled.
				}
			});

			builder.show();
		});
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			View view = getCurrentFocus();
//			if ( view instanceof EditText) {
//				Rect outRect = new Rect();
//				view.getGlobalVisibleRect(outRect);
//
//				if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
////					Log.d("DispatchTouchEventTemp", "touchevent");
//
//					view.clearFocus();
//
//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//				}
//			}
//		}
//
//		return super.dispatchTouchEvent(event);
//	}

	@Override
	public void OnAddOrEditContainer(int position)
	{
		this.currentContainerPosition = position;

		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*"); // Specify the MIME type of the files you want to allow, e.g., "image/*" for images

		startActivityForResult(intent, REQUEST_CODE_ADD_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK || data == null)
		{
			return;
		}

		Uri uri = data.getData();

		if (uri == null)
		{
			return;
		}

		// This will give you the file path
		String directoryPath = Uri.decode(uri.toString());

		String fileName = directoryPath.substring(directoryPath.lastIndexOf("/") + 1);
		String finalizedUri = Environment.getExternalStorageDirectory() + "/" + directoryPath.substring(directoryPath.lastIndexOf(":") + 1);

		if (this.currentContainerPosition != -1)
		{
			this.OnContainerEdit(
					this.currentContainerPosition,
					new AttachableNote_Container(
							finalizedUri,
							this.containersList.get(this.currentContainerPosition).getContainerName(),
							this.containersList.get(this.currentContainerPosition).getContainerContent(),
							AttachableNote_ContainerAdapter.FILE_CONTAINER
					)
			);
		}
		else
		{
			this.OnContainerAdd(
					new AttachableNote_Container(
							finalizedUri,
							fileName,
							"",
							AttachableNote_ContainerAdapter.FILE_CONTAINER
					)
			);
		}
	}

	@Override
	public void OnContainerDelete(int position)
	{
		this.containersList.remove(position);
		this.fileNoteFileAdapter.notifyItemRemoved(position);

		if (this.containersList.size() == 0)
		{
//			this.binding.noteView.setVisibility(View.GONE);
		}
	}

	@Override
	public void OnContainerEdit(int position, AttachableNote_Container container)
	{
		this.containersList.set(position, container);
		this.fileNoteFileAdapter.notifyItemChanged(position);
	}

	@Override
	public void OnContainerAdd(AttachableNote_Container container)
	{
		this.containersList.add(container);
		this.fileNoteFileAdapter.notifyItemInserted(this.containersList.size() - 1);

		if (this.containersList.size() >= 1)
		{
//			this.binding.noteView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void OnLinkClick(AttachableNote_Container container)
	{
		//        Log.d("FileNoteActivityTemp", "Url: " + linkUrl);
		Uri linkUrl = Uri.parse(container.getContainerLink());

		if (!linkUrl.toString().startsWith("http://") && !linkUrl.toString().startsWith("https://"))
		{
			linkUrl = Uri.parse("https://" + linkUrl);
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setData(linkUrl);

		startActivity(intent);
	}

	@Override
	public void OnFileClick(AttachableNote_Container container)
	{
		//        Log.d("FileNoteActivityTemp", "File Directory Path: " + fileUri);

		Uri fileUri = Uri.parse(container.getContainerLink());

		String ext = fileUri.toString().substring(fileUri.toString().lastIndexOf(".") + 1);
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeType = mimeTypeMap.getMimeTypeFromExtension(ext);

		//        Log.d("FileNoteActivityTemp", "MimeType: " + mimeType + ", ext: " + ext);

		// If the MIME type is null, or if it's a directory, open the folder directly
		if (mimeType == null || mimeType.equals(MIME_TYPE_DIR))
		{
			Toast.makeText(this, "Can not open this File...", Toast.LENGTH_SHORT).show();
		}
		else
		{
			// If it's a file, open it with the "Open With" option
			this.openWith(fileUri, mimeType);
		}
	}

	private boolean SaveNote(View view) {
		view.requestFocus();

		if (this.dateCreated == null)
		{
			this.dateCreated = Calendar.getInstance().getTime();
		}

		AttachableNote attachableNote = new AttachableNote(
				this.fileName,
				this.binding.titleEditText.getText().toString(),
				this.dateCreated,
				Calendar.getInstance().getTime(),
				this.binding.contentEditText.getText().toString(),
				this.isFavorite,
				this.containersList
		);

		//      Not validated, just simply want to leave
		if (!attachableNote.Validate()) {
//			this.finish();

			return false;
		}

		return attachableNote.WriteToStorage(this, false);
	}

	private void openWith(Uri fileUri, String mimeType)
	{
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());

		this.getIntent().setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + fileUri.toString()), mimeType);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (intent.resolveActivity(getPackageManager()) != null)
		{
			startActivity(intent);
		}
		else
		{
			// Handle case where no suitable app is installed
			Toast.makeText(this, "No app found to open the file", Toast.LENGTH_SHORT).show();
		}
	}

	public void SetEditting(boolean isEditting)
	{
		if (!isEditting)
		{
			this.binding.editButton.setImageResource(R.drawable.icon_read);

			this.binding.addFileButton.setVisibility(View.GONE);
			this.binding.addLinkButton.setVisibility(View.GONE);

			this.binding.addFileView.setVisibility(View.GONE);
			this.binding.addLinkView.setVisibility(View.GONE);

			if (this.containersList.size() == 0)
			{
//				this.binding.noteView.setVisibility(View.GONE);
			}

			this.binding.contentEditText.setFocusable(false);
			this.binding.contentEditText.setFocusableInTouchMode(false);
			this.binding.contentEditText.setHint("");

			this.binding.titleEditText.setFocusable(false);
			this.binding.titleEditText.setFocusableInTouchMode(false);
			this.binding.titleEditText.setHint("");

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

			imm.hideSoftInputFromWindow(this.binding.contentEditText.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(this.binding.titleEditText.getWindowToken(), 0);
		}
		else
		{
			this.binding.editButton.setImageResource(R.drawable.icon_edit);

			this.binding.addFileButton.setVisibility(View.VISIBLE);
			this.binding.addLinkButton.setVisibility(View.VISIBLE);

			this.binding.addFileView.setVisibility(View.VISIBLE);
			this.binding.addLinkView.setVisibility(View.VISIBLE);

			if (this.containersList.size() != 0)
			{
//				this.binding.noteView.setVisibility(View.VISIBLE);
			}

			this.binding.contentEditText.setFocusable(true);
			this.binding.contentEditText.setFocusableInTouchMode(true);
			this.binding.contentEditText.setHint(R.string.this_is_the_note_s_text_content);

			this.binding.titleEditText.setFocusable(true);
			this.binding.titleEditText.setFocusableInTouchMode(true);
			this.binding.titleEditText.setHint(R.string.title_of_the_note);
		}

		this.isEditing = isEditting;
		this.fileNoteFileAdapter.SetEditing(isEditting);
	}
}