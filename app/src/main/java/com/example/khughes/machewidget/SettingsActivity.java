package com.example.khughes.machewidget;

import android.content.Context;
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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

            // Changing any of these preferences requires updating the widget
            for( int id : new int[] {R.string.show_app_links_key, R.string.transp_bg_key, R.string.enable_commands_key, R.string.last_refresh_time_key, R.string.show_OTA_key, R.string.show_location_key} ) {
                Preference showApps = findPreference(this.getResources().getString(id));
                showApps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        MainActivity.updateWidget(mContext);
                        return true;
                    }
                });
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
            // Only allow number and upper-case letters (no punctuation) in a VIN
            EditTextPreference vinPreference = findPreference(this.getResources().getString(R.string.VIN_key));
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
