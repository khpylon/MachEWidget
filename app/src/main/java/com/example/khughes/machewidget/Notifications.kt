package com.example.khughes.machewidget

import com.example.khughes.machewidget.Misc.Companion.ignoringBatteryOptimizations
import android.content.BroadcastReceiver
import android.content.Intent
import com.example.khughes.machewidget.CarStatus.CarStatus
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.HashMap

class Notifications : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            LVB_NOTIFICATION -> LVBNotificationVisible = false
            TPMS_NOTIFICATION -> TPMSNotificationVisible = false
            CHARGE_NOTIFICATION -> ChargeNotificationVisible = false
        }
    }

    companion object {
        //    private static final int OTA_NOTIFICATION = 935;
        //
        //    public static void newOTA(Context context, String message) {
        //        Intent intent = new Intent(context, OTAViewActivity.class);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        //        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
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
            val currentLVBStatus = carStatus.vehiclestatus.battery?.batteryHealth?.value ?: lastLVBStatus
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
                    val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.notification_icon
                            )
                        )
                        .setContentTitle("LVB Status")
                        .setContentText("The LVB's status is reporting \"low\".")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
        private const val LEFT_FRONT_TIRE = "the left front tire is"
        private const val RIGHT_FRONT_TIRE = "the right front tire is"
        private const val LEFT_REAR_TIRE = "the left rear tire is"
        private const val RIGHT_REAR_TIRE = "the right rear tire is"
        private const val MANY_TIRES = "multiple tires are"
        private var TPMSNotificationVisible = false
        @JvmStatic
        fun checkTPMSStatus(context: Context, carStatus: CarStatus, vehInfo: VehicleInfo) {
            val lastTPMSStatus = vehInfo.lastTPMSStatus
            val currentTPMSStatus: MutableMap<String, String> = HashMap()
            currentTPMSStatus[LEFT_FRONT_TIRE] = carStatus.vehiclestatus.tpms?.leftFrontTireStatus?.value ?: "Normal"
            currentTPMSStatus[RIGHT_FRONT_TIRE] = carStatus.vehiclestatus.tpms?.rightFrontTireStatus?.value ?: "Normal"
            currentTPMSStatus[LEFT_REAR_TIRE] = carStatus.vehiclestatus.tpms?.outerLeftRearTireStatus?.value ?: "Normal"
            currentTPMSStatus[RIGHT_REAR_TIRE] = carStatus.vehiclestatus.tpms?.outerRightRearTireStatus?.value ?: "Normal"
            var badTire = ""
            for (key in arrayOf(
                LEFT_FRONT_TIRE,
                RIGHT_FRONT_TIRE,
                LEFT_REAR_TIRE,
                RIGHT_REAR_TIRE
            )) {
                val tire = currentTPMSStatus[key]
                if (tire != "Normal") {
                    badTire = if (badTire == "") {
                        key
                    } else {
                        MANY_TIRES
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
                    val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.notification_icon
                            )
                        )
                        .setContentTitle("TPMS Status")
                        .setContentText("The TPMS status on $badTire abnormal.")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
                val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.notification_icon
                        )
                    )
                    .setContentTitle("Charge Status")
                    .setContentText("Charging is complete.")
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
                    val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.notification_icon
                            )
                        )
                        .setContentTitle("Battery Optimizations are on")
                        .setContentText("This may interfere with the app's performance; consider disabling them.")
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
            val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.notification_icon))
                .setContentTitle("Charge Reminder")
                .setContentText("HVB level is below threshold and charger is not detected.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(context)
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
                context!!, MainActivity.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Login required")
                .setContentText("Unable to refresh tokens: you need to log in again.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
                val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.notification_icon
                        )
                    )
                    .setContentTitle("New survey available")
                    .setContentText("Please take the survey to provide feedback to the developer.")
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
                val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.notification_icon
                        )
                    )
                    .setContentTitle("FordPass account disabled!")
                    .setContentText("The app is unable to access your information.  Contact Ford to reactivate your account.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                val notificationManager = NotificationManagerCompat.from(context)
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(ACCOUNT_ERROR, builder.build())
            }
        }
    }
}