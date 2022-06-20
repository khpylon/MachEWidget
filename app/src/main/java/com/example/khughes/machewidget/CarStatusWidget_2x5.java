package com.example.khughes.machewidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.icu.text.DecimalFormatSymbols;
import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.CarStatus.CarStatus;
import com.example.khughes.machewidget.db.UserInfoDao;
import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDao;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import org.apache.avro.LogicalTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class CarStatusWidget_2x5 extends CarStatusWidget_5x5 {
    public static final String WIDGET_IDS_KEY = BuildConfig.APPLICATION_ID + ".CARSTATUSWIDGET2x5";

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showAppLinks = sharedPref.getBoolean(context.getResources().getString(R.string.show_app_links_key), true);
        if (showAppLinks) {
            views.setOnClickPendingIntent(R.id.leftappbutton, getPendingSelfIntent(context, id, LEFT_BUTTON_CLICK));
            views.setOnClickPendingIntent(R.id.rightappbutton, getPendingSelfIntent(context, id, RIGHT_BUTTON_CLICK));
        } else {
            views.setOnClickPendingIntent(R.id.leftappbutton, getPendingSelfIntent(context, id, WIDGET_CLICK));
            views.setOnClickPendingIntent(R.id.rightappbutton, getPendingSelfIntent(context, id, WIDGET_CLICK));
        }

        boolean forceUpdates = sharedPref.getBoolean(context.getResources().getString(R.string.user_forcedUpdate_key), true);
        views.setOnClickPendingIntent(R.id.refresh, getPendingSelfIntent(context, id, forceUpdates ? UPDATE_CLICK : WIDGET_CLICK));
        views.setViewVisibility(R.id.refresh, forceUpdates ? View.VISIBLE : View.GONE);

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
        return new RemoteViews(context.getPackageName(), R.layout.widget_2x5);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, InfoRepository info) {

        // Construct the RemoteViews object
        RemoteViews views = getWidgetView(context);

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

        views.setTextViewText(R.id.profile, vehicleInfo.getNickname());
//        views.setTextViewText(R.id.profile, "My Mach-E");

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

        int fuelType = Utils.getFuelType(vehicleInfo.getVIN());
        boolean hasEngine = fuelType == Utils.FUEL_GAS || fuelType == Utils.FUEL_HYBRID;
        views.setViewVisibility(R.id.lock_gasoline, hasEngine ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.bottom_gasoline, hasEngine ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.lock_electric, hasEngine ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.bottom_electric, hasEngine ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.plug, hasEngine ? View.GONE : View.VISIBLE);
        setPHEVCallbacks(context, views, fuelType, appWidgetId, "showGasoline");

        // Show last refresh, odometer, OTA status
        String timeFormat = userInfo.getCountry().equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
        drawLastRefresh(context, views, carStatus, timeFormat);
        drawOdometer(views, carStatus, distanceConversion, distanceUnits);
        drawOTAInfo(context, views, vehicleInfo, timeFormat);

        // Location
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.show_location_key), true)) {
            views.setViewVisibility(R.id.location_container, View.VISIBLE);
            updateLocation(context, views, carStatus.getLatitude(), carStatus.getLongitude());
        } else {
            views.setViewVisibility(R.id.location_container, View.GONE);
        }

        // Ignition, alarm/sleep, plug icons
        drawIcons(views, carStatus);

        // Draw range and fuel/gas stuff
        boolean twoLines = false;
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
        Map<String, Integer> vehicleImages = Utils.getVehicleDrawables_1x5(vehicleInfo.getVIN());

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

        updateLinkedApps(context, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = getWidgetView(context);
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final InfoRepository[] info = {null};

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                UserInfo user = info[0].getUser();
                if (user == null) {
                    LogFile.d(context, MainActivity.CHANNEL_ID, "CarStatusWidget_2x5.onUpdate(): no userinfo found");
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
        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(new ComponentName(context, this.getClass()));
        return;
        // Enter relevant functionality for when the last widget is disabled
    }

    public void onReceive(Context context, Intent intent) {
        // Handle the actions which don't require info about the vehicle or user
        String action = intent.getAction();
        int appWidgetId = intent.getIntExtra(APPWIDGETID, -1);
        String widget_action = action + "_" + appWidgetId;
        String widget_VIN = Constants.VIN_KEY + appWidgetId;

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager man = AppWidgetManager.getInstance(context);
            int[] ids = man.getAppWidgetIds(new ComponentName(context, CarStatusWidget_2x5.class));
            onUpdate(context, AppWidgetManager.getInstance(context), ids);
//            if (intent.hasExtra(WIDGET_IDS_KEY)) {
//                int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
//                onUpdate(context, AppWidgetManager.getInstance(context), ids);
//            }
            return;
        } else if (action.equals(WIDGET_CLICK)) {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } else if (action.equals(LEFT_BUTTON_CLICK)) {
            StoredData appInfo = new StoredData(context);
            String appPackageName = appInfo.getLeftAppPackage();
            if (appPackageName != null) {
                PackageManager pm = context.getApplicationContext().getPackageManager();
                intent = pm.getLaunchIntentForPackage(appPackageName);
                if (intent != null) {
                    context.startActivity(intent);
                } else {
                    appInfo.setLeftAppPackage(null);
                    MainActivity.updateWidget(context);
                    Toast.makeText(context, "App is no longer installed", Toast.LENGTH_LONG).show();
                }
            }
            return;
        } else if (action.equals(RIGHT_BUTTON_CLICK)) {
            StoredData appInfo = new StoredData(context);
            String appPackageName = appInfo.getRightAppPackage();
            if (appPackageName != null) {
                PackageManager pm = context.getApplicationContext().getPackageManager();
                intent = pm.getLaunchIntentForPackage(appPackageName);
                if (intent != null) {
                    context.startActivity(intent);
                } else {
                    appInfo.setRightAppPackage(null);
                    MainActivity.updateWidget(context);
                    Toast.makeText(context, "App is no longer installed", Toast.LENGTH_LONG).show();
                }
            }
            return;
        } else if (action.equals(PROFILE_CLICK)) {
            InfoRepository[] info = {null};
            int clickCount = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getInt(widget_action, 0);
            context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit().putInt(widget_action, ++clickCount).commit();
            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    int clickCount = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getInt(widget_action, 0);
                    if (clickCount > 2) {
                        String VIN = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getString(widget_VIN, null);
                        VehicleInfo vehInfo = info[0].getVehicleByVIN(VIN);
                        UserInfo userInfo = info[0].getUser();
                        long lastUpdateInMillis = vehInfo.getLastUpdateTime();
                        String timeFormat = userInfo.getCountry().equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
                        String lastUpdate = OTAViewActivity.convertMillisToDate(lastUpdateInMillis, timeFormat);
                        Toast.makeText(context, "Last update at " + lastUpdate, Toast.LENGTH_SHORT).show();
                        long lastAlarmInMillis = new StoredData(context).getLastAlarmTime();
                        String lastAlarm = OTAViewActivity.convertMillisToDate(lastAlarmInMillis, timeFormat);
                        Toast.makeText(context, "Last alarm at " + lastAlarm, Toast.LENGTH_SHORT).show();
                    } else if (clickCount > 1) {
                        ProfileManager.changeProfile(context, widget_VIN);
                    } else {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit().remove(widget_action).apply();
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
            } else if (clickCount > 3) {
                context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit().remove(widget_action).apply();
            }
            return;
        } else if (action.equals(SETTINGS_CLICK)) {
            intent = new Intent(context, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } else if (action.equals(PHEVTOGGLE_CLICK)) {
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
        } else if (action.equals(IGNITION_CLICK) || action.equals(LOCK_CLICK) || action.equals(UPDATE_CLICK)) {
            InfoRepository[] info = {null};
            int clickCount = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getInt(widget_action, 0);
            context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit().putInt(widget_action, ++clickCount).commit();
            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    int clickCount = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getInt(widget_action, 0);
                    if (clickCount > 1) {
                        String VIN = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getString(widget_VIN, null);
                        VehicleInfo vehInfo = info[0].getVehicleByVIN(VIN);
                        if (vehInfo != null) {
                            CarStatus carStatus = vehInfo.getCarStatus();
                            if (carStatus != null) {
                                switch (action) {
                                    case IGNITION_CLICK:
                                        if (carStatus.getRemoteStartStatus() != null
                                                && carStatus.getIgnition() != null && carStatus.getIgnition().equals("Off")) {
                                            if (!carStatus.getRemoteStartStatus()) {
                                                remoteStart(context, VIN);
                                            } else {
                                                remoteStop(context, VIN);
                                            }
                                        }
                                        break;
                                    case LOCK_CLICK:
                                        if (carStatus.getLock() != null) {
                                            if (carStatus.getLock().equals("LOCKED")) {
                                                unlock(context, VIN);
                                            } else {
                                                lock(context, VIN);
                                            }
                                        }
                                        break;
                                    case UPDATE_CLICK:
                                        if (carStatus.getLVBStatus().equals("STATUS_GOOD")) {
                                            long nowTime = Instant.now().toEpochMilli();
                                            long firstTime = vehInfo.getInitialForcedRefreshTime();
                                            long lastTime = vehInfo.getLastForcedRefreshTime();
                                            long seconds = (nowTime - firstTime) / 1000;

                                            if(seconds > 60*60*12) {
                                                vehInfo.setForcedRefreshCount(0);
                                            }
                                            seconds = (nowTime - lastTime) / 1000;

                                            long count = vehInfo.getForcedRefreshCount();
                                            if((count < 3 && seconds > 120) || (count < 5 && seconds > 300) ) {
                                                long timeout = info[0].getUser().getExpiresIn();
                                                seconds = (timeout - nowTime) / 1000;
                                                // If the access token has expired, or is about to, do a refresh first
                                                if (seconds < 30) {
                                                    Toast.makeText(context, "The token is being refreshed; this may take a minute.", Toast.LENGTH_SHORT).show();
                                                    StatusReceiver.nextAlarm(context, 2);
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                synchronized (this) {
                                                                    wait(15000);
                                                                }
                                                                forceUpdate(context, VIN);
                                                            } catch (InterruptedException ex) {
                                                            }
                                                        }
                                                    }.start();
                                                }
                                                // Otherwise just do it
                                                else {
                                                    Toast.makeText(context, "Forcing a refresh; this may take 30 seconds.", Toast.LENGTH_SHORT).show();
                                                    long now = Instant.now().toEpochMilli();
                                                    vehInfo.setLastForcedRefreshTime(now);
                                                    count = vehInfo.getForcedRefreshCount();
                                                    if (count == 0) {
                                                        vehInfo.setInitialForcedRefreshTime(now);
                                                    }
                                                    vehInfo.setForcedRefreshCount(++count);
                                                    info[0].setVehicle(vehInfo);
//                                                    forceUpdate(context, VIN);
                                                }
                                            } else if (count < 3) {
                                                Toast.makeText(context, "Cannot force update for another " + (120 - seconds) + " seconds.", Toast.LENGTH_SHORT).show();
                                            } else if (count < 5) {
                                                Toast.makeText(context, "Cannot force update for another " + (300 - seconds) + " seconds.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Too many forced updates attempted; ignored.", Toast.LENGTH_SHORT).show();
                                            }



//                                            long nowTime = Instant.now().toEpochMilli();
//                                            long lastTime = vehInfo.getLastForcedRefreshTime();
//                                            long seconds = (nowTime - lastTime) / 1000;
//                                            // Need to be at least two minutes between updates
//                                            if (seconds >= 120) {
//                                                long count = vehInfo.getForcedRefreshCount();
//                                                // if it's less been less than 10 minutes, make note
//                                                if (seconds <= 600) {
//                                                    ++count;
//                                                    vehInfo.setForcedRefreshCount(count);
//                                                    info[0].setVehicle(vehInfo);
//                                                }
//                                                // If it's been more than 10 minutes,
//                                                else if (count > 0) {
//                                                    vehInfo.setForcedRefreshCount(0);
//                                                    info[0].setVehicle(vehInfo);
//                                                }
//                                                // If there are too many requests in 30 minutes, ignore it
//                                                if (count > 3) {
//                                                    Toast.makeText(context, "Too many forced updates attempted; ignored.", Toast.LENGTH_SHORT).show();
//                                                }
//                                                // Let's give it a go
//                                                else {
//                                                    // If the access token has expired, or is about to, do a refresh first
//                                                    long timeout = info[0].getUser().getExpiresIn();
//                                                    seconds = (timeout - nowTime) / 1000;
//                                                    if (seconds < 30) {
//                                                        Toast.makeText(context, "The token is being refreshed; try again in a few seconds", Toast.LENGTH_SHORT).show();
//                                                        StatusReceiver.nextAlarm(context, 2);
//                                                    } else {
//                                                        Toast.makeText(context, "Attempting to force an update.", Toast.LENGTH_SHORT).show();
//                                                        vehInfo.setLastForcedRefreshTime(nowTime);
//                                                        info[0].setVehicle(vehInfo);
//                                                  //      forceUpdate(context, VIN);
//                                                    }
//                                                }
//                                            } else {
//                                                Toast.makeText(context, "Cannot force update for another " + (120 - seconds) + " seconds.", Toast.LENGTH_SHORT).show();
//                                            }
                                        } else {
                                            Toast.makeText(context, "The LVB status is not good.", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit().putInt(widget_action, 0).commit();
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
            } else if (clickCount > 3) {
                context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit().remove(widget_action).apply();
            }
            return;
        } else {
            super.onReceive(context, intent);
        }
    }
}