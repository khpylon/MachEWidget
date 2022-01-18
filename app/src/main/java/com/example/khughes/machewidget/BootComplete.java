package com.example.khughes.machewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.text.format.DateUtils;

public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            StoredData appInfo = new StoredData(context);
            // On boot, assume we're in an inactive state
//            appInfo.setAppState(SavedData.INACTIVE);

            // Go check the calendars as soon as possible.
            Intent intent1 = new Intent(context, StatusReceiver.class);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.SECOND_IN_MILLIS, pendingIntent);
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.SECOND_IN_MILLIS,
                    DateUtils.SECOND_IN_MILLIS, pendingIntent);
        }
    }

}
