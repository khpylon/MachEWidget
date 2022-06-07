package com.example.khughes.machewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) ||
                action.equalsIgnoreCase(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            LogFile.d(context, MainActivity.CHANNEL_ID, "BootComplete received action " + action);

            // Check the car's status as soon as possible.
            intent = new Intent(context, StatusReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.SECOND_IN_MILLIS, pendingIntent);
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.SECOND_IN_MILLIS * 15,
                    DateUtils.SECOND_IN_MILLIS, pendingIntent);

            // Check for updates (github version only).
            UpdateReceiver.createIntent(context);
        }
        if (action.equalsIgnoreCase(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            LogFile.d(context, MainActivity.CHANNEL_ID, "BootComplete: running package updates");
            Utils.removeAPK(context);
            MainActivity.performUpdates(context);
            MainActivity.updateWidget(context);
        }
    }
}
