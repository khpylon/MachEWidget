package com.example.khughes.machewidget

import android.content.Context
import android.content.Intent
import android.icu.text.MessageFormat
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.example.khughes.machewidget.ProfileManager.finish
import com.example.khughes.machewidget.StatusReceiver.Companion.nextAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    class SettingsFragment : PreferenceFragmentCompat() {
        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                displayOptimizationMessage(requireContext())
            }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val context = context as Context

            val commands = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(
                    context.resources.getString(R.string.enable_commands_key),
                    false
                )
            val forced = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(
                    context.resources.getString(R.string.user_forcedUpdate_key),
                    false
                )
            if(commands == true || forced == true) {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                finish();
            }

            // If update frequency is changed, sent the info to the Alarm Manager
            var showApps: Preference? =
                findPreference(this.resources.getString(R.string.update_frequency_key))
            showApps?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                    val newInterval = newValue as String?
                    newInterval?.let {
                        nextAlarm(context, newInterval.toInt())
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
            val verbose: Preference? =
                findPreference(this.resources.getString(R.string.logging_key))
            verbose?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    if (newValue as Boolean) {
                        LogFile.clearLogFile(context, false)
                    }
                    true
                }

            // Only handle these settings if there are electric vehicles
            val appInfo = StoredData(context)
            if (appInfo.electricVehicles) {
                // These options are only valid in precedence.  If one gets disabled, also disable
                // the others that depend in it.

                val dcfcLogs =
                    findPreference(context.resources.getString(R.string.dcfclog_key)) as SwitchPreferenceCompat?
                dcfcLogs?.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                        DCFC.clearLogFile(context)
                        true
                    }

                val dcfcCharging =
                    findPreference(context.resources.getString(R.string.check_dcfastcharging_key)) as SwitchPreferenceCompat?
                dcfcCharging?.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                        if (!(newValue as Boolean)) {
                            dcfcLogs?.isChecked = false
                        }
                        true
                    }

                val charging: Preference? =
                    findPreference(context.resources.getString(R.string.check_charging_key))
                charging?.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                        if (!(newValue as Boolean)) {
                            DCFC.clearLogFile(context)
                            dcfcLogs?.isChecked = false
                            dcfcCharging?.isChecked = false
                        }
                        true
                    }
                findPreference<PreferenceCategory>(
                    context.resources.getString(R.string.charging_preferences_key)
                )?.isEnabled = true
            } else {
                findPreference<PreferenceCategory>(
                    context.resources.getString(R.string.charging_preferences_key)
                )?.isEnabled = false
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
                R.string.use_image_key,
                R.string.check_charging_key,
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
            val version: Preference? =
                findPreference(this.resources.getString(R.string.version_key))
            version?.summary = BuildConfig.VERSION_NAME + " " + BuildConfig.FLAVOR
            version?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    val appInfo = StoredData(requireContext())
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
            val tmpBattery: Preference? =
                findPreference(this.resources.getString(R.string.battery_opt_key))
            battery = tmpBattery as Preference
            displayOptimizationMessage(context)
            activity?.let {
                battery.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener { _: Preference? ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                        resultLauncher.launch(intent)
                        true
                    }
            }

            // Hide the old version number and user Id
            for (id in intArrayOf(R.string.last_version_key, R.string.userId_key)) {
                val item = findPreference<EditTextPreference>(this.resources.getString(id))
                item?.parent?.removePreference(item)
            }

            val showMMOTA =
                PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(
                        "showMMOTA", false
                    )

            if (!showMMOTA) {
                val item = findPreference<SwitchPreferenceCompat>("checkMMOTA")
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