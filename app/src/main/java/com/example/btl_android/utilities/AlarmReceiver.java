package com.example.btl_android.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.example.btl_android.R;
import com.example.btl_android.activities.ReminderNoteActivity;

import java.util.Date;
import java.util.Objects;

public class AlarmReceiver
		extends BroadcastReceiver
{

    @Override public void onReceive(Context context, Intent intent)
    {
        if (Objects.equals(intent.getAction(), "Calender"))
        {
            Bundle b = intent.getBundleExtra("bundle");
            String time = b.getString("time");
            String title = b.getString("title");

            String description = b.getString("description");
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
            notificationIntent.putExtra("bundle", b);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID).setContentTitle(
                            title + time).setContentText(description).setContentIntent(pendingIntent).setSmallIcon(R.drawable.baseline_notifications).setColor(Color.RED)
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
