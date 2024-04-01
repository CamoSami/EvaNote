package com.example.btl_android.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ItemContainerSmallTaskNoteSubtaskBinding;
import com.example.btl_android.listeners.TaskNoteViewHolderListener;
import com.example.btl_android.models.TaskNote_SubTask;

import java.util.List;

public class SmallTaskNote_SubTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private final List<TaskNote_SubTask> subTasksList;
	private final Context context;
	private final TaskNoteViewHolderListener taskNoteListener;
	private boolean isEditing = false;

	public SmallTaskNote_SubTaskAdapter(List<TaskNote_SubTask> subTaskList, TaskNoteViewHolderListener taskNoteListener,
	                                    Context context)
	{
		this.subTasksList = subTaskList;
		this.taskNoteListener = taskNoteListener;
		this.context = context;
	}

	public void SetEditing(boolean isEditing) {
		this.isEditing = isEditing;

		this.notifyItemRangeChanged(0, this.subTasksList.size());
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		return new SmallSubTaskNoteViewHolder(
				ItemContainerSmallTaskNoteSubtaskBinding.inflate(
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
		SmallSubTaskNoteViewHolder smallSubTaskNoteViewHolder = (SmallSubTaskNoteViewHolder) holder;

		smallSubTaskNoteViewHolder.LoadSummarizedSubTask(this.subTasksList.get(position));
		smallSubTaskNoteViewHolder.SetListeners(this.subTasksList.get(position));
	}

	@Override public int getItemCount()
	{
		return this.subTasksList.size();
	}

	public class SmallSubTaskNoteViewHolder extends RecyclerView.ViewHolder
	{
		private final ItemContainerSmallTaskNoteSubtaskBinding binding;

		public SmallSubTaskNoteViewHolder(ItemContainerSmallTaskNoteSubtaskBinding itemContainerSmallTaskNoteSubtaskBinding)
		{
			super(itemContainerSmallTaskNoteSubtaskBinding.getRoot());

			this.binding = itemContainerSmallTaskNoteSubtaskBinding;
		}

		public void SetListeners(TaskNote_SubTask subTask)
		{
			if (SmallTaskNote_SubTaskAdapter.this.isEditing)
			{
				//      Nothing, really
			}
			else
			{
				this.binding.noteSubTaskCheckBox.setOnClickListener(view ->
				{
//					Log.d("SmallTaskNoteViewHolderTemp", "Event Ran!");

					subTask.setDone(this.binding.noteSubTaskCheckBox.isChecked());

					SmallTaskNote_SubTaskAdapter.this.taskNoteListener.OnSubTaskTick(
							SmallTaskNote_SubTaskAdapter.this.subTasksList.indexOf(subTask),
							this.binding.noteSubTaskCheckBox.isChecked()
					);

					this.LoadSummarizedSubTask(subTask);
				});

				this.binding.noteSubTaskTitle.setOnClickListener(view ->
				{
					this.binding.noteSubTaskCheckBox.setChecked(!this.binding.noteSubTaskCheckBox.isChecked());

					subTask.setDone(this.binding.noteSubTaskCheckBox.isChecked());

					SmallTaskNote_SubTaskAdapter.this.taskNoteListener.OnSubTaskTick(
							SmallTaskNote_SubTaskAdapter.this.subTasksList.indexOf(subTask),
							this.binding.noteSubTaskCheckBox.isChecked()
					);

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

			if (SmallTaskNote_SubTaskAdapter.this.isEditing)
			{
				this.binding.noteSubTaskCheckBox.setVisibility(View.GONE);
			}
			else
			{
				this.binding.noteSubTaskCheckBox.setVisibility(View.VISIBLE);
			}

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
		}
	}
}
