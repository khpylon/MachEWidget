package com.example.khughes.machewidget;

import android.content.Context;
import android.content.Intent;
import android.icu.text.MessageFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final int LAUNCH_BATTERY_OPTIMIZATIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_BATTERY_OPTIMIZATIONS) {
            displayOptimizationMessage(getApplicationContext());
        }
    }

    private static Preference battery;

    private static void displayOptimizationMessage(Context context) {
        if (Utils.ignoringBatteryOptimizations(context)) {
            battery.setSummary("Off (recommended)");
        } else {
            battery.setSummary("On (may cause issues)");
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Context context = getContext();

            // If update frequency is changed, sent the info to the Alarm Manager
            Preference showApps = findPreference(this.getResources().getString(R.string.update_frequency_key));
            showApps.setOnPreferenceChangeListener((preference, newValue) -> {
                StatusReceiver.nextAlarm(context, Integer.parseInt((String) newValue));
                return true;
            });

            // Update the widget once the user pick a new unit preference.
            Preference units = findPreference(this.getResources().getString(R.string.units_key));
            units.setOnPreferenceChangeListener((preference, newValue) -> {
                CarStatusWidget.updateWidget(context);
                return true;
            });

            // No matter the choice, erase the username and password for all profiles
            Preference cred = findPreference(this.getResources().getString(R.string.save_credentials_key));
            cred.setOnPreferenceChangeListener((preference, newValue) -> {
                new Encryption(context).clearCredentials();
                return true;
            });

            // Erase the old log file on enable.
            Preference verbose = findPreference(this.getResources().getString(R.string.logging_key));
            verbose.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    LogFile.clearLogFile(context, false);
                }
                return true;
            });

            // Changing any of these preferences requires updating the widget
            for (int id : new int[]{R.string.show_app_links_key, R.string.transp_bg_key, R.string.enable_commands_key, R.string.last_refresh_time_key, R.string.show_OTA_key,
                    R.string.show_location_key, R.string.user_forcedUpdate_key, R.string.use_colors_key,  R.string.use_image_key}) {
                showApps = findPreference(this.getResources().getString(id));
                showApps.setOnPreferenceClickListener(preference -> {
                    if (preference.getKey().equals(getResources().getString(R.string.update_frequency_key))) {
                        StatusReceiver.nextAlarm(context);
                    } else {
                        CarStatusWidget.updateWidget(context);
                    }
                    return true;
                });
            }

            // Set app version info
            Preference version = findPreference(this.getResources().getString(R.string.version_key));
            version.setSummary(BuildConfig.VERSION_NAME + " " + BuildConfig.FLAVOR);
            version.setOnPreferenceClickListener(preference -> {
                    StoredData appInfo = new StoredData(getContext());
                    String units1 = MessageFormat.format("status = {0}/{1}/{2}/{3}/{4}/{5}",
                            appInfo.getCounter(StoredData.STATUS_NOT_LOGGED_IN),
                            appInfo.getCounter(StoredData.STATUS_LOG_OUT),
                            appInfo.getCounter(StoredData.STATUS_LOG_IN),
                            appInfo.getCounter(StoredData.STATUS_VEHICLE_INFO),
                            appInfo.getCounter(StoredData.STATUS_UPDATED),
                            appInfo.getCounter(StoredData.STATUS_UNKNOWN));
                    Toast.makeText(getContext(), units1, Toast.LENGTH_LONG).show();
                return false;
            });

            // Provide a link to the GitHub repository
            Preference github = findPreference(this.getResources().getString(R.string.github_repo_key));
            github.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.REPOURL));
                startActivity(intent);
                return true;
            });

            // Display battery optimization setting and make easier for user to change
            battery = findPreference(this.getResources().getString(R.string.battery_opt_key));
            displayOptimizationMessage(context);
            battery.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                getActivity().startActivityForResult(intent, LAUNCH_BATTERY_OPTIMIZATIONS);
                return true;
            });

            // Hide the old version number and user Id
            for (int id : new int[]{R.string.last_version_key, R.string.userId_key}) {
                EditTextPreference item = findPreference(this.getResources().getString(id));
                item.getParent().removePreference(item);
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
        }
    }
}
