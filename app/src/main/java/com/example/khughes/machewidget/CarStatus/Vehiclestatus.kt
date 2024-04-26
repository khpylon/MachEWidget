package com.example.khughes.machewidget.CarStatus

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import com.example.khughes.machewidget.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Vehiclestatus {
    @SerializedName("vin")
    @Expose
    @Ignore
    var vin: String? = null

    @SerializedName("lastRefresh")
    @Expose
    var lastRefresh: String? = null

    @SerializedName("lastModifiedDate")
    @Expose
    var lastModifiedDate: String? = null

    @SerializedName("chargePower")
    @Expose
    @ColumnInfo(defaultValue = "0", )
    var chargePower: Double = 0.0

    @SerializedName("chargeEnergy")
    @Expose
    @ColumnInfo(defaultValue = "0")
    var chargeEnergy: Double = 0.0

    @SerializedName("chargeType")
    @Expose
    @ColumnInfo(defaultValue = "''")
    var chargeType: String = ""

    @SerializedName("initialDte")
    @Expose
    @ColumnInfo(defaultValue = "0")
    var initialDte: Double = 0.0

    @SerializedName("pluginTime")
    @Expose
    @ColumnInfo(defaultValue = "''")
    var pluginTime: String = "null"

    @SerializedName("xevBatteryEnergyRemaining")
    @Expose
    @ColumnInfo(defaultValue = "0", )
    var xevBatteryEnergyRemaining: Double = 0.0

    @SerializedName("xevBatteryVoltage")
    @Expose
    @ColumnInfo(defaultValue = "0", )
    var xevBatteryTemperature: Double = 0.0

    @SerializedName("xevBatteryVoltage")
    @Expose
    @ColumnInfo(defaultValue = "0", )
    var xevBatteryVoltage: Double = 0.0

    @SerializedName("ambientTemp")
    @Expose
    @ColumnInfo(defaultValue = "0", )
    var ambientTemp: Double = 0.0

    @Generated("jsonschema2pojo")
    class Alarm {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "alarm_value")
        internal var value: String? = null
    }

    @SerializedName("alarm")
    @Expose
    @Embedded
    var alarm: Alarm? = null

    @Generated("jsonschema2pojo")
    class LockStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "lockstatus_value")
        internal var value: String? = null
    }

    @SerializedName("lockStatus")
    @Expose
    @Embedded
    var lockStatus: LockStatus? = null

    @Generated("jsonschema2pojo")
    class Odometer {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "odometer_value")
        internal var value: Double? = null
    }

    @SerializedName("odometer")
    @Expose
    @Embedded
    var odometer: Odometer? = null

    @Generated("jsonschema2pojo")
    class Fuel {
        @SerializedName("fuelLevel")
        @Expose
        internal var fuelLevel: Double? = null

        @SerializedName("distanceToEmpty")
        @Expose
        internal var distanceToEmpty: Double? = null
    }

    @SerializedName("fuel")
    @Expose
    @Embedded
    var fuel: Fuel? = null

    @Generated("jsonschema2pojo")
    class Gps {
        @SerializedName("latitude")
        @Expose
        internal var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        internal var longitude: String? = null

        @SerializedName("gpsState")
        @Expose
        internal var gpsState: String? = null
    }

    @SerializedName("gps")
    @Expose
    @Embedded
    var gps: Gps? = null

    @Generated("jsonschema2pojo")
    class RemoteStart {
        @SerializedName("remoteStartDuration")
        @Expose
        internal var remoteStartDuration: Int? = null

        @SerializedName("remoteStartTime")
        @Expose
        internal var remoteStartTime: Int? = null
    }

    @SerializedName("remoteStart")
    @Expose
    @Embedded
    var remoteStart: RemoteStart? = null

    @Generated("jsonschema2pojo")
    class RemoteStartStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "remotestartstatus_value")
        internal var value: Int? = null
    }

    @SerializedName("remoteStartStatus")
    @Expose
    @Embedded
    var remoteStartStatus: RemoteStartStatus? = null

    @Generated("jsonschema2pojo")
    class DeepSleepInProgress {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "deepsleep_value")
        var value: Boolean? = null
    }

    @SerializedName("deepSleepInProgress")
    @Expose
    @Embedded
    var deepSleepInProgress: DeepSleepInProgress? = null

    @Generated("jsonschema2pojo")
    class BatteryFillLevel {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "batteryfilllevel_value")
        internal var value: Double? = null
    }

    @SerializedName("batteryFillLevel")
    @Expose
    @Embedded
    var batteryFillLevel: BatteryFillLevel? = null

    @Generated("jsonschema2pojo")
    class ElVehDTE {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "elvehdte_value")
        internal var value: Double? = null
    }

    @SerializedName("elVehDTE")
    @Expose
    @Embedded
    var elVehDTE: ElVehDTE? = null

    @Generated("jsonschema2pojo")
    class ChargingStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "chargingstatus_value")
        internal var value: String? = null
    }

    @SerializedName("chargingStatus")
    @Expose
    @Embedded
    var chargingStatus: ChargingStatus? = null

    @Generated("jsonschema2pojo")
    class PlugStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "plugstatus_value")
        internal var value: Int? = null
    }

    @SerializedName("plugStatus")
    @Expose
    @Embedded
    var plugStatus: PlugStatus? = null

    @Generated("jsonschema2pojo")
    class ChargeEndTime {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "chargeendtime_value")
        internal var value: String? = null
    }

    @SerializedName("chargeEndTime")
    @Expose
    @Embedded
    var chargeEndTime: ChargeEndTime? = null

    @Generated("jsonschema2pojo")
    class IgnitionStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "ignitionstatus_value")
        internal var value: String? = null
    }

    @SerializedName("ignitionStatus")
    @Expose
    @Embedded
    var ignitionStatus: IgnitionStatus? = null

    @SerializedName("windowPosition")
    @Expose
    @Embedded
    var windowPosition: WindowPosition? = null

    @SerializedName("doorStatus")
    @Expose
    @Embedded
    var doorStatus: DoorStatus? = null

    @SerializedName("battery")
    @Expose
    @Embedded
    var battery: Battery? = null

    @SerializedName("TPMS")
    @Expose
    @Embedded
    var tpms: TPMS? = null

    @SerializedName("dieselSystemStatus")
    @Expose
    @Embedded
    var diesel: DieselSystemStatus? = null

}
