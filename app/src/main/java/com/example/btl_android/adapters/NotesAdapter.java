package com.example.btl_android.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ItemContainerSmallAttachableNoteBinding;
import com.example.btl_android.databinding.ItemContainerSmallDefaultNoteBinding;
import com.example.btl_android.interfaces.NoteViewHolderInterface;
import com.example.btl_android.listeners.NoteListener;
import com.example.btl_android.models.AttachableNote;
import com.example.btl_android.models.AttachableNote_Container;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	ArrayList<RecyclerView.ViewHolder> views = new ArrayList<>();
	private final List<_DefaultNote> notesList;
	private final NoteListener noteListener;
	private boolean isEditing = false;

	public NotesAdapter(List<_DefaultNote> notesList, NoteListener noteListener)
	{
		this.notesList = notesList;
		this.noteListener = noteListener;
	}

	public void SetEditing(boolean isEditing) {
		this.isEditing = isEditing;
		this.notifyDataSetChanged();
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		//      TODO: Các bạn bổ sung if else ViewType tương ứng của các bạn
		if (viewType == Constants.ATTACHABLE_NOTE)
		{
			return new AttachableNoteViewHolder(
					ItemContainerSmallAttachableNoteBinding.inflate(
							LayoutInflater.from(parent.getContext()
							),
							parent,
							false
					)
			);
		}
		else
		{
			Log.d("ERROREEEEEEEEEE", "onCreateViewHolder: Unknown Note");

			Toast.makeText(parent.getContext(), "NoteType not recognized", Toast.LENGTH_SHORT).show();

			return null;
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
	{
		//      TODO: Các bạn bổ sung if else View tương ứng của các bạn
		if (getItemViewType(position) == Constants.ATTACHABLE_NOTE)
		{
			AttachableNoteViewHolder attachableNoteViewHolder = (AttachableNoteViewHolder) holder;

			attachableNoteViewHolder.LoadSummarizedNote(this.notesList.get(position));
			attachableNoteViewHolder.SetListeners(this.notesList.get(position));
		}
	}

	@Override
	public int getItemCount()
	{
		return this.notesList.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		//      TODO: Các bạn bổ sung if else getItemViewType tương ứng của các bạn
		if (this.notesList.get(position).getClass() == AttachableNote.class)
		{
			return Constants.ATTACHABLE_NOTE;
		}
 		else
		{
			Log.d("ERROREEEEEEEEEE", "getItemViewType: Unknown Note");

			return -1;
		}
	}

	//      TODO: Các bạn bổ sung ViewHolder của các bạn:
	//          + extends RecyclerView.ViewHolder
	//          + implement NoteViewHolderInterface
	//          + Tham khảo AttachableNoteViewHolder

	public class AttachableNoteViewHolder extends RecyclerView.ViewHolder implements NoteViewHolderInterface
	{
		private final ItemContainerSmallAttachableNoteBinding binding;

		public AttachableNoteViewHolder(ItemContainerSmallAttachableNoteBinding itemContainerSmallAttachableNoteBinding)
		{
			super(itemContainerSmallAttachableNoteBinding.getRoot());

			binding = itemContainerSmallAttachableNoteBinding;
		}

		public void SetListeners(_DefaultNote defaultNote) {
			this.binding.noteCheckbox.setOnClickListener(view ->
			{
				defaultNote.setChecked(defaultNote.isChecked());
			});

			if (NotesAdapter.this.isEditing) {
				//      Meh
				this.binding.getRoot().setOnClickListener(view ->
				{
					this.binding.noteCheckbox.setChecked(!this.binding.noteCheckbox.isChecked());
					defaultNote.setChecked(!defaultNote.isChecked());
				});

				//      Context Menu
				this.binding.getRoot().setOnLongClickListener(view ->
				{
					this.binding.noteCheckbox.setChecked(!this.binding.noteCheckbox.isChecked());
					defaultNote.setChecked(!defaultNote.isChecked());

					return false;
				});
			}
			else
			{
				//      Meh
				this.binding.getRoot().setOnClickListener(view ->
				{
					noteListener.onNoteClick(NotesAdapter.this.notesList.indexOf(defaultNote));
				});

				//      Context Menu
				this.binding.getRoot().setOnLongClickListener(view ->
				{
					this.binding.noteCheckbox.setChecked(true);
					defaultNote.setChecked(true);

					noteListener.onNoteLongClick(NotesAdapter.this.notesList.indexOf(defaultNote));

					return false;
				});
			}
		}

		@Override
		public void LoadSummarizedNote(_DefaultNote defaultNote)
		{
			if (NotesAdapter.this.isEditing)
			{
				ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
						0,
						ConstraintLayout.LayoutParams.WRAP_CONTENT
				);
				layoutParams.startToEnd = R.id.noteCheckbox;
				layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
				layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
				layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

				this.binding.layoutAddNew.setLayoutParams(layoutParams);
			}
			else
			{
				this.binding.noteCheckbox.setChecked(false);
				defaultNote.setChecked(false);

				ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
						ConstraintLayout.LayoutParams.MATCH_PARENT,
						ConstraintLayout.LayoutParams.WRAP_CONTENT
				);
				layoutParams.startToEnd = R.id.noteCheckbox;
				layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
				layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
				layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

				this.binding.layoutAddNew.setLayoutParams(layoutParams);
			}

			AttachableNote attachableNote = (AttachableNote) defaultNote;

			//      Get Favoritism
			boolean isFavorite = defaultNote.isFavorite();

			if (isFavorite)
			{
				binding.iconFavorited.setVisibility(View.VISIBLE);
			}
			else
			{
				binding.iconFavorited.setVisibility(View.GONE);
			}

			//      Get Containers
			List<AttachableNote_Container> containers = attachableNote.getContainers();

			if (containers != null)
			{
				if (containers.size() >= 1)
				{
					this.binding.noteContainer1.setText(containers.get(0).getContainerName());
					this.binding.noteContainer1.setVisibility(View.VISIBLE);
				}
				else
				{
					this.binding.noteContainer1.setText("");
					this.binding.noteContainer1.setVisibility(View.GONE);
				}

				if (containers.size() >= 2)
				{
					this.binding.noteContainer2.setText(containers.get(1).getContainerName());
					this.binding.noteContainer2.setVisibility(View.VISIBLE);
				}
				else
				{
					this.binding.noteContainer2.setText("");
					this.binding.noteContainer2.setVisibility(View.GONE);
				}
				if (containers.size() >= 3)
				{
					this.binding.noteContainer3.setText(containers.get(2).getContainerName());
					this.binding.noteContainer3.setVisibility(View.VISIBLE);
				}
				else
				{
					this.binding.noteContainer3.setText("");
					this.binding.noteContainer3.setVisibility(View.GONE);
				}
			}


			//      Get Title
			String title = attachableNote.getTitle();
			//			Log.d("NotesAdapter", "Title: " + title);

			if (title.length() == 0)
			{
				binding.noteTitle.setText(null);
				binding.noteTitle.setVisibility(View.GONE);
			}
			else
			{
				binding.noteTitle.setText(title);
				binding.noteTitle.setVisibility(View.VISIBLE);
			}
			//      Date and Type
			String dateAndType = NotesAdapter.GetShorterDate(attachableNote.getDate()) + " | " + attachableNote.getClass().getSimpleName().replace("Note", "");

			binding.noteDateAndType.setText(dateAndType);
			binding.noteDateAndType.setVisibility(View.VISIBLE);

			//      Note Main Content
			String content = attachableNote.getContent().substring(0, Math.min(100, attachableNote.getContent().length()));

			binding.noteContent.setText(content);
		}
	}

	public static String GetShorterDate(Date dateCreated)
	{
		Date dateCurrent = Calendar.getInstance().getTime();

		Calendar calendarCreated = Calendar.getInstance();
		Calendar calendarCurrent = Calendar.getInstance();

		calendarCreated.setTime(dateCreated);
		calendarCurrent.setTime(dateCurrent);

		//      Holy mother of ifs elses
		if (calendarCreated.get(Calendar.YEAR) != calendarCurrent.get(Calendar.YEAR)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

			return dateFormat.format(dateCreated);
		}
		else if (calendarCreated.get(Calendar.MONTH) != calendarCurrent.get(Calendar.MONTH))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

			return dateFormat.format(dateCreated);
		}
		else if (calendarCreated.get(Calendar.DAY_OF_MONTH) != calendarCurrent.get(Calendar.DAY_OF_MONTH)) {
			int day = calendarCurrent.get(Calendar.DAY_OF_MONTH) - calendarCreated.get(Calendar.DAY_OF_MONTH);
			int week = day / 7;

			if (week > 1) {
				return week + " weeks ago";
			}
			else if (week == 1) {
				return week + " week ago";
			}
			else if (day > 1) {
				return day + " days ago";
			}
			else {
				return day + " day ago";
			}
		}
		else if (calendarCreated.get(Calendar.HOUR_OF_DAY) != calendarCurrent.get(Calendar.HOUR_OF_DAY)) {
			int hour = calendarCurrent.get(Calendar.HOUR_OF_DAY) - calendarCreated.get(Calendar.HOUR_OF_DAY);

			if (hour > 1) {
				return hour + " hours ago";
			}
			else {
				return hour + " hour ago";
			}
		}
		else if (calendarCreated.get(Calendar.MINUTE) != calendarCurrent.get(Calendar.MINUTE)) {
			int minute = calendarCurrent.get(Calendar.MINUTE) - calendarCreated.get(Calendar.MINUTE);

			if (minute > 1) {
				return minute + " minutes ago";
			}
			else {
				return minute + " minute ago";
			}
		}
		else
		{
			return "Just now";
		}
	}
}
