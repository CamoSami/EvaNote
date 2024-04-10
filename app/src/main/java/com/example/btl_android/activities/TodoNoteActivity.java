package com.example.btl_android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.btl_android.R;
import com.example.btl_android.adapters.TodoListNoteAdapter;
import com.example.btl_android.databinding.ActivityTodoNoteBinding;
import com.example.btl_android.databinding.DialogAddTodoListItemBinding;
import com.example.btl_android.models.TodoListNote;
import com.example.btl_android.models.TodoNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.Helpers;

import java.util.ArrayList;
import java.util.Date;

public class TodoNoteActivity
		extends AppCompatActivity {
    private final ArrayList<TodoListNote> data = new ArrayList<>();

    private ArrayList<TodoListNote> todoListNotes = new ArrayList<>();

    private TodoListNote todoListNote;

    private boolean isEditting = false;

    private boolean isCreating = false;

    private TodoListNoteAdapter adapter;
    private ActivityTodoNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityTodoNoteBinding.inflate(getLayoutInflater());
        this.setContentView(this.binding.getRoot());
        this.adapter = new TodoListNoteAdapter(this.data);
        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.binding.recyclerView.setAdapter(this.adapter);

        this.SetData();
        this.SetListeners();
        this.SetUI();
    }

    private void SetListeners() {
        //      Back Button
        this.binding.backButton.setOnClickListener(view ->
        {

            if(this.isCreating) {
                this.writeFile();
            }
            else {
                // Update Data
                if(this.todoListNotes != null && this.todoListNotes.size() != 0) {
                    boolean isRemoved = this.todoListNotes.removeIf(todoListNote -> todoListNote.getId().equals(this.todoListNote.getId()));

                    if (isRemoved) {
                        if(this.data.size() != 0) {
                            this.todoListNote.setDateModified(new Date()); // Update Date Modified
                            this.todoListNotes.add(this.data.get(0));
                        }
                    }

                    this.data.clear();
                    this.data.addAll(this.todoListNotes);

                    this.writeFile();
                }
            }
        });

        //      Add New Todo List
        this.binding.addNewTodoBtn.setOnClickListener(view ->
        {
            //      TODO
            PopupMenu popupMenu = new PopupMenu(this, this.binding.addNewTodoBtn);
            popupMenu.getMenuInflater().inflate(R.menu.menu_add_todo_popup, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.newTodoList) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    DialogAddTodoListItemBinding dialogAddTodoListItemBinding = DialogAddTodoListItemBinding.inflate(getLayoutInflater());
                    builder.setView(dialogAddTodoListItemBinding.getRoot());

                    builder.setPositiveButton("Add", (dialog, which) -> {
                        String newTitle = dialogAddTodoListItemBinding.todoListItemTitle.getText().toString();
                        System.out.println("New Title: " + newTitle);
                        AddNewTodolist(newTitle);
                    });


                    builder.create().show();

                    return true;
                }
                return false;
            });

            // Showing the popup menu
            popupMenu.show();
        });

        // Edit mode
        this.binding.editToDoNoteButton.setOnClickListener(view -> {
            this.isEditting = !this.isEditting;
            SetUI();
        });
    }

    private void SetData() {
        Intent intent = this.getIntent();
        this.todoListNote = (TodoListNote) intent.getSerializableExtra(Constants.TODO_NOTE_KEY);

        this.isEditting = intent.getBooleanExtra(Constants.IS_TODO_NOTE_EDITING_KEY, false);
        this.isCreating = intent.getBooleanExtra(Constants.IS_TODO_NOTE_CREATING_KEY, false);

        Helpers<TodoListNote> helpers = new Helpers<>();
        this.todoListNotes = helpers.ReadFile(this, Constants.JSON_TODO_NOTE_NAME_FILE, TodoListNote.class);

        if(!isEditting) {
            if(this.todoListNotes != null && this.todoListNotes.size() != 0) {
                this.data.addAll(this.todoListNotes);
            }
        }
        else {
            if(this.todoListNote != null) {
                Log.d("TodoNote intent 2", "onCreate: " + this.todoListNote.getTitle());

                this.data.add(this.todoListNote);
            }
        }

        this.adapter.notifyItemInserted(this.data.size() - 1);
    }

    private void SetUI() {
        if (this.isEditting) {
            this.binding.editToDoNoteButton.setImageResource(R.drawable.icon_edit);

        }
        else {
            this.binding.addNewTodoBtn.setVisibility(View.VISIBLE);
            this.binding.editToDoNoteButton.setImageResource(R.drawable.icon_read);
        }

        if(this.isCreating && this.isEditting) {
            this.binding.addNewTodoBtn.setVisibility(View.VISIBLE);
        }
        else {
            this.binding.addNewTodoBtn.setVisibility(View.GONE);
        }

        this.adapter.setEditing(this.isEditting);
        this.adapter.notifyDataSetChanged();
    }

    private void AddNewTodolist(String title) {
        TodoListNote todoList = new TodoListNote(title.length() > 0 ? title : "New Todo List");
        TodoNote todoNote = new TodoNote("");
        todoList.getTodoNotes().add(todoNote);

        this.data.add(todoList);
        this.adapter.notifyItemInserted(this.data.size() - 1); // notify();
    }

    private void writeFile() {
        Helpers<ArrayList<TodoListNote>> helpers = new Helpers<>();
        String json = helpers.transferObjectToJson(this.data);

        boolean isWritten = Helpers.WriteFile(this, Constants.JSON_TODO_NOTE_NAME_FILE, json);

        if (isWritten) {
            setResult(Activity.RESULT_OK);

            finish();
        }
        else {
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
        }
    }
}