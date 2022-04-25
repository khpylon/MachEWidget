package com.example.khughes.machewidget;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.CarStatus.CarStatus;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;
import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDao;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarStatusWidgetIntent extends IntentService {

    public static final String WIDGETINTENTACTIONKEY = "update_action";
    public static final String CARSTATUSINTENTACTION = "status_update";
    public static final String OTASTATUSINTENTACTION = "ota_update";

    private static final String PADDING = "   ";
    private static final String CHARGING_STATUS_NOT_READY = "NotReady";
    private static final String CHARGING_STATUS_CHARGING_AC = "ChargingAC";
    private static final String CHARGING_STATUS_CHARGING_DC = "ChargingDC";
    private static final String CHARGING_STATUS_TARGET_REACHED = "ChargeTargetReached";
    private static final String CHARGING_STATUS_PRECONDITION = "CabinPreconditioning";
    private static final String CHARGING_STATUS_PAUSED = "EvsePaused";

    public CarStatusWidgetIntent() {
        super("widgetintent");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void updateWindow(RemoteViews views, String window, int id, int drawable) {
        // If we can't confirm the window is open, draw nothing.
        if (window == null || window.toLowerCase().replaceAll("[^a-z0-9]", "").contains("fullyclosed")
                || window.toLowerCase().contains("undefined")) {
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

    private void updateLocation(Context context, RemoteViews views, String latitude, String longitude) {
        List<Address> addresses = null;
        String streetName = PADDING;
        String cityState = "";

        views.setTextViewText(R.id.location_line1, "Location:");

        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);
        try {
            addresses = mGeocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            LogFile.e(context, MainActivity.CHANNEL_ID, "IOException in CarStatusWidget.updateAppWidget (normal)");
        }

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

    // Check if a door is open or closed.  Undefined defaults to closed.
    private Boolean isDoorClosed(String status) {
        return (status == null || status.toLowerCase().contains("closed"));
    }

    // Based on the VIN, find the right widget layout
    private RemoteViews getWidgetView(Context context) {
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");
        return new RemoteViews(context.getPackageName(), Utils.getLayoutByVIN(VIN));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                CarStatusWidget.class));

        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");


        RemoteViews views = new RemoteViews(context.getPackageName(), Utils.getLayoutByVIN(VIN));

        String action = intent.getStringExtra(WIDGETINTENTACTIONKEY);

        VehicleInfoDao vehDao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao();
        VehicleInfo vehInfo = vehDao.findVehicleInfoByVIN(VIN);
        if (vehInfo == null) {
            return;
        }

        String userId = vehInfo.getUserId();
        UserInfo userInfo = UserInfoDatabase.getInstance(context)
                .userInfoDao().findUserInfo(userId);
        if (userInfo == null) {
            return;
        }

        String timeFormat = userInfo.getCountry().equals("USA") ? Constants.LOCALTIMEFORMATUS : Constants.LOCALTIMEFORMAT;

        if (action.equals(CARSTATUSINTENTACTION)) {
            CarStatus carStatus = vehInfo.getCarStatus();
//            CarStatusDatabase.getInstance(context).carStatusDao().findCarStatusByVIN(VIN);

            if (carStatus == null || carStatus.getVehiclestatus() == null) {
                views.setTextViewText(R.id.lastRefresh, "Unable to retrieve status information.");
                appWidgetManager.updateAppWidget(appWidgetIds, views);
                return;
            }

            int fuelType = Utils.getFuelType(VIN);

            // Fill in the last update time
            Calendar lastUpdateTime = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.STATUSTIMEFORMAT, Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                lastUpdateTime.setTime(sdf.parse(carStatus.getLastRefresh()));// all done
            } catch (ParseException e) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "exception in CarStatusWidget.updateAppWidget: ", e);
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
                    // less than an hour
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
                } else {
                    long days = minutes / (24 * 60);
                    // one day
                    if (days == 1) {
                        refresh += " 1 day ago";
                        // multiple days
                    } else {
                        refresh += days + " days ago";
                    }
                }
            }
            views.setTextViewText(R.id.lastRefresh, refresh);

            // Get conversion factors for Metric vs Imperial measurement units
            StoredData appInfo = new StoredData(context);
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

            // Door locks
            String lockStatus = carStatus.getLock();
            if (lockStatus != null) {
                views.setImageViewResource(R.id.lock, lockStatus.equals("LOCKED") ?
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
            views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_zzz_red);

//            views.setViewVisibility(R.id.sleep, View.GONE);
//            if (sleep != null && sleep) {
//                views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_zzz_red);
//                views.setTextViewText(R.id.sleep, "Deep sleep: yes");
//            } else {
//                views.setTextViewText(R.id.sleep, "Deep sleep: no");
//                if (alarm != null) {
//                    views.setImageViewResource(R.id.alarm, alarm.equals("NOTSET") ?
//                            R.drawable.bell_icon_red : R.drawable.bell_icon_green);
//                } else {
//                    views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_gray);
//                }
//            }

            String rangeCharge = "N/A";
            if (fuelType == Utils.FUEL_ELECTRIC) {

                // Estimated range
                Double range = carStatus.getElVehDTE();
                if (range != null && range > 0) {
                    rangeCharge = MessageFormat.format("{0} {1}", Math.round(range * distanceConversion), distanceUnits);
                }

                // Charging port
                Boolean pluggedIn = carStatus.getPlugStatus();
                views.setImageViewResource(R.id.plug, pluggedIn ?
                        R.drawable.plug_icon_green : R.drawable.plug_icon_gray);

                // Check charging station info.  If it's been cleared and we're plugges in, then update it
//            ChargeStation chargeStation = appInfo.getChargeStation(VIN);
//            if ((chargeStation == null || chargeStation.getVin() == null) && pluggedIn) {
//                getChargeStation(context, VIN);
//            }
//
//            double lat1 = Double.parseDouble(carStatus.getLatitude());
//            double lon1 = Double.parseDouble(carStatus.getLongitude());
//            double lat2 = chargeStation.getCoordinates().getLat();
//            double lon2 = chargeStation.getCoordinates().getLon();
//            float[] distance = new float[1];
//            new Location("home").distanceBetween(lat1, lon1, lat2, lon2, distance);

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

                    // Normally there will be something from the GOM; if so, display this info below it
                    if (!rangeCharge.equals("")) {
                        rangeCharge += "\n";
                    }
                    if (chargeStatus.equals(CHARGING_STATUS_TARGET_REACHED)) {
                        rangeCharge += "Target Reached";
                    } else if (chargeStatus.equals(CHARGING_STATUS_PRECONDITION)) {
                        rangeCharge += "Preconditioning";
                    } else {
                        sdf = new SimpleDateFormat(Constants.STATUSTIMEFORMAT, Locale.US);
                        Calendar endChargeTime = Calendar.getInstance();
                        try {
                            endChargeTime.setTime(sdf.parse(carStatus.getVehiclestatus().getChargeEndTime().getValue()));

                            // TODO: try to use charge station target charge level to adjust end time.
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
                            LogFile.e(context, MainActivity.CHANNEL_ID, "exception in CarStatusWidget.updateAppWidget: ", e);
                        }
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
            } else {
                // Estimated range
                Double range = carStatus.getDistanceToEmpty();
                if (range != null && range >= 0) {
//                    appInfo.setLastDTE(VIN, range);
                    vehInfo.setLastDTE(range);
                    vehDao.updateVehicleInfo(vehInfo);
                } else {
//                    range = appInfo.getLastDTE(VIN);
                    range = vehInfo.getLastDTE();
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
//                    appInfo.setLastFuelLevel(VIN, fuelLevel);
                    vehInfo.setLastFuelLevel(fuelLevel);
                    vehDao.updateVehicleInfo(vehInfo);
                } else {
//                    fuelLevel = appInfo.getLastFuelLevel(VIN);
                    fuelLevel = vehInfo.getLastFuelLevel();
                    if (fuelLevel == null) {
                        fuelLevel = -1.0;
                    } else if (fuelLevel > 100.0) {
                        fuelLevel = 100.0;
                    }
                }
                views.setProgressBar(R.id.fuelLevelProgress, 100, (int) Math.round(fuelLevel + 0.5), false);
                views.setTextViewText(R.id.fuelLevelPercent,
                        MessageFormat.format("{0}%", new DecimalFormat("#.0", // "#.0",
                                DecimalFormatSymbols.getInstance(Locale.US)).format(fuelLevel)));
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
            updateWindow(views, carStatus.getDriverWindow(), R.id.lfwindow, R.drawable.icons8_left_front_window_down_red);
            updateWindow(views, carStatus.getPassengerWindow(), R.id.rfwindow, R.drawable.icons8_right_front_window_down_red);
            updateWindow(views, carStatus.getLeftRearWindow(), R.id.lrwindow, R.drawable.icons8_left_rear_window_down_red);
            updateWindow(views, carStatus.getRightRearWindow(), R.id.rrwindow, R.drawable.icons8_right_rear_window_down_red);

            // Get the right images to use for this vehicle
            Map<String, Integer> vehicleImages = Utils.getVehicleDrawables(VIN);

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

            // Location
            if (PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(context.getResources().getString(R.string.show_location_key), true)) {
                views.setViewVisibility(R.id.location_container, View.VISIBLE);
                updateLocation(context, views, carStatus.getLatitude(), carStatus.getLongitude());
            } else {
                views.setViewVisibility(R.id.location_container, View.GONE);
            }
        } else if (action.equals(OTASTATUSINTENTACTION)) {
            // OTA status
            OTAStatus otaStatus = vehInfo.toOTAStatus();
            boolean displayOTA = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(context.getResources().getString(R.string.show_OTA_key), true);
            views.setViewVisibility(R.id.ota_container, displayOTA ? View.VISIBLE : View.GONE);
            if (displayOTA && vehInfo != null) {
                // If the report doesn't say the vehicle DOESN'T support OTA, then try to display something
                String OTAAlertStatus = vehInfo.getOtaAlertStatus();
                if (OTAAlertStatus != null && !OTAAlertStatus.toLowerCase().replaceAll("[^a-z0-9]", "").contains("doesntsupport")) {
                    views.setTextViewText(R.id.ota_line1, "OTA Status:");
                    String OTArefresh;
                    long lastOTATime = OTAViewActivity.getLastOTATimeInMillis(context, timeFormat);
                    String currentUTCOTATime = otaStatus.getOTADateTime();
                    if (currentUTCOTATime == null) {
                        OTArefresh = "Unknown";
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
                            OTArefresh = OTAViewActivity.convertMillisToDate(lastOTATime, timeFormat);
                        }
                    }
                    views.setTextViewText(R.id.ota_line2, PADDING + OTArefresh);
                }
            }
        }

        appWidgetManager.partiallyUpdateAppWidget(appWidgetIds, views);
    }
}
