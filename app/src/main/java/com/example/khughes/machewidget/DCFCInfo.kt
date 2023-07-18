package com.example.khughes.machewidget

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

// Information read from Ford, except for currentDte, batteryFillLevel, and time
@Generated("jsonschema2pojo")
class DCFCInfo {
    @SerializedName("vin")
    var VIN: String? = ""
    var chargeType: String? = ""
    var plugInTime: String? = ""
    var initialDte: Double? = 0.0
    var energy: Double? = 0.0
    var power: Double? = 0.0
    var chargeLocationName: String? = ""
    var network: String? = ""
    var currentDte: Double? = 0.0
    var batteryFillLevel: Double? = 0.0
    var time: String? = ""
}

// Information saved as charging progresses.
@Generated("jsonschema2pojo")
class DCFCUpdate {
    var time: String? = ""
    var energy: Double? = 0.0
    var power: Double? = 0.0
    var dte: Double? = 0.0
    var batteryFillLevel: Double? = 0.0

    constructor(session: DCFCInfo) {
        time = session.time
        energy = session.energy
        power = session.power
        dte = session.currentDte
        batteryFillLevel = session.batteryFillLevel
    }
}

// Information saved for a single charging session.
@Generated("jsonschema2pojo")
class DCFCSession {
    @SerializedName("vin")
    var VIN: String? = ""
    var chargeType: String? = ""
    var plugInTime: String? = ""
    var initialDte: Double? = 0.0
    var chargeLocationName: String? = ""
    var network: String? = ""
    lateinit var updates: MutableList<DCFCUpdate>

    constructor(session: DCFCInfo, updates: MutableList<DCFCUpdate>) {
        VIN = session.VIN
        chargeType = session.chargeType
        plugInTime = session.plugInTime
        chargeLocationName = session.chargeLocationName
        initialDte = session.initialDte
        chargeLocationName = session.chargeLocationName
        network = session.network
        this.updates = updates
    }
}

//class TestExclStrat : ExclusionStrategy {
//    override fun shouldSkipClass(arg0: Class<*>?): Boolean {
//        return false
//    }
//
//    override fun shouldSkipField(f: FieldAttributes): Boolean {
//        return  f.name in listOf("VIN", "chargeType", "initialDte",
//            "finalDte", "chargeLocationName", "network")
//    }
//}
