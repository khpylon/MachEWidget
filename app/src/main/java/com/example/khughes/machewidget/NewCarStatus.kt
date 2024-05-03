package com.example.khughes.machewidget

import com.example.khughes.machewidget.CarStatus.Battery
import com.example.khughes.machewidget.CarStatus.CarStatus
import com.example.khughes.machewidget.CarStatus.DieselSystemStatus
import com.example.khughes.machewidget.CarStatus.Vehiclestatus
import com.example.khughes.machewidget.CarStatus.DoorStatus
import com.example.khughes.machewidget.CarStatus.TPMS
import com.example.khughes.machewidget.CarStatus.WindowPosition
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class NewCarStatus(
    val updateTime: String,

    @SerializedName("vehicleId")
    val vehicleID: String,

    val vin: String,
    val metrics: CarStatusMetrics
) {
    companion object {
        @JvmStatic
        fun getCarStatus(status: NewCarStatus): CarStatus {
            val metrics = status.metrics
            val carStatus = CarStatus()
            val vehicleStatus = carStatus.vehiclestatus

            val updateTime = status.updateTime.replace("\\.[0-9]*Z".toRegex(), "Z")
            vehicleStatus.vin = status.vin
            vehicleStatus.lastRefresh = updateTime
            vehicleStatus.lastModifiedDate = updateTime

            // Alarm
            val alarm = Vehiclestatus.Alarm()
            alarm.value = metrics.alarmStatus?.value
            vehicleStatus.alarm = alarm // "ARMED" or "DISARMED"

            // LVB battery info
            // TODO: these may need to hold null instead of a value
            val batteryStatusActual = Battery.BatteryStatusActual()
            batteryStatusActual.value = metrics.batteryVoltage?.value ?: 0.0
            batteryStatusActual.percentage = metrics.batteryStateOfCharge?.value ?: 0.0
            val batteryHealth = Battery.BatteryHealth()
            batteryHealth.value =
                if (batteryStatusActual.percentage > 50.0) "STATUS_GOOD" else "STATUS_LOW"

            val battery = Battery()
            battery.batteryStatusActual = batteryStatusActual
            battery.batteryHealth = batteryHealth
            vehicleStatus.battery = battery

            // HVB battery info
            val batteryFillLevel = Vehiclestatus.BatteryFillLevel()
            batteryFillLevel.value = metrics.xevBatteryStateOfCharge?.value
            vehicleStatus.batteryFillLevel = batteryFillLevel

            // Estimated charge finishing time
            val chargeEndTime = Vehiclestatus.ChargeEndTime()
            // TODO: what value goes here: derived from metrics.xevBatteryTimeToFullCharge.value (Double)
            // TODO: metrics.xevBatteryTimeToFullCharge.value appears to be number of minutes until target reached
            // TODO: metrics.evBatteryTimeToFullCharge.value may also
            chargeEndTime.value = ""
            vehicleStatus.chargeEndTime = chargeEndTime

            // Charging plug status
            metrics.xevPlugChargerStatus?.let {
                val plugStatus = Vehiclestatus.PlugStatus()
                plugStatus.value =
                    if (it.value == "CONNECTED") 1 else 0
                vehicleStatus.plugStatus = plugStatus
            }

            // .xevBatteryChargeDisplayStatus
            //   unplugged -> "value":"NOT_READY"
            //   plugged in, charging -> "value": "IN_PROGRESS", "xevChargerPowerType": "AC", "xevBatteryType": "HIGH_VOLTAGE"
            //   plugged in, not charging -> "value": "SCHEDULED", "xevChargeStatusOrigin": "IN_VEHICLE"

            // Status of charge and battery energy remaining
            metrics.xevBatteryChargeDisplayStatus?.let {
                val chargingStatus = Vehiclestatus.ChargingStatus()
                // TODO: find out the other cases
                when (it.value) {
                    Constants.CHARGING_STATUS_IN_PROGRESS -> {
                        chargingStatus.value = Constants.CHARGING_STATUS_IN_PROGRESS
                        metrics.xevBatteryChargeDisplayStatus.xevChargerPowerType?.let {type ->
                            if (type.contains("AC") ) {
                                chargingStatus.value = Constants.CHARGING_STATUS_CHARGING_AC
                            } else if (type.contains("DC") ) {
                                chargingStatus.value = Constants.CHARGING_STATUS_CHARGING_DC
                            }
                        }
                    }
                    Constants.CHARGING_SCHEDULED -> {
                        chargingStatus.value = Constants.CHARGING_SCHEDULED
                    }
                    Constants.CHARGING_STATUS_COMPLETE -> {
                        chargingStatus.value = Constants.CHARGING_STATUS_COMPLETE
                    }
                    else -> {
                        chargingStatus.value = Constants.CHARGING_STATUS_NOT_READY
                    }
                }
                vehicleStatus.chargingStatus = chargingStatus
                vehicleStatus.xevBatteryEnergyRemaining = metrics.xevBatteryEnergyRemaining?.value ?: 0.0
                vehicleStatus.xevBatteryTemperature = metrics.xevBatteryTemperature?.value ?: 0.0
                vehicleStatus.xevBatteryVoltage = metrics.xevBatteryVoltage?.value ?: 0.0
                vehicleStatus.pluginTime = ""
            }

            // TODO: what value goes here
            val deepSleepInProgress = Vehiclestatus.DeepSleepInProgress()
            deepSleepInProgress.value = false
            vehicleStatus.deepSleepInProgress = deepSleepInProgress

            // EV battery range estimate
            metrics.xevBatteryRange?.let {
                val elVehDTE = Vehiclestatus.ElVehDTE()
                elVehDTE.value = it.value
                vehicleStatus.elVehDTE = elVehDTE
            }

            // GPS location
            val gps = Vehiclestatus.Gps()
            gps.latitude = metrics.position.value.location.lat.toString()
            gps.longitude = metrics.position.value.location.lon.toString()
            vehicleStatus.gps = gps

            // Ignition...
            val ignitionStatus = Vehiclestatus.IgnitionStatus()
            ignitionStatus.value =
                if (metrics.ignitionStatus.value == "OFF") "Off" else "Run"  // "OFF" or "ON"
            vehicleStatus.ignitionStatus = ignitionStatus

            // Odometer
            val odometer = Vehiclestatus.Odometer()
            odometer.value = metrics.odometer.value
            vehicleStatus.odometer = odometer

            // Remote start setting
            val remoteStartStatus = Vehiclestatus.RemoteStartStatus()
            // TODO - verify this is correct
            remoteStartStatus.value = if (metrics.remoteStartCountdownTimer.value > 0) 1 else 0
            vehicleStatus.remoteStartStatus = remoteStartStatus

            val doorStatus = DoorStatus()
            doorStatus.driverDoor = DoorStatus.DriverDoor()
            doorStatus.driverDoor!!.value = "CLOSED"
            doorStatus.passengerDoor = DoorStatus.PassengerDoor()
            doorStatus.passengerDoor!!.value = "CLOSED"
            doorStatus.leftRearDoor = DoorStatus.LeftRearDoor()
            doorStatus.leftRearDoor!!.value = "CLOSED"
            doorStatus.rightRearDoor = DoorStatus.RightRearDoor()
            doorStatus.rightRearDoor!!.value = "CLOSED"
            doorStatus.hoodDoor = DoorStatus.HoodDoor()
            doorStatus.hoodDoor!!.value = "CLOSED"
            doorStatus.tailgateDoor = DoorStatus.TailgateDoor()
            doorStatus.tailgateDoor!!.value = "CLOSED"
            for (door in metrics.doorStatus) {
                if (door.vehicleDoor == "TAILGATE") {
                    doorStatus.tailgateDoor!!.value = door.value
                } else if (door.vehicleDoor == "REAR_LEFT") {
                    doorStatus.leftRearDoor!!.value = door.value
                } else if (door.vehicleDoor == "REAR_RIGHT") {
                    doorStatus.rightRearDoor!!.value = door.value
                } else if (door.vehicleDoor == "UNSPECIFIED_FRONT") {
                    if (door.vehicleSide == "DRIVER") {
                        doorStatus.driverDoor!!.value = door.value
                    } else {
                        doorStatus.passengerDoor!!.value = door.value
                    }
                }
            }
            // For whatever reason, the hood is its own status
            doorStatus.hoodDoor!!.value = metrics.hoodStatus.value
            vehicleStatus.doorStatus = doorStatus

            val windowPosition = WindowPosition()
            windowPosition.driverWindowPosition = WindowPosition.DriverWindowPosition()
            windowPosition.passWindowPosition = WindowPosition.PassWindowPosition()
            windowPosition.rearDriverWindowPos = WindowPosition.RearDriverWindowPos()
            windowPosition.rearPassWindowPos = WindowPosition.RearPassWindowPos()
            metrics.windowStatus?.let {status ->
                for (window in status) {
                    val state = if (window.value.doubleRange.lowerBound > 0.0) "open" else "fullyclosed"
                    if (window.vehicleWindow == "UNSPECIFIED_FRONT") {
                        if (window.vehicleSide == "DRIVER") {
                            windowPosition.driverWindowPosition!!.value = state
                        } else {
                            windowPosition.passWindowPosition!!.value = state
                        }
                    } else if (window.vehicleWindow == "UNSPECIFIED_REAR") {
                        if (window.vehicleSide == "DRIVER") {
                            windowPosition.rearDriverWindowPos!!.value = state
                        } else {
                            windowPosition.rearPassWindowPos!!.value = state
                        }
                    }
                }
            }
            vehicleStatus.windowPosition = windowPosition

            val tpms = TPMS()
            metrics.tirePressure?.let {
                tpms.leftFrontTirePressure = TPMS.LeftFrontTirePressure()
                tpms.rightFrontTirePressure = TPMS.RightFrontTirePressure()
                tpms.outerLeftRearTirePressure = TPMS.OuterLeftRearTirePressure()
                tpms.outerRightRearTirePressure = TPMS.OuterRightRearTirePressure()
                for (tire in metrics.tirePressure) {
                    val pressure = tire.value.toString()
                    when (tire.vehicleWheel) {
                        "FRONT_LEFT" -> {
                            tpms.leftFrontTirePressure!!.value = pressure
                        }
                        "FRONT_RIGHT" -> {
                            tpms.rightFrontTirePressure!!.value = pressure
                        }
                        "REAR_LEFT" -> {
                            tpms.outerLeftRearTirePressure!!.value = pressure
                        }
                        "REAR_RIGHT" -> {
                            tpms.outerRightRearTirePressure!!.value = pressure
                        }
                    }
                }
            }

            metrics.tirePressureStatus?.let {
                tpms.leftFrontTireStatus = TPMS.LeftFrontTireStatus()
                tpms.rightFrontTireStatus = TPMS.RightFrontTireStatus()
                tpms.outerLeftRearTireStatus = TPMS.OuterLeftRearTireStatus()
                tpms.outerRightRearTireStatus = TPMS.OuterRightRearTireStatus()
                for (tire in metrics.tirePressureStatus) {
                    val status = if (tire.value == "NORMAL") "Normal" else "Low"
                    when (tire.vehicleWheel) {
                        "FRONT_LEFT" -> {
                            tpms.leftFrontTireStatus!!.value = status
                        }
                        "FRONT_RIGHT" -> {
                            tpms.rightFrontTireStatus!!.value = status
                        }
                        "REAR_LEFT" -> {
                            tpms.outerLeftRearTireStatus!!.value = status
                        }
                        "REAR_RIGHT" -> {
                            tpms.outerRightRearTireStatus!!.value = status
                        }
                    }
                }
            }
            vehicleStatus.tpms = tpms

            // Doors locked status
            val lockStatus = Vehiclestatus.LockStatus()
            metrics.doorLockStatus?.let {doors ->
                for (lock in doors) {
                    if (lock.vehicleDoor == "ALL_DOORS") {
                        lockStatus.value = lock.value
                    }
                }
            }
            vehicleStatus.lockStatus = lockStatus

            // ICE/Hybrid/PHEV fuel level
            metrics.fuelLevel?.value?.let {
                val fuel = Vehiclestatus.Fuel()
                fuel.fuelLevel = metrics.fuelLevel?.value
                fuel.distanceToEmpty = metrics.fuelRange?.value
                vehicleStatus.fuel = fuel
            }

            // Diesel info
            val diesel = DieselSystemStatus()
            metrics.dieselExhaustFluidLevel?.value?.let {
                val exhaustFluidLevel = DieselSystemStatus.ExhaustFluidLevel()
                exhaustFluidLevel.value = it.toString()
                diesel.exhaustFluidLevel = exhaustFluidLevel
            }
            metrics.dieselExhaustFluidLevelRangeRemaining?.value?.let {
                val ureaRange = DieselSystemStatus.UreaRange()
                ureaRange.value = it.toString()
                diesel.ureaRange = ureaRange
            }
            vehicleStatus.diesel = diesel

            // EV charging information
            metrics.xevBatteryIoCurrent?.let { current ->
                vehicleStatus.chargePower = -current.value *
                        metrics.xevBatteryChargerVoltageOutput!!.value
            }
            metrics.xevBatteryChargeDisplayStatus?.xevChargerPowerType?.let {
                vehicleStatus.chargeType = it
            }

            // Vehicle's internal temperature
            vehicleStatus.ambientTemp = metrics.ambientTemp?.value ?: 0.0

            return carStatus
        }
    }

    data class CarStatusMetrics(
//    val customMetrics: FluffyCustomMetrics,
        val alarmStatus: AlarmStatus?,
//    val acceleration: Acceleration,
//    val acceleratorPedalPosition: DoubleValue,
        val ambientTemp: DoubleValue?,
        val batteryStateOfCharge: AcceleratorPedalPosition?,
        val batteryVoltage: AcceleratorPedalPosition?,
//    val brakePedalStatus: AlarmStatus,
//    val brakeTorque: AcceleratorPedalPosition,
        val compassDirection: AlarmStatus,
        val dieselExhaustFluidLevel: DoubleValue?,
        val dieselExhaustFluidLevelRangeRemaining: DoubleValue?,
        val doorLockStatus: List<DoorStatus>?,
        val doorStatus: List<DoorStatus>,
//    val engineCoolantTemp: DoubleValue,
//    val engineOilTemp: DoubleValue,
        val fuelLevel: DoubleValue?,
        val fuelRange: DoubleValue?,
        val engineSpeed: DoubleValue,
        val xevBatteryTimeToFullCharge: AcceleratorPedalPosition?,
        val evBatteryTimeToFullCharge: AcceleratorPedalPosition?,
//    val gearLeverPosition: AlarmStatus,
//    val heading: Heading,
        val hoodStatus: StringValue,
        val hybridVehicleModeStatus: AlarmStatus?,
        val ignitionStatus: AlarmStatus,
        val indicators: Map<String, Indicator>?,
//    val outsideTemperature: AcceleratorPedalPosition,
//    val yawRate: AcceleratorPedalPosition,
//    val seatOccupancyStatus: List<AlarmStatus>,
//    val seatBeltStatus: List<AlarmStatus>,
//    val parkingBrakeStatus: AlarmStatus,
        val oilLifeRemaining: AcceleratorPedalPosition,
        val odometer: DoubleValue,
        val position: Position,
        val remoteStartCountdownTimer: AcceleratorPedalPosition,
//    val configurations: PurpleConfigurations,
        val speed: DoubleValue,
        val tirePressureStatus: List<AlarmStatus>?,
        val tirePressure: List<TirePressure>?,
        val tirePressureSystemStatus: List<AlarmStatus>?,
        val vehicleLifeCycleMode: AlarmStatus,
        val windowStatus: List<WindowStatus>?,
//    val wheelTorqueStatus: AlarmStatus,
        val xevPlugChargerStatus: StringValue?,
        val xevBatteryCapacity: DoubleValue?,
        val xevBatteryMaximumRange: DoubleValue?,
        val xevBatteryRange: DoubleValue?,
        val xevBatteryStateOfCharge: DoubleValue?,
//    val xevBatteryPerformanceStatus: AlarmStatus,
////    val torqueAtTransmission: AcceleratorPedalPosition,
////    val tripFuelEconomy: AcceleratorPedalPosition,
//    val tripXevBatteryRangeRegenerated: AcceleratorPedalPosition,
//    val tripXevBatteryChargeRegenerated: AcceleratorPedalPosition,
//    val panicAlarmStatus: AlarmStatus,
        val xevBatteryEnergyRemaining: AcceleratorPedalPosition?,
        val xevBatteryChargeDisplayStatus: BatteryChargeDisplayStatus?,
        val xevChargeStationPowerType: AlarmStatus?, // AC_BASIC or DC_BASIC?
//    val xevChargeStationCommunicationStatus: AlarmStatus,
        val displaySystemOfMeasure: AlarmStatus,
//    val doorPresenceStatus: List<DoorStatus>,
        val xevBatteryTemperature: AcceleratorPedalPosition?,
        val xevBatteryChargerCurrentOutput: AcceleratorPedalPosition?,
        val xevBatteryChargerVoltageOutput: AcceleratorPedalPosition?,

        // TODO: xevBatteryChargerVoltageOutput * xevBatteryChargerCurrentOutput == charging kW?
        val xevBatteryActualStateOfCharge: AcceleratorPedalPosition?,
        val xevBatteryIoCurrent: AcceleratorPedalPosition?,
        val xevBatteryVoltage: AcceleratorPedalPosition?,

//    val xevTractionMotorCurrent: AcceleratorPedalPosition,
//    val xevTractionMotorVoltage: AcceleratorPedalPosition
    ) {

        data class BatteryChargeDisplayStatus(
            val updateTime: String,

            @SerializedName("oemCorrelationId")
            val oemCorrelationID: String,

            val value: String,
            val xevChargerPowerType: String? = null,
            val xevBatteryType: String? = null,
            val xevChargeStatusOrigin: String? = null
        )

        data class AcceleratorPedalPosition(
            val updateTime: String,

            @SerializedName("oemCorrelationId")
            val oemCorrelationID: String,

            val value: Double,
            val vehicleBattery: String? = null,
            val tripProgress: String? = null
        )

        data class DoubleValue(
            val updateTime: String,
            val value: Double,
        )

        data class StringValue(
            val updateTime: String,
            val value: String,
        )

        data class Position(
            val updateTime: String,

            @SerializedName("oemCorrelationId")
            val oemCorrelationID: String,

            val value: PurpleValue,
            val gpsModuleTimestamp: String
        )

        data class PurpleValue(
            val location: Location
        )

        data class Location(
            val lat: Double,
            val lon: Double,
            val alt: Double
        )

        enum class ErrorSource(val value: String) {
            Device("DEVICE");

            companion object {
                fun fromValue(value: String): ErrorSource = when (value) {
                    "DEVICE" -> Device
                    else -> throw IllegalArgumentException()
                }
            }
        }

        enum class ScheduleType(val value: String) {
            Recurring("RECURRING");

            companion object {
                fun fromValue(value: String): ScheduleType = when (value) {
                    "RECURRING" -> Recurring
                    else -> throw IllegalArgumentException()
                }
            }
        }

        enum class LocalTimeZone(val value: String) {
            LocalTime("LOCAL_TIME");

            companion object {
                fun fromValue(value: String): LocalTimeZone = when (value) {
                    "LOCAL_TIME" -> LocalTime
                    else -> throw IllegalArgumentException()
                }
            }
        }

        enum class TimeOfDay(val value: String) {
            The0000("00:00"),
            The0830("08:30"),
            The0940("09:40");

            companion object {
                fun fromValue(value: String): TimeOfDay = when (value) {
                    "00:00" -> The0000
                    "08:30" -> The0830
                    "09:40" -> The0940
                    else -> throw IllegalArgumentException()
                }
            }
        }

        enum class ScheduleStatus(val value: String) {
            Off("OFF");

            companion object {
                fun fromValue(value: String): ScheduleStatus = when (value) {
                    "OFF" -> Off
                    else -> throw IllegalArgumentException()
                }
            }
        }

        data class AlarmStatus(
            val updateTime: String,

            @SerializedName("oemCorrelationId")
            val oemCorrelationID: String,

            val value: String,
            val tags: AlarmStatusTags? = null,
            val gpsModuleTimestamp: String? = null,
            val parkingBrakeType: String? = null,
            val vehicleOccupantRole: String? = null,
            val vehicleWheel: String? = null
        )

        data class AlarmStatusTags(
            @SerializedName("ALARM_SOURCE")
            val alarmSource: AlarmSource
        )

        enum class AlarmSource(val value: String) {
            Closed("CLOSED"),
            Locked("LOCKED"),
            Unknown("UNKNOWN");

            companion object {
                fun fromValue(value: String): AlarmSource = when (value) {
                    "CLOSED" -> Closed
                    "LOCKED" -> Locked
                    "UNKNOWN" -> Unknown
                    else -> throw IllegalArgumentException()
                }
            }
        }

        enum class Vehicle(val value: String) {
            Driver("DRIVER"),
            Passenger("PASSENGER"),
            Unknown("UNKNOWN");

            companion object {
                fun fromValue(value: String): Vehicle = when (value) {
                    "DRIVER" -> Driver
                    "PASSENGER" -> Passenger
                    "UNKNOWN" -> Unknown
                    else -> throw IllegalArgumentException()
                }
            }
        }


        data class DoorStatus(
            val updateTime: String,

            @SerializedName("oemCorrelationId")
            val oemCorrelationID: String,

            val value: String? = null,
            val vehicleDoor: String,
            val vehicleOccupantRole: String? = null,
            val vehicleSide: String? = null
        )

        data class WindowStatus(
            val updateTime: String,

            @SerializedName("oemCorrelationId")
            val oemCorrelationID: String,

            val value: WindowStatusValue,
            val vehicleWindow: String? = null,
            val vehicleOccupantRole: String? = null,
            val vehicleSide: String? = null
        )

        data class WindowStatusValue(
            val doubleRange: DoubleRange
        )

        data class DoubleRange(
            val lowerBound: Double,
            val upperBound: Double
        )

        enum class VehicleWindow(val value: String) {
            UnspecifiedFront("UNSPECIFIED_FRONT"),
            UnspecifiedRear("UNSPECIFIED_REAR");

            companion object {
                fun fromValue(value: String): VehicleWindow = when (value) {
                    "UNSPECIFIED_FRONT" -> UnspecifiedFront
                    "UNSPECIFIED_REAR" -> UnspecifiedRear
                    else -> throw IllegalArgumentException()
                }
            }
        }

        data class Indicator(
            val updateTime: String,
            val value: Boolean,
            val additionalInfo: String
        )

        data class TirePressure(
            val updateTime: String,

            @SerializedName("oemCorrelationId")
            val oemCorrelationID: String,

            val value: Double,
            val vehicleWheel: String,
            val wheelPlacardFront: Double? = null,
            val wheelPlacardRear: Double? = null
        )

    }
}
