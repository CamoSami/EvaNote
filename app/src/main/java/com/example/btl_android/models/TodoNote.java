package com.example.btl_android.models;

import java.util.ArrayList;
import java.util.UUID;

public class TodoNote extends _DefaultNote {
    private UUID id;
    private boolean isDone = false;
    public int layer = 1;

    public ArrayList<TodoNote> todoNotes;

    public TodoNote() {
        this.id = UUID.randomUUID();
    }

    public TodoNote(int layer){
        this.layer = layer;
    }

    public TodoNote(String title, boolean isDone) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.isDone = isDone;
        this.todoNotes = new ArrayList<>();
    }

    public TodoNote(String title, int layer, boolean isDone) {
        this.title = title;
        this.isDone = isDone;
        this.layer = layer;
        this.todoNotes = new ArrayList<>();
    }

    public TodoNote(TodoNote todoNote) {
        super(todoNote);

        this.id = todoNote.id;
        this.layer = todoNote.layer;
        this.title = todoNote.getTitle();
        this.isDone = todoNote.isDone();
        this.todoNotes = todoNote.getTodoNotes();
    }

    public UUID getId() {
        return id;
    }

    public void setRandomId() {
        this.id = UUID.randomUUID();
    }

    public void setDone (boolean isDone) {
        this.isDone = isDone;
    }
    public boolean isDone() {
        return this.isDone;
    }

    public ArrayList<TodoNote> getTodoNotes() {
        return todoNotes;
    }
}
