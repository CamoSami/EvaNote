package com.example.btl_android.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ItemContainerAttachableNoteContainerBinding;
import com.example.btl_android.listeners.AttachableNoteListener;
import com.example.btl_android.models.AttachableNote_Container;

import java.util.List;
import java.util.UUID;

public class AttachableNote_ContainerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

	private final List<AttachableNote_Container> containersList;
	private final AttachableNoteListener attachableNoteListener;
	public static final int FILE_CONTAINER = 0;
	public static final int LINK_CONTAINER = 1;

	public AttachableNote_ContainerAdapter(List<AttachableNote_Container> filesList, AttachableNoteListener attachableNoteListener)
	{
		this.containersList = filesList;
		this.attachableNoteListener = attachableNoteListener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		if (viewType == FILE_CONTAINER)
		{
			return new FileNote_FileViewHolder(ItemContainerAttachableNoteContainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
		}
		else
		{
			return new FileNote_LinkViewHolder(ItemContainerAttachableNoteContainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
	{
		if (getItemViewType(position) == FILE_CONTAINER)
		{
			FileNote_FileViewHolder fileNote_FileViewHolder = (FileNote_FileViewHolder) holder;

			fileNote_FileViewHolder.SetData(this.containersList.get(position));
		}
		else
		{
			FileNote_LinkViewHolder fileNote_LinkViewHolder = (FileNote_LinkViewHolder) holder;

			fileNote_LinkViewHolder.SetData(this.containersList.get(position));
		}
	}

	@Override
	public int getItemCount()
	{
		return this.containersList.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		return this.containersList.get(position).getContainerType();
	}

	public class FileNote_FileViewHolder extends RecyclerView.ViewHolder
	{

		private ItemContainerAttachableNoteContainerBinding binding;

		public FileNote_FileViewHolder(ItemContainerAttachableNoteContainerBinding itemContainerAttachableNoteContainerBinding)
		{
			super(itemContainerAttachableNoteContainerBinding.getRoot());

			binding = itemContainerAttachableNoteContainerBinding;
		}

		public void SetData(AttachableNote_Container container)
		{
			this.binding.containerName.setText(container.getContainerName());
			this.binding.containerContent.setText(container.getContainerContent());

			this.SetFileIcon(container);
			this.SetListeners(container);
		}

		public void SetFileIcon(AttachableNote_Container container)
		{
			if (container.getContainerLink().toString().contains(".txt"))
			{
				//      Is Text

				this.binding.containerIcon.setImageResource(R.drawable.icon_txt);
			}
			else if (container.getContainerLink().toString().contains(".pdf"))
			{
				//      Is PDF

				this.binding.containerIcon.setImageResource(R.drawable.icon_pdf);
			}
			else if (container.getContainerLink().toString().contains(".jpg") || container.getContainerLink().toString().contains(".png") || container.getContainerLink().toString().contains(".jpeg"))
			{
				//      Is Image

				this.binding.containerIcon.setImageResource(R.drawable.icon_image);
			}
			else if (container.getContainerLink().toString().contains(".mp3") || container.getContainerLink().toString().contains(".wav"))
			{
				//      Is Audio

				this.binding.containerIcon.setImageResource(R.drawable.icon_audio);
			}
			else if (container.getContainerLink().toString().contains("."))
			{
				//      Is File

				this.binding.containerIcon.setImageResource(R.drawable.icon_file);
			}
			else
			{
				//      Is Folder

				this.binding.containerIcon.setImageResource(R.drawable.icon_folder);
			}
		}

		public void SetListeners(AttachableNote_Container container)
		{

			this.binding.containerIcon.setOnClickListener(view ->
			{
				attachableNoteListener.OnFileClick(container);
			});

			this.binding.containerName.setOnClickListener(view ->
			{
				attachableNoteListener.OnFileClick(container);
			});

			this.binding.containerContent.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus)
				{
					if (!hasFocus) {
						Log.d("FileNote_LinkViewHolderTemp", "Content: " + binding.containerContent.getText().toString());

						container.setContainerContent(binding.containerContent.getText().toString());
					}
				}
			});

			this.binding.containerSettings.setOnClickListener(view ->
			{
				PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.containerSettings);

				popupMenu.getMenuInflater().inflate(R.menu.menu_attachable_note_file_container, popupMenu.getMenu());

				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					@Override
					public boolean onMenuItemClick(MenuItem menuItem)
					{
						int id = menuItem.getItemId();

						if (id == R.id.menuItemNewDirectory)
						{
							attachableNoteListener.OnAddOrEditContainer(containersList.indexOf(container));
						}
						else if (id == R.id.menuItemEdit)
						{
							final AttachableNote_Container newContainer = new AttachableNote_Container();
							AttachableNote_Container currentContainer = containersList.get(containersList.indexOf(container));

							AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

							View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.item_container_attachable_note_alert_file_container, null);

							builder.setTitle("Fix your Container (and your life):");
							builder.setView(viewInflated);

							final EditText Url = (EditText) viewInflated.findViewById(R.id.linkEditText);
							final EditText NameOfTheUrl = (EditText) viewInflated.findViewById(R.id.nameEditText);

							Url.setText(container.getContainerLink());
							NameOfTheUrl.setText(container.getContainerName());

							builder.setPositiveButton("Done~", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{

									newContainer.setContainerLink(Url.getText().toString());
									newContainer.setContainerName(NameOfTheUrl.getText().toString());
									newContainer.setContainerType(currentContainer.getContainerType());

									attachableNoteListener.OnContainerEdit(containersList.indexOf(container), newContainer);
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
						}
						else if (id == R.id.menuItemDelete)
						{
							attachableNoteListener.OnContainerDelete(containersList.indexOf(container));
						}

						return true;
					}
				});

				// Showing the popup menu
				popupMenu.show();
			});
		}
	}

	public class FileNote_LinkViewHolder extends RecyclerView.ViewHolder
	{

		private ItemContainerAttachableNoteContainerBinding binding;

		public FileNote_LinkViewHolder(ItemContainerAttachableNoteContainerBinding itemContainerAttachableNoteContainerBinding)
		{
			super(itemContainerAttachableNoteContainerBinding.getRoot());

			binding = itemContainerAttachableNoteContainerBinding;
		}

		public void SetData(AttachableNote_Container container)
		{
			this.binding.containerName.setText(container.getContainerName());
			this.binding.containerContent.setText(container.getContainerContent());

			this.SetLinkIcon(container);
			this.SetListeners(container);
		}

		public void SetLinkIcon(AttachableNote_Container container)
		{
			this.binding.containerIcon.setImageResource(R.drawable.icon_web);
		}

		public void SetListeners(AttachableNote_Container container)
		{
			this.binding.containerIcon.setOnClickListener(view ->
			{
				attachableNoteListener.OnLinkClick(container);
			});

			this.binding.containerName.setOnClickListener(view ->
			{
				attachableNoteListener.OnLinkClick(container);
			});

			this.binding.containerContent.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus)
				{
					if (!hasFocus) {
//						Log.d("FileNote_LinkViewHolderTemp", "Content: " + binding.containerContent.getText().toString());

						container.setContainerContent(binding.containerContent.getText().toString());
					}
				}
			});

			this.binding.containerSettings.setOnClickListener(view ->
			{
				PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.containerSettings);

				popupMenu.getMenuInflater().inflate(R.menu.menu_attachable_note_link_container, popupMenu.getMenu());


				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					@Override
					public boolean onMenuItemClick(MenuItem menuItem)
					{
						int id = menuItem.getItemId();

						if (id == R.id.menuItemEdit)
						{
							AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

							View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.item_container_attachable_note_alert_link_container, null);

							builder.setTitle("Enter your link:");

							builder.setView(viewInflated);

							final EditText Url = (EditText) viewInflated.findViewById(R.id.urlEditText);
							final EditText NameOfTheUrl = (EditText) viewInflated.findViewById(R.id.nameEditText);

							Url.setText(container.getContainerLink());
							NameOfTheUrl.setText(container.getContainerName());

							builder.setPositiveButton("Done~", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{
									//                    Log.d("FileNoteActivityTemp", "Url: " + Url.getText().toString());
									//                    Log.d("FileNoteActivityTemp", "Name: " + NameOfTheUrl.getText().toString());

									attachableNoteListener.OnContainerEdit(
											containersList.indexOf(container),
											new AttachableNote_Container(
													Url.getText().toString(),
													NameOfTheUrl.getText().toString(),
													container.getContainerContent(),
													AttachableNote_ContainerAdapter.LINK_CONTAINER
											)
									);
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
						}
						else if (id == R.id.menuItemDelete)
						{
							attachableNoteListener.OnContainerDelete(containersList.indexOf(container));
						}

						return true;
					}
				});

				// Showing the popup menu
				popupMenu.show();
			});
		}
	}
}
