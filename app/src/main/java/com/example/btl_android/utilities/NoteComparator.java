package com.example.btl_android.utilities;

import android.provider.ContactsContract;

import com.example.btl_android.models._DefaultNote;

import java.util.Comparator;

public class NoteComparator implements Comparator<_DefaultNote>
{
	@Override public int compare(_DefaultNote o1, _DefaultNote o2)
	{
		if (_DefaultNote.sortType == _DefaultNote.SortType.ByTitle)
		{
			return o1.getTitle().compareTo(o2.getTitle());
		}
		else if (_DefaultNote.sortType == _DefaultNote.SortType.ByDateCreated)
		{
			return o1.getDateCreated().compareTo(o2.getDateCreated());
		}
		else
		{
			return o1.getDateModified().compareTo(o2.getDateModified());
		}
	}
}
