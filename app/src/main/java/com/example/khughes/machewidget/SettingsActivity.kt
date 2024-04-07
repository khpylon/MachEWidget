package com.example.khughes.machewidget

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.icu.text.MessageFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.preference.*
import com.example.khughes.machewidget.ProfileManager.finish
import com.example.khughes.machewidget.StatusReceiver.Companion.nextAlarm
import java.util.Locale

private lateinit var battery: Preference

class SettingsActivity : AppCompatActivity() {

    private var defaultLanguage: Locale? = null

    private fun getContextForLanguage(context: Context): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return context

        if (defaultLanguage == null) {
            defaultLanguage = Resources.getSystem().configuration.locales[0]
        }

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val languageTag =
            sharedPref.getString(context.resources.getString(R.string.language_key), "")
        val locale = if (languageTag!!.isEmpty()) {
            defaultLanguage as Locale
        } else {
            Locale.forLanguageTag(languageTag)
        }
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(getContextForLanguage(newBase))
    }

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
        private var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                displayOptimizationMessage(requireContext())
            }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val context = context as Context

            // get the system language
            val locales = ConfigurationCompat.getLocales(Resources.getSystem().configuration)
            val systemLanguage = locales[0]!!.toLanguageTag()

            // get the setting saved within the app
            val settingsLanguage = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.resources.getString(R.string.language_key), "") as String

            var currentLanguage : String

            // For Android 13 and later, have to jump through some hoops
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // get per-application locale info
                val applicationLocales = AppCompatDelegate.getApplicationLocales()
                // either get language tag, or if no per-application locale use empty string
                // (to indicate system language setting)
                currentLanguage =
                    if (applicationLocales.size() > 0) applicationLocales[0]!!.toLanguageTag() else ""
            }
            // Android 12 and earlier, not so much
            else {
                currentLanguage = settingsLanguage
            }

            // Construct a ListPreference from available languages
            val languages = findPreference(getString(R.string.language_key)) as ListPreference?
            val entries: MutableList<String> =
                mutableListOf(getString(R.string.activity_settings_system_default_label))
            val entriesValue: MutableList<String> = mutableListOf("")
            languages!!.value = currentLanguage
            for (i in 0 until locales.size()) {
                val locale = locales[i]
                entries.add(locale!!.displayName)
                entriesValue.add(locale.toLanguageTag())
            }

            languages.entries = entries.toTypedArray()
            languages.entryValues = entriesValue.toTypedArray()
            languages.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                    val newInterval = newValue as String
                    val appLocale = if (newInterval == "")
                        LocaleListCompat.getEmptyLocaleList()
                    else
                        LocaleListCompat.forLanguageTags(newInterval)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                    CarStatusWidget.updateWidget(context)
                    true
                }

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
            if (commands || forced) {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                finish()
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

            // Update the widget once the user picks a new unit preference.
            val units: Preference? = findPreference(this.resources.getString(R.string.units_key))
            units?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, _: Any? ->
                    CarStatusWidget.updateWidget(context)
                    true
                }

            // Update the widget once the user picks a new LVB status preference.
            val lvbDisplay: Preference? =
                findPreference(this.resources.getString(R.string.lvb_display_key))
            lvbDisplay?.onPreferenceChangeListener =
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
                    Preference.OnPreferenceChangeListener { _: Preference?, _: Any ->
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
                battery.summary = context.getString(R.string.settings_battery_opts_off_description)
            } else {
                battery.summary = context.getString(R.string.settings_battery_opts_on_description)
            }
        }
    }

}