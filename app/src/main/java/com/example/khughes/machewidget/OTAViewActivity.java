package com.example.khughes.machewidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;

import com.example.khughes.machewidget.OTAStatus.FuseResponse;
import com.example.khughes.machewidget.OTAStatus.FuseResponseList;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;
import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class OTAViewActivity extends AppCompatActivity {

    public static String convertDate(String UTCdate, String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.OTATIMEFORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            cal.setTime(sdf.parse(UTCdate));
            sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            return sdf.format(cal.getTime());
        } catch (ParseException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in OTAViewActivity.convertDate: ", e);
            return "";
        }
    }

    public static long convertDateToMillis(String UTCdate) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.OTATIMEFORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            cal.setTime(sdf.parse(UTCdate));
            return cal.toInstant().toEpochMilli();
        } catch (ParseException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in OTAViewActivity.convertDateToMillis: ", e);
            return 0;
        }
    }

    public static String convertMillisToDate(long millis, String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        cal.setTimeInMillis(millis);
        return sdf.format(cal.getTime());
    }

    public static long getLastOTATimeInMillis(Context context, String format) {
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String lastOTATime = sharedPref.getString(context.getResources().getString(R.string.last_ota_time), "0");
        if (lastOTATime.contains(":")) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf;
            // Determine the format of the date
            if (lastOTATime.length() == Constants.OLDLOCALTIMEFORMAT.length()) {
                sdf = new SimpleDateFormat(Constants.OLDLOCALTIMEFORMAT, Locale.ENGLISH);
            } else {
                sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            }
            try {
                cal.setTime(sdf.parse(lastOTATime));
            } catch (ParseException e) {
                Log.e(MainActivity.CHANNEL_ID, "exception in OTAViewActivity.getLastOTATimeInMillis: ", e);
            }
            return cal.toInstant().toEpochMilli();
        } else {
            return Long.parseLong(lastOTATime);
        }
    }

    private static long currentOTATime = 0;
    private static long lastOTATime;

    private static Button clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otaview);
        String VIN = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getResources().getString(R.string.VIN_key), "");

        Context context = getApplicationContext();

        WebView mWebView = findViewById(R.id.ota_webview);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(mWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }

        clear = findViewById(R.id.button);
        clear.setOnClickListener(view -> {
            if (currentOTATime > 0) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPref.edit().putString(context.getResources().getString(R.string.last_ota_time), Long.valueOf(currentOTATime).toString()).apply();
                MainActivity.updateWidget(context);
                clear.setEnabled(false);
            }
        });
        new DownloadChangelog(context, mWebView).execute(VIN);
    }

    private static class DownloadChangelog extends AsyncTask<String, String, OTAStatus> {

        private final WeakReference<Context> mContext;
        private final WeakReference<WebView> mWebView;
        private String dateFormat;

        public DownloadChangelog(Context context, WebView webView) {
            mContext = new WeakReference<>(context);
            mWebView = new WeakReference<>(webView);
        }

        @Override
        protected OTAStatus doInBackground(String... VINs) {
            Context context = mContext.get();

            VehicleInfo vehInfo = VehicleInfoDatabase.getInstance(context)
                    .vehicleInfoDao().findVehicleInfoByVIN(VINs[0]);
            if (vehInfo == null) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "OTAViewActivity.DownloadChangelog(): vehicle info is null for VIN " + VINs[0]);
                return null;
            }
            UserInfo userInfo = UserInfoDatabase.getInstance(context)
                    .userInfoDao().findUserInfo(vehInfo.getUserId());

            dateFormat = userInfo.getCountry().equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
            OTAStatus ota = vehInfo.toOTAStatus();

            return ota;
        }

        protected void onPostExecute(OTAStatus ota) {
            Context context = mContext.get();

            StringBuilder unencodedHtml = new StringBuilder("<html><body>");
            if (ota != null && ota.getFuseResponse() != null) {
                String tmp;
                for (FuseResponseList fuse : ota.getFuseResponse().getFuseResponseList()) {
                    if (fuse != null) {
                        if (fuse.getLatestStatus() != null) {
                            currentOTATime = convertDateToMillis(fuse.getLatestStatus().getDateTimestamp());
                            unencodedHtml.append("<b>Latest status</b><ul>");
                            unencodedHtml.append("<li><b>Aggregate:</b> ");
                            unencodedHtml.append((tmp = fuse.getLatestStatus().getAggregateStatus()) != null ? tmp : "");
                            unencodedHtml.append("<li><b>Detailed:</b> ");
                            unencodedHtml.append((tmp = fuse.getLatestStatus().getDetailedStatus()) != null ? tmp : "");
                            unencodedHtml.append("<li><b>Time stamp:</b> ");
                            unencodedHtml.append(convertMillisToDate(currentOTATime, dateFormat));
                            unencodedHtml.append("</ul><p>");
                        }
                        unencodedHtml.append("<b>Other info</b><ul>");
                        unencodedHtml.append("<li><b>CorrelationID:</b> ");
                        unencodedHtml.append(((tmp = fuse.getOemCorrelationId()) != null ? tmp : ""));
                        unencodedHtml.append("<li><b>Deployment</b>");
                        unencodedHtml.append("    <ul><li><b>Created:</b> ");
                        unencodedHtml.append(convertDate(fuse.getDeploymentCreationDate(), dateFormat));
                        unencodedHtml.append("    <li><b>Expires:</b> ");
                        unencodedHtml.append(convertDate(fuse.getDeploymentExpirationTime(), dateFormat));
                        unencodedHtml.append("</ul><li><b>Communication priority:</b> ");
                        unencodedHtml.append(((tmp = fuse.getCommunicationPriority()) != null ? tmp : ""));
                        unencodedHtml.append("<li><b>Type:</b> ");
                        unencodedHtml.append(((tmp = fuse.getType()) != null ? tmp : ""));
                        unencodedHtml.append("<li><b>Final action:</b> \"");
                        unencodedHtml.append(((tmp = fuse.getDeploymentFinalConsumerAction()) != null ? tmp : ""));
                        unencodedHtml.append("\"</ul><hr>");
                    } else {
                        unencodedHtml.append("<b>No specific information found</b><p>");
                    }
                }
                String description = ota.getDescription();
                if (description != null) {
                    unencodedHtml.append("<b>Description:</b><p>");
                    unencodedHtml.append(description.replace("\n", "<br>"));
                    unencodedHtml.append("<hr>");
                }
            } else {
                unencodedHtml.append("OTA Status is <i>unavailable</i>");
            }
            unencodedHtml.append("</body></html>");
            lastOTATime = getLastOTATimeInMillis(context, dateFormat);


            String encodedHtml = Base64.encodeToString(unencodedHtml.toString().getBytes(),
                    Base64.NO_PADDING);
            mWebView.get().loadData(encodedHtml, Constants.TEXT_HTML, "base64");

            clear.setEnabled(currentOTATime > lastOTATime);

        }
    }

}
