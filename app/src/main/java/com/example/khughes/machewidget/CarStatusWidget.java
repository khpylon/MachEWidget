package com.example.khughes.machewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.icu.text.DecimalFormatSymbols;
import android.icu.text.MessageFormat;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.CarStatus.CarStatus;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CarStatusWidget extends AppWidgetProvider {
    protected static final String PROFILE_CLICK = "Profile";
    protected static final String WIDGET_CLICK = "Widget";
    protected static final String SETTINGS_CLICK = "SettingsButton";
    protected static final String LEFT_BUTTON_CLICK = "FordPassButton";
    protected static final String RIGHT_BUTTON_CLICK = "ChargerButton";
    protected static final String IGNITION_CLICK = "IgnitionButton";
    protected static final String LOCK_CLICK = "LockButton";
    protected static final String REFRESH_CLICK = "Refresh";
    protected static final String PHEVTOGGLE_CLICK = "PHEVToggle";
    protected static final String UPDATE_CLICK = "ForceUpdate";

    protected static final String APPWIDGETID = "appWidgetId";

    protected static final String PADDING = "   ";
    protected static final String CHARGING_STATUS_NOT_READY = "NotReady";
    protected static final String CHARGING_STATUS_CHARGING_AC = "ChargingAC";
    protected static final String CHARGING_STATUS_CHARGING_DC = "ChargingDC";
    protected static final String CHARGING_STATUS_TARGET_REACHED = "ChargeTargetReached";
    protected static final String CHARGING_STATUS_PRECONDITION = "CabinPreconditioning";
    protected static final String CHARGING_STATUS_PAUSED = "EvsePaused";

    protected void updateTire(Context context, RemoteViews views, String pressure, String status,
                              String units, Double conversion, int id) {
        // Set the textview background color based on the status
        int drawable;
        if (status != null && !status.equals("Normal")) {
            drawable = R.drawable.pressure_oval_red_solid;
            // Get the tire pressure and do any conversion necessary.
            if (pressure != null) {
                try {
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
                } catch (NumberFormatException e) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "java.lang.NumberFormatException in CarStatusIdget.updateTire(): pressure = " + pressure);
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


    protected void updateLocation(Context context, RemoteViews views, String latitude, String longitude) {
        List<Address> addresses = null;
        String streetName = PADDING;
        String cityState = "";

        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);
        try {
            addresses = mGeocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            LogFile.e(context, MainActivity.CHANNEL_ID, "IOException in CarStatusWidget_5x5.updateAppWidget for Geocoder (this is normal)");
            return;
        }

        views.setTextViewText(R.id.location_line1, "Location:");

        // If an address was found, go with the first entry
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            // Street address and name
            if (address.getSubThoroughfare() != null) {
                streetName += address.getSubThoroughfare() + " ";
            }
            if (address.getThoroughfare() != null) {
                streetName += address.getThoroughfare();
            }

            // Other locality info (state, province, etc)
            if (address.getLocality() != null && address.getAdminArea() != null) {
                String adminArea = address.getAdminArea();
                if (Utils.states.containsKey(adminArea)) {
                    adminArea = Utils.states.get(adminArea);
                }
                cityState = PADDING + address.getLocality() + ", " + adminArea;
            }

            // If no street, move city/state up
            if (streetName.equals(PADDING)) {
                streetName = cityState;
                cityState = "";
            }
        } else {
            streetName = PADDING + "N/A";
        }
//        streetName = PADDING + "45500 Fremont Blvd";
//        cityState = PADDING + "Fremont, CA";
        views.setTextViewText(R.id.location_line2, streetName);
        views.setTextViewText(R.id.location_line3, cityState);
    }

    // Check if a window is open or closed.  Undefined defaults to closed.
    protected boolean isWindowClosed(String status) {
        return (status == null || status.toLowerCase().replaceAll("[^a-z0-9]", "").contains("fullyclosed")
                || status.toLowerCase().contains("undefined"));
    }

    // Check if a door is open or closed.  Undefined defaults to closed.
    protected Boolean isDoorClosed(String status) {
        return (status == null || status.toLowerCase().contains("closed"));
    }

    // Set background transparency
    protected void setBackground(Context context, RemoteViews views) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useTranparency = sharedPref.getBoolean(context.getResources().getString(R.string.transp_bg_key), false);
        views.setInt(R.id.thewidget, "setBackgroundResource",
                useTranparency ? R.color.transparent_black : R.color.black);
    }

    protected VehicleInfo getVehicleInfo(Context context, InfoRepository info, int appWidgetId) {
        // Find the VIN associated with this widget
        String widget_VIN = Constants.VIN_KEY + appWidgetId;
        String VIN = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getString(widget_VIN, null);

        // If there's a VIN but no vehicle, then essentially there is no VIN
        if (VIN != null && info.getVehicleByVIN(VIN) == null) {
            VIN = null;
        }

        // if VIN is undefined, pick first VIN that we find
        if (VIN == null) {
            List<VehicleInfo> vehicles = info.getVehicles();
            // No vehicles; hmmm....
            if (vehicles.size() == 0) {
                return null;
            }
            //  Look for an enabled vehicle with this owner
            for (VehicleInfo vehicleInfo : vehicles) {
                if (vehicleInfo.isEnabled() && vehicleInfo.getUserId().equals(info.getUser().getUserId())) {
                    VIN = vehicleInfo.getVIN();
                    break;
                }
            }
            // If we found something, save it.
            if (VIN != null) {
                context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit().putString(widget_VIN, VIN).commit();
            }
        }

        return info.getVehicleByVIN(VIN);
    }

    protected PendingIntent getPendingSelfIntent(Context context, int id, String action) {
        Intent intent = new Intent(context, getClass());
        intent.putExtra(APPWIDGETID, id);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Define actions for clicking on various icons, including the widget itself
    protected void setCallbacks(Context context, RemoteViews views, int id) {
        views.setOnClickPendingIntent(R.id.thewidget, getPendingSelfIntent(context, id, WIDGET_CLICK));
        views.setOnClickPendingIntent(R.id.settings, getPendingSelfIntent(context, id, SETTINGS_CLICK));
        views.setOnClickPendingIntent(R.id.lastRefresh, getPendingSelfIntent(context, id, REFRESH_CLICK));

//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        boolean showAppLinks = sharedPref.getBoolean(context.getResources().getString(R.string.show_app_links_key), true);
//        views.setOnClickPendingIntent(R.id.leftappbutton, getPendingSelfIntent(context, id, showAppLinks ? LEFT_BUTTON_CLICK : WIDGET_CLICK));
//        views.setOnClickPendingIntent(R.id.rightappbutton, getPendingSelfIntent(context, id, showAppLinks ? RIGHT_BUTTON_CLICK : WIDGET_CLICK));

        boolean forceUpdates = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.user_forcedUpdate_key), false);
        views.setOnClickPendingIntent(R.id.refresh, getPendingSelfIntent(context, id, forceUpdates ? UPDATE_CLICK : WIDGET_CLICK));
        views.setViewVisibility(R.id.refresh, forceUpdates ? View.VISIBLE : View.GONE);

        boolean enableCommands = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.enable_commands_key), false);
        views.setOnClickPendingIntent(R.id.lock_gasoline, getPendingSelfIntent(context, id, enableCommands ? LOCK_CLICK : WIDGET_CLICK));
        views.setOnClickPendingIntent(R.id.lock_electric, getPendingSelfIntent(context, id, enableCommands ? LOCK_CLICK : WIDGET_CLICK));
        views.setOnClickPendingIntent(R.id.ignition, getPendingSelfIntent(context, id, enableCommands ? IGNITION_CLICK : WIDGET_CLICK));
    }

    protected void setPHEVCallbacks(Context context, RemoteViews views, boolean isPHEV, int id, String mode) {
        if (!isPHEV) {
            views.setOnClickPendingIntent(R.id.bottom_gasoline, getPendingSelfIntent(context, id, WIDGET_CLICK));
            views.setOnClickPendingIntent(R.id.bottom_electric, getPendingSelfIntent(context, id, WIDGET_CLICK));
        } else {
            Intent intent = new Intent(context, getClass());
            intent.putExtra(APPWIDGETID, id);
            intent.putExtra("nextMode", mode);
            intent.setAction(PHEVTOGGLE_CLICK);
            views.setOnClickPendingIntent(R.id.bottom_gasoline,
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
            intent.setAction(PHEVTOGGLE_CLICK);
            views.setOnClickPendingIntent(R.id.bottom_electric,
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
        }
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

    protected void drawRangeFuel(Context context, RemoteViews views, CarStatus carStatus,
                                 InfoRepository info, VehicleInfo vehicleInfo,
                                 double distanceConversion, String distanceUnits, boolean twoLines) {
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

        String rangeCharge = "N/A";
        if (!isICEOrHybrid) {
            // Estimated range
            Double range = carStatus.getElVehDTE();
            if (range != null && range > 0) {
                rangeCharge = MessageFormat.format("{0} {1}", Math.round(range * distanceConversion), distanceUnits);
            }

            // Charging port
            Boolean pluggedIn = carStatus.getPlugStatus();
            views.setImageViewResource(R.id.plug, pluggedIn ?
                    R.drawable.plug_icon_green : R.drawable.plug_icon_gray);

            // High-voltage battery
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

                // Normally there will be something from the GOM; if so, display this info with it
                if (!rangeCharge.equals("")) {
                    rangeCharge += twoLines ? "\n" : " - ";
                }
                if (chargeStatus.equals(CHARGING_STATUS_TARGET_REACHED)) {
                    rangeCharge += "Target Reached";
                    if (!vehicleInfo.getLastChargeStatus().equals(CHARGING_STATUS_TARGET_REACHED)) {
                        Notifications.chargeComplete(context);
                    }
                } else if (chargeStatus.equals(CHARGING_STATUS_PRECONDITION)) {
                    rangeCharge += "Preconditioning";
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(Constants.STATUSTIMEFORMAT, Locale.US);
                    Calendar endChargeTime = Calendar.getInstance();
                    try {
                        endChargeTime.setTime(sdf.parse(carStatus.getVehiclestatus().getChargeEndTime().getValue()));

                        Calendar nowTime = Calendar.getInstance();
                        long min = Duration.between(nowTime.toInstant(), endChargeTime.toInstant()).getSeconds() / 60;
                        if (min > 0) {
                            int hours = (int) min / 60;
                            min %= 60;
                            if (hours > 0) {
                                rangeCharge += hours + " hr";
                                if (min > 0) {
                                    rangeCharge += ", ";
                                }
                            }
                            if (min > 0) {
                                rangeCharge += (int) min + " min";
                            }
                            rangeCharge += " left";
                        }
                    } catch (ParseException e) {
                        LogFile.e(context, MainActivity.CHANNEL_ID, "exception in CarStatusWidget_5x5.updateAppWidget: ", e);
                    }
                }

                // If status changed, save for future reference.
                if (!vehicleInfo.getLastChargeStatus().equals(chargeStatus)) {
                    vehicleInfo.setLastChargeStatus(chargeStatus);
                    info.setVehicle(vehicleInfo);
                }
            } else {
                views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_gray);
            }
            views.setTextViewText(R.id.GOM, rangeCharge);

            // High-voltage battery charge levels
            Double chargeLevel = carStatus.getHVBFillLevel();
            if (chargeLevel != null) {
                views.setProgressBar(R.id.HBVChargeProgress, 100, (int) Math.round(chargeLevel + 0.5), false);
                views.setTextViewText(R.id.HVBChargePercent,
                        MessageFormat.format("{0}%", new DecimalFormat("#.0", // "#.0",
                                DecimalFormatSymbols.getInstance(Locale.US)).format(chargeLevel)));
            }
        }

        if (isICEOrHybrid || isPHEV) {
            // Estimated range
            Double range = carStatus.getDistanceToEmpty();
            if (range != null && range >= 0) {
                vehicleInfo.setLastDTE(range);
                info.setVehicle(vehicleInfo);
            } else {
                range = vehicleInfo.getLastDTE();
                if (range == null) {
                    range = -1.0;
                    distanceConversion = 1.0;
                }
            }
            rangeCharge = MessageFormat.format("{0} {1}", Math.round(range * distanceConversion), distanceUnits);
            views.setTextViewText(R.id.distanceToEmpty, rangeCharge);

            // Fuel tank level
            Double fuelLevel = carStatus.getFuelLevel();
            if (fuelLevel != null && fuelLevel >= 0) {
                vehicleInfo.setLastFuelLevel(fuelLevel);
                info.setVehicle(vehicleInfo);
            } else {
                fuelLevel = vehicleInfo.getLastFuelLevel();
            }

            if (fuelLevel == null) {
                fuelLevel = -1.0;
            } else if (fuelLevel > 100.0) {
                fuelLevel = 100.0;
            }

            views.setProgressBar(R.id.fuelLevelProgress, 100, (int) Math.round(fuelLevel + 0.5), false);
            views.setTextViewText(R.id.fuelLevelPercent,
                    MessageFormat.format("{0}%", new DecimalFormat("#.0", // "#.0",
                            DecimalFormatSymbols.getInstance(Locale.US)).format(fuelLevel)));

            if (carStatus.getVehiclestatus() == null) {
                Toast.makeText(context, "carStatus.getVehiclestatus() is null", Toast.LENGTH_SHORT).show();
            } else if (carStatus.getVehiclestatus().getFuel() == null) {
                Toast.makeText(context, "carStatus.getVehiclestatus().getFuel() is null", Toast.LENGTH_SHORT).show();
            } else {
                if (carStatus.getVehiclestatus().getFuel().getDistanceToEmpty() == null) {
                    Toast.makeText(context, "carStatus.getVehiclestatus().getFuel().getDistanceToEmpty() is null", Toast.LENGTH_SHORT).show();
                }
                if (carStatus.getVehiclestatus().getFuel().getFuelLevel() == null) {
                    Toast.makeText(context, "carStatus.getVehiclestatus().getFuel().getFuelLevel() is null", Toast.LENGTH_SHORT).show();
                }
            }
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
    }

    protected void drawLastRefresh(Context context, RemoteViews views, CarStatus carStatus, String timeFormat) {
        // Fill in the last update time
        Calendar lastUpdateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.STATUSTIMEFORMAT, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            lastUpdateTime.setTime(sdf.parse(carStatus.getLastRefresh()));// all done
        } catch (ParseException e) {
            LogFile.e(context, MainActivity.CHANNEL_ID, "exception in CarStatusWidget_5x5.updateAppWidget: ", e);
        }
        Calendar currentTime = Calendar.getInstance();
        long minutes = (Duration.between(lastUpdateTime.toInstant(), currentTime.toInstant()).getSeconds() + 30) / 60;
        LogFile.i(context, MainActivity.CHANNEL_ID, "updateAppWidget(): last vehicle update was " + minutes + " minutes ago.");

        String refresh = "Last refresh:\n  ";
        boolean displayTime = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.last_refresh_time_key), false);
        if (displayTime) {
            sdf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
            refresh += sdf.format(lastUpdateTime.getTime());
        } else {
            // less than 1 minute
            if (minutes < 1) {
                refresh += "just now";
            } else {
                refresh += Utils.elapsedMinutesToDescription(minutes) + " ago";
            }
        }
        views.setTextViewText(R.id.lastRefresh, refresh);
    }

    protected void drawOdometer(RemoteViews views, CarStatus carStatus, double distanceConversion, String distanceUnits) {
        Double odometer = carStatus.getOdometer();
        if (odometer != null && odometer > 0) {
            // FordPass truncates; go figure.
            views.setTextViewText(R.id.odometer,
                    MessageFormat.format("Odo: {0} {1}", Double.valueOf(odometer * distanceConversion).intValue(), distanceUnits));
        } else {
            views.setTextViewText(R.id.odometer, "Odo: ---");
        }
    }

    // OTA status
    protected void drawOTAInfo(Context context, RemoteViews views, VehicleInfo vehicleInfo, String timeFormat) {
        OTAStatus otaStatus = vehicleInfo.toOTAStatus();
        boolean displayOTA = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.show_OTA_key), true) && vehicleInfo.isSupportsOTA();

        views.setViewVisibility(R.id.ota_container, displayOTA ? View.VISIBLE : View.GONE);
        if (displayOTA && otaStatus != null) {
            // If the report doesn't say the vehicle DOESN'T support OTA, then try to display something
            if (Utils.OTASupportCheck(vehicleInfo.getOtaAlertStatus())) {
                views.setTextViewText(R.id.ota_line1, "OTA Status:");
                String OTArefresh;
                if (otaStatus.getFuseResponse() == null) {
                    OTArefresh = "No OTA data";
                } else {
                    long lastOTATime = vehicleInfo.getLastOTATime();
                    String currentUTCOTATime = otaStatus.getOTADateTime();
                    if (currentUTCOTATime == null) {
                        OTArefresh = "No date specified";
                    } else {
                        long currentOTATime = OTAViewActivity.convertDateToMillis(currentUTCOTATime);

                        // If there's new information, display that data/time in a different color
                        if (currentOTATime > lastOTATime) {
                            // if OTA failed, show it in red (that means something bad)
                            String OTAResult = otaStatus.getOTAAggregateStatus();
                            if (OTAResult != null && OTAResult.equals("failure")) {
                                views.setTextColor(R.id.ota_line2, context.getColor(R.color.red));
                            } else {
                                views.setTextColor(R.id.ota_line2, context.getColor(R.color.green));
                            }
                            OTArefresh = OTAViewActivity.convertMillisToDate(currentOTATime, timeFormat);
                            Notifications.newOTA(context);
                        } else {
                            views.setTextColor(R.id.ota_line2, context.getColor(R.color.white));
                            OTArefresh = OTAViewActivity.convertMillisToDate(lastOTATime, timeFormat);
                        }
                    }
                }
                views.setTextViewText(R.id.ota_line2, PADDING + OTArefresh);
            }
        }
    }

    protected void setAppBitmap(Context context, RemoteViews views, String appPackageName, int id) {
        try {
            if (appPackageName != null) {
                Drawable icon = context.getApplicationContext().getPackageManager().getApplicationIcon(appPackageName);
                if (icon != null) {
                    int size = Math.min(icon.getIntrinsicWidth(), 96);
                    Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bmp);
                    icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    icon.draw(canvas);
                    views.setImageViewBitmap(id, bmp);
                }
            } else {
                views.setImageViewResource(id, R.drawable.x_gray);
            }
        } catch (PackageManager.NameNotFoundException e) {
            views.setImageViewResource(id, R.drawable.x_gray);
        }
    }

    protected void drawVehicleImage(Context context, RemoteViews views, CarStatus carStatus, int vehicleColor, ArrayList<Integer> whatsOpen, Map<String, Integer> vehicleImages) {
        // If we're not passed a list, create one
        if (whatsOpen == null) {
            whatsOpen = new ArrayList<>();
        }

        // Find anything that's open
        whatsOpen.add(isDoorClosed(carStatus.getFrunk()) ? null : vehicleImages.get(Utils.HOOD));
        whatsOpen.add(isDoorClosed(carStatus.getTailgate()) ? null : vehicleImages.get(Utils.TAILGATE));
        whatsOpen.add(isDoorClosed(carStatus.getDriverDoor()) ? null : vehicleImages.get(Utils.LEFT_FRONT_DOOR));
        whatsOpen.add(isDoorClosed(carStatus.getPassengerDoor()) ? null : vehicleImages.get(Utils.RIGHT_FRONT_DOOR));
        whatsOpen.add(isDoorClosed(carStatus.getLeftRearDoor()) ? null : vehicleImages.get(Utils.LEFT_REAR_DOOR));
        whatsOpen.add(isDoorClosed(carStatus.getRightRearDoor()) ? null : vehicleImages.get(Utils.RIGHT_REAR_DOOR));
        whatsOpen.removeAll(Collections.singleton(null));

        // Determine the orientation of the image
        Drawable icon = context.getDrawable(vehicleImages.get(Utils.WIREFRAME));
        int width = icon.getIntrinsicWidth();
        int height = icon.getIntrinsicHeight();
        if (width > height) {
            width = 225;
            height = 100;
        } else {
            width = 100;
            height = 225;
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // See if color image is enabled and defined.
        Boolean useColor = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.use_colors_key), false);

        Utils.drawColoredVehicle(context, bmp, vehicleColor, whatsOpen, useColor, vehicleImages);
        views.setImageViewBitmap(R.id.wireframe, bmp);
    }

    protected void updateLinkedApps(Context context, RemoteViews views) {
        boolean showAppLinks = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.show_app_links_key), true);
        if (showAppLinks) {
            setAppBitmap(context, views, new StoredData(context).getLeftAppPackage(), R.id.leftappbutton);
            setAppBitmap(context, views, new StoredData(context).getRightAppPackage(), R.id.rightappbutton);
        } else {
            views.setImageViewResource(R.id.leftappbutton, R.drawable.filler);
            views.setImageViewResource(R.id.rightappbutton, R.drawable.filler);
        }
    }

    private Handler getHandler(Context context) {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("action");
                if (result != null && result.equals(NetworkCalls.COMMAND_SUCCESSFUL)) {
                    StatusReceiver.nextAlarm(context, 2);
                }
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
        };
    }

    protected void remoteStart(Context context, String VIN) {
        NetworkCalls.remoteStart(getHandler(context), context, VIN);
    }

    protected void remoteStop(Context context, String VIN) {
        NetworkCalls.remoteStop(getHandler(context), context, VIN);
    }

    protected void lock(Context context, String VIN) {
        NetworkCalls.lockDoors(getHandler(context), context, VIN);
    }

    protected void unlock(Context context, String VIN) {
        NetworkCalls.unlockDoors(getHandler(context), context, VIN);
    }

    protected void forceUpdate(Context context, String VIN) {
        NetworkCalls.updateStatus(
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        String result = msg.getData().getString("action");
                        if (result != null && result.equals(NetworkCalls.COMMAND_SUCCESSFUL)) {
                            StatusReceiver.nextAlarm(context, 2);
                        }
                    }
                },
                context, VIN);
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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static final long MILLIS = 1000;
    private static final long SECONDS = 60;
    private static final long MINUTES = 60;

    private static final int FIRST_LIMIT = 3;
    private static final long FIRST_INTERVAL = 2 * SECONDS;

    private static final int SECOND_LIMIT = 5;
    private static final long SECOND_INTERVAL = 10 * SECONDS;

    private static final long TIMEOUT_INTERVAL = 24 * MINUTES * SECONDS;

    public void onReceive(Context context, Intent intent) {
        // Handle the actions which don't require info about the vehicle or user
        String action = intent.getAction();
        int appWidgetId = intent.getIntExtra(APPWIDGETID, -1);
        String widget_action = action + "_" + appWidgetId;
        String widget_VIN = Constants.VIN_KEY + appWidgetId;

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager man = AppWidgetManager.getInstance(context);
            int[] ids = man.getAppWidgetIds(new ComponentName(context, this.getClass()));
            onUpdate(context, AppWidgetManager.getInstance(context), ids);
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
                    CarStatusWidget.updateWidget(context);
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
                    CarStatusWidget.updateWidget(context);
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
                                            long seconds = (nowTime - firstTime) / MILLIS;

                                            // If it's been twelve hours since the initial refresh, reset the count
                                            if (seconds > TIMEOUT_INTERVAL) {
                                                vehInfo.setForcedRefreshCount(0);
                                                info[0].setVehicle(vehInfo);
                                            }

                                            // Calculate how long since the last refresh
                                            seconds = (nowTime - lastTime) / MILLIS;
                                            long count = vehInfo.getForcedRefreshCount();

                                            // The first three refreshes must have 2 minutes between them; the next
                                            // two refreshes must have 10 minutes
                                            if ((count < FIRST_LIMIT && seconds > FIRST_INTERVAL) || (count < SECOND_LIMIT && seconds > SECOND_INTERVAL)) {
                                                long timeout = info[0].getUser().getExpiresIn();
                                                seconds = (timeout - nowTime) / MILLIS;
                                                // If the access token has expired, or is about to, do a refresh first
                                                if (seconds < 30) {
                                                    Toast.makeText(context, "The token is being refreshed; this may take a minute.", Toast.LENGTH_SHORT).show();
                                                    StatusReceiver.nextAlarm(context, 2);
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                synchronized (this) {
                                                                    wait(15 * MILLIS);
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
                                                    forceUpdate(context, VIN);
                                                }
                                            } else if (count < FIRST_LIMIT) {
                                                Toast.makeText(context, "Cannot force update for another " + Utils.elapsedSecondsToDescription(2 * 60 - seconds) + ".", Toast.LENGTH_SHORT).show();
                                            } else if (count < SECOND_LIMIT) {
                                                Toast.makeText(context, "Cannot force update for another " + Utils.elapsedSecondsToDescription(10 * 60 - seconds) + "", Toast.LENGTH_SHORT).show();
                                            } else {
                                                long remainingMinutes = ((firstTime - nowTime) / MILLIS + TIMEOUT_INTERVAL) / SECONDS;
                                                Toast.makeText(context, "Too many forced updates; feature disabled for " +
                                                        Utils.elapsedMinutesToDescription(remainingMinutes) + ".", Toast.LENGTH_SHORT).show();
                                            }
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

    public static void updateWidget(Context context) {
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);
    }

}
