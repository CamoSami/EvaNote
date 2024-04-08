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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReminderNote
		extends _DefaultNote
{


    //      Tuwj xoas

    private Date timeOfReminder;
    private int numberOfSnoozes;
    private int minutesEachSnooze;
    private Boolean repeatOfSnooze;

    public ReminderNote()
    {
        super();
    }

    public ReminderNote(String fileName, String title, Date dateCreated, Date dateModified, String content,
                        boolean isFavorite, Date time, int number, int snooze, boolean repeat
    )
    {
        super(fileName, title, dateCreated, dateModified, content, isFavorite);

        this.timeOfReminder = time;
        this.numberOfSnoozes = number;
        this.minutesEachSnooze = snooze;
        this.repeatOfSnooze = repeat;
    }

    public Date getTimeOfReminder()
    {
        return timeOfReminder;
    }

    public void setTimeOfReminder(Date timeOfReminder)
    {
        this.timeOfReminder = timeOfReminder;
    }

    public int getNumberOfSnoozes()
    {
        return numberOfSnoozes;
    }

    public void setNumberOfSnoozes(int numberOfSnoozes)
    {
        this.numberOfSnoozes = numberOfSnoozes;
    }

    public int getMinutesEachSnooze()
    {
        return minutesEachSnooze;
    }

    public void setMinutesEachSnooze(int minutesEachSnooze)
    {
        this.minutesEachSnooze = minutesEachSnooze;
    }

    public boolean getRepeatOfSnooze()
    {
        return repeatOfSnooze;
    }

    public void setRepeatOfSnooze(boolean repeatOfSnooze)
    {
        this.repeatOfSnooze = repeatOfSnooze;
    }


    @Override public boolean Validate()
    {
        return (super.Validate());
    }

    @Override public boolean WriteToStorage(Context context, boolean isDuplicated)
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

        map.put(Constants.JSON_REMINDER_NOTE_TIME_OF_REMINDER,
                longDateFormat.format(this.getTimeOfReminder())
        );
        map.put(Constants.JSON_REMINDER_NOTE_REPEAT_OF_SNOOZE,
                String.valueOf(this.getRepeatOfSnooze())
        );
        map.put(Constants.JSON_REMINDER_NOTE_NUMBER_OF_SNOOZES,
                String.valueOf(this.getNumberOfSnoozes())
        );
        map.put(Constants.JSON_REMINDER_NOTE_MINUTES_EACH_SNOOZE,
                String.valueOf(this.getMinutesEachSnooze())
        );

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
            this.fileName = "ReminderNote" + longDateFormat.format(this.getDateCreated()) + ".txt";

            file = new File(directory, this.fileName);

            try
            {
                while (!file.createNewFile())
                {
                    this.fileName =
                            "ReminderNote" + longDateFormat.format(this.getDateCreated()) + "(" + i++ + ")" +
                                    ".txt";

                    file = new File(directory, this.fileName);
                }
            }
            catch (IOException e)
            {
                Log.d("ReminderNoteTemp", "File not created");
                Log.d("ReminderNoteTemp",
                        "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName
                );

                Toast.makeText(context, "Note can not be created, please give adequate permissions!",
                        Toast.LENGTH_SHORT
                ).show();

                e.printStackTrace();

                return false;
            }
        }
        else if (isDuplicated)
        {
            //      Duplication!
            String regex = "\\([0-9]+\\)\\.txt";
            String fileName = this.fileName.substring(this.fileName.lastIndexOf(".") - 3,
                    this.fileName.length()
            );

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

                fileName = this.fileName.substring(0, this.fileName.lastIndexOf("."));
            }

            //      Create a New File with Similar Name
            file = new File(directory, this.fileName = fileName + "(" + i++ + ").txt");
            try
            {
                while (!file.createNewFile())
                {
                    this.fileName = fileName + "(" + i++ + ").txt";

                    file = new File(directory, this.fileName);
                }
            }
            catch (IOException e)
            {
                Log.d("ReminderNoteTemp", "File not created");
                Log.d("ReminderNoteTemp",
                        "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName
                );

                Toast.makeText(context, "Note can not be created, please give adequate permissions!",
                        Toast.LENGTH_SHORT
                ).show();

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
            Log.d("ReminderNoteTemp", "File not written");
            Log.d("ReminderNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

            Toast.makeText(context, "Note can not be written, please give adequate permissions!",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static ReminderNote ReadFromStorage(Context context, String fileName)
    {
        //      Initiate
        TypeToken<Map<String, Map<String, String>>> typeToken = new TypeToken<Map<String, Map<String, String>>>()
        {
        };
        SimpleDateFormat longDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());
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
            Log.d("ReminderNoteTemp", "File not pre-read");
            Log.d("ReminderNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

            Toast.makeText(context, "Note can not be read, please give adequate permissions!",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

            return null;
        }

        Map<String, Map<String, String>> noteMap = gson.fromJson(readFile, typeToken.getType());

        if (noteMap == null)
        {
            Log.d("ReminderNoteTemp", "File not read");
            Log.d("ReminderNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

            Toast.makeText(context, "ERROR: Note reading failed!", Toast.LENGTH_SHORT).show();

            return null;
        }

        //      Initiate
        ReminderNote note = new ReminderNote();

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

            note.setTimeOfReminder(longDateFormat.parse(map.get(Constants.JSON_REMINDER_NOTE_TIME_OF_REMINDER)));
            note.setRepeatOfSnooze(Boolean.parseBoolean(map.get(Constants.JSON_REMINDER_NOTE_REPEAT_OF_SNOOZE)));
            note.setNumberOfSnoozes(Integer.parseInt(map.get(Constants.JSON_REMINDER_NOTE_NUMBER_OF_SNOOZES)));
            note.setMinutesEachSnooze(Integer.parseInt(map.get(Constants.JSON_REMINDER_NOTE_MINUTES_EACH_SNOOZE)));

            return note;
        }
        catch (Exception e)
        {
            Log.d("ReminderNoteTemp", "File not post-read");
            Log.d("ReminderNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

            Toast.makeText(context, "ERROR: Note post-reading failed!", Toast.LENGTH_SHORT).show();

            e.printStackTrace();

            return null;
        }
    }
}
