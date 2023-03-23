package com.example.khughes.machewidget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.khughes.machewidget.OTAStatus.FuseResponseList;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

    private static long currentOTATime = 0;

    private static InfoRepository info;
    private static VehicleInfo mVehicleInfo;

    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otaview);

        Context context = getApplicationContext();

        WebView mWebView = findViewById(R.id.ota_webview);
        Misc.checkDarkMode(context, mWebView);

        Button clear = findViewById(R.id.button);
        clear.setOnClickListener(view -> {
            if (currentOTATime > 0) {
                mVehicleInfo.setLastOTATime( currentOTATime );
                info.setVehicle(mVehicleInfo);
                CarStatusWidget.updateWidget(context);
                clear.setEnabled(false);
            }
        });

        Spinner spinner = findViewById(R.id.spinner);
        arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Handler handler = new Handler(this.getMainLooper());
        new Thread(() -> {
            info = new InfoRepository(getApplicationContext());
            handler.post(() -> {
                List<VehicleInfo> vehicles = info.getVehicles();
                if(vehicles.size() == 1) {
                    spinner.setVisibility(View.GONE);
                    new DownloadChangelog(context, mWebView, clear).execute(info.getVehicles().get(0).getVin());
                } else {
                    arrayList.clear();
                    for (VehicleInfo vehicle : vehicles) {
                        arrayList.add(vehicle.getVin());
                    }
                    spinner.setAdapter(arrayAdapter);
                }
            });
        }).start();

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String VIN = parent.getItemAtPosition(position).toString();
                new DownloadChangelog(context, mWebView, clear).execute(VIN);
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
    }

    private static class DownloadChangelog extends AsyncTask<String, String, VehicleInfo> {

        private final WeakReference<Context> mContext;
        private final WeakReference<WebView> mWebView;
        private final WeakReference<Button> mClear;
        private String dateFormat = Constants.LOCALTIMEFORMAT;

        public DownloadChangelog(Context context, WebView webView, Button clear) {
            mContext = new WeakReference<>(context);
            mWebView = new WeakReference<>(webView);
            mClear = new WeakReference<>(clear);
        }

        @Override
        protected VehicleInfo doInBackground(String... VINs) {
            info = new InfoRepository(mContext.get());
            if(info.getUser() != null ) {
                dateFormat = info.getUser().getCountry().equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
            }
            return info.getVehicleByVIN(VINs[0]);
        }

        protected void onPostExecute(VehicleInfo vehicleInfo) {
            Button clear = mClear.get();
            mVehicleInfo = vehicleInfo;
            OTAStatus ota = vehicleInfo.toOTAStatus();
            long lastOTATime = vehicleInfo.getLastOTATime();

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
                        unencodedHtml.append("<li><b>Alert Status:</b> \"");
                        unencodedHtml.append((((tmp = ota.getOtaAlertStatus()) != null ? tmp : "") + "\""));
                        unencodedHtml.append("<li><b>Final action:</b> \"");
                        unencodedHtml.append((((tmp = fuse.getDeploymentFinalConsumerAction()) != null ? tmp : "") + "\""));
                        unencodedHtml.append("</ul><hr>");
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

            String encodedHtml = Base64.encodeToString(unencodedHtml.toString().getBytes(),
                    Base64.NO_PADDING);
            mWebView.get().loadData(encodedHtml, Constants.TEXT_HTML, "base64");

            clear.setEnabled(currentOTATime > lastOTATime);
        }
    }

}
