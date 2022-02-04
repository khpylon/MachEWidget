package com.example.khughes.machewidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout aliasWidget, usernameWidget, passwordWidget, VINWidget;

    private StoredData appInfo;

    private String VIN;
    private String alias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Button login;
        appInfo = new StoredData(this);

        ArrayList<Profile> profiles = new ArrayList<>();
        for (String VIN: appInfo.getProfiles()) {
            Profile p = new Profile(VIN);
            p.setAlias(appInfo.getProfileName(VIN));
            profiles.add(p);
        }

        aliasWidget = findViewById(R.id.alias);
        usernameWidget = findViewById(R.id.username);
        passwordWidget = findViewById(R.id.password);
        VINWidget = findViewById(R.id.VIN);

        VIN = getIntent().getStringExtra("VIN");
        alias = getIntent().getStringExtra("alias");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!VIN.equals("")) {
            VINWidget.getEditText().setText(VIN);
        }

        // No alias is OK when there is only one profile.
        // If there is no alias and more than one profile, create a new generic alias
        if (alias.equals("") && profiles.size() > 1 ) {
            for(int i = 1; i < 4; ++i ) {
                final String newAlias = "User " + i;
                if( !profiles.stream().anyMatch(s -> s.getAlias().equals(newAlias))) {
                    alias = newAlias;
                    break;
                }
            }

        }
        aliasWidget.getEditText().setText(alias);

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String alias = aliasWidget.getEditText().getText().toString();
                String username = usernameWidget.getEditText().getText().toString();
                String password = passwordWidget.getEditText().getText().toString();
                VIN = VINWidget.getEditText().getText().toString();

                // Only allow no alias if there are no profiles or the only profile matches this VIN
                if (alias.length() == 0) {
                    // 1) there are no profiles
                    if ((profiles.size() == 1 && !profiles.get(0).getVIN().equals(VIN)) || profiles.size() > 1) {
                        Toast.makeText(getApplicationContext(), "Please enter a profile name.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Check if alias exists with a different profile
                if (alias.length() > 0){
                    Profile match = profiles.stream().filter(s -> s.getAlias().equals(alias) && !s.getVIN().equals(VIN)).findAny().orElse(null);
                    if(match != null ) {
                        Toast.makeText(getApplicationContext(), "The profile name is already in use.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (username.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                } else if (VIN.length() != 17) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid VIN", Toast.LENGTH_SHORT).show();
                } else {
                    appInfo.setProgramState(VIN, ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN);
                    sharedPref.edit().putString(getApplicationContext().getResources().getString(R.string.VIN_key), VIN).apply();

//                    Intent data = new Intent();
//                    data.putExtra("VIN", VIN);
//                    data.putExtra("alias", aliasWidget.getEditText().getText().toString());
//                    setResult(Activity.RESULT_OK, data);
//                    finish();

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
                appInfo.setProgramState(VIN,action);
                if (action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS) ||
                        action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VIN_AGAIN) ||
                        action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                    String accessToken = bb.getString("access_token");
                    appInfo.setAccessToken(VIN,accessToken);
                    appInfo.setRefreshToken(VIN,bb.getString("refresh_token"));
                    int expires = bb.getInt("expires", 0);
                    LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(expires);
                    long nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    appInfo.setTokenTimeout(VIN,nextTime);
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
                appInfo.setProgramState(VIN,action);
                if (action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                    MainActivity.updateWidget(getApplicationContext());
                    StatusReceiver.nextAlarm(getApplicationContext());
                    getOTAStatus(accessToken);

                    Intent data = new Intent();
                    data.putExtra("VIN", VIN);
                    data.putExtra("alias", aliasWidget.getEditText().getText().toString());
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
                bb = msg.getData();
                ProgramStateMachine.States action = ProgramStateMachine.States.valueOf(bb.getString("action"));
                if (action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS) ||
                        action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VIN_AGAIN)) {
                    MainActivity.updateWidget(getApplicationContext());
                }
            }
        };
        NetworkCalls.getOTAStatus(h, getApplicationContext(), accessToken);
    }
}
