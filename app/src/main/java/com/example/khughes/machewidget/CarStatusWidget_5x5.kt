package com.example.khughes.machewidget

import com.example.khughes.machewidget.Vehicle.Companion.getVehicle
import com.example.khughes.machewidget.VehicleImages.Companion.getRandomImage
import com.example.khughes.machewidget.VehicleColor.Companion.scanImageForColor
import com.example.khughes.machewidget.VehicleColor.Companion.isFirstEdition
import com.example.khughes.machewidget.ProfileManager.changeProfile
import android.widget.RemoteViews
import com.example.khughes.machewidget.CarStatus.CarStatus
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import android.icu.text.MessageFormat
import android.view.View
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import java.lang.NumberFormatException
import java.util.*

class CarStatusWidget_5x5 : CarStatusWidget() {
    override fun updateTire(
        context: Context, views: RemoteViews, pressure: String?, status: String?,
        units: String?, conversion: Double, id: Int
    ) {
        var result: String
        // Set the textview background color based on the status
        val drawable: Int = if (status != null && status != "Normal") {
            R.drawable.pressure_oval_red
        } else {
            R.drawable.pressure_oval
        }
        views.setInt(id, "setBackgroundResource", drawable)

        // Get the tire pressure and do any conversion necessary.
        if (pressure != null) {
            try {
                val value = pressure.toDouble()
                // Only display value if it's not ridiculous; after some OTA updates the
                // raw value is "65533"
                if (value < 2000) {
                    // If conversion is really small, show value in tenths
                    val pattern = if (conversion >= 0.1) "#" else "#.0"
                    result = MessageFormat.format(
                        "{0}{1}", DecimalFormat(
                            pattern,  // "#.0",
                            DecimalFormatSymbols.getInstance(Locale.US)
                        ).format(value * conversion), units
                    )
                    views.setInt(id, "setBackgroundResource", R.drawable.pressure_oval)
                } else {
                    result = "N/A"
                }
            } catch (e: NumberFormatException) {
                LogFile.e(
                    context,
                    MainActivity.CHANNEL_ID,
                    "java.lang.NumberFormatException in CarStatusWidget_5x5.updateTire(): pressure = $pressure"
                )
                result = "N/A"
            }
        } else {
            result = "N/A"
        }
        views.setTextViewText(id, result)
    }

    // Define actions for clicking on various icons, including the widget itself
    override fun setCallbacks(context: Context, views: RemoteViews, id: Int) {
        views.setOnClickPendingIntent(
            R.id.profile,
            getPendingSelfIntent(context, id, PROFILE_CLICK)
        )
        views.setOnClickPendingIntent(R.id.logo, getPendingSelfIntent(context, id, PROFILE_CLICK))
        val showAppLinks = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.resources.getString(R.string.show_app_links_key), true)
        views.setOnClickPendingIntent(
            R.id.leftappbutton,
            getPendingSelfIntent(context, id, if (showAppLinks) LEFT_BUTTON_CLICK else WIDGET_CLICK)
        )
        views.setOnClickPendingIntent(
            R.id.rightappbutton,
            getPendingSelfIntent(
                context,
                id,
                if (showAppLinks) RIGHT_BUTTON_CLICK else WIDGET_CLICK
            )
        )
        super.setCallbacks(context, views, id)
    }

    // Based on the VIN, find the right widget layout
    private fun getWidgetView(context: Context, widget_VIN: String): RemoteViews {
        val VIN = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
            .getString(widget_VIN, "")
        return RemoteViews(context.packageName, getVehicle(VIN!!).layoutID)
    }

    override fun drawIcons(views: RemoteViews, carStatus: CarStatus) {
        // Door locks
        carStatus.vehiclestatus?.lockStatus?.value?.let { lockStatus ->
            views.setImageViewResource(
                R.id.lock_electric,
                if (lockStatus == "LOCKED") R.drawable.locked_icon_green else R.drawable.unlocked_icon_red
            )
            views.setImageViewResource(
                R.id.lock_gasoline,
                if (lockStatus == "LOCKED") R.drawable.locked_icon_green else R.drawable.unlocked_icon_red
            )
        }

        // Ignition and remote start
        if (carStatus.vehiclestatus?.remoteStartStatus?.value == 1) {
            views.setImageViewResource(R.id.ignition, R.drawable.ignition_icon_yellow)
        } else carStatus.vehiclestatus?.ignitionStatus?.value?.let { ignition ->
            views.setImageViewResource(
                R.id.ignition,
                if (ignition == "Off") R.drawable.ignition_icon_gray else R.drawable.ignition_icon_green
            )
        }

        // Motion alarm and deep sleep state
        if (carStatus.vehiclestatus?.deepSleepInProgress?.value ?: false) {
            views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_zzz_red)
        } else {
            carStatus.vehiclestatus?.alarm?.value?.let { alarm ->
                views.setImageViewResource(
                    R.id.alarm,
                    if (alarm == "NOTSET") R.drawable.bell_icon_red else R.drawable.bell_icon_green
                )
            } ?: run {
                views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_gray)
            }
        }
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int, info: InfoRepository
    ) {

        // Find which user is active.
        val userInfo = info.user
        if (userInfo.userId == "") return

        // Find the vehicle for this widget
        val vehicleInfo = getVehicleInfo(context, info, appWidgetId) ?: return
        val widget_VIN = Constants.VIN_KEY + appWidgetId
        val views = getWidgetView(context, widget_VIN)

        // Make sure the left side is visible depending on the widget width
        val appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId)
        onResize(appWidgetOptions, views)

        // Setup actions for specific widgets
        setCallbacks(context, views, appWidgetId)

        // Set background transparency
        setBackground(context, views)

        // If the vehicle image has been downloaded, update it
        val useImage = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.resources.getString(R.string.use_image_key), true)
        val bmp = getRandomImage(context, vehicleInfo.vin!!)
        if (useImage && bmp != null) {
            views.setImageViewBitmap(R.id.logo, bmp)
        } else {
            views.setImageViewResource(R.id.logo, getVehicle(vehicleInfo.vin).logoID)
        }

        // Display the vehicle's nickname
        views.setTextViewText(R.id.profile, vehicleInfo.nickname)
        //        views.setTextViewText(R.id.profile, "My Mach-E");
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)

        // If no status information, print something generic and return
        // TODO: also refresh the icons as if we're logged out?
        val carStatus = vehicleInfo.carStatus
        if (carStatus == null || carStatus.vehiclestatus == null) {
            views.setTextViewText(R.id.lastRefresh, "Unable to retrieve status information.")
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }
        val isICEOrHybrid = carStatus.isPropulsionICEOrHybrid(carStatus.propulsion)
        val isPHEV = carStatus.isPropulsionPHEV(carStatus.propulsion)
        views.setViewVisibility(R.id.lock_gasoline, if (isICEOrHybrid) View.VISIBLE else View.GONE)
        views.setViewVisibility(
            R.id.bottom_gasoline,
            if (isICEOrHybrid) View.VISIBLE else View.GONE
        )
        views.setViewVisibility(R.id.lock_electric, if (isICEOrHybrid) View.GONE else View.VISIBLE)
        views.setViewVisibility(
            R.id.bottom_electric,
            if (isICEOrHybrid) View.GONE else View.VISIBLE
        )
        views.setViewVisibility(R.id.plug, if (isICEOrHybrid) View.GONE else View.VISIBLE)
        setPHEVCallbacks(context, views, isPHEV, appWidgetId, "showGasoline")

        // Get conversion factors and descriptions for measurement units
        val units = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(
                context.resources.getString(R.string.units_key),
                context.resources.getString(R.string.units_mphpsi)
            )!!.toInt()
        val distanceConversion: Double
        val distanceUnits: String
        if (units == Constants.UNITS_MPHPSI) {
            distanceConversion = Constants.KMTOMILES
            distanceUnits = "miles"
        } else {
            distanceConversion = 1.0
            distanceUnits = "km"
        }
        val pressureConversion: Double
        val pressureUnits: String

        when (units) {
            Constants.UNITS_KPHPSI, Constants.UNITS_MPHPSI -> {
                pressureConversion = Constants.KPATOPSI
                pressureUnits = "psi"
            }
            Constants.UNITS_KPHBAR -> {
                pressureConversion = Constants.KPATOBAR
                pressureUnits = "bar"
            } else -> {
                pressureConversion = 1.0
                pressureUnits = "kPa"
            }
        }

        // Show last refresh, odometer
        val timeFormat =
            if (userInfo.country == "USA") Constants.LOCALTIMEFORMATUS else Constants.LOCALTIMEFORMAT
        drawLastRefresh(context, views, carStatus, timeFormat)
        drawOdometer(views, carStatus, distanceConversion, distanceUnits)

        // Ignition, alarm/sleep, plug icons
        drawIcons(views, carStatus)

        // Draw range and fuel/gas stuff
        val displayTime = true
        drawRangeFuel(
            context, views, carStatus, info, vehicleInfo,
            distanceConversion, distanceUnits, displayTime
        )

        // Current Odometer reading
//        val odometer = carStatus.odometer
//        if (odometer > 0) {
//            // FordPass truncates; go figure.
//            views.setTextViewText(
//                R.id.odometer,
//                MessageFormat.format(
//                    "Odo: {0} {1}",
//                    java.lang.Double.valueOf(odometer * distanceConversion).toInt(),
//                    distanceUnits
//                )
//            )
//        } else {
//            views.setTextViewText(R.id.odometer, "Odo: ---")
//        }
        views.setTextViewText(R.id.odometer,
            carStatus.vehiclestatus?.odometer?.value?.let {
                MessageFormat.format(
                    "Odo: {0} {1}",
                    java.lang.Double.valueOf(it * distanceConversion).toInt(),
                    distanceUnits
                )
            } ?: "Odo: ---"
        )

        // Tire pressures
        updateTire(
            context, views,
            carStatus.vehiclestatus?.tpms?.leftFrontTirePressure?.value,
            carStatus.vehiclestatus?.tpms?.leftFrontTireStatus?.value,
            pressureUnits, pressureConversion, R.id.lftire
        )
        updateTire(
            context, views,
            carStatus.vehiclestatus?.tpms?.rightFrontTirePressure?.value,
            carStatus.vehiclestatus?.tpms?.rightFrontTireStatus?.value,
            pressureUnits, pressureConversion, R.id.rftire
        )
        updateTire(
            context, views,
            carStatus.vehiclestatus?.tpms?.outerLeftRearTirePressure?.value,
            carStatus.vehiclestatus?.tpms?.outerLeftRearTireStatus?.value,
            pressureUnits, pressureConversion, R.id.lrtire
        )
        updateTire(
            context, views,
            carStatus.vehiclestatus?.tpms?.outerRightRearTirePressure?.value,
            carStatus.vehiclestatus?.tpms?.outerRightRearTireStatus?.value,
            pressureUnits, pressureConversion, R.id.rrtire
        )

        // Window statuses
        views.setImageViewResource(
            R.id.lt_ft_window,
            if (isWindowClosed(carStatus.vehiclestatus?.windowPosition?.driverWindowPosition?.value)) R.drawable.filler else R.drawable.icons8_left_front_window_down_red
        )
        views.setImageViewResource(
            R.id.rt_ft_window,
            if (isWindowClosed(carStatus.vehiclestatus?.windowPosition?.passWindowPosition?.value)) R.drawable.filler else R.drawable.icons8_right_front_window_down_red
        )
        views.setImageViewResource(
            R.id.lt_rr_window,
            if (isWindowClosed(carStatus.vehiclestatus?.windowPosition?.rearDriverWindowPos?.value)) R.drawable.filler else R.drawable.icons8_left_rear_window_down_red
        )
        views.setImageViewResource(
            R.id.rt_rr_window,
            if (isWindowClosed(carStatus.vehiclestatus?.windowPosition?.rearPassWindowPos?.value)) R.drawable.filler else R.drawable.icons8_right_rear_window_down_red
        )

        // Get the right images to use for this vehicle
        val vehicleImages = getVehicle(vehicleInfo.vin).verticalDrawables

        // See if we should guess vehicle color
        if (scanImageForColor(context, vehicleInfo)) {
            info.setVehicle(vehicleInfo)
        }

        // If vehicle is a Mach-E First Edition, show mirrors in body color
        if (isFirstEdition(context, vehicleInfo.vin!!)) {
            vehicleImages[Vehicle.BODY_SECONDARY] = R.drawable.mache_secondary_no_mirrors_vert
        }

        // Draw the vehicle image
        drawVehicleImage(context, views, carStatus, vehicleInfo.colorValue, null, vehicleImages)

//        views.setTextColor(R.id.ota_line2, context.getColor(R.color.white));

        // OTA status
//        drawOTAInfo(context, views, vehicleInfo, timeFormat);

        // Location
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.show_location_key), true)
        ) {
            views.setViewVisibility(R.id.location_container, View.VISIBLE)
            updateLocation(context, views, carStatus.vehiclestatus?.gps?.latitude, carStatus.vehiclestatus?.gps?.longitude)
        } else {
            views.setViewVisibility(R.id.location_container, View.GONE)
        }
        updateLinkedApps(context, views)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val info = getInfo(context)
            if (info.user.userId != "") {
                // There may be multiple widgets active, so update all of them
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, info)
                }
            } else {
                LogFile.d(
                    context,
                    MainActivity.CHANNEL_ID,
                    "CarStatusWidget_5x5.onUpdate(): no userinfo found"
                )
            }
        }
    }

    override fun onResize(newOptions: Bundle, views: RemoteViews) {
        val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        //        Toast.makeText(context, "width = " + minWidth, Toast.LENGTH_SHORT).show();
        if (minWidth < 250) {
            views.setViewVisibility(R.id.leftside, View.GONE)
        } else {
            views.setViewVisibility(R.id.leftside, View.VISIBLE)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        val widget_VIN = Constants.VIN_KEY + appWidgetId
        val VIN = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
            .getString(widget_VIN, "")
        if (VIN != "") {
            val views = getWidgetView(context, widget_VIN)
            onResize(newOptions, views)
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Handle the actions which don't require info about the vehicle or user
        val action = intent.action
        val appWidgetId = intent.getIntExtra(APPWIDGETID, -1)
        val widget_VIN = Constants.VIN_KEY + appWidgetId
        when (action) {
            PROFILE_CLICK -> changeProfile(context, widget_VIN)
            PHEVTOGGLE_CLICK -> {
                val mode = intent.getStringExtra("nextMode")
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val views = getWidgetView(context, widget_VIN)
                val nextMode: String
                if (mode == "showGasoline") {
                    nextMode = "showElectric"
                    views.setViewVisibility(R.id.bottom_electric, View.GONE)
                    views.setViewVisibility(R.id.bottom_gasoline, View.VISIBLE)
                } else {
                    nextMode = "showGasoline"
                    views.setViewVisibility(R.id.bottom_electric, View.VISIBLE)
                    views.setViewVisibility(R.id.bottom_gasoline, View.GONE)
                }
                setPHEVCallbacks(context, views, true, appWidgetId, nextMode)
                appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
            }
            else -> super.onReceive(context, intent)
        }
    }

}