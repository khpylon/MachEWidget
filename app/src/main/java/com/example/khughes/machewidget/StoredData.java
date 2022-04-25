package com.example.khughes.machewidget;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.CarStatus.CarStatus;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.compress.utils.Charsets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

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
    private static final String CHARGESTATION = "ChargeStation";
    private static final String HVBSTATUS = "HVBStatus";
    private static final String TPMSSTATUS = "TPMSStatus";
    private static final String COUNTRY = "Country";
    private static final String LANGUAGE = "Language";
    private static final String SPEEDUNITS = "SpeedUnits";
    private static final String DISTANCEUNITS = "DistanceUnits";
    private static final String PRESSUREUNITS = "PressureUnits";
    private static final String LASTFUELLEVEL = "LastFuelLevel";
    private static final String LASTDTE = "LastDTE";
    private static final String LASTUPDATETIME = "LastUpdateTime";
    private static final String LASTREFRESHTIME = "LastRefreshTime";
    private static final String LASTALARMTIME = "LastAlarmTime";
    private static final String LEFTAPPPACKAGE = "LeftAppPackage";
    private static final String RIGHTAPPPACKAGE = "RightAppPackage";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String WIDGETMODE = "widgetmode";

    private static final String LATESTVERSION = "LatestVersion";

    public static final String GOOD = "Good";
    public static final String BAD = "Bad";
    public static final String UGLY = "Ugly";

    public static final String STATUS_NOT_LOGGED_IN = "Logged out";
    public static final String STATUS_LOG_OUT = "Log out";
    public static final String STATUS_LOG_IN = "Log in";
    public static final String STATUS_UPDATED = "Updated";
    public static final String STATUS_UNKNOWN = "Unknown";

    private final Context mContext;
    private final Gson gson = new GsonBuilder().create();

    public StoredData(Context context) {
        mContext = context;
    }

    public enum StringCompressor {
        ;

        public static byte[] compress(String text) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                OutputStream out = new DeflaterOutputStream(baos);
                out.write(text.getBytes(StandardCharsets.UTF_8));
                out.close();
            } catch (IOException e) {
                throw new AssertionError(e);
            }
            return baos.toByteArray();
        }

        public static String decompress(byte[] bytes) {
            InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                return new String(baos.toByteArray(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
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
            File f = new File(mContext.getDataDir(), "/shared_prefs");
            f = new File(f, VIN + ".xml");
            f.delete();
        }
    }

    // Remove all keys not specific to a profile (pretty much everything)
    public void deleteOldCredentials() {
        SharedPreferences prefs = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        // Keep the latest version info
        File f = new File(mContext.getDataDir(), "/shared_prefs");
        String t;
        for (File m : f.listFiles()) {
            t = m.getName().toUpperCase();
            if (t.toUpperCase().startsWith("3FMT") && t.toUpperCase().endsWith(".XML")) {
                System.out.println(t);
            }
        }
        for (String key : prefs.getAll().keySet()) {
            // This key should not be present anyway, but just in case it is don't remove it
            if (!key.equals(StoredData.LATESTVERSION)) {
                edit.remove(key);
            }
        }
        // Use commit to be sure changes finish before
        commitWait(edit);
    }

    // Getters/setters for specific attributes

    private void commitWait(SharedPreferences.Editor edit) {
        for (int i = 0; i < 10; ++i) {
            if (edit.commit()) {
                return;
            }
        }
    }

//    public void setProfileName(String VIN, String name) {
//        commitWait(mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit().putString(PROFILENAME, name));
//    }
//
//    public String getProfileName(String VIN) {
//        return mContext.getSharedPreferences(VIN, MODE_PRIVATE).getString(PROFILENAME, "");
//    }
//
//    public long getLastUpdateTime(String VIN) {
//        return mContext.getSharedPreferences(VIN, MODE_PRIVATE).getLong(LASTUPDATETIME, 0);
//    }

    public long getLastUpdateElapsedTime() {
        String VIN = PreferenceManager.getDefaultSharedPreferences(mContext).getString(mContext.getResources().getString(R.string.VIN_key), "");
        long lastUpdate = mContext.getSharedPreferences(VIN, MODE_PRIVATE).getLong(LASTUPDATETIME, 0);
        long nowtime = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return nowtime - lastUpdate;
    }

//    public void setLastRefreshTime(String VIN, long time) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        commitWait(edit.putLong(LASTREFRESHTIME, time));
//    }
//
//    public long getLastRefreshTime(String VIN) {
//        return mContext.getSharedPreferences(VIN, MODE_PRIVATE).getLong(LASTREFRESHTIME, 0);
//    }
//

//    public String getAccessToken(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(ACCESSTOKEN, "");
//    }
//
//    public String getRefreshToken(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(REFRESHTOKEN, "");
//    }
//
//    public long getTokenTimeout(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getLong(TOKENTIMEOUT, 0);
//    }
//
//    public void setTokenInfo(String VIN, String access, String refresh, long time) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(ACCESSTOKEN, access);
//        edit.putString(REFRESHTOKEN, refresh);
//        commitWait(edit.putLong(TOKENTIMEOUT, time));
//    }
//
//    public String getProgramState(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(PROGRAMSTATE, Constants.STATE_INITIAL_STATE);
//    }
//
//    public void setProgramState(String VIN, String state) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        commitWait(edit.putString(PROGRAMSTATE, state));
//    }
//
//    public CarStatus getCarStatus(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        String status = pref.getString(CARSTATUS, "{}");
//        if (!status.equals("{}") && !status.contains("vehiclestatus")) {
//            status = StringCompressor.decompress(status.getBytes(Charsets.ISO_8859_1));
//        }
//        return gson.fromJson(status, CarStatus.class);
//    }
//
//    public void setCarStatus(String VIN, CarStatus status) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        String str = new String(StoredData.StringCompressor.compress(gson.toJson(status)), Charsets.ISO_8859_1);
//        edit.putString(CARSTATUS, str);
//        long nowtime = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//        edit.putLong(LASTUPDATETIME, nowtime);
//        commitWait(edit);
//        CarStatusWidget.clearAwaitingFlag();
//    }
//
//    public OTAStatus getOTAStatus(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        String status = pref.getString(OTASTATUS, "{}");
//        if (!status.equals("{}") && !status.contains("otaAlertStatus")) {
//            status = StringCompressor.decompress(status.getBytes(Charsets.ISO_8859_1));
//        }
//        return gson.fromJson(status, OTAStatus.class);
//    }
//
//    public void setOTAStatus(String VIN, OTAStatus status) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        String str = new String(StoredData.StringCompressor.compress(gson.toJson(status)), Charsets.ISO_8859_1);
//        edit.putString(OTASTATUS, str);
//        commitWait(edit);
//    }
//
//    public ChargeStation getChargeStation(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        String status = pref.getString(CHARGESTATION, "{}");
//        if (!status.equals("{}") && !status.contains("otaAlertStatus")) {
//            status = StringCompressor.decompress(status.getBytes(Charsets.ISO_8859_1));
//        }
//        return gson.fromJson(status, ChargeStation.class);
//    }
//
//    public void setChargeStation(String VIN, ChargeStation station) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        String str = new String(StoredData.StringCompressor.compress(gson.toJson(station)), Charsets.ISO_8859_1);
//        edit.putString(CHARGESTATION, str);
//        commitWait(edit);
//    }
//
//    public String getHVBStatus(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(HVBSTATUS, "STATUS_GOOD");
//    }
//
//    public void setHVBStatus(String VIN, String status) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(HVBSTATUS, status);
//        commitWait(edit);
//    }
//
//    public String getTPMSStatus(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(TPMSSTATUS, "STATUS_GOOD");
//    }
//
//    public void setTPMSStatus(String VIN, String status) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(TPMSSTATUS, status);
//        commitWait(edit);
//    }
//
//    public String getLanguage(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(LANGUAGE, "");
//    }
//
//    public void setLanguage(String VIN, String language) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(LANGUAGE, language);
//        commitWait(edit);
//    }
//
//    public String getCountry(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(COUNTRY, "");
//    }
//
//    public void setCountry(String VIN, String country) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(COUNTRY, country);
//        commitWait(edit);
//    }
//
//    public String getTimeFormatByCountry(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(COUNTRY, "USA").equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
//    }
//
//    public String getSpeedUnits(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(SPEEDUNITS, "MPH");
//    }
//
//    public void setSpeedUnits(String VIN, String units) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(SPEEDUNITS, units);
//        commitWait(edit);
//    }
//
//    public int getDistanceUnits(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getInt(DISTANCEUNITS, -1);
//    }
//
//    public void setDistanceUnits(String VIN, int units) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putInt(DISTANCEUNITS, units);
//        commitWait(edit);
//    }
//
//    public String getPressureUnits(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(PRESSUREUNITS, "PSI");
//    }
//
//    public void setPressureUnits(String VIN, String units) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(PRESSUREUNITS, units);
//        commitWait(edit);
//    }
//
//    public Double getLastFuelLevel(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return Float.valueOf(pref.getFloat(LASTFUELLEVEL, 0)).doubleValue();
//    }
//
//    public void setLastFuelLevel(String VIN, Double level) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putFloat(LASTFUELLEVEL, level.floatValue());
//        commitWait(edit);
//    }
//
//    public Double getLastDTE(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return Float.valueOf(pref.getFloat(LASTDTE, 0)).doubleValue();
//    }
//
//    public void setLastDTE(String VIN, Double distance) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putFloat(LASTDTE, distance.floatValue());
//        commitWait(edit);
//    }
//
//    public String getLeftAppPackage(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(LEFTAPPPACKAGE, "com.ford.fordpass");
//    }
//
//    public void setLeftAppPackage(String VIN, String name) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(LEFTAPPPACKAGE, name);
//        commitWait(edit);
//    }
//
//    public String getRightAppPackage(String VIN) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        return pref.getString(RIGHTAPPPACKAGE, null);
//    }
//
//    public void setRightAppPackage(String VIN, String name) {
//        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
//        edit.putString(RIGHTAPPPACKAGE, name);
//        commitWait(edit);
//    }

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

//    private void createPassword() {
//        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
//        if (!pref.contains(PASSWORD)) {
//            byte[] array = new byte[64];
//            new Random().nextBytes(array);
//            commitWait(pref.edit().putString(PASSWORD, new String(array, StandardCharsets.UTF_8)));
//        }
//    }
//
//
//    private String getCryptoString(String VIN, String tag) {
//        String value = "";
//
//        // Just in case, make sure we're supposed to save this
//        boolean savingCredentials = PreferenceManager.getDefaultSharedPreferences(mContext)
//                .getBoolean(mContext.getResources().getString(R.string.save_credentials_key), true);
//        if (savingCredentials) {
//            createPassword();
//            SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
//            String password = pref.getString(PASSWORD, null);
//            pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//            String cipher = pref.getString(tag, null);
//            if (password != null && cipher != null) {
//                try {
//                    value = NetworkCalls.decrypt(password.toCharArray(), cipher);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return value;
//    }
//
//    private void setCryptoString(String VIN, String tag, String value) {
//
//        // Just in case, make sure we're supposed to save this
//        boolean savingCredentials = PreferenceManager.getDefaultSharedPreferences(mContext)
//                .getBoolean(mContext.getResources().getString(R.string.save_credentials_key), true);
//        if (savingCredentials) {
//            createPassword();
//            SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
//            String password = pref.getString(PASSWORD, null);
//            pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//            if (password != null) {
//                try {
//                    String cipher = NetworkCalls.encrypt(password.toCharArray(), value);
//                    commitWait(pref.edit().putString(tag, cipher));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void clearCryptoString(String VIN, String tag) {
//        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
//        if (pref.contains(tag)) {
//            commitWait(pref.edit().remove(tag));
//        }
//    }
//
//    public String getUsername(String VIN) {
//        return getCryptoString(VIN, USERNAME);
//    }
//
//    public void setUsername(String VIN, String name) {
//        setCryptoString(VIN, USERNAME, name);
//    }
//
//    public String getPassword(String VIN) {
//        return getCryptoString(VIN, PASSWORD);
//    }
//
//    public void setPassword(String VIN, String name) {
//        setCryptoString(VIN, PASSWORD, name);
//    }
//
//    public void clearUsernameAndPassword() {
//        for (String VIN : getProfiles()) {
//            clearCryptoString(VIN, USERNAME);
//            clearCryptoString(VIN, PASSWORD);
//        }
//    }

    public String getLatestVersion() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(LATESTVERSION, "");
    }

    public void setLatestVersion(String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        edit.putString(LATESTVERSION, name);
        commitWait(edit);
    }

    public String getWidgetMode() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(WIDGETMODE, Utils.WIDGETMODE_MACHE);
    }

    public void setWidgetMode(String mode) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        edit.putString(WIDGETMODE, mode);
        commitWait(edit);
    }

    public void setLastAlarmTime() {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        long nowtime = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        commitWait(edit.putLong(LASTALARMTIME, nowtime));
    }

    public long getLastAlarmTime() {
        long nowtime = mContext.getSharedPreferences(TAG, MODE_PRIVATE).getLong(LASTALARMTIME, 0);
        return nowtime;
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
