package com.example.khughes.machewidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.CarStatus.CarStatus;

import java.util.ArrayList;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class CarStatusWidget_1x5 extends CarStatusWidget {

    // Define actions for clicking on various icons, including the widget itself
    protected void setCallbacks(Context context, RemoteViews views, int id) {
        views.setOnClickPendingIntent(R.id.wireframe, getPendingSelfIntent(context, id, PROFILE_CLICK));
        super.setCallbacks(context, views, id);
    }

    protected void drawVehicleImage(Context context, RemoteViews views, CarStatus carStatus, int vehicleColor, ArrayList<Integer> whatsOpen, Map<String, Integer> vehicleImages) {
        whatsOpen = new ArrayList<>();
        whatsOpen.add(isWindowClosed(carStatus.getDriverWindow()) ? null : vehicleImages.get(Utils.LEFT_FRONT_WINDOW));
        whatsOpen.add(isWindowClosed(carStatus.getPassengerWindow()) ? null : vehicleImages.get(Utils.RIGHT_FRONT_WINDOW));
        whatsOpen.add(isWindowClosed(carStatus.getLeftRearWindow()) ? null : vehicleImages.get(Utils.LEFT_REAR_WINDOW));
        whatsOpen.add(isWindowClosed(carStatus.getRightRearWindow()) ? null : vehicleImages.get(Utils.RIGHT_REAR_WINDOW));

        super.drawVehicleImage(context, views, carStatus, vehicleColor, whatsOpen, vehicleImages);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, InfoRepository info) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_1x5);

        // Setup actions for specific widgets
        setCallbacks(context, views, appWidgetId);

        // Set background transparency
        setBackground(context, views);

        // Find which user is active.
        UserInfo userInfo = info.getUser();
        if (userInfo == null) {
            return;
        }

        // Find the vehicle for this widget
        VehicleInfo vehicleInfo = getVehicleInfo(context, info, appWidgetId);
        if (vehicleInfo == null) {
            return;
        }

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
        double pressureConversion;
        String pressureUnits;
        if ((units == Constants.UNITS_SYSTEM && userInfo.getUomPressure().equals("PSI")) || units == Constants.UNITS_IMPERIAL) {
            pressureConversion = Constants.KPATOPSI;
            pressureUnits = "psi";
        } else if (units == Constants.UNITS_SYSTEM && userInfo.getUomPressure().equals("BAR")) {
            pressureConversion = Constants.KPATOBAR;
            pressureUnits = "bar";
        } else {
            pressureConversion = 1.0;
            pressureUnits = "kPa";
        }

        CarStatus carStatus = vehicleInfo.getCarStatus();
        if (carStatus == null || carStatus.getVehiclestatus() == null) {
            views.setTextViewText(R.id.lastRefresh, "Unable to retrieve status information.");
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }

        boolean isICEOrHybrid;
        boolean isPHEV;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.use_old_engine_key), false)) {
            int fuelType = Utils.getFuelType(vehicleInfo.getVIN());
            isICEOrHybrid = (fuelType == Utils.FUEL_GAS || fuelType == Utils.FUEL_HYBRID);
            isPHEV = (fuelType == Utils.FUEL_PHEV);
        } else {
            isICEOrHybrid = carStatus.isPropulsionICEOrHybrid(carStatus.getPropulsion());
            isPHEV = carStatus.isPropulsionPHEV(carStatus.getPropulsion());
        }
        views.setViewVisibility(R.id.lock_gasoline, isICEOrHybrid ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.bottom_gasoline, isICEOrHybrid ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.lock_electric, isICEOrHybrid ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.bottom_electric, isICEOrHybrid ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.plug, isICEOrHybrid ? View.GONE : View.VISIBLE);
        setPHEVCallbacks(context, views, isPHEV, appWidgetId, "showGasoline");

        // Ingition, alarm/sleep, plug icons
        drawIcons(views, carStatus);

        // Draw range and fuel/gas stuff
        boolean twoLines = true;
        drawRangeFuel(context, views, carStatus, info, vehicleInfo,
                distanceConversion, distanceUnits, twoLines);

        // Tire pressures
        updateTire(context, views, carStatus.getLeftFrontTirePressure(), carStatus.getLeftFrontTireStatus(),
                pressureUnits, pressureConversion, R.id.lt_ft_tire);
        updateTire(context, views, carStatus.getRightFrontTirePressure(), carStatus.getRightFrontTireStatus(),
                pressureUnits, pressureConversion, R.id.rt_ft_tire);
        updateTire(context, views, carStatus.getLeftRearTirePressure(), carStatus.getLeftRearTireStatus(),
                pressureUnits, pressureConversion, R.id.lt_rr_tire);
        updateTire(context, views, carStatus.getRightRearTirePressure(), carStatus.getRightRearTireStatus(),
                pressureUnits, pressureConversion, R.id.rt_rr_tire);

        // Get the right images to use for this vehicle
        Map<String, Integer> vehicleImages = Utils.getVehicleDrawables_1x5(vehicleInfo.getVIN());

        // See if we should guess vehicle color
        if( Utils.scanImageForColor (context, vehicleInfo) ) {
            info.setVehicle(vehicleInfo);
        }

        // Draw the vehicle image
        drawVehicleImage( context, views,  carStatus, vehicleInfo.getColorValue(), null,  vehicleImages);

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
                    LogFile.d(context, MainActivity.CHANNEL_ID, "CarStatusWidget_5x5.onUpdate(): no userinfo found");
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

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(PHEVTOGGLE_CLICK)) {
            String mode = intent.getStringExtra("nextMode");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_1x5);
            String nextMode;
            if (mode.equals("showGasoline")) {
                nextMode = "showElectric";
                views.setViewVisibility(R.id.bottom_electric, View.GONE);
                views.setViewVisibility(R.id.bottom_gasoline, View.VISIBLE);
            } else {
                nextMode = "showGasoline";
                views.setViewVisibility(R.id.bottom_electric, View.VISIBLE);
                views.setViewVisibility(R.id.bottom_gasoline, View.GONE);
            }
            int appWidgetId = intent.getIntExtra(APPWIDGETID,-1);
            setPHEVCallbacks(context, views, true, appWidgetId, nextMode);
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
        } else {
            super.onReceive(context, intent);
        }
    }
}
