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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttachableNote extends _DefaultNote
{
	private List<AttachableNote_Container> containers;

	public AttachableNote() {
		super();
	}

	public AttachableNote(AttachableNote attachableNote) {
		super(attachableNote);

		this.containers = attachableNote.getContainers();
	}

	public AttachableNote(String fileName, String title, Date dateCreated, Date dateModified,
	                      String content, boolean isFavorite, List<AttachableNote_Container> containers)
	{
		super(fileName, title, dateCreated, dateModified, content, isFavorite);

		this.containers = containers;
	}

	public List<AttachableNote_Container> getContainers()
	{
		return this.containers;
	}

	public void setContainers(List<AttachableNote_Container> containers)
	{
		this.containers = containers;
	}

	@Override
	public boolean Validate()
	{
		return (super.Validate() || (this.containers != null && this.containers.size() > 0));
	}

	@Override
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

		//      Write Containers
		for (int i = 0; i < this.containers.size(); i++)
		{
			map = new HashMap<>();
			AttachableNote_Container container = this.containers.get(i);

			map.put(Constants.JSON_ATTACHABLE_NOTE_CONTAINER_TYPE, String.valueOf(container.getContainerType()));
			map.put(Constants.JSON_ATTACHABLE_NOTE_CONTAINER_LINK, container.getContainerLink());
			map.put(Constants.JSON_ATTACHABLE_NOTE_CONTAINER_NAME, container.getContainerName());
			map.put(Constants.JSON_ATTACHABLE_NOTE_CONTAINER_CONTENT, container.getContainerContent());

			noteMap.put(Constants.JSON_ATTACHABLE_NOTE_CONTAINER + i, map);
		}

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
			this.fileName = "AttachableNote" + longDateFormat.format(this.getDateCreated()) + ".txt";

			file = new File(directory, this.fileName);

			try
			{
				while (!file.createNewFile()) {
					this.fileName = "AttachableNote" + longDateFormat.format(this.getDateCreated()) + "(" + i++ + ").txt";

					file = new File(directory, this.fileName);
				}
			}
			catch (IOException e)
			{
				Log.d("AttachableNoteTemp", "File not created");
				Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

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
//				Log.d("AttachableNoteTemp", "Duplication: " + this.fileName + " -> " + this.fileName.substring(0, this.fileName.lastIndexOf(".") - 3));

				fileName = this.fileName.substring(0, this.fileName.lastIndexOf(".") - 3);
			}
			else
			{
//				Log.d("AttachableNoteTemp", "Duplication: " + this.fileName + " -> " + this.fileName.substring(0, this.fileName.lastIndexOf(".")));

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
				Log.d("AttachableNoteTemp", "File not created");
				Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

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
			Log.d("AttachableNoteTemp", "File not written");
			Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

			Toast.makeText(context, "Note can not be written, please give adequate permissions!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();

			return false;
		}

		return true;
	}

	public static AttachableNote ReadFromStorage(Context context, String fileName)
	{
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
			Log.d("AttachableNoteTemp", "File not pre-read");
			Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

			Toast.makeText(context, "Note can not be read, please give adequate permissions!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();

			return null;
		}

		Map<String, Map<String, String>> noteMap = gson.fromJson(readFile, typeToken.getType());

		if (noteMap == null) {
			Log.d("AttachableNoteTemp", "File not read");
			Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

			Toast.makeText(context, "ERROR: Note reading failed!", Toast.LENGTH_SHORT).show();

			return null;
		}

		//      Initiate
		AttachableNote note = new AttachableNote();

		try {
			//      Read Default
			Map<String, String> map = noteMap.get(Constants.JSON_DEFAULT);

			note.setFileName(fileName);
			note.setTitle(map.get(Constants.JSON_DEFAULT_TITLE));
			note.setDateCreated(longDateFormat.parse(map.get(Constants.JSON_DEFAULT_DATE_CREATED)));
			note.setDateModified(longDateFormat.parse(map.get(Constants.JSON_DEFAULT_DATE_MODIFIED)));
			note.setContent(map.get(Constants.JSON_DEFAULT_CONTENT));
			note.setFavorite(Boolean.parseBoolean(map.get(Constants.JSON_DEFAULT_FAVORITE)));

			//      Read Container
			List<AttachableNote_Container> containers = new ArrayList<>();
			int i = 0;
			map = noteMap.get(Constants.JSON_ATTACHABLE_NOTE_CONTAINER + i);

			while (map != null) {
				AttachableNote_Container container = new AttachableNote_Container();

				container.setContainerType(Integer.parseInt(map.get(Constants.JSON_ATTACHABLE_NOTE_CONTAINER_TYPE)));
				container.setContainerName(map.get(Constants.JSON_ATTACHABLE_NOTE_CONTAINER_NAME));
				container.setContainerLink(map.get(Constants.JSON_ATTACHABLE_NOTE_CONTAINER_LINK));
				container.setContainerContent(map.get(Constants.JSON_ATTACHABLE_NOTE_CONTAINER_CONTENT));

				containers.add(container);
				map = noteMap.get(Constants.JSON_ATTACHABLE_NOTE_CONTAINER + ++i);
			}

			note.setContainers(containers);

			return note;
		}
		catch (Exception e)
		{
			Log.d("AttachableNoteTemp", "File not post-read");
			Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

			Toast.makeText(context, "ERROR: Note post-reading failed!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();

			return null;
		}
	}
}
