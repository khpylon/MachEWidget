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
    fun toNewDoorStatus(data: String?): List<NewDoorStatus>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val gson = Gson()
        val listType = object :
            TypeToken<List<NewDoorStatus>?>() {}.type
        return gson.fromJson<List<NewDoorStatus>>(data, listType)
    }

    @TypeConverter
    fun FromNewDoorStatus(someObjects: List<NewDoorStatus>?): String? {
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

    @ColumnInfo(name = "door_status")
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
//    @ColumnInfo(name = "charging_status")
    var value: String = "",
    // "NotReady", "ChargingAC"

//    @ColumnInfo(name = "charge_start_time")
    var chargeStartTime: String = "",

//    @ColumnInfo(name = "charge_end_time")
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
    @ColumnInfo(name = "vehicle_door")
    var vehicleDoor: String = "",
    // "UNSPECIFIED_FRONT", "REAR_LEFT", "REAR_RIGHT", "TAILGATE", "HOOD_DOOR", "INNER_TAILGATE"
    @ColumnInfo(name = "vehicle_door_value")
    var value: String = "",
    // "CLOSED", "AJAR"
    @ColumnInfo(name = "vehicle_occupant_role")
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

