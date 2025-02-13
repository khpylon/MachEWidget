package com.example.khughes.machewidget

import android.content.BroadcastReceiver
import android.content.Intent
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.Context
import android.text.format.DateUtils
import androidx.preference.PreferenceManager

class BootComplete : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        LogFile.defineContext(context)
        val action = intent.action
        if (action.equals(Intent.ACTION_LOCALE_CHANGED, ignoreCase = true) ) {
            LogFile.d(MainActivity.CHANNEL_ID, "BootComplete received action $action")
            CarStatusWidget.updateWidget(context)
        }

        if (action.equals(Intent.ACTION_BOOT_COMPLETED, ignoreCase = true) ||
            action.equals(Intent.ACTION_MY_PACKAGE_REPLACED, ignoreCase = true)
        ) {
            LogFile.d(MainActivity.CHANNEL_ID, "BootComplete received action $action")

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val delayInMillis = sharedPref.getString(
                context.resources.getString(R.string.update_frequency_key),
                "15"
            )!!.toInt()

            if (!Misc.isHibernating(context) && delayInMillis > 0) {
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
            } else {
                LogFile.d(
                    MainActivity.CHANNEL_ID,
                    "Manual updates enabled, so alarm not set."
                )
            }

            // Check for updates (github version only).
            UpdateReceiver.createIntent(context)

            // Restart any charging reminders that exist
            ReminderReceiver.checkAlarms(context)

            // See if we need to notify the user to read the status update
            Notifications.statusUpdate(context)
        }

        if (action.equals(Intent.ACTION_MY_PACKAGE_REPLACED, ignoreCase = true)) {
            LogFile.d(MainActivity.CHANNEL_ID, "BootComplete: running package updates")

            // If package was replaced, then the "new version" is this version.
            val appInfo = StoredData(context);
            val version = appInfo.latestVersion;
            if (version != null && BuildConfig.VERSION_NAME.compareTo(version) >= 0 ) {
                LogFile.d(MainActivity.CHANNEL_ID, "BootComplete: new app version found")
                appInfo.latestVersion = BuildConfig.VERSION_NAME
            }

            Misc.removeAPK(context)
            AppUpdates.performUpdates(context)
            CarStatusWidget.updateWidget(context)
            ReminderReceiver.checkAlarms(context)
        }

        // Check for updates (github version only).
        UpdateReceiver.createIntent(context)
    }
}
