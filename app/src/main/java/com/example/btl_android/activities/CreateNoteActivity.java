package com.example.btl_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.btl_android.databinding.ActivityCreateNoteBinding;

public class CreateNoteActivity extends AppCompatActivity
{

	private ActivityCreateNoteBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.binding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
		this.setContentView(this.binding.getRoot());

		this.SetListeners();
	}

	private void SetListeners()
	{
		//  TODO: Các bạn tự bổ sung
		this.binding.newDefaultNote.setOnClickListener(view -> {
			Toast.makeText(this, "No Default Note yet", Toast.LENGTH_SHORT).show();
		});

		this.binding.newPrivateNote.setOnClickListener(view -> {
			Intent intent = new Intent(this, EnterPassWordActivity.class);

			startActivity(intent);

			finish();
		});

		this.binding.newReminderNote.setOnClickListener(view -> {
			Toast.makeText(this, "No Reminder Note yet", Toast.LENGTH_SHORT).show();
		});

		this.binding.newTodoNote.setOnClickListener(view -> {
			Intent intent = new Intent(this, TodoNoteActivity.class);

			startActivity(intent);

			finish();
		});

		this.binding.newAttachableNote.setOnClickListener(view ->
		{
			Intent intent = new Intent(this, AttachableNoteActivity.class);

			startActivity(intent);

			finish();
		});

		this.binding.newTaskNote.setOnClickListener(view ->
		{
			Intent intent = new Intent(this, TaskNoteActivity.class);

			startActivity(intent);

			finish();
		});

		this.binding.backButton.setOnClickListener(view ->
		{
			onBackPressed();
		});
	}
}