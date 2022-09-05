package com.example.khughes.machewidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    public static final String VINIDENTIFIER = "VIN";
    public static final String PROFILENAME = "ProfileName";

    private TextInputLayout aliasWidget, usernameWidget, passwordWidget;

    private StoredData appInfo;

    private Boolean profilesActive;
    private Boolean savingCredentials;

    //    private String VIN;
    private String alias;

    private void updateDisclamer(TextView view, boolean saved) {
        if (saved) {
            view.setText(R.string.disclamer);
        } else {
            view.setText(R.string.other_disclamer);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        profilesActive = sharedPref.getBoolean(context.getResources().getString(R.string.show_profiles_key), false);
        appInfo = new StoredData(this);

        setContentView(R.layout.login_activity);

        savingCredentials = sharedPref.getBoolean(context.getResources().getString(R.string.save_credentials_key), true);

        TextView disclaimerView = findViewById(R.id.credentials);
        Button fingerprint = findViewById(R.id.fingerprint);

        Switch credentials = findViewById(R.id.storeCredentials);
        credentials.setChecked(savingCredentials);
        credentials.setOnCheckedChangeListener((button, value) -> {
            savingCredentials = value;
            sharedPref.edit().putBoolean(context.getResources().getString(R.string.save_credentials_key), value).commit();
            if (!savingCredentials) {
                new Encryption(context).clearCredentials();
            }
            fingerprint.setVisibility(View.GONE);
            updateDisclamer(disclaimerView, savingCredentials);
        });

        updateDisclamer(disclaimerView, savingCredentials);

        ArrayList<Profile> profiles = new ArrayList<>();
        for (String VIN : appInfo.getProfiles()) {
            Profile p = new Profile(VIN);
//            p.setAlias(appInfo.getProfileName(VIN));
            profiles.add(p);
        }

        usernameWidget = findViewById(R.id.username);
        passwordWidget = findViewById(R.id.password);

        aliasWidget = findViewById(R.id.alias);
        if (aliasWidget != null) {
            aliasWidget.setVisibility(profilesActive ? View.VISIBLE : View.GONE);
        }
        alias = getIntent().getStringExtra(PROFILENAME);
        if (profilesActive) {

            // No alias is OK when there is only one profile.
            // If there is no alias and more than one profile, create a new generic alias
            if (alias.equals("") && profiles.size() > 1) {
                for (int i = 1; i < 4; ++i) {
                    final String newAlias = "User " + i;
                    if (!profiles.stream().anyMatch(s -> s.getProfileName().equals(newAlias))) {
                        alias = newAlias;
                        break;
                    }
                }
            }
            if (aliasWidget != null && aliasWidget.getEditText() != null) {
                aliasWidget.getEditText().setText(alias);
            }
//            VIN = getIntent().getStringExtra(VINIDENTIFIER);
        } else {
//            VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");
        }

        // If the user has stored credentials, allow to reuse if they have registered a fingerprint
        if (savingCredentials) {
            final boolean fingerprintHardware;
            final boolean fingerprintSaved;
            BiometricManager biometricManager = androidx.biometric.BiometricManager.from(context);
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                // We have everything we need
                case BiometricManager.BIOMETRIC_SUCCESS:
                    fingerprintHardware = true;
                    fingerprintSaved = true;
                    break;
                // There is the hardware, but not any fingerprints
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    fingerprintHardware = true;
                    fingerprintSaved = false;
                    break;
                // No fingerprint scanner
                case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                default:
                    fingerprintHardware = false;
                    fingerprintSaved = false;
                    break;
            }

            // If there's hardware, at least put up the button
            if (fingerprintHardware) {
                fingerprint.setVisibility(View.VISIBLE);

                Executor executor = ContextCompat.getMainExecutor(this);
                final BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                Bundle bundle = msg.getData();
                                Encryption encrypt = new Encryption(context);
                                String username = encrypt.getPlaintextString(bundle.getString("username"));
                                String password = encrypt.getPlaintextString(bundle.getString("password"));
                                if (username != null && password != null) {
                                    getAccess(username, password);
                                } else {
                                    Toast.makeText(context, "Unable to retrieve user data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };

                        new Thread(() -> {
                            String userId = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.userId_key), null);
                            if (userId != null) {
                                Bundle bundle = new Bundle();
                                UserInfo userInfo = UserInfoDatabase.getInstance(context)
                                        .userInfoDao().findUserInfo(userId);
                                if (userInfo != null) {
                                    bundle.putString("username", userInfo.getUsername());
                                    bundle.putString("password", userInfo.getPassword());
                                }
                                Message m = Message.obtain();
                                m.setData(bundle);
                                handler.sendMessage(m);
                            }
                        }).start();
                    }
                });

                fingerprint.setOnClickListener(v -> {
                    // There is a fingerprint registered
                    if (fingerprintSaved) {
                        biometricPrompt.authenticate(new BiometricPrompt.PromptInfo.Builder().setTitle("Fingerprint required.")
                                .setDescription("Use your fingerprint to authenticate.").setNegativeButtonText("Cancel").build());
                    }
                    // Uh, no there isn't.  Explain why they can't use this
                    else {
                        Toast.makeText(context, "Please register a fingerprint first.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        Button login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            if (usernameWidget == null || usernameWidget.getEditText() == null ||
                    passwordWidget == null || passwordWidget.getEditText() == null) {
                return;
            }

            String username = usernameWidget.getEditText().getText().toString();
            String password = passwordWidget.getEditText().getText().toString();

            // Only allow no alias if there are no profiles or the only profile matches this VIN
//            if (profilesActive && aliasWidget != null && aliasWidget.getEditText() != null) {
//                String alias = aliasWidget.getEditText().getText().toString();
//                if (alias.length() == 0) {
//                    // 1) there are no profiles
//                    if ((profiles.size() == 1 && !profiles.get(0).getVIN().equals(VIN)) || profiles.size() > 1) {
//                        Toast.makeText(getApplicationContext(), "Please enter a profile name.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//
//                // Check if alias exists with a different profile
//                if (alias.length() > 0) {
//                    Profile match = profiles.stream().filter(s -> s.getProfileName().equals(alias) && !s.getVIN().equals(VIN)).findAny().orElse(null);
//                    if (match != null) {
//                        Toast.makeText(getApplicationContext(), "The profile name is already in use.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//            }

            if (username.length() == 0) {
                Toast.makeText(getApplicationContext(), "Please enter a username.", Toast.LENGTH_SHORT).show();
            } else if (password.length() == 0) {
                Toast.makeText(getApplicationContext(), "Please enter a password.", Toast.LENGTH_SHORT).show();
            } else {
                getAccess(username, password);
            }
        });
    }

    private void getAccess(String username, String password) {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bb = msg.getData();
                String action = bb.getString("action");
                LogFile.i(context, MainActivity.CHANNEL_ID, "Access: " + action);
                if (action.equals(Constants.STATE_HAVE_TOKEN)) {

                    // Log in was successful, so update global userId key
                    String userId = bb.getString("userId");
                    sharedPref.edit().putString(context.getResources().getString(R.string.userId_key), userId).apply();

                    // If profiles are not being used, update the VIN list.
//                    if (!profilesActive) {
//                        new StoredData(getApplicationContext()).addProfile(VIN, alias);
//                    }

                    Toast.makeText(getApplicationContext(), "Log-in successful; requesting vehicle list.", Toast.LENGTH_SHORT).show();
                    getUserVehicles(userId);
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to login to server: check your username and/or password?", Toast.LENGTH_LONG).show();
                }
            }
        };
        NetworkCalls.getAccessToken(h, getApplicationContext(), username, password);
    }

    private void getUserVehicles(String userId) {
        Context context = getApplicationContext();

        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bb = msg.getData();
                String action = bb.getString("action");
                LogFile.i(context, MainActivity.CHANNEL_ID, "UserVehicles: " + action);
                if (action.equals(Constants.STATE_HAVE_TOKEN_AND_VIN)) {
                    Toast.makeText(getApplicationContext(), "Vehicles list obtained; updating in 5 seconds.", Toast.LENGTH_SHORT).show();
                    StatusReceiver.nextAlarm(context, 5);
                    Intent data = new Intent();
//                    data.putExtra(VINIDENTIFIER, VIN);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to get vehicles; will attempt again on next refresh.", Toast.LENGTH_LONG).show();
                }
            }
        };
        NetworkCalls.getUserVehicles(h, getApplicationContext(), userId);
    }
}