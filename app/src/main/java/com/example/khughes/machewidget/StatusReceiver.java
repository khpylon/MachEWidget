package com.example.khughes.machewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StatusReceiver extends BroadcastReceiver {
    private Context mContext;
    private StoredData appInfo;

    @Override
    public void onReceive(Context context, Intent arg1) {
        mContext = context;
        nextAlarm(mContext);

        appInfo = new StoredData(context);
        long timeout = appInfo.getTokenTimeout();
        LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
        long nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ProgramStateMachine.States state = new ProgramStateMachine(appInfo.getProgramState()).getCurrentState();

        Log.d(MainActivity.CHANNEL_ID, "times are " + (timeout / 1000 - nowtime / 1000) + ", state is " + state.name());

        if (state.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
            if (timeout + 5000 < nowtime) {
                getRefresh(appInfo.getRefreshToken());
            } else {
                getStatus(appInfo.getAccessToken());
                getOTAStatus(appInfo.getAccessToken());
            }
            appInfo.incCounter(StoredData.GOOD);
        } else if (state.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VIN_AGAIN) ||
                state.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN)) {
            getRefresh(appInfo.getRefreshToken());
            appInfo.incCounter(StoredData.BAD);
        } else {
            Log.d(MainActivity.CHANNEL_ID, "Hmmm... don't know what to do here");
            getRefresh(appInfo.getRefreshToken());
            appInfo.incCounter(StoredData.UGLY);
        }
        int good = appInfo.getCounter(StoredData.GOOD);
        int bad = appInfo.getCounter(StoredData.BAD);
        int ugly = appInfo.getCounter(StoredData.UGLY);

        Log.d(MainActivity.CHANNEL_ID, "good = " + good + ", bad = " + bad + ",ugly = " + ugly);
    }

    private Bundle bb = new Bundle();

    private void getRefresh(String refreshToken) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                ProgramStateMachine.States action = ProgramStateMachine.States.valueOf(bb.getString("action"));
                Log.d(MainActivity.CHANNEL_ID, "Refresh: " + action);
                appInfo.setProgramState(action);
                if (action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS) || action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                    String accessToken = bb.getString("access_token");
                    appInfo.setAccessToken(accessToken);
                    appInfo.setRefreshToken(bb.getString("refresh_token"));
                    int expires = bb.getInt("expires", 0);
                    LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(expires);
                    long nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    appInfo.setTokenTimeout(nextTime);
                    getStatus(accessToken);getOTAStatus(accessToken);
                }
            }
        };
        NetworkCalls.refreshAccessToken(h, mContext, refreshToken);
    }

    private void getStatus(String accessToken) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                ProgramStateMachine.States action = ProgramStateMachine.States.valueOf(bb.getString("action"));
                Log.d(MainActivity.CHANNEL_ID, "Status: " + action);
                appInfo.setProgramState(action);
                if (action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                    MainActivity.updateWidget(mContext);
                }
            }
        };
        NetworkCalls.getStatus(h, mContext, accessToken);
    }

    private void getOTAStatus(String accessToken) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                ProgramStateMachine.States action = ProgramStateMachine.States.valueOf(bb.getString("action"));
                if (action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                    MainActivity.updateWidget(mContext);
                }
            }
        };
        NetworkCalls.getOTAStatus(h, mContext, accessToken);
    }

    public static void nextAlarm(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int delay = new Integer(sharedPref.getString( context.getResources().getString(R.string.update_frequency_key), "10")) * 60;
        nextAlarm(context, delay);
    }

    public static void nextAlarm(Context context, int delay) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(delay);
        String timeText = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss", Locale.US));
        Log.i(MainActivity.CHANNEL_ID, "Next AlarmReceiver at " + timeText);
        long nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Intent intent = new Intent(context, StatusReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, nextTime, DateUtils.SECOND_IN_MILLIS, pendingIntent);
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, StatusReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
