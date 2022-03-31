package com.example.khughes.machewidget;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateActivity extends AppCompatActivity {

    private static final String ACTION_INSTALL_COMPLETE = "com.example.khughes.machewidget.INSTALL_COMPLETE";
    private static final String CHANGELOG_URL = "https://raw.githubusercontent.com/khpylon/MachEWidget/master/CHANGELOG.md";
    private static final String appUrl = "https://github.com/khpylon/MachEWidget/blob/master/app/release/app-release.apk?raw=true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();

        String newVersion = "## " + new StoredData(context).getLatestVersion();
        String currentVersion = "## " + BuildConfig.VERSION_NAME;

        if (newVersion.compareTo(currentVersion) <= 0) {
            Toast.makeText(context, "No update found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_update);

        WebView mWebView = findViewById(R.id.changelog_webview);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(mWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
        String encodedHtml = Base64.encodeToString("<html><body><h2>Attempting to download CHANGELOG.md file...</h2></body></html>".getBytes(), Base64.NO_PADDING);

        mWebView.loadData(encodedHtml, "text/html", "base64");

        new DownloadChangelog(context, mWebView).execute(CHANGELOG_URL);

        Button applyButton = findViewById(R.id.apply_button);
        applyButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("Notice")
                .setMessage("After the app is updated, it will close.")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> new DownloadApp(context).execute(appUrl))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
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
                current = new StringBuilder("<html><body><h2>Error occurred downloading CHANGELOG.md</h2></body></html>");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return current.toString();
        }

        protected void onPostExecute(String result) {
            Context context = mContext.get();

            // Attempt to isolate only the inform between the current version and he new vesion.
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

    private static class DownloadApp extends AsyncTask<String, String, File> {

        private final WeakReference<Context> mContext;

        public DownloadApp(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected File doInBackground(String... urls) {
            Context context = mContext.get();
            File apkFile;
            HttpURLConnection urlConnection = null;
            try {
                apkFile = removeAPK(context);
                apkFile.createNewFile();
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                FileOutputStream output = new FileOutputStream(apkFile);
                byte[] data = new byte[8192];
                int c;
                while ((c = input.read(data)) != -1) {
                    output.write(data, 0, c);
                }
                input.close();
                output.close();
            } catch (Exception e) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "exception in UpdateReceiver.DownloadApp()" + e);
                apkFile = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return apkFile;
        }

        protected void onPostExecute(File apkFile) {
            Context context = mContext.get();
            PackageInstaller pi = context.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            params.setAppPackageName(context.getPackageName());
            try {
                int sessionId = pi.createSession(params);
                PackageInstaller.Session session = pi.openSession(sessionId);
                InputStream in = new FileInputStream(apkFile);
                OutputStream out = session.openWrite("package", 0, apkFile.length());
                byte[] buffer = new byte[65536];
                int c;
                while ((c = in.read(buffer)) != -1) {
                    out.write(buffer, 0, c);
                }
                session.fsync(out);
                in.close();
                out.close();

                Intent intent = new Intent(context, UpdateActivity.class);
                intent.setAction(ACTION_INSTALL_COMPLETE);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                IntentSender statusReceiver = pendingIntent.getIntentSender();

                // Commit the session (this will start the installation workflow).
                session.commit(statusReceiver);
                session.close();
//                apkFile.delete();
            } catch (IOException e) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "exception in UpdateReceiver.DownloadApp()" + e);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (ACTION_INSTALL_COMPLETE.equals(intent.getAction())) {
            int status = extras.getInt(PackageInstaller.EXTRA_STATUS);
            String message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
            switch (status) {
                case PackageInstaller.STATUS_PENDING_USER_ACTION:
                    // This test app isn't privileged, so the user has to confirm the install.
                    Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
                    startActivity(confirmIntent);
                    break;
                case PackageInstaller.STATUS_SUCCESS:
                    Toast.makeText(this, "Install succeeded!", Toast.LENGTH_SHORT).show();
                    break;
                case PackageInstaller.STATUS_FAILURE_ABORTED:
                    Toast.makeText(this, "Install aborted.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case PackageInstaller.STATUS_FAILURE:
                case PackageInstaller.STATUS_FAILURE_BLOCKED:
                case PackageInstaller.STATUS_FAILURE_CONFLICT:
                case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                case PackageInstaller.STATUS_FAILURE_INVALID:
                case PackageInstaller.STATUS_FAILURE_STORAGE:
                    Toast.makeText(this, "Install failed! " + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Unrecognized status received from installer: " + status,
                            Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static File removeAPK(Context context) {
        File apkFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app-release.apk");
        apkFile.delete();
        return apkFile;
    }
}