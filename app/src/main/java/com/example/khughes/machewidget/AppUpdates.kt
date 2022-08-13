package com.example.khughes.machewidget;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.io.File;
import java.util.List;

public class AppUpdates {

    // This method is intended to bundle various changes from older versions to the most recent.
    public static void performUpdates(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastVersion = prefs.getString(context.getResources().getString(R.string.last_version_key), "");

        // See if we need to upgrade anything since the last version
        if (!lastVersion.equals("") && BuildConfig.VERSION_NAME.compareTo(lastVersion) > 0) {
            LogFile.i(context, MainActivity.CHANNEL_ID, "running updates");

            // Add operations here

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

}
