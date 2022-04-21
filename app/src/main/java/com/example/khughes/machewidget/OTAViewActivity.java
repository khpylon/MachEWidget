package com.example.khughes.machewidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;

import com.example.khughes.machewidget.OTAStatus.FuseResponse;
import com.example.khughes.machewidget.OTAStatus.FuseResponseList;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;

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

    public static long getLastOTATimeInMillis(Context context) {
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
                sdf = new SimpleDateFormat(new StoredData(context).getTimeFormatByCountry(VIN), Locale.ENGLISH);
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

    private long currentOTATime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otaview);
        String VIN = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getResources().getString(R.string.VIN_key), "");

        Context context = getApplicationContext();

        OTAStatus ota = new StoredData(context).getOTAStatus(VIN);
        StringBuilder unencodedHtml = new StringBuilder("<html><body>");
        String dateFormat = new StoredData(context).getTimeFormatByCountry(VIN);
        if (ota != null && ota.getFuseResponse() != null) {
            String tmp;
            for (FuseResponseList fuse : ota.getFuseResponse().getFuseResponseList()) {
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
                unencodedHtml.append( convertDate(fuse.getDeploymentCreationDate(), dateFormat));
                unencodedHtml.append("    <li><b>Expires:</b> ");
                unencodedHtml.append( convertDate(fuse.getDeploymentExpirationTime(), dateFormat) );
                unencodedHtml.append("</ul><li><b>Communication priority:</b> ");
                unencodedHtml.append( ((tmp = fuse.getCommunicationPriority()) != null ? tmp : ""));
                unencodedHtml.append("<li><b>Type:</b> ");
                unencodedHtml.append( ((tmp = fuse.getType()) != null ? tmp : ""));
                unencodedHtml.append("<li><b>Final action:</b> \"");
                unencodedHtml.append( ((tmp = fuse.getDeploymentFinalConsumerAction()) != null ? tmp : "") );
                unencodedHtml.append("\"</ul><hr>");
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

        String encodedHtml = Base64.encodeToString(unencodedHtml.toString().getBytes(),
                Base64.NO_PADDING);
        WebView mWebView = findViewById(R.id.ota_webview);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(mWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
        mWebView.loadData(encodedHtml, "text/html", "base64");

        long lastOTATime = getLastOTATimeInMillis(getApplicationContext());

        Button clear = findViewById(R.id.button);
        clear.setEnabled(currentOTATime > lastOTATime);
        clear.setOnClickListener(view -> {
            if (currentOTATime > 0) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPref.edit().putString(context.getResources().getString(R.string.last_ota_time), Long.valueOf(currentOTATime).toString()).apply();
                MainActivity.updateWidget(context);
                clear.setEnabled(false);
            }
        });
    }
}
