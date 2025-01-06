package com.example.khughes.machewidget

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.khughes.machewidget.Vehicle.Companion.Model
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "vehicle_info")
data class VehicleInfo (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var tokenId: String? = "",
    var modelId: Model = Model.UNKNOWN,
    var lastRefreshTime: Long = 0,
    var lastUpdateTime: Long = 0,

    @ColumnInfo(defaultValue = "0")
    var lastLVBStatus: String? = "STATUS_GOOD",
    var lastTPMSStatus: String? = "Normal",

    @ColumnInfo(defaultValue = "''")
    var lastChargeStatus: String = "''",
    var lastDTE : Double = 0.0,
    var lastFuelLevel : Double = 0.0,

    @ColumnInfo(defaultValue = "0")
    var initialForcedRefreshTime: Long = 0,

    @ColumnInfo(defaultValue = "0")
    var lastForcedRefreshTime: Long = 0,

    @ColumnInfo(defaultValue = "0")
    var forcedRefreshCount: Long = 0,

    @ColumnInfo(name = "enabled", defaultValue = "1")
    var isEnabled : Boolean = true,

    @ColumnInfo(defaultValue = "0xffffffff")
    var colorValue: Int = -0x1,

    @ColumnInfo(defaultValue = "0")
    var chargeHour: Int = 0,

    @ColumnInfo(defaultValue = "7")
    var chargeThresholdLevel: Int = 7,

    @Embedded(prefix = "car_")
    var carStatus: CarStatus = CarStatus()
) {
    init {
        // for new database entries, this will generate a new id
        colorValue = -0x1
        chargeHour = 0
        chargeThresholdLevel = 7
    }

//    fun fromOTAStatus(status: OTAStatus) {
//        responseList = status.fuseResponse.fuseResponseList[0]
//        error = status.error
//        otaAlertStatus = status.otaAlertStatus
//        updatePendingState = status.updatePendingState
//        languageText = status.fuseResponse.languageText
//    }
//
//    fun toOTAStatus(): OTAStatus {
//        val status = OTAStatus()
//        val list = ArrayList<FuseResponseList?>()
//        list.add(responseList)
//        val tmp = FuseResponse()
//        tmp.fuseResponseList = list
//        status.fuseResponse = tmp
//        status.error = error
//        status.otaAlertStatus = otaAlertStatus
//        status.updatePendingState = updatePendingState
//        status.fuseResponse.languageText = languageText
//        return status
//    }

    fun setLastUpdateTime() {
        lastUpdateTime =
            LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
    }
}