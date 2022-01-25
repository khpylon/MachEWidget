package com.example.khughes.machewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Implementation of App Widget functionality.
 */

public class StatusWidget extends AppWidgetProvider {

    public static final String WIDGET_IDS_KEY = "mywidgetproviderwidgetids";
    public static final String UPDATE_TYPE = "update_type";
    public static final int UPDATE_CAR = 0;
    public static final int UPDATE_OTA = UPDATE_CAR + 1;
    public static final int LOGGED_OUT = UPDATE_OTA + 1;

    private static final String CHARGING_STATUS_NOT_READY = "NotReady";
    private static final String CHARGING_STATUS_CHARGING_AC = "ChargingAC";
    private static final String CHARGING_STATUS_CHARGING_DC = "ChargingDC";
    private static final String CHARGING_STATUS_TARGET_REACHED = "ChargeTargetReached";
    private static final String CHARGING_STATUS_PRECONDITION = "CabinPreconditioning";
    private static final String CHARGING_STATUS_PAUSED = "EvsePaused";

    private static int updateType = UPDATE_CAR;
    private static int textWidth = 20;

    private int[][] leftFrontDoorIds = {
            {R.drawable.icons8_left_front_window_down_open_red, R.drawable.icons8_left_front_window_down_red,},
            {R.drawable.icons8_left_front_window_up_open_red, R.drawable.icons8_left_front_window_up_green,}
    };

    private int[][] rightFrontDoorIds = {
            {R.drawable.icons8_right_front_window_down_open_red, R.drawable.icons8_right_front_window_down_red},
            {R.drawable.icons8_right_front_window_up_open_red, R.drawable.icons8_right_front_window_up_green},
    };

    private int[][] leftRearDoorIds = {
            {R.drawable.icons8_left_rear_window_down_open_red, R.drawable.icons8_left_rear_window_down_red},
            {R.drawable.icons8_left_rear_window_up_open_red, R.drawable.icons8_left_rear_window_up_green},
    };

    private int[][] rightRearDoorIds = {
            {R.drawable.icons8_right_rear_window_down_open_red, R.drawable.icons8_right_rear_window_down_red},
            {R.drawable.icons8_right_rear_window_up_open_red, R.drawable.icons8_right_rear_window_up_green},
    };

    private int getDoorResource(int[][] icon, String doorStatus, String windowStatus) {
        return icon[(windowStatus.equals("Fully closed position") ? 1 : 0)]
                [(doorStatus.equals("Closed") ? 1 : 0)];

    }

    private int[][] trunkFrunkDoorIds = {
            {R.drawable.car_frunk_trunk_open, R.drawable.car_frunk_open},
            {R.drawable.car_trunk_open, R.drawable.car_nothing_open},
    };

    private int getTrunkFrunkResource(int[][] icon, String frunkStatus, String trunkStatus) {
        return icon[frunkStatus.equals("Closed") ? 1 : 0]
                [trunkStatus.equals("Closed") ? 1 : 0];
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.status_widget);
        CarStatus carStatus = new StoredData(context).getCarStatus();

        if (carStatus == null || carStatus.getVehiclestatus() == null) {
            views.setTextViewText(R.id.lastRefresh, "Unable to retrieve status information.");
            return;
        }

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

        Double chargeLevel = carStatus.getHVBFillLevel();
        if (chargeLevel != null) {
            views.setProgressBar(R.id.HBVChargeProgress, 100, (int) Math.round(chargeLevel + 0.5), false);
            views.setTextViewText(R.id.HVBChargeLevel,
                    MessageFormat.format("{0}% charge", new DecimalFormat("#.0", // "#.0",
                            DecimalFormatSymbols.getInstance(Locale.US)).format(chargeLevel)));
        }

        Double range = carStatus.getElVehDTE();
        if (range != null) {
            views.setTextViewText(R.id.estimatedRange,
                    MessageFormat.format("{0} {1} range", Math.round(range * distanceConversion), distanceUnits));
        }

        Integer LVBLevel = carStatus.getLVBVoltage();
        String LVBStatus = carStatus.getLVBStatus();
        if (LVBLevel != null && LVBStatus != null) {
            views.setTextColor(R.id.LVBVoltage,
                    context.getColor(LVBStatus.equals("STATUS_GOOD") ?
                            R.color.white : R.color.red));
            views.setTextViewText(R.id.LVBVoltage, MessageFormat.format("LVB {0}V", LVBLevel));
        }

        Double odometer = carStatus.getOdometer();
        if (odometer != null) {
            views.setTextViewText(R.id.odometer,
                    MessageFormat.format("{0} {1}", Math.round(odometer * distanceConversion), distanceUnits));
        }

        String frunk = carStatus.getFrunk();
        String trunk = carStatus.getTrunk();
        if (frunk != null && trunk != null) {
            views.setImageViewResource(R.id.trunkFrunk, getTrunkFrunkResource(trunkFrunkDoorIds, frunk, trunk));
        }

        String door = carStatus.getDriverDoor();
        String window = carStatus.getDriverWindow();
        if (door != null && window != null) {
            views.setImageViewResource(R.id.leftFrontDoor, getDoorResource(leftFrontDoorIds, door, window));
        }

        door = carStatus.getPassengerDoor();
        window = carStatus.getPassengerWindow();
        if (door != null && window != null) {
            views.setImageViewResource(R.id.rightFrontDoor, getDoorResource(rightFrontDoorIds, door, window));
        }

        door = carStatus.getLeftRearDoor();
        window = carStatus.getLeftRearWindow();
        if (door != null && window != null) {
            views.setImageViewResource(R.id.leftRearDoor, getDoorResource(leftRearDoorIds, door, window));
        }

        door = carStatus.getRightRearDoor();
        window = carStatus.getRightRearWindow();
        if (door != null && window != null) {
            views.setImageViewResource(R.id.rightRearDoor, getDoorResource(rightRearDoorIds, door, window));
        }

        String ignition = carStatus.getIgnition();
        if (ignition != null) {
            views.setImageViewResource(R.id.ignitionStatusIcon, ignition.equals("Off") ?
                    R.drawable.icons8_ignition_red : R.drawable.icons8_ignition_green);
        }

        String lockStatus = carStatus.getLock();
        if (lockStatus != null) {
            views.setImageViewResource(R.id.lockStatusIcon, lockStatus.equals("LOCKED") ?
                    R.drawable.icons8_lock_green : R.drawable.icons8_unlock_red);
        }

        String alarm = carStatus.getAlarm();
        if (alarm != null) {
            views.setImageViewResource(R.id.alarmStatusIcon, alarm.equals("NOTSET") ?
                    R.drawable.icons8_alarm_gray : R.drawable.icons8_alarm_red);
        }

        Boolean sleep = carStatus.getDeepSleep();
        if (sleep != null) {
            views.setImageViewResource(R.id.sleepStatusIcon, sleep ?
                    R.drawable.icons8_sleep_red : R.drawable.icons8_sleep_gray);
        }

        Boolean pluggedIn = carStatus.getPlugStatus();
        views.setImageViewResource(R.id.plugIcon, pluggedIn ?
                R.drawable.icons8_electrical_green_100 : R.drawable.icons8_electrical_red_100);

        String remainingTime = "Not charging";
        if (pluggedIn) {
            String chargeStatus = carStatus.getChargingStatus();
            switch (chargeStatus) {
                case CHARGING_STATUS_NOT_READY:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.icons8_charging_battery_red_100);
                    break;
                case CHARGING_STATUS_CHARGING_AC:
                case CHARGING_STATUS_CHARGING_DC:
                case CHARGING_STATUS_TARGET_REACHED:
                case CHARGING_STATUS_PRECONDITION:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.icons8_charging_battery_green_100);
                    break;
                case CHARGING_STATUS_PAUSED:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.icons8_charging_battery_paused_yellow_100);
                    break;
                default:
                    views.setImageViewResource(R.id.HVBIcon, R.drawable.icons8_charging_battery_gray_100);
                    break;
            }

            if (chargeStatus == null) {
                remainingTime = "";
            } else if (chargeStatus.equals(CHARGING_STATUS_TARGET_REACHED)) {
                remainingTime = "Target Reached";
            } else if (chargeStatus.equals(CHARGING_STATUS_PRECONDITION)) {
                remainingTime = "Preconditioning";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US);
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
                    Log.e(MainActivity.CHANNEL_ID, "exception in StatusWidget.updateAppWidget: ", e);
                }
            }
        } else {
            views.setImageViewResource(R.id.HVBIcon, R.drawable.icons8_charging_battery_gray_100);
        }

        views.setTextViewText(R.id.remainingChargeTime, remainingTime);

        List<Address> addresses = null;
        Geocoder mGeocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        String streetName = "";
        Double lat = Double.valueOf(carStatus.getLatitude());
        Double lon = Double.valueOf(carStatus.getLongitude());
        try {
            addresses = mGeocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in StatusWidget.updateAppWidget: ", e);
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            if (address.getSubThoroughfare() != null) {
                streetName = address.getSubThoroughfare() + " ";
            }
            streetName += address.getThoroughfare();

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
                if (streetName.length() > textWidth) {
                    streetName = streetName.substring(0, textWidth - 3) + "...";
                }
                String cityState = address.getLocality() + ", " + adminArea;
                if (cityState.length() > textWidth) {
                    cityState = cityState.substring(0, textWidth - 3) + "...";
                }
                streetName += "\n" + cityState;
            }
        }
        views.setTextViewText(R.id.location, streetName);

        Calendar lastUpdateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            lastUpdateTime.setTime(sdf.parse(carStatus.getLastRefresh()));// all done
        } catch (ParseException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in StatusWidget.updateAppWidget: ", e);
        }
        Calendar currentTime = Calendar.getInstance();
        long minutes = (Duration.between(lastUpdateTime.toInstant(), currentTime.toInstant()).getSeconds() + 30) / 60;
        Log.i(MainActivity.CHANNEL_ID, "Last vehicle update was " + minutes + " minutes ago.");

        String refresh = "Last refresh: ";
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

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void updateAppWidgetOTA(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.status_widget);

        OTAStatus otaStatus = new StoredData(context).getOTAStatus();
        if (otaStatus != null) {
            String OTArefresh;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String lastOTATime = sharedPref.getString(context.getResources().getString(R.string.last_ota_time), "");
            String currentOTATime = OTAViewActivity.convertDate(otaStatus.getOTADateTime());
            if (currentOTATime != null && currentOTATime.compareTo(lastOTATime) > 0) {
                Notifications.newOTA(context);
                OTArefresh = "New OTA\ninfo found";
            } else {
                if (lastOTATime.length() > textWidth) {
                    lastOTATime = lastOTATime.substring(0, textWidth - 3) + "...";
                }
                OTArefresh = "Last OTA update:\n" + lastOTATime;
            }
            views.setTextViewText(R.id.OTAinfo, OTArefresh);
        }

        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
    }

    private void updateAppLogout(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.status_widget);

        views.setTextViewText(R.id.lastRefresh, "Not logged in");

        views.setProgressBar(R.id.HBVChargeProgress, 100, 0, false);
        views.setTextViewText(R.id.HVBChargeLevel, "Charge N/A");
        views.setTextViewText(R.id.estimatedRange, "Range N/A");
        views.setTextViewText(R.id.LVBVoltage, "LVB N/A");
        views.setTextColor(R.id.LVBVoltage, context.getColor(R.color.white));

        views.setImageViewResource(R.id.plugIcon, R.drawable.icons8_electrical_gray_100);
        views.setImageViewResource(R.id.HVBIcon, R.drawable.icons8_charging_battery_gray_100);
        views.setTextViewText(R.id.remainingChargeTime, "");
        views.setTextViewText(R.id.odometer, "Odometer N/A");
        views.setTextViewText(R.id.location, "Location N/A");

        views.setImageViewResource(R.id.ignitionStatusIcon, R.drawable.icons8_ignition_gray);
        views.setImageViewResource(R.id.lockStatusIcon, R.drawable.icons8_lock_gray);
        views.setImageViewResource(R.id.alarmStatusIcon, R.drawable.icons8_alarm_gray);
        views.setImageViewResource(R.id.sleepStatusIcon, R.drawable.icons8_sleep_gray);

        views.setImageViewResource(R.id.trunkFrunk, R.drawable.car_nothing_gray);
        views.setImageViewResource(R.id.leftFrontDoor, R.drawable.icons8_left_front_window_up_gray);
        views.setImageViewResource(R.id.rightFrontDoor, R.drawable.icons8_right_front_window_up_gray);
        views.setImageViewResource(R.id.leftRearDoor, R.drawable.icons8_left_rear_window_up_gray);
        views.setImageViewResource(R.id.rightRearDoor, R.drawable.icons8_right_rear_window_up_gray);

        views.setTextViewText(R.id.OTAinfo, "Last OTA update:\nN/A");

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            switch (updateType) {
                case UPDATE_CAR:
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                    break;
                case UPDATE_OTA:
                    updateAppWidgetOTA(context, appWidgetManager, appWidgetId);
                    break;
                default:
                    updateAppLogout(context, appWidgetManager, appWidgetId);
                    break;
            }
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.status_widget);
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        if (minWidth < 300) {
            if (textWidth != 16) {
                textWidth = 16;
                updateAppWidget(context, appWidgetManager, appWidgetId);
                updateAppWidgetOTA(context, appWidgetManager, appWidgetId);
            }
        } else {
            if (textWidth != 20) {
                textWidth = 20;
                updateAppWidget(context, appWidgetManager, appWidgetId);
                updateAppWidgetOTA(context, appWidgetManager, appWidgetId);
            }
        }
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(WIDGET_IDS_KEY)) {
            int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
            if (intent.hasExtra(UPDATE_TYPE)) {
                updateType = intent.getExtras().getInt(UPDATE_TYPE);
            }
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        } else super.onReceive(context, intent);
    }
}
