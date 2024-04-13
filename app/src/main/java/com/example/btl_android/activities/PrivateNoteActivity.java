package com.example.btl_android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityPrivateNoteBinding;
import com.example.btl_android.models.PrivateNote;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

public class PrivateNoteActivity
		extends AppCompatActivity {
    private ActivityPrivateNoteBinding binding;
    private boolean isEditing = true;
    private boolean isFavorite = false;
    private String fileName = null;
    private Date dateCreated = null;
    private boolean isConfirmed = false;
    public static final int REQUEST_CODE_ADD_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityPrivateNoteBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        this.ReadToActivity(bundle);
        this.SetListeners();
    }
    //Trả về activity được truyền từ main

    private void ReadToActivity(Bundle bundle) {
        if (bundle == null)
        {
            //      If New
            this.fileName = null;
            this.binding.titleEditText.setText(null);
            this.binding.editTextText.setText(null);
            this.isFavorite = false;
        }
        else
        {
            PreferenceManager preferenceManager = new PreferenceManager(this);
            this.isEditing = preferenceManager.getBoolean(Constants.SETTINGS_NOTE_DEFAULT_IS_EDITING);

            //      If Edit
            String fileName = bundle.getString(Constants.BUNDLE_FILENAME_KEY);
            PrivateNote privateNote = PrivateNote.ReadFromStorage(this, fileName);

            this.fileName = fileName;
            this.binding.titleEditText.setText(privateNote.getTitle());
            this.binding.editTextText.setText(privateNote.getContent());
            this.isFavorite = privateNote.isFavorite();
            this.dateCreated = privateNote.getDateCreated();
        }
    }

    private void SetListeners(){
        this.binding.backButton.setOnClickListener(view ->{
            this.onBackPressed();
        });

        this.binding.editButton.setOnClickListener(view ->
        {
            this.SetEditing(!this.isEditing);
        });

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
                        PrivateNoteActivity.this.isFavorite = !PrivateNoteActivity.this.isFavorite;
                    }
                    else if (id == R.id.menuItemDelete)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PrivateNoteActivity.this);

                        builder.setTitle("Delete Confirmation");
                        builder.setMessage("Are you sure you want to delete the Note?");

                        builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (fileName == null) {
                                    finish();

                                    return;
                                }

                                if (_DefaultNote.DeleteFromStorage(PrivateNoteActivity.this, fileName)) {
                                    PrivateNoteActivity.this.finish();
                                }
                                else {
                                    Log.d("AttachableNoteActivityTemp", "FileName = " + fileName);

                                    Toast.makeText(PrivateNoteActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("Nah", null);

                        builder.create().show();
                    }
                    return true;
                }
            });

            // Showing the popup menu
            popupMenu.show();
        });
    }

    @Override
    public void onBackPressed()
    {
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
                    if (PrivateNoteActivity.this.fileName != null)
                    {
                        _DefaultNote.DeleteFromStorage(PrivateNoteActivity.this,
                                PrivateNoteActivity.this.fileName);
                    }

                    finish();
                }
            });

            builder.setNegativeButton("Nah", null);

            builder.create().show();

            return;
        }
    }

    private boolean SaveNote(View view) {
        view.requestFocus();

        if (this.dateCreated == null)
        {
            this.dateCreated = Calendar.getInstance().getTime(); // lấy thời gian mặc định của hệ thống và trả về đối tượng date
        }

        PrivateNote privateNote = new PrivateNote(
                this.fileName,
                this.binding.titleEditText.getText().toString(),
                this.dateCreated,
                Calendar.getInstance().getTime(),
                this.binding.editTextText.getText().toString(),
                this.isFavorite
        );

        //      Not validated, just simply want to leave
        if (!privateNote.Validate()) {
            this.isConfirmed = true;
            this.finish();
            return false;
        }

        if (privateNote.WriteToStorage(this, false))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void SetEditing(boolean isEditing)
    {
        if (!isEditing)
        {
            this.binding.editButton.setImageResource(R.drawable.icon_read);

            this.binding.editTextText.setFocusable(false);
            this.binding.editTextText.setFocusableInTouchMode(false);
            this.binding.editTextText.setHint("");

            this.binding.titleEditText.setFocusable(false);
            this.binding.titleEditText.setFocusableInTouchMode(false);
            this.binding.titleEditText.setHint("");

           // this.binding.contentTextView.setEnabled(false);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(this.binding.editTextText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(this.binding.titleEditText.getWindowToken(), 0);
        }
        else
        {
            this.binding.editButton.setImageResource(R.drawable.icon_edit);

            this.binding.editTextText.setFocusable(true);
            this.binding.editTextText.setFocusableInTouchMode(true);
            this.binding.editTextText.setHint(R.string.this_is_the_note_s_text_content);

            this.binding.titleEditText.setFocusable(true);
            this.binding.titleEditText.setFocusableInTouchMode(true);
            this.binding.titleEditText.setHint(R.string.title_of_the_note);

            //this.binding.contentTextView.setEnabled(true);

        }

        this.isEditing = isEditing;
    }
}