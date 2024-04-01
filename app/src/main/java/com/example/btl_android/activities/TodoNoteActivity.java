package com.example.btl_android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.PopupMenu;

import com.example.btl_android.R;
import com.example.btl_android.adapters.TodoListNoteAdapter;
import com.example.btl_android.databinding.ActivityTodoNoteBinding;
import com.example.btl_android.databinding.DialogAddTodoListItemBinding;
import com.example.btl_android.models.TodoListNote;
import com.example.btl_android.models.TodoNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.Helpers;

import java.util.ArrayList;

public class TodoNoteActivity extends AppCompatActivity {
    private final ArrayList<TodoListNote> data = new ArrayList<>();

    private TodoListNoteAdapter adapter;
    private ActivityTodoNoteBinding binding;

    private final Helpers<ArrayList<TodoListNote>> helpers = new Helpers<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityTodoNoteBinding.inflate(getLayoutInflater());
        this.setContentView(this.binding.getRoot());
        this.adapter = new TodoListNoteAdapter(this.data);
        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.binding.recyclerView.setAdapter(this.adapter);

        //      Listeners
        this.SetListeners();
//        this.SetData();
    }

    private void SetData() {
        ArrayList<TodoListNote> data = new ArrayList<>();

        TodoListNote todoListNote1 = new TodoListNote("Title 1");
        TodoListNote todoListNote2 = new TodoListNote("Title 2");
        TodoListNote todoListNote3 = new TodoListNote("Title 3");

        TodoNote SubTodoNote1 = new TodoNote("Sub Todo 1", 2);
        TodoNote SubTodoNote2 = new TodoNote("Sub Todo 2", 2);
        TodoNote SubTodoNote3 = new TodoNote("Sub Todo 3", 2);

        TodoNote todoNote1 = new TodoNote("Todo 1");
        TodoNote todoNote2 = new TodoNote("Todo 2");
        TodoNote todoNote3 = new TodoNote("Todo 3");

        todoNote1.getTodoNotes().add(SubTodoNote1);
        todoNote1.getTodoNotes().add(SubTodoNote2);

        todoNote2.getTodoNotes().add(SubTodoNote2);
        todoNote2.getTodoNotes().add(SubTodoNote3);

        todoListNote1.getTodoNotes().add(todoNote1);
        todoListNote1.getTodoNotes().add(todoNote2);
        todoListNote1.getTodoNotes().add(todoNote3);

        data.add(todoListNote1);
        data.add(todoListNote2);
        data.add(todoListNote3);

        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TodoListNoteAdapter adapter = new TodoListNoteAdapter(data);
        this.binding.recyclerView.setAdapter(adapter);
    }

    private void SetListeners() {
        //      Back Button
        this.binding.backButton.setOnClickListener(view ->
        {
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

                return;
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
    }

    private void AddNewTodolist(String title) {
        TodoListNote todoList = new TodoListNote(title.length() > 0 ? title : "New Todo List");
        TodoNote todoNote = new TodoNote("");
        todoList.getTodoNotes().add(todoNote);

        this.data.add(todoList);
        this.adapter.notifyItemInserted(this.data.size() - 1); // notify();
    }
}