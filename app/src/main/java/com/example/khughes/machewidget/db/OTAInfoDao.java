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

    @Query("SELECT * FROM ota_info WHERE ota_oemCorrelationId LIKE :correlationId AND vin LIKE :VIN "
            + "ORDER BY ota_dateTimestamp ASC" )
    List<OTAInfo> findOTAInfoByCorrelationId(String correlationId, String VIN);

    @Update
    void updateOTAInfo(OTAInfo info);
}
