package com.example.khughes.machewidget

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.view.ContextThemeWrapper
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.example.khughes.machewidget.Misc.Companion.checkDarkMode
import com.example.khughes.machewidget.Misc.Companion.removeAPK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class UpdateActivity : AppCompatActivity() {

    // Create Locales we can use for other languages
    private val NORWAY_BOKMAL = Locale("no")
    private val SPANISH = Locale("es")
    private val PORTUGUESE = Locale("pt")
    private val POLISH = Locale("pl")
    private val FINNISH = Locale("fi")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = applicationContext

        val appInfo = StoredData(context)

        // Get new version value
        val newVersion = appInfo.latestVersion

        // Assume update is cancelled or abandoned, so set "new version" back to current version.
        appInfo.latestVersion = BuildConfig.VERSION_NAME

        // Make sure we have permission to install apps
        val packageName = "package:" + context.packageName
        if (!packageManager.canRequestPackageInstalls()) {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse(packageName)
                )
            )
        }

        setContentView(R.layout.activity_update)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = PROGRESS_INTERVAL
        progressBar.visibility = View.GONE

        val mWebView = findViewById<WebView>(R.id.changelog_webview)
        checkDarkMode(context, mWebView)
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES && WebViewFeature.isFeatureSupported(
                WebViewFeature.FORCE_DARK
            )
        ) {
            WebSettingsCompat.setForceDark(mWebView.settings, WebSettingsCompat.FORCE_DARK_ON)
        }
        val encodedHtml = Base64.encodeToString(
            getString(R.string.activity_update_downloading_html).toByteArray(),
            Base64.NO_PADDING
        )

        mWebView.loadData(encodedHtml, "text/html", "base64")

        downloadChangelog(mWebView, newVersion!!)

        val cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener { _: View? ->
            // If update is cancelled, then skip this version
            AlertDialog.Builder( //
                ContextThemeWrapper(this, R.style.AlertDialogCustom)
            )
                .setTitle(R.string.activity_update_ignore_dialog_title)
                .setMessage(R.string.activity_update_ignore_dialog_message)
                .setPositiveButton(
                    android.R.string.yes
                ) { _: DialogInterface?, _: Int ->
                    appInfo.latestVersion = newVersion
                    finish()
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { _: DialogInterface?, _: Int -> }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        val applyButton = findViewById<Button>(R.id.apply_button)
        applyButton.setOnClickListener { _: View? ->
            if (!context.packageManager.canRequestPackageInstalls()) {
                Toast.makeText(context, R.string.activity_update_app_permissions, Toast.LENGTH_LONG)
                    .show()
            } else {
                AlertDialog.Builder( //
                    ContextThemeWrapper(this, R.style.AlertDialogCustom)
                )
                    .setTitle(R.string.activity_update_updating_dialog_title)
                    .setMessage(R.string.activity_update_updating_dialog_message)
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _: DialogInterface?, _: Int ->
                        progressBar.visibility = View.VISIBLE
                        downloadApp(context, progressBar)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }
    }

    private fun downloadChangelog(webView: WebView, newVersion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val changelogPage =
                when (val language = Locale.getDefault().language) {
                    Locale.FRENCH.language,
                    Locale.GERMAN.language,
                    Locale.ITALIAN.language,
                    POLISH.language,
                    NORWAY_BOKMAL.language,
                    FINNISH.language,
                    SPANISH.language,
                    PORTUGUESE.language -> "https://raw.githubusercontent.com/khpylon/MachEWidget/master/changelogs/CHANGELOG_$language.md"
                    else -> "https://raw.githubusercontent.com/khpylon/MachEWidget/master/CHANGELOG.md"
                }

            // Try to read the change log
            val client = OkHttpClient.Builder().build()
            val request: Request = Request.Builder().url(changelogPage).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body
                val result = responseBody?.bytes()?.decodeToString() as String
                val log: String

                // Attempt to isolate only the info between the current version and he new version.
                val currentVersion = BuildConfig.VERSION_NAME
                val startIndex = result.indexOf("## $newVersion")
                val endIndex = result.indexOf("## $currentVersion", startIndex)
                log = if (startIndex in 0 until endIndex) {
                    result.substring(startIndex, endIndex)
                } else {
                    result
                }

                // Convert remaining data into HTML
                val parser = Parser.builder().build()
                val document = parser.parse(log)
                val htmlRenderer = HtmlRenderer.builder().build()
                val encodedHtml = Base64.encodeToString(
                    htmlRenderer.render(document).toByteArray(),
                    Base64.NO_PADDING
                )

                // Use post() to display, since a webview can only be update in main thread.
                webView.post {
                    run {
                        webView.loadData(encodedHtml, "text/html", "base64")
                    }
                }
            }
        }
    }

    private fun downloadApp(context: Context, progressBar: ProgressBar) {
        CoroutineScope(Dispatchers.IO).launch {
            var apkFile: File? = null

            // Pull APK file from GitHub
            val client = OkHttpClient.Builder().build()
            val request: Request = Request.Builder().url(APP_URL).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                // Remove the last APK, if it exists, and create a new file
                apkFile = removeAPK(context)
                apkFile.createNewFile()

                //  Process the body of the response, and figure out how large it is
                val responseBody = response.body as ResponseBody
                val fileLength = responseBody.contentLength()

                // Create streams
                val input = responseBody.byteStream()
                val output = FileOutputStream(apkFile)

                // Read the data 32K at a time
                val data = ByteArray(32768)
                var c: Int
                var currentSize = 0
                var lastSize = 0
                while ((input.read(data).also { c = it }) != -1) {
                    output.write(data, 0, c)
                    currentSize += c
                    if ((currentSize - lastSize) * PROGRESS_INTERVAL / fileLength > 0) {
                        progressBar.progress =
                            ((currentSize * PROGRESS_INTERVAL) / fileLength).toInt()
                        lastSize = currentSize
                    }
                }

                // Close files
                input.close()
                output.close()
            }

            // When finished, hide progress bar.  This needs to be done in same thread where the
            // widget was created, so use withContext() to throw back to Main thread
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
            }

            // If we read the file, prepare to install it
            if (apkFile != null) {
                val apkURI = FileProvider.getUriForFile(
                    context.applicationContext,
                    context.packageName + ".provider",
                    apkFile
                )
                val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive")
                context.startActivity(intent)
            }
        }
    }

    companion object {
        private const val APP_URL =
            "https://github.com/khpylon/MachEWidget/blob/master/app/github/release/app-release.apk?raw=true"
        private const val PROGRESS_INTERVAL = 50
    }
}
