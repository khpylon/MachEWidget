package com.example.khughes.machewidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.DecimalFormatSymbols;
import android.icu.text.MessageFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.CarStatus.CarStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Implementation of App Widget functionality.
 */
public class CarStatusWidget_1x5 extends CarStatusWidget {
    public static final String WIDGET_IDS_KEY = BuildConfig.APPLICATION_ID + ".CARSTATUSWIDGET2";

    private void updateTire(RemoteViews views, String pressure, String status,
                            String units, Double conversion, int id) {
        // Set the textview background color based on the status
        int drawable;
        if (status != null && !status.equals("Normal")) {
            drawable = R.drawable.pressure_oval_red_solid;
            // Get the tire pressure and do any conversion necessary.
            if (pressure != null) {
                double value = Double.parseDouble(pressure);
                // Only display value if it's not ridiculous; after some OTA updates the
                // raw value is "65533"
                if (value < 2000) {
                    // If conversion is really small, show value in tenths
                    String pattern = conversion >= 0.1 ? "#" : "#.0";
                    pressure = MessageFormat.format("{0}{1}", new DecimalFormat(pattern, // "#.0",
                            DecimalFormatSymbols.getInstance(Locale.US)).format(value * conversion), units);
                    views.setInt(id, "setBackgroundResource", R.drawable.pressure_oval);
                } else {
                    pressure = "N/A";
                }
            } else {
                pressure = "N/A";
            }
            views.setTextViewText(id, pressure);
        } else {
            drawable = R.drawable.filler;
            views.setTextViewText(id, "");
        }
        views.setInt(id, "setBackgroundResource", drawable);
    }

    private void setCallbacks(Context context, RemoteViews views, int id) {
        // Define actions for clicking on various icons, including the widget itself
        views.setOnClickPendingIntent(R.id.thewidget, getPendingSelfIntent(context, id, WIDGET_CLICK));

        views.setOnClickPendingIntent(R.id.wireframe, getPendingSelfIntent(context, id, PROFILE_CLICK));
        views.setOnClickPendingIntent(R.id.settings, getPendingSelfIntent(context, id, SETTINGS_CLICK));

        boolean enableCommands = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.enable_commands_key), false);
        if (enableCommands) {
            views.setOnClickPendingIntent(R.id.lock_gasoline, getPendingSelfIntent(context, id, LOCK_CLICK));
            views.setOnClickPendingIntent(R.id.lock_electric, getPendingSelfIntent(context, id, LOCK_CLICK));
            views.setOnClickPendingIntent(R.id.ignition, getPendingSelfIntent(context, id, IGNITION_CLICK));
        } else {
            views.setOnClickPendingIntent(R.id.lock_gasoline, getPendingSelfIntent(context, id, WIDGET_CLICK));
            views.setOnClickPendingIntent(R.id.lock_electric, getPendingSelfIntent(context, id, WIDGET_CLICK));
            views.setOnClickPendingIntent(R.id.ignition, getPendingSelfIntent(context, id, WIDGET_CLICK));
        }
    }

    // Based on the VIN, find the right widget layout
    private RemoteViews getWidgetView(Context context) {
//        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");
//        return new RemoteViews(context.getPackageName(), Utils.getLayoutByVIN(VIN));
        return new RemoteViews(context.getPackageName(), R.layout.widget_1x5);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, InfoRepository info) {

        // Construct the RemoteViews object
        RemoteViews views = getWidgetView(context);

        // Setup actions for specific widgets
        setCallbacks(context, views, appWidgetId);

        // Set background transparency
        setBackground(context, views);

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

        int fuelType = Utils.getFuelType(VIN);
        boolean hasEngine = fuelType == Utils.FUEL_GAS || fuelType == Utils.FUEL_HYBRID;
        views.setViewVisibility(R.id.lock_gasoline, hasEngine ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.bottom_gasoline, hasEngine ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.lock_electric, hasEngine ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.bottom_electric, hasEngine ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.plug, hasEngine ? View.GONE : View.VISIBLE);
        setPHEVCallbacks(context, views, fuelType, appWidgetId, "showGasoline");

        // Ingition, alarm/sleep, plug icons
        drawIcons(views, carStatus);

        // Draw range and fuel/gas stuff
        boolean twoLines = true;
        drawRangeFuel(context, views, carStatus, info, vehicleInfo, fuelType,
                distanceConversion, distanceUnits, twoLines);

        // Tire pressures
        updateTire(views, carStatus.getLeftFrontTirePressure(), carStatus.getLeftFrontTireStatus(),
                pressureUnits, pressureConversion, R.id.lt_ft_tire);
        updateTire(views, carStatus.getRightFrontTirePressure(), carStatus.getRightFrontTireStatus(),
                pressureUnits, pressureConversion, R.id.rt_ft_tire);
        updateTire(views, carStatus.getLeftRearTirePressure(), carStatus.getLeftRearTireStatus(),
                pressureUnits, pressureConversion, R.id.lt_rr_tire);
        updateTire(views, carStatus.getRightRearTirePressure(), carStatus.getRightRearTireStatus(),
                pressureUnits, pressureConversion, R.id.rt_rr_tire);

        // Get the right images to use for this vehicle
        Map<String, Integer> vehicleImages = Utils.getVehicleDrawables_1x5(VIN);

        views.setImageViewResource(R.id.wireframe, vehicleImages.get(Utils.WIREFRAME));

        // Window statuses
        views.setImageViewResource(R.id.lt_ft_window,
                isWindowClosed(carStatus.getDriverWindow()) ? R.drawable.filler : vehicleImages.get(Utils.LEFT_FRONT_WINDOW));
        views.setImageViewResource(R.id.rt_ft_window,
                isWindowClosed(carStatus.getPassengerWindow()) ? R.drawable.filler : vehicleImages.get(Utils.RIGHT_FRONT_WINDOW));
        views.setImageViewResource(R.id.lt_rr_window,
                isWindowClosed(carStatus.getLeftRearWindow()) ? R.drawable.filler : vehicleImages.get(Utils.LEFT_REAR_WINDOW));
        views.setImageViewResource(R.id.rt_rr_window,
                isWindowClosed(carStatus.getRightRearWindow()) ? R.drawable.filler : vehicleImages.get(Utils.RIGHT_REAR_WINDOW));

        // Hood, tailgate, and door statuses
        views.setImageViewResource(R.id.hood,
                isDoorClosed(carStatus.getFrunk()) ? R.drawable.filler : vehicleImages.get(Utils.HOOD));
        views.setImageViewResource(R.id.tailgate,
                isDoorClosed(carStatus.getTailgate()) ? R.drawable.filler : vehicleImages.get(Utils.TAILGATE));
        views.setImageViewResource(R.id.lt_ft_door,
                isDoorClosed(carStatus.getDriverDoor()) ? R.drawable.filler : vehicleImages.get(Utils.LEFT_FRONT_DOOR));
        views.setImageViewResource(R.id.rt_ft_door,
                isDoorClosed(carStatus.getPassengerDoor()) ? R.drawable.filler : vehicleImages.get(Utils.RIGHT_FRONT_DOOR));
        views.setImageViewResource(R.id.lt_rr_door,
                isDoorClosed(carStatus.getLeftRearDoor()) ? R.drawable.filler : vehicleImages.get(Utils.LEFT_REAR_DOOR));
        views.setImageViewResource(R.id.rt_rr_door,
                isDoorClosed(carStatus.getRightRearDoor()) ? R.drawable.filler : vehicleImages.get(Utils.RIGHT_REAR_DOOR));

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
        } else if (action.equals(WIDGET_CLICK)) {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } else if (action.equals(PROFILE_CLICK)) {
            InfoRepository[] info = {null};
            int clickCount = context.getSharedPreferences("widget", Context.MODE_PRIVATE).getInt(action, 0);
            context.getSharedPreferences("widget", Context.MODE_PRIVATE).edit().putInt(action, ++clickCount).commit();
            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    int clickCount = context.getSharedPreferences("widget", Context.MODE_PRIVATE).getInt(action, 0);
                    if (clickCount > 2) {
                        VehicleInfo vehInfo = info[0].getVehicle();
                        UserInfo userInfo = info[0].getUser();
                        long lastUpdateInMillis = vehInfo.getLastUpdateTime();
                        String timeFormat = userInfo.getCountry().equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
                        String lastUpdate = OTAViewActivity.convertMillisToDate(lastUpdateInMillis, timeFormat);
                        Toast.makeText(context, "Last update at " + lastUpdate, Toast.LENGTH_SHORT).show();
                        long lastAlarmInMillis = new StoredData(context).getLastAlarmTime();
                        String lastAlarm = OTAViewActivity.convertMillisToDate(lastAlarmInMillis, timeFormat);
                        Toast.makeText(context, "Last alarm at " + lastAlarm, Toast.LENGTH_SHORT).show();
                        Calendar lastUpdateTime = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat(Constants.STATUSTIMEFORMAT, Locale.US);
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        try {
                            lastUpdateTime.setTime(sdf.parse(vehInfo.getCarStatus().getLastRefresh()));// all done
                        } catch (ParseException e) {
                            LogFile.e(context, MainActivity.CHANNEL_ID, "exception in CarStatusWidget.updateAppWidget: ", e);
                        }
                        String lastRefresh = OTAViewActivity.convertMillisToDate(lastUpdateTime.toInstant().toEpochMilli(), timeFormat);
                        Toast.makeText(context, "Last refresh at " + lastRefresh, Toast.LENGTH_SHORT).show();
                    } else if (clickCount > 1) {
                        ProfileManager.changeProfile(context);
                    } else {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    context.getSharedPreferences("widget", Context.MODE_PRIVATE).edit().putInt(action, 0).commit();
                }
            };
            if (clickCount == 1) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            info[0] = new InfoRepository(context);
                            synchronized (this) {
                                wait(500);
                            }
                            handler.sendEmptyMessage(0);
                        } catch (InterruptedException ex) {
                        }
                    }
                }.start();
            }
            return;
        } else if (action.equals(SETTINGS_CLICK)) {
            intent = new Intent(context, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } else if (action.equals(PHEVTOGGLE_CLICK)) {
            int appWidgetId = intent.getIntExtra("appWidgetId", -1);
            String mode = intent.getStringExtra("nextMode");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews views = getWidgetView(context);
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
            setPHEVCallbacks(context, views, Utils.FUEL_PHEV, appWidgetId, nextMode);
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
            return;
        } else if (action.equals(IGNITION_CLICK) || action.equals(LOCK_CLICK)) {
            InfoRepository[] info = {null};
            int clickCount = context.getSharedPreferences("widget", Context.MODE_PRIVATE).getInt(action, 0);
            context.getSharedPreferences("widget", Context.MODE_PRIVATE).edit().putInt(action, ++clickCount).commit();
            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    int clickCount = context.getSharedPreferences("widget", Context.MODE_PRIVATE).getInt(action, 0);
                    if (clickCount > 1) {
                        VehicleInfo vehInfo = info[0].getVehicle();
                        if (vehInfo != null) {
                            CarStatus carStatus = vehInfo.getCarStatus();
                            if (carStatus != null) {
                                switch (action) {
                                    case IGNITION_CLICK:
                                        if (carStatus.getRemoteStartStatus() != null
                                                && carStatus.getIgnition() != null && carStatus.getIgnition().equals("Off")) {
                                            if (!carStatus.getRemoteStartStatus()) {
                                                remoteStart(context);
                                            } else {
                                                remoteStop(context);
                                            }
                                        }
                                        break;
                                    case LOCK_CLICK:
                                        if (carStatus.getLock() != null) {
                                            if (carStatus.getLock().equals("LOCKED")) {
                                                unlock(context);
                                            } else {
                                                lock(context);
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    context.getSharedPreferences("widget", Context.MODE_PRIVATE).edit().putInt(action, 0).commit();
                }
            };
            if (clickCount == 1) new Thread() {
                @Override
                public void run() {
                    try {
                        info[0] = new InfoRepository(context);
                        synchronized (this) {
                            wait(500);
                        }
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException ex) {
                    }
                }
            }.start();
        }
    }
}