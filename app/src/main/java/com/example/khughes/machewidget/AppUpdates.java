package com.example.khughes.machewidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUpdates {

    // This method is intended to bundle various changes from older versions to the most recent.
    public static void performUpdates(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastVersion = prefs.getString(context.getResources().getString(R.string.last_version_key), "");

        // See if we need to upgrade anything since the last version
        if (!lastVersion.equals("") && BuildConfig.VERSION_NAME.compareTo(lastVersion) > 0) {
            LogFile.i(context, MainActivity.CHANNEL_ID, "running updates");

            // Add operations here

            // Replace sharedpreference files with databases
            if (lastVersion.compareTo("2022.04.29") < 0) {
                migrateToDatabases(context);
            }

            // Make sure MainActivity is enabled
            if (lastVersion.compareTo("2022.05.12") < 0) {

                PackageManager manager = context.getPackageManager();
                String packageName = context.getPackageName();

                Map<String, Boolean> results = new HashMap<>();
                results.put(".MainActivity", true);
                results.put(".F150MainActivity", false);
                results.put(".BroncoMainActivity", false);
                results.put(".ExplorerMainActivity", false);
                try {
                    for (String activity : results.keySet()) {
                        manager.setComponentEnabledSetting(new ComponentName(packageName, packageName + activity),
                                results.get(activity) ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                    }
                    // Give the OS some time to finish reconfiguring things
                    Thread.sleep(1500);
                } catch (Exception e) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "exception in CarStatusWidget_5x5.matchWidgetWithVin()" + e);
                }
            }

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion.compareTo("2022.05.25") < 0) {
                LogFile.d(context, MainActivity.CHANNEL_ID, "running 2022.05.25 updates");

                PreferenceManager.setDefaultValues(context, R.xml.settings_preferences, true);
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String VIN = sharedPrefs.getString(context.getResources().getString(R.string.VIN_key), null);

                new Thread(() -> {
                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao().updateSupportOTA();
                    if (VIN != null) {
                        VehicleInfo info = VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfoByVIN(VIN);
                        String userId = info.getUserId();
                        // Some vehicle entries had missing userID value.  If so, get the user ID from the first entry
                        // of the user database and update all vehicles
                        if (userId == null) {
                            LogFile.d(context, MainActivity.CHANNEL_ID, "2022.05.25 update: adding user ID to vehicles");
                            List<UserInfo> userInfo = UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo();
                            if (!userInfo.isEmpty()) {
                                userId = userInfo.get(0).getUserId();
                                for (VehicleInfo vehInfo : VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo()) {
                                    vehInfo.setUserId(userId);
                                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao().updateVehicleInfo(vehInfo);
                                }
                            }
                        }
                        sharedPrefs.edit().putString(context.getResources().getString(R.string.userId_key), userId).commit();
                    }
                }).start();
            }

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion.compareTo("2022.05.31") < 0 || lastVersion.compareTo("2022.06.04a") < 0) {
                PreferenceManager.setDefaultValues(context, R.xml.settings_preferences, true);
            }

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion.compareTo("2022.06.20") < 0) {
                new Thread(() -> {
                    for (UserInfo userInfo : UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo()) {
                        userInfo.setProgramState(Constants.STATE_HAVE_TOKEN_AND_VIN);
                        UserInfoDatabase.getInstance(context).userInfoDao().updateUserInfo(userInfo);
                    }
                }).start();
            }

            // Reload vehicle images, including angles
            if (lastVersion.compareTo("2022.07.16") < 0) {
                File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
                if (imageDir.exists() && imageDir.isDirectory()) {
                    for (String file : imageDir.list()) {
                        new File(imageDir, file).delete();
                    }
                }
                new Thread(() -> {
                    for (VehicleInfo vehicleInfo : VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo()) {
                        UserInfo user = UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo(vehicleInfo.getUserId());
                        if (user != null) {
                            NetworkCalls.getVehicleImage(context, user.getAccessToken(), vehicleInfo.getVIN(), user.getCountry());
                        }
                    }
                }).start();
            }
        }

        // Update internally
        prefs.edit().putString(context.getResources().getString(R.string.last_version_key), BuildConfig.VERSION_NAME).commit();
    }

    private static void migrateToDatabases(Context context) {

        // Turn off profiles
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String currentVIN = prefs.getString(context.getResources().getString(R.string.VIN_key), "");
        prefs.edit().putBoolean(context.getResources().getString(R.string.show_profiles_key), false).apply();

        StoredData appInfo = new StoredData(context);

        // If stored credentials are active, get user and vehicle info for each VIN and
        // set up databases

        boolean savingCredentials = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.save_credentials_key), true);

        appInfo.setLeftAppPackage(appInfo.getLeftAppPackage(currentVIN));
        appInfo.setRightAppPackage(appInfo.getRightAppPackage(currentVIN));

        prefs.edit().putString(context.getResources().getString(R.string.VIN_key), "").apply();
        if (savingCredentials) {
            // Clear current VIN

            for (String VIN : appInfo.getProfiles()) {
                String username = appInfo.getUsername(VIN);
                String password = appInfo.getPassword(VIN);
                Handler h = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                    }
                };
                NetworkCalls.getAccessToken(h, context, username, password);
                appInfo.removeProfile(VIN);
            }
        }

        // if store credentials aren't available, add the current VIN file to vehicle database
        // and assign to a temp user ID
        else {

            ArrayList<String> VINs = appInfo.getProfiles();
            UserInfo userInfo = new UserInfo();

            if (!VINs.isEmpty()) {
                LogFile.i(context, MainActivity.CHANNEL_ID, "creating temporary user profile");
                String userId = Constants.TEMP_ACCOUNT;
                userInfo.setUserId(userId);
                userInfo.setAccessToken(appInfo.getAccessToken(currentVIN));
                userInfo.setRefreshToken(appInfo.getRefreshToken(currentVIN));
                userInfo.setCountry(appInfo.getCountry(currentVIN));
                userInfo.setUsername("");
                userInfo.setPassword("");
                userInfo.setProgramState(appInfo.getProgramState(currentVIN));
                userInfo.setAccessToken(appInfo.getAccessToken(currentVIN));
                userInfo.setRefreshToken(appInfo.getRefreshToken(currentVIN));
                userInfo.setExpiresIn(appInfo.getTokenTimeout(currentVIN));
                userInfo.setCountry(appInfo.getCountry(currentVIN));
                userInfo.setLanguage(appInfo.getLanguage(currentVIN));
                userInfo.setUomSpeed(appInfo.getSpeedUnits(currentVIN));
                userInfo.setUomDistance(appInfo.getDistanceUnits(currentVIN));
                userInfo.setUomPressure(appInfo.getPressureUnits(currentVIN));
                new Thread(() -> {
                    UserInfoDatabase.getInstance(context)
                            .userInfoDao()
                            .insertUserInfo(userInfo);
                    LogFile.i(context, MainActivity.CHANNEL_ID, "temporary user profile completed");

                    Handler h1 = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            Bundle bundle = msg.getData();
                            String userId = bundle.getString("userId");
                            for (String VIN : VINs) {
                                Handler h = new Handler(Looper.getMainLooper()) {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        LogFile.i(context, MainActivity.CHANNEL_ID, "handler returned " + VIN);
                                    }
                                };
                                NetworkCalls.getUserVehicles(h, context, userId);
                                LogFile.i(context, MainActivity.CHANNEL_ID, "processing VIN " + VIN);
                                appInfo.removeProfile(VIN);
                            }
                        }
                    };

                    NetworkCalls.refreshAccessToken(h1, context, userId, userInfo.getRefreshToken());

                }).start();
            }
        }

        // Remove VINs.xml file
        new File(context.getDataDir() + File.separator + Constants.SHAREDPREFS_FOLDER, StoredData.VINLIST + ".xml").deleteOnExit();
        LogFile.i(context, MainActivity.CHANNEL_ID, "database conversion completed");

        // Do a refresh in 5 seconds
        StatusReceiver.nextAlarm(context, 5);
    }

}
