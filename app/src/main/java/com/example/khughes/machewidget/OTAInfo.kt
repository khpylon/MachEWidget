package com.example.khughes.machewidget

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Entity
import com.example.khughes.machewidget.OTAStatus.FuseResponseList
import com.example.khughes.machewidget.OTAStatus.LanguageText
import com.example.khughes.machewidget.OTAStatus.OTAStatus
import com.example.khughes.machewidget.OTAStatus.FuseResponse
import java.util.ArrayList

@Entity(tableName = "ota_info")
class OTAInfo(var VIN: String) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @Embedded(prefix = "ota_")
    var responseList: FuseResponseList? = null

    @Embedded(prefix = "ota_")
    var languageText: LanguageText? = null

    @ColumnInfo(name = "ota_alertStatus")
    var alertStatus: String? = null
    fun fromOTAStatus(status: OTAStatus) {
        responseList = status.fuseResponse.fuseResponseList[0]
        alertStatus = status.otaAlertStatus
        languageText = status.fuseResponse.languageText
    }

    fun toOTAStatus(): OTAStatus {
        val status = OTAStatus()
        val list = ArrayList<FuseResponseList?>()
        list.add(responseList)
        val tmp = FuseResponse()
        tmp.fuseResponseList = list
        status.fuseResponse = tmp
        status.otaAlertStatus = alertStatus
        status.fuseResponse.languageText = languageText
        return status
    }
}