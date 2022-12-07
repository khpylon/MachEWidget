package com.example.khughes.machewidget

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.db.UserInfoDatabase
import com.example.khughes.machewidget.db.VehicleInfoDatabase
import java.io.File

object AppUpdates {
    // This method is intended to bundle various changes from older versions to the most recent.
    @JvmStatic
    fun performUpdates(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val lastVersion =
            prefs.getString(context.resources.getString(R.string.last_version_key), "") as String

        // See if we need to upgrade anything since the last version
        if (lastVersion != "" && BuildConfig.VERSION_NAME > lastVersion) {

            LogFile.i(context, MainActivity.CHANNEL_ID, "running updates")

            // Add operations here

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion < "2022.05.25") {
                LogFile.d(context, MainActivity.CHANNEL_ID, "running 2022.05.25 updates")
                PreferenceManager.setDefaultValues(context, R.xml.settings_preferences, true)
                val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
                val VIN = sharedPrefs.getString(context.resources.getString(R.string.VIN_key), null)
                Thread {
                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao().updateSupportOTA()
                    if (VIN != null) {
                        val info = VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
                            .findVehicleInfoByVIN(VIN)
                        var userId = info.userId
                        // Some vehicle entries had missing userID value.  If so, get the user ID from the first entry
                        // of the user database and update all vehicles
                        if (userId == null) {
                            LogFile.d(
                                context,
                                MainActivity.CHANNEL_ID,
                                "2022.05.25 update: adding user ID to vehicles"
                            )
                            val userInfo =
                                UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo()
                            if (userInfo.isNotEmpty()) {
                                userId = userInfo[0].userId
                                for (vehInfo in VehicleInfoDatabase.getInstance(context)
                                    .vehicleInfoDao().findVehicleInfo()) {
                                    vehInfo.userId = userId
                                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
                                        .updateVehicleInfo(vehInfo)
                                }
                            }
                        }
                        sharedPrefs.edit()
                            .putString(context.resources.getString(R.string.userId_key), userId)
                            .commit()
                    }
                }.start()
            }

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion < "2022.05.31" || lastVersion < "2022.06.04a") {
                PreferenceManager.setDefaultValues(context, R.xml.settings_preferences, true)
            }

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion < "2022.06.20") {
                Thread {
                    for (userInfo in UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo()) {
                        userInfo.programState = Constants.STATE_HAVE_TOKEN_AND_VIN
                        UserInfoDatabase.getInstance(context).userInfoDao().updateUserInfo(userInfo)
                    }
                }.start()
            }

            // Reload vehicle images, including angles
            if (lastVersion < "2022.07.16") {
                val imageDir = File(context.dataDir, Constants.IMAGES_FOLDER)
                if (imageDir.exists() && imageDir.isDirectory) {
                    for (file in imageDir.list()!!) {
                        File(imageDir, file).delete()
                    }
                }
                Thread {
                    for (vehicleInfo in VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
                        .findVehicleInfo()) {
                        val user = UserInfoDatabase.getInstance(context).userInfoDao()
                            .findUserInfo(vehicleInfo.userId)
                        if (user != null) {
                            NetworkCalls.getVehicleImage(
                                context,
                                user.accessToken,
                                vehicleInfo.vin,
                                user.country
                            )
                        }
                    }
                }.start()
            }

            if (lastVersion < "2022.12.07") {
                // Disable saving credentials
                prefs.edit()
                    .putBoolean(
                        context.resources.getString(R.string.save_credentials_key),
                        false
                    )
                    .commit()
            }
        }

        // Update internally
        prefs.edit().putString(context.resources.getString(R.string.last_version_key), BuildConfig.VERSION_NAME).commit()
    }
}