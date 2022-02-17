package com.example.khughes.machewidget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public static final String VINIDENTIFIER = "VIN";
    public static final String PROFILENAME = "ProfileName";

    private TextInputLayout aliasWidget, usernameWidget, passwordWidget, VINWidget;

    private StoredData appInfo;

    private Boolean profilesActive;
    private Boolean savingCredentials;

    private String VIN;
    private String alias;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        profilesActive = sharedPref.getBoolean(context.getResources().getString(R.string.show_profiles_key), false);
        appInfo = new StoredData(this);

        setContentView(profilesActive ? R.layout.profile_login_activity : R.layout.login_activity);

        savingCredentials = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.save_credentials_key), true);

        TextView disclaimerView = findViewById(R.id.credentials);

        if (savingCredentials) {
            disclaimerView.setText(R.string.disclamer);
        } else {
            disclaimerView.setText(R.string.other_disclamer);
        }

        ArrayList<Profile> profiles = new ArrayList<>();
        for (String VIN : appInfo.getProfiles()) {
            Profile p = new Profile(VIN);
            p.setAlias(appInfo.getProfileName(VIN));
            profiles.add(p);
        }

        usernameWidget = findViewById(R.id.username);
        passwordWidget = findViewById(R.id.password);
        VINWidget = findViewById(R.id.VIN);

        if (profilesActive) {
            aliasWidget = findViewById(R.id.alias);
            alias = getIntent().getStringExtra(PROFILENAME);

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
            aliasWidget.getEditText().setText(alias);
            VIN = getIntent().getStringExtra(VINIDENTIFIER);
        } else {
            VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");
        }

        if (VIN != null && !VIN.equals("")) {
            VINWidget.getEditText().setText(VIN);
        }

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = usernameWidget.getEditText().getText().toString();
                String password = passwordWidget.getEditText().getText().toString();
                VIN = VINWidget.getEditText().getText().toString();

                // Only allow no alias if there are no profiles or the only profile matches this VIN
                if (profilesActive) {
                    String alias = aliasWidget.getEditText().getText().toString();
                    if (alias.length() == 0) {
                        // 1) there are no profiles
                        if ((profiles.size() == 1 && !profiles.get(0).getVIN().equals(VIN)) || profiles.size() > 1) {
                            Toast.makeText(getApplicationContext(), "Please enter a profile name.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // Check if alias exists with a different profile
                    if (alias.length() > 0) {
                        Profile match = profiles.stream().filter(s -> s.getProfileName().equals(alias) && !s.getVIN().equals(VIN)).findAny().orElse(null);
                        if (match != null) {
                            Toast.makeText(getApplicationContext(), "The profile name is already in use.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                if (username.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a username.", Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a password.", Toast.LENGTH_SHORT).show();
                } else if (VIN.length() != 17) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid VIN.", Toast.LENGTH_SHORT).show();
                } else {
                    appInfo.setProgramState(VIN, ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN);
                    sharedPref.edit().putString(getApplicationContext().getResources().getString(R.string.VIN_key), VIN).apply();
                    getAccess(username, password);
                }
            }
        });
    }

    private Bundle bb = new Bundle();

    private void getAccess(String username, String password) {
        Context context = getApplicationContext();
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");

        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                String xx = bb.getString("action");
                ProgramStateMachine.States action = ProgramStateMachine.States.valueOf(bb.getString("action"));
                Log.i(MainActivity.CHANNEL_ID, "Access: " + action);
                appInfo.setProgramState(VIN, action);
                if (action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS) ||
                        action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VIN_AGAIN) ||
                        action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {

                    // If profiles are not being used, update the VIN list.
                    if (!profilesActive) {
                        new StoredData(getApplicationContext()).addProfile(VIN, alias);
                    }

                    // See if we should save the login credentials
                    if (savingCredentials) {
                        appInfo.setUsername(VIN, username);
                        appInfo.setPassword(VIN, password);
                    }

                    String accessToken = bb.getString("access_token");
                    String refreshToken = bb.getString("refresh_token");
                    Toast.makeText(getApplicationContext(), "Log-in successful; attempting to get status.", Toast.LENGTH_SHORT).show();
                    int expires = bb.getInt("expires", 0);
                    LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(expires);
                    long nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    appInfo.setTokenInfo(VIN, accessToken, refreshToken, nextTime);
                    getStatus(accessToken);
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to login to server: check your username and/or password?", Toast.LENGTH_LONG).show();
                }
            }
        };
        NetworkCalls.getAccessToken(h, getApplicationContext(), username, password);
    }

    private void getStatus(String accessToken) {
        Context context = getApplicationContext();
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");

        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                ProgramStateMachine.States action = ProgramStateMachine.States.valueOf(bb.getString("action"));
                Log.i(MainActivity.CHANNEL_ID, "Status: " + action);
                appInfo.setProgramState(VIN, action);
                if (action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                    MainActivity.updateWidget(getApplicationContext());
                    StatusReceiver.nextAlarm(getApplicationContext());
                    getOTAStatus(accessToken);
                    Toast.makeText(getApplicationContext(), "Status retrieved successfully.", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra(VINIDENTIFIER, VIN);
                    if (profilesActive) {
                        data.putExtra(PROFILENAME, aliasWidget.getEditText().getText().toString());
                    }
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else if (action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VIN_AGAIN)) {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve status: check your VIN?", Toast.LENGTH_LONG).show();
                }
            }
        };
        NetworkCalls.getStatus(h, getApplicationContext(), accessToken);
    }

    private void getOTAStatus(String accessToken) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                MainActivity.updateWidget(getApplicationContext());
            }
        };
        NetworkCalls.getOTAStatus(h, getApplicationContext(), accessToken);
    }
}
