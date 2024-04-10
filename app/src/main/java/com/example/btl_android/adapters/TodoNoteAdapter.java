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

import com.example.btl_android.databinding.TodoNoteItemBinding;
import com.example.btl_android.listeners.TodoNoteViewHolderListener;
import com.example.btl_android.models.TodoListNote;
import com.example.btl_android.models.TodoNote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TodoNoteAdapter extends RecyclerView.Adapter<TodoNoteAdapter.TodoNoteViewHolder>
{
    private final ArrayList<TodoNote> todoNotes;

    private TodoListNoteAdapter todoListNoteAdapter;

    private ArrayList<TodoListNote> todoListNotes;

    private TodoListNote todoListNoteCurrent;

    private TodoNoteViewHolderListener todoNoteViewHolderListener;

    private boolean isEditing;

    public TodoNoteAdapter(ArrayList<TodoNote> todoNotes, TodoListNoteAdapter todoListNoteAdapter,
                           TodoListNote todoListNoteCurrent, boolean isEditing,
                           TodoNoteViewHolderListener todoNoteViewHolderListener
    )
    {
        this.todoNotes = todoNotes;
        this.todoListNoteAdapter = todoListNoteAdapter;
        this.todoListNoteCurrent = todoListNoteCurrent;
        this.todoListNotes = todoListNoteAdapter.getTodoListNotes();
        this.isEditing = isEditing;
        this.todoNoteViewHolderListener = todoNoteViewHolderListener;
    }

    @NonNull @Override
    public TodoNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        TodoNoteItemBinding view =
                TodoNoteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new TodoNoteViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull TodoNoteViewHolder holder, int position)
    {
        Log.d("ReadFilesByHelpers", "onBindViewHolder: ");

        TodoNoteViewHolder itemViewHolder = (TodoNoteViewHolder) holder;
        TodoNote item = this.todoNotes.get(position);

        itemViewHolder.SetData(item);
        itemViewHolder.SetListener(item, this.todoNotes, this);

        //        Log.d("Testing", "onBindViewHolder: " + TodoNoteAdapter.this.todoListNoteAdapter);
        TodoNoteAdapter adapter = new TodoNoteAdapter(item.getTodoNotes(), TodoNoteAdapter.this.todoListNoteAdapter,
                TodoNoteAdapter.this.todoListNoteCurrent, this.isEditing, this.todoNoteViewHolderListener
        );
        holder.recyclerSubTodoView.setLayoutManager(new LinearLayoutManager(holder.recyclerSubTodoView.getContext()));
        holder.recyclerSubTodoView.setAdapter(adapter);

        adapter.notifyItemChanged(holder.getAdapterPosition());
    }

    @Override public int getItemCount()
    {
        return this.todoNotes.size();
    }

    public class TodoNoteViewHolder
            extends RecyclerView.ViewHolder
    {
        public TodoNoteItemBinding binding;
        private boolean isShowed = true;
        public RecyclerView recyclerSubTodoView;

        public TodoNoteViewHolder(@NonNull TodoNoteItemBinding itemView)
        {
            super(itemView.getRoot());

            this.binding = itemView;
            this.recyclerSubTodoView = this.binding.recyclerSubTodoView;
        }

        public void SetData(TodoNote item)
        {
            Log.d("ReadFilesByHelpers", "TodoNoteViewHolder.SetData: ");

            this.binding.todoName.setText(item.getTitle());
            this.binding.todoCheckBox.setChecked(item.isChecked());

            this.recyclerSubTodoView.setVisibility(View.GONE);

            if (!TodoNoteAdapter.this.isEditing)
            {
                this.binding.todoItemTrash.setVisibility(View.GONE);
                this.binding.todoName.setFocusable(false);
            }

            if (TodoNoteAdapter.this.isEditing)
            {
                this.binding.todoItemTrash.setVisibility(View.VISIBLE);
                this.binding.todoName.setFocusable(true);
            }
        }

        public void SetListener(TodoNote item, ArrayList<TodoNote> todoNotes, TodoNoteAdapter adapter
        )
        {
            this.binding.todoName.setOnLongClickListener(v ->
            {
                if (this.binding.todoName.length() == 0 ||
                        todoNotes.get(getAdapterPosition()).layer > 1)
                {
                    return false;
                }

                // Add new item if empty item
                if (item.getTodoNotes().size() == 0 && item.layer == 1)
                {
                    System.out.println("Add new item");
                    TodoNote newTodoNote = new TodoNote("", 2);
                    item.getTodoNotes().add(newTodoNote);
                }

                // Change show/hide
                if (this.isShowed)
                {
                    this.isShowed = false;
                    this.binding.recyclerSubTodoView.setVisibility(View.VISIBLE);
                }
                else
                {
                    this.isShowed = true;
                    this.binding.recyclerSubTodoView.setVisibility(View.GONE);
                }

                return true;
            });
            this.binding.todoName.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    // Update item changed
                    todoNotes.get(getAdapterPosition()).setTitle(s.toString());

                    // Check last item is edited
                    int index = todoNotes.indexOf(item);
                    if (index == todoNotes.size() - 1 && item.getTitle().length() > 0)
                    {
                        System.out.println("Add new item");
                        TodoNote newTodoNote = new TodoNote("", item.layer);
                        todoNotes.add(newTodoNote);
                        adapter.notifyItemInserted(todoNotes.size() - 1);
                    }
                }

                @Override public void afterTextChanged(Editable s)
                {

                }
            });

            this.binding.todoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
            {
                Log.d("Checked---", "SetListener: ");

                if (isChecked)
                {
                    item.setChecked(true);
                }
                else
                {
                    item.setChecked(false);
                }

                if (item.layer == 1)
                {
                    if (TodoNoteAdapter.this.todoListNoteCurrent.checkAllTodoChecked())
                    {
                        TodoNoteAdapter.this.todoListNoteCurrent.setChecked(true);

                        // Sort todo list
                        this.sortTodoListNotes();

                        TodoNoteAdapter.this.todoListNoteAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        TodoNoteAdapter.this.todoListNoteCurrent.setChecked(false);
                    }

                    TodoNoteAdapter.this.todoListNoteAdapter.notifyDataSetChanged();
                }

                if (TodoNoteAdapter.this.todoNoteViewHolderListener != null)
                {
                    TodoNoteAdapter.this.todoNoteViewHolderListener.onTodoNoteChanged();
                }
            });

            this.binding.todoItemTrash.setOnClickListener(v ->
            {
                todoNotes.remove(getAdapterPosition());
                adapter.notifyItemRemoved(getAdapterPosition());
                adapter.notifyItemRangeChanged(getAdapterPosition(), todoNotes.size());
            });
        }

        public void sortTodoListNotes()
        {
            Collections.sort(TodoNoteAdapter.this.todoListNotes, new Comparator<TodoListNote>()
            {
                @Override public int compare(TodoListNote o1, TodoListNote o2)
                {
                    return o1.isChecked() ? 1 : -1;
                }
            });
        }
    }

    public void setEditing(boolean isEditting)
    {
        this.isEditing = isEditting;
    }
}