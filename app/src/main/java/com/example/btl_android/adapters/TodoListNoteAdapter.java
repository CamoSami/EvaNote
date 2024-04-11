package com.example.btl_android.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.R;
import com.example.btl_android.databinding.TodoListNoteItemBinding;
import com.example.btl_android.models.TodoListNote;

import java.util.ArrayList;

public class TodoListNoteAdapter extends RecyclerView.Adapter<TodoListNoteAdapter.TodoListNoteViewHolder>
{
    private ArrayList<TodoListNote> todoListNotes;

    private TodoNoteAdapter adapter;

    private boolean isEditing = false;

    public TodoListNoteAdapter(ArrayList<TodoListNote> todoListNotes)
    {
        this.todoListNotes = todoListNotes;
    }

    @NonNull @Override
    public TodoListNoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        TodoListNoteItemBinding view =
                TodoListNoteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                        false
                );
        return new TodoListNoteViewHolder(view);
    }

    @Override public void onBindViewHolder(TodoListNoteViewHolder holder, int position)
    {
        TodoListNoteViewHolder itemViewHolder = (TodoListNoteViewHolder) holder;

        TodoListNote item = this.todoListNotes.get(position);

        this.adapter = new TodoNoteAdapter(item.getTodoNotes(), this, item, this.isEditing, null);
        holder.todoRecyclerView.setLayoutManager(new LinearLayoutManager(holder.todoRecyclerView.getContext()));
        holder.todoRecyclerView.setAdapter(this.adapter);

        itemViewHolder.SetData(item);
        itemViewHolder.SetListener(this.todoListNotes);
    }

    @Override public int getItemCount()
    {
        return this.todoListNotes.size();
    }

    class TodoListNoteViewHolder
            extends RecyclerView.ViewHolder
    {
        public TodoListNoteItemBinding binding;
        public RecyclerView todoRecyclerView;

        public TodoListNoteViewHolder(TodoListNoteItemBinding itemView)
        {
            super(itemView.getRoot());
            this.binding = itemView;
            this.todoRecyclerView = this.binding.recyclerTodoView;
        }

        public void SetData(TodoListNote todoListNote)
        {
            this.binding.todoListTitle.setText(todoListNote.getTitle());

            TodoListNoteAdapter.this.adapter.setEditing(TodoListNoteAdapter.this.isEditing);
            if (!isEditing)
            {
                this.binding.todoListItemTrash.setVisibility(View.GONE);
                this.binding.todoListTitle.setEnabled(false);
            }
            else
            {
                this.binding.todoListItemTrash.setVisibility(View.VISIBLE);
                this.binding.todoListTitle.setEnabled(true);
            }

            // Set background if all todo is checked
            Log.d("CheckAll", "SetData: " + todoListNote.checkAllTodoDone());
            if (todoListNote.checkAllTodoDone())
            {
                this.binding.todoListItem.setBackground(this.binding.getRoot().getContext().getDrawable(R.drawable.background_todolist_finished));
            }
            else
            {
                this.binding.todoListItem.setBackground(this.binding.getRoot().getContext().getDrawable(R.drawable.background_content_darker));
            }
        }

        public void SetListener(ArrayList<TodoListNote> todoListNotes)
        {
            this.binding.todoListTitle.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    todoListNotes.get(getAdapterPosition()).setTitle(s.toString());
                }

                @Override public void afterTextChanged(Editable s)
                {

                }
            });

            this.binding.todoListItemTrash.setOnClickListener(v ->
            {
                todoListNotes.remove(getAdapterPosition());
                Log.d("Remove aaa", "SetListener: " + todoListNotes.size());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), todoListNotes.size());
            });
        }
    }

    public void setEditing(boolean isEditting)
    {
        this.isEditing = isEditting;
    }

    public ArrayList<TodoListNote> getTodoListNotes()
    {
        return this.todoListNotes;
    }
}
