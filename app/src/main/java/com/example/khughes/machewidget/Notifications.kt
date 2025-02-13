package com.example.khughes.machewidget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.CarStatus.CarStatus
import com.example.khughes.machewidget.Misc.Companion.ignoringBatteryOptimizations
import java.text.MessageFormat
import java.time.LocalDateTime
import java.time.ZoneId

class Notifications : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            LVB_NOTIFICATION -> LVBNotificationVisible = false
            TPMS_NOTIFICATION -> TPMSNotificationVisible = false
            CHARGE_NOTIFICATION -> ChargeNotificationVisible = false
        }
    }

    companion object {
        val NORMAL_NOTIFICATIONS = "NORMAL_NOTIFICATIONS"
        val IMPORTANT_NOTIFICATIONS = "IMPORTANT_NOTIFICATIONS"
        val CHARGE_NOTIFICATIONS = "CHARGER_NOTIFICATIONS"

        @JvmStatic
        fun removeNotificationChannels(context: Context) {
            var notificationManager: NotificationManager = getSystemService<NotificationManager>(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            val channels = notificationManager.notificationChannels
            for (channel in channels) {
                notificationManager.deleteNotificationChannel(channel.id)
            }

        }

        @JvmStatic
        fun createNotificationChannels(context: Context) {

            val notificationManager: NotificationManager = getSystemService<NotificationManager>(context,
                NotificationManager::class.java
            ) as NotificationManager

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            var name: CharSequence = "Informational" // getString(R.string.channel_name);
            var importance = NotificationManager.IMPORTANCE_DEFAULT
            var channel = NotificationChannel(NORMAL_NOTIFICATIONS, name, importance)
            channel.description = "General information from the app" // getString(R.string.channel_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel)

            name = "Important"
            importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(IMPORTANT_NOTIFICATIONS, name, importance)
            channel.description = "Alerts for unusual situations"
            notificationManager.createNotificationChannel(channel)

            name = "Charging"
            importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(CHARGE_NOTIFICATIONS, name, importance)
            channel.description = "Reminder to plug in for charging"
            notificationManager.createNotificationChannel(channel)
        }


        //    private static final int OTA_NOTIFICATION = 935;
        //
        //    public static void newOTA(Context context, String message) {
        //        Intent intent = new Intent(context, OTAViewActivity.class);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        //        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NORMAL_NOTIFICATIONS)
        //                .setSmallIcon(R.drawable.notification_icon)
        //                .setContentTitle("OTA information")
        //                .setContentText(message)
        //                .setContentIntent(pendingIntent)
        //                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        //                .setAutoCancel(true);
        //        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //        // notificationId is a unique int for each notification that you must define
        //        notificationManager.notify(OTA_NOTIFICATION, builder.build());
        //    }
        private const val LVB_STATUS = 936
        private const val LVB_NOTIFICATION = BuildConfig.APPLICATION_ID + ".Notifications.LVB"
        private var LVBNotificationVisible = false

        @JvmStatic
        fun checkLVBStatus(context: Context, carStatus: CarStatus, vehInfo: VehicleInfo) {
            val lastLVBStatus = vehInfo.lastLVBStatus
            val currentLVBStatus =
                carStatus.vehiclestatus.battery?.batteryHealth?.value ?: lastLVBStatus
            if (currentLVBStatus != lastLVBStatus) {
                // Save the current status
                vehInfo.lastLVBStatus = currentLVBStatus

                // If the current status is bad and we haven't already posted the notification, then post it
                if (currentLVBStatus != "STATUS_GOOD" && !LVBNotificationVisible) {
                    val intent = Intent(context, Notifications::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.action = LVB_NOTIFICATION
                    val pendingIntent =
                        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    val builder = NotificationCompat.Builder(context, IMPORTANT_NOTIFICATIONS)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                        .setContentTitle(context.getString(R.string.lvb_status_notification_title))
                        .setContentText(context.getString(R.string.lbv_status_notification_description))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                    val notificationManager = NotificationManagerCompat.from(context)
                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(LVB_STATUS, builder.build())
                    LVBNotificationVisible = true
                } else {
                    LVBNotificationVisible = false
                }
            }
        }

        private const val TPMS_STATUS = 937
        private const val TPMS_NOTIFICATION = BuildConfig.APPLICATION_ID + ".Notifications.TPMS"
        private var TPMSNotificationVisible = false

        @JvmStatic
        fun checkTPMSStatus(context: Context, carStatus: CarStatus, vehInfo: VehicleInfo) {
            val lastTPMSStatus = vehInfo.lastTPMSStatus
            val currentTPMSStatus: MutableMap<String, String> = HashMap()
            val leftFrontTire = context.getString(R.string.tpms_left_front_tire)
            val rightFrontTire = context.getString(R.string.tpms_right_front_tire)
            val leftRearTire = context.getString(R.string.tpms_left_rear_tire)
            val rightRearTire = context.getString(R.string.tpms_right_rear_tire)
            currentTPMSStatus[leftFrontTire] =
                carStatus.vehiclestatus.tpms?.leftFrontTireStatus?.value ?: "Normal"
            currentTPMSStatus[rightFrontTire] =
                carStatus.vehiclestatus.tpms?.rightFrontTireStatus?.value ?: "Normal"
            currentTPMSStatus[leftRearTire] =
                carStatus.vehiclestatus.tpms?.outerLeftRearTireStatus?.value ?: "Normal"
            currentTPMSStatus[rightRearTire] =
                carStatus.vehiclestatus.tpms?.outerRightRearTireStatus?.value ?: "Normal"
            var badTire = ""
            for (key in arrayOf(
                leftFrontTire,
                rightFrontTire,
                leftRearTire,
                rightRearTire
            )) {
                val tire = currentTPMSStatus[key]
                if (tire != "Normal") {
                    badTire = if (badTire == "") {
                        key
                    } else {
                        context.getString(R.string.tpms_multiple_tires)
                    }
                }
            }
            if (lastTPMSStatus != null && lastTPMSStatus != badTire) {
                // Save the current status
                vehInfo.lastTPMSStatus = badTire

                // If the current status is bad and we haven't already posted the notification, then post it
                if (badTire != "" && !TPMSNotificationVisible) {
                    val intent = Intent(context, Notifications::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.action = TPMS_NOTIFICATION
                    val pendingIntent =
                        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    val builder = NotificationCompat.Builder(context, IMPORTANT_NOTIFICATIONS)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                        .setContentTitle(context.getString(R.string.tpms_status_notification_title))
                        .setContentText(MessageFormat.format(context.getString(R.string.tpms_status_notification_format), badTire))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                    val notificationManager = NotificationManagerCompat.from(context)
                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(TPMS_STATUS, builder.build())
                    TPMSNotificationVisible = true
                } else {
                    TPMSNotificationVisible = false
                }
            }
        }

        private const val CHARGE_STATUS = 938
        private const val CHARGE_NOTIFICATION = BuildConfig.APPLICATION_ID + ".Notifications.Charge"
        private var ChargeNotificationVisible = false

        @JvmStatic
        fun chargeComplete(context: Context) {
            if (!ChargeNotificationVisible) {
                val intent = Intent(context, Notifications::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.action = LVB_NOTIFICATION
                val pendingIntent =
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                val builder = NotificationCompat.Builder(context, NORMAL_NOTIFICATIONS)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                    .setContentTitle(context.getString(R.string.charge_status_notification_title))
                    .setContentText(context.getString(R.string.charge_status_notification_message))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                val notificationManager = NotificationManagerCompat.from(context)
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(CHARGE_STATUS, builder.build())
                ChargeNotificationVisible = true
            }
        }

        private const val BATTERY_OPTIMIZATION = 939

        @JvmStatic
        fun batteryOptimization(context: Context) {
            val appInfo = StoredData(context)
            val batteryNotification = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.batteryNotification_key), true)
            if (batteryNotification && !ignoringBatteryOptimizations(context)) {
                val lastBatteryNotification = appInfo.batteryNotification
                val time = LocalDateTime.now(ZoneId.systemDefault())
                var nowTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                if (nowTime > lastBatteryNotification) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                    val pendingIntent =
                        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    val builder = NotificationCompat.Builder(context, NORMAL_NOTIFICATIONS)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                        .setContentTitle(context.getString(R.string.battery_optimization_notification_title))
                        .setContentText(context.getString(R.string.battery_optimization_notification_description))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                    val notificationManager = NotificationManagerCompat.from(context)
                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(BATTERY_OPTIMIZATION, builder.build())

                    // If this was the first notification, notify again in one more day
                    if (lastBatteryNotification == 0L) {
                        nowTime += (1000 * 60 * 60 * 24).toLong()
                    } else {
                        nowTime = Long.MAX_VALUE
                    }
                    appInfo.batteryNotification = nowTime
                }
            } else {
                appInfo.batteryNotification = 0
            }
        }

        private const val CHARGE_REMINDER = 940
        fun chargeReminder(context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            val builder = NotificationCompat.Builder(context, CHARGE_NOTIFICATIONS)
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                .setContentTitle(context.getString(R.string.charge_reminder_notification_title))
                .setContentText(context.getString(R.string.charge_reminder_notification_description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(CHARGE_REMINDER, builder.build())
        }

        private const val LOGIN_STATUS = 941
        fun loginRequired(context: Context?) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(
                context!!, IMPORTANT_NOTIFICATIONS
            )
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                .setContentTitle(context.getString(R.string.login_issue_notification_title))
                .setContentText(context.getString(R.string.login_issue_notification_description))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(context)
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(LOGIN_STATUS, builder.build())
        }

        private const val SURVEY_STATUS = 942
        fun surveyPrompt(context: Context) {
            val surveyVersion_key = context.resources.getString(R.string.surveyVersion_key)
            val currentSurveyVersion =
                PreferenceManager.getDefaultSharedPreferences(context).getInt(surveyVersion_key, 0)
            if (currentSurveyVersion < Constants.SURVEY_VERSION) {
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putInt(surveyVersion_key, Constants.SURVEY_VERSION).apply()
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                val builder = NotificationCompat.Builder(context, NORMAL_NOTIFICATIONS)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                    .setContentTitle(context.getString(R.string.survey_notification_title))
                    .setContentText(context.getString(R.string.survey_notification_description))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                val notificationManager = NotificationManagerCompat.from(context)
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(SURVEY_STATUS, builder.build())
            }
        }

        private const val ACCOUNT_ERROR = 943
        fun accountError(context: Context, state: String) {
            if (state === Constants.STATE_ACCOUNT_DISABLED) {
                val builder = NotificationCompat.Builder(context, IMPORTANT_NOTIFICATIONS)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                    .setContentTitle(context.getString(R.string.account_disabled_notification_title))
                    .setContentText(context.getString(R.string.account_disabled_notification_description))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                val notificationManager = NotificationManagerCompat.from(context)
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(ACCOUNT_ERROR, builder.build())
            }
        }

        private const val NO_VEHICLES = 944
        fun missingVehicles(context: Context) {
            val intent = Intent(context, VehicleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(
                context!!, IMPORTANT_NOTIFICATIONS
            )
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                .setContentTitle(context.getString(R.string.no_vehicles_notification_title))
                .setContentText(context.getString(R.string.no_vehicles_notification_description))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NO_VEHICLES, builder.build())
        }

        private const val JAN25STATUS = 945
        fun statusUpdate(context: Context) {
            val statusVersion_key = "viewJan25status"
            val currentStatusVersion =
                PreferenceManager.getDefaultSharedPreferences(context).getInt(statusVersion_key, 0)
            if (currentStatusVersion < Constants.STATUS_UPDATE_VERSION) {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                val builder = NotificationCompat.Builder(context, NORMAL_NOTIFICATIONS)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setColor(ContextCompat.getColor(context, R.color.light_blue_900))
                    .setContentTitle(context.getString(R.string.app_status_update_title))
                    .setContentText(context.getString(R.string.app_status_update_text))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                val notificationManager = NotificationManagerCompat.from(context)
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(JAN25STATUS, builder.build())
            }
        }

    }
}