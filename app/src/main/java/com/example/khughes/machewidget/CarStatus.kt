package com.example.khughes.machewidget

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.Collections
import javax.annotation.Generated

class DoorConverters {
    @TypeConverter
    fun stringToListDoorStatus(data: String?): List<NewDoorStatus>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val gson = Gson()
        val listType = object :
            TypeToken<List<NewDoorStatus>?>() {}.type
        return gson.fromJson<List<NewDoorStatus>>(data, listType)
    }

    @TypeConverter
    fun listDoorStatusToString(someObjects: List<NewDoorStatus>?): String? {
        val gson = Gson()
        return gson.toJson(someObjects)
    }

}

@Generated("jsonschema2pojo")
class CarStatus(
    var status: String = "",
    @Embedded
    var vehicle: CarStatusVehicle = CarStatusVehicle()
) {
    fun isPropulsionElectric(): Boolean {
        return vehicle.engineType == "BEV"
    }

    fun isPropulsionPHEV(): Boolean {
        return vehicle.engineType == "PHEV"
    }

    fun isPropulsionICEOrHybrid(): Boolean {
        return vehicle.engineType == "ICE" || isPropulsionPHEV()
    }

}

@Generated("jsonschema2pojo")
data class CarStatusVehicle(
    var vehicleId: String = "",
    var make: String = "",
    var modelName: String = "",
    var modelYear: String = "",
    var color: String = "",
    var nickName: String? = null,
    var modemEnabled: Boolean = false,
    var lastUpdated: String = "",
//    var vehicleAuthorizationIndicator: Long = 0,
//    var serviceCompatible: Boolean = false,
    var engineType: String = "",
    @Embedded
    var vehicleDetails: VehicleDetails = VehicleDetails(),
    @Embedded
    var vehicleStatus: VehicleStatus = VehicleStatus(),
    @Embedded
    var vehicleLocation: VehicleLocation = VehicleLocation(),
)

@Generated("jsonschema2pojo")
data class VehicleDetails(
    @SerializedName("batteryChargeLevel")
    @Expose
    @Embedded
    var batteryChargeLevel: BatteryChargeLevel? = null,
    
    @SerializedName("fuelLevel")
    @Expose
    @Embedded
    var fuelLevel: FuelLevel? = null,

    @SerializedName("odometer")
    @Expose
    @ColumnInfo(name = "odometer")
    var odometer: Double = 0.0,
    
    var mileage: Double = 0.0,
)

@Generated("jsonschema2pojo")
data class BatteryChargeLevel(
    @ColumnInfo(name = "bev_fill_level")
    @SerializedName("value")
    var value: Double = 0.0,

    @ColumnInfo(name = "bev_distance_to_empty")
    @SerializedName("distanceToEmpty")
    var distanceToEmpty: Double = 0.0,
//    val timestamp: String,
)

@Generated("jsonschema2pojo")
data class FuelLevel(
    @ColumnInfo(name = "ice_fill_level")
    @SerializedName("value")
    var value: Double = 0.0,

    @ColumnInfo(name = "ice_distance_to_empty")
    @SerializedName("distanceToEmpty")
    var distanceToEmpty: Double = 0.0,
//    var timestamp: String? = null,
)

@Generated("jsonschema2pojo")
data class VehicleStatus(
    @ColumnInfo(name = "tire_pressure_warning")
    var tirePressureWarning: Boolean = false,

    @ColumnInfo(name = "deep_sleep_in_progress")
    var deepSleepInProgress: Boolean = false,

    @Embedded
    var lockStatus: LockStatus = LockStatus(),

    @Embedded
    var alarmStatus: AlarmStatus = AlarmStatus(),

    @Embedded
    var remoteStartStatus: RemoteStartStatus = RemoteStartStatus(),

    @Embedded
    var chargingStatus: ChargingStatus? = null,

    @Embedded
    var plugStatus: PlugStatus? = null,

    @Embedded
    var ignitionStatus: IgnitionStatus = IgnitionStatus(),

    // TODO: finishing this typeconverter
    @Ignore
    var doorStatus: List<NewDoorStatus> = listOf(),

    var firmwareUpgradeInProgress: Boolean = false,
)

@Generated("jsonschema2pojo")
data class RemoteStartStatus(
    @ColumnInfo(name = "remote_start_status")
    var status: String = "",
    // "ENGINE_STOPPED", "ENGINE_RUNNING",
    
    @ColumnInfo(name = "remote_start_duration")
    var duration: Long = 0,
)

@Generated("jsonschema2pojo")
data class ChargingStatus(
    @ColumnInfo(name = "charging_status")
    var value: String = "",
    // "NotReady", "ChargingAC"

    @ColumnInfo(name = "charge_start_time")
    var chargeStartTime: String = "",

    @ColumnInfo(name = "charge_end_time")
    var chargeEndTime: String = "",

//    var timeStamp: String,
)

@Generated("jsonschema2pojo")
data class PlugStatus(
    @ColumnInfo(name = "plug_status")
    var value: Boolean = false,
//    var timeStamp: String = "",
)

@Generated("jsonschema2pojo")
data class IgnitionStatus(
    @ColumnInfo(name = "ignition_status")
    var value: String = "",
    // "OFF", "ON"
//    var timeStamp: String,
)

@Generated("jsonschema2pojo")
data class NewDoorStatus(
    var vehicleDoor: String = "",
    // "UNSPECIFIED_FRONT", "REAR_LEFT", "REAR_RIGHT", "TAILGATE", "HOOD_DOOR", "INNER_TAILGATE"
    var value: String = "",
    // "CLOSED", "AJAR"
    var vehicleOccupantRole: String = "",
    // "DRIVER", "PASSENGER", "NOT_APPLICABLE"
//    var timeStamp: String,
)

@Generated("jsonschema2pojo")
data class LockStatus(
    @ColumnInfo(name = "lock_status")
    var value: String = "",
    // "LOCKED"
//    var timeStamp: String,
)

@Generated("jsonschema2pojo")
data class AlarmStatus(
    @ColumnInfo(name = "alarm_status")
    var value: String = "",
    // "SET"
//    var timeStamp: String,
)

@Generated("jsonschema2pojo")
data class VehicleLocation(
    var speed: Double = 0.0,
    var direction: String = "",
    @ColumnInfo(name = "longitude")
    var longitude: String = "",
    @ColumnInfo(name = "latitude")
    var latitude: String = "",
//    var timeStamp: String,
)

//
// From NewCarStatus
//
//    data class CarStatusMetrics(
////    val customMetrics: FluffyCustomMetrics,
//        val alarmStatus: AlarmStatus?,
////    val acceleration: Acceleration,
////    val acceleratorPedalPosition: DoubleValue,
//        val ambientTemp: DoubleValue?,
//        val batteryStateOfCharge: AcceleratorPedalPosition?,
//        val batteryVoltage: AcceleratorPedalPosition?,
////    val brakePedalStatus: AlarmStatus,
////    val brakeTorque: AcceleratorPedalPosition,
//        val compassDirection: AlarmStatus,
//        val dieselExhaustFluidLevel: DoubleValue?,
//        val dieselExhaustFluidLevelRangeRemaining: DoubleValue?,
//        val doorLockStatus: List<DoorStatus>?,
//        val doorStatus: List<DoorStatus>,
////    val engineCoolantTemp: DoubleValue,
////    val engineOilTemp: DoubleValue,
//        val fuelLevel: DoubleValue?,
//        val fuelRange: DoubleValue?,
//        val engineSpeed: DoubleValue,
//        val xevBatteryTimeToFullCharge: AcceleratorPedalPosition?,
//        val evBatteryTimeToFullCharge: AcceleratorPedalPosition?,
////    val gearLeverPosition: AlarmStatus,
////    val heading: Heading,
//        val hoodStatus: StringValue,
//        val hybridVehicleModeStatus: AlarmStatus?,
//        val ignitionStatus: AlarmStatus,
//        val indicators: Map<String, Indicator>?,
////    val outsideTemperature: AcceleratorPedalPosition,
////    val yawRate: AcceleratorPedalPosition,
////    val seatOccupancyStatus: List<AlarmStatus>,
////    val seatBeltStatus: List<AlarmStatus>,
////    val parkingBrakeStatus: AlarmStatus,
//        val oilLifeRemaining: AcceleratorPedalPosition,
//        val odometer: DoubleValue,
//        val position: Position,
//        val remoteStartCountdownTimer: AcceleratorPedalPosition,
////    val configurations: PurpleConfigurations,
//        val speed: DoubleValue,
//        val tirePressureStatus: List<AlarmStatus>?,
//        val tirePressure: List<TirePressure>?,
//        val tirePressureSystemStatus: List<AlarmStatus>?,
//        val vehicleLifeCycleMode: AlarmStatus,
//        val windowStatus: List<WindowStatus>?,
////    val wheelTorqueStatus: AlarmStatus,
//        val xevPlugChargerStatus: StringValue?,
//        val xevBatteryCapacity: DoubleValue?,
//        val xevBatteryMaximumRange: DoubleValue?,
//        val xevBatteryRange: DoubleValue?,
//        val xevBatteryStateOfCharge: DoubleValue?,
////    val xevBatteryPerformanceStatus: AlarmStatus,
//////    val torqueAtTransmission: AcceleratorPedalPosition,
//////    val tripFuelEconomy: AcceleratorPedalPosition,
////    val tripXevBatteryRangeRegenerated: AcceleratorPedalPosition,
////    val tripXevBatteryChargeRegenerated: AcceleratorPedalPosition,
////    val panicAlarmStatus: AlarmStatus,
//        val xevBatteryEnergyRemaining: AcceleratorPedalPosition?,
//        val xevBatteryChargeDisplayStatus: BatteryChargeDisplayStatus?,
//        val xevChargeStationPowerType: AlarmStatus?, // AC_BASIC or DC_BASIC?
////    val xevChargeStationCommunicationStatus: AlarmStatus,
//        val displaySystemOfMeasure: AlarmStatus,
////    val doorPresenceStatus: List<DoorStatus>,
//        val xevBatteryTemperature: AcceleratorPedalPosition?,
//        val xevBatteryChargerCurrentOutput: AcceleratorPedalPosition?,
//        val xevBatteryChargerVoltageOutput: AcceleratorPedalPosition?,
//
//        // TODO: xevBatteryChargerVoltageOutput * xevBatteryChargerCurrentOutput == charging kW?
//        val xevBatteryActualStateOfCharge: AcceleratorPedalPosition?,
//        val xevBatteryIoCurrent: AcceleratorPedalPosition?,
//        val xevBatteryVoltage: AcceleratorPedalPosition?,
//
////    val xevTractionMotorCurrent: AcceleratorPedalPosition,
////    val xevTractionMotorVoltage: AcceleratorPedalPosition
//    ) {
//
//        data class BatteryChargeDisplayStatus(
//            val updateTime: String,
//
//            @SerializedName("oemCorrelationId")
//            val oemCorrelationID: String,
//
//            val value: String,
//            val xevChargerPowerType: String? = null,
//            val xevBatteryType: String? = null,
//            val xevChargeStatusOrigin: String? = null
//        )
//
//        data class AcceleratorPedalPosition(
//            val updateTime: String,
//
//            @SerializedName("oemCorrelationId")
//            val oemCorrelationID: String,
//
//            val value: Double,
//            val vehicleBattery: String? = null,
//            val tripProgress: String? = null
//        )
//
//        data class DoubleValue(
//            val updateTime: String,
//            val value: Double,
//        )
//
//        data class StringValue(
//            val updateTime: String,
//            val value: String,
//        )
//
//        data class Position(
//            val updateTime: String,
//
//            @SerializedName("oemCorrelationId")
//            val oemCorrelationID: String,
//
//            val value: PurpleValue,
//            val gpsModuleTimestamp: String
//        )
//
//        data class PurpleValue(
//            val location: Location
//        )
//
//        data class Location(
//            val lat: Double,
//            val lon: Double,
//            val alt: Double
//        )
//
//        enum class ErrorSource(val value: String) {
//            Device("DEVICE");
//
//            companion object {
//                fun fromValue(value: String): ErrorSource = when (value) {
//                    "DEVICE" -> Device
//                    else -> throw IllegalArgumentException()
//                }
//            }
//        }
//
//        enum class ScheduleType(val value: String) {
//            Recurring("RECURRING");
//
//            companion object {
//                fun fromValue(value: String): ScheduleType = when (value) {
//                    "RECURRING" -> Recurring
//                    else -> throw IllegalArgumentException()
//                }
//            }
//        }
//
//        enum class LocalTimeZone(val value: String) {
//            LocalTime("LOCAL_TIME");
//
//            companion object {
//                fun fromValue(value: String): LocalTimeZone = when (value) {
//                    "LOCAL_TIME" -> LocalTime
//                    else -> throw IllegalArgumentException()
//                }
//            }
//        }
//
//        enum class TimeOfDay(val value: String) {
//            The0000("00:00"),
//            The0830("08:30"),
//            The0940("09:40");
//
//            companion object {
//                fun fromValue(value: String): TimeOfDay = when (value) {
//                    "00:00" -> The0000
//                    "08:30" -> The0830
//                    "09:40" -> The0940
//                    else -> throw IllegalArgumentException()
//                }
//            }
//        }
//
//        enum class ScheduleStatus(val value: String) {
//            Off("OFF");
//
//            companion object {
//                fun fromValue(value: String): ScheduleStatus = when (value) {
//                    "OFF" -> Off
//                    else -> throw IllegalArgumentException()
//                }
//            }
//        }
//
//        data class AlarmStatus(
//            val updateTime: String,
//
//            @SerializedName("oemCorrelationId")
//            val oemCorrelationID: String,
//
//            val value: String,
//            val tags: AlarmStatusTags? = null,
//            val gpsModuleTimestamp: String? = null,
//            val parkingBrakeType: String? = null,
//            val vehicleOccupantRole: String? = null,
//            val vehicleWheel: String? = null
//        )
//
//        data class AlarmStatusTags(
//            @SerializedName("ALARM_SOURCE")
//            val alarmSource: AlarmSource
//        )
//
//        enum class AlarmSource(val value: String) {
//            Closed("CLOSED"),
//            Locked("LOCKED"),
//            Unknown("UNKNOWN");
//
//            companion object {
//                fun fromValue(value: String): AlarmSource = when (value) {
//                    "CLOSED" -> Closed
//                    "LOCKED" -> Locked
//                    "UNKNOWN" -> Unknown
//                    else -> throw IllegalArgumentException()
//                }
//            }
//        }
//
//        enum class Vehicle(val value: String) {
//            Driver("DRIVER"),
//            Passenger("PASSENGER"),
//            Unknown("UNKNOWN");
//
//            companion object {
//                fun fromValue(value: String): Vehicle = when (value) {
//                    "DRIVER" -> Driver
//                    "PASSENGER" -> Passenger
//                    "UNKNOWN" -> Unknown
//                    else -> throw IllegalArgumentException()
//                }
//            }
//        }
//
//
//        data class DoorStatus(
//            val updateTime: String,
//
//            @SerializedName("oemCorrelationId")
//            val oemCorrelationID: String,
//
//            val value: String? = null,
//            val vehicleDoor: String,
//            val vehicleOccupantRole: String? = null,
//            val vehicleSide: String? = null
//        )
//
//        data class WindowStatus(
//            val updateTime: String,
//
//            @SerializedName("oemCorrelationId")
//            val oemCorrelationID: String,
//
//            val value: WindowStatusValue,
//            val vehicleWindow: String? = null,
//            val vehicleOccupantRole: String? = null,
//            val vehicleSide: String? = null
//        )
//
//        data class WindowStatusValue(
//            val doubleRange: DoubleRange
//        )
//
//        data class DoubleRange(
//            val lowerBound: Double,
//            val upperBound: Double
//        )
//
//        enum class VehicleWindow(val value: String) {
//            UnspecifiedFront("UNSPECIFIED_FRONT"),
//            UnspecifiedRear("UNSPECIFIED_REAR");
//
//            companion object {
//                fun fromValue(value: String): VehicleWindow = when (value) {
//                    "UNSPECIFIED_FRONT" -> UnspecifiedFront
//                    "UNSPECIFIED_REAR" -> UnspecifiedRear
//                    else -> throw IllegalArgumentException()
//                }
//            }
//        }
//
//        data class Indicator(
//            val updateTime: String,
//            val value: Boolean,
//            val additionalInfo: String
//        )
//
//        data class TirePressure(
//            val updateTime: String,
//
//            @SerializedName("oemCorrelationId")
//            val oemCorrelationID: String,
//
//            val value: Double,
//            val vehicleWheel: String,
//            val wheelPlacardFront: Double? = null,
//            val wheelPlacardRear: Double? = null
//        )
//    }

