package com.example.khughes.machewidget.db

import androidx.room.*
import com.example.khughes.machewidget.OTAInfo

@Dao
interface OTAInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOTAInfo(info: OTAInfo?)

    @Query(
        "SELECT * FROM ota_info WHERE ota_oemCorrelationId LIKE :correlationId AND vin LIKE :VIN "
                + "ORDER BY ota_dateTimestamp ASC"
    )
    fun findOTAInfoByCorrelationId(correlationId: String?, VIN: String?): List<OTAInfo?>?

    @Update
    fun updateOTAInfo(info: OTAInfo?)
}