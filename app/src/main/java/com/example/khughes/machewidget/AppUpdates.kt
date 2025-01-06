package com.example.khughes.machewidget

import android.content.Context
import androidx.preference.PreferenceManager
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

            // TODO: insert correct release date heer
            if (lastVersion < "2025.10.08") {

                // The old databases aren't used anymore; delete them.
                context.deleteDatabase("vehicle_info_db")
                context.deleteDatabase("user_info_db")

                // If user had hibernated the app, remove hibernation and inform them it's alive again
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                val hibernatingKey = context.resources.getString(R.string.hibernate_api_key)

                val hibernating = prefs.getBoolean(hibernatingKey, false)
                if (hibernating) {
                    Notifications.hibernateOver(context)
                    prefs.edit().remove(hibernatingKey).commit()
                }
            }
        }
        // Update internally
        prefs.edit().putString(context.resources.getString(R.string.last_version_key), BuildConfig.VERSION_NAME).commit()
    }
}