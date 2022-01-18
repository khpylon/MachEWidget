package com.example.khughes.machewidget;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StoredData {
    private static final String TAG = "saveAppInfo";
    private static final String ACCESSTOKEN = "AccessToken";
    private static final String REFRESHTOKEN = "RefreshToken";
    private static final String PROGRAMSTATE = "ProgramState";
    private static final String TOKENTIMEOUT = "TokenTimeout";
    private static final String CARSTATUS = "CarStatus";
    private static final String OTASTATUS = "OTAStatus";
    private static final String COUNTRY = "Country";
    private static final String LANGUAGE = "Language";
    private static final String SPEEDUNITS = "SpeedUnits";
    private static final String DISTANCEUNITS = "DistanceUnits";
    private static final String PRESSUREUNITS = "PressureUnits";
    public static final String GOOD = "Good";
    public static final String BAD = "Bad";
    public static final String UGLY = "Ugly";

    private Context mContext;
    private Gson gson = new GsonBuilder().create();

    public StoredData(Context context) {
        mContext = context;
    }

    public String getAccessToken() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getString(ACCESSTOKEN, "");
    }

    public void setAccessToken(String token) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(ACCESSTOKEN, token);
        edit.commit();
    }

    public String getRefreshToken() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getString(REFRESHTOKEN, "");
    }

    public void setRefreshToken(String token) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(REFRESHTOKEN, token);
        edit.commit();
    }

    public ProgramStateMachine.States getProgramState() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return ProgramStateMachine.States.valueOf(pref.getString(PROGRAMSTATE,
                ProgramStateMachine.States.INITIAL_STATE.name()));
    }

    public void setProgramState(ProgramStateMachine.States state) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PROGRAMSTATE, state.name());
        edit.commit();
    }

    public CarStatus getCarStatus() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return gson.fromJson(pref.getString(CARSTATUS, "{}"), CarStatus.class);
    }

    public void setCarStatus(CarStatus status) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(CARSTATUS, gson.toJson(status));
        edit.commit();
    }

    public OTAStatus getOTAStatus() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return gson.fromJson(pref.getString(OTASTATUS, "{}"), OTAStatus.class);
    }

    public void setOTAStatus(OTAStatus status) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(OTASTATUS, gson.toJson(status));
        edit.commit();
    }

    public long getTokenTimeout() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getLong(TOKENTIMEOUT, 0);
    }

    public void setTokenTimeout(long time) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putLong(TOKENTIMEOUT, time);
        edit.commit();
    }

    public String getLanguage() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getString(LANGUAGE, "");
    }

    public void setLanguage(String language) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(LANGUAGE, language);
        edit.commit();
    }

    public String getCountry() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getString(COUNTRY, "");
    }

    public void setCountry(String country) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(COUNTRY, country);
        edit.commit();
    }
    
    public String getSpeedUnits() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getString(SPEEDUNITS, "MPH");
    }

    public void setSpeedUnits(String units) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(SPEEDUNITS, units);
        edit.commit();
    }

    public int getDistanceUnits() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getInt(DISTANCEUNITS, -1);
    }

    public void setDistanceUnits(int units) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt(DISTANCEUNITS, units);
        edit.commit();
    }

    public String getPressureUnits() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getString(PRESSUREUNITS,"PSI");
    }

    public void setPressureUnits(String units) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(PRESSUREUNITS, units);
        edit.commit();
    }

    public int getCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public void resetCounters() {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt(GOOD, 0);
        edit.putInt(BAD, 0);
        edit.putInt(UGLY, 0);
        edit.apply();
    }

    public int incCounter(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        int value = pref.getInt(key, 0) + 1;
        edit.putInt(key, value);
        edit.apply();
        return value;
    }
}
