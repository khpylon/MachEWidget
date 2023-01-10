package com.example.khughes.machewidget;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class StoredData {
    public static final String TAG = "saveAppInfo";

    private static final String LASTALARMTIME = "LastAlarmTime";
    private static final String LEFTAPPPACKAGE = "LeftAppPackage";
    private static final String RIGHTAPPPACKAGE = "RightAppPackage";
    private static final String LATESTVERSION = "LatestVersion";
    private static final String BATTERYNOTIFICATION = "BatteryOptNotificationTime";

    public static final String STATUS_NOT_LOGGED_IN = "Logged out";
    public static final String STATUS_LOG_OUT = "Log out";
    public static final String STATUS_LOG_IN = "Log in";
    public static final String STATUS_VEHICLE_INFO = "Vehicle Info";
    public static final String STATUS_UPDATED = "Updated";
    public static final String STATUS_UNKNOWN = "Unknown";

    // When the list above changes, be sure to change this function also
    public static final ArrayList<String> getKeys() {
        return new ArrayList<>(Arrays.asList(
                LASTALARMTIME, LEFTAPPPACKAGE, RIGHTAPPPACKAGE, LATESTVERSION, BATTERYNOTIFICATION,
                STATUS_NOT_LOGGED_IN, STATUS_LOG_OUT, STATUS_LOG_IN, STATUS_VEHICLE_INFO,
                STATUS_UPDATED, STATUS_UNKNOWN
        ));
    }

    private final Context mContext;

    public StoredData(Context context) {
        mContext = context;
    }

    // Getters/setters for specific attributes

    private void commitWait(SharedPreferences.Editor edit) {
        for (int i = 0; i < 10; ++i) {
            if (edit.commit()) {
                return;
            }
        }
    }

    public String getLeftAppPackage() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(LEFTAPPPACKAGE, "com.ford.fordpass");
    }

    public void setLeftAppPackage(String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        edit.putString(LEFTAPPPACKAGE, name);
        commitWait(edit);
    }

    public String getRightAppPackage() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(RIGHTAPPPACKAGE, null);
    }

    public void setRightAppPackage(String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        edit.putString(RIGHTAPPPACKAGE, name);
        commitWait(edit);
    }

    public String getLatestVersion() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(LATESTVERSION, "");
    }

    public void setLatestVersion(String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        edit.putString(LATESTVERSION, name);
        commitWait(edit);
    }

    public void setLastAlarmTime() {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        long nowtime = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        commitWait(edit.putLong(LASTALARMTIME, nowtime));
    }

    public long getLastAlarmTime() {
        return mContext.getSharedPreferences(TAG, MODE_PRIVATE).getLong(LASTALARMTIME, 0);
    }

    public void setBatteryNotification(long time) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        commitWait(edit.putLong(BATTERYNOTIFICATION, time));
    }

    public long getBatteryNotification() {
        return mContext.getSharedPreferences(TAG, MODE_PRIVATE).getLong(BATTERYNOTIFICATION, 0);
    }

    public int getCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public void incCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        int value = pref.getInt(key, 0) + 1;
        commitWait(edit.putInt(key, value));
    }

}
