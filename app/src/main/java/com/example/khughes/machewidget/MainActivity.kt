package com.example.khughes.machewidget

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewAssetLoader.ResourcesPathHandler
import androidx.webkit.WebViewClientCompat
import com.example.khughes.machewidget.AppUpdates.performUpdates
import com.example.khughes.machewidget.CarStatusWidget.Companion.updateWidget
import com.example.khughes.machewidget.DCFC.Companion.purgeChargingData
import com.example.khughes.machewidget.LogFile.copyLogFile
import com.example.khughes.machewidget.Misc.Companion.checkDarkMode
import com.example.khughes.machewidget.Misc.Companion.checkLogcat
import com.example.khughes.machewidget.Misc.Companion.doSurvey
import com.example.khughes.machewidget.Notifications.Companion.batteryOptimization
import com.example.khughes.machewidget.Notifications.Companion.createNotificationChannels
import com.example.khughes.machewidget.StatusReceiver.Companion.initateAlarm
import com.example.khughes.machewidget.StatusReceiver.Companion.nextAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    private fun fordPassInfo(context: Context?) {
        val surveyWebview = WebView(this)
        surveyWebview.settings.javaScriptEnabled = true
        checkDarkMode(context!!, surveyWebview)
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(this))
            .addPathHandler("/res/", ResourcesPathHandler(this))
            .build()
        surveyWebview.webViewClient = LocalContentWebViewClient(assetLoader)
        val indexPage = "https://appassets.androidplatform.net/assets/fordpass.html"
        surveyWebview.loadUrl(indexPage)
        AlertDialog.Builder(this)
            .setTitle(R.string.fordpass_description)
            .setNegativeButton("Close") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setView(surveyWebview).show()
    }

    private fun checkCommandsEnabled(activity: Activity, context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        // Check status of remote commands and forced updates
        val commandsKey = context.resources.getString(R.string.enable_commands_key)
        val commands = prefs.getBoolean(commandsKey, false)
        val forcedKey = context.resources.getString(R.string.user_forcedUpdate_key)
        val forced = prefs.getBoolean(forcedKey, false)

        // if either is set, let the user know why they're disabled
        if (commands || forced) {
            AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogCustom))
                .setTitle("Remote commands have been disabled.")
                .setNegativeButton("Close") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                .setMessage(
                    "Ford has changed the API and remote commands (lock and start) no longer work.  If they are" +
                            "exposed in the future public API, they will automatically be re-enabled in the app."
                )
                .show()

            // disable active settings and store them for future use
            var result = StoredData.NO_COMMANDS
            if (commands) {
                result += StoredData.REMOTE_COMMANDS
                prefs.edit().putBoolean(commandsKey, false).commit()
            }
            if (forced) {
                result += StoredData.FORCED_REFRESH
                prefs.edit().putBoolean(forcedKey, false).commit()
            }
            val appInfo = StoredData(context)
            appInfo.remoteCommands = result
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppCompat)
        setContentView(R.layout.activity_main)

        // If we haven't bugged about the survey before, do it once and get it over with
        if (doSurvey(applicationContext)) {
            fordPassInfo(applicationContext)
        }

        // First thing, check logcat for a crash and save if so
        val crashMessage = checkLogcat(applicationContext)
        if (crashMessage != null) {
            Toast.makeText(applicationContext, crashMessage, Toast.LENGTH_SHORT).show()
        }

        checkCommandsEnabled(this, applicationContext)

        // Initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false)

        // Handle any changes to the app.
        performUpdates(applicationContext)

        // Initiate check for a new app version
        UpdateReceiver.initiateAlarm(applicationContext)

        // See if we need to notify user about battery optimizations
        batteryOptimization(applicationContext)

        // Initiate update of the widget
        val updateIntent = Intent()
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        applicationContext.sendBroadcast(updateIntent)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                }
            }
        }

        // Android 13 and later require user to allow posting of notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.POST_NOTIFICATIONS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { }.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        // If app has been running OK, try to initiate status updates and count number of vehicles in the database
        val userId = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getString(applicationContext.resources.getString(R.string.userId_key), null)
        if (userId != null) {
            Thread {
                val info = InfoRepository(applicationContext)
                val userInfo = info.user
                if (userInfo.programState == Constants.STATE_HAVE_TOKEN_AND_VIN) {
                    initateAlarm(applicationContext)
                }
            }.start()
        }

        // Do bookkeeping on old charging logs
        purgeChargingData(applicationContext)

        // Create the webview containing instruction for use.
        val mWebView = findViewById<WebView>(R.id.main_description)
        checkDarkMode(applicationContext, mWebView)
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(this))
            .addPathHandler("/res/", ResourcesPathHandler(this))
            .build()
        mWebView.webViewClient = LocalContentWebViewClient(assetLoader)
        val indexPage = "https://appassets.androidplatform.net/assets/index_page.html"
        mWebView.loadUrl(indexPage)

        // Update the widget
        updateWidget(applicationContext)

        // Allow the app to display notifications
        createNotificationChannels(applicationContext)

        // If we get into a weird state where the user appears to have logged in but there are no
        // vehicles, send them to Manage Vehicles
        CoroutineScope(Dispatchers.IO).launch {
            val info = InfoRepository(applicationContext)
            if (info.user.programState == Constants.STATE_HAVE_TOKEN && info.vehicles.isEmpty()) {
                val intent = Intent(applicationContext, VehicleActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this@MainActivity, "Permission Granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    private class LocalContentWebViewClient(private val mAssetLoader: WebViewAssetLoader) :
        WebViewClientCompat() {
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            return mAssetLoader.shouldInterceptRequest(request.url)
        }

        // to support API < 21
        @Deprecated("Deprecated in Java")
        override fun shouldInterceptRequest(
            view: WebView,
            url: String
        ): WebResourceResponse? {
            return mAssetLoader.shouldInterceptRequest(Uri.parse(url))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // Only enable "Choose Linked Apps" if app buttons are enabled
        val showAppLinks = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(applicationContext.resources.getString(R.string.show_app_links_key), true)
        menu.findItem(R.id.action_chooseapp).isEnabled = showAppLinks

        // Only enable "Save Logfile" if logging is enabled,
        val loggingEnabled = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(applicationContext.resources.getString(R.string.logging_key), true)
        menu.findItem(R.id.action_copylog).isEnabled = loggingEnabled

        // Only enable "Set Vehicle Color" if colors are enabled,
        val colorsEnabled = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(applicationContext.resources.getString(R.string.use_colors_key), true)
        menu.findItem(R.id.action_color).isVisible = colorsEnabled

        // The PlayStore version doesn't do all the update stuff
        @Suppress("KotlinConstantConditions")
        if (BuildConfig.FLAVOR == "playstore") {
            menu.findItem(R.id.action_update).isVisible = false
        }

        // Only show the DCFC log option if there are electric vehicles
        val appInfo = StoredData(applicationContext)
        menu.findItem(R.id.action_charge).isVisible = appInfo.electricVehicles

        return true
    }

    // Callback for choosing setting file to restore
    private var restoreSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult?> {
            override fun onActivityResult(result: ActivityResult?) {
                if (result?.resultCode == RESULT_OK) {
                    val data = result.data
                    if (data != null) {
                        val uri = data.data
                        if (uri != null) {
                            val type = applicationContext.contentResolver.getType(uri)
                            try {
                                if (type == Constants.APPLICATION_JSON || type == Constants.APPLICATION_OCTETSTREAM && uri.path!!.endsWith(
                                        ".json"
                                    )
                                ) {
//                                        Utils.restorePrefs(context, uri);
                                    PrefManagement().restorePrefs(applicationContext, uri)
                                } else {
                                    return
                                }
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } catch (e2: IOException) {
                                Log.e(
                                    CHANNEL_ID,
                                    "exception in MainActivity.restoreSettingsLauncher: ",
                                    e2
                                )
                            }
                        }
                    }
                }
            }
        }
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_login -> {
                // Depending on whether profiles are being used, either start the profile manager or go straight to login screen
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_refresh -> {
                nextAlarm(applicationContext, 5)
                Toast.makeText(
                    applicationContext,
                    "Refresh scheduled in 5 seconds.",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }

            R.id.action_chooseapp -> {
                val intent = Intent(this, ChooseAppActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_vehicle -> {
                val intent = Intent(this, VehicleActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_color -> {
                val intent = Intent(this, ColorActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_reminder -> {
                val intent = Intent(this, ReminderActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_copylog -> {
                val result = copyLogFile(applicationContext)
                Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.action_backup -> {
                PrefManagement().savePrefs(applicationContext)
                return true
            }

            R.id.action_restore -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "application/*"
                val mimeTypes =
                    arrayOf(Constants.APPLICATION_JSON, Constants.APPLICATION_OCTETSTREAM)
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                restoreSettingsLauncher.launch(intent)
                return true
            }

            R.id.action_update -> {
                val intent = Intent(this, UpdateReceiver::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                applicationContext.sendBroadcast(intent)
                Toast.makeText(
                    applicationContext,
                    "Checking for an update; if one is found, a notification will appear.",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }

            R.id.action_charge -> {
                val intent = Intent(this, ChargingActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_fordpass -> {
                fordPassInfo(applicationContext)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val CHANNEL_ID = "934TXS"
    }
}