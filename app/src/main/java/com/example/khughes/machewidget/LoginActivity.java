package com.example.khughes.machewidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout usernameWidget, passwordWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        usernameWidget = findViewById(R.id.username);
        passwordWidget = findViewById(R.id.password);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            if (usernameWidget == null || usernameWidget.getEditText() == null ||
                    passwordWidget == null || passwordWidget.getEditText() == null) {
                return;
            }

            String username = usernameWidget.getEditText().getText().toString();
            String password = passwordWidget.getEditText().getText().toString();

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

                switch (action) {

                    // Log in was successful, and vehicles associated with user ID exists, so update them.
                    case Constants.STATE_HAVE_TOKEN_AND_VIN:
                        Toast.makeText(getApplicationContext(), "Login successful; updating in 5 seconds.", Toast.LENGTH_SHORT).show();
                        StatusReceiver.nextAlarm(context, 5);
                        Intent data = new Intent();
                        setResult(Activity.RESULT_OK, data);
                        finish();
                        break;

                    // Log in was successful but no vehicles found
                    case Constants.STATE_HAVE_TOKEN:
                        // Update global userId key
                        String userId = bb.getString("userId");
                        sharedPref.edit().putString(context.getResources().getString(R.string.userId_key), userId).apply();

                        // Prompt the user to add vehicles
                        Intent intent = new Intent(context, VehicleActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;

                    case Constants.STATE_ACCOUNT_DISABLED:
                        Toast.makeText(getApplicationContext(), "Your account has been disabled (error "
                                + Authenticate.ACCOUNT_DISABLED_CODE + "): contact Ford via the FordPass app to reactivate.", Toast.LENGTH_LONG).show();
                        break;

                    default:
                        Toast.makeText(getApplicationContext(), "Unable to login to server: check your username and/or password?", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        Toast.makeText(getApplicationContext(), "Attempting to log in...", Toast.LENGTH_SHORT).show();
        NetworkCalls.getAccessToken(h, getApplicationContext(), username, password);
    }
}