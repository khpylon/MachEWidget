package com.example.khughes.machewidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "934TXS";

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String marketPref = sharedPref.getString("VIN", "");

        context = this.getApplicationContext();
//        if(BuildConfig.DEBUG) {
//            StoredData appInfo = new StoredData(context);
//            appInfo.resetCounters();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                Intent settingsIntent = new Intent(this,
                        LoginActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_logout:
                StoredData appInfo = new StoredData(context);
                appInfo.setAccessToken("");
                appInfo.setProgramState(ProgramStateMachine.States.INITIAL_STATE);
                StatusReceiver.cancelAlarm(context);
                updateWidgetLoggedOut(context);
                Toast.makeText(context, "Removing authentication token", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_ota_view:
                settingsIntent = new Intent(this,
                        OTAViewActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_settings:
                settingsIntent = new Intent(this,
                        SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean checkInternetConnection() {
        // Get Connectivity Manager
        return checkInternetConnection(context);
    }

    public static boolean checkInternetConnection(Context context) {
        // Get Connectivity Manager
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Details about the currently active default data network
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return false;
        }

        if (!networkInfo.isConnected()) {
            return false;
        }

        if (!networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static void updateWidgetCarStatus(Context context) {
        updateWidget( context, StatusWidget.UPDATE_CAR);
    }

    public static void updateWidgetOTAStatus(Context context) {
        updateWidget( context, StatusWidget.UPDATE_OTA);
    }

    public static void updateWidgetLoggedOut(Context context) {
        updateWidget( context,StatusWidget.LOGGED_OUT);
    }

    private  static void updateWidget(Context context, int updateType) {
        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(new ComponentName(context, StatusWidget.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(StatusWidget.WIDGET_IDS_KEY, ids);
        updateIntent.putExtra(StatusWidget.UPDATE_TYPE, updateType);
        context.sendBroadcast(updateIntent);
    }

}