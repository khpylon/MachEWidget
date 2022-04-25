package com.example.khughes.machewidget.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.khughes.machewidget.CarStatus.CarStatus;
import com.example.khughes.machewidget.VehicleInfo;

import java.util.List;

@Dao
public interface VehicleInfoDao {
    @Insert
    void insertVehicleInfo(VehicleInfo info);

    @Query("SELECT * FROM vehicle_info WHERE vin LIKE :VIN")
    VehicleInfo findVehicleInfoByVIN(String VIN);

    @Query("SELECT * FROM vehicle_info WHERE userId LIKE :userId")
    List<VehicleInfo> findVehicleInfoByUserId(String userId);

    @Query("DELETE FROM vehicle_info WHERE vin LIKE :VIN")
    void deleteVehicleInfoByVIN(String VIN);

    @Query("DELETE FROM vehicle_info WHERE userId LIKE :userId")
    void deleteVehicleInfoByUserId(String userId);

    @Update
    void updateVehicleInfo(VehicleInfo info);

    @Update(entity = VehicleInfo.class)
    void updateCarStatus(VehicleInfo.CarStatusInfo info);

}
