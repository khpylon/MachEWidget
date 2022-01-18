package com.example.khughes.machewidget;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

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
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
            // Only allow number and upper-case letters (no punctuation) in a VIN
            EditTextPreference vinPreference = findPreference("VIN");
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

                                editText.setFilters(new InputFilter[] {filter});
                            }
                        });
            }
        }
    }
}