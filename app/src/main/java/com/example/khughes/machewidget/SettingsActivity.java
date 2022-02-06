package com.example.khughes.machewidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private static Context mContext = null;

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
        mContext = this.getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // If update frequency is changed, sent the info to the Alarm Manager
            Preference showApps = findPreference(this.getResources().getString(R.string.update_frequency_key));
            showApps.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    StatusReceiver.cancelAlarm(getContext());
                    StatusReceiver.nextAlarm(getContext(),Integer.valueOf((String) newValue));
                    return true;
                }
            });

            // Changing any of these preferences requires updating the widget
            for (int id : new int[]{R.string.show_app_links_key, R.string.transp_bg_key, R.string.enable_commands_key, R.string.last_refresh_time_key, R.string.show_OTA_key,
                    R.string.show_location_key}) {
                showApps = findPreference(this.getResources().getString(id));
                showApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (preference.getKey().equals(getResources().getString(R.string.update_frequency_key))) {
                            StatusReceiver.cancelAlarm(getContext());
                            StatusReceiver.nextAlarm(getContext());
                        } else {
                            MainActivity.updateWidget(mContext);
                        }
                        return true;
                    }
                });
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
            EditTextPreference vinPreference = findPreference(this.getResources().getString(R.string.VIN_key));

            Context context = getContext();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean profilesActive = sharedPref.getBoolean(context.getResources().getString(R.string.show_profiles_key), false);

            // When profiles are enabled, you can't change the VIN here
            if (profilesActive) {
                vinPreference.setEnabled(false);
            } else {
                // Only allow number and upper-case letters (no punctuation) in a VIN
                if (vinPreference != null) {
                    vinPreference.setOnBindEditTextListener(
                            new EditTextPreference.OnBindEditTextListener() {
                                @Override
                                public void onBindEditText(@NonNull EditText editText) {
                                    InputFilter filter = new InputFilter() {
                                        public CharSequence filter(CharSequence source, int start, int end,
                                                                   Spanned dest, int dstart, int dend) {
                                            String result = "";
                                            for (int i = start; i < end; i++) {
                                                char c = source.charAt(i);
                                                if (!Character.isLetterOrDigit(c)) { // Accept only letter & digits ; otherwise just return
                                                    return "";
                                                }
                                                result += Character.toUpperCase(c);
                                            }
                                            return result;
                                        }
                                    };

                                    editText.setFilters(new InputFilter[]{filter});
                                }
                            });
                }
            }
        }
    }
}
