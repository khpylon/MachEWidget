package com.example.khughes.machewidget;

import androidx.annotation.RequiresApi;
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
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "934TXS";

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();

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
        mWebView.loadUrl("https://appassets.androidplatform.net/assets/index.html");

        updateWidget(context);

        // Allow the app to display notifications
        createNotificationChannel();
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
                Intent settingsIntent = new Intent(this,
                        LoginActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_logout:
                StoredData appInfo = new StoredData(context);
                appInfo.setAccessToken("");
                appInfo.setProgramState(ProgramStateMachine.States.INITIAL_STATE);
                StatusReceiver.cancelAlarm(context);
                updateWidget(context);
                Toast.makeText(context, "Removing authentication token", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_refresh:
                if (new StoredData(context).getLastUpdadeElapsedTime() > 5 * 60 * 1000) {
                    StatusReceiver.cancelAlarm(context);
                    StatusReceiver.nextAlarm(context, 5);
                    Toast.makeText(context, "Refresh scheduled in 5 seconds.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "An update occurred within the past 5 minutes.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_chooseapp:
                settingsIntent = new Intent(this, ChooseApp.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_ota_view:
                settingsIntent = new Intent(this, OTAViewActivity.class);
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

        if (!networkInfo.isAvailable()) {
            return false;
        }
        return true;
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
