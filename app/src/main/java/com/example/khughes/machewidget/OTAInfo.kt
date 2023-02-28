package com.example.khughes.machewidget

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.khughes.machewidget.OTAStatus.FuseResponse
import com.example.khughes.machewidget.OTAStatus.FuseResponseList
import com.example.khughes.machewidget.OTAStatus.LanguageText
import com.example.khughes.machewidget.OTAStatus.OTAStatus

@Entity(tableName = "ota_info")
class OTAInfo(var vIN: String) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @Embedded(prefix = "ota_")
    var responseList: FuseResponseList? = null

    @Embedded(prefix = "ota_")
    var languageText: LanguageText? = null
    var otaAlertStatus: String? = null
        get() = field
        set(otaAlertStatus) {
            field = otaAlertStatus
        }

    fun fromOTAStatus(status: OTAStatus) {
        responseList = status.fuseResponse.fuseResponseList[0]
        otaAlertStatus = status.otaAlertStatus
        languageText = status.fuseResponse.languageText
    }

    fun toOTAStatus(): OTAStatus {
        val status = OTAStatus()
        val list = ArrayList<FuseResponseList?>()
        list.add(responseList)
        val tmp = FuseResponse()
        tmp.fuseResponseList = list
        status.fuseResponse = tmp
        status.otaAlertStatus = otaAlertStatus
        status.fuseResponse.languageText = languageText
        return status
    }
}