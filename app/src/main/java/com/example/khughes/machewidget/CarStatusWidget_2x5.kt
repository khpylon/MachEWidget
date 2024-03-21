package com.example.khughes.machewidget

import com.example.khughes.machewidget.Vehicle.Companion.getVehicle
import com.example.khughes.machewidget.VehicleColor.Companion.scanImageForColor
import com.example.khughes.machewidget.VehicleColor.Companion.isFirstEdition
import android.widget.RemoteViews
import com.example.khughes.machewidget.CarStatus.CarStatus
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.view.View
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*

/**
 * Implementation of App Widget functionality.
 */
class CarStatusWidget_2x5 : CarStatusWidget() {

    // Define actions for clicking on various icons, including the widget itself
    override fun setCallbacks(context: Context, views: RemoteViews, id: Int) {
        views.setOnClickPendingIntent(
            R.id.wireframe,
            getPendingSelfIntent(context, id, PROFILE_CLICK)
        )
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

    override fun drawVehicleImage(
        context: Context,
        views: RemoteViews,
        carStatus: CarStatus,
        vehicleColor: Int,
        whatzOpen: MutableList<Int>?,
        vehicleImages: Map<String, Int>
    ) {
        val whatsOpen = whatzOpen ?: mutableListOf()

        if (!isWindowClosed(carStatus.vehiclestatus.windowPosition?.driverWindowPosition?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.LEFT_FRONT_WINDOW]!!)
        }
        if (!isWindowClosed(carStatus.vehiclestatus.windowPosition?.passWindowPosition?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.RIGHT_FRONT_WINDOW]!!)
        }
        if (!isWindowClosed(carStatus.vehiclestatus.windowPosition?.rearDriverWindowPos?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.LEFT_REAR_WINDOW]!!)
        }
        if (!isWindowClosed(carStatus.vehiclestatus.windowPosition?.rearPassWindowPos?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.RIGHT_REAR_WINDOW]!!)
        }
        super.drawVehicleImage(context, views, carStatus, vehicleColor, whatsOpen, vehicleImages)
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int, info: InfoRepository?
    ) {

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.widget_2x5)

        // Setup actions for specific widgets
        setCallbacks(context, views, appWidgetId)

        // Set background transparency
        setBackground(context, views)

        // Find which user is active.
        val userInfo = info!!.user

        // Find the vehicle for this widget
        val vehicleInfo = getVehicleInfo(context, info, appWidgetId) ?: return
        views.setTextViewText(R.id.profile, vehicleInfo.nickname)
        //        views.setTextViewText(R.id.profile, "My Mach-E");

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
            }
            else -> {
                pressureConversion = 1.0
                pressureUnits = "kPa"
            }
        }

        val carStatus = vehicleInfo.carStatus
//        if (carStatus == null || carStatus.vehiclestatus == null) {
//            views.setTextViewText(R.id.lastRefresh, "Unable to retrieve status information.")
//            appWidgetManager.updateAppWidget(appWidgetId, views)
//            return
//        }
        val isICEOrHybrid = carStatus.isPropulsionICEOrHybrid(carStatus.propulsion)
        val isPHEV = carStatus.isPropulsionPHEV(carStatus.propulsion)
        val isDiesel = carStatus.isPropulsionDiesel(carStatus.propulsion)
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
        setDieselCallbacks(context, views, isDiesel, appWidgetId, "showDEFLevel")

        // Show last refresh, odometer, OTA status
        val timeFormat =
            if (userInfo.country == "USA") Constants.LOCALTIMEFORMATUS else Constants.LOCALTIMEFORMAT
        drawLastRefresh(context, views, carStatus, timeFormat)
        drawOdometer(context, views, carStatus, distanceConversion, distanceUnits)
        //        drawOTAInfo(context, views, vehicleInfo, timeFormat);

        // Location
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.show_location_key), true)
        ) {
            views.setViewVisibility(R.id.location_container, View.VISIBLE)
            updateLocation(
                context,
                appWidgetManager,
                appWidgetId,
                views,
                carStatus.vehiclestatus.gps?.latitude,
                carStatus.vehiclestatus.gps?.longitude
            )
        } else {
            views.setViewVisibility(R.id.location_container, View.GONE)
        }

        // Ignition, alarm/sleep, plug icons
        drawIcons(views, carStatus)

        // Draw range and fuel/gas stuff
        val displayTime = true
        drawRangeFuel(
            context, views, carStatus, info, vehicleInfo,
            distanceConversion, distanceUnits, displayTime
        )

        // Tire pressures
        updateTire(
            context, views,
            carStatus.vehiclestatus.tpms?.leftFrontTirePressure?.value,
            carStatus.vehiclestatus.tpms?.leftFrontTireStatus?.value,
            pressureUnits, pressureConversion, R.id.lt_ft_tire
        )
        updateTire(
            context, views,
            carStatus.vehiclestatus.tpms?.rightFrontTirePressure?.value,
            carStatus.vehiclestatus.tpms?.rightFrontTireStatus?.value,
            pressureUnits, pressureConversion, R.id.rt_ft_tire
        )
        updateTire(
            context, views,
            carStatus.vehiclestatus.tpms?.outerLeftRearTirePressure?.value,
            carStatus.vehiclestatus.tpms?.outerLeftRearTireStatus?.value,
            pressureUnits, pressureConversion, R.id.lt_rr_tire
        )
        updateTire(
            context, views,
            carStatus.vehiclestatus.tpms?.outerRightRearTirePressure?.value,
            carStatus.vehiclestatus.tpms?.outerRightRearTireStatus?.value,
            pressureUnits, pressureConversion, R.id.rt_rr_tire
        )

        // Get the right images to use for this vehicle
        val vehicleImages = getVehicle(vehicleInfo.vin).horizontalDrawables

        // See if we should guess vehicle color
        if (scanImageForColor(context, vehicleInfo)) {
            info.setVehicle(vehicleInfo)
        }

        // If vehicle is a Mach-E First Edition, show mirrors in body color
        if (isFirstEdition(context, vehicleInfo.vin!!)) {
            vehicleImages[Vehicle.BODY_SECONDARY] = R.drawable.mache_secondary_no_mirrors_horz
        }

        // Draw the vehicle image
        drawVehicleImage(context, views, carStatus, vehicleInfo.colorValue, null, vehicleImages)
        updateLinkedApps(context, views)

        // Instruct the widget manager to update the widget
        try {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        } catch (_: NullPointerException) {
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_2x5)
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val info = getInfo(context)
            // There may be multiple widgets active, so update all of them
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, info)
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            PHEVTOGGLE_CLICK -> {
                val mode = intent.getStringExtra("nextMode")
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val views = RemoteViews(context.packageName, R.layout.widget_2x5)
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
                val appWidgetId = intent.getIntExtra(APPWIDGETID, -1)
                setPHEVCallbacks(context, views, true, appWidgetId, nextMode)
                appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
            }
            DIESELTOGGLE_CLICK -> {
                val mode = intent.getStringExtra("nextMode")
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val views = RemoteViews(context.packageName, R.layout.widget_2x5)
                val nextMode: String
                when (mode) {
                    "showDEFLevel" -> {
                        nextMode = "showDEFRange"
                        views.setViewVisibility(R.id.LVBDisplay, View.GONE)
                        views.setViewVisibility(R.id.DEFLevel, View.VISIBLE)
                        views.setViewVisibility(R.id.DEFRange, View.GONE)
                    }
                    "showDEFRange" -> {
                        nextMode = "showLVBVoltage"
                        views.setViewVisibility(R.id.LVBDisplay, View.GONE)
                        views.setViewVisibility(R.id.DEFLevel, View.GONE)
                        views.setViewVisibility(R.id.DEFRange, View.VISIBLE)
                    }
                    else -> {
                        nextMode = "showDEFLevel"
                        views.setViewVisibility(R.id.LVBDisplay, View.VISIBLE)
                        views.setViewVisibility(R.id.DEFLevel, View.GONE)
                        views.setViewVisibility(R.id.DEFRange, View.GONE)
                    }
                }
                val appWidgetId = intent.getIntExtra(APPWIDGETID, -1)
                setDieselCallbacks(context, views, true, appWidgetId, nextMode)
                appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
            }
            else -> {
                super.onReceive(context, intent)
            }
        }
    }
}