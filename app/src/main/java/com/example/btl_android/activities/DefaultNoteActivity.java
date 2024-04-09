package com.example.btl_android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityDefaultNoteBinding;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.NewlineInputFilter;
import com.example.btl_android.utilities.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

public class DefaultNoteActivity
		extends AppCompatActivity {
    private ActivityDefaultNoteBinding binding;
    private boolean isEditing = true;
    private boolean isFavorite = false;
    private Date dateCreated = null;
    private String fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.binding = ActivityDefaultNoteBinding.inflate(getLayoutInflater());
        this.setContentView(this.binding.getRoot());

        //      Filters
        this.binding.etTitle.setFilters(new InputFilter[]{new NewlineInputFilter()});

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        PreferenceManager preferenceManager = new PreferenceManager(this);
        this.isEditing = preferenceManager.getBoolean(Constants.SETTINGS_NOTE_DEFAULT_IS_EDITING);

        //      Listeners
        this.SetListeners();
        this.ReadToActivity(bundle);
        this.SetEditting(this.isEditing);
    }
    @Override
    public void onBackPressed() {
        View view = new View(this);

        if (this.SaveNote(view)) {
            super.onBackPressed();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Leave Confirmation");
            builder.setMessage("Note can not be saved, do you still want to leave?");

            builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    finish();
                }
            });

            builder.setNegativeButton("Noooo", null);

            builder.create().show();

            return;
        }
    }
    private void SetListeners() {
        //      Back Button
        this.binding.backButton.setOnClickListener(view ->
        {
            if (this.SaveNote(view))
            {
                setResult(Activity.RESULT_OK);

                finish();
            }
            else
            {
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

        // Menu Button giống Attachable
        this.binding.settingsButton.setOnClickListener(view ->
        {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.settingsButton);

            popupMenu.getMenuInflater().inflate(R.menu.menu_attachable_note, popupMenu.getMenu());

            if (this.isFavorite)
            {
                popupMenu.getMenu().findItem(R.id.menuItemAddToFavorite).setTitle("Remove from Favorites");
            }
            else
            {
                popupMenu.getMenu().findItem(R.id.menuItemAddToFavorite).setTitle("Add to Favorites");
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem)
                {
                    int id = menuItem.getItemId();

                    if (id == R.id.menuItemAddToFavorite)
                    {
                        DefaultNoteActivity.this.isFavorite = !DefaultNoteActivity.this.isFavorite;
                    }
                    else if (id == R.id.menuItemDelete)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DefaultNoteActivity.this);

                        builder.setTitle("Delete Confirmation");
                        builder.setMessage("You Sure Babe ?");

                        builder.setPositiveButton("Yes :>", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (fileName == null) {
                                    finish();
                                    return;
                                }

                                if (_DefaultNote.DeleteFromStorage(DefaultNoteActivity.this, fileName)) {
                                    DefaultNoteActivity.this.finish();
                                }
                                else {
                                    Log.d("DefaultNoteActivityTemp", "FileName = " + fileName);

                                    Toast.makeText(DefaultNoteActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("Nooooo", null);

                        builder.create().show();
                    }
                    return true;
                }
            });
            // Showing menu
            popupMenu.show();
        });

        //      Pencil/book button
        this.binding.editButton.setOnClickListener(view ->
        {
            this.SetEditting(!this.isEditing);
        });


    }
    private void ReadToActivity(Bundle bundle) {
        if (bundle == null) {
            // tao moi
        }
        else {
            // Sua
            String fileName = bundle.getString(Constants.BUNDLE_FILENAME_KEY);

            _DefaultNote defaultNote = _DefaultNote.ReadFromStorage(this, fileName);

            // Cập nhật các thành phần trên giao diện người dùng với dữ liệu đã đọc
            this.binding.etTitle.setText(defaultNote.getTitle());
            this.binding.etContent.setText(defaultNote.getContent());
            this.isFavorite = defaultNote.isFavorite();
            this.dateCreated = defaultNote.getDateCreated();
            this.fileName = fileName;
        }
    }
    private boolean SaveNote(View view) {
        view.requestFocus();

        if (this.dateCreated == null)
        {
            this.dateCreated = Calendar.getInstance().getTime();
        }

        _DefaultNote defaultNote = new _DefaultNote(
                this.fileName,
                this.binding.etTitle.getText().toString(),
                this.dateCreated,
                Calendar.getInstance().getTime(),
                this.binding.etContent.getText().toString(),
                this.isFavorite
        );

        if (!defaultNote.Validate()) {
            return false;
        }

        return defaultNote.WriteToStorage(this, false);
    }
    public void SetEditting(boolean isEditting)
    {
        // Trang thai chi doc khong duoc sua
        if(!isEditting){
            this.binding.editButton.setImageResource(R.drawable.icon_read);

            this.binding.etContent.setFocusable(false);
            this.binding.etContent.setFocusableInTouchMode(false);
            this.binding.etContent.setHint("");

            this.binding.etTitle.setFocusable(false);
            this.binding.etTitle.setFocusableInTouchMode(false);
            this.binding.etTitle.setHint("");

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(this.binding.etContent.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(this.binding.etTitle.getWindowToken(), 0);
        }
        else{
            //có thẻ sua
            this.binding.editButton.setImageResource(R.drawable.icon_edit);
            this.binding.etContent.setFocusable(true);
            this.binding.etContent.setFocusableInTouchMode(true);
            this.binding.etContent.setHint(R.string.this_is_the_note_s_text_content);

            this.binding.etTitle.setFocusable(true);
            this.binding.etTitle.setFocusableInTouchMode(true);
            this.binding.etTitle.setHint(R.string.title_of_the_note);
        }
        this.isEditing = isEditting;

    }
}




