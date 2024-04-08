package com.example.btl_android.models;

import java.util.ArrayList;
import java.util.UUID;

public class TodoNote extends _DefaultNote {
    private UUID id;
    public int layer = 1;

    public ArrayList<TodoNote> todoNotes;

    public TodoNote() {
        this.id = UUID.randomUUID();
    }

    public TodoNote(int layer){
        this.layer = layer;
    }

    public TodoNote(String title) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.isChecked = false;
        this.todoNotes = new ArrayList<>();
    }

    public TodoNote(String title, int layer) {
        this.title = title;
        this.layer = layer;
        this.todoNotes = new ArrayList<>();
    }

    public TodoNote(TodoNote todoNote) {
        this.id = todoNote.id;
        this.layer = todoNote.layer;
        this.title = todoNote.getTitle();
        this.isChecked = todoNote.isChecked;
        this.todoNotes = todoNote.getTodoNotes();
    }
    public UUID getId() {
        return id;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public ArrayList<TodoNote> getTodoNotes() {
        return todoNotes;
    }
}
