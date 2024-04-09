package com.example.btl_android.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		//      Initiate
		Map<String, Map<String, String>> noteMap = new HashMap<>();
		SimpleDateFormat longDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());

		//      Write Defaults
		Map<String, String> map = new HashMap<>();

		map.put(Constants.JSON_DEFAULT_TITLE, this.getTitle());
		map.put(Constants.JSON_DEFAULT_DATE_CREATED, longDateFormat.format(this.getDateCreated()));
		map.put(Constants.JSON_DEFAULT_DATE_MODIFIED, longDateFormat.format(this.getDateModified()));
		map.put(Constants.JSON_DEFAULT_CONTENT, this.getContent());
		map.put(Constants.JSON_DEFAULT_FAVORITE, String.valueOf(this.isFavorite()));

		noteMap.put(Constants.JSON_DEFAULT, map);

		//      Get GSON class to write Map<> -> JSON
		Gson gson = new Gson();
		String json = gson.toJson(noteMap);

		//      Get Directory via PrefereceManager
		PreferenceManager preferenceManager = new PreferenceManager(context);
		String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
		File file;
		int i = 1;

		if (this.fileName == null)
		{
			//      TODO: File + Date Format + .txt
			this.fileName = "DefaultNote" + longDateFormat.format(this.getDateCreated()) + ".txt";

			file = new File(directory, this.fileName);

			try
			{
				while (!file.createNewFile()) {
					this.fileName = "DefaultNote" + longDateFormat.format(this.getDateCreated()) + "(" + i++ + ").txt";

					file = new File(directory, this.fileName);
				}
			}
			catch (IOException e)
			{
				Log.d("DefaultNoteTemp", "File not created");
				Log.d("DefaultNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

				Toast.makeText(context, "Note can not be created, please give adequate permissions!", Toast.LENGTH_SHORT).show();

				e.printStackTrace();

				return false;
			}
		}
		else if (isDuplicated)
		{
			//      Duplication!
			String regex = "\\([0-9]+\\)\\.txt";
			String fileName = this.fileName.substring(this.fileName.lastIndexOf(".") - 3, this.fileName.length());

			Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
			Matcher matcher = pattern.matcher(fileName);

			if (matcher.matches())
			{

				fileName = this.fileName.substring(0, this.fileName.lastIndexOf(".") - 3);
			}
			else
			{

				fileName =  this.fileName.substring(0, this.fileName.lastIndexOf("."));
			}

			//      Create a New File with Similar Name
			file = new File(directory, this.fileName = fileName + "(" + i++ + ").txt");
			try
			{
				while (!file.createNewFile()) {
					this.fileName = fileName + "(" + i++ + ").txt";

					file = new File(directory, this.fileName);
				}
			}
			catch (IOException e)
			{
				Log.d("DefaultNoteTemp", "File not created");
				Log.d("DefaultNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

				Toast.makeText(context, "Note can not be created, please give adequate permissions!", Toast.LENGTH_SHORT).show();

				e.printStackTrace();

				return false;
			}
		}
		else
		{
			file = new File(directory, this.fileName);
		}

		//      Attempt to Write File
		try
		{
			FileOutputStream stream = new FileOutputStream(file);

			stream.write(json.getBytes());
			stream.close();
		}
		catch (IOException e)
		{
			Log.d("DefaultNoteTemp", "File not written");
			Log.d("DefaultNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

			Toast.makeText(context, "Note can not be written, please give adequate permissions!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();

			return false;
		}

		return true;
	}

	public static _DefaultNote ReadFromStorage(Context context, String fileName) {
		//      Initiate
		TypeToken<Map<String, Map<String, String>>> typeToken = new TypeToken<Map<String, Map<String, String>>>() {};
		SimpleDateFormat longDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());
		Gson gson = new Gson();

		PreferenceManager preferenceManager = new PreferenceManager(context);
		String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
		File file = new File(directory, fileName);
		String readFile;

		try {
			FileInputStream stream = new FileInputStream(file);

			BufferedReader br = new BufferedReader( new InputStreamReader(stream, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String readFileGradually;

			while(( readFileGradually = br.readLine()) != null ) {
				sb.append( readFileGradually );
				sb.append( '\n' );
			}

			readFile = sb.toString();
		}
		catch (IOException e)
		{
			Log.d("DefaultNoteTemp", "File not pre-read");
			Log.d("DefaultNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

			Toast.makeText(context, "Note can not be read, please give adequate permissions!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();

			return null;
		}

		Map<String, Map<String, String>> noteMap = gson.fromJson(readFile, typeToken.getType());

		if (noteMap == null) {
			Log.d("DefaultNoteTemp", "File not read");
			Log.d("DefaultNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

			Toast.makeText(context, "ERROR: Note reading failed!", Toast.LENGTH_SHORT).show();

			return null;
		}

		//      Initiate
		_DefaultNote note = new _DefaultNote();

		try {
			//      Read Default
			Map<String, String> map = noteMap.get(Constants.JSON_DEFAULT);

			note.setFileName(fileName);
			note.setTitle(map.get(Constants.JSON_DEFAULT_TITLE));
			note.setDateCreated(longDateFormat.parse(map.get(Constants.JSON_DEFAULT_DATE_CREATED)));
			note.setDateModified(longDateFormat.parse(map.get(Constants.JSON_DEFAULT_DATE_MODIFIED)));
			note.setContent(map.get(Constants.JSON_DEFAULT_CONTENT));
			note.setFavorite(Boolean.parseBoolean(map.get(Constants.JSON_DEFAULT_FAVORITE)));

			return note;
		}
		catch (Exception e)
		{
			Log.d("DefaultNoteTemp", "File not post-read");
			Log.d("DefaultNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

//			Toast.makeText(context, "ERROR: Note post-reading failed!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();

			return null;
		}
	}

	public static boolean DeleteFromStorage(Context context, String fileName)
	{
		PreferenceManager preferenceManager = new PreferenceManager(context);

		String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
		File file = new File(directory, fileName);

		return file.delete();
	}
}
