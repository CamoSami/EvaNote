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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskNote extends _DefaultNote
{
	public enum RepeatableType
	{
		Minutes, Hours, Days, Weeks, Months
	}

	//      TODO: Types:
	//          If none both -> Regular Task with no Deadlines
	//          If only dueDate -> repeatableType = None
	//          If only repeatableCount + Type -> dueDate = today
	//          If dueDate and repeatableCount + Type -> repeat after dueDate
	private int repeatableCount;
	private boolean hasDeadline;
	private boolean isDone;
	private RepeatableType repeatableType;
	private Date dueDate;
	private List<TaskNote_SubTask> subTasks;

	public TaskNote(TaskNote taskNote)
	{
		super(taskNote);
	}

	public TaskNote()
	{
		super();
	}

	public TaskNote(String fileName, String title, Date dateCreated, Date dateModified,
	                String content,
	                boolean isFavorite,
	                int repeatableCount, RepeatableType repeatableType, Date dueDate,
	                List<TaskNote_SubTask> subTasks, boolean hasDeadline, boolean isDone)
	{
		super(fileName, title, dateCreated, dateModified, content, isFavorite);

		this.repeatableCount = repeatableCount;
		this.repeatableType = repeatableType;
		this.dueDate = dueDate;
		this.subTasks = subTasks;
		this.hasDeadline = hasDeadline;
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return isDone;
	}

	public void setDone(boolean done)
	{
		isDone = done;
	}

	public boolean isHasDeadline()
	{
		return hasDeadline;
	}

	public void setHasDeadline(boolean hasDeadline)
	{
		this.hasDeadline = hasDeadline;
	}

	public int getRepeatableCount()
	{
		return repeatableCount;
	}

	public void setRepeatableCount(int repeatableCount)
	{
		this.repeatableCount = repeatableCount;
	}

	public RepeatableType getRepeatableType()
	{
		return repeatableType;
	}

	public void setRepeatableType(RepeatableType repeatableType)
	{
		this.repeatableType = repeatableType;
	}

	public Date getDueDate()
	{
		return dueDate;
	}

	public void setDueDate(Date dueDate)
	{
		this.dueDate = dueDate;
	}

	public List<TaskNote_SubTask> getSubTasks()
	{
		return subTasks;
	}

	public void setSubTasks(List<TaskNote_SubTask> subTasks)
	{
		this.subTasks = subTasks;
	}

	public boolean IsRepeatable()
	{
		return this.getRepeatableCount() > 0 && this.getRepeatableType() != null;
	}

	@Override
	public boolean Validate()
	{
		return (super.Validate() || (this.subTasks != null && this.subTasks.size() > 0) && !Objects.equals(this.title, ""));
	}

	@Override
	public boolean WriteToStorage(Context context, boolean isDuplicated)
	{
		//      Initiate
		Map<String, Map<String, String>> noteMap = new HashMap<>();
		SimpleDateFormat longDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss",
				Locale.getDefault()
		);
		SimpleDateFormat dueDateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy, HH:mm",
				Locale.getDefault()
		);

		//      Write Defaults
		Map<String, String> map = new HashMap<>();

		map.put(Constants.JSON_DEFAULT_TITLE, this.getTitle());
		map.put(Constants.JSON_DEFAULT_DATE_CREATED, longDateFormat.format(this.getDateCreated()));
		map.put(Constants.JSON_DEFAULT_DATE_MODIFIED, longDateFormat.format(this.getDateModified()));
		map.put(Constants.JSON_DEFAULT_CONTENT, this.getContent());
		map.put(Constants.JSON_DEFAULT_FAVORITE, String.valueOf(this.isFavorite()));

		map.put(Constants.JSON_TASK_NOTE_DUE_DATE, dueDateFormat.format(this.getDueDate()));
		map.put(Constants.JSON_TASK_NOTE_REPEATABLE_COUNT, String.valueOf(this.getRepeatableCount()));
		map.put(Constants.JSON_TASK_NOTE_REPEATABLE_TYPE, this.getRepeatableType().toString());
		map.put(Constants.JSON_TASK_NOTE_HAS_DEADLINE, this.isHasDeadline() ? "true" : "false");
		map.put(Constants.JSON_TASK_NOTE_IS_DONE, this.isDone() ? "true" : "false");

		noteMap.put(Constants.JSON_DEFAULT, map);

		//      Write Containers
		for (int i = 0; i < this.subTasks.size(); i++)
		{
			map = new HashMap<>();
			TaskNote_SubTask subTask = this.subTasks.get(i);

			map.put(Constants.JSON_TASK_NOTE_SUB_TASKS_TASK_TITLE,
					String.valueOf(subTask.getTaskTitle()));
			map.put(Constants.JSON_TASK_NOTE_SUB_TASKS_IS_DONE,
					subTask.isDone() ? "true" : "false");

			noteMap.put(Constants.JSON_TASK_NOTE_SUB_TASKS + i, map);
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
			this.fileName = "TaskNote" + longDateFormat.format(this.getDateCreated()) + ".txt";

			file = new File(directory, this.fileName);

			try
			{
				while (!file.createNewFile()) {
					this.fileName = "TaskNote" + longDateFormat.format(this.getDateCreated()) + "(" + i++ + ").txt";

					file = new File(directory, this.fileName);
				}
			}
			catch (IOException e)
			{
				Log.d("TaskNoteTemp", "File not created");
				Log.d("TaskNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

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
				Log.d("TaskNoteTemp", "File not created");
				Log.d("TaskNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

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
			Log.d("TaskNoteTemp", "File not written");
			Log.d("TaskNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

			Toast.makeText(context, "Note can not be written, please give adequate permissions!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();

			return false;
		}

		return true;
	}

	public static Date AcceleDate(Date date, int repeatableCount, RepeatableType repeatableType)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		//				Toast.makeText(this, calendar.getTime().toString(), Toast.LENGTH_SHORT).show();

		switch (repeatableType)
		{
			case Minutes:
				calendar.add(
						Calendar.MINUTE,
						repeatableCount
				);
				break;
			case Hours:
				calendar.add(
						Calendar.HOUR_OF_DAY,
						repeatableCount
				);
				break;
			case Days:
				calendar.add(
						Calendar.DATE,
						repeatableCount
				);
				break;
			case Weeks:
				calendar.add(
						Calendar.WEEK_OF_MONTH,
						repeatableCount
				);
				break;
			case Months:
				calendar.add(
						Calendar.MONTH,
						repeatableCount
				);
				break;
		}

		return calendar.getTime();
	}

	public static TaskNote ReadFromStorage(Context context, String fileName)
	{
		//      Initiate
		TypeToken<Map<String, Map<String, String>>> typeToken = new TypeToken<Map<String, Map<String, String>>>()
		{
		};
		SimpleDateFormat longDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());
		SimpleDateFormat dueDateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy, HH:mm",
				Locale.getDefault());

		Gson gson = new Gson();

		PreferenceManager preferenceManager = new PreferenceManager(context);
		String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
		File file = new File(directory, fileName);
		String readFile;

		try
		{
			FileInputStream stream = new FileInputStream(file);

			BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String readFileGradually;

			while ((readFileGradually = br.readLine()) != null)
			{
				sb.append(readFileGradually);
				sb.append('\n');
			}

			readFile = sb.toString();
		}
		catch (IOException e)
		{
			Log.d("TaskNoteTemp", "File not pre-read");
			Log.d("TaskNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

			Toast.makeText(context, "Note can not be read, please give adequate permissions!",
					Toast.LENGTH_SHORT
			).show();

			e.printStackTrace();

			return null;
		}

		Map<String, Map<String, String>> noteMap = gson.fromJson(readFile, typeToken.getType());

		if (noteMap == null)
		{
			Log.d("TaskNoteTemp", "File not read");
			Log.d("TaskNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

			Toast.makeText(context, "ERROR: Note reading failed!", Toast.LENGTH_SHORT).show();

			return null;
		}

		//      Initiate
		TaskNote note = new TaskNote();

		try
		{
			//      Read Default
			Map<String, String> map = noteMap.get(Constants.JSON_DEFAULT);

			note.setFileName(fileName);
			note.setTitle(map.get(Constants.JSON_DEFAULT_TITLE));
			note.setDateCreated(longDateFormat.parse(map.get(Constants.JSON_DEFAULT_DATE_CREATED)));
			note.setDateModified(longDateFormat.parse(map.get(Constants.JSON_DEFAULT_DATE_MODIFIED)));
			note.setContent(map.get(Constants.JSON_DEFAULT_CONTENT));
			note.setFavorite(Boolean.parseBoolean(map.get(Constants.JSON_DEFAULT_FAVORITE)));

			note.setDueDate(dueDateFormat.parse(map.get(Constants.JSON_TASK_NOTE_DUE_DATE)));
			note.setRepeatableCount(Integer.parseInt(map.get(Constants.JSON_TASK_NOTE_REPEATABLE_COUNT)));
			note.setRepeatableType(RepeatableType.valueOf(map.get(Constants.JSON_TASK_NOTE_REPEATABLE_TYPE)));
			note.setHasDeadline(Boolean.parseBoolean(map.get(Constants.JSON_TASK_NOTE_HAS_DEADLINE)));
			note.setDone(Boolean.parseBoolean(map.get(Constants.JSON_TASK_NOTE_IS_DONE)));

			//      Read Container
			List<TaskNote_SubTask> subTasks = new ArrayList<>();
			int i = 0;
			map = noteMap.get(Constants.JSON_TASK_NOTE_SUB_TASKS + i);

			while (map != null)
			{
				TaskNote_SubTask subTask = new TaskNote_SubTask();

				subTask.setTaskTitle(map.get(Constants.JSON_TASK_NOTE_SUB_TASKS_TASK_TITLE));
				subTask.setDone(Boolean.parseBoolean(map.get(Constants.JSON_TASK_NOTE_SUB_TASKS_IS_DONE)));

				subTasks.add(subTask);
				map = noteMap.get(Constants.JSON_TASK_NOTE_SUB_TASKS + ++i);
			}

			note.setSubTasks(subTasks);

			return note;
		}
		catch (Exception e)
		{
			Log.d("TaskNoteTemp", "File not post-read");
			Log.d("TaskNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

			Toast.makeText(context, "ERROR: Note post-reading failed!", Toast.LENGTH_SHORT).show();

			e.printStackTrace();

			return null;
		}
	}
}
