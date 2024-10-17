package com.example.khughes.machewidget

import com.example.khughes.machewidget.Vehicle.Companion.getVehicle
import com.example.khughes.machewidget.VehicleColor.Companion.scanImageForColor
import android.widget.RemoteViews
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Implementation of App Widget functionality.
 */
class CarStatusWidget_1x5 : CarStatusWidget() {
    // Define actions for clicking on various icons, including the widget itself
    override fun setCallbacks(context: Context, views: RemoteViews, id: Int) {
        views.setOnClickPendingIntent(
            R.id.wireframe,
            getPendingSelfIntent(context, id, PROFILE_CLICK)
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

        // TODO: FordConnenect API doesn't provide window status
//        if (!isWindowClosed(carStatus.vehicle.vehicleStatus.windowPosition?.driverWindowPosition?.value)) {
//            whatsOpen.add(vehicleImages[Vehicle.LEFT_FRONT_WINDOW]!!)
//        }
//        if (!isWindowClosed(carStatus.vehicle.vehicleStatus.windowPosition?.passWindowPosition?.value)) {
//            whatsOpen.add(vehicleImages[Vehicle.RIGHT_FRONT_WINDOW]!!)
//        }
//        if (!isWindowClosed(carStatus.vehicle.vehicleStatus.windowPosition?.rearDriverWindowPos?.value)) {
//            whatsOpen.add( vehicleImages[Vehicle.LEFT_REAR_WINDOW]!!)
//        }
//        if (!isWindowClosed(carStatus.vehicle.vehicleStatus.windowPosition?.rearPassWindowPos?.value)) {
//            whatsOpen.add(vehicleImages[Vehicle.RIGHT_REAR_WINDOW]!!)
//        }
        super.drawVehicleImage( context, views, carStatus, vehicleColor, whatsOpen, vehicleImages )
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int, info: InfoRepository?
    ) {

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.widget_1x5)

        // Setup actions for specific widgets
        setCallbacks(context, views, appWidgetId)

        // Set background transparency
        setBackground(context, views)

        // Find which user is active.
        if(info == null) return

        // Find the vehicle for this widget
        val vehicleInfo = getVehicleInfo(context, info, appWidgetId) ?: return

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
        if(carStatus.vehicle.vehicleId == "") {
            return
        }

//        if (carStatus == null || carStatus.vehicle.vehicleStatus == null) {
//            views.setTextViewText(R.id.lastRefresh, "Unable to retrieve status information.")
//            appWidgetManager.updateAppWidget(appWidgetId, views)
//            return
//        }
        val isICEOrHybrid = carStatus.isPropulsionICEOrHybrid()
        val isPHEV = carStatus.isPropulsionPHEV()
        val isElectric = carStatus.isPropulsionElectric()

        views.setViewVisibility(
            R.id.lock_gasoline,
            if (isICEOrHybrid) View.VISIBLE else View.GONE
        )
        views.setViewVisibility(
            R.id.bottom_gasoline,
            if (isICEOrHybrid) View.VISIBLE else View.GONE
        )
        views.setViewVisibility(
            R.id.lock_electric,
            if (isICEOrHybrid) View.GONE else View.VISIBLE
        )
        views.setViewVisibility(
            R.id.bottom_electric,
            if (isICEOrHybrid) View.GONE else View.VISIBLE
        )
        views.setViewVisibility(R.id.plug, if (isICEOrHybrid) View.GONE else View.VISIBLE)
        setPHEVCallbacks(context, views, isPHEV, appWidgetId, "showGasoline")
        setElectricCallbacks(context, views, isElectric, appWidgetId)

        // Ingition, alarm/sleep, plug icons
        drawIcons(views, carStatus)

        // Draw range and fuel/gas stuff
        val displayTime = false
        drawRangeFuel(
            context, views, carStatus, info, vehicleInfo,
            distanceConversion, distanceUnits, displayTime
        )

        // Tire pressures
        updateTire(
            context, views,
            "", // carStatus.vehicle.vehicleStatus.tpms?.leftFrontTirePressure?.value,
            "", // carStatus.vehicle.vehicleStatus.tpms?.leftFrontTireStatus?.value,
            pressureUnits, pressureConversion, R.id.lt_ft_tire
        )
        updateTire(
            context, views,
            "", // carStatus.vehicle.vehicleStatus.tpms?.rightFrontTirePressure?.value,
            "", // carStatus.vehicle.vehicleStatus.tpms?.rightFrontTireStatus?.value,
            pressureUnits, pressureConversion, R.id.rt_ft_tire
        )
        updateTire(
            context, views,
            "", // carStatus.vehicle.vehicleStatus.tpms?.outerLeftRearTirePressure?.value,
            "", // carStatus.vehicle.vehicleStatus.tpms?.outerLeftRearTireStatus?.value,
            pressureUnits, pressureConversion, R.id.lt_rr_tire
        )
        updateTire(
            context, views,
            "", // carStatus.vehicle.vehicleStatus.tpms?.outerRightRearTirePressure?.value,
            "", // carStatus.vehicle.vehicleStatus.tpms?.outerRightRearTireStatus?.value,
            pressureUnits, pressureConversion, R.id.rt_rr_tire
        )

        // Get the right images to use for this vehicle
        val vehicleImages = getVehicle(vehicleInfo.modelId).horizontalDrawables

        // See if we should guess vehicle color
        if (scanImageForColor(context, vehicleInfo)) {
            info.setVehicle(vehicleInfo)
        }

        // TODO: implement once we can access the VIN
//        // If vehicle is a Mach-E First Edition, show mirrors in body color
//        if (isFirstEdition(context, vehicleInfo.carStatus.vehicle.vehicleId)) {
//            vehicleImages[Vehicle.BODY_SECONDARY] = R.drawable.mache_secondary_no_mirrors_horz
//        }

        // Draw the vehicle image
        drawVehicleImage(context, views, carStatus, vehicleInfo.colorValue, null, vehicleImages)

        // Instruct the widget manager to update the widget
        try {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        } catch (_: NullPointerException) {
        }
}

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        LogFile.defineContext(context)
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
        if (action == PHEVTOGGLE_CLICK) {
            val mode = intent.getStringExtra("nextMode")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val views = RemoteViews(context.packageName, R.layout.widget_1x5)
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
        } else {
            super.onReceive(context, intent)
        }
    }
}
