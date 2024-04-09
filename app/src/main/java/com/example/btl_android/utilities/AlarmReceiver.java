package com.example.btl_android.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.btl_android.R;
import com.example.btl_android.activities.ReminderNoteActivity;
import com.example.btl_android.models.ReminderNote;

import java.util.Date;
import java.util.Objects;

public class AlarmReceiver
        extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
//        Log.d("oke", "Đây là Alarm");
        if (Objects.equals(intent.getAction(), "Calender"))
        {
            Bundle bundle = intent.getExtras();
            String fileName = bundle.getString(Constants.BUNDLE_FILENAME_KEY);

            ReminderNote reminderNote = ReminderNote.ReadFromStorage(context, fileName);
            assert reminderNote != null;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, "Chanel",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("DES");
                notificationManager.createNotificationChannel(channel);
            }

            Intent notificationIntent = new Intent(context, ReminderNoteActivity.class);

            notificationIntent.setAction("Notification Reminder");
            notificationIntent.putExtra(Constants.BUNDLE_FILENAME_KEY, fileName);

            Log.d("AlarmReceiver", "FileName: " + fileName);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_MUTABLE
            );

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                    .setContentTitle(reminderNote.getTitle() + ", " + reminderNote.getDateOfReminder())
                    .setContentText(reminderNote.getContent())
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.baseline_notifications)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);

            notificationManager.notify(getNotificationid(), builder.build());
        }
    }

    private int getNotificationid()
    {
        int time = (int) new Date().getTime();
        return time;
    }
}
