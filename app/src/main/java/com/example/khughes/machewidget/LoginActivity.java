package com.example.khughes.machewidget;

import android.app.Activity;
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

public class LoginActivity extends AppCompatActivity {

    private IntentFilter mIntentFilter;
    private TextInputLayout usernameWidget, passwordWidget, VINWidget;

    private StoredData appInfo;

    private String VIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Button login;
        appInfo = new StoredData(this);

        usernameWidget = findViewById(R.id.username);
        passwordWidget = findViewById(R.id.password);
        VINWidget = findViewById(R.id.VIN);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        VIN = sharedPref.getString(this.getResources().getString(R.string.VIN_key), "");
        if (!VIN.equals("")) {
            VINWidget.getEditText().setText(VIN);
        }

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = usernameWidget.getEditText().getText().toString();
                String password = passwordWidget.getEditText().getText().toString();
                String newVIN = VINWidget.getEditText().getText().toString();
                if (username.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                } else if (newVIN.length() != 17 ) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid VIN", Toast.LENGTH_SHORT).show();
                } else {
                    if (!newVIN.equals(VIN)) {
                        VIN = newVIN;
                        sharedPref.edit().putString(getApplicationContext().getResources().getString(R.string.VIN_key), VIN).apply();
                    }
                    appInfo.setProgramState(ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN);
                    getAccess(username, password);
                }
            }
        });
    }

    private Bundle bb = new Bundle();

    private void getAccess(String username, String password) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                String xx = bb.getString("action");
                ProgramStateMachine.States action = ProgramStateMachine.States.valueOf(bb.getString("action"));
                Log.i(MainActivity.CHANNEL_ID,"Access: " + action);
                appInfo.setProgramState(action);
                if (action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS) ||
                        action.equals(ProgramStateMachine.States.ATTEMPT_TO_GET_VIN_AGAIN) ||
                        action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                    String accessToken = bb.getString("access_token");
                    appInfo.setAccessToken(accessToken);
                    appInfo.setRefreshToken(bb.getString("refresh_token"));
                    int expires = bb.getInt("expires", 0);
                    LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(expires);
                    long nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    appInfo.setTokenTimeout(nextTime);
                    getStatus(accessToken);
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to login to server: check your username and/or password?", Toast.LENGTH_LONG).show();
                }
            }
        };
        NetworkCalls.getAccessToken(h, getApplicationContext(), username, password);
    }

    private void getStatus(String accessToken) {
        Handler h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                bb = msg.getData();
                ProgramStateMachine.States action = ProgramStateMachine.States.valueOf(bb.getString("action"));
                Log.i(MainActivity.CHANNEL_ID,"Status: " + action);
                appInfo.setProgramState(action);
                if (action.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                    MainActivity.updateWidget(getApplicationContext());
                    StatusReceiver.nextAlarm(getApplicationContext());
                    getOTAStatus(accessToken);
                    setResult(Activity.RESULT_OK);
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
