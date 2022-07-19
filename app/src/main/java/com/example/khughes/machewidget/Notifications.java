package com.example.khughes.machewidget;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.CarStatus.CarStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class Notifications extends BroadcastReceiver {
    private static final int OTA_NOTIFICATION = 935;

    public static void newOTA(Context context) {
        Intent intent = new Intent(context, OTAViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("OTA information")
                .setContentText("New OTA information was found.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(OTA_NOTIFICATION, builder.build());
    }

    private static final int LVB_STATUS = 936;
    private static final String LVB_NOTIFICATION = BuildConfig.APPLICATION_ID + ".Notifications.LVB";

    private static Boolean LVBNotificationVisible = false;

    public static void checkLVBStatus(Context context, CarStatus carStatus, VehicleInfo vehInfo) {
        String lastLVBStatus = vehInfo.getLastLVBStatus();
        String currentLVBStatus = carStatus.getLVBStatus();
        if (currentLVBStatus != null && !currentLVBStatus.equals(lastLVBStatus)) {
            // Save the current status
            vehInfo.setLastLVBStatus(currentLVBStatus);

            // If the current status is bad and we haven't already posted the notification, then post it
            if (!currentLVBStatus.equals("STATUS_GOOD") && !LVBNotificationVisible) {
                Intent intent = new Intent(context, Notifications.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setAction(LVB_NOTIFICATION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("LVB Status")
                        .setContentText("The LVB's status is reporting \"low\".")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(LVB_STATUS, builder.build());
                LVBNotificationVisible = true;
            } else {
                LVBNotificationVisible = false;
            }
        }
    }

    private static final int TPMS_STATUS = 937;
    private static final String TPMS_NOTIFICATION = BuildConfig.APPLICATION_ID + ".Notifications.TPMS";

    private static final String LEFT_FRONT_TIRE = "the left front tire is";
    private static final String RIGHT_FRONT_TIRE = "the right front tire is";
    private static final String LEFT_REAR_TIRE = "the left rear tire is";
    private static final String RIGHT_REAR_TIRE = "the right rear tire is";
    private static final String MANY_TIRES = "multiple tires are";

    private static Boolean TPMSNotificationVisible = false;

    public static void checkTPMSStatus(Context context, CarStatus carStatus, VehicleInfo vehInfo) {
        String lastTPMSStatus = vehInfo.getLastTPMSStatus();

        Map<String, String> currentTPMSStatus = new HashMap<String, String>();
        currentTPMSStatus.put(LEFT_FRONT_TIRE, carStatus.getLeftFrontTireStatus());
        currentTPMSStatus.put(RIGHT_FRONT_TIRE, carStatus.getRightFrontTireStatus());
        currentTPMSStatus.put(LEFT_REAR_TIRE, carStatus.getLeftRearTireStatus());
        currentTPMSStatus.put(RIGHT_REAR_TIRE, carStatus.getRightRearTireStatus());

        String badTire = "";
        for (String key : new String[]{LEFT_FRONT_TIRE, RIGHT_FRONT_TIRE, LEFT_REAR_TIRE, RIGHT_REAR_TIRE}) {
            String tire = currentTPMSStatus.get(key);
            if (tire != null && !tire.equals("Normal")) {
                if (badTire.equals("")) {
                    badTire = key;
                } else {
                    badTire = MANY_TIRES;
                }
            }
        }

        if (lastTPMSStatus != null && !lastTPMSStatus.equals(badTire)) {
            // Save the current status
            vehInfo.setLastTPMSStatus(badTire);

            // If the current status is bad and we haven't already posted the notification, then post it
            if (!badTire.equals("") && !TPMSNotificationVisible) {
                Intent intent = new Intent(context, Notifications.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setAction(TPMS_NOTIFICATION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("TPMS Status")
                        .setContentText("The TPMS status on " + badTire + " abnormal.")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(TPMS_STATUS, builder.build());
                TPMSNotificationVisible = true;
            } else {
                TPMSNotificationVisible = false;
            }
        }
    }

    private static final int CHARGE_STATUS = 938;
    private static final String CHARGE_NOTIFICATION = BuildConfig.APPLICATION_ID + ".Notifications.Charge";

    private static Boolean ChargeNotificationVisible = false;

    public static void chargeComplete(Context context) {
        if (!ChargeNotificationVisible) {
            Intent intent = new Intent(context, Notifications.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(LVB_NOTIFICATION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Charge Status")
                    .setContentText("Charging is complete.")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(CHARGE_STATUS, builder.build());
            ChargeNotificationVisible = true;
        }
    }

    private static final int BATTERY_OPTIMIZATION = 939;
    private static final String BATTERY_OPTIMIZATION_NOTIFICATION = BuildConfig.APPLICATION_ID + ".Notifications.BatteryOptimization";

    public static void batteryOptimization(Context context) {
        StoredData appInfo = new StoredData(context);
        boolean batteryNotification = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.batteryNotification_key), true);

        if (batteryNotification && !Utils.ignoringBatteryOptimizations(context)) {
            long lastBatteryNotification = appInfo.getBatteryNotification();
            LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
            long nowTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            if (nowTime > lastBatteryNotification) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("Battery Optimizations are on")
                        .setContentText("This may interfere with the app's performance; consider disabling them.")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(CHARGE_STATUS, builder.build());

                // If this was the first notification, notify again in one more day
                if (lastBatteryNotification == 0) {
                    nowTime += 1000 * 60 * 60 * 24;
                }
                // Otherwise, don't bother for a long time
                else {
                    nowTime = Long.MAX_VALUE;
                }
                appInfo.setBatteryNotification(nowTime);
            }
        } else {
            appInfo.setBatteryNotification(0);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(LVB_NOTIFICATION)) {
            LVBNotificationVisible = false;
        } else if (action.equals(TPMS_NOTIFICATION)) {
            TPMSNotificationVisible = false;
        } else if (action.equals(CHARGE_NOTIFICATION)) {
            ChargeNotificationVisible = false;
        }
    }
}
