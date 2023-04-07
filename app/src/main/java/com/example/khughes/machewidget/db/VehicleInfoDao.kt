package com.example.khughes.machewidget.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.khughes.machewidget.VehicleIds
import com.example.khughes.machewidget.VehicleInfo

@Dao
interface VehicleInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVehicleInfo(info: VehicleInfo)

    @Query("SELECT * FROM vehicle_info")
    fun findVehicleInfo(): List<VehicleInfo>

    @Query("SELECT * FROM vehicle_info WHERE vin LIKE :VIN")
    fun findVehicleInfoByVIN(VIN: String): VehicleInfo?

    @Query("SELECT vin FROM vehicle_info WHERE userId LIKE :userId")
    fun findVINsByUserId(userId: String): List<String>

    @Query("SELECT * FROM vehicle_info WHERE userId LIKE :userId")
    fun findVehicleInfoByUserId(userId: String): List<VehicleInfo>

    @get:Query("SELECT nickname, VIN, enabled FROM vehicle_info")
    val liveDataVehicleInfo: LiveData<List<VehicleIds>>

    @Query("DELETE FROM vehicle_info WHERE vin LIKE :VIN")
    fun deleteVehicleInfoByVIN(VIN: String)

    @Query("DELETE FROM vehicle_info WHERE userId LIKE :userId")
    fun deleteVehicleInfoByUserId(userId: String)

    @Query("UPDATE vehicle_info SET supportsOTA = 1")
    fun updateSupportOTA()

    @Query("UPDATE vehicle_info SET enabled = :value WHERE vin = :VIN")
    fun updateEnable(VIN: String, value: Boolean)

    @Update
    fun updateVehicleInfo(info: VehicleInfo)
}