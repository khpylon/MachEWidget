package com.example.khughes.machewidget;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;
import androidx.webkit.WebViewFeature;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.IOException;

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
        UpdateReceiver.initateAlarm(context);

        // Initiate update of the widget
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);

        // If app has been running OK, tty to initiate status updates
        StoredData appInfo = new StoredData(context);
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");
        if (!VIN.equals("")) {
            ProgramStateMachine.States state = new ProgramStateMachine(appInfo.getProgramState(VIN)).getCurrentState();
            if (state.equals(ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS)) {
                StatusReceiver.initateAlarm(context);
            }
        }

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastVersion = prefs.getString(context.getResources().getString(R.string.last_version_key), "");

        // See if we need to upgrade anything since the last version
        if (BuildConfig.VERSION_NAME.compareTo(lastVersion) > 0) {

            // Add operations here

            // Update internally
            prefs.edit().putString(context.getResources().getString(R.string.last_version_key), BuildConfig.VERSION_NAME).commit();
        }
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

    // Callback for choosing setting file to restore
    ActivityResultLauncher<Intent> restoreSettingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                try {
                                    ZipManager.unzip(context, uri);
                                    Toast.makeText(context, "Settings restored.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } catch (IOException e) {
                                    Log.e(MainActivity.CHANNEL_ID, "exception in MainActivity.restoreSettingsLauncher: ", e);
                                }
                            }
                        }
                    }
                }
            }
    );

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
//                    StatusReceiver.cancelAlarm(context);
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
                return true;
            case R.id.action_copylog:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    String result = LogFile.copyLogFile(context);
                    if (result == null) {
                        Toast.makeText(context, "Log file copied to Download folder.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Logging not implemented for this version of Android.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_backup:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    ZipManager.zipStuff(context);
                    Toast.makeText(context, "Settings saved.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Settings backup not implemented for this version of Android.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_restore:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/*");
                    String[] mimeTypes = new String[]{"application/zip"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    restoreSettingsLauncher.launch(intent);
                } else {
                    Toast.makeText(context, "Settings backup not implemented for this version of Android.", Toast.LENGTH_SHORT).show();
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

    public static Boolean checkBatteryOptimizations(Context context) {
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(packageName);
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
