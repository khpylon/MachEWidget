package com.example.khughes.machewidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import android.icu.text.MessageFormat
import android.location.Address
import android.location.Geocoder
import android.os.*
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.CarStatus.CarStatus
import com.example.khughes.machewidget.Misc.Companion.elapsedMinutesToDescription
import com.example.khughes.machewidget.Misc.Companion.elapsedSecondsToDescription
import com.example.khughes.machewidget.Notifications.Companion.chargeComplete
import com.example.khughes.machewidget.ProfileManager.changeProfile
import com.example.khughes.machewidget.StatusReceiver.Companion.nextAlarm
import com.example.khughes.machewidget.VehicleColor.Companion.drawColoredVehicle
import kotlinx.coroutines.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt

open class CarStatusWidget : AppWidgetProvider() {

    open fun updateTire(
        context: Context, views: RemoteViews, pressure: String?, status: String?,
        units: String?, conversion: Double, id: Int
    ) {
        // Set the textview background color based on the status
        var tmpPressure: String
        val drawable: Int
        if (status != null && status != "Normal" && status != "Unknown") {
            drawable = R.drawable.pressure_oval_red_solid
            // Get the tire pressure and do any conversion necessary.
            if (pressure != null) {
                try {
                    val value = pressure.toDouble()
                    // Only display value if it's not ridiculous; after some OTA updates the
                    // raw value is "65533"
                    if (value < 2000) {
                        // If conversion is really small, show value in tenths
                        val pattern = if (conversion >= 0.1) "#" else "#.0"
                        tmpPressure = MessageFormat.format(
                            "{0}{1}", DecimalFormat(
                                pattern,  // "#.0",
                                DecimalFormatSymbols.getInstance(Locale.US)
                            ).format(value * conversion), units
                        )
                        views.setInt(id, "setBackgroundResource", R.drawable.pressure_oval)
                    } else {
                        tmpPressure = "N/A"
                    }
                } catch (e: NumberFormatException) {
                    LogFile.e(
                        context,
                        MainActivity.CHANNEL_ID,
                        "java.lang.NumberFormatException in CarStatusWidget.updateTire(): pressure = $pressure"
                    )
                    tmpPressure = "N/A"
                }
            } else {
                tmpPressure = "N/A"
            }
            views.setTextViewText(id, tmpPressure)
//            views.setTextViewTextSize(id,TypedValue.COMPLEX_UNIT_DIP, 12.0)
        } else {
            drawable = R.drawable.filler
            views.setTextViewText(id, "")
        }
        views.setInt(id, "setBackgroundResource", drawable)
    }

    private fun showAddress(
        views: RemoteViews,
        addresses: MutableList<Address>
    ) {
        // If an address was found, go with the first entry
        var streetName = PADDING
        var cityState = ""

        if (addresses.isNotEmpty()) {
            val address = addresses[0]

            // Street address and name
            if (address.subThoroughfare != null) {
                streetName += address.subThoroughfare + " "
            }
            if (address.thoroughfare != null) {
                streetName += address.thoroughfare
            }

            // Other locality info (state, province, etc)
            if (address.locality != null && address.adminArea != null) {
                var adminArea = address.adminArea
                if (states.containsKey(adminArea)) {
                    adminArea = states[adminArea]
                }
                cityState = PADDING + address.locality + ", " + adminArea
            }

            // If no street, move city/state up
            if (streetName == PADDING) {
                streetName = cityState
                cityState = ""
            }
        } else {
            streetName = PADDING + "N/A"
        }
        //        streetName = PADDING + "45500 Fremont Blvd";
        //        cityState = PADDING + "Fremont, CA";
        views.setTextViewText(R.id.location_line2, streetName)
        views.setTextViewText(R.id.location_line3, cityState)
    }

    protected fun updateLocation(
        context: Context?,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        views: RemoteViews,
        latitude: String?,
        longitude: String?
    ) {
        if (latitude != null && longitude != null) {
            views.setTextViewText(R.id.location_line1, "Location:")

            val mGeocoder = Geocoder(context!!, Locale.getDefault())
            val lat = latitude.toDouble()
            val lon = longitude.toDouble()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mGeocoder.getFromLocation(lat, lon, 1) { addresses ->
                    showAddress(views, addresses)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } else {
                try {
                    @Suppress("DEPRECATION") val addresses = mGeocoder.getFromLocation(lat, lon, 1)
                    showAddress(views, addresses as MutableList<Address>)
                } catch (_: Exception) {
                }
            }
        }
    }

    // Check if a window is open or closed.  Undefined defaults to closed.
    protected fun isWindowClosed(status: String?): Boolean {
        return (status == null || status.lowercase(Locale.getDefault())
            .replace("[^a-z\\d]".toRegex(), "").contains("fullyclosed")
                || status.lowercase(Locale.getDefault()).contains("undefined"))
    }

    // Check if a door is open or closed.  Undefined defaults to closed.
    private fun isDoorClosed(status: String?): Boolean {
        return status == null || status.lowercase(Locale.getDefault()).contains("closed") ||
                !status.lowercase(Locale.getDefault()).contains("ajar")
    }

    // Set background transparency
    protected fun setBackground(context: Context, views: RemoteViews) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val useTranparency =
            sharedPref.getBoolean(context.resources.getString(R.string.transp_bg_key), false)
        views.setInt(
            R.id.thewidget, "setBackgroundResource",
            if (useTranparency) R.color.transparent_black else R.color.black
        )
    }

    protected fun getVehicleInfo(
        context: Context,
        info: InfoRepository,
        appWidgetId: Int
    ): VehicleInfo? {
        // Find the VIN associated with this widget
        val widget_VIN = Constants.VIN_KEY + appWidgetId
        var VIN = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
            .getString(widget_VIN, null)

        // If there's a VIN but no vehicle, then essentially there is no VIN
        if (VIN != null && info.getVehicleByVIN(VIN).vin == "") {
            VIN = null
        }

        // if VIN is undefined, pick first VIN that we find
        if (VIN == null) {
            val vehicles = info.vehicles
            // No vehicles; hmmm....
            if (vehicles.isEmpty()) {
                return null
            }
            //  Look for an enabled vehicle with this owner
            for (vehicleInfo in vehicles) {
                if (vehicleInfo.isEnabled && vehicleInfo.userId == info.user.userId) {
                    VIN = vehicleInfo.vin
                    break
                }
            }
            // If we found something, save it.
            if (VIN != null) {
                context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit()
                    .putString(widget_VIN, VIN).commit()
            }
        }
        return info.getVehicleByVIN(VIN)
    }

    protected fun getPendingSelfIntent(context: Context?, id: Int, action: String?): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.putExtra(APPWIDGETID, id)
        intent.action = action
        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // Define actions for clicking on various icons, including the widget itself
    protected open fun setCallbacks(context: Context, views: RemoteViews, id: Int) {
        views.setOnClickPendingIntent(
            R.id.thewidget,
            getPendingSelfIntent(context, id, WIDGET_CLICK)
        )
        views.setOnClickPendingIntent(
            R.id.settings,
            getPendingSelfIntent(context, id, SETTINGS_CLICK)
        )
        views.setOnClickPendingIntent(
            R.id.lastRefresh,
            getPendingSelfIntent(context, id, REFRESH_CLICK)
        )

//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        boolean showAppLinks = sharedPref.getBoolean(context.getResources().getString(R.string.show_app_links_key), true);
//        views.setOnClickPendingIntent(R.id.leftappbutton, getPendingSelfIntent(context, id, showAppLinks ? LEFT_BUTTON_CLICK : WIDGET_CLICK));
//        views.setOnClickPendingIntent(R.id.rightappbutton, getPendingSelfIntent(context, id, showAppLinks ? RIGHT_BUTTON_CLICK : WIDGET_CLICK));
        val forceUpdates = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.resources.getString(R.string.user_forcedUpdate_key), false)
        views.setOnClickPendingIntent(
            R.id.refresh,
            getPendingSelfIntent(context, id, if (forceUpdates) UPDATE_CLICK else WIDGET_CLICK)
        )
        views.setViewVisibility(R.id.refresh, if (forceUpdates) View.VISIBLE else View.GONE)
        val enableCommands = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.resources.getString(R.string.enable_commands_key), false)
        views.setOnClickPendingIntent(
            R.id.lock_gasoline,
            getPendingSelfIntent(context, id, if (enableCommands) LOCK_CLICK else WIDGET_CLICK)
        )
        views.setOnClickPendingIntent(
            R.id.lock_electric,
            getPendingSelfIntent(context, id, if (enableCommands) LOCK_CLICK else WIDGET_CLICK)
        )
        views.setOnClickPendingIntent(
            R.id.ignition,
            getPendingSelfIntent(context, id, if (enableCommands) IGNITION_CLICK else WIDGET_CLICK)
        )
    }

    protected fun setPHEVCallbacks(
        context: Context?,
        views: RemoteViews,
        isPHEV: Boolean,
        id: Int,
        mode: String?
    ) {
        if (!isPHEV) {
            views.setOnClickPendingIntent(
                R.id.bottom_gasoline,
                getPendingSelfIntent(context, id, WIDGET_CLICK)
            )
            views.setOnClickPendingIntent(
                R.id.bottom_electric,
                getPendingSelfIntent(context, id, WIDGET_CLICK)
            )
        } else {
            val intent = Intent(context, javaClass)
            intent.putExtra(APPWIDGETID, id)
            intent.putExtra("nextMode", mode)
            intent.action = PHEVTOGGLE_CLICK
            views.setOnClickPendingIntent(
                R.id.bottom_gasoline,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            intent.action = PHEVTOGGLE_CLICK
            views.setOnClickPendingIntent(
                R.id.bottom_electric,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }

    protected fun setDieselCallbacks(
        context: Context?,
        views: RemoteViews,
        isDiesel: Boolean,
        id: Int,
        mode: String?
    ) {
        if (!isDiesel) {
            views.setOnClickPendingIntent(
                R.id.LVBVoltage,
                getPendingSelfIntent(context, id, WIDGET_CLICK)
            )
            views.setOnClickPendingIntent(
                R.id.DEFLevel,
                getPendingSelfIntent(context, id, WIDGET_CLICK)
            )
            views.setOnClickPendingIntent(
                R.id.DEFRange,
                getPendingSelfIntent(context, id, WIDGET_CLICK)
            )
        } else {
            val intent = Intent(context, javaClass)
            intent.putExtra(APPWIDGETID, id)
            intent.putExtra("nextMode", mode)
            intent.action = DIESELTOGGLE_CLICK
            views.setOnClickPendingIntent(
                R.id.LVBVoltage,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            intent.action = DIESELTOGGLE_CLICK
            views.setOnClickPendingIntent(
                R.id.DEFLevel,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            intent.action = DIESELTOGGLE_CLICK
            views.setOnClickPendingIntent(
                R.id.DEFRange,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }

    protected open fun drawIcons(views: RemoteViews, carStatus: CarStatus) {
        // Door locks
        carStatus.vehiclestatus.lockStatus?.value?.let { lockStatus ->
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
        if (carStatus.vehiclestatus.remoteStartStatus?.value == 1) {
            views.setImageViewResource(R.id.ignition, R.drawable.ignition_icon_yellow)
        } else carStatus.vehiclestatus.ignitionStatus?.value?.let { ignition ->
            views.setImageViewResource(
                R.id.ignition,
                if (ignition == "Off") R.drawable.ignition_icon_gray else R.drawable.ignition_icon_green
            )
        }

        // Motion alarm and deep sleep state
        if (carStatus.vehiclestatus.deepSleepInProgress?.value == true) {
            views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_zzz_red)
        } else {
            carStatus.vehiclestatus.alarm?.value?.let { alarm ->
                views.setImageViewResource(
                    R.id.alarm,
                    if (alarm == "NOTSET") R.drawable.bell_icon_red else R.drawable.bell_icon_green
                )
            } ?: run {
                views.setImageViewResource(R.id.alarm, R.drawable.bell_icon_gray)
            }
        }
    }

    protected fun drawRangeFuel(
        context: Context, views: RemoteViews, carStatus: CarStatus,
        info: InfoRepository, vehicleInfo: VehicleInfo,
        distanceConversion: Double, distanceUnits: String?,
        displayTime: Boolean
    ) {
        val isICEOrHybrid = carStatus.isPropulsionICEOrHybrid(carStatus.propulsion)
        val isPHEV = carStatus.isPropulsionPHEV(carStatus.propulsion)
        val isDiesel = carStatus.isPropulsionDiesel(carStatus.propulsion)
        var rangeMessage = "N/A"
        var chargeMessage = ""

        if (!isICEOrHybrid) {
            // Estimated range
            carStatus.vehiclestatus.elVehDTE?.value?.let {
                rangeMessage = MessageFormat.format(
                    "{0} {1}",
                    (it * distanceConversion).roundToInt(),
                    distanceUnits
                )
            }
            val messageLength = rangeMessage.length

            // Charging port
            views.setImageViewResource(
                R.id.plug,
                if (carStatus.vehiclestatus.plugStatus?.value == 1) R.drawable.plug_icon_green else R.drawable.plug_icon_gray
            )

            var rangeMessageSize = 1.0F
            // High-voltage battery
            if ((carStatus.vehiclestatus.plugStatus?.value ?: 0) == 1) {
                val chargeStatus = carStatus.vehiclestatus.chargingStatus?.value ?: ""
                when (chargeStatus) {
                    Constants.CHARGING_STATUS_NOT_READY -> views.setImageViewResource(
                        R.id.HVBIcon,
                        R.drawable.battery_icon_red
                    )

                    Constants.CHARGING_STATUS_CHARGING_AC, Constants.CHARGING_STATUS_CHARGING_DC -> views.setImageViewResource(
                        R.id.HVBIcon,
                        R.drawable.battery_charging
                    )

                    Constants.CHARGING_STATUS_TARGET_REACHED, Constants.CHARGING_STATUS_PRECONDITION,
                    Constants.CHARGING_STATUS_COMPLETE -> views.setImageViewResource(
                        R.id.HVBIcon,
                        R.drawable.battery_icon_charged_green
                    )

                    Constants.CHARGING_STATUS_PAUSED -> views.setImageViewResource(
                        R.id.HVBIcon,
                        R.drawable.battery_icon_yellow
                    )

                    Constants.CHARGING_SCHEDULED -> views.setImageViewResource(
                        R.id.HVBIcon,
                        R.drawable.battery_icon_blue
                    )

                    else -> views.setImageViewResource(
                        R.id.HVBIcon,
                        R.drawable.battery_icon_gray
                    )
                }

                // Add charging information is available, display it
                val queryCharging =
                    PreferenceManager.getDefaultSharedPreferences(context)
                        .getBoolean(
                            context.resources
                                .getString(R.string.check_charging_key), false
                        )
                if (queryCharging && (vehicleInfo.carStatus.vehiclestatus.plugStatus?.value
                        ?: 0) == 1
                    && vehicleInfo.carStatus.vehiclestatus.chargeEnergy > 0
                ) {
                    rangeMessage += MessageFormat.format(
                        "\n{0}kW rate, {1}kWh added",
                        DecimalFormat(
                            "#0.0",
                            DecimalFormatSymbols.getInstance(Locale.US)
                        ).format(vehicleInfo.carStatus.vehiclestatus.chargePower / 1000.0),
                        DecimalFormat(
                            "#0.0",
                            DecimalFormatSymbols.getInstance(Locale.US)
                        ).format(vehicleInfo.carStatus.vehiclestatus.chargeEnergy / 1000.0)
                    )
                    rangeMessageSize = 0.8F
                }

                // Normally there will be something from the GOM; if so, display this info with it
                if (chargeStatus == Constants.CHARGING_STATUS_TARGET_REACHED) {
                    chargeMessage = "- Target Reached"
                    if (vehicleInfo.lastChargeStatus != Constants.CHARGING_STATUS_TARGET_REACHED) {
                        chargeComplete(context)
                    }
                } else if (chargeStatus == Constants.CHARGING_STATUS_COMPLETE) {
                    chargeMessage = "- Completed"
                    if (vehicleInfo.lastChargeStatus != Constants.CHARGING_STATUS_COMPLETE) {
                        chargeComplete(context)
                    }
                } else if (chargeStatus == Constants.CHARGING_STATUS_PRECONDITION) {
                    chargeMessage = "- Preconditioning"
                } else {
//                    val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.US)
//                    val endChargeTime = Calendar.getInstance()
//                    try {
//                        endChargeTime.time =
//                            carStatus.vehiclestatus.chargeEndTime!!.value?.let { sdf.parse(it) } as Date
//                        val nowTime = Calendar.getInstance()
//                        var min = Duration.between(
//                            nowTime.toInstant(),
//                            endChargeTime.toInstant()
//                        ).seconds / 60
//                        if (min > 0) {
//                            chargeMessage = "- "
//                            val hours = min.toInt() / 60
//                            min %= 60
//                            if (hours > 0) {
//                                chargeMessage += "$hours hr"
//                                if (min > 0) {
//                                    chargeMessage += ", "
//                                }
//                            }
//                            if (min > 0) {
//                                chargeMessage += "$min min"
//                            }
//                            chargeMessage += " left"
//                        }
//                    } catch (e: ParseException) {
//                        LogFile.e(
//                            context,
//                            MainActivity.CHANNEL_ID,
//                            "exception in CarStatusWidget.updateAppWidget: ",
//                            e
//                        )
//                    }
                }

                // If status changed, save for future reference.
                if (vehicleInfo.lastChargeStatus != chargeStatus) {
                    vehicleInfo.lastChargeStatus = chargeStatus
                    info.setVehicle(vehicleInfo)
                }
            } else {
                views.setImageViewResource(R.id.HVBIcon, R.drawable.battery_icon_gray)
            }
            val text = SpannableString(rangeMessage)
            text.setSpan(RelativeSizeSpan(rangeMessageSize), messageLength, rangeMessage.length, 0)
            views.setTextViewText(R.id.GOM, text)


            // High-voltage battery charge levels
            carStatus.vehiclestatus.batteryFillLevel?.value?.let {

                views.setProgressBar(
                    R.id.HBVChargeProgress,
                    100,
                    min(100, (it + 0.5).roundToInt()),
                    false
                )
                views.setTextViewText(
                    R.id.HVBChargePercent,
                    MessageFormat.format(
                        "{0}%", DecimalFormat(
                            "#.#",  // "#.0",
                            DecimalFormatSymbols.getInstance(Locale.US)
                        ).format(it)
                    ) + if (displayTime) chargeMessage else ""
                )
            }
        }

        if (isICEOrHybrid || isPHEV) {
            // Estimated range
            var range = carStatus.vehiclestatus.fuel?.distanceToEmpty ?: Double.MAX_VALUE
            if (range != Double.MAX_VALUE) {
                vehicleInfo.lastDTE = range
                info.setVehicle(vehicleInfo)
            } else {
                range = vehicleInfo.lastDTE
            }
            rangeMessage = MessageFormat.format(
                "{0} {1}",
                (range * distanceConversion).roundToInt(),
                distanceUnits
            )
            views.setTextViewText(R.id.distanceToEmpty, rangeMessage)

            // Fuel tank level
            var fuelLevel = carStatus.vehiclestatus.fuel?.fuelLevel ?: Double.MAX_VALUE
            if (fuelLevel != Double.MAX_VALUE) {
                vehicleInfo.lastFuelLevel = fuelLevel
                info.setVehicle(vehicleInfo)
            } else {
                fuelLevel = vehicleInfo.lastFuelLevel
            }
            fuelLevel = min(fuelLevel, 100.0)
            views.setProgressBar(
                R.id.fuelLevelProgress,
                100,
                (fuelLevel + 0.5).roundToInt(),
                false
            )
            views.setTextViewText(
                R.id.fuelLevelPercent,
                MessageFormat.format(
                    "{0}%", DecimalFormat(
                        "#.0",  // "#.0",
                        DecimalFormatSymbols.getInstance(Locale.US)
                    ).format(fuelLevel)
                )
            )

            if (carStatus.vehiclestatus.fuel == null) {
                Toast.makeText(
                    context,
                    "carStatus.getvehiclestatus().getFuel() is null",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (carStatus.vehiclestatus.fuel?.distanceToEmpty == null) {
                    Toast.makeText(
                        context,
                        "carStatus.getvehiclestatus().getFuel().getDistanceToEmpty() is null",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (carStatus.vehiclestatus.fuel?.fuelLevel == null) {
                    Toast.makeText(
                        context,
                        "carStatus.getvehiclestatus().getFuel().getFuelLevel() is null",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // 12 volt battery status
        views.setViewVisibility(R.id.LVBLevelGreen, View.GONE)
        views.setViewVisibility(R.id.LVBLevelYellow, View.GONE)
        views.setViewVisibility(R.id.LVBLevelRed, View.GONE)
        carStatus.vehiclestatus.battery?.batteryStatusActual?.value?.let { LVBLevel ->
            val LVBStatus =
                carStatus.vehiclestatus.battery?.batteryHealth?.value ?: "STATUS_GOOD"
            val LVBPercent =
                carStatus.vehiclestatus.battery?.batteryStatusActual?.percentage ?: 0.0
//            views.setTextColor(
//                R.id.LVBVoltage,
//                context.getColor(if (LVBStatus == "STATUS_GOOD") R.color.white else R.color.red)
//            )
            if (LVBLevel > 0) {
                val displayLVB = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(context.resources.getString(R.string.lvb_display_key), "")
                val message: String
                if (displayLVB == "Graph") {
                    val id: Int
                    if (LVBPercent >= 80.0) {
                        id = R.id.LVBLevelGreen
                    } else if (LVBPercent >= 50.0) {
                        id = R.id.LVBLevelYellow
                    } else {
                        id = R.id.LVBLevelRed
                    }
                    views.setViewVisibility(id, View.VISIBLE)
                    message = MessageFormat.format( "{0,number,#.0}V", LVBLevel )
                } else if (displayLVB == "Volts") {
                    message = MessageFormat.format( "{0,number,#.0}V", LVBLevel )
                } else if (displayLVB == "SOC" ) {
                    message = MessageFormat.format( "{0}%", LVBPercent )
                } else {
                    message = MessageFormat.format( "{0,number,#.0}V ({1}%)", LVBLevel, LVBPercent )
                }
                views.setTextViewText( R.id.LVBVoltage, message )
            } else {
                views.setTextViewText(
                    R.id.LVBVoltage,
                    MessageFormat.format(
                        "LV Battery: {0}",
                        (if (LVBStatus == "STATUS_GOOD") "Good" else "Warning")
                    )
                )
            }
        } ?: run {
            views.setTextColor(R.id.LVBVoltage, context.getColor(R.color.white))
            views.setTextViewText(R.id.LVBVoltage, "LV Battery: N/A")
        }

        if (isDiesel) {
            // It appears as if default DEF range is in miles, as opposed to all other distance values
            @Suppress("NAME_SHADOWING") var distanceConversion = 1.0
            if (distanceUnits == "km") {
                distanceConversion = 1.0 / Constants.KMTOMILES
            }
            carStatus.vehiclestatus.diesel?.exhaustFluidLevel?.value?.let { fluidLevel ->
                val level = fluidLevel.toString().toDouble()
                views.setTextViewText(
                    R.id.DEFLevel,
                    MessageFormat.format("DEF Level: {0}%", level)
                )
            } ?: run {
                views.setTextViewText(R.id.DEFLevel, "DEF Level: N/A")
            }
            carStatus.vehiclestatus.diesel?.ureaRange?.value?.let { ureaRange ->
                val range = ureaRange.toString().toDouble()
                views.setTextViewText(
                    R.id.DEFRange,
                    MessageFormat.format("DEF Range: {0} {1}",
                        (range * distanceConversion).roundToInt(), distanceUnits)
                )
            } ?: run {
                views.setTextViewText(R.id.DEFRange, "DEF Range: N/A")
            }
        }
    }

    protected fun drawLastRefresh(
        context: Context,
        views: RemoteViews,
        carStatus: CarStatus,
        timeFormat: String?
    ) {
        // Fill in the last update time
        val lastUpdateTime = Calendar.getInstance()
        var sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        try {
            lastUpdateTime.time = carStatus.lastRefresh?.let { sdf.parse(it) } ?: Date(0)
        } catch (e: Exception) {
            LogFile.e(
                context,
                MainActivity.CHANNEL_ID,
                "exception in CarStatusWidget.updateAppWidget: ",
                e
            )
        }
        val currentTime = Calendar.getInstance()
        val minutes = (Duration.between(
            lastUpdateTime.toInstant(),
            currentTime.toInstant()
        ).seconds + 30) / 60
        LogFile.i(
            context,
            MainActivity.CHANNEL_ID,
            "updateAppWidget(): last vehicle update was $minutes minutes ago."
        )
        var refresh: String? = "Last refresh:\n  "
        val displayTime = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.resources.getString(R.string.last_refresh_time_key), false)
        if (displayTime) {
            sdf = SimpleDateFormat(timeFormat, Locale.ENGLISH)
            refresh += sdf.format(lastUpdateTime.time)
        } else {
            // less than 1 minute
            refresh += if (minutes < 1) {
                "just now"
            } else {
                elapsedMinutesToDescription(minutes) + " ago"
            }
        }
        views.setTextViewText(R.id.lastRefresh, refresh)
    }

    protected fun drawOdometer(
        views: RemoteViews,
        carStatus: CarStatus,
        distanceConversion: Double,
        distanceUnits: String?
    ) {
        views.setTextViewText(R.id.odometer,
            carStatus.vehiclestatus.odometer?.value?.let {
                MessageFormat.format(
                    "Odo: {0} {1}",
                    java.lang.Double.valueOf(it * distanceConversion).toInt(),
                    distanceUnits
                )
            } ?: "Odo: ---"
        )
    }

// OTA status
//    protected void drawOTAInfo(Context context, RemoteViews views, VehicleInfo vehicleInfo, String timeFormat) {
//        views.setViewVisibility(R.id.ota_container, View.GONE);
//        OTAStatus otaStatus = vehicleInfo.toOTAStatus();
//        boolean displayOTA = PreferenceManager.getDefaultSharedPreferences(context)
//                .getBoolean(context.getResources().getString(R.string.show_OTA_key), true) && vehicleInfo.isSupportsOTA();
//
//        if (displayOTA && otaStatus != null) {
//            // If the report doesn't say the vehicle DOESN'T support OTA, then try to display something
//            if (Misc.OTASupportCheck(vehicleInfo.getOtaAlertStatus())) {
//                views.setTextViewText(R.id.ota_line1, "OTA Status:");
//                String OTArefresh;
//                if (otaStatus.getFuseResponse() == null) {
//                    OTArefresh = "No OTA data";
//                } else {
//                    long lastOTATime = vehicleInfo.getLastOTATime();
//                    String currentUTCOTATime = otaStatus.getOTADateTime();
//                    if (currentUTCOTATime == null) {
//                        OTArefresh = "No date specified";
//                    } else {
//                        long currentOTATime = OTAViewActivity.convertDateToMillis(currentUTCOTATime);
//
//                        // If there's new information, display that data/time in a different color
//                        if (currentOTATime > lastOTATime) {
//                            // if OTA failed, show it in red (that means something bad)
//                            String OTAResult = otaStatus.getOTAAggregateStatus();
//                            if (OTAResult != null && OTAResult.equals("failure")) {
//                                views.setTextColor(R.id.ota_line2, context.getColor(R.color.red));
//                            } else {
//                                views.setTextColor(R.id.ota_line2, context.getColor(R.color.green));
//                            }
//                            OTArefresh = OTAViewActivity.convertMillisToDate(currentOTATime, timeFormat);
//                            String message;
//                            switch(otaStatus.getOTAAggregateStatus()) {
//                                case "request_delivery_queued":
//                                    message = "An update is ready for download to your vehicle.";
//                                    break;
//                                case "artifact_retrieval_in_progress":
//                                    message = "Your vehicle is downloading the update.";
//                                    break;
//                                case "installation_queued":
//                                    if(otaStatus.getOtaAlertStatus().equals("UPDATE REMINDER")) {
//                                        message = "Your vehicle has downloaded the update but requires your attention.";
//                                    } else {
//                                        message = "Your vehicle has downloaded the update; you should turn on the ignition.";
//                                    }
//                                    break;
//                                case "deploying":
//                                    message = "Your vehicle is installing the update.";
//                                    break;
//                                case "success":
//                                    message = "The update was successful.";
//                                    break;
//                                case "failure":
//                                    message = "The update was not successful.";
//                                    break;
//                                case "requested":
//                                default:
//                                    message = "New OTA information was found.";
//                                    break;
//                            }
//                            Notifications.newOTA(context, message);
//                        } else {
//                            views.setTextColor(R.id.ota_line2, context.getColor(R.color.white));
//                            OTArefresh = OTAViewActivity.convertMillisToDate(lastOTATime, timeFormat);
//                        }
//                    }
//                }
//                views.setTextViewText(R.id.ota_line2, PADDING + OTArefresh);
//            }
//        }
//    }

    private fun setAppBitmap(
        context: Context,
        views: RemoteViews,
        appPackageName: String?,
        id: Int
    ) {
        try {
            if (appPackageName != null) {
                val icon =
                    context.applicationContext.packageManager.getApplicationIcon(appPackageName)
                val size = icon.intrinsicWidth.coerceAtMost(96)
                val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bmp)
                icon.setBounds(0, 0, canvas.width, canvas.height)
                icon.draw(canvas)
                views.setImageViewBitmap(id, bmp)
            } else {
                views.setImageViewResource(id, R.drawable.x_gray)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            views.setImageViewResource(id, R.drawable.x_gray)
        }
    }

    open fun drawVehicleImage(
        context: Context,
        views: RemoteViews,
        carStatus: CarStatus,
        vehicleColor: Int,
        whatzOpen: MutableList<Int>?,
        vehicleImages: Map<String, Int>
    ) {
        // If we're not passed a list, create one
        val whatsOpen = whatzOpen ?: mutableListOf()

        // Find anything that's open
        if (!isDoorClosed(carStatus.vehiclestatus.doorStatus?.hoodDoor?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.HOOD]!!)
        }
        if (!isDoorClosed(carStatus.vehiclestatus.doorStatus?.tailgateDoor?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.TAILGATE]!!)
        }
        if (!isDoorClosed(carStatus.vehiclestatus.doorStatus?.driverDoor?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.LEFT_FRONT_DOOR]!!)
        }
        if (!isDoorClosed(carStatus.vehiclestatus.doorStatus?.passengerDoor?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.RIGHT_FRONT_DOOR]!!)
        }
        if (!isDoorClosed(carStatus.vehiclestatus.doorStatus?.leftRearDoor?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.LEFT_REAR_DOOR]!!)
        }
        if (!isDoorClosed(carStatus.vehiclestatus.doorStatus?.rightRearDoor?.value)) {
            whatsOpen.add(vehicleImages[Vehicle.RIGHT_REAR_DOOR]!!)
        }
        whatsOpen.removeAll(setOf<Any?>(null))

        // Determine the orientation of the image
        val icon = AppCompatResources.getDrawable(context, vehicleImages[Vehicle.WIREFRAME]!!)
        var width = icon!!.intrinsicWidth
        var height = icon.intrinsicHeight
        if (width > height) {
            width = 225
            height = 100
        } else {
            width = 100
            height = 225
        }
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // See if color image is enabled and defined.
        val useColor = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.resources.getString(R.string.use_colors_key), false)
        drawColoredVehicle(context, bmp, vehicleColor, whatsOpen, useColor, vehicleImages)
        views.setImageViewBitmap(R.id.wireframe, bmp)
    }

    protected fun updateLinkedApps(context: Context, views: RemoteViews) {
        val showAppLinks = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.resources.getString(R.string.show_app_links_key), true)
        if (showAppLinks) {
            setAppBitmap(context, views, StoredData(context).leftAppPackage, R.id.leftappbutton)
            setAppBitmap(context, views, StoredData(context).rightAppPackage, R.id.rightappbutton)
        } else {
            views.setImageViewResource(R.id.leftappbutton, R.drawable.filler)
            views.setImageViewResource(R.id.rightappbutton, R.drawable.filler)
        }
    }

    private fun getHandler(context: Context): Handler {
        return object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val result = msg.data.getString("action")
                if (result != null && result == NetworkCalls.COMMAND_SUCCESSFUL) {
                    nextAlarm(context, 2)
                }
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun remoteStart(context: Context, VIN: String) {
        NetworkCalls.remoteStart(getHandler(context), context, VIN)
    }

    private fun remoteStop(context: Context, VIN: String) {
        NetworkCalls.remoteStop(getHandler(context), context, VIN)
    }

    private fun lock(context: Context, VIN: String) {
        NetworkCalls.lockDoors(getHandler(context), context, VIN)
    }

    private fun unlock(context: Context, VIN: String) {
        NetworkCalls.unlockDoors(getHandler(context), context, VIN)
    }

    private fun forceUpdate(context: Context, VIN: String) {
        NetworkCalls.updateStatus(
            object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    msg.data.getString("action")?.let { result ->
                        if (result == NetworkCalls.COMMAND_SUCCESSFUL) {
                            nextAlarm(context, 2)
                        }
                    }
                }
            },
            context, VIN
        )
    }

    open fun onResize(newOptions: Bundle, views: RemoteViews) {
        val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        //        Toast.makeText(context, "width = " + minWidth, Toast.LENGTH_SHORT).show();
        if (minWidth < 250) {
            views.setViewVisibility(R.id.leftside, View.GONE)
        } else {
            views.setViewVisibility(R.id.leftside, View.VISIBLE)
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
        val widget_action = action + "_" + appWidgetId
        val widget_VIN = Constants.VIN_KEY + appWidgetId

        when (action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val man = AppWidgetManager.getInstance(context)
                val ids = man.getAppWidgetIds(ComponentName(context, this.javaClass))
                onUpdate(context, AppWidgetManager.getInstance(context), ids)
                return
            }

            WIDGET_CLICK -> {
                val newIntent = Intent(context, MainActivity::class.java)
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(newIntent)
                return
            }

            LEFT_BUTTON_CLICK -> {
                val appInfo = StoredData(context)
                val appPackageName = appInfo.leftAppPackage
                if (appPackageName != null) {
                    val pm = context.applicationContext.packageManager
                    val newIntent = pm.getLaunchIntentForPackage(appPackageName)
                    if (newIntent != null) {
                        context.startActivity(newIntent)
                    } else {
                        appInfo.leftAppPackage = null
                        updateWidget(context)
                        Toast.makeText(context, "App is no longer installed", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                return
            }

            RIGHT_BUTTON_CLICK -> {
                val appInfo = StoredData(context)
                val appPackageName = appInfo.rightAppPackage
                if (appPackageName != null) {
                    val pm = context.applicationContext.packageManager
                    val newIntent = pm.getLaunchIntentForPackage(appPackageName)
                    if (newIntent != null) {
                        context.startActivity(newIntent)
                    } else {
                        appInfo.rightAppPackage = null
                        updateWidget(context)
                        Toast.makeText(context, "App is no longer installed", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                return
            }

            REFRESH_CLICK -> {
                var clickCount =
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
                        .getInt(widget_action, 0)
                context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit()
                    .putInt(widget_action, ++clickCount).commit()
                if (clickCount == 1) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val info = getInfo(context)
                        delay(500)
                        clickCount =
                            context.getSharedPreferences(
                                Constants.WIDGET_FILE,
                                Context.MODE_PRIVATE
                            )
                                .getInt(widget_action, 0)
                        if (clickCount > 2) {
                            val VIN = context.getSharedPreferences(
                                Constants.WIDGET_FILE,
                                Context.MODE_PRIVATE
                            ).getString(widget_VIN, null)
                            val vehInfo = info.getVehicleByVIN(VIN)
                            val userInfo = info.user
                            val lastUpdateInMillis = vehInfo.lastUpdateTime
                            val timeFormat =
                                if (userInfo.country == "USA") Constants.LOCALTIMEFORMATUS else Constants.LOCALTIMEFORMAT
                            val lastUpdate =
                                OTAViewActivity.convertMillisToDate(lastUpdateInMillis, timeFormat)
                            Toast.makeText(
                                context,
                                "Last update at $lastUpdate",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val lastAlarmInMillis = StoredData(context).lastAlarmTime
                            val lastAlarm =
                                OTAViewActivity.convertMillisToDate(lastAlarmInMillis, timeFormat)
                            Toast.makeText(context, "Last alarm at $lastAlarm", Toast.LENGTH_SHORT)
                                .show()
                        } else if (clickCount > 1) {
                            changeProfile(context, widget_VIN)
                        } else {
                            val newIntent = Intent(context, MainActivity::class.java)
                            newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(newIntent)
                        }
                        context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
                            .edit()
                            .remove(widget_action).apply()
                    }
                } else if (clickCount > 3) {
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit()
                        .remove(widget_action).apply()
                }
                return

            }

            SETTINGS_CLICK -> {
                val newIntent = Intent(context, SettingsActivity::class.java)
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(newIntent)
                return
            }

            IGNITION_CLICK, LOCK_CLICK, UPDATE_CLICK -> {
                var clickCount =
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
                        .getInt(widget_action, 0)
                context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit()
                    .putInt(widget_action, ++clickCount).commit()
                if (clickCount == 1) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val info = getInfo(context)
                        delay(500)
                        clickCount =
                            context.getSharedPreferences(
                                Constants.WIDGET_FILE,
                                Context.MODE_PRIVATE
                            )
                                .getInt(widget_action, 0)
                        if (clickCount > 1) {
                            val VIN = context.getSharedPreferences(
                                Constants.WIDGET_FILE,
                                Context.MODE_PRIVATE
                            ).getString(widget_VIN, null)
                            val vehInfo = info.getVehicleByVIN(VIN)
                            val carStatus = vehInfo.carStatus
                            when (action) {
                                IGNITION_CLICK -> if (carStatus.vehiclestatus.ignitionStatus?.value == "Off") {
                                    carStatus.vehiclestatus.remoteStartStatus?.value?.let { status ->
                                        if (status == 0) {
                                            remoteStart(context, VIN!!)
                                        } else {
                                            remoteStop(context, VIN!!)
                                        }
                                    }
                                }

                                LOCK_CLICK -> carStatus.vehiclestatus.lockStatus?.value?.let {
                                    if (it == "LOCKED") {
                                        unlock(context, VIN!!)
                                    } else {
                                        lock(context, VIN!!)
                                    }
                                }

                                UPDATE_CLICK -> {
                                    // If user is undefined, don't do anything
                                    val user = info.user
                                    user.let {
                                        if (carStatus.vehiclestatus.battery?.batteryHealth?.value == "STATUS_GOOD") {
                                            val nowTime = Instant.now().toEpochMilli()
                                            val firstTime = vehInfo.initialForcedRefreshTime
                                            val lastTime = vehInfo.lastForcedRefreshTime
                                            var seconds = (nowTime - firstTime) / MILLIS

                                            // If it's been twelve hours since the initial refresh, reset the count
                                            if (seconds > TIMEOUT_INTERVAL) {
                                                vehInfo.forcedRefreshCount = 0
                                                info.setVehicle(vehInfo)
                                            }

                                            // Calculate how long since the last refresh
                                            seconds = (nowTime - lastTime) / MILLIS
                                            val count = vehInfo.forcedRefreshCount

                                            // The first three refreshes must have 2 minutes between them; the next
                                            // two refreshes must have 10 minutes
                                            if (count < FIRST_LIMIT && seconds > FIRST_INTERVAL || count < SECOND_LIMIT && seconds > SECOND_INTERVAL) {
                                                val timeout = user.expiresIn
                                                seconds = (timeout - nowTime) / MILLIS
                                                // If the access token has expired, or is about to, do a refresh first
                                                if (seconds < 30) {
                                                    Toast.makeText(
                                                        context,
                                                        "The token is being refreshed; this may take a minute.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    nextAlarm(context, 2)

                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        delay(15 * MILLIS)
                                                        forceUpdate(context, VIN!!)
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Forcing a refresh; this may take 30 seconds.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    forceUpdate(context, VIN!!)
                                                }
                                            } else if (count < FIRST_LIMIT) {
                                                Toast.makeText(
                                                    context,
                                                    "Cannot force update for another " + elapsedSecondsToDescription(
                                                        2 * 60 - seconds
                                                    ) + ".",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else if (count < SECOND_LIMIT) {
                                                Toast.makeText(
                                                    context,
                                                    "Cannot force update for another " + elapsedSecondsToDescription(
                                                        10 * 60 - seconds
                                                    ) + "",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                val remainingMinutes =
                                                    ((firstTime - nowTime) / MILLIS + TIMEOUT_INTERVAL) / SECONDS
                                                Toast.makeText(
                                                    context,
                                                    "Too many forced updates; feature disabled for " +
                                                            elapsedMinutesToDescription(
                                                                remainingMinutes
                                                            ) + ".",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "The LVB status is not good.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                        context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
                            .edit()
                            .putInt(widget_action, 0).commit()
                    }
                } else if (clickCount > 3) {
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit()
                        .remove(widget_action).apply()
                }
                return
            }

            else -> {
                super.onReceive(context, intent)
            }
        }
    }

    suspend fun getInfo(context: Context): InfoRepository =
        coroutineScope {
            withContext(Dispatchers.IO) {
                InfoRepository(context)
            }
        }


    companion object {
        const val PROFILE_CLICK = "Profile"
        const val WIDGET_CLICK = "Widget"
        const val SETTINGS_CLICK = "SettingsButton"
        const val LEFT_BUTTON_CLICK = "FordPassButton"
        const val RIGHT_BUTTON_CLICK = "ChargerButton"
        const val IGNITION_CLICK = "IgnitionButton"
        const val LOCK_CLICK = "LockButton"
        const val REFRESH_CLICK = "Refresh"
        const val PHEVTOGGLE_CLICK = "PHEVToggle"
        const val DIESELTOGGLE_CLICK = "DieselToggle"
        const val UPDATE_CLICK = "ForceUpdate"
        const val APPWIDGETID = "appWidgetId"
        const val PADDING = "   "

        // Mapping from long state/territory names to abbreviations
        private var states = mutableMapOf(
            "Alaska" to "AK",
            "Alaska" to "AK",
            "Alberta" to "AB",
            "American Samoa" to "AS",
            "Arizona" to "AZ",
            "Arkansas" to "AR",
            "Armed Forces (AE)" to "AE",
            "Armed Forces Americas" to "AA",
            "Armed Forces Pacific" to "AP",
            "British Columbia" to "BC",
            "California" to "CA",
            "Colorado" to "CO",
            "Connecticut" to "CT",
            "Delaware" to "DE",
            "District Of Columbia" to "DC",
            "Florida" to "FL",
            "Georgia" to "GA",
            "Guam" to "GU",
            "Hawaii" to "HI",
            "Idaho" to "ID",
            "Illinois" to "IL",
            "Indiana" to "IN",
            "Iowa" to "IA",
            "Kansas" to "KS",
            "Kentucky" to "KY",
            "Louisiana" to "LA",
            "Maine" to "ME",
            "Manitoba" to "MB",
            "Maryland" to "MD",
            "Massachusetts" to "MA",
            "Michigan" to "MI",
            "Minnesota" to "MN",
            "Mississippi" to "MS",
            "Missouri" to "MO",
            "Montana" to "MT",
            "Nebraska" to "NE",
            "Nevada" to "NV",
            "New Brunswick" to "NB",
            "New Hampshire" to "NH",
            "New Jersey" to "NJ",
            "New Mexico" to "NM",
            "New York" to "NY",
            "Newfoundland" to "NF",
            "North Carolina" to "NC",
            "North Dakota" to "ND",
            "Northwest Territories" to "NT",
            "Nova Scotia" to "NS",
            "Nunavut" to "NU",
            "Ohio" to "OH",
            "Oklahoma" to "OK",
            "Ontario" to "ON",
            "Oregon" to "OR",
            "Pennsylvania" to "PA",
            "Prince Edward Island" to "PE",
            "Puerto Rico" to "PR",
            "Quebec" to "QC",
            "Rhode Island" to "RI",
            "Saskatchewan" to "SK",
            "South Carolina" to "SC",
            "South Dakota" to "SD",
            "Tennessee" to "TN",
            "Texas" to "TX",
            "Utah" to "UT",
            "Vermont" to "VT",
            "Virgin Islands" to "VI",
            "Virginia" to "VA",
            "Washington" to "WA",
            "West Virginia" to "WV",
            "Wisconsin" to "WI",
            "Wyoming" to "WY",
            "Yukon Territory" to "YT"
        )

        private const val MILLIS: Long = 1000
        private const val SECONDS: Long = 60
        private const val MINUTES: Long = 60
        private const val FIRST_LIMIT = 3
        private const val FIRST_INTERVAL = 2 * SECONDS
        private const val SECOND_LIMIT = 5
        private const val SECOND_INTERVAL = 10 * SECONDS
        private const val TIMEOUT_INTERVAL = 24 * MINUTES * SECONDS

        @JvmStatic
        fun updateWidget(context: Context) {
            val updateIntent = Intent()
            updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            context.sendBroadcast(updateIntent)
        }
    }
}
