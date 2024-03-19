package com.example.btl_android.listeners;

public interface NoteListener
{
	void onNoteClick(int position);

	void onNoteLongClick(int position);
	boolean isEditting();
}
