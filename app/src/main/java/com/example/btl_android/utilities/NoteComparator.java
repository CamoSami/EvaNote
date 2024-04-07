package com.example.btl_android.utilities;

import com.example.btl_android.models.TaskNote;
import com.example.btl_android.models._DefaultNote;

import java.util.Comparator;

import javax.sql.CommonDataSource;

public class NoteComparator implements Comparator<_DefaultNote>
{
	public static boolean isReversed = true;
	public static boolean taskNoteSortToBottomOnCompletion = false;
	public static boolean taskNoteDeleteOnCompletion = false;

	@Override public int compare(_DefaultNote o1, _DefaultNote o2)
	{
		//      If only 1 of them is Favorite -> The Favorite ones are up to the Top!
		if ((o1.isFavorite() || o2.isFavorite()) && !(o1.isFavorite() && o2.isFavorite()))
		{
			return o2.isFavorite() ? 1 : -1;
		}

		//      Check if 1 of them is taskNote something something
		if (
				!NoteComparator.taskNoteDeleteOnCompletion &&
				NoteComparator.taskNoteSortToBottomOnCompletion
			)
		{
			if (o1 instanceof TaskNote && o2 instanceof TaskNote)
			{
				TaskNote taskNote1 = (TaskNote) o1;
				TaskNote taskNote2 = (TaskNote) o2;

				if (taskNote1.isDone() && !taskNote2.isDone())
				{
					return 1;
				}
				else if (!taskNote1.isDone() && taskNote2.isDone())
				{
					return -1;
				}
			}
			else if (o1 instanceof TaskNote)
			{
				TaskNote taskNote1 = (TaskNote) o1;

				if (taskNote1.isDone())
				{
					return 1;
				}
			}
			else if (o2 instanceof TaskNote)
			{
				TaskNote taskNote2 = (TaskNote) o2;

				if (taskNote2.isDone())
				{
					return -1;
				}
			}
		}

		int returnInt;

		if (_DefaultNote.sortType == _DefaultNote.SortType.ByTitle)
		{
			returnInt = o1.getTitle().compareTo(o2.getTitle());
		}
		else if (_DefaultNote.sortType == _DefaultNote.SortType.ByDateCreated)
		{
			returnInt = o1.getDateCreated().compareTo(o2.getDateCreated());
		}
		else
		{
			returnInt = o1.getDateModified().compareTo(o2.getDateModified());
		}

		if (NoteComparator.isReversed)
		{
			return -returnInt;
		}
		else
		{
			return returnInt;
		}
	}
}
