package com.example.khughes.machewidget;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateActivity extends AppCompatActivity {

    private static final String CHANGELOG_URL = "https://raw.githubusercontent.com/khpylon/MachEWidget/master/CHANGELOG.md";
    private static final String appUrl = "https://github.com/khpylon/MachEWidget/blob/master/app/github/release/app-release.apk?raw=true";

    private static final int progressIntervals = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();

        StoredData appInfo = new StoredData(context);

        // Get new version value
        String newVersion = appInfo.getLatestVersion();

        // Assume update is cancelled or abandoned, so set "new version" back to current version.
        appInfo.setLatestVersion(BuildConfig.VERSION_NAME);

        // Make sure we have permission to install apps
        String packageName = "package:" + context.getPackageName();
        if (!getPackageManager().canRequestPackageInstalls()) {
            startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse(packageName)));
        }

        setContentView(R.layout.activity_update);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(progressIntervals);
        progressBar.setVisibility(View.GONE);

        WebView mWebView = findViewById(R.id.changelog_webview);
        Misc.checkDarkMode(context, mWebView);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(mWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
        String encodedHtml = Base64.encodeToString(getString(R.string.activity_update_downloading_html).getBytes(), Base64.NO_PADDING);

        mWebView.loadData(encodedHtml, "text/html", "base64");

        new DownloadChangelog(context, mWebView).execute(CHANGELOG_URL);

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> {
            // If update is cancelled, then skip this version
            new AlertDialog.Builder(//
                    new ContextThemeWrapper(this,R.style.AlertDialogCustom))

                    .setTitle(R.string.activity_update_ignore_dialog_title)
                    .setMessage(R.string.activity_update_ignore_dialog_message)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                                appInfo.setLatestVersion(newVersion);
                                finish();
                            }
                    )
                    .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                            }
                    )
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        Button applyButton = findViewById(R.id.apply_button);
        applyButton.setOnClickListener(view -> {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                Toast.makeText(context, R.string.activity_update_app_permissions, Toast.LENGTH_LONG).show();
            } else {
                new AlertDialog.Builder(//
                        new ContextThemeWrapper(this,R.style.AlertDialogCustom))
                        .setTitle(R.string.activity_update_updating_dialog_title)
                        .setMessage(R.string.activity_update_updating_dialog_message)
                        .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                            progressBar.setVisibility(View.VISIBLE);
                            new DownloadApp(context, progressBar).execute(appUrl);
                        }
                        )
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private static class DownloadChangelog extends AsyncTask<String, String, String> {

        private final WeakReference<Context> mContext;
        private final WeakReference<WebView> mWebView;

        public DownloadChangelog(Context context, WebView webView) {
            mContext = new WeakReference<>(context);
            mWebView = new WeakReference<>(webView);
        }

        @Override
        protected String doInBackground(String... urls) {
            Context context = mContext.get();

            StringBuilder current = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                byte[] data = new byte[8192];
                int count;
                while ((count = input.read(data)) != -1) {
                    current.append(new String(data, 0, count, StandardCharsets.UTF_8));
                }
                input.close();
            } catch (Exception e) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "exception in UpdateReceiver.DownloadChangelog()" + e);
                current = new StringBuilder(context.getString(R.string.activity_update_download_error_html));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return current.toString();
        }

        protected void onPostExecute(String result) {
            Context context = mContext.get();

            // Attempt to isolate only the inform between the current version and he new version.
            String newVersion = "## " + new StoredData(context).getLatestVersion();
            String currentVersion = "## " + BuildConfig.VERSION_NAME;
            int startIndex = result.indexOf(newVersion);
            int endIndex = result.indexOf(currentVersion, startIndex);
            if (startIndex >= 0 && endIndex > startIndex) {
                result = result.substring(startIndex, endIndex);
            }

            Parser parser = Parser.builder().build();
            Node document = parser.parse(result);
            HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
            String encodedHtml = Base64.encodeToString(htmlRenderer.render(document).getBytes(), Base64.NO_PADDING);
            mWebView.get().loadData(encodedHtml, "text/html", "base64");
        }
    }

    private static class DownloadApp extends AsyncTask<String, Integer, File> {

        private final WeakReference<Context> mContext;
        private final WeakReference<ProgressBar> mProgressBar;

        public DownloadApp(Context context, ProgressBar mProgress) {
            mContext = new WeakReference<>(context);
            mProgressBar = new WeakReference<>(mProgress);
        }

        @Override
        protected File doInBackground(String... urls) {
            Context context = mContext.get();
            File apkFile;
            HttpURLConnection urlConnection = null;
            try {
                apkFile = Misc.removeAPK(context);
                apkFile.createNewFile();
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                int fileLength = urlConnection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                FileOutputStream output = new FileOutputStream(apkFile);
                byte[] data = new byte[8192];
                int c;
                int currentSize = 0;
                int lastSize = 0;
                while ((c = input.read(data)) != -1) {
                    output.write(data, 0, c);
                    currentSize += c;
                    if((currentSize - lastSize)*progressIntervals/fileLength > 0 ) {
                        publishProgress((currentSize*progressIntervals)/fileLength);
                        lastSize = currentSize;
                    }
                }
                input.close();
                output.close();
            } catch (Exception e) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "exception in UpdateActivity.DownloadApp()" + e);
                apkFile = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return apkFile;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.get().setProgress(values[0]);
        }

        protected void onPostExecute(File apkFile) {
            Context context = mContext.get();
            mProgressBar.get().setVisibility(View.GONE);
             if (apkFile != null) {
                Uri apkURI = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".provider", apkFile);
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                context.startActivity(intent);
            }
        }
    }
}
