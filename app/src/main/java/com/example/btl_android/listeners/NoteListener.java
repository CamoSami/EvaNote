package com.example.btl_android.listeners;

public interface NoteListener
{
	void onNoteClick(int position);

	void onNoteLongClick();
	boolean isEditting();
}
