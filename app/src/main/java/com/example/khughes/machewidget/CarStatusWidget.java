package com.example.khughes.machewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class CarStatusWidget extends AppWidgetProvider {
    public static final String WIDGET_IDS_KEY = "com.example.khughes.machewidget.CARSTATUSWIDGET";

    private static final String WIDGET_CLICK = "Widget";
    private static final String SETTINGS_CLICK = "SettingsButton";
    private static final String FORDPASS_CLICK = "FordPassButton";
    private static final String CHARGER_CLICK = "ChargerButton";
    private static final String IGNITION_CLICK = "IgnitionButton";

    private static final String CHARGING_STATUS_NOT_READY = "NotReady";
    private static final String CHARGING_STATUS_CHARGING_AC = "ChargingAC";
    private static final String CHARGING_STATUS_CHARGING_DC = "ChargingDC";
    private static final String CHARGING_STATUS_TARGET_REACHED = "ChargeTargetReached";
    private static final String CHARGING_STATUS_PRECONDITION = "CabinPreconditioning";
    private static final String CHARGING_STATUS_PAUSED = "EvsePaused";

    private static int[][] front = {
            {R.drawable.left_fr_cl_right_fr_cl, R.drawable.left_fr_cl_right_fr_op},
            {R.drawable.left_fr_op_right_fr_cl, R.drawable.left_fr_op_right_fr_op},
    };

    private static int[][] rear = {
            {R.drawable.left_rr_cl_right_rr_cl, R.drawable.left_rr_cl_right_rr_op},
            {R.drawable.left_rr_op_right_rr_cl, R.drawable.left_rr_op_right_rr_op},
    };

    private static int[][] left = {
            {R.drawable.left_none, R.drawable.left_rear},
            {R.drawable.left_front, R.drawable.left_both},
    };

    private static int[][] right = {
            {R.drawable.right_none, R.drawable.right_rear},
            {R.drawable.right_front, R.drawable.right_both},
    };

    private void updateWindow(RemoteViews views, String window, int id, int drawable) {
        // If we can't confirm the window is open, draw nothing.
        if (window == null || window.equals("Fully closed position")) {
            drawable = R.drawable.filler;
        }
        views.setImageViewResource(id, drawable);
    }

    private void updateTire(RemoteViews views, String pressure, String status,
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
            Double value = Double.valueOf(pressure) * conversion;
            pressure = MessageFormat.format("{0}{1}", new DecimalFormat("#", // "#.0",
                    DecimalFormatSymbols.getInstance(Locale.US)).format(value), units);
            views.setInt(id, "setBackgroundResource", R.drawable.pressure_oval);
        } else {
            pressure = "N/A";
        }
        views.setTextViewText(id, pressure);
    }

    private void updateLocation(Context context, RemoteViews views, String latitude, String longitude) {
        List<Address> addresses = null;
        String streetName = "N/A";
        String cityState = "";

        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        Double lat = Double.valueOf(latitude);
        Double lon = Double.valueOf(longitude);
        try {
            addresses = mGeocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in CarStatusWidget.updateAppWidget: ", e);
        }

        // If an address was found, go with the first entry
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            // Street address and name
            if (address.getSubThoroughfare() != null) {
                streetName = address.getSubThoroughfare() + " ";
            }
            streetName += address.getThoroughfare();

            // Other locality info (state, province, etc)
            if (address.getLocality() != null && address.getAdminArea() != null) {
                String adminArea = address.getAdminArea();
                Map<String, String> states = new HashMap<String, String>();
                states.put("Alabama", "AL");
                states.put("Alaska", "AK");
                states.put("Alberta", "AB");
                states.put("American Samoa", "AS");
                states.put("Arizona", "AZ");
                states.put("Arkansas", "AR");
                states.put("Armed Forces (AE)", "AE");
                states.put("Armed Forces Americas", "AA");
                states.put("Armed Forces Pacific", "AP");
                states.put("British Columbia", "BC");
                states.put("California", "CA");
                states.put("Colorado", "CO");
                states.put("Connecticut", "CT");
                states.put("Delaware", "DE");
                states.put("District Of Columbia", "DC");
                states.put("Florida", "FL");
                states.put("Georgia", "GA");
                states.put("Guam", "GU");
                states.put("Hawaii", "HI");
                states.put("Idaho", "ID");
                states.put("Illinois", "IL");
                states.put("Indiana", "IN");
                states.put("Iowa", "IA");
                states.put("Kansas", "KS");
                states.put("Kentucky", "KY");
                states.put("Louisiana", "LA");
                states.put("Maine", "ME");
                states.put("Manitoba", "MB");
                states.put("Maryland", "MD");
                states.put("Massachusetts", "MA");
                states.put("Michigan", "MI");
                states.put("Minnesota", "MN");
                states.put("Mississippi", "MS");
                states.put("Missouri", "MO");
                states.put("Montana", "MT");
                states.put("Nebraska", "NE");
                states.put("Nevada", "NV");
                states.put("New Brunswick", "NB");
                states.put("New Hampshire", "NH");
                states.put("New Jersey", "NJ");
                states.put("New Mexico", "NM");
                states.put("New York", "NY");
                states.put("Newfoundland", "NF");
                states.put("North Carolina", "NC");
                states.put("North Dakota", "ND");
                states.put("Northwest Territories", "NT");
                states.put("Nova Scotia", "NS");
                states.put("Nunavut", "NU");
                states.put("Ohio", "OH");
                states.put("Oklahoma", "OK");
                states.put("Ontario", "ON");
                states.put("Oregon", "OR");
                states.put("Pennsylvania", "PA");
                states.put("Prince Edward Island", "PE");
                states.put("Puerto Rico", "PR");
                states.put("Quebec", "QC");
                states.put("Rhode Island", "RI");
                states.put("Saskatchewan", "SK");
                states.put("South Carolina", "SC");
                states.put("South Dakota", "SD");
                states.put("Tennessee", "TN");
                states.put("Texas", "TX");
                states.put("Utah", "UT");
                states.put("Vermont", "VT");
                states.put("Virgin Islands", "VI");
                states.put("Virginia", "VA");
                states.put("Washington", "WA");
                states.put("West Virginia", "WV");
                states.put("Wisconsin", "WI");
                states.put("Wyoming", "WY");
                states.put("Yukon Territory", "YT");
                if (states.containsKey(adminArea)) {
                    adminArea = states.get(adminArea);
                }
                cityState = address.getLocality() + ", " + adminArea;
            }
        }
        views.setTextViewText(R.id.locationStreet, streetName);
        views.setTextViewText(R.id.locationCity, cityState);
    }

    // Check if a door is open or closed.  Undefined defaults to closed.
    private Boolean isDoorClosed(String status) {
        return (status == null || status.equals("Closed"));
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void setCallbacks(Context context, RemoteViews views) {
        // Define actions for clicking on various icons, including the widget itself
        views.setOnClickPendingIntent(R.id.thewidget, getPendingSelfIntent(context, WIDGET_CLICK));
        views.setOnClickPendingIntent(R.id.settings, getPendingSelfIntent(context, SETTINGS_CLICK));
        views.setOnClickPendingIntent(R.id.fordpass, getPendingSelfIntent(context, FORDPASS_CLICK));
        views.setOnClickPendingIntent(R.id.chargerapp, getPendingSelfIntent(context, CHARGER_CLICK));
//        views.setOnClickPendingIntent(R.id.ignition, getPendingSelfIntent(context, IGNITION_CLICK));
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.car_status_widget);

        setCallbacks(context, views);

        // If no status information, print something generic and return
        // TODO: also refresh the icons as if we're logged out?
        CarStatus carStatus = new StoredData(context).getCarStatus();
        if (carStatus == null || carStatus.getVehiclestatus() == null) {
            views.setTextViewText(R.id.lastRefresh, "Unable to retrieve status information.");
            return;
        }

        views.setInt(R.id.lftire, "setBackgroundResource", R.drawable.pressure_oval_red);

        // Fill in the last update time
        Calendar lastUpdateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            lastUpdateTime.setTime(sdf.parse(carStatus.getLastRefresh()));// all done
        } catch (ParseException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in CarStatusWidget.updateAppWidget: ", e);
        }
        Calendar currentTime = Calendar.getInstance();
        long minutes = (Duration.between(lastUpdateTime.toInstant(), currentTime.toInstant()).getSeconds() + 30) / 60;
        Log.i(MainActivity.CHANNEL_ID, "Last vehicle update was " + minutes + " minutes ago.");

        String refresh = "Last refresh:\n  ";
// less than 1 minute
        if (minutes < 1) {
            refresh += minutes + " just now";
// less than a hour
        } else if (minutes < 60) {
            refresh += minutes + " min ago";
// less than a day
        } else if (minutes / 60 < 24) {
            refresh += (minutes / 60) + " hr";
// right on the hour
            if ((minutes % 60) == 0) {
                if (minutes == 60) {
                    refresh += " ago";
                } else {
                    refresh += "s ago";
                }
                // hours and minutes
            } else {
                if (minutes >= 120) {
                    refresh += "s";
                }
                refresh += ", " + (minutes % 60) + " min ago";
            }
// days
        } else {
            long days = minutes / (24 * 60);
            if (days == 1) {
                refresh += " 1 day ago";

            } else {
                refresh += days + " days ago";
            }
        }
        views.setTextViewText(R.id.lastRefresh, refresh);

        // Get conversion factors for Metric vs Imperial measurement units
        StoredData appInfo = new StoredData(context);
        Double distanceConversion;
        String distanceUnits;
        if (appInfo.getSpeedUnits().equals("MPH")) {
            distanceConversion = Constants.KMTOMILES;
            distanceUnits = "miles";
        } else {
            distanceConversion = 1.0;
            distanceUnits = "km";
        }
        Double pressureConversion;
        String pressureUnits;
        if (appInfo.getPressureUnits().equals("PSI")) {
            pressureConversion = Constants.KPATOPSI;
            pressureUnits = "psi";
        } else {
            pressureConversion = 1.0;
            pressureUnits = "kPa";
        }

        // Door locks
        String lockStatus = carStatus.getLock();
        if (lockStatus != null) {
            views.setImageViewResource(R.id.lock, lockStatus.equals("LOCKED") ?
                    R.drawable.locked_icon_green : R.drawable.unlocked_icon_red);
        }

        // Ignition
        String ignition = carStatus.getIgnition();
        if (ignition != null) {
            views.setImageViewResource(R.id.ignition, ignition.equals("Off") ?
                    R.drawable.ignition_icon_gray : R.drawable.ignition_icon_green);
        }

        // Motion alarm and deep sleep state
        String alarm = carStatus.getAlarm();
        Boolean sleep = carStatus.getDeepSleep();
        if (sleep != null && sleep) {
            views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_zzz_red);
            views.setTextViewText(R.id.sleep, "Deep sleep: yes");
        } else {
            views.setTextViewText(R.id.sleep, "Deep sleep: no");
            if (alarm != null) {
                views.setImageViewResource(R.id.alarm, alarm.equals("NOTSET") ?
                        R.drawable.bell_icon_red : R.drawable.bell_icon_green);
            } else {
                views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_gray);
            }
        }

        // Charging port
        Boolean pluggedIn = carStatus.getPlugStatus();
        views.setImageViewResource(R.id.plug, pluggedIn ?
                R.drawable.plug_icon_green : R.drawable.plug_icon_gray);

        // High-voltage battery
        String remainingTime = "Not charging";
        if (pluggedIn) {
            String chargeStatus = carStatus.getChargingStatus();
            switch (chargeStatus) {
                case CHARGING_STATUS_NOT_READY:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_red);
                    break;
                case CHARGING_STATUS_CHARGING_AC:
                case CHARGING_STATUS_CHARGING_DC:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_charging);
                    break;
                case CHARGING_STATUS_TARGET_REACHED:
                case CHARGING_STATUS_PRECONDITION:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_charged_green);
                    break;
                case CHARGING_STATUS_PAUSED:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_yellow);
                    break;
                default:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_gray);
                    break;
            }

            if (chargeStatus == null) {
                remainingTime = "";
            } else if (chargeStatus.equals(CHARGING_STATUS_TARGET_REACHED)) {
                remainingTime = "Target Reached";
            } else if (chargeStatus.equals(CHARGING_STATUS_PRECONDITION)) {
                remainingTime = "Preconditioning";
            } else {
                sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US);
                Calendar endChargeTime = Calendar.getInstance();
                String endChargeTimeStr = carStatus.getVehiclestatus().getChargeEndTime().getValue();
                try {
                    endChargeTime.setTime(sdf.parse(carStatus.getVehiclestatus().getChargeEndTime().getValue()));

                    Calendar nowTime = Calendar.getInstance();
                    long min = Duration.between(nowTime.toInstant(), endChargeTime.toInstant()).getSeconds() / 60;
                    if (min > 0) {
                        int hours = (int) min / 60;
                        min %= 60;
                        if (hours > 0) {
                            remainingTime = Integer.toString(hours) + " hr";
                            if (min > 0) {
                                remainingTime += ", ";
                            }
                        } else {
                            remainingTime = "";
                        }
                        if (min > 0) {
                            remainingTime += Integer.toString((int) min) + " min";
                        }
                        remainingTime += " left.";
                    }
                } catch (ParseException e) {
                    Log.e(MainActivity.CHANNEL_ID, "exception in CarStatusWidget.updateAppWidget: ", e);
                }
            }
        } else {
            views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_gray);
        }
        //views.setTextViewText(R.id.remainingChargeTime, remainingTime);

        // High-voltage battery charge levels
        Double chargeLevel = carStatus.getHVBFillLevel();
        if (chargeLevel != null) {
            views.setProgressBar(R.id.HBVChargeProgress, 100, (int) Math.round(chargeLevel + 0.5), false);
            views.setTextViewText(R.id.HVBChargePercent,
                    MessageFormat.format("{0}%", new DecimalFormat("#.0", // "#.0",
                            DecimalFormatSymbols.getInstance(Locale.US)).format(chargeLevel)));
        }

        // Estimated range
        Double range = carStatus.getElVehDTE();
        if (range != null && range > 0) {
            views.setTextViewText(R.id.GOM,
                    MessageFormat.format("{0} {1}", Math.round(range * distanceConversion), distanceUnits));
        } else {
            views.setTextViewText(R.id.GOM, "");
        }

        // 12 volt battery status
        Integer LVBLevel = carStatus.getLVBVoltage();
        String LVBStatus = carStatus.getLVBStatus();
        if (LVBLevel != null && LVBStatus != null) {
            views.setTextColor(R.id.LVBVoltage,
                    context.getColor(LVBStatus.equals("STATUS_GOOD") ? R.color.white : R.color.red));
            views.setTextViewText(R.id.LVBVoltage, MessageFormat.format("LVB Volts: {0}V", LVBLevel));
        } else {
            views.setTextColor(R.id.LVBVoltage, context.getColor(R.color.white));
            views.setTextViewText(R.id.LVBVoltage, MessageFormat.format("LVB Volts: N/A", LVBLevel));
        }

        // Current Odometer reading
        Double odometer = carStatus.getOdometer();
        if (odometer != null && odometer > 0) {
            views.setTextViewText(R.id.odometer,
                    MessageFormat.format("Odo: {0} {1}", Math.round(odometer * distanceConversion), distanceUnits));
        } else {
            views.setTextViewText(R.id.odometer, "Odo: ---");
        }

        // Location
        updateLocation(context, views, carStatus.getLatitude(), carStatus.getLongitude());

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
        updateWindow(views, carStatus.getDriverWindow(), R.id.lfwindow, R.drawable.icons8_left_front_window_down_red);
        updateWindow(views, carStatus.getPassengerWindow(), R.id.rfwindow, R.drawable.icons8_right_front_window_down_red);
        updateWindow(views, carStatus.getLeftRearWindow(), R.id.lrwindow, R.drawable.icons8_left_rear_window_down_red);
        updateWindow(views, carStatus.getRightRearWindow(), R.id.rrwindow, R.drawable.icons8_right_rear_window_down_red);

        // Frunk and trunk statuses
        String frunk = carStatus.getFrunk();
        if (frunk == null || frunk.equals("Closed")) {
            views.setImageViewResource(R.id.frunk, R.drawable.frunk_closed);
        } else {
            views.setImageViewResource(R.id.frunk, R.drawable.frunk_open);
        }
        String trunk = carStatus.getTrunk();
        if (trunk == null || trunk.equals("Closed")) {
            views.setImageViewResource(R.id.trunk, R.drawable.trunk_closed);
        } else {
            views.setImageViewResource(R.id.trunk, R.drawable.trunk_open);
        }

        // Door statuses are trickier since there are mixed images that need to be used....
        int l_front_door = isDoorClosed(carStatus.getDriverDoor()) ? 0 : 1;
        int r_front_door = isDoorClosed(carStatus.getPassengerDoor()) ? 0 : 1;
        int l_rear_door = isDoorClosed(carStatus.getLeftRearDoor()) ? 0 : 1;
        int r_rear_door = isDoorClosed(carStatus.getRightRearDoor()) ? 0 : 1;

        views.setImageViewResource(R.id.front_doors, front[l_front_door][r_front_door]);
        views.setImageViewResource(R.id.left_doors, left[l_front_door][l_rear_door]);
        views.setImageViewResource(R.id.right_doors, right[r_front_door][r_rear_door]);
        views.setImageViewResource(R.id.rear_doors, rear[l_rear_door][r_rear_door]);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void updateAppWidgetOTA(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.car_status_widget);

        OTAStatus otaStatus = new StoredData(context).getOTAStatus();
        if (otaStatus != null) {
            String OTArefresh;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String lastOTATime = sharedPref.getString(context.getResources().getString(R.string.last_ota_time), "");
            String currentOTATime = OTAViewActivity.convertDate(otaStatus.getOTADateTime());
            if (currentOTATime != null && currentOTATime.compareTo(lastOTATime) > 0) {
                Notifications.newOTA(context);
                OTArefresh = "New info found";
            } else {
                OTArefresh = lastOTATime;
            }
            views.setTextViewText(R.id.OTAInfo, OTArefresh);
        }

        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
    }

    private void updateAppLogout(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.car_status_widget);

        setCallbacks(context, views);

        views.setTextViewText(R.id.lastRefresh, "Not logged in");
        views.setTextViewText(R.id.odometer, "Odo: N/A");
        views.setTextViewText(R.id.sleep, "Deep sleep: N/A");
        views.setTextViewText(R.id.LVBVoltage, "LVB Volts: N/A");
        views.setTextColor(R.id.LVBVoltage, context.getColor(R.color.white));
        views.setTextViewText(R.id.OTAInfo, "N/A");
        views.setTextViewText(R.id.locationStreet, "N/A");
        views.setTextViewText(R.id.locationCity, "");

        views.setProgressBar(R.id.HBVChargeProgress, 100, 0, false);
        views.setTextViewText(R.id.HVBChargePercent, "Charge N/A");
        views.setTextViewText(R.id.GOM, "N/A");
        views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_gray);

        views.setImageViewResource(R.id.plug, R.drawable.plug_icon_gray);
        views.setImageViewResource(R.id.ignition, R.drawable.ignition_icon_gray);
        views.setImageViewResource(R.id.lock, R.drawable.locked_icon_gray);
        views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_gray);

        views.setImageViewResource(R.id.frunk, R.drawable.frunk_closed);
        views.setImageViewResource(R.id.trunk, R.drawable.trunk_closed);
        views.setImageViewResource(R.id.left_doors, R.drawable.left_none);
        views.setImageViewResource(R.id.right_doors, R.drawable.right_none);
        views.setImageViewResource(R.id.front_doors, R.drawable.left_fr_cl_right_fr_cl);
        views.setImageViewResource(R.id.rear_doors, R.drawable.left_rr_cl_right_rr_cl);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setImageBitmap(RemoteViews views, Drawable icon, int id) {
        Bitmap bmp = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        Paint paint = new Paint();
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//        paint.setColorFilter(f);
        icon.draw(canvas);
//        canvas.drawBitmap(bmp, 0F, 0F, paint);
        views.setImageViewBitmap(id, bmp);
    }

    private void updateLinkedApp(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.car_status_widget);
        try {
            String appPackageName = new StoredData(context).getAppPackage();
            Drawable icon = context.getApplicationContext().getPackageManager().getApplicationIcon(appPackageName);
            if (icon != null) {
                setImageBitmap(views, icon, R.id.chargerapp);
            }
            //    icon = context.getApplicationContext().getPackageManager().getApplicationIcon(
//                    context.getResources().getString(R.string.fordpassPackage));
//            setImageBitmap(views, icon, R.id.fordpass);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        StoredData appInfo = new StoredData(context);
        ProgramStateMachine.States state = new ProgramStateMachine(appInfo.getProgramState()).getCurrentState();

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            if (!state.equals(ProgramStateMachine.States.INITIAL_STATE)) {
                updateAppWidgetOTA(context, appWidgetManager, appWidgetId);
                updateAppWidget(context, appWidgetManager, appWidgetId);
                updateLinkedApp(context, appWidgetManager, appWidgetId);
            } else {
                updateAppLogout(context, appWidgetManager, appWidgetId);
            }
        }
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
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) && intent.hasExtra(WIDGET_IDS_KEY)) {
            int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        } else if (action.equals(WIDGET_CLICK)) {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (action.equals(SETTINGS_CLICK)) {
            intent = new Intent(context, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (action.equals(FORDPASS_CLICK)) {
            PackageManager pm = context.getApplicationContext().getPackageManager();
            intent = pm.getLaunchIntentForPackage(context.getResources().getString(R.string.fordpassPackage));
            if (intent != null) {
                context.startActivity(intent);
            }
        } else if (action.equals(CHARGER_CLICK)) {
            PackageManager pm = context.getApplicationContext().getPackageManager();
            String appPackageName = new StoredData(context).getAppPackage();
            intent = pm.getLaunchIntentForPackage(appPackageName);
            if (intent != null) {
                context.startActivity(intent);
            }
        } else if (action.equals(IGNITION_CLICK)) {
        } else {
            super.onReceive(context, intent);
        }
    }
}