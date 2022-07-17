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
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class StoredData {
    public static final String TAG = "saveAppInfo";
    public static final String VINLIST = "VINs";

    private static final String ACCESSTOKEN = "AccessToken";
    private static final String REFRESHTOKEN = "RefreshToken";
    private static final String PROGRAMSTATE = "ProgramState";
    private static final String TOKENTIMEOUT = "TokenTimeout";
    private static final String COUNTRY = "Country";
    private static final String LANGUAGE = "Language";
    private static final String SPEEDUNITS = "SpeedUnits";
    private static final String DISTANCEUNITS = "DistanceUnits";
    private static final String PRESSUREUNITS = "PressureUnits";
    private static final String LASTALARMTIME = "LastAlarmTime";
    private static final String LEFTAPPPACKAGE = "LeftAppPackage";
    private static final String RIGHTAPPPACKAGE = "RightAppPackage";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String WIDGETMODE = "widgetmode";

    private static final String LATESTVERSION = "LatestVersion";
    private static final String BATTERYNOTIFICATION = "BatteryOptNotificationTime";

    public static final String GOOD = "Good";
    public static final String BAD = "Bad";
    public static final String UGLY = "Ugly";

    public static final String STATUS_NOT_LOGGED_IN = "Logged out";
    public static final String STATUS_LOG_OUT = "Log out";
    public static final String STATUS_LOG_IN = "Log in";
    public static final String STATUS_VEHICLE_INFO = "Vehicle Info";
    public static final String STATUS_UPDATED = "Updated";
    public static final String STATUS_UNKNOWN = "Unknown";

    private final Context mContext;

    public StoredData(Context context) {
        mContext = context;
    }

    // Profile specific methods

    public ArrayList<String> getProfiles() {
        SharedPreferences pref = mContext.getSharedPreferences(VINLIST, MODE_PRIVATE);
        return new ArrayList<>(pref.getAll().keySet());
    }

    private void setProfiles(ArrayList<String> profiles) {
        clearProfiles();
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VINLIST, MODE_PRIVATE).edit();
        for (String VIN : profiles) {
            edit.putInt(VIN, 0);
        }
        commitWait(edit);
    }

    public void addProfile(String VIN, String profileName) {
        // Just in case, remove old info if it exists
        removeProfile(VIN);

        // Add the VIN to profile lsit
        commitWait(mContext.getSharedPreferences(VINLIST, MODE_PRIVATE).edit().putInt(VIN, 0));

        // Store the profile name
//        setProfileName(VIN, profileName);
    }

    public void clearProfiles() {
        for (String VIN : getProfiles()) {
            removeProfile(VIN);
        }
    }

    public void removeProfile(String VIN) {
        ArrayList<String> profiles = getProfiles();
        String match = profiles.stream().filter(s -> s.equals(VIN)).findFirst().orElse(null);
        if (match != null) {
            // remove the VIN from the profile list
            commitWait(mContext.getSharedPreferences(VINLIST, MODE_PRIVATE).edit().remove(VIN));

            // Remove the actual file from storage
            File f = new File(mContext.getDataDir() + File.separator + Constants.SHAREDPREFS_FOLDER, VIN + ".xml");
            f.deleteOnExit();
        }
    }

    // Getters/setters for specific attributes

    private void commitWait(SharedPreferences.Editor edit) {
        for (int i = 0; i < 10; ++i) {
            if (edit.commit()) {
                return;
            }
        }
    }

    public String getAccessToken(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(ACCESSTOKEN, "");
    }

    public String getRefreshToken(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(REFRESHTOKEN, "");
    }

    public long getTokenTimeout(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getLong(TOKENTIMEOUT, 0);
    }

    public String getProgramState(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(PROGRAMSTATE, Constants.STATE_INITIAL_STATE);
    }

    public String getLanguage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(LANGUAGE, "");
    }

    public String getCountry(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(COUNTRY, "");
    }

    public String getSpeedUnits(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(SPEEDUNITS, "MPH");
    }

    public int getDistanceUnits(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getInt(DISTANCEUNITS, -1);
    }

    public String getPressureUnits(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(PRESSUREUNITS, "PSI");
    }

    public String getLeftAppPackage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(LEFTAPPPACKAGE, "com.ford.fordpass");
    }

    public String getRightAppPackage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(RIGHTAPPPACKAGE, null);
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

    public String getSecretPassword() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        if (!pref.contains(PASSWORD)) {
            byte[] array = new byte[64];
            new Random().nextBytes(array);
            commitWait(pref.edit().putString(PASSWORD, new String(array, StandardCharsets.UTF_8)));
        }
        return pref.getString(PASSWORD, "");
    }

    private String getCryptoString(String VIN, String tag) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        String cipher = pref.getString(tag, null);
        Encryption encrypt = new Encryption(mContext);
        return encrypt.getPlaintextString( cipher);
    }

    public String getUsername(String VIN) { return getCryptoString(VIN, USERNAME); }

    public String getPassword(String VIN) {
        return getCryptoString(VIN, PASSWORD);
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

    public void removeCounters() {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        edit.remove(GOOD);
        edit.remove(BAD);
        edit.remove(UGLY);
        commitWait(edit);
    }

    public void incCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        int value = pref.getInt(key, 0) + 1;
        commitWait(edit.putInt(key, value));
    }
}
