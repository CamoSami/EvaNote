package com.example.btl_android.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodoNote extends _DefaultNote {
    public int layer = 1;

    public ArrayList<TodoNote> todoNotes;

    public TodoNote() {}

    public TodoNote(int layer){
        this.layer = layer;
    }

    public TodoNote(String title) {
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
        this.title = todoNote.getTitle();
        this.isChecked = todoNote.isChecked;
        this.todoNotes = todoNote.getTodoNotes();
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public ArrayList<TodoNote> getTodoNotes() {
        return todoNotes;
    }
}
