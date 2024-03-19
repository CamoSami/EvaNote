package com.example.btl_android.utilities;

public class Constants
{
	//      Preferences
	public static final String PREFERENCE_KEY = "evaNote";
	public static final String PREFERENCE_STORAGE_DIRECTORY = "storageDirectory";

	//      Note Type
	public static final int DEFAULT_NOTE = 0;
	public static final int CHECKLIST_NOTE = 1;
	public static final int TODO_NOTE = 2;
	public static final int PRIVATE_NOTE = 3;
	public static final int REMINDER_NOTE = 4;
	public static final int ATTACHABLE_NOTE = 5;

	//      Bundle Key
	public static final String BUNDLE_FILENAME_KEY = "fileName";

	//      JSON String
	//          DEFAULT
	public static final String JSON_DEFAULT = "default";
	public static final String JSON_DEFAULT_TYPE = "type";
	public static final String JSON_DEFAULT_TITLE = "title";
	public static final String JSON_DEFAULT_DATE = "date";
	public static final String JSON_DEFAULT_CONTENT = "content";
	public static final String JSON_DEFAULT_FAVORITE = "favorite";
	//          ATTACHABLE NOTE
	public static final String JSON_ATTACHABLE_NOTE = "container";
	public static final String JSON_ATTACHABLE_NOTE_TYPE = "containerType";
	public static final String JSON_ATTACHABLE_NOTE_LINK = "containerLink";
	public static final String JSON_ATTACHABLE_NOTE_NAME = "containerName";
	public static final String JSON_ATTACHABLE_NOTE_CONTENT = "containerContent";
}