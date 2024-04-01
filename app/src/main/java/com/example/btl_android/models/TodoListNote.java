package com.example.btl_android.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class TodoListNote extends TodoNote {
    private Date createdDate;
    private Date updatedDate;

    public TodoListNote() {
        super();
    }

    public TodoListNote(String title) {
        super(title);
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }

    public TodoListNote(TodoListNote todoListNote) {
        super(todoListNote);
        this.createdDate = todoListNote.getCreatedDate();
        this.updatedDate = todoListNote.getUpdatedDate();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean checkAllTodoChecked() {
        Log.d("TodoNoteAdapter dddd", "isChecked: ");
        if(this.todoNotes.size() == 1 && this.todoNotes.get(0).getTitle().length() == 0) {
            return false;
        }

        for(TodoNote todoNote : this.todoNotes) {
            if(!todoNote.isChecked && todoNote.getTitle().length() > 0) {
                return false;
            }
        }

        return true;
    }
}
