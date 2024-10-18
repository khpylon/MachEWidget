package com.example.khughes.machewidget

import android.Manifest
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
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
import com.example.khughes.machewidget.MainActivity.Companion.CHANNEL_ID
import com.example.khughes.machewidget.Misc.Companion.checkDarkMode
import com.example.khughes.machewidget.Misc.Companion.checkLogcat
import com.example.khughes.machewidget.Misc.Companion.doSurvey
import com.example.khughes.machewidget.Notifications.Companion.batteryOptimization
import com.example.khughes.machewidget.Notifications.Companion.createNotificationChannels
import com.example.khughes.machewidget.StatusReceiver.Companion.initateAlarm
import com.example.khughes.machewidget.StatusReceiver.Companion.nextAlarm
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import java.io.IOException
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object {
        const val CHANNEL_ID = "934TXS"
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun checkCommandsEnabled(context: Context) {

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        // Check status of remote commands and forced updates
        val commandsKey = context.resources.getString(R.string.enable_commands_key)
        val commands = prefs.getBoolean(commandsKey, false)
        val forcedKey = context.resources.getString(R.string.user_forcedUpdate_key)
        val forced = prefs.getBoolean(forcedKey, false)

        // if either is set, let the user know why they're disabled
        if (commands || forced) {
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle(context.getString(R.string.remote_commands_disabled_title))
                .setNegativeButton(context.getString(R.string.close_button)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                .setMessage(
                    context.getString(R.string.remote_commands_disabled_description)
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
        val context = applicationContext
        LogFile.defineContext(context)

        // First thing, check logcat for a crash and save if so
        val crashMessage = checkLogcat(context)
        if (crashMessage != null) {
            Toast.makeText(context, crashMessage, Toast.LENGTH_SHORT).show()
        }

        // If commands enabled, disable and show message to user to explain why
        checkCommandsEnabled(context)

        // Initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false)

        // Handle any changes to the app.
        performUpdates(context)

        // Initiate check for a new app version
        UpdateReceiver.initiateAlarm(context)

        // See if we need to notify user about battery optimizations
        batteryOptimization(context)

        // Initiate update of the widget
        val updateIntent = Intent()
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        context.sendBroadcast(updateIntent)

        // Older versions require permission to write log files
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    registerForActivityResult(RequestPermission()) { }.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }
        }

        // Android 13 and later require user to allow posting of notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    registerForActivityResult(RequestPermission()) { }.launch(
                        Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        // If app has been running OK, try to initiate status updates and count number of vehicles in the database
        initateAlarm(context)

        // Do bookkeeping on old charging logs
        purgeChargingData(context)

        // Update the widget
        updateWidget(context)

        // Allow the app to display notifications
        createNotificationChannels(context)

        // TODO: probably not necessary anymore
//        // If we get into a weird state where the user appears to have logged in but there are no
//        // vehicles, send them to Manage Vehicles
//        CoroutineScope(Dispatchers.IO).launch {
//            val info = InfoRepository(context)
//            if (info.user.programState == Constants.STATE_HAVE_TOKEN && info.vehicles.isEmpty()) {
//                val intent = Intent(context, VehicleActivity::class.java)
//                startActivity(intent)
//            }
//        }

        setContent {
            MacheWidgetTheme {
                Surface (color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    DisplayWebview()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }
}


// Create Locales we can use for other languages
private val NORWAY_BOKMAL = Locale("nb")
private val SPANISH = Locale("es")
private val PORTUGUESE = Locale("pt")
private val POLISH = Locale("pl")
private val FINNISH = Locale("fi")

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

@SuppressLint("SetJavaScriptEnabled", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayWebview() {

    // fetching local context
    val context = LocalContext.current

    // If we haven't bugged about the survey before, do it once and get it over with
    if (doSurvey(context)) {
    }

    // Create a boolean variable
    // to store the display menu state
    var mDisplayMenu by remember { mutableStateOf(false) }

    // Callback for choosing setting file to restore
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let  {
            val type = context.contentResolver.getType(uri)
            try {
                if (type == Constants.APPLICATION_JSON ||
                    (type == Constants.APPLICATION_OCTETSTREAM && uri.path!!.endsWith(
                        ".json"
                    ) )
                ) {
                    PrefManagement().restorePrefs(context, uri)
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            } catch (e: IOException) {
                Log.e(
                    CHANNEL_ID,
                    "exception in MainActivity.DisplayWebview(): ",
                    e
                )
            }
        }
    }

    Scaffold (
        topBar = {
            // Creating a Top bar
            TopAppBar(
                title = { Text(context.getString(R.string.app_name)) },
                actions = {

                    // Creating Icon button for dropdown menu
                    IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                        Icon(Icons.Default.MoreVert, "")
                    }

                    // Creating a dropdown menu
                    DropdownMenu(
                        expanded = mDisplayMenu,
                        onDismissRequest = { mDisplayMenu = false },
                    ) {
                        // Add in each menu item
                        DropdownMenuItem(
                            onClick = {
                                val intent = Intent(context, VehicleActivity::class.java)
                                context.startActivity(intent)
                                mDisplayMenu = false
                            },
                            text = { Text(text = context.getString(R.string.action_vehicle)) }
                        )
                        DropdownMenuItem(
                            onClick = {
                                nextAlarm(context, 5)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.refresh_in_5_seconds_description),
                                    Toast.LENGTH_SHORT
                                ).show()
                                mDisplayMenu = false
                            },
                            text = { Text(text = context.getString(R.string.action_refresh)) }
                        )
                        // Only enable "Choose Linked Apps" if app buttons are enabled
                        val showAppLinks = PreferenceManager.getDefaultSharedPreferences(context)
                            .getBoolean(context.resources.getString(R.string.show_app_links_key), true)
                        if (showAppLinks) {
                            DropdownMenuItem(
                                onClick = {
                                    val intent = Intent(context, ChooseAppActivity::class.java)
                                    context.startActivity(intent)
                                    mDisplayMenu = false
                                },
                                text = { Text(text = context.getString(R.string.action_chooseapp)) }
                            )
                        }
                        // Only enable "Set Vehicle Color" if colors are enabled,
                        val colorsEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                            .getBoolean(context.resources.getString(R.string.use_colors_key), true)
                        if (colorsEnabled) {
                            DropdownMenuItem(
                                onClick = {
                                    val intent = Intent(context, ColorActivity::class.java)
                                    context.startActivity(intent)
                                    mDisplayMenu = false
                                },
                                text = { Text(text = context.getString(R.string.action_color)) }
                            )
                        }
                        DropdownMenuItem(
                            onClick = {
                                val intent = Intent(context, ReminderActivity::class.java)
                                context.startActivity(intent)
                                mDisplayMenu = false
                            },
                            text = { Text(text = context.getString(R.string.action_reminder)) }
                        )
                        DropdownMenuItem(
                            onClick = {
                                PrefManagement().savePrefs(context)
                                mDisplayMenu = false
                            },
                            text = { Text(text = context.getString(R.string.action_backup)) }
                        )
                        DropdownMenuItem(
                            onClick = {
                                launcher.launch(arrayOf(Constants.APPLICATION_JSON, Constants.APPLICATION_OCTETSTREAM))
                                mDisplayMenu = false
                            },
                            text = { Text(text = context.getString(R.string.action_restore)) }
                        )
                        // Only enable "Set Vehicle Color" if colors are enabled,
                        val loggingEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                            .getBoolean(context.resources.getString(R.string.logging_key), true)
                        if (loggingEnabled) {
                            DropdownMenuItem(
                                onClick = {
                                    val result = copyLogFile(context)
                                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                                    mDisplayMenu = false
                                },
                                text = { Text(text = context.getString(R.string.action_copylog)) }
                            )
                        }
                        DropdownMenuItem(
                            onClick = {
                                val intent = Intent(context, ChargingActivity::class.java)
                                context.startActivity(intent)
                            },
                            text = { Text(text = context.getString(R.string.action_view_dcfc_logs)) }
                        )
                        // The GitHub version does all the update stuff
                        @Suppress("KotlinConstantConditions")
                        if (BuildConfig.FLAVOR == "github") {
                            DropdownMenuItem(
                                onClick = {
                                    val intent = Intent(context, UpdateReceiver::class.java)
                                    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                    context.sendBroadcast(intent)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.update_check_description),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                text = { Text(text = context.getString(R.string.action_update)) }
                            )
                        }
                        DropdownMenuItem(
                            onClick = {
                                val intent = Intent(context, SettingsActivity::class.java)
                                context.startActivity(intent)
                                mDisplayMenu = false
                            },
                            text = { Text(text = context.getString(R.string.action_settings)) }
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 12.dp),

    ) { innerPadding ->
        Column (modifier = Modifier.padding(innerPadding)) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = WebViewClient()
                    }
                },
                update = { webView ->
                    val assetLoader = WebViewAssetLoader.Builder()
                        .addPathHandler("/assets/", AssetsPathHandler(context))
                        .addPathHandler("/res/", ResourcesPathHandler(context))
                        .build()
                    webView.webViewClient = LocalContentWebViewClient(assetLoader)
                    checkDarkMode(context, webView)

                    // Use the system language to choose which webpage to display
                    val language = Locale.getDefault().language
                    val indexPage =
                        when (language) {
                            Locale.FRENCH.language -> "https://appassets.androidplatform.net/assets/index_page_fr.html"
                            Locale.GERMAN.language -> "https://appassets.androidplatform.net/assets/index_page_de.html"
                            Locale.ITALIAN.language -> "https://appassets.androidplatform.net/assets/index_page_it.html"
                            POLISH.language -> "https://appassets.androidplatform.net/assets/index_page_pl.html"
                            NORWAY_BOKMAL.language -> "https://appassets.androidplatform.net/assets/index_page_nb.html"
                            FINNISH.language ->  "https://appassets.androidplatform.net/assets/index_page_fi.html"
                            SPANISH.language -> "https://appassets.androidplatform.net/assets/index_page_es.html"
                            PORTUGUESE.language -> "https://appassets.androidplatform.net/assets/index_page_pt.html"
                            else -> "https://appassets.androidplatform.net/assets/index_page.html"
                        }
                    webView.loadUrl(indexPage)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LightPreview() {
    MacheWidgetTheme(dynamicColor = false) {
        DisplayWebview()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DarkPreview() {
    MacheWidgetTheme(dynamicColor = false) {
        DisplayWebview()
    }
}
