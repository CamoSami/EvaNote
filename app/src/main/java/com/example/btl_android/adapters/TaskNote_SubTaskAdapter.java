package com.example.btl_android.adapters;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.activities.TaskNoteActivity;
import com.example.btl_android.databinding.ItemContainerSmallTaskNoteSubtaskBinding;
import com.example.btl_android.databinding.ItemContainerTaskNoteSubtaskBinding;
import com.example.btl_android.listeners.TaskNoteListener;
import com.example.btl_android.models.TaskNote;
import com.example.btl_android.models.TaskNote_SubTask;

import java.util.List;

public class TaskNote_SubTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private final List<TaskNote_SubTask> subTasksList;
	private final Context context;
	private final TaskNoteListener taskNoteListener;
	private boolean isEditing = true;

	public TaskNote_SubTaskAdapter(List<TaskNote_SubTask> subTaskList, Context context,
			TaskNoteListener TaskNoteListener )
	{
		this.subTasksList = subTaskList;
		this.context = context;
		this.taskNoteListener = TaskNoteListener;
	}

	public void SetEditing(boolean isEditing) {
		this.isEditing = isEditing;

		this.notifyItemRangeChanged(0, this.subTasksList.size());
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		return new TaskNote_SubTaskAdapter.SubTaskNoteViewHolder(
				ItemContainerTaskNoteSubtaskBinding.inflate(
						LayoutInflater.from(parent.getContext()
						),
						parent,
						false
				)
		);
	}
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
	{
		TaskNote_SubTaskAdapter.SubTaskNoteViewHolder
				subTaskNoteViewHolder = (TaskNote_SubTaskAdapter.SubTaskNoteViewHolder) holder;

		subTaskNoteViewHolder.LoadSummarizedSubTask(this.subTasksList.get(position));
		subTaskNoteViewHolder.SetListeners(this.subTasksList.get(position));
	}

	@Override public int getItemCount()
	{
		return this.subTasksList.size();
	}

	public class SubTaskNoteViewHolder extends RecyclerView.ViewHolder
	{
		private final ItemContainerTaskNoteSubtaskBinding binding;

		public SubTaskNoteViewHolder(ItemContainerTaskNoteSubtaskBinding itemContainerTaskNoteSubtaskBinding)
		{
			super(itemContainerTaskNoteSubtaskBinding.getRoot());

			this.binding = itemContainerTaskNoteSubtaskBinding;
		}

		public void SetListeners(TaskNote_SubTask subTask)
		{
			this.binding.noteSubTaskDelete.setOnClickListener(view -> {
				TaskNote_SubTaskAdapter.this.taskNoteListener
						.OnSubTaskDelete(TaskNote_SubTaskAdapter.this.subTasksList.indexOf(subTask));
			});

			if (TaskNote_SubTaskAdapter.this.isEditing)
			{
				this.binding.noteSubTaskCheckBox.setOnClickListener(view ->
				{
					//					Log.d("TaskNoteViewHolderTemp", "Event Ran!");

					subTask.setDone(this.binding.noteSubTaskCheckBox.isChecked());

					this.LoadSummarizedSubTask(subTask);
				});

				this.binding.noteSubTaskTitle.setOnFocusChangeListener(new View.OnFocusChangeListener()
				{
					@Override public void onFocusChange(View v, boolean hasFocus)
					{
						if (!hasFocus) {
//							Log.d("FileNote_LinkViewHolderTemp", "Content: " + binding.containerContent.getText().toString());

							subTask.setTaskTitle(binding.noteSubTaskTitle.getText().toString());
						}
					}
				});
			}
			else
			{
				this.binding.noteSubTaskCheckBox.setOnClickListener(view ->
				{
					//					Log.d("TaskNoteViewHolderTemp", "Event Ran!");

					subTask.setDone(this.binding.noteSubTaskCheckBox.isChecked());

					this.LoadSummarizedSubTask(subTask);
				});

				this.binding.noteSubTaskTitle.setOnClickListener(view ->
				{
					this.binding.noteSubTaskCheckBox.setChecked(!this.binding.noteSubTaskCheckBox.isChecked());

					subTask.setDone(this.binding.noteSubTaskCheckBox.isChecked());

					this.LoadSummarizedSubTask(subTask);
				});
			}
		}

		public void LoadSummarizedSubTask(TaskNote_SubTask subTask)
		{
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

			this.binding.noteSubTaskTitle.setText(subTask.getTaskTitle());
			boolean isDone = subTask.isDone();

			if (isDone)
			{
				this.binding.noteSubTaskCheckBox.setChecked(true);
				this.binding.noteSubTaskTitle.setTextColor(typedValueSecondary.data);
			}
			else
			{
				this.binding.noteSubTaskCheckBox.setChecked(false);
				this.binding.noteSubTaskTitle.setTextColor(typedValuePrimary.data);
			}

			if (TaskNote_SubTaskAdapter.this.isEditing)
			{
				this.binding.noteSubTaskDelete.setVisibility(View.VISIBLE);

				this.binding.noteSubTaskTitle.setFocusable(true);
				this.binding.noteSubTaskTitle.setFocusableInTouchMode(true);
			}
			else
			{
				this.binding.noteSubTaskDelete.setVisibility(View.GONE);

				InputMethodManager imm = (InputMethodManager) getSystemService(context, InputMethodManager.class);

				imm.hideSoftInputFromWindow(this.binding.noteSubTaskTitle.getWindowToken(), 0);

				this.binding.noteSubTaskTitle.setFocusable(false);
				this.binding.noteSubTaskTitle.setFocusableInTouchMode(false);
			}
		}
	}
}
