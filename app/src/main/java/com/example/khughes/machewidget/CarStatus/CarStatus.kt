package com.example.khughes.machewidget.CarStatus

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class CarStatus {

    // simpler getters go here
//    val odometer: Double
//        get() = vehiclestatus?.odometer?.value ?: 0.0
    val HVBFillLevel: Double
        get() = vehiclestatus?.batteryFillLevel?.value ?: Double.MAX_VALUE
//    val elVehDTE: Double
//        get() = vehiclestatus?.elVehDTE?.value ?: 0.0
    val distanceToEmpty: Double?
        get() = try {
            vehiclestatus!!.fuel!!.distanceToEmpty
        } catch (e: NullPointerException) {
            null
        }
    val fuelLevel: Double?
        get() = try {
            vehiclestatus!!.fuel!!.fuelLevel
        } catch (e: NullPointerException) {
            null
        }
    val LVBVoltage: Int?
        get() = try {
            vehiclestatus!!.battery!!.batteryStatusActual!!.value
        } catch (e: NullPointerException) {
            null
        }
    val LVBStatus: String?
        get() = try {
            vehiclestatus!!.battery!!.batteryHealth!!.value
        } catch (e: NullPointerException) {
            null
        }
    val frunk: String?
        get() = try {
            vehiclestatus!!.doorStatus!!.hoodDoor!!.value
        } catch (e: NullPointerException) {
            null
        }
    val tailgate: String?
        get() = try {
            vehiclestatus!!.doorStatus!!.tailgateDoor!!.value
        } catch (e: NullPointerException) {
            null
        }
    val driverDoor: String?
        get() = try {
            vehiclestatus!!.doorStatus!!.driverDoor!!.value
        } catch (e: NullPointerException) {
            null
        }
    val passengerDoor: String?
        get() = try {
            vehiclestatus!!.doorStatus!!.passengerDoor!!.value
        } catch (e: NullPointerException) {
            null
        }
    val leftRearDoor: String?
        get() = try {
            vehiclestatus!!.doorStatus!!.leftRearDoor!!.value
        } catch (e: NullPointerException) {
            null
        }
    val rightRearDoor: String?
        get() = try {
            vehiclestatus!!.doorStatus!!.rightRearDoor!!.value
        } catch (e: NullPointerException) {
            null
        }
    val driverWindow: String?
        get() = try {
            vehiclestatus!!.windowPosition!!.driverWindowPosition!!.value
        } catch (e: NullPointerException) {
            null
        }
    val passengerWindow: String?
        get() = try {
            vehiclestatus!!.windowPosition!!.passWindowPosition!!.value
        } catch (e: NullPointerException) {
            null
        }
    val leftRearWindow: String?
        get() = try {
            vehiclestatus!!.windowPosition!!.rearDriverWindowPos!!.value
        } catch (e: NullPointerException) {
            null
        }
    val rightRearWindow: String?
        get() = try {
            vehiclestatus!!.windowPosition!!.rearPassWindowPos!!.value
        } catch (e: NullPointerException) {
            null
        }
    val leftFrontTireStatus: String?
        get() = try {
            vehiclestatus!!.tpms!!.leftFrontTireStatus!!.value
        } catch (e: NullPointerException) {
            null
        }
    val rightFrontTireStatus: String?
        get() = try {
            vehiclestatus!!.tpms!!.rightFrontTireStatus!!.value
        } catch (e: NullPointerException) {
            null
        }
    val leftRearTireStatus: String?
        get() = try {
            vehiclestatus!!.tpms!!.outerLeftRearTireStatus!!.value
        } catch (e: NullPointerException) {
            null
        }
    val rightRearTireStatus: String?
        get() = try {
            vehiclestatus!!.tpms!!.outerRightRearTireStatus!!.value
        } catch (e: NullPointerException) {
            null
        }
    val leftFrontTirePressure: String?
        get() = try {
            vehiclestatus!!.tpms!!.leftFrontTirePressure!!.value
        } catch (e: NullPointerException) {
            null
        }
    val rightFrontTirePressure: String?
        get() = try {
            vehiclestatus!!.tpms!!.rightFrontTirePressure!!.value
        } catch (e: NullPointerException) {
            null
        }
    val leftRearTirePressure: String?
        get() = try {
            vehiclestatus!!.tpms!!.outerLeftRearTirePressure!!.value
        } catch (e: NullPointerException) {
            null
        }
    val rightRearTirePressure: String?
        get() = try {
            vehiclestatus!!.tpms!!.outerRightRearTirePressure!!.value
        } catch (e: NullPointerException) {
            null
        }
    val ignition: String?
        get() = try {
            vehiclestatus!!.ignitionStatus!!.value
        } catch (e: NullPointerException) {
            null
        }
    val lock: String?
        get() = try {
            vehiclestatus!!.lockStatus!!.value
        } catch (e: NullPointerException) {
            null
        }
    val remoteStartStatus: Boolean?
        get() = try {
            vehiclestatus!!.remoteStartStatus!!.value == 1
        } catch (e: NullPointerException) {
            null
        }
    val alarm: String?
        get() = vehiclestatus?.alarm?.value
    val deepSleep: Boolean?
        get() = try {
            vehiclestatus!!.deepSleepInProgress!!.value
        } catch (e: NullPointerException) {
            null
        }
    val plugStatus: Boolean
        get() = try {
            vehiclestatus!!.plugStatus!!.value == 1
        } catch (e: NullPointerException) {
            false
        }
    val chargingStatus: String?
        get() = try {
            vehiclestatus!!.chargingStatus!!.value
        } catch (e: NullPointerException) {
            null
        }
    val chargingEndTime: String?
        get() = try {
            vehiclestatus!!.chargeEndTime!!.value
        } catch (e: NullPointerException) {
            null
        }
    val latitude: String?
        get() = try {
            vehiclestatus!!.gps!!.latitude
        } catch (e: NullPointerException) {
            null
        }
    val longitude: String?
        get() = try {
            vehiclestatus!!.gps!!.longitude
        } catch (e: NullPointerException) {
            null
        }
    val lastRefresh: String?
        get() = try {
            var lastRefresh = vehiclestatus!!.lastRefresh
            if (lastRefresh!!.contains("01-01-2018")) {
                lastRefresh = vehiclestatus!!.lastModifiedDate
            }
            lastRefresh
        } catch (e: NullPointerException) {
            null
        }
    val propulsion: Int
        get() = if (vehiclestatus != null) {
            if (vehiclestatus!!.fuel == null) {
                PROPULSION_ELECTRIC
            } else if (vehiclestatus!!.batteryFillLevel == null) {
                PROPULSION_ICE_OR_HYBRID
            } else {
                PROPULSION_PHEV
            }
        } else PROPULSION_UNKNOWN

    fun isPropulsionElectric(method: Int): Boolean {
        return method == PROPULSION_ELECTRIC
    }

    fun isPropulsionPHEV(method: Int): Boolean {
        return method == PROPULSION_PHEV
    }

    fun isPropulsionICEOrHybrid(method: Int): Boolean {
        return method == PROPULSION_ICE_OR_HYBRID
    }

    @SerializedName("vehiclestatus")
    @Expose
    @Embedded
    var vehiclestatus: Vehiclestatus? = null

    @SerializedName("version")
    @Expose
    var version: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    companion object {
        private const val PROPULSION_UNKNOWN = 0
        private const val PROPULSION_ELECTRIC = 1
        private const val PROPULSION_PHEV = 2
        private const val PROPULSION_ICE_OR_HYBRID = 3
    }
}
