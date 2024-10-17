package com.example.khughes.machewidget.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.khughes.machewidget.Vehicle.Companion.Model
import com.example.khughes.machewidget.VehicleIds
import com.example.khughes.machewidget.VehicleInfo

@Dao
interface VehicleInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVehicleInfo(info: VehicleInfo)

    @Query("SELECT * FROM vehicle_info")
    fun findVehicleInfo(): List<VehicleInfo>

    @Query("SELECT * FROM vehicle_info WHERE car_vehicleId LIKE :vehicleId")
    fun findVehicleInfoByVIN(vehicleId: String): VehicleInfo?

    @get:Query("SELECT car_nickName, car_vehicleId, modelId, enabled FROM vehicle_info")
    val liveDataVehicleInfo: LiveData<List<VehicleIds>>

    @Query("DELETE FROM vehicle_info WHERE car_vehicleId LIKE :vehicleId")
    fun deleteVehicleInfoByVIN(vehicleId: String)

    @Query("UPDATE vehicle_info SET enabled = :value WHERE car_vehicleId = :vehicleId")
    fun updateEnable(vehicleId: String, value: Boolean)

    @Query("UPDATE vehicle_info SET modelId = :value WHERE car_vehicleId = :vehicleId")
    fun updateModel(vehicleId: String, value: Model)

    @Update
    fun updateVehicleInfo(info: VehicleInfo)
}