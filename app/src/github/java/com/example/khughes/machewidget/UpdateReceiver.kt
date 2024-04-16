package com.example.khughes.machewidget

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.format.DateUtils
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.khughes.machewidget.LogFile.e
import com.example.khughes.machewidget.LogFile.i
import com.example.khughes.machewidget.Notifications.Companion.NORMAL_NOTIFICATIONS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        nextAlarm(context)

        CoroutineScope(Dispatchers.IO).launch {

            // Try to read version info for current release
            val apiUrl =
                "https://raw.githubusercontent.com/khpylon/MachEWidget/master/app/VERSION.txt"
            val client = OkHttpClient.Builder().build()
            val request: Request = Request.Builder().url(apiUrl).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {

                // Get last version that was seen
                val appInfo = StoredData(context)
                val latestVersion = appInfo.latestVersion as String

                // Convert body to a string
                val responseBody = response.body
                val result = responseBody?.bytes()?.decodeToString() as String

                // Look for the line containing version info
                val version = "Version: "
                for (item in result.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()) {
                    if (item.contains(version)) {
                        val newVersion = item.replace(version, "")
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "UpdateReceiver.onPostExecute(): newest version is $newVersion"
                        )

                        // If version is newer than this version and last seen version, we
                        // have a new version
                        if (newVersion > BuildConfig.VERSION_NAME &&
                            newVersion > latestVersion
                        ) {
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                "UpdateReceiver.onPostExecute(): launching notification"
                            )

                            // Save new version for UpdateActivity
                            appInfo.latestVersion = newVersion

                            // Display a notification
                            newApp(context)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val APP_NOTIFICATION = 938

        // Display notification for new update, and use it to
        private fun newApp(context: Context?) {
            val intent = Intent(context, UpdateActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(
                context!!, NORMAL_NOTIFICATIONS
            )
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                .setContentTitle(context.getString(R.string.update_receiver_notification_title))
                .setContentText(context.getString(R.string.update_receiver_notification_message))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(APP_NOTIFICATION, builder.build())
            }
        }

        private fun getIntent(context: Context): Intent {
            return Intent(context, UpdateReceiver::class.java).setAction("UpdateReceiver")
        }

        // Set an alarm in one hour.
        fun nextAlarm(context: Context) {
            val time = LocalDateTime.now(ZoneId.systemDefault()).plusHours(1)
            val timeText = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss", Locale.US))
            i(context, MainActivity.CHANNEL_ID, "UpdateReceiver: next update alarm at $timeText")
            val nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val pendingIntent = PendingIntent.getBroadcast(
                context, 0,
                getIntent(context), PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.RTC_WAKEUP, nextTime] = pendingIntent
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                nextTime,
                DateUtils.MINUTE_IN_MILLIS,
                pendingIntent
            )
        }

        // If no alarm is pending, start one
        fun initiateAlarm(context: Context) {
            if (PendingIntent.getBroadcast(
                    context, 0,
                    getIntent(context), PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                ) == null
            ) {
                nextAlarm(context)
            }
        }

        // Check for a new version of the app sometime soon
        fun createIntent(context: Context) {
            val intent = Intent(context, UpdateReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.HOUR_IN_MILLIS] =
                pendingIntent
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.MINUTE_IN_MILLIS,
                DateUtils.MINUTE_IN_MILLIS, pendingIntent
            )
        }
    }
}
