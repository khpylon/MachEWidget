package com.example.khughes.machewidget

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.db.UserInfoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppUpdates {
    // This method is intended to bundle various changes from older versions to the most recent.
    @Suppress("UNUSED_VARIABLE")
    @JvmStatic
    fun performUpdates(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val lastVersion =
            prefs.getString(context.resources.getString(R.string.last_version_key), "") as String

        // See if we need to upgrade anything since the last version
        if (lastVersion != "" && BuildConfig.VERSION_NAME > lastVersion) {

            LogFile.i(context, MainActivity.CHANNEL_ID, "running updates")

            // Add operations here

            // Rename old DCFC *.txt to *.json
            if (lastVersion < "2023.08.03") {
                DCFC.renameLogFiles(context)
            }

            // Remove old notification channels, and remove DCFC session file
            if (lastVersion < "2023.08.13") {
                DCFC.renameLogFiles(context)
                Notifications.removeNotificationChannels(context)
            }

            // Create stored data value reflecting presence of electric vehicles in database
            if (lastVersion < "2023.08.20") {
                CoroutineScope(Dispatchers.IO).launch {
                    val info = InfoRepository(context)
                }
            }

            // Disable automatic forced updates
            if (lastVersion < "2023.10.08") {
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(context.resources.getString(R.string.forceUpdate_key), false)
                    .commit()
            }

            // Reset the program state to require a login
            if (lastVersion < "2024.01.24") {
                CoroutineScope(Dispatchers.IO).launch {
                    val info = InfoRepository(context)
                    val userInfo = info.user
                    userInfo.programState = Constants.STATE_INITIAL_STATE
                    val userInfoDao = UserInfoDatabase.getInstance(context).userInfoDao()
                    userInfoDao.updateUserInfo(userInfo)
                }
            }

            // Try to find any missing vehicle images
            if (lastVersion < "2024.02.01-16") {
                CoroutineScope(Dispatchers.IO).launch {
                    val info = InfoRepository(context)
                    val userInfo = info.user
                    for (vehicle in info.vehicles) {
                        vehicle.vin?.let {vin ->
                            NetworkCalls.getVehicleImage(context, vin, userInfo.country!!)
                        }
                    }
                }
            }
        }


        // Update internally
        prefs.edit().putString(context.resources.getString(R.string.last_version_key), BuildConfig.VERSION_NAME).commit()
    }
}