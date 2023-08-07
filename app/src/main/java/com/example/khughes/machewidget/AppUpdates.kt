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
                if (delayInMillis in 1..14) {
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

            // Remove any keys in widget file that aren't related to VINs. This is related to the
            // version 2023.03.02 bug fix and should have been included in that release.
            if (lastVersion < "2023.03.03") {
                val edit =
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
                        .edit()
                val widgetPrefs =
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
                        .all

                for (item in widgetPrefs) {
                    val key = item.key
                    if (!key.startsWith(Constants.VIN_KEY)) {
                        edit.remove(key)
                    }
                }
                edit.apply()
            }

            // Rename old DCFC *.txt to *.json
            if (lastVersion < "2023.08.03") {
                DCFC.renameLogFiles(context)
            }
        }

        // Update internally
        prefs.edit().putString(context.resources.getString(R.string.last_version_key), BuildConfig.VERSION_NAME).commit()
    }
}