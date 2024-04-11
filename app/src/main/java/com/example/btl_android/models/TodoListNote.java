package com.example.btl_android.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class TodoListNote extends TodoNote {

    public TodoListNote() {
        super();
    }

    public TodoListNote(String title, boolean isDone) {
        super(title, isDone);

        this.dateCreated = new Date();
        this.dateModified = new Date();
        this.todoNotes = new ArrayList<>();
    }

    public TodoListNote(TodoListNote todoListNote) {
        super(todoListNote);

        this.dateCreated = todoListNote.dateCreated;
        this.dateModified = todoListNote.dateModified;
        this.todoNotes = todoListNote.todoNotes;
    }

    public boolean checkAllTodoDone() {
        Log.d("TodoNoteAdapter dddd", "isChecked: ");
        if(this.todoNotes.size() == 1 && this.todoNotes.get(0).getTitle().length() == 0) {
            return false;
        }

        for(TodoNote todoNote : this.todoNotes) {
            if(!todoNote.isDone() && todoNote.getTitle().length() > 0) {
                return false;
            }
        }

        return true;
    }
}
