package com.example.khughes.machewidget;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "934TXS";

    private Context context;
    private int vehicleCount = 0;

    private void FordPassInfo(Context context) {
        WebView surveyWebview = new WebView(this);
        surveyWebview.getSettings().setJavaScriptEnabled(true);

        Misc.checkDarkMode(context, surveyWebview);
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                .build();
        surveyWebview.setWebViewClient(new LocalContentWebViewClient(assetLoader));
        String indexPage = "https://appassets.androidplatform.net/assets/fordpass.html";
        surveyWebview.loadUrl(indexPage);

        new AlertDialog.Builder(this)
                .setTitle(R.string.fordpass_description)
                .setNegativeButton("Close", (dialog, id) -> dialog.dismiss())
                .setView(surveyWebview).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();


//
//
//        String other = "{\"vin\":\"3FMTK3R75MMA09929\",\"status\":200,\"maxRangePossible\":null,\"plugInTime\":null,\"initialDte\":null,\"energy\":null,\"power\":null,\"chargeLocationName\":null,\"estimatedChargeEndTime\":null,\"chargeTargetLevel\":null,\"chargeType\":null,\"network\":null,\"gsscInitiatedBy\":null,\"chargeLocationAddress\":null,\"locationType\":null}";
//        DCFCInfo stuff2 = gson.fromJson(other, DCFCInfo.class);
//
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        long x = 0;
//        try {
//            cal.setTime(sdf.parse(stuff.getTime()));
//            x = cal.toInstant().toEpochMilli();
//        } catch (ParseException e) {
//            x = -1;
//        }

        // If we haven't bugged about the survey before, do it once and get it over with
        if (Misc.doSurvey(context)) {
            FordPassInfo(context);
        }

        // First thing, check logcat for a crash and save if so
        String crashMessage = Misc.checkLogcat(context);
        if (crashMessage != null) {
            Toast.makeText(context, crashMessage, Toast.LENGTH_SHORT).show();
        }

        // Initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);

        // Handle any changes to the app.
        AppUpdates.performUpdates(context);

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

        // Android 13 and later require user to allow posting of notifications
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                }).launch(Manifest.permission.POST_NOTIFICATIONS);
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

        // Do bookkeeping on old charging logs
        DCFC.purgeChargingData(context);

        // Create the webview containing instruction for use.
        WebView mWebView = findViewById(R.id.main_description);
        Misc.checkDarkMode(context, mWebView);
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                .build();
        mWebView.setWebViewClient(new LocalContentWebViewClient(assetLoader));
        String indexPage = "https://appassets.androidplatform.net/assets/index_page.html";
        mWebView.loadUrl(indexPage);

        // Update the widget
        CarStatusWidget.updateWidget(context);

        // Allow the app to display notifications
        Notifications.createNotificationChannels(context);
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

        // Hide view DCFC charging option if there isn't a log file
        if (!DCFC.logFileExists(context)) {
            menu.findItem(R.id.action_charge).setVisible(false);
        }

        // The PlayStore version doesn't do all the update stuff
        if (com.example.khughes.machewidget.BuildConfig.FLAVOR.equals("playstore")) {
            menu.findItem(R.id.action_update).setVisible(false);
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
//                                        Utils.restorePrefs(context, uri);
                                        new PrefManagement().restorePrefs(context, uri);
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
            Intent intent = new Intent(this, LoginActivity.class);
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
        } else if (id == R.id.action_reminder) {
            Intent intent = new Intent(this, ReminderActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_copylog) {
            String result = LogFile.copyLogFile(context);
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_backup) {
            new PrefManagement().savePrefs(context);
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
        } else if (id == R.id.action_charge) {
            Intent intent = new Intent(this, ChargingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_fordpass) {
            FordPassInfo(context);
        }
        return super.onOptionsItemSelected(item);
    }
}
