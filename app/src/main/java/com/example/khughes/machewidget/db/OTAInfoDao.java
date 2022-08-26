package com.example.khughes.machewidget.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.khughes.machewidget.OTAInfo;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;
import com.example.khughes.machewidget.VehicleIds;
import com.example.khughes.machewidget.VehicleInfo;

import java.util.List;

@Dao
public interface OTAInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOTAInfo(OTAInfo info);

    @Query("SELECT * FROM ota_info WHERE ota_oemCorrelationId LIKE :correlationId AND vin LIKE :VIN" )
    List<OTAInfo> findOTAStatusByCorrelationId(String correlationId, String VIN);

    @Update
    void updateOTAInfo(OTAInfo info);

//    @Query("SELECT * FROM ota_status WHERE vin LIKE :VIN")
//    VehicleInfo findVehicleInfoByVIN(String VIN);
//
//    @Query("SELECT vin FROM vehicle_info WHERE userId LIKE :userId")
//    List<String> findVINsByUserId(String userId);
//
//    @Query("SELECT * FROM vehicle_info WHERE userId LIKE :userId")
//    List<VehicleInfo> findVehicleInfoByUserId(String userId);
//
//    @Query("SELECT nickname, VIN, enabled FROM vehicle_info")
//    LiveData<List<VehicleIds>> getLiveDataVehicleInfo();
//
//    @Query("DELETE FROM vehicle_info WHERE vin LIKE :VIN")
//    void deleteVehicleInfoByVIN(String VIN);
//
//    @Query("DELETE FROM vehicle_info WHERE userId LIKE :userId")
//    void deleteVehicleInfoByUserId(String userId);
//
//    @Query("UPDATE vehicle_info SET supportsOTA = 1")
//    void updateSupportOTA();
//
//    @Query("UPDATE vehicle_info SET enabled = :value WHERE vin = :VIN")
//    void updateEnable(String VIN, boolean value);
//
}
