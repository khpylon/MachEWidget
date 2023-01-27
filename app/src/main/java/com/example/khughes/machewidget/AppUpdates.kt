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

                // If applicable, change update frequency to 15 minute minimum
                val delayInMillis = prefs.getString(
                    context.resources.getString(R.string.update_frequency_key),
                    "15"
                )!!.toInt()
                if( delayInMillis > 0 && delayInMillis < 15)
                {
                    prefs.edit()
                        .putString(
                            context.resources.getString(R.string.update_frequency_key),
                            "15"
                        )
                        .commit()
                }
            }

            // Convert old unit display settings to new settings
            if (lastVersion < "2023.01.27") {
                Misc.updateUnits(context)
            }

        }

        // Update internally
        prefs.edit().putString(context.resources.getString(R.string.last_version_key), BuildConfig.VERSION_NAME).commit()
    }
}