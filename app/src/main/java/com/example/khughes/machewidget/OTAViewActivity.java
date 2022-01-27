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
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class OTAViewActivity extends AppCompatActivity {

    public static String convertDate(String UTCdate) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.OTATIMEFORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            cal.setTime(sdf.parse(UTCdate));
            sdf = new SimpleDateFormat(Constants.LOCALTIMEFORMAT, Locale.ENGLISH);
            return sdf.format(cal.getTime());
        } catch (ParseException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in OTAViewActivity.convertDate: ", e);
            return "";
        }
    }

    private String currentOTATime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otaview);

        Context context = getApplicationContext();

        OTAStatus ota = new StoredData(context).getOTAStatus();
        String unencodedHtml = "<html><body>";
        if (ota != null && ota.getFuseResponse() != null) {
            String tmp;
            unencodedHtml += "<b>Alert status:</b> " + ((tmp = ota.getOtaAlertStatus()) != null ? tmp : "") + "<p>";
            for (OTAStatus.FuseResponse__1 fuse : ota.getFuseResponse().getFuseResponseList()) {
                unencodedHtml += "<ul>";
                unencodedHtml += "<li><b>CorrelationID:</b> " + ((tmp = fuse.getOemCorrelationId()) != null ? tmp : "");
                unencodedHtml += "<li><b>Created:</b> " + convertDate(fuse.getDeploymentCreationDate());
                unencodedHtml += "<li><b>Expiration:</b> " + convertDate(fuse.getDeploymentExpirationTime());
                unencodedHtml += "<li><b>Priority:</b> " + ((tmp = fuse.getCommunicationPriority()) != null ? tmp : "");
                unencodedHtml += "<li><b>Type:</b> " + ((tmp = fuse.getType()) != null ? tmp : "");
                unencodedHtml += "<li><b>Final Acton:</b> \"" + ((tmp = fuse.getDeploymentFinalConsumerAction()) != null ? tmp : "") + "\"";
                if (fuse.getLatestStatus() != null) {
                    currentOTATime = convertDate(fuse.getLatestStatus().getDateTimestamp());
                    unencodedHtml += "<li><b>Latest status: </b>" + ((tmp = fuse.getLatestStatus().getAggregateStatus()) != null ? tmp : "") + "<ul>";
                    unencodedHtml += "<li><b>Details:</b> " + ((tmp = fuse.getLatestStatus().getDetailedStatus()) != null ? tmp : "");
                    unencodedHtml += "<li><b>Time Stamp:</b> " + currentOTATime;
                    unencodedHtml += "</ul></li>";
                }
                unencodedHtml += "</ul><hr>";
            }
            String description = ota.getDescription();
            if (description != null) {
                unencodedHtml += "<b>Description:</b><p>" + description.replace("\n", "<br>") + "<hr>";
            }

        } else {
            unencodedHtml += "OTA Status is <i>unavailable</i>";
        }

        unencodedHtml += "</body></html>";

        String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(),
                Base64.NO_PADDING);
        WebView mWebView = findViewById(R.id.ota_webview);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(mWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
        mWebView.loadData(encodedHtml, "text/html", "base64");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String lastOTATime = sharedPref.getString(context.getResources().getString(R.string.last_ota_time), "");

        Button clear = findViewById(R.id.button);
        clear.setEnabled(lastOTATime.equals("") || currentOTATime.compareTo(lastOTATime) > 0);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentOTATime != null && !currentOTATime.equals("")) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sharedPref.edit().putString(context.getResources().getString(R.string.last_ota_time), currentOTATime).apply();
                    MainActivity.updateWidget(context);
                    clear.setEnabled(false);
                }
            }
        });
    }
}