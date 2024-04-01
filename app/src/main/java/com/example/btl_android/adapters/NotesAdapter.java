package com.example.btl_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.activities.TodoNoteActivity;
import com.example.btl_android.databinding.ItemContainerSmallAttachableNoteBinding;
import com.example.btl_android.databinding.ItemContainerSmallTaskNoteBinding;
import com.example.btl_android.databinding.ItemContainerSmallTodoNoteBinding;
import com.example.btl_android.interfaces.NoteViewHolderInterface;
import com.example.btl_android.listeners.NoteListener;
import com.example.btl_android.listeners.TaskNoteViewHolderListener;
import com.example.btl_android.models.AttachableNote;
import com.example.btl_android.models.AttachableNote_Container;
import com.example.btl_android.models.TaskNote;
import com.example.btl_android.models.TaskNote_SubTask;
import com.example.btl_android.models.TodoListNote;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;

import java.lang.ref.PhantomReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private final List<_DefaultNote> notesList;
	private final NoteListener noteListener;
	private final Context context;
	private final PreferenceManager preferenceManager;
	private boolean isEditing = false;

	public NotesAdapter(List<_DefaultNote> notesList, NoteListener noteListener, Context context)
	{
		this.notesList = notesList;
		this.noteListener = noteListener;
		this.context = context;
		this.preferenceManager = new PreferenceManager(context);
	}

	public void SetEditing(boolean isEditing)
	{
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
		else if (viewType == Constants.TASK_NOTE)
		{
			return new TaskNoteViewHolder(
					ItemContainerSmallTaskNoteBinding.inflate(
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
		else if (getItemViewType(position) == Constants.TASK_NOTE)
		{
			TaskNoteViewHolder taskNoteViewHolder = (TaskNoteViewHolder) holder;

			taskNoteViewHolder.LoadSummarizedNote(this.notesList.get(position));
			taskNoteViewHolder.SetListeners(this.notesList.get(position));
		}
		else if(getItemViewType(position) == Constants.TODO_NOTE) {
			Log.d("Thai: Load", "LoadSummarizedNote: ");
			TodoNoteViewHolder todoNoteViewHolder = (TodoNoteViewHolder) holder;
			todoNoteViewHolder.LoadSummarizedNote(this.notesList.get(position));
			todoNoteViewHolder.SetListeners(this.notesList.get(position));
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
		_DefaultNote defaultNote = this.notesList.get(position);

		//      TODO: Các bạn bổ sung if else getItemViewType tương ứng của các bạn
		if (defaultNote.getClass() == AttachableNote.class)
		{
			return Constants.ATTACHABLE_NOTE;
		}
		else if (defaultNote.getClass() == TaskNote.class)
		{
			return Constants.TASK_NOTE;
		}
		else if(this.notesList.get(position).getClass() == TodoListNote.class) {
			return Constants.TODO_NOTE;
		}
 		else
		{
			Log.d("ERROREEEEEEEEEE", "getItemViewType: Unknown Note");

			return -1;
		}
	}

	public static String GetShorterDate(Date dateThen)
	{
		Calendar calendarThen = Calendar.getInstance();
		Calendar calendarCurrent = Calendar.getInstance();

		calendarThen.setTime(dateThen);

		String time;

		//      Holy mother of ifs elses
		if (calendarThen.get(Calendar.YEAR) != calendarCurrent.get(Calendar.YEAR)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

			return dateFormat.format(dateThen);
		}
		else if (calendarThen.get(Calendar.MONTH) != calendarCurrent.get(Calendar.MONTH))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

			return dateFormat.format(dateThen);
		}
		else if (calendarThen.get(Calendar.DAY_OF_MONTH) != calendarCurrent.get(Calendar.DAY_OF_MONTH)) {
			int day =
					Math.abs(calendarCurrent.get(Calendar.DAY_OF_MONTH) - calendarThen.get(Calendar.DAY_OF_MONTH));
			int week =
					Math.abs(day / 7);

			if (week > 1) {
				time = week + " weeks";
			}
			else if (week == 1) {
				time = week + " week";
			}
			else if (day > 1) {
				time = day + " days";
			}
			else {
				time = day + " day";
			}
		}
		else if (calendarThen.get(Calendar.HOUR_OF_DAY) != calendarCurrent.get(Calendar.HOUR_OF_DAY)) {
			int hour =
					Math.abs(calendarCurrent.get(Calendar.HOUR_OF_DAY) - calendarThen.get(Calendar.HOUR_OF_DAY));

			if (hour > 1) {
				time = hour + " hours";
			}
			else {
				time = hour + " hour";
			}
		}
		else if (calendarThen.get(Calendar.MINUTE) != calendarCurrent.get(Calendar.MINUTE)) {
			int minute =
					Math.abs(calendarCurrent.get(Calendar.MINUTE) - calendarThen.get(Calendar.MINUTE));

			if (minute > 1) {
				time =  minute + " minutes";
			}
			else {
				time =  minute + " minute";
			}
		}
		else
		{
			return "Just now";
		}

		//      If more or less?
		if (calendarThen.getTime().after(calendarCurrent.getTime()))
		{
			return "In " + time;
		}
		else
		{
			return time + " ago";
		}
	}

	//      TODO: Các bạn bổ sung ViewHolder của các bạn:
	//          + extends RecyclerView.ViewHolder
	//          + implement NoteViewHolderInterface
	//          + Tham khảo AttachableNoteViewHolder


	public class TodoNoteViewHolder extends RecyclerView.ViewHolder implements NoteViewHolderInterface
	{
		private final ItemContainerSmallTodoNoteBinding binding;

		public TodoNoteViewHolder(ItemContainerSmallTodoNoteBinding itemContainerSmallTodoNoteBinding)
		{
			super(itemContainerSmallTodoNoteBinding.getRoot());
			binding = itemContainerSmallTodoNoteBinding;
		}

		@Override
		public void SetListeners(_DefaultNote defaultNote)
		{
			binding.itemContainerSmallTodoNote.setOnClickListener(v -> {
				Intent intent = new Intent(binding.getRoot().getContext(), TodoNoteActivity.class);

				binding.getRoot().getContext().startActivity(intent);
			});
		}

		@Override
		public void LoadSummarizedNote(_DefaultNote defaultNote)
		{
			Log.d("Thai: Load", "LoadSummarizedNote: ");
			TodoListNote todoListNote = (TodoListNote) defaultNote;
			binding.todoNoteListQuantity.setText(todoListNote.getTitle());
		}
	}

	public class TaskNoteViewHolder extends RecyclerView.ViewHolder implements NoteViewHolderInterface,
			TaskNoteViewHolderListener
	{
		private final ItemContainerSmallTaskNoteBinding binding;
		private SmallTaskNote_SubTaskAdapter subTaskAdapter;
		private List<TaskNote_SubTask> subTasksList;

		private TaskNote taskNote;

		public TaskNoteViewHolder(ItemContainerSmallTaskNoteBinding itemContainerSmallTaskNoteBinding)
		{
			super(itemContainerSmallTaskNoteBinding.getRoot());

			this.binding = itemContainerSmallTaskNoteBinding;

			this.subTaskAdapter = null;
			this.subTasksList = null;
		}

		private void RepeatTaskNote(TaskNote taskNote)
		{
			TypedValue typedValuePrimary = new TypedValue();
			Resources.Theme theme = context.getTheme();
			theme.resolveAttribute(
					com.google.android.material.R.attr.colorOnPrimary,
					typedValuePrimary,
					true
			);

			if (!taskNote.isChecked())
			{
				Toast.makeText(NotesAdapter.this.context,
						"Repeating the TaskNote canceled...",
						Toast.LENGTH_SHORT
				).show();

				return;
			}

			taskNote.setChecked(false);

			for (TaskNote_SubTask subTask : this.subTasksList)
			{
				subTask.setDone(false);
			}
			this.subTaskAdapter.notifyItemRangeChanged(0, this.subTasksList.size());

			this.binding.noteTaskCheckbox.setChecked(false);
			this.binding.noteContent.setTextColor(
							typedValuePrimary.data
			);
			this.binding.noteTitle.setTextColor(
							typedValuePrimary.data
			);

			taskNote.setDueDate(
					TaskNote.AcceleDate(
							taskNote.getDueDate(),
							taskNote.getRepeatableCount(),
							taskNote.getRepeatableType()
					)
			);

			String dateAndType =
					NotesAdapter.GetShorterDate(taskNote.getDueDate()) +
							" | " +
							taskNote.getClass().getSimpleName().replace("Note", "");

			this.binding.noteDateAndType.setText(dateAndType);
			this.binding.noteDateAndType.setVisibility(View.VISIBLE);

			if (Calendar.getInstance().getTime().after(taskNote.getDueDate()))
			{
				this.binding.noteDateAndType.setTextColor(
						ContextCompat.getColor(NotesAdapter.this.context, R.color.red)
				);
			}
			else
			{
				this.binding.noteDateAndType.setTextColor(
						ContextCompat.getColor(NotesAdapter.this.context, R.color.green)
				);
			}

			if (!taskNote.WriteToStorage(NotesAdapter.this.context, false))
			{
				Log.d("AttachableNoteViewHolderTemp", "Failed to write to storage");

				Toast.makeText(NotesAdapter.this.context,
						"Failed to save the Note",
						Toast.LENGTH_SHORT).show();
			}
		}

		public void SetListeners(_DefaultNote defaultNote)
		{
			this.taskNote = (TaskNote) defaultNote;

			this.binding.noteCheckbox.setOnClickListener(view ->
			{
				this.taskNote.setChecked(this.binding.noteCheckbox.isChecked());
			});

			this.binding.noteTaskCheckbox.setOnClickListener(view ->
			{
				boolean isChecked = this.binding.noteTaskCheckbox.isChecked();

				this.taskNote.setChecked(isChecked);

				TypedValue typedValueSecondary = new TypedValue(), typedValuePrimary = new TypedValue();
				Resources.Theme theme = context.getTheme();
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

				this.binding.noteContent.setTextColor(
						isChecked ?
								typedValueSecondary.data :
								typedValuePrimary.data
				);
				this.binding.noteTitle.setTextColor(
						isChecked ?
								typedValueSecondary.data :
								typedValuePrimary.data
				);

				if (!isChecked)
				{
					if (!this.taskNote.WriteToStorage(NotesAdapter.this.context, false))
					{
						Log.d("AttachableNoteViewHolderTemp", "Failed to write to storage");

						Toast.makeText(NotesAdapter.this.context,
								"Failed to save the Note",
								Toast.LENGTH_SHORT).show();
					}
				}
				else if (this.taskNote.IsRepeatable())
				{
					Toast.makeText(NotesAdapter.this.context,
							"Note has been completed! Preparing to repeat the TaskNote...",
							Toast.LENGTH_SHORT
					).show();

					(new Handler()).postDelayed(() -> RepeatTaskNote(this.taskNote), 3000);
				}
				else
				{
					if (NotesAdapter.this.preferenceManager.getBoolean(Constants.TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION))
					{
						//      Delete On Completion
						Toast.makeText(NotesAdapter.this.context,
								"Note has been completed! Deleting TaskNote...", Toast.LENGTH_SHORT
						).show();

						String tempDeleteAfter =
								NotesAdapter.this.preferenceManager.getString(Constants.TASK_NOTE_SETTINGS_DELETE_AFTER);

						(new Handler()).postDelayed(() ->
						{
							if (!_DefaultNote.DeleteFromStorage(NotesAdapter.this.context,
									this.taskNote.getFileName()
							))
							{
								Log.d("AttachableNoteViewHolderTemp", "Failed to delete Note");

								Toast.makeText(NotesAdapter.this.context, "Failed to save the Note",
										Toast.LENGTH_SHORT
								).show();
							}

							NotesAdapter.this.notifyItemRemoved(NotesAdapter.this.notesList.indexOf(defaultNote));
						}, tempDeleteAfter == null ? 2000 :
								Integer.parseInt(tempDeleteAfter) * 1000L);
					}
					else
					{
						//      Naw
						Toast.makeText(NotesAdapter.this.context,
								"Note has been completed! TaskNote does not repeat...",
								Toast.LENGTH_SHORT
						).show();

						if (!this.taskNote.WriteToStorage(NotesAdapter.this.context, false))
						{
							Log.d("AttachableNoteViewHolderTemp", "Failed to write to storage");

							Toast.makeText(NotesAdapter.this.context,
									"Failed to save the Note",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			if (NotesAdapter.this.isEditing)
			{
				//      Meh
				this.binding.getRoot().setOnClickListener(view ->
				{
					this.binding.noteCheckbox.setChecked(!this.binding.noteCheckbox.isChecked());

					this.taskNote.setChecked(this.binding.noteCheckbox.isChecked());
				});

				//      Context Menu
				this.binding.getRoot().setOnLongClickListener(view ->
				{
					this.binding.noteCheckbox.setChecked(!this.binding.noteCheckbox.isChecked());

					this.taskNote.setChecked(this.binding.noteCheckbox.isChecked());

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

					noteListener.onNoteLongClick();

					return false;
				});
			}
		}

		public void LoadSummarizedNote(_DefaultNote defaultNote)
		{
			//      Conversion / Initiate
			this.taskNote = (TaskNote) defaultNote;

			this.subTasksList = this.taskNote.getSubTasks();
			this.subTaskAdapter = new SmallTaskNote_SubTaskAdapter(
					this.subTasksList,
					this,
					NotesAdapter.this.context
			);
			this.binding.noteSubTaskRecyclerView.setAdapter(this.subTaskAdapter);
			this.subTaskAdapter.notifyDataSetChanged();

			//      If Editing
			if (NotesAdapter.this.isEditing)
			{
				ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
						(
						0,
						ConstraintLayout.LayoutParams.WRAP_CONTENT
				);
				layoutParams.startToEnd = R.id.noteCheckbox;
				layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
				layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
				layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

				this.binding.layoutAddNew.setLayoutParams(layoutParams);

				this.binding.noteTaskCheckbox.setVisibility(View.GONE);

				this.subTaskAdapter.SetEditing(true);
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

				this.binding.noteTaskCheckbox.setVisibility(View.VISIBLE);

				this.subTaskAdapter.SetEditing(false);
			}

			//      Get Favoritism
			boolean isFavorite = defaultNote.isFavorite();

			if (isFavorite)
			{
				this.binding.iconFavorited.setVisibility(View.VISIBLE);
			}
			else
			{
				this.binding.iconFavorited.setVisibility(View.GONE);
			}

			//      Get Title
			String title = this.taskNote.getTitle();
			//			Log.d("NotesAdapter", "Title: " + title);

			if (title.length() == 0)
			{
				this.binding.noteTitle.setText(null);
				this.binding.noteTitle.setVisibility(View.GONE);
			}
			else
			{
				this.binding.noteTitle.setText(title);
				this.binding.noteTitle.setVisibility(View.VISIBLE);
			}

			//      Date and Type
			String dateAndType = null;

			if (this.taskNote.isHasDeadline())
			{
				dateAndType = NotesAdapter.GetShorterDate(this.taskNote.getDueDate()) +
						" | " +
						this.taskNote.getClass().getSimpleName().replace("Note", "");

				if (Calendar.getInstance().getTime().after(this.taskNote.getDueDate()))
				{
					this.binding.noteDateAndType.setTextColor(ContextCompat.getColor(NotesAdapter.this.context, R.color.red));
				}
				else
				{
					this.binding.noteDateAndType.setTextColor(ContextCompat.getColor(NotesAdapter.this.context, R.color.green));
				}
			}
			else
			{
				dateAndType = NotesAdapter.GetShorterDate(this.taskNote.getDateCreated()) +
						" | " +
						this.taskNote.getClass().getSimpleName().replace("Note", "");

				TypedValue typedValue = new TypedValue();
				Resources.Theme theme = context.getTheme();
				theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue,
						true);

				this.binding.noteDateAndType.setTextColor(typedValue.data);
			}

			this.binding.noteDateAndType.setText(dateAndType);
			this.binding.noteDateAndType.setVisibility(View.VISIBLE);

			//      Repeatable Icon
			if (this.taskNote.IsRepeatable())
			{
				this.binding.iconRepeatable.setVisibility(View.VISIBLE);
			}
			else
			{
				this.binding.iconRepeatable.setVisibility(View.GONE);
			}

			//      Note Main Content
			String content = this.taskNote.getContent().substring(0, Math.min(100, this.taskNote.getContent().length()));

			this.binding.noteContent.setText(content);
		}

		@Override
		public void OnSubTaskTick(int position, boolean isChecked)
		{
			this.taskNote.setSubTasks(this.subTasksList);

			if (!this.taskNote.WriteToStorage(NotesAdapter.this.context, false))
			{
				Log.d("AttachableNoteViewHolderTemp", "Failed to write to storage");

				Toast.makeText(NotesAdapter.this.context,
						"Failed to save the Note",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

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

					noteListener.onNoteLongClick();

					return false;
				});
			}
		}

		@Override
		public void LoadSummarizedNote(_DefaultNote defaultNote)
		{
			//      Conversion / Editing
			AttachableNote attachableNote = (AttachableNote) defaultNote;

			//      If Editing
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

			//      Get Favoritism
			boolean isFavorite = defaultNote.isFavorite();

			if (isFavorite)
			{
				this.binding.iconFavorited.setVisibility(View.VISIBLE);
			}
			else
			{
				this.binding.iconFavorited.setVisibility(View.GONE);
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
				this.binding.noteTitle.setText(null);
				this.binding.noteTitle.setVisibility(View.GONE);
			}
			else
			{
				this.binding.noteTitle.setText(title);
				this.binding.noteTitle.setVisibility(View.VISIBLE);
			}

			//      Date and Type
			String dateAndType =
					NotesAdapter.GetShorterDate(attachableNote.getDateCreated()) +
							" | " +
							attachableNote.getClass().getSimpleName().replace("Note", "");

			this.binding.noteDateAndType.setText(dateAndType);
			this.binding.noteDateAndType.setVisibility(View.VISIBLE);

			//      Note Main Content
			String content = attachableNote.getContent().substring(0, Math.min(100, attachableNote.getContent().length()));

			this.binding.noteContent.setText(content);
		}
	}
}
