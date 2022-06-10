package com.example.khughes.machewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.CarStatus.CarStatus;

/**
 * Implementation of App Widget functionality.
 */
public class CarStatusWidget_1x5 extends CarStatusWidget {
    public static final String WIDGET_IDS_KEY = BuildConfig.APPLICATION_ID + ".CARSTATUSWIDGET2";

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, InfoRepository info) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_1x5);

        views.setImageViewResource(R.id.wireframe, R.drawable.mache_wireframe_horz);
        views.setImageViewResource(R.id.frunk, R.drawable.mache_frunk_horz);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useTranparency = sharedPref.getBoolean(context.getResources().getString(R.string.transp_bg_key), false);
        views.setInt(R.id.thewidget, "setBackgroundResource",
                useTranparency ? R.color.transparent_black : R.color.black);

        VehicleInfo vehicleInfo = info.getVehicle();
        UserInfo userInfo = info.getUser();
        if (vehicleInfo == null || userInfo == null) {
            return;
        }

        String VIN = vehicleInfo.getVIN();

        // Get conversion factors for Metric vs Imperial measurement units
        int units = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(
                        context.getResources().getString(R.string.units_key),
                        context.getResources().getString(R.string.units_system)
                ));
        double distanceConversion;
        String distanceUnits;
        if ((units == Constants.UNITS_SYSTEM && userInfo.getUomSpeed().equals("MPH")) || units == Constants.UNITS_IMPERIAL) {
            distanceConversion = Constants.KMTOMILES;
            distanceUnits = "miles";
        } else {
            distanceConversion = 1.0;
            distanceUnits = "km";
        }

        int fuelType = Utils.getFuelType(VIN);

        CarStatus carStatus = vehicleInfo.getCarStatus();
        if (carStatus == null || carStatus.getVehiclestatus() == null) {
            views.setTextViewText(R.id.lastRefresh, "Unable to retrieve status information.");
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }

        drawIcons(views, carStatus);

        drawRangeFuel(context, views, carStatus,
                info, vehicleInfo, fuelType, distanceConversion, distanceUnits);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final InfoRepository[] info = {null};

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                UserInfo user = info[0].getUser();
                if (user == null) {
                    LogFile.d(context, MainActivity.CHANNEL_ID, "CarStatusWidget.onUpdate(): no userinfo found");
                    return;
                }

                // There may be multiple widgets active, so update all of them
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, info[0]);
                }
            }
        };

        new Thread(() -> {
            info[0] = new InfoRepository(context);
            handler.sendEmptyMessage(0);
        }).start();

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void onReceive(Context context, Intent intent) {
        // Handle the actions which don't require info about the vehicle or user
        String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) && intent.hasExtra(WIDGET_IDS_KEY)) {
            int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
            onUpdate(context, AppWidgetManager.getInstance(context), ids);
            return;
        }
    }
}