package com.example.khughes.machewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

public class UpdateReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent arg) {
        mContext = context;
        nextAlarm(mContext);
        final String apiUrl = "https://raw.githubusercontent.com/khpylon/MachEWidget/master/app/VERSION.txt";
        new Download().execute(apiUrl);
    }

    private class Download extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            String current = "";
            try {
                HttpURLConnection urlConnection = null;
                URL url = new URL(urls[0]);
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    byte[] data = new byte[2048];
                    int count = 0;
                    while ((count = input.read(data)) != -1) {
                        current += new String(data, 0, count, StandardCharsets.UTF_8);
                    }
                    input.close();
                    return current;
                } catch (Exception e) {
                    LogFile.e(mContext, MainActivity.CHANNEL_ID, "exception in UpdateReceiver.doInBackground()" + e);
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                LogFile.e(mContext, MainActivity.CHANNEL_ID, "exception in UpdateReceiver.doInBackground()" + e);
            }
            return current;
        }

        protected void onPostExecute(String result) {
            final String Version = "Version: ";
            StoredData appInfo = new StoredData(mContext);
            final String latestVersion = appInfo.getLatestVersion();
            for (String item : result.split("\n")) {
                if (item.contains(Version)) {
                    String newVersion = item.replace(Version, "");
                    if (newVersion.compareTo(BuildConfig.VERSION_NAME) > 0 &&
                            newVersion.compareTo(latestVersion) > 0) {
                        appInfo.setLatestVersion(newVersion);
                        Notifications.newApp(mContext);
                        return;
                    }
                }
            }
        }
    }

    private static Intent getIntent(Context context) {
        return new Intent(context, UpdateReceiver.class).setAction("UpdateReceiver");
    }

    public static void nextAlarm(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault()).plusHours(4);
        long nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                getIntent(context), PendingIntent.FLAG_IMMUTABLE);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, nextTime, DateUtils.MINUTE_IN_MILLIS, pendingIntent);
    }

    // If no alarm is pending, start one
    public static void initateAlarm(Context context) {
        if (PendingIntent.getBroadcast(context, 0,
                getIntent(context), PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) == null) {
            nextAlarm(context);
        }
    }
}
