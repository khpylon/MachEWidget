package com.example.khughes.machewidget

import androidx.room.ColumnInfo
import java.util.*

class VehicleIds {
    @ColumnInfo(name = "VIN")
    var vin: String? = null
    var nickname: String? = null
    var enabled = false
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as VehicleIds
        return vin == that.vin && nickname == that.nickname
    }

    override fun hashCode(): Int {
        return Objects.hash(vin, nickname)
    }
}