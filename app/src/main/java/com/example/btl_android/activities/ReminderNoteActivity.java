package com.example.btl_android.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.databinding.ActivityReminderNoteBinding;
import com.example.btl_android.models.ReminderNote;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.AlarmReceiver;
import com.example.btl_android.utilities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderNoteActivity
		extends AppCompatActivity
{
    private ActivityReminderNoteBinding binding;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private boolean isFavorite = false;
    private String fileName = null;
    private Date dateCreated = null;
    private final Calendar alarmTime = Calendar.getInstance();

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //        Log.d("TAG", "onCreate: ");

        this.binding = ActivityReminderNoteBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        this.alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);

        this.SetListeners();
        this.ReadToActivity(bundle);
    }

    private void SetListeners()
    {
        this.binding.btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if (ReminderNoteActivity.this.SaveNote(v))
                {
                    ReminderNoteActivity.this.SetAlarmListener();

                    ReminderNoteActivity.this.finish();
                }
                else
                {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(ReminderNoteActivity.this);

                    builder.setTitle("Leave Confirmation");
                    builder.setMessage("Note can not be saved, do you still want to leave?");

                    builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                            ReminderNoteActivity.this.ResetReminder();

                            finish();
                        }
                    });

                    builder.setNegativeButton("Nah", null);

                    builder.create().show();
                }
            }
        });

        this.binding.btnReset.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                try
                {
                    ReminderNoteActivity.this.ResetReminder();
                }
                catch (Exception e)
                {
                    Toast.makeText(ReminderNoteActivity.this, "Calendar reset failed!",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        this.binding.btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                ReminderNoteActivity.this.onBackPressed();
            }
        });

        this.binding.swRepeat.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        Log.d("IsChecker: ", String.valueOf(isChecked));

                        ReminderNoteActivity.this.binding.swRepeat.setChecked(isChecked);
                        ReminderNoteActivity.this.binding.txbSnooze.setEnabled(isChecked);

                        if (isChecked)
                        {
                            //                            txbSnooze.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                            ReminderNoteActivity.this.binding.txbSnooze.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                        }
                        else
                        {
                            ReminderNoteActivity.this.binding.txbSnooze.setBackgroundColor(
                                    getResources().getColor(android.R.color.darker_gray));
                        }
                    }
                });
    }

    @Override public void onBackPressed()
    {
        View view = new View(this);

        if (this.SaveNote(view))
        {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(ReminderNoteActivity.this);

            builder.setTitle("Alarm not active");
            builder.setMessage(
                    "Note is saved but alarm is not up, do you still want to " + "leave?");

            builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    ReminderNoteActivity.this.ResetReminder();

                    ReminderNoteActivity.super.onBackPressed();
                }
            });

            builder.setNegativeButton("Nah", null);

            builder.create().show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Leave Confirmation");
            builder.setMessage("Note can not be saved, do you still want to leave?");

            builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    ReminderNoteActivity.super.onBackPressed();
                }
            });

            builder.setNegativeButton("Nah", null);

            builder.create().show();
        }
    }

    private void ReadToActivity(Bundle bundle)
    {
        if (bundle == null)
        {
            //      If New
            alarmTime.set(Calendar.YEAR, 0);
            alarmTime.set(Calendar.MONTH, 0);
            alarmTime.set(Calendar.DAY_OF_MONTH, 0);
            alarmTime.set(Calendar.HOUR_OF_DAY, 0);
            alarmTime.set(Calendar.MINUTE, 0);
            alarmTime.set(Calendar.SECOND, 0);
        }
        else
        {
            //      If Edit
            String fileName = bundle.getString(Constants.BUNDLE_FILENAME_KEY);

            Log.d("AlarmReceiver", "FileName: " + fileName);

            String dateFormat = "dd/MM/yyyy";
            String timeFormat = "HH:mm";

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());

            ReminderNote reminderNote = ReminderNote.ReadFromStorage(this, fileName);

            this.fileName = fileName;
            this.binding.txbTitle.setText(
                    reminderNote.getTitle() == null ? "" : reminderNote.getTitle());
            this.binding.txbContent.setText(reminderNote.getContent());
            this.isFavorite = reminderNote.isFavorite();
            this.dateCreated = reminderNote.getDateCreated();

            this.binding.txbSnooze.setText(reminderNote.getMinutesEachSnooze() + "");

            Date tempDate = reminderNote.getDateOfReminder();

            this.binding.btnDate.setText(simpleDateFormat.format(tempDate) + "");
            this.binding.btnTime.setText(simpleTimeFormat.format(tempDate) + "");
            this.binding.swRepeat.setChecked(reminderNote.getRepeatOfSnooze());
        }
    }

    private boolean Check(int minute, int hour, int day, int month, int year)
    {
        if (alarmTime.get(Calendar.YEAR) < year)
        {
            return false;
        }
        else if (alarmTime.get(Calendar.MONTH) < month)
        {
            return false;
        }
        else if (alarmTime.get(Calendar.DATE) < day)
        {
            return false;
        }
        else if (alarmTime.get(Calendar.DATE) == day && alarmTime.get(Calendar.MONTH) == month &&
                alarmTime.get(Calendar.YEAR) == year)
        {
            if (alarmTime.get(Calendar.HOUR) < hour)
            {
                alarmTime.set(Calendar.DATE, alarmTime.get(Calendar.DATE) + 1);
                return true;
            }
            else if (alarmTime.get(Calendar.MINUTE) < minute)
            {
                alarmTime.set(Calendar.HOUR, alarmTime.get(Calendar.HOUR) + 1);
                return true;
            }
        }
        return true;
    }

    public void btnDay_click(View view)
    {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        alarmTime.set(Calendar.DATE, day);
        alarmTime.set(Calendar.MONTH, month);
        alarmTime.set(Calendar.YEAR, year);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
                    @SuppressLint("DefaultLocale") @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        alarmTime.set(Calendar.DATE, dayOfMonth);
                        alarmTime.set(Calendar.MONTH, month);
                        alarmTime.set(Calendar.YEAR, year);

                        ReminderNoteActivity.this.binding.btnDate.setText(
                                String.format("%d/%d/%d", dayOfMonth, month + 1, year));

                        //                        Toast.makeText(ReminderNoteActivity.this,
                        //                                String.format("Selected day %d/%d/%d", dayOfMonth, month, year),
                        //                                Toast.LENGTH_SHORT).show();
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    public void btnTime_click(View view)
    {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
                    @SuppressLint("DefaultLocale") @Override
                    public void onTimeSet(TimePicker view, int hour, int minute)
                    {
                        alarmTime.set(Calendar.MINUTE, minute);
                        alarmTime.set(Calendar.HOUR, hour);

                        ReminderNoteActivity.this.binding.btnTime.setText(
                                String.format("%d:%d", hour, minute));

                        //                        Toast.makeText(ReminderNoteActivity.this,
                        //                                String.format("Selected time %d:%d", hour, minute),
                        //                                Toast.LENGTH_SHORT).show();
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void SetAlarmListener()
    {
        Calendar calendar = Calendar.getInstance();
        alarmTime.set(Calendar.SECOND, 0);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        String title = this.binding.txbTitle.getText().toString();
        Log.d("title: ", title);

        String description = this.binding.txbContent.getText().toString();

        if (!Check(minute, hour, day, month, year))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification").setMessage("Please set time!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        String sSnooze = this.binding.txbSnooze.getText().toString();
        int snooze = sSnooze.equals("") ? 1 : (Integer.parseInt(sSnooze) <= 0 ? 1 : Integer.parseInt(sSnooze));

        Intent intent = new Intent(this, AlarmReceiver.class);

        //        Log.d("IsChecker: ", String.valueOf(this.binding.swRepeat.isChecked()));

        intent.setAction(Constants.REMINDER_NOTE_KEY);
        intent.putExtra(Constants.BUNDLE_FILENAME_KEY, this.fileName);

        Log.d("filename: ", this.fileName);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        //        Log.d("alarmTime: ", alarmTime.getTime() + "");
        //        Log.d("isChecked(): ", this.binding.swRepeat.isChecked() + "");


        if (this.binding.swRepeat.isChecked())
        {
            //            Log.d("alarmTime: ", "alarmTime.getTimeInMillis()");

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                    snooze * 1000L, pendingIntent
            );
        }
        else
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(ReminderNoteActivity.this, "Set calender successfully!", Toast.LENGTH_SHORT).show();
    }

    private void ResetReminder()
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            {
                alarmManager.cancelAll();
            }
            else
            {
                Intent intent = new Intent(this, AlarmReceiver.class);

                intent.setAction(Constants.REMINDER_NOTE_KEY);
                intent.putExtra(Constants.BUNDLE_FILENAME_KEY, this.fileName);

                PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
                        PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT
                );

                alarmManager.cancel(sender);
            }

            Toast.makeText(ReminderNoteActivity.this, "Calendar reset!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            Toast.makeText(ReminderNoteActivity.this, "Calendar reset failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean SaveNote(View view)
    {
        view.requestFocus();

        String dateFormat = "dd/MM/yyyy";
        String timeFormat = "HH:mm";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());

        if (this.dateCreated == null)
        {
            this.dateCreated = Calendar.getInstance().getTime();
        }

        if (this.binding.btnTime.getText().toString().equals("Pick Time") &&
                this.binding.btnDate.getText().toString().equals("Pick Date"))
        {
            Toast.makeText(ReminderNoteActivity.this, "Please select time", Toast.LENGTH_SHORT).show();

            return false;
        }

        //      Set Date Time Calendar
        Calendar calendarForAlarm = Calendar.getInstance();

        if (!this.binding.btnTime.getText().toString().equals("Pick Time"))
        {
            try
            {
                Date dateTime = simpleTimeFormat.parse(this.binding.btnTime.getText().toString());

                Calendar calendarTime = Calendar.getInstance();
                calendarTime.setTime(dateTime);

                calendarForAlarm.set(Calendar.HOUR, calendarTime.get(Calendar.HOUR));
                calendarForAlarm.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
            }
            catch (ParseException e)
            {
                Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());
            }
        }

        if (!this.binding.btnDate.getText().toString().equals("Pick Date"))
        {
            try
            {
                Date dateDate = simpleDateFormat.parse(this.binding.btnDate.getText().toString());

                Calendar calendarDate = Calendar.getInstance();
                calendarDate.setTime(dateDate);

                calendarForAlarm.set(Calendar.DATE, calendarDate.get(Calendar.DATE));
                calendarForAlarm.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH));
                calendarForAlarm.set(Calendar.YEAR, calendarDate.get(Calendar.YEAR));
            }
            catch (ParseException e)
            {
                Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());
            }
        }

        ReminderNote reminderNote =
                new ReminderNote(this.fileName, this.binding.txbTitle.getText().toString(), this.dateCreated,
                        Calendar.getInstance().getTime(), this.binding.txbContent.getText().toString(),
                        this.isFavorite, calendarForAlarm.getTime(),
                        this.binding.txbSnooze.getText().toString().length() > 0 ?
                                Integer.parseInt(this.binding.txbSnooze.getText().toString()) : 0,
                        this.binding.swRepeat.isChecked()
                );

        this.fileName = reminderNote.getFileName();

        //      Not validated, just simply want to leave
        if (!reminderNote.Validate())
        {
            //			this.finish();

            return false;
        }

        if (reminderNote.WriteToStorage(this, false))
        {
            this.fileName = reminderNote.getFileName();

            return true;
        }
        else
        {
            return false;
        }
    }
}