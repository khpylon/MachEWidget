package com.example.khughes.machewidget

import androidx.room.ColumnInfo
import java.util.*

class VehicleIds {
    @ColumnInfo(name = "car_vehicleId")
    var vehicleId: String = ""
    @ColumnInfo(name = "car_nickName")
    var nickname: String? = null
    var modelId: Vehicle.Companion.Model = Vehicle.Companion.Model.UNKNOWN
    var enabled = false
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as VehicleIds
        return vehicleId == that.vehicleId && nickname == that.nickname
    }

    override fun hashCode(): Int {
        return Objects.hash(vehicleId, nickname)
    }
}