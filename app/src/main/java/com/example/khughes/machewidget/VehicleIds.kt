package com.example.khughes.machewidget

import androidx.room.ColumnInfo
import java.util.*

class VehicleIds {
    @ColumnInfo(name = "VIN")
    var vin: String? = null
    var nickname: String? = null
    var enabled = false
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as VehicleIds
        return vin == that.vin && nickname == that.nickname
    }

    override fun hashCode(): Int {
        return Objects.hash(vin, nickname)
    }
}