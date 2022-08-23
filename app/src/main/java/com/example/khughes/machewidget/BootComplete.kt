package com.example.khughes.machewidget

import android.content.BroadcastReceiver
import android.content.Intent
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.Context
import android.text.format.DateUtils

class BootComplete : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action.equals(Intent.ACTION_BOOT_COMPLETED, ignoreCase = true) ||
            action.equals(Intent.ACTION_MY_PACKAGE_REPLACED, ignoreCase = true)
        ) {
            LogFile.d(context, MainActivity.CHANNEL_ID, "BootComplete received action $action")

            // Check the car's status as soon as possible.
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context, 0,
                    Intent(context, StatusReceiver::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.SECOND_IN_MILLIS] =
                pendingIntent
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + DateUtils.SECOND_IN_MILLIS * 15,
                DateUtils.SECOND_IN_MILLIS,
                pendingIntent
            )

            // Check for updates (github version only).
            UpdateReceiver.createIntent(context)
        }

        if (action.equals(Intent.ACTION_MY_PACKAGE_REPLACED, ignoreCase = true)) {
            LogFile.d(context, MainActivity.CHANNEL_ID, "BootComplete: running package updates")
            Misc.removeAPK(context)
            AppUpdates.performUpdates(context)
            CarStatusWidget.updateWidget(context)
        }
    }
}