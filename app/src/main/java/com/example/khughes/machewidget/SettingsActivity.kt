package com.example.khughes.machewidget

import android.content.Context
import android.content.Intent
import android.icu.text.MessageFormat
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.khughes.machewidget.StatusReceiver.Companion.nextAlarm

private const val LAUNCH_BATTERY_OPTIMIZATIONS = 1
private lateinit var battery: Preference

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_BATTERY_OPTIMIZATIONS) {
            displayOptimizationMessage(applicationContext)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val context = context as Context

            // If update frequency is changed, sent the info to the Alarm Manager
            var showApps: Preference? =
                findPreference(this.resources.getString(R.string.update_frequency_key))
            showApps?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                    val newInterval = newValue as String?
                    newInterval?.let {
                        nextAlarm( context, newInterval.toInt() )
                    }
                    true
                }

            // Update the widget once the user pick a new unit preference.
            val units: Preference? = findPreference(this.resources.getString(R.string.units_key))
            units?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, _: Any? ->
                    CarStatusWidget.updateWidget(context)
                    true
                }

            // Erase the old log file on enable.
            val verbose: Preference? = findPreference(this.resources.getString(R.string.logging_key))
            verbose?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    if (newValue as Boolean) {
                        LogFile.clearLogFile(context, false)
                    }
                    true
                }

            // Changing any of these preferences requires updating the widget
            for (id in intArrayOf(
                R.string.show_app_links_key,
                R.string.transp_bg_key,
                R.string.enable_commands_key,
                R.string.last_refresh_time_key,
                R.string.show_location_key,
                R.string.user_forcedUpdate_key,
                R.string.use_colors_key,
                R.string.use_image_key
            )) {
                showApps = findPreference(this.resources.getString(id))
                showApps?.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener { preference: Preference ->
                        if (preference.key == resources.getString(R.string.update_frequency_key)) {
                            nextAlarm(context)
                        } else {
                            CarStatusWidget.updateWidget(context)
                        }
                        true
                    }
            }

            // Set app version info
            val version: Preference? = findPreference(this.resources.getString(R.string.version_key))
            version?.summary = BuildConfig.VERSION_NAME + " " + BuildConfig.FLAVOR
            version?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    val appInfo = StoredData(getContext())
                    val units1 = MessageFormat.format(
                        "status = {0}/{1}/{2}/{3}/{4}/{5}",
                        appInfo.getCounter(StoredData.STATUS_NOT_LOGGED_IN),
                        appInfo.getCounter(StoredData.STATUS_LOG_OUT),
                        appInfo.getCounter(StoredData.STATUS_LOG_IN),
                        appInfo.getCounter(StoredData.STATUS_VEHICLE_INFO),
                        appInfo.getCounter(StoredData.STATUS_UPDATED),
                        appInfo.getCounter(StoredData.STATUS_UNKNOWN)
                    )
                    Toast.makeText(getContext(), units1, Toast.LENGTH_LONG).show()
                    false
                }

            // Provide a link to the GitHub repository
            val github: Preference? =
                findPreference(this.resources.getString(R.string.github_repo_key))
            github?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(Constants.REPOURL)
                    startActivity(intent)
                    true
                }

            // Display battery optimization setting and make easier for user to change
            val tmpBattery : Preference? = findPreference(this.resources.getString(R.string.battery_opt_key))
            battery = tmpBattery as Preference
            displayOptimizationMessage(context)
            activity?.let {
                battery.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener { _: Preference? ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                        it.startActivityForResult(
                            intent,
                            LAUNCH_BATTERY_OPTIMIZATIONS
                        )
                        true
                    }
            }

            // Hide the old version number and user Id
            for (id in intArrayOf(R.string.last_version_key, R.string.userId_key)) {
                val item = findPreference<EditTextPreference>(this.resources.getString(id))
                item?.parent?.removePreference(item)
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        }
    }

    companion object {
        fun displayOptimizationMessage(context: Context) {
            if (Misc.ignoringBatteryOptimizations(context)) {
                battery.summary = "Off (recommended)"
            } else {
                battery.summary = "On (may cause issues)"
            }
        }
    }

}