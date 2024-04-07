package com.example.btl_android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityPrivateNoteBinding;
import com.example.btl_android.models.PrivateNote;
import com.example.btl_android.utilities.Constants;

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
        if (bundle == null) {
            //      If New
            this.fileName = null;
            this.binding.titleEditText.setText(null);
            this.binding.editTextText.setText(null);
            this.isFavorite = false;
        }
        else {
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
            if (this.SaveNote(view)) {
                setResult(Activity.RESULT_OK);

                finish();
            }
            else {
                if (this.isConfirmed) {
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Leave Confirmation");
                builder.setMessage("Note can not be saved, do you still want to leave?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                builder.setNegativeButton("No", null);

                builder.create().show(); //Dialog được tạo và hiện lên màn hình

                return;
            }
            //startActivity(MainActivity);
        });
        this.binding.editButton.setOnClickListener(view ->
        {
            this.SetEditting(!this.isEditing);
        });
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
    public void SetEditting(boolean isEditting)
    {
        if (!isEditting)
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

        this.isEditing = isEditting;
    }
}