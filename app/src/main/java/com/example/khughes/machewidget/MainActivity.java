package com.example.khughes.machewidget;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;
import androidx.webkit.WebViewFeature;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.compress.utils.Charsets;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "934TXS";

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();

        // Initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);

        // Handle any changes to the app.
        performUpdates(context);

        // Initiate check for a new app version
        Intent updateIntent = new Intent(context, UpdateReceiver.class);
        context.sendBroadcast(updateIntent);

        // Initiate update of the widget
        updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);

        // Create the webview containing instruction for use.
        WebView mWebView = findViewById(R.id.main_description);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(mWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                .build();
        mWebView.setWebViewClient(new LocalContentWebViewClient(assetLoader));

        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");
        String indexPage = "https://appassets.androidplatform.net/assets/index_mache.html";
        if (!VIN.equals("")) {
            if (Utils.isBronco(VIN)) {
                indexPage = "https://appassets.androidplatform.net/assets/index_bronco.html";
            } else if (Utils.isF150(VIN)) {
                indexPage = "https://appassets.androidplatform.net/assets/index_f150.html";
            }
        }
        mWebView.loadUrl(indexPage);

        // Update the widget
        updateWidget(context);

        // Allow the app to display notifications
        createNotificationChannel();
    }

    // This method is intended to bundle various changes from older versions to the most recent.
    private void performUpdates(Context context) {
        // Convert over to profiles (Version 2022.02.06)
        ProfileManager.upgradeToProfiles(context);
    }

    private static class LocalContentWebViewClient extends WebViewClientCompat {
        private final WebViewAssetLoader mAssetLoader;

        LocalContentWebViewClient(WebViewAssetLoader assetLoader) {
            mAssetLoader = assetLoader;
        }

        @Override
        @RequiresApi(21)
        public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          WebResourceRequest request) {
            return mAssetLoader.shouldInterceptRequest(request.getUrl());
        }

        @Override
        @SuppressWarnings("deprecation") // to support API < 21
        public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          String url) {
            return mAssetLoader.shouldInterceptRequest(Uri.parse(url));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Only enable "Choose Linked Apps" if app buttons are enabled
        Boolean showAppLinks = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(context.getResources().getString(R.string.show_app_links_key), true);
        menu.findItem(R.id.action_chooseapp).setEnabled(showAppLinks);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                // Depending on whether profiles are being used, either start the profile manager or go straight to login screen
                Boolean profiles = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.show_profiles_key), false);
                Intent intent = new Intent(this, profiles ? ProfileManager.class : LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");
                if (new StoredData(context).getLastUpdateElapsedTime() > 5 * 60 * 1000) {
                    StatusReceiver.cancelAlarm(context);
                    StatusReceiver.nextAlarm(context, 5);
                    Toast.makeText(context, "Refresh scheduled in 5 seconds.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "An update occurred within the past 5 minutes.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_chooseapp:
                intent = new Intent(this, ChooseApp.class);
                startActivity(intent);
                return true;
            case R.id.action_ota_view:
                intent = new Intent(this, OTAViewActivity.class);
                startActivity(intent);
            case R.id.action_copylog:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    if (LogFile.copyLogFile(context)) {
                        Toast.makeText(context, "Log file saved to Downloads folder.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "An error occurred saving the log file.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Logging not implemented for this version of Android.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel name"; // getString(R.string.channel_name);
            String description = "De-scription"; // getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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

        return networkInfo.isAvailable();
    }

    public static void updateWidget(Context context) {
        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(new ComponentName(context, CarStatusWidget.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(CarStatusWidget.WIDGET_IDS_KEY, ids);
        context.sendBroadcast(updateIntent);
    }

}
