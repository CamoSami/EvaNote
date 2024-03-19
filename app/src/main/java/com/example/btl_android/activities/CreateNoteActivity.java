package com.example.btl_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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
		this.binding.newAttachableNote.setOnClickListener(view ->
		{
			Intent intent = new Intent(this, AttachableNoteActivity.class);

			startActivity(intent);

			finish();
		});

		this.binding.backButton.setOnClickListener(view ->
		{
			onBackPressed();
		});
	}
}