package com.example.btl_android.models;

import android.content.Context;

import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class _DefaultNote implements Serializable
{
	//      Saved Values
	protected String fileName;
	protected String title;
	protected Date dateCreated;
	protected Date dateModified;
	protected String content;
	protected boolean isFavorite;

	//      Not Saved Value
	protected boolean isChecked = false;

	//      SORTING!
	public enum SortType {
		ByTitle,
		ByDateCreated,
		ByDateModified
	}
	public static SortType sortType = SortType.ByDateCreated;

	public _DefaultNote() {};

	public _DefaultNote(_DefaultNote defaultNote)
	{
		this.fileName = defaultNote.getFileName();
		this.title = defaultNote.getTitle();
		this.dateCreated = defaultNote.getDateCreated();
		this.content = defaultNote.getContent();
		this.isFavorite = defaultNote.isFavorite();
	}

	public _DefaultNote(String fileName, String title, Date dateCreated, Date dateModified, String content,
	                    boolean isFavorite)
	{
		this.fileName = fileName;
		this.title = title;
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
		this.content = content;
		this.isFavorite = isFavorite;
	}

	public String getFileName()
	{
		return this.fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		if (title == null)
		{
			this.title = "";
		}
		else
		{
			this.title = title;
		}
	}

	public Date getDateCreated()
	{
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	public Date getDateModified()
	{
		return dateModified;
	}

	public void setDateModified(Date dateModified)
	{
		this.dateModified = dateModified;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		if (content == null)
		{
			this.content = "";
		}
		else
		{
			this.content = content;
		}
	}

	public boolean isFavorite()
	{
		return isFavorite;
	}

	public void setFavorite(boolean favorite)
	{
		isFavorite = favorite;
	}

	public boolean isChecked()
	{
		return isChecked;
	}

	public void setChecked(boolean checked)
	{
		isChecked = checked;
	}

	public boolean Validate()
	{
		return (!Objects.equals(this.title, "") || !Objects.equals(this.content, ""));
	}

	public boolean WriteToStorage(Context context, boolean isDuplicated)
	{
		return false;
	}

	public static _DefaultNote ReadFromStorage(Context context, String fileName)
	{
		return null;
	}

	public static boolean DeleteFromStorage(Context context, String fileName)
	{
		PreferenceManager preferenceManager = new PreferenceManager(context);
		String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
		File file = new File(directory, fileName);

		return file.delete();
	}
}
