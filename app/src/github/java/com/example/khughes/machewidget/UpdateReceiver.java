package com.example.khughes.machewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        nextAlarm(context);
        final String apiUrl = "https://raw.githubusercontent.com/khpylon/MachEWidget/master/app/VERSION.txt";
        new Download(context).execute(apiUrl);
    }

    private static class Download extends AsyncTask<String, String, String> {

        private final WeakReference<Context> mContext;

        public Download(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... urls) {
            Context context = mContext.get();
            StringBuilder current = new StringBuilder();
            try {
                HttpURLConnection urlConnection = null;
                URL url = new URL(urls[0]);
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    byte[] data = new byte[2048];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        current.append(new String(data, 0, count, StandardCharsets.UTF_8));
                    }
                    input.close();
                } catch (Exception e) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "exception in UpdateReceiver.doInBackground()" + e);
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "exception in UpdateReceiver.doInBackground()" + e);
            }
            return current.toString();
        }

        protected void onPostExecute(String result) {
            Context context = mContext.get();
            final String Version = "Version: ";
            StoredData appInfo = new StoredData(context);
            final String latestVersion = appInfo.getLatestVersion();
            for (String item : result.split("\n")) {
                if (item.contains(Version)) {
                    String newVersion = item.replace(Version, "");
                    LogFile.e(context, MainActivity.CHANNEL_ID, "UpdateReceiver.onPostExecute(): newest version is " + newVersion);
                    if (newVersion.compareTo(BuildConfig.VERSION_NAME) > 0 &&
                            newVersion.compareTo(latestVersion) > 0) {
                        appInfo.setLatestVersion(newVersion);
                        newApp(context);
                        LogFile.e(context, MainActivity.CHANNEL_ID, "UpdateReceiver.onPostExecute(): launching notification");
                        return;
                    }
                }
            }
        }
    }

    private static final int APP_NOTIFICATION = 938;

    private static void newApp(Context context) {
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Notifications.Companion.getNORMAL_NOTIFICATIONS())
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(context,R.color.light_blue_900))
                .setContentTitle("App update")
                .setContentText("A new app version was found.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(APP_NOTIFICATION, builder.build());
    }



    private static Intent getIntent(Context context) {
        return new Intent(context, UpdateReceiver.class).setAction("UpdateReceiver");
    }

    public static void nextAlarm(Context context) {
        LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault()).plusHours(1);
        String timeText = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss", Locale.US));
        LogFile.i(context, MainActivity.CHANNEL_ID, "UpdateReceiver: next update alarm at " + timeText);
        long nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                getIntent(context), PendingIntent.FLAG_IMMUTABLE);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, nextTime, DateUtils.MINUTE_IN_MILLIS, pendingIntent);
    }

    // If no alarm is pending, start one
    public static void initiateAlarm(Context context) {
        if (PendingIntent.getBroadcast(context, 0,
                getIntent(context), PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) == null) {
            nextAlarm(context);
        }
    }

    // Check for a new version of the app sometime soon
    public static void createIntent(Context context) {
        Intent intent = new Intent(context, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.HOUR_IN_MILLIS, pendingIntent);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateUtils.MINUTE_IN_MILLIS,
                DateUtils.MINUTE_IN_MILLIS, pendingIntent);
    }
}
