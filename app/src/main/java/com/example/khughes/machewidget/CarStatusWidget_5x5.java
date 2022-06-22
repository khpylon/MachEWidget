package com.example.khughes.machewidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.util.Locale;
import java.util.Map;

public class CarStatusWidget_5x5 extends CarStatusWidget {

    @Override
    protected void updateTire(RemoteViews views, String pressure, String status,
                            String units, Double conversion, int id) {
        // Set the textview background color based on the status
        int drawable;
        if (status != null && !status.equals("Normal")) {
            drawable = R.drawable.pressure_oval_red;
        } else {
            drawable = R.drawable.pressure_oval;
        }
        views.setInt(id, "setBackgroundResource", drawable);

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
    }

    // Define actions for clicking on various icons, including the widget itself
    protected void setCallbacks(Context context, RemoteViews views, int id) {
        views.setOnClickPendingIntent(R.id.profile, getPendingSelfIntent(context, id, PROFILE_CLICK));
        views.setOnClickPendingIntent(R.id.logo, getPendingSelfIntent(context, id, PROFILE_CLICK));

        boolean showAppLinks = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.show_app_links_key), true);
        views.setOnClickPendingIntent(R.id.leftappbutton, getPendingSelfIntent(context, id, showAppLinks ? LEFT_BUTTON_CLICK : WIDGET_CLICK));
        views.setOnClickPendingIntent(R.id.rightappbutton, getPendingSelfIntent(context, id, showAppLinks ? RIGHT_BUTTON_CLICK : WIDGET_CLICK));

        super.setCallbacks(context, views, id);
    }

    // Based on the VIN, find the right widget layout
    private RemoteViews getWidgetView(Context context) {
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");
        return new RemoteViews(context.getPackageName(), Utils.getLayoutByVIN(VIN));
    }

    protected void drawIcons(RemoteViews views, CarStatus carStatus) {
        // Door locks
        String lockStatus = carStatus.getLock();
        if (lockStatus != null) {
            views.setImageViewResource(R.id.lock_electric, lockStatus.equals("LOCKED") ?
                    R.drawable.locked_icon_green : R.drawable.unlocked_icon_red);
            views.setImageViewResource(R.id.lock_gasoline, lockStatus.equals("LOCKED") ?
                    R.drawable.locked_icon_green : R.drawable.unlocked_icon_red);
        }

        // Ignition and remote start
        String ignition = carStatus.getIgnition();
        Boolean remote = carStatus.getRemoteStartStatus();
        if (remote != null && remote) {
            views.setImageViewResource(R.id.ignition, R.drawable.ignition_icon_yellow);
        } else if (ignition != null) {
            views.setImageViewResource(R.id.ignition, ignition.equals("Off") ?
                    R.drawable.ignition_icon_gray : R.drawable.ignition_icon_green);
        }

        // Motion alarm and deep sleep state
        String alarm = carStatus.getAlarm();
        Boolean sleep = carStatus.getDeepSleep();
        if (sleep != null && sleep) {
            views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_zzz_red);
        } else {
            if (alarm != null) {
                views.setImageViewResource(R.id.alarm, alarm.equals("NOTSET") ?
                        R.drawable.bell_icon_red : R.drawable.bell_icon_green);
            } else {
                views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_gray);
            }
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, InfoRepository info) {
        RemoteViews views = getWidgetView(context);

        // Make sure the left side is visible depending on the widget width
        Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        onResize(appWidgetOptions, views);

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

        // If the vehicle image has been downloaded, update it
        File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
        File image = new File(imageDir, vehicleInfo.getVIN() + ".png");
        if (image.exists()) {
            String path = image.getPath();
            views.setImageViewBitmap(R.id.logo, BitmapFactory.decodeFile(path));
        }

        // Display the vehicle's nickname
        views.setTextViewText(R.id.profile, vehicleInfo.getNickname());
//        views.setTextViewText(R.id.profile, "My Mach-E");
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);

        // If no status information, print something generic and return
        // TODO: also refresh the icons as if we're logged out?
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

        // Show last refresh, odometer
        String timeFormat = userInfo.getCountry().equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
        drawLastRefresh(context, views, carStatus, timeFormat);
        drawOdometer(views, carStatus, distanceConversion, distanceUnits);

        // Ignition, alarm/sleep, plug icons
        drawIcons(views, carStatus);

        // Draw range and fuel/gas stuff
        boolean twoLines = true;
        drawRangeFuel(context, views, carStatus, info, vehicleInfo, fuelType,
                distanceConversion, distanceUnits, twoLines);

        // Current Odometer reading
        Double odometer = carStatus.getOdometer();
        if (odometer != null && odometer > 0) {
            // FordPass truncates; go figure.
            views.setTextViewText(R.id.odometer,
                    MessageFormat.format("Odo: {0} {1}", Double.valueOf(odometer * distanceConversion).intValue(), distanceUnits));
        } else {
            views.setTextViewText(R.id.odometer, "Odo: ---");
        }

        // Tire pressures
        updateTire(views, carStatus.getLeftFrontTirePressure(), carStatus.getLeftFrontTireStatus(),
                pressureUnits, pressureConversion, R.id.lftire);
        updateTire(views, carStatus.getRightFrontTirePressure(), carStatus.getRightFrontTireStatus(),
                pressureUnits, pressureConversion, R.id.rftire);
        updateTire(views, carStatus.getLeftRearTirePressure(), carStatus.getLeftRearTireStatus(),
                pressureUnits, pressureConversion, R.id.lrtire);
        updateTire(views, carStatus.getRightRearTirePressure(), carStatus.getRightRearTireStatus(),
                pressureUnits, pressureConversion, R.id.rrtire);

        // Window statuses
        views.setImageViewResource(R.id.lt_ft_window,
                isWindowClosed(carStatus.getDriverWindow()) ? R.drawable.filler : R.drawable.icons8_left_front_window_down_red);
        views.setImageViewResource(R.id.rt_ft_window,
                isWindowClosed(carStatus.getPassengerWindow()) ? R.drawable.filler : R.drawable.icons8_right_front_window_down_red);
        views.setImageViewResource(R.id.lt_rr_window,
                isWindowClosed(carStatus.getLeftRearWindow()) ? R.drawable.filler : R.drawable.icons8_left_rear_window_down_red);
        views.setImageViewResource(R.id.rt_rr_window,
                isWindowClosed(carStatus.getRightRearWindow()) ? R.drawable.filler : R.drawable.icons8_right_rear_window_down_red);

        // Get the right images to use for this vehicle
        Map<String, Integer> vehicleImages = Utils.getVehicleDrawables(vehicleInfo.getVIN());

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

        views.setImageViewResource(R.id.wireframe, vehicleImages.get(Utils.WIREFRAME));

        views.setTextColor(R.id.ota_line2, context.getColor(R.color.white));

        // OTA status
        drawOTAInfo(context, views, vehicleInfo, timeFormat);

        // Location
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.show_location_key), true)) {
            views.setViewVisibility(R.id.location_container, View.VISIBLE);
            updateLocation(context, views, carStatus.getLatitude(), carStatus.getLongitude());
        } else {
            views.setViewVisibility(R.id.location_container, View.GONE);
        }

        updateLinkedApps(context, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    private void updateAppLogout(Context context, AppWidgetManager appWidgetManager, int appWidgetId, InfoRepository info) {
        RemoteViews views = getWidgetView(context);

        // Make sure the left side is visible depending on the widget width
        Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        onResize(appWidgetOptions, views);

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

        int fuelType = Utils.getFuelType(vehicleInfo.getVIN());
        boolean hasEngine = fuelType == Utils.FUEL_GAS || fuelType == Utils.FUEL_HYBRID;
        views.setViewVisibility(R.id.lock_gasoline, hasEngine ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.bottom_gasoline, hasEngine ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.lock_electric, hasEngine ? View.GONE : View.VISIBLE);
        views.setViewVisibility(R.id.lock_electric, hasEngine ? View.GONE : View.VISIBLE);

        // Reset everything else
        views.setTextViewText(R.id.lastRefresh, "Not logged in");
        views.setTextViewText(R.id.odometer, "Odo: N/A");
        views.setTextViewText(R.id.LVBVoltage, "LVB Volts: N/A");
        views.setTextColor(R.id.ota_line2, context.getColor(R.color.white));
        views.setTextViewText(R.id.ota_line1, "");
        views.setTextViewText(R.id.ota_line2, "");
        views.setTextViewText(R.id.location_line1, "");
        views.setTextViewText(R.id.location_line2, "");
        views.setTextViewText(R.id.location_line3, "");

        views.setProgressBar(R.id.HBVChargeProgress, 100, 0, false);
        views.setTextViewText(R.id.HVBChargePercent, "N/A");
        views.setTextViewText(R.id.GOM, "N/A");

        views.setImageViewResource(R.id.ignition, R.drawable.ignition_icon_gray);
        views.setImageViewResource(R.id.lock_gasoline, R.drawable.locked_icon_gray);
        views.setImageViewResource(R.id.lock_electric, R.drawable.locked_icon_gray);
        views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_gray);

        views.setImageViewResource(R.id.lt_ft_window, R.drawable.filler);
        views.setImageViewResource(R.id.rt_ft_window, R.drawable.filler);
        views.setImageViewResource(R.id.lt_rr_window, R.drawable.filler);
        views.setImageViewResource(R.id.rt_rr_window, R.drawable.filler);

        views.setInt(R.id.lftire, "setBackgroundResource", R.drawable.pressure_oval);
        views.setTextViewText(R.id.lftire, "N/A");
        views.setInt(R.id.rftire, "setBackgroundResource", R.drawable.pressure_oval);
        views.setTextViewText(R.id.rftire, "N/A");
        views.setInt(R.id.lrtire, "setBackgroundResource", R.drawable.pressure_oval);
        views.setTextViewText(R.id.lrtire, "N/A");
        views.setInt(R.id.rrtire, "setBackgroundResource", R.drawable.pressure_oval);
        views.setTextViewText(R.id.rrtire, "N/A");

        if (fuelType != Utils.FUEL_GAS) {
            views.setTextColor(R.id.LVBVoltage, context.getColor(R.color.white));
            views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_gray);
            views.setImageViewResource(R.id.plug, R.drawable.plug_icon_gray);
        }

        // Get the wireframe for the vehicle
        Map<String, Integer> vehicleImages = Utils.getVehicleDrawables(vehicleInfo.getVIN());
        views.setImageViewResource(R.id.wireframe, vehicleImages.get(Utils.WIREFRAME));

        views.setImageViewResource(R.id.hood, R.drawable.filler);
        views.setImageViewResource(R.id.tailgate, R.drawable.filler);
        views.setImageViewResource(R.id.lt_ft_door, R.drawable.filler);
        views.setImageViewResource(R.id.rt_ft_door, R.drawable.filler);
        views.setImageViewResource(R.id.lt_rr_door, R.drawable.filler);
        views.setImageViewResource(R.id.rt_rr_door, R.drawable.filler);

        updateLinkedApps(context, views);

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

                String state = info[0].getUser().getProgramState();

                // There may be multiple widgets active, so update all of them
                for (int appWidgetId : appWidgetIds) {
                    if (!state.equals(Constants.STATE_INITIAL_STATE)) {
                        updateAppWidget(context, appWidgetManager, appWidgetId, info[0]);
                    } else {
                        updateAppLogout(context, appWidgetManager, appWidgetId, info[0]);
                    }
                }
            }
        };

        new Thread(() -> {
            info[0] = new InfoRepository(context);
            handler.sendEmptyMessage(0);
        }).start();
    }

    public void onResize(Bundle newOptions, RemoteViews views) {
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
//        Toast.makeText(context, "width = " + minWidth, Toast.LENGTH_SHORT).show();
        if (minWidth < 250) {
            views.setViewVisibility(R.id.leftside, View.GONE);
        } else {
            views.setViewVisibility(R.id.leftside, View.VISIBLE);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = getWidgetView(context);
        onResize(newOptions, views);
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
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
        // Handle the actions which don't require info about the vehicle or user
        String action = intent.getAction();
        int appWidgetId = intent.getIntExtra(APPWIDGETID, -1);
        String widget_action = action + "_" + appWidgetId;
        String widget_VIN = Constants.VIN_KEY + appWidgetId;

        if (action.equals(PROFILE_CLICK)) {
            ProfileManager.changeProfile(context, widget_VIN);
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
        } else if (action.equals(REFRESH_CLICK)) {
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

//    public void onReceive(Context context, Intent intent) {
//        // Handle the actions which don't require info about the vehicle or user
//        String action = intent.getAction();
//        String widget_action = action + "_" + intent.getStringExtra(APPWIDGETID);
//        String widget_VIN = Constants.VIN_KEY + intent.getStringExtra(APPWIDGETID);
//
//        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
//            if (intent.hasExtra(WIDGET_IDS_KEY)) {
//                int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
//                onUpdate(context, AppWidgetManager.getInstance(context), ids);
//            }
//            return;
////        } else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED) && intent.hasExtra(WIDGET_IDS_KEY)) {
////                int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
////                onUpdate(context, AppWidgetManager.getInstance(context), ids);
////                return;
//        } else if (action.equals(PROFILE_CLICK)) {
//            ProfileManager.changeProfile(context, widget_VIN) ;
//            return;
//        } else if (action.equals(WIDGET_CLICK)) {
//            intent = new Intent(context, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//            return;
//        } else if (action.equals(SETTINGS_CLICK)) {
//            intent = new Intent(context, SettingsActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//            return;
//        } else if (action.equals(LEFT_BUTTON_CLICK)) {
//            StoredData appInfo = new StoredData(context);
//            String appPackageName = appInfo.getLeftAppPackage();
//            if (appPackageName != null) {
//                PackageManager pm = context.getApplicationContext().getPackageManager();
//                intent = pm.getLaunchIntentForPackage(appPackageName);
//                if (intent != null) {
//                    context.startActivity(intent);
//                } else {
//                    appInfo.setLeftAppPackage(null);
//                    MainActivity.updateWidget(context);
//                    Toast.makeText(context, "App is no longer installed", Toast.LENGTH_LONG).show();
//                }
//            }
//            return;
//        } else if (action.equals(RIGHT_BUTTON_CLICK)) {
//            StoredData appInfo = new StoredData(context);
//            String appPackageName = appInfo.getRightAppPackage();
//            if (appPackageName != null) {
//                PackageManager pm = context.getApplicationContext().getPackageManager();
//                intent = pm.getLaunchIntentForPackage(appPackageName);
//                if (intent != null) {
//                    context.startActivity(intent);
//                } else {
//                    appInfo.setRightAppPackage(null);
//                    MainActivity.updateWidget(context);
//                    Toast.makeText(context, "App is no longer installed", Toast.LENGTH_LONG).show();
//                }
//            }
//            return;
//        } else if (action.equals(PHEVTOGGLE_CLICK)) {
//            int appWidgetId = intent.getIntExtra(APPWIDGETID, -1);
//            String mode = intent.getStringExtra("nextMode");
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            RemoteViews views = getWidgetView(context);
//            String nextMode;
//            if (mode.equals("showGasoline")) {
//                nextMode = "showElectric";
//                views.setViewVisibility(R.id.bottom_electric, View.GONE);
//                views.setViewVisibility(R.id.bottom_gasoline, View.VISIBLE);
//            } else {
//                nextMode = "showGasoline";
//                views.setViewVisibility(R.id.bottom_electric, View.VISIBLE);
//                views.setViewVisibility(R.id.bottom_gasoline, View.GONE);
//            }
//            setPHEVCallbacks(context, views, Utils.FUEL_PHEV, appWidgetId, nextMode);
//            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
//            return;
//        }
//
//        // The remaining actions need to wait for info from the databases
//        InfoRepository[] info = {null};
//
//        final Gson gson = new Gson();
//        Handler handler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                String VIN = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getString(widget_VIN, null);
//                VehicleInfo vehInfo = info[0].getVehicleByVIN(VIN);
//
//                UserInfo userInfo = info[0].getUser();
//                if (action.equals(LOCK_CLICK)) {
//                    // Avoid performing the action on a single press (in case the widget is accidentally
//                    // touched): require two presses within 500 ms of one another to activate.
//                    LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
//                    long nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//                    if (nowtime - lastLockClicktime < 500) {
//                        CarStatus carStatus = vehInfo.getCarStatus();
//                        if (!awaitingUpdate && carStatus != null && carStatus.getLock() != null) {
//                            if (carStatus.getLock().equals("LOCKED")) {
//                                unlock(context, VIN);
//                            } else {
//                                lock(context, VIN);
//                            }
//                        }
//                    }
//                    lastLockClicktime = nowtime;
//                } else if (action.equals(IGNITION_CLICK)) {
//                    LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
//                    long nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//                    if (nowtime - lastIgnitionClicktime < 500) {
//                        CarStatus carStatus = vehInfo.getCarStatus();
//                        if (!awaitingUpdate && carStatus != null && carStatus.getRemoteStartStatus() != null
//                                && carStatus.getIgnition() != null && carStatus.getIgnition().equals("Off")) {
//                            if (!carStatus.getRemoteStartStatus()) {
//                                remoteStart(context, VIN);
//                            } else {
//                                remoteStop(context, VIN);
//                            }
//                        }
//                    }
//                    lastIgnitionClicktime = nowtime;
//                } else if (action.equals(REFRESH_CLICK)) {
//                    LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
//                    long nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//                    if (nowtime - lastRefreshClicktime[0] < 500) {
//                        long lastUpdateInMillis = vehInfo.getLastUpdateTime();
//                        String timeFormat = userInfo.getCountry().equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;
//                        String lastUpdate = OTAViewActivity.convertMillisToDate(lastUpdateInMillis, timeFormat);
//                        Toast.makeText(context, "Last update at " + lastUpdate, Toast.LENGTH_SHORT).show();
//                        long lastAlarmInMillis = new StoredData(context).getLastAlarmTime();
//                        String lastAlarm = OTAViewActivity.convertMillisToDate(lastAlarmInMillis, timeFormat);
//                        Toast.makeText(context, "Last alarm at " + lastAlarm, Toast.LENGTH_SHORT).show();
//                    }
//                    lastRefreshClicktime[0] = lastRefreshClicktime[1];
//                    lastRefreshClicktime[1] = nowtime;
//                }
//            }
//
//        };
//
//        new Thread(() -> {
//            info[0] = new InfoRepository(context);
//            handler.sendEmptyMessage(0);
//        }).start();
//        super.onReceive(context, intent);
//
//    }
}
