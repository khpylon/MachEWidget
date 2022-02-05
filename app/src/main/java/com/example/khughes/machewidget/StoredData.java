package com.example.khughes.machewidget;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class StoredData {
    public static final String TAG = "saveAppInfo";
    private static final String VINLIST = "VINs";

    private static final String PROFILENAME = "Alias";
    private static final String ACCESSTOKEN = "AccessToken";
    private static final String REFRESHTOKEN = "RefreshToken";
    private static final String PROGRAMSTATE = "ProgramState";
    private static final String TOKENTIMEOUT = "TokenTimeout";
    private static final String CARSTATUS = "CarStatus";
    private static final String OTASTATUS = "OTAStatus";
    private static final String HVBSTATUS = "HVBStatus";
    private static final String TPMSSTATUS = "TPMSStatus";
    private static final String COUNTRY = "Country";
    private static final String LANGUAGE = "Language";
    private static final String SPEEDUNITS = "SpeedUnits";
    private static final String DISTANCEUNITS = "DistanceUnits";
    private static final String PRESSUREUNITS = "PressureUnits";
    private static final String LASTUPDATETIME = "LastUpdateTime";
    private static final String LEFTAPPPACKAGE = "LeftAppPackage";
    private static final String RIGHTAPPPACKAGE = "RightAppPackage";

    private static final String LATESTVERSION = "LatestVersion";
    public static final String GOOD = "Good";
    public static final String BAD = "Bad";
    public static final String UGLY = "Ugly";

    private Context mContext;
    private Gson gson = new GsonBuilder().create();

    public StoredData(Context context) {
        mContext = context;
    }

    // Profile specific methods

    public ArrayList<String> getProfiles() {
        SharedPreferences pref = mContext.getSharedPreferences(VINLIST, MODE_PRIVATE);
        return new ArrayList<String>(pref.getAll().keySet());
    }

    private void setProfiles(ArrayList<String> profiles) {
        clearProfiles();
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VINLIST, MODE_PRIVATE).edit();
        for (String VIN : profiles) {
            edit.putInt(VIN, 0);
        }
        edit.apply();
    }

    public void addProfile(String VIN, String profileName) {
        // Just in case, remove old info if it exists
        removeProfile(VIN);

        // Add the VIN to profile lsit
        mContext.getSharedPreferences(VINLIST, MODE_PRIVATE).edit().putInt(VIN, 0).apply();

        // Store the profile name
        setProfileName(VIN, profileName);
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
            mContext.getSharedPreferences(VINLIST, MODE_PRIVATE).edit().remove(VIN).apply();

            // Remove the actual file from storage
            Path dir = mContext.getFilesDir().getParentFile().toPath();
            File f = new File(dir.toString() + "/shared_prefs", VIN + ".xml");
            f.delete();
        }
    }

    // Remove all keys not specific to a profile (pretty much everything)
    public void deleteOldCredentials() {
        SharedPreferences prefs = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        // Keep the latest version info
        String version = getLatestVersion();
        for (String key : prefs.getAll().keySet()) {
            // This key should not be present anyway, but just in case it is don't remove it
            if(!key.equals(StoredData.LATESTVERSION)) {
                edit.remove(key);
            }
        }
        // Use commit to be sure changes finish before
        edit.apply();
    }

    // Getters/setters for specific attributes

    public void setProfileName(String VIN, String token) {
        mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit().putString(PROFILENAME, token).apply();
    }

    public String getProfileName(String VIN) {
        return mContext.getSharedPreferences(VIN, MODE_PRIVATE).getString(PROFILENAME, "");
    }

    public void setLastUpdateTime(String VIN ) {
        long nowtime = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit().putLong(LASTUPDATETIME, nowtime).apply();
    }

    public void setLastUpdateTime(String VIN, long nowtime ) {
        mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit().putLong(LASTUPDATETIME, nowtime).apply();
    }

    public long getLastUpdateTime(String VIN) {
        return mContext.getSharedPreferences(VIN, MODE_PRIVATE).getLong(LASTUPDATETIME, 0);
    }

    public long getLastUpdateElapsedTime() {
        String VIN = PreferenceManager.getDefaultSharedPreferences(mContext).getString(mContext.getResources().getString(R.string.VIN_key), "");
        long lastUpdate = mContext.getSharedPreferences(VIN, MODE_PRIVATE).getLong(LASTUPDATETIME, 0);
        long nowtime = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return nowtime - lastUpdate;
    }

    public String getAccessToken(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(ACCESSTOKEN, "");
    }

    public void setAccessToken(String VIN, String token) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(ACCESSTOKEN, token);
        edit.commit();
    }

    public String getRefreshToken(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(REFRESHTOKEN, "");
    }

    public void setRefreshToken(String VIN, String token) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(REFRESHTOKEN, token);
        edit.commit();
    }

    public ProgramStateMachine.States getProgramState(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return ProgramStateMachine.States.valueOf(pref.getString(PROGRAMSTATE,
                ProgramStateMachine.States.INITIAL_STATE.name()));
    }

    public void setProgramState(String VIN, ProgramStateMachine.States state) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PROGRAMSTATE, state.name());
        edit.commit();
    }

    public CarStatus getCarStatus(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return gson.fromJson(pref.getString(CARSTATUS, "{}"), CarStatus.class);
    }

    public void setCarStatus(String VIN, CarStatus status) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(CARSTATUS, gson.toJson(status));
        edit.commit();
        CarStatusWidget.clearAwaitingFlag();
    }

    public OTAStatus getOTAStatus(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return gson.fromJson(pref.getString(OTASTATUS, "{}"), OTAStatus.class);
    }

    public void setOTAStatus(String VIN, OTAStatus status) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(OTASTATUS, gson.toJson(status));
        edit.commit();
    }

    public String getHVBStatus(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(HVBSTATUS, "STATUS_GOOD");
    }

    public void setHVBStatus(String VIN, String status) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(HVBSTATUS, status);
        edit.commit();
    }

    public String getTPMSStatus(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(TPMSSTATUS, "STATUS_GOOD");
    }

    public void setTPMSStatus(String VIN, String status) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(TPMSSTATUS, status);
        edit.commit();
    }

    public long getTokenTimeout(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getLong(TOKENTIMEOUT, 0);
    }

    public void setTokenTimeout(String VIN, long time) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putLong(TOKENTIMEOUT, time);
        edit.commit();
    }

    public String getLanguage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(LANGUAGE, "");
    }

    public void setLanguage(String VIN, String language) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(LANGUAGE, language);
        edit.commit();
    }

    public String getCountry(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(COUNTRY, "");
    }

    public void setCountry(String VIN, String country) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(COUNTRY, country);
        edit.commit();
    }

    public String getTimeFormatByCountry(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(COUNTRY, "USA").equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
    }

    public String getSpeedUnits(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(SPEEDUNITS, "MPH");
    }

    public void setSpeedUnits(String VIN, String units) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(SPEEDUNITS, units);
        edit.commit();
    }

    public int getDistanceUnits(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getInt(DISTANCEUNITS, -1);
    }

    public void setDistanceUnits(String VIN, int units) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt(DISTANCEUNITS, units);
        edit.commit();
    }

    public String getPressureUnits(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(PRESSUREUNITS, "PSI");
    }

    public void setPressureUnits(String VIN, String units) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PRESSUREUNITS, units);
        edit.commit();
    }

    public String getLeftAppPackage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(LEFTAPPPACKAGE, "com.ford.fordpass");
    }

    public void setLeftAppPackage(String VIN, String name) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(LEFTAPPPACKAGE, name);
        edit.commit();
    }

    public String getRightAppPackage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(RIGHTAPPPACKAGE, null);
    }

    public void setRightAppPackage(String VIN, String name) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(RIGHTAPPPACKAGE, name);
        edit.commit();
    }

    public String getLatestVersion() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(LATESTVERSION, "");
    }

    public void setLatestVersion(String name) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(LATESTVERSION, name);
        edit.commit();
    }

    public int getCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public void resetCounters() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt(GOOD, 0);
        edit.putInt(BAD, 0);
        edit.putInt(UGLY, 0);
        edit.apply();
    }

    public int incCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        int value = pref.getInt(key, 0) + 1;
        edit.putInt(key, value).apply();
        return value;
    }
}
