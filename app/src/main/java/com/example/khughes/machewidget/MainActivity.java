package com.example.khughes.machewidget;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;
import androidx.webkit.WebViewFeature;

import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "934TXS";

    private Context context;
    private int vehicleCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();

        // First thing, check logcat for a crash and save if so
        String crashMessage = Utils.checkLogcat(context);
        if (crashMessage != null) {
            Toast.makeText(context, crashMessage, Toast.LENGTH_SHORT).show();
        }

        // Initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);

        // Handle any changes to the app.
        performUpdates(context);

        // Initiate check for a new app version
        UpdateReceiver.initiateAlarm(context);

        // See if we need to notify user about battery optimizations
        Notifications.batteryOptimization(context);

        // Initiate update of the widget
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }

        // If app has been running OK, try to initiate status updates and count number of vehicles in the database
        String userId = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.userId_key), null);
        if (userId != null) {
            new Thread(() -> {
                InfoRepository info = new InfoRepository(context);
                UserInfo userInfo = info.getUser();
                if (userInfo != null && userInfo.getProgramState().equals(Constants.STATE_HAVE_TOKEN_AND_VIN)) {
                    StatusReceiver.initateAlarm(context);
                }
                vehicleCount = info.getVehicles().size();
            }).start();
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
        String indexPage = "https://appassets.androidplatform.net/assets/index_page.html";
        mWebView.loadUrl(indexPage);

        // Update the widget
        updateWidget(context);

        // Allow the app to display notifications
        createNotificationChannel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    // This method is intended to bundle various changes from older versions to the most recent.
    public static void performUpdates(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastVersion = prefs.getString(context.getResources().getString(R.string.last_version_key), "");

        // See if we need to upgrade anything since the last version
        if (!lastVersion.equals("") && BuildConfig.VERSION_NAME.compareTo(lastVersion) > 0) {
            LogFile.i(context, CHANNEL_ID, "running updates");

            // Add operations here

            // Replace sharedpreference files with databases
            if (lastVersion.compareTo("2022.04.29") < 0) {
                migrateToDatabases(context);
            }

            // Make sure MainActivity is enabled
            if (lastVersion.compareTo("2022.05.12") < 0) {

                PackageManager manager = context.getPackageManager();
                String packageName = context.getPackageName();

                Map<String, Boolean> results = new HashMap<>();
                results.put(".MainActivity", true);
                results.put(".F150MainActivity", false);
                results.put(".BroncoMainActivity", false);
                results.put(".ExplorerMainActivity", false);
                try {
                    for (String activity : results.keySet()) {
                        manager.setComponentEnabledSetting(new ComponentName(packageName, packageName + activity),
                                results.get(activity) ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                    }
                    // Give the OS some time to finish reconfiguring things
                    Thread.sleep(1500);
                } catch (Exception e) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "exception in CarStatusWidget_5x5.matchWidgetWithVin()" + e);
                }
            }

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion.compareTo("2022.05.25") < 0) {
                LogFile.d(context, MainActivity.CHANNEL_ID, "running 2022.05.25 updates");

                PreferenceManager.setDefaultValues(context, R.xml.settings_preferences, true);
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String VIN = sharedPrefs.getString(context.getResources().getString(R.string.VIN_key), null);

                new Thread(() -> {
                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao().updateSupportOTA();
                    if (VIN != null) {
                        VehicleInfo info = VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfoByVIN(VIN);
                        String userId = info.getUserId();
                        // Some vehicle entries had missing userID value.  If so, get the user ID from the first entry
                        // of the user database and update all vehicles
                        if (userId == null) {
                            LogFile.d(context, MainActivity.CHANNEL_ID, "2022.05.25 update: adding user ID to vehicles");
                            List<UserInfo> userInfo = UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo();
                            if (!userInfo.isEmpty()) {
                                userId = userInfo.get(0).getUserId();
                                for (VehicleInfo vehInfo : VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo()) {
                                    vehInfo.setUserId(userId);
                                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao().updateVehicleInfo(vehInfo);
                                }
                            }
                        }
                        sharedPrefs.edit().putString(context.getResources().getString(R.string.userId_key), userId).commit();
                    }
                }).start();
            }

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion.compareTo("2022.05.31") < 0 || lastVersion.compareTo("2022.06.04a") < 0) {
                PreferenceManager.setDefaultValues(context, R.xml.settings_preferences, true);
            }

            // Re-enable OTA support on all vehicles, and add userId to settings.
            if (lastVersion.compareTo("2022.06.20") < 0) {
                new Thread(() -> {
                    for (UserInfo userInfo : UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo()) {
                        userInfo.setProgramState(Constants.STATE_HAVE_TOKEN_AND_VIN);
                        UserInfoDatabase.getInstance(context).userInfoDao().updateUserInfo(userInfo);
                    }
                }).start();
            }

            // Reload vehicle images, including angles
            if (lastVersion.compareTo("2022.07.16") < 0) {
                File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
                if (imageDir != null) {
                    for (String file : imageDir.list()) {
                        new File(imageDir, file).delete();
                    }
                }
                new Thread(() -> {
                    for (VehicleInfo vehicleInfo : VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo()) {
                        UserInfo user = UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo(vehicleInfo.getUserId());
                        if (user != null) {
                            NetworkCalls.getVehicleImage(context, user.getAccessToken(), vehicleInfo.getVIN(), user.getCountry());
                        }
                    }
                }).start();
            }
        }

        // Update internally
        prefs.edit().putString(context.getResources().getString(R.string.last_version_key), BuildConfig.VERSION_NAME).commit();
    }

    private static void migrateToDatabases(Context context) {

        // Turn off profiles
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String currentVIN = prefs.getString(context.getResources().getString(R.string.VIN_key), "");
        prefs.edit().putBoolean(context.getResources().getString(R.string.show_profiles_key), false).apply();

        StoredData appInfo = new StoredData(context);

        // If stored credentials are active, get user and vehicle info for each VIN and
        // set up databases

        boolean savingCredentials = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.save_credentials_key), true);

        appInfo.setLeftAppPackage(appInfo.getLeftAppPackage(currentVIN));
        appInfo.setRightAppPackage(appInfo.getRightAppPackage(currentVIN));

        if (savingCredentials) {
            // Clear current VIN
            prefs.edit().putString(context.getResources().getString(R.string.VIN_key), "").apply();

            for (String VIN : appInfo.getProfiles()) {
                String username = appInfo.getUsername(VIN);
                String password = appInfo.getPassword(VIN);
                Handler h = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                    }
                };
                NetworkCalls.getAccessToken(h, context, username, password);
                appInfo.removeProfile(VIN);
            }
        }

        // if store credentials aren't available, add the current VIN file to vehicle database
        // and assign to a temp user ID
        else {
            prefs.edit().putString(context.getResources().getString(R.string.VIN_key), "").apply();

            ArrayList<String> VINs = appInfo.getProfiles();
            UserInfo userInfo = new UserInfo();

            if (!VINs.isEmpty()) {
                LogFile.i(context, MainActivity.CHANNEL_ID, "creating temporary user profile");
                String userId = Constants.TEMP_ACCOUNT;
                userInfo.setUserId(userId);
                userInfo.setAccessToken(appInfo.getAccessToken(currentVIN));
                userInfo.setRefreshToken(appInfo.getRefreshToken(currentVIN));
                userInfo.setCountry(appInfo.getCountry(currentVIN));
                userInfo.setUsername("");
                userInfo.setPassword("");
                userInfo.setProgramState(appInfo.getProgramState(currentVIN));
                userInfo.setAccessToken(appInfo.getAccessToken(currentVIN));
                userInfo.setRefreshToken(appInfo.getRefreshToken(currentVIN));
                userInfo.setExpiresIn(appInfo.getTokenTimeout(currentVIN));
                userInfo.setCountry(appInfo.getCountry(currentVIN));
                userInfo.setLanguage(appInfo.getLanguage(currentVIN));
                userInfo.setUomSpeed(appInfo.getSpeedUnits(currentVIN));
                userInfo.setUomDistance(appInfo.getDistanceUnits(currentVIN));
                userInfo.setUomPressure(appInfo.getPressureUnits(currentVIN));
                new Thread(() -> {
                    UserInfoDatabase.getInstance(context)
                            .userInfoDao()
                            .insertUserInfo(userInfo);
                    LogFile.i(context, MainActivity.CHANNEL_ID, "temporary user profile completed");

                    Handler h1 = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            Bundle bundle = msg.getData();
                            String userId = bundle.getString("userId");
                            for (String VIN : VINs) {
                                Handler h = new Handler(Looper.getMainLooper()) {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        LogFile.i(context, MainActivity.CHANNEL_ID, "handler returned " + VIN);
                                    }
                                };
                                NetworkCalls.getUserVehicles(h, context, userId);
                                LogFile.i(context, MainActivity.CHANNEL_ID, "processing VIN " + VIN);
                                appInfo.removeProfile(VIN);
                            }
                        }
                    };

                    NetworkCalls.refreshAccessToken(h1, context, userId, userInfo.getRefreshToken());

                }).start();
            }
        }

        // Remove VINs.xml file
        new File(context.getDataDir() + File.separator + Constants.SHAREDPREFS_FOLDER, StoredData.VINLIST + ".xml").deleteOnExit();
        LogFile.i(context, MainActivity.CHANNEL_ID, "database conversion completed");

        // Do a refresh in 5 seconds
        StatusReceiver.nextAlarm(context, 5);
    }

    private static class LocalContentWebViewClient extends WebViewClientCompat {
        private final WebViewAssetLoader mAssetLoader;

        LocalContentWebViewClient(WebViewAssetLoader assetLoader) {
            mAssetLoader = assetLoader;
        }

        @Override
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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Only enable "Choose Linked Apps" if app buttons are enabled
        boolean showAppLinks = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(context.getResources().getString(R.string.show_app_links_key), true);
        menu.findItem(R.id.action_chooseapp).setEnabled(showAppLinks);

        // Only enable "Save Logfile" if logging is enabled,
        boolean loggingEnabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(context.getResources().getString(R.string.logging_key), true);
        menu.findItem(R.id.action_copylog).setEnabled(loggingEnabled);

        // Only enable "Set Vehicle Color" if colors are enabled,
        boolean colorsEnabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(context.getResources().getString(R.string.use_colors_key), true);
        menu.findItem(R.id.action_color).setVisible(colorsEnabled);

        // The PlayStore version doesn't do all the update stuff
        if (com.example.khughes.machewidget.BuildConfig.FLAVOR.equals("playstore")) {
            menu.findItem(R.id.action_update).setVisible(false);
        }

        // If there aren't multiple vehicles, don't display manage vehicles option.
        if (vehicleCount < 2) {
            menu.findItem(R.id.action_vehicle).setVisible(false);
        }

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
                                String type = context.getContentResolver().getType(uri);
                                try {
                                    if (type.equals(Constants.APPLICATION_JSON) ||
                                            (type.equals(Constants.APPLICATION_OCTETSTREAM) &&
                                                    uri.getPath().endsWith(".json"))) {
                                        Utils.restorePrefs(context, uri);
                                    } else {
                                        return;
                                    }
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } catch (IOException e2) {
                                    Log.e(MainActivity.CHANNEL_ID, "exception in MainActivity.restoreSettingsLauncher: ", e2);
                                }
                            }
                        }
                    }
                }
            }
    );

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_login) {
            // Depending on whether profiles are being used, either start the profile manager or go straight to login screen
            boolean profiles = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.show_profiles_key), false);
            Intent intent = new Intent(this, profiles ? ProfileManager.class : LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_refresh) {
            StatusReceiver.nextAlarm(context, 5);
            Toast.makeText(context, "Refresh scheduled in 5 seconds.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_chooseapp) {
            Intent intent = new Intent(this, ChooseAppActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_vehicle) {
            Intent intent = new Intent(this, VehicleActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_color) {
            Intent intent = new Intent(this, ColorActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_ota_view) {
            Intent intent = new Intent(this, OTAViewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_copylog) {
            String result = LogFile.copyLogFile(context);
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_backup) {
            Utils.savePrefs(context);
            return true;
        } else if (id == R.id.action_restore) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/*");
            String[] mimeTypes = new String[]{Constants.APPLICATION_JSON, Constants.APPLICATION_OCTETSTREAM};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            restoreSettingsLauncher.launch(intent);
            return true;
        } else if (id == R.id.action_update) {
            Intent intent = new Intent(this, UpdateReceiver.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            context.sendBroadcast(intent);
            Toast.makeText(context, "Checking for an update; if one is found, a notification will appear.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "Channel name"; // getString(R.string.channel_name);
        String description = "Description"; // getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static Boolean ignoringBatteryOptimizations(Context context) {
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(packageName);
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
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);
    }
}
