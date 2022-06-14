package com.example.khughes.machewidget;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.MessageFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StatusReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        final int Millis = 1000;

        mContext = context;
        nextAlarm(mContext);

        Notifications.batteryOptimization(context);

        StoredData appInfo = new StoredData(context);
        InfoRepository[] info = {null};

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                UserInfo userInfo = info[0].getUser();
                if (userInfo == null) {
                    LogFile.d(mContext, MainActivity.CHANNEL_ID, "StatusReceiver: no userinfo found");
                    return;
                }
                long timeout = userInfo.getExpiresIn();
                LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
                long nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                // Store time when we run the update;
                appInfo.setLastAlarmTime();

                String userId = userInfo.getUserId();
                String state = userInfo.getProgramState();

                LogFile.d(mContext, MainActivity.CHANNEL_ID,
                        MessageFormat.format("StatusReceiver: time({0}), state({1}), battery optimization({2})",
                                (timeout - nowtime) / Millis, state, MainActivity.ignoringBatteryOptimizations(context)));

                // Check whether credentials are being saved
                boolean savingCredentials = PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getBoolean(mContext.getResources().getString(R.string.save_credentials_key), true);

                switch (state) {
                    case Constants.STATE_INITIAL_STATE:
                        LogFile.d(mContext, MainActivity.CHANNEL_ID, "Initial state: how'd did the alarm go off (manual refresh)?");
                        cancelAlarm(context);
                        appInfo.incCounter(StoredData.STATUS_NOT_LOGGED_IN);
                        break;
                    case Constants.STATE_ATTEMPT_TO_GET_ACCESS_TOKEN:
                        if (savingCredentials) {
                            LogFile.d(mContext, MainActivity.CHANNEL_ID, "Ok, trying to log in; wish me luck");
                            Encryption encrypt = new Encryption(context);
                            String username = encrypt.getPlaintextString(userInfo.getUsername());
                            String password = encrypt.getPlaintextString(userInfo.getPassword());
                            getAccess(username, password);
                            appInfo.incCounter(StoredData.STATUS_LOG_IN);
                        } else {
                            userInfo.setProgramState(Constants.STATE_INITIAL_STATE);
                            LogFile.d(mContext, MainActivity.CHANNEL_ID, "Log-in required but credentials are not being saved: cancelling alarm");
                            cancelAlarm(context);
                            appInfo.incCounter(StoredData.STATUS_LOG_OUT);
                        }
                        break;
                    case Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN:
                        LogFile.d(mContext, MainActivity.CHANNEL_ID, "STILL need to refresh token");
                        getRefresh(userId, userInfo.getRefreshToken());
                        appInfo.incCounter(StoredData.STATUS_UPDATED);
                        break;
                    case Constants.STATE_HAVE_TOKEN:
                        LogFile.d(mContext, MainActivity.CHANNEL_ID, "need to get vehicle info");
                        getVehicleInfo(userId);
                        appInfo.incCounter(StoredData.STATUS_VEHICLE_INFO);
                        break;
                    case Constants.STATE_HAVE_TOKEN_AND_VIN:
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                        int delayInMillis = Integer.parseInt(sharedPref.getString(context.getResources().getString(R.string.update_frequency_key), "10")) * 60 * Millis;

                        // Find out when the list of vehicles was updated
                        String lastModified = userInfo.getLastModified();
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat(Constants.LASTMODIFIEDFORMAT, Locale.ENGLISH);
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        try {
                            cal.setTime(sdf.parse(lastModified));
                        } catch (ParseException e) {
                        }
                        long thenTime = cal.getTimeInMillis();

                        // Since actions such as "Refresh" don't check the token's expiration, be sure to refresh if it would expire before
                        // the next update.
                        long calculation = (timeout - delayInMillis - 5 * Millis) - nowtime;
                        LogFile.d(mContext, MainActivity.CHANNEL_ID, "Calculating time as " + calculation);
                        if (timeout - delayInMillis - 5 * Millis < nowtime) {
                            LogFile.d(mContext, MainActivity.CHANNEL_ID, "Need to refresh token");
                            getRefresh(userId, userInfo.getRefreshToken());
                        }
                        // Checked the vehicle list every hour
                        else if ((nowtime - thenTime) / (1000 * 60 * 60) > 1) {
                            LogFile.d(mContext, MainActivity.CHANNEL_ID, "Going to check for vehicle changes");
                            getVehicleInfo(userId);
                        } else {
                            LogFile.d(mContext, MainActivity.CHANNEL_ID, "Token good? Just grab info");
                            getStatus(userId);
                        }
                        appInfo.incCounter(StoredData.STATUS_UPDATED);
                        break;
                    // Since everything above should cover all the states, we should never get here
                    default:
                        if (savingCredentials) {
                            LogFile.d(mContext, MainActivity.CHANNEL_ID, "Hmmm... How did I get here. Trying to login");
                            Encryption encrypt = new Encryption(context);
                            String username = encrypt.getPlaintextString(userInfo.getUsername());
                            String password = encrypt.getPlaintextString(userInfo.getPassword());
                            getAccess(username, password);
                        } else {
                            LogFile.d(mContext, MainActivity.CHANNEL_ID, "Hmmm... How did I get here. Wish I could login");
                        }
                        appInfo.incCounter(StoredData.STATUS_UNKNOWN);
                        break;
                }

                LogFile.d(mContext, MainActivity.CHANNEL_ID,
                        MessageFormat.format("StatusReceiver status history: {0}({1}) {2}({3}) {4}({5}) {6}({7}) {8}({9}) {10}({11})",
                                StoredData.STATUS_NOT_LOGGED_IN, appInfo.getCounter(StoredData.STATUS_NOT_LOGGED_IN),
                                StoredData.STATUS_LOG_OUT, appInfo.getCounter(StoredData.STATUS_LOG_OUT),
                                StoredData.STATUS_LOG_IN, appInfo.getCounter(StoredData.STATUS_LOG_IN),
                                StoredData.STATUS_UPDATED, appInfo.getCounter(StoredData.STATUS_UPDATED),
                                StoredData.STATUS_VEHICLE_INFO, appInfo.getCounter(StoredData.STATUS_VEHICLE_INFO),
                                StoredData.STATUS_UNKNOWN, appInfo.getCounter(StoredData.STATUS_UNKNOWN)
                        ));

            }
        };

        new Thread(() -> {
            info[0] = new InfoRepository(context);
            handler.sendEmptyMessage(0);
        }).start();
    }

    private Bundle bb = new Bundle();

    private void getAccess(String username, String password) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                String action = bb.getString("action");
                LogFile.i(mContext, MainActivity.CHANNEL_ID, "Access: " + action);
                if (action.equals(Constants.STATE_HAVE_TOKEN)) {
                    String userId = bb.getString("usedId");
                    getVehicleInfo(userId);
                }
            }
        };
        NetworkCalls.getAccessToken(h, mContext, username, password);
    }

    private void getRefresh(String userId, String refreshToken) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                String action = bb.getString("action");
                LogFile.i(mContext, MainActivity.CHANNEL_ID, "Refresh: " + action);
                if (action.equals(Constants.STATE_HAVE_TOKEN_AND_VIN)) {
                    getStatus(userId);
                }
            }
        };
        NetworkCalls.refreshAccessToken(h, mContext, userId, refreshToken);
    }

    private void getVehicleInfo(String userId) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                String action = bb.getString("action");
                if (action.equals(Constants.STATE_HAVE_TOKEN_AND_VIN)) {
                    getStatus(userId);
                }
                LogFile.i(mContext, MainActivity.CHANNEL_ID, "VehicleInfo: " + action);
            }
        };
        NetworkCalls.getUserVehicles(h, mContext, userId);
    }

    private void getStatus(String userId) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                String action = bb.getString("action");
                LogFile.i(mContext, MainActivity.CHANNEL_ID, "Status: " + action);
                // Update the widgets, no mattery what.
                MainActivity.updateWidget(mContext);
            }
        };
        NetworkCalls.getStatus(h, mContext, userId);
    }

    public static void nextAlarm(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int delay = Integer.parseInt(sharedPref.getString(context.getResources().getString(R.string.update_frequency_key), "10"));
        if (delay != 0) {
            nextAlarm(context, delay * 60);
        }
    }

    private static Intent getIntent(Context context) {
        return new Intent(context, StatusReceiver.class).setAction("StatusReceiver");
    }

    public static void nextAlarm(Context context, int delay) {
        LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(delay);
        String timeText = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss", Locale.US));
        LogFile.i(context, MainActivity.CHANNEL_ID, "StatusReceiver: next status alarm at " + timeText);
        long nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                getIntent(context), PendingIntent.FLAG_IMMUTABLE);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, nextTime, DateUtils.SECOND_IN_MILLIS, pendingIntent);
    }

    public static void cancelAlarm(Context context) {
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                getIntent(context), PendingIntent.FLAG_IMMUTABLE);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        Log.d(MainActivity.CHANNEL_ID, "StatusReceiver: cancelling status alarm");
    }

    // If no alarm is pending, start one
    @SuppressLint("NewApi")
    public static void initateAlarm(Context context) {
        if (PendingIntent.getBroadcast(context, 0,
                getIntent(context), PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) == null) {
            nextAlarm(context);
        } else {
            Log.d(MainActivity.CHANNEL_ID, "StatusReceiver: no pending status alarm");
        }
    }
}
