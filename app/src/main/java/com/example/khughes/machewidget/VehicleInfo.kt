package com.example.khughes.machewidget

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.khughes.machewidget.CarStatus.CarStatus
import com.example.khughes.machewidget.OTAStatus.FuseResponse
import com.example.khughes.machewidget.OTAStatus.FuseResponseList
import com.example.khughes.machewidget.OTAStatus.LanguageText
import com.example.khughes.machewidget.OTAStatus.OTAStatus
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "vehicle_info")
class VehicleInfo {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "VIN")
    var vin: String? = ""
    var userId: String? = ""
    var nickname: String? = ""
    var lastRefreshTime: Long = 0
    var lastUpdateTime: Long = 0

    @ColumnInfo(defaultValue = "0")
    var lastOTATime: Long = 0
    var lastLVBStatus: String? = "STATUS_GOOD"
    var lastTPMSStatus: String? = "Normal"

    @ColumnInfo(defaultValue = "''")
    var lastChargeStatus = "''"
    var lastDTE = 0.0
    var lastFuelLevel = 0.0
    @ColumnInfo(name = "supportsOTA")
    var isSupportsOTA = true

    @ColumnInfo(defaultValue = "0")
    var initialForcedRefreshTime: Long = 0

    @ColumnInfo(defaultValue = "0")
    var lastForcedRefreshTime: Long = 0

    @ColumnInfo(defaultValue = "0")
    var forcedRefreshCount: Long = 0

    @ColumnInfo(name = "enabled", defaultValue = "1")
    var isEnabled = true

    @ColumnInfo(defaultValue = "0xffffffff")
    var colorValue: Int

    @ColumnInfo(defaultValue = "0")
    var chargeHour: Int

    @ColumnInfo(defaultValue = "7")
    var chargeThresholdLevel: Int

    var chargingPower = 0.0

    var chargingEnergy = 0.0

    @Embedded(prefix = "car_")
    var carStatus: CarStatus? = null

    @Embedded(prefix = "ota_")
    var responseList: FuseResponseList? = null

    @Embedded(prefix = "ota_")
    var error: Any? = null
    var otaAlertStatus: String? = null

    @Embedded(prefix = "ota_")
    var updatePendingState: Any? = null

    @Embedded(prefix = "ota_")
    var languageText: LanguageText? = null

    init {
        // for new database entries, this will generate a new id
        colorValue = -0x1
        chargeHour = 0
        chargeThresholdLevel = 7
    }

    fun fromOTAStatus(status: OTAStatus) {
        responseList = status.fuseResponse.fuseResponseList[0]
        error = status.error
        otaAlertStatus = status.otaAlertStatus
        updatePendingState = status.updatePendingState
        languageText = status.fuseResponse.languageText
    }

    fun toOTAStatus(): OTAStatus {
        val status = OTAStatus()
        val list = ArrayList<FuseResponseList?>()
        list.add(responseList)
        val tmp = FuseResponse()
        tmp.fuseResponseList = list
        status.fuseResponse = tmp
        status.error = error
        status.otaAlertStatus = otaAlertStatus
        status.updatePendingState = updatePendingState
        status.fuseResponse.languageText = languageText
        return status
    }

    fun setLastUpdateTime() {
        lastUpdateTime =
            LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
    }
}