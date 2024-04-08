package com.example.btl_android.utilities;

public class Constants
{
	//      Preference Key
	public static final String PREFERENCE_KEY = "evaNote";

	//      First Start Up
	public static final String APP_NOT_FIRST_START_UP = "appNotFirstStartUp";

	//      Settings
	public static final String SETTINGS_STORAGE_LOCATION = "storageDirectory";
	public static final String SETTINGS_SORT_TYPE = "sortType";
	public static final String SETTINGS_SORT_IS_REVERSED = "sortIsReversed";
	//          is Editing or Not?
	public static final String SETTINGS_NOTE_DEFAULT_IS_EDITING = "settingsNoteDefaultIsEditing";
	//          Theme
	public static final String SETTINGS_APP_THEME = "settingsAppTheme";
	//          Task Note
	public static final String TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION =
			"taskNoteSettingsDeleteOnCompletion";
	public static final String TASK_NOTE_SETTINGS_DELETE_ON_COMPLETION_AFTER =
			"taskNoteSettingsDeleteOnCompletionAfter";
	public static final String TASK_NOTE_SETTINGS_SORT_TO_BOTTOM_ON_COMPLETION =
			"taskNoteSettingsSortToBottomOnCompletion";

	//      Note Type
	public static final int DEFAULT_NOTE = 0;
	public static final int TASK_NOTE = 1;
	public static final int TODO_NOTE = 2;
	public static final int PRIVATE_NOTE = 3;
	public static final int REMINDER_NOTE = 4;
	public static final int ATTACHABLE_NOTE = 5;

	//      Bundle Key
	public static final String BUNDLE_FILENAME_KEY = "fileName";

	//      JSON String
	//          DEFAULT
	public static final String JSON_DEFAULT = "default";
	public static final String JSON_DEFAULT_TITLE = "title";
	public static final String JSON_DEFAULT_DATE_CREATED = "dateCreated";
	public static final String JSON_DEFAULT_DATE_MODIFIED = "dateModified";
	public static final String JSON_DEFAULT_CONTENT = "content";
	public static final String JSON_DEFAULT_FAVORITE = "favorite";
	//          ATTACHABLE NOTE
	public static final String JSON_ATTACHABLE_NOTE_CONTAINER = "attachableNoteContainer";
	public static final String JSON_ATTACHABLE_NOTE_CONTAINER_TYPE = "attachableNoteContainerType";
	public static final String JSON_ATTACHABLE_NOTE_CONTAINER_LINK = "attachableNoteContainerLink";
	public static final String JSON_ATTACHABLE_NOTE_CONTAINER_NAME = "attachableNoteContainerName";
	public static final String JSON_ATTACHABLE_NOTE_CONTAINER_CONTENT = "attachableNoteContainerContent";
	//          TASK NOTE
	public static final String JSON_TASK_NOTE_REPEATABLE_COUNT = "taskNoteRepeatableCount";
	public static final String JSON_TASK_NOTE_REPEATABLE_TYPE = "taskNoteRepeatableType";
	public static final String JSON_TASK_NOTE_DUE_DATE = "taskNoteDueDate";
	public static final String JSON_TASK_NOTE_HAS_DEADLINE = "taskNoteHasDeadline";
	public static final String JSON_TASK_NOTE_IS_DONE = "taskNoteIsDone";
	public static final String JSON_TASK_NOTE_SUB_TASKS = "taskNoteSubTasks";
	public static final String JSON_TASK_NOTE_SUB_TASKS_TASK_TITLE = "taskNoteSubTasksTaskTitle";
	public static final String JSON_TASK_NOTE_SUB_TASKS_IS_DONE = "taskNoteSubTasksIsDone";
	//			REMINDER NOTE
	public static final String JSON_REMINDER_NOTE_TIME_OF_REMINDER = "timeOfReminder";
	public static final String JSON_REMINDER_NOTE_NUMBER_OF_SNOOZES = "numberOfSnoozes";
	public static final String JSON_REMINDER_NOTE_MINUTES_EACH_SNOOZE = "minutesEachSnooze";
	public static final String JSON_REMINDER_NOTE_REPEAT_OF_SNOOZE = "repeatOfSnooze";
	//          TO DO NOTE
	public static final String JSON_TODO_NOTE_NAME_FILE = "todoNote.txt";

	//      Note Specific Constants
	//          TO DO NOTE
	public static final String TODO_NOTE_KEY= "todoNote";
	public static final String IS_TODO_NOTE_EDITING_KEY = "isEditing";
	public static final String IS_TODO_NOTE_CREATING_KEY = "isCreating";
	//          PRIVATE NOTE
	public static final String CHANNEL_ID = "201";
}