package com.example.khughes.machewidget;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.compress.utils.Charsets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    public enum StringCompressor {
        ;
        public static byte[] compress(String text) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                OutputStream out = new DeflaterOutputStream(baos);
                out.write(text.getBytes("UTF-8"));
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
                while((len = in.read(buffer))>0)
                    baos.write(buffer, 0, len);
                return new String(baos.toByteArray(), "UTF-8");
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
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
        edit.commit();
    }

    public void addProfile(String VIN, String profileName) {
        // Just in case, remove old info if it exists
        removeProfile(VIN);

        // Add the VIN to profile lsit
        mContext.getSharedPreferences(VINLIST, MODE_PRIVATE).edit().putInt(VIN, 0).commit();

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
            mContext.getSharedPreferences(VINLIST, MODE_PRIVATE).edit().remove(VIN).commit();

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
        edit.commit();
    }

    // Getters/setters for specific attributes

    public void setProfileName(String VIN, String token) {
        mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit().putString(PROFILENAME, token).commit();
    }

    public String getProfileName(String VIN) {
        return mContext.getSharedPreferences(VIN, MODE_PRIVATE).getString(PROFILENAME, "");
    }

    public void setLastUpdateTime(String VIN ) {
        long nowtime = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit().putLong(LASTUPDATETIME, nowtime).commit();
    }

    public void setLastUpdateTime(String VIN, long nowtime ) {
        mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit().putLong(LASTUPDATETIME, nowtime).commit();
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
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(ACCESSTOKEN, token).commit();
    }

    public String getRefreshToken(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(REFRESHTOKEN, "");
    }

    public void setRefreshToken(String VIN, String token) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(REFRESHTOKEN, token).commit();
    }

    public ProgramStateMachine.States getProgramState(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return ProgramStateMachine.States.valueOf(pref.getString(PROGRAMSTATE,
                ProgramStateMachine.States.INITIAL_STATE.name()));
    }

    public void setProgramState(String VIN, ProgramStateMachine.States state) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(PROGRAMSTATE, state.name()).commit();
    }

    public CarStatus getCarStatus(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        String status = pref.getString(CARSTATUS, "{}");
        if (!status.equals("{}") && !status.contains("vehiclestatus")) {
            status = StringCompressor.decompress(status.getBytes(Charsets.ISO_8859_1));
        }
        return gson.fromJson(status, CarStatus.class);
    }

    public void setCarStatus(String VIN, CarStatus status) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        String str = new String(StoredData.StringCompressor.compress(gson.toJson(status)), Charsets.ISO_8859_1);
        edit.putString(CARSTATUS, str).commit();
        CarStatusWidget.clearAwaitingFlag();
    }

    public OTAStatus getOTAStatus(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        String status= pref.getString(OTASTATUS, "{}");
        if(!status.equals("{}") && !status.contains("otaAlertStatus")) {
            status = StringCompressor.decompress(status.getBytes(Charsets.ISO_8859_1));
        }
        return gson.fromJson(status, OTAStatus.class);
    }

    public void setOTAStatus(String VIN, OTAStatus status) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        String str = new String(StoredData.StringCompressor.compress(gson.toJson(status)), Charsets.ISO_8859_1);
        edit.putString(OTASTATUS, str).commit();
    }

    public String getHVBStatus(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(HVBSTATUS, "STATUS_GOOD");
    }

    public void setHVBStatus(String VIN, String status) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(HVBSTATUS, status).commit();
    }

    public String getTPMSStatus(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(TPMSSTATUS, "STATUS_GOOD");
    }

    public void setTPMSStatus(String VIN, String status) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(TPMSSTATUS, status).commit();
    }

    public long getTokenTimeout(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getLong(TOKENTIMEOUT, 0);
    }

    public void setTokenTimeout(String VIN, long time) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putLong(TOKENTIMEOUT, time).commit();
    }

    public String getLanguage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(LANGUAGE, "");
    }

    public void setLanguage(String VIN, String language) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(LANGUAGE, language).commit();
    }

    public String getCountry(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(COUNTRY, "");
    }

    public void setCountry(String VIN, String country) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(COUNTRY, country).commit();
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
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(SPEEDUNITS, units).commit();
    }

    public int getDistanceUnits(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getInt(DISTANCEUNITS, -1);
    }

    public void setDistanceUnits(String VIN, int units) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putInt(DISTANCEUNITS, units).commit();
    }

    public String getPressureUnits(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(PRESSUREUNITS, "PSI");
    }

    public void setPressureUnits(String VIN, String units) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(PRESSUREUNITS, units).commit();
    }

    public String getLeftAppPackage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(LEFTAPPPACKAGE, "com.ford.fordpass");
    }

    public void setLeftAppPackage(String VIN, String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(LEFTAPPPACKAGE, name).commit();
    }

    public String getRightAppPackage(String VIN) {
        SharedPreferences pref = mContext.getSharedPreferences(VIN, MODE_PRIVATE);
        return pref.getString(RIGHTAPPPACKAGE, null);
    }

    public void setRightAppPackage(String VIN, String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(VIN, MODE_PRIVATE).edit();
        edit.putString(RIGHTAPPPACKAGE, name).commit();
    }

    public String getLatestVersion() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(LATESTVERSION, "");
    }

    public void setLatestVersion(String name) {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        edit.putString(LATESTVERSION, name).commit();
    }

    public int getCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public void resetCounters() {
        SharedPreferences.Editor edit = mContext.getSharedPreferences(TAG, MODE_PRIVATE).edit();
        edit.putInt(GOOD, 0);
        edit.putInt(BAD, 0);
        edit.putInt(UGLY, 0);
        edit.commit();
    }

    public int incCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        int value = pref.getInt(key, 0) + 1;
        edit.putInt(key, value).commit();
        return value;
    }
}
