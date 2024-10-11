package com.example.khughes.machewidget

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.db.TokenIdDao
import com.example.khughes.machewidget.db.TokenIdDatabase
import com.example.khughes.machewidget.db.VehicleInfoDao
import com.example.khughes.machewidget.db.UserInfoDao
import com.example.khughes.machewidget.db.VehicleInfoDatabase
import com.example.khughes.machewidget.db.UserInfoDatabase

class InfoRepository internal constructor(mContext: Context) {
    private val mVehicleInfoDao: VehicleInfoDao
    private val mTokenInfoDao: TokenIdDao
    val vehicles: List<VehicleInfo>
    private var mVehicleInfo: VehicleInfo? = null
    private val mTokenIdList: List<TokenId>
    private var mTokenIdInfo: TokenId? = null

    init {
        mVehicleInfoDao = VehicleInfoDatabase.getInstance(mContext).vehicleInfoDao()
        mTokenInfoDao = TokenIdDatabase.getInstance(mContext).tokenIdDao()
        vehicles = mVehicleInfoDao.findVehicleInfo()
        mTokenIdList = mTokenInfoDao.findTokenIds()

        // Check if any vehicles are electric
        val appInfo = StoredData(mContext)
        appInfo.electricVehicles = false
        for (vehicleInfo in vehicles) {
            val carStatus = vehicleInfo.carStatus
            if (carStatus.isPropulsionElectric()) {
                appInfo.electricVehicles = true
                break
            }
        }
    }

    fun getVehicleByVIN(VIN: String?): VehicleInfo {
        if (VIN != null) {
            for (vehicleInfo in vehicles) {
                if (VIN == vehicleInfo.vin) {
                    return vehicleInfo
                }
            }
        }
        return VehicleInfo()
    }

    fun setVehicle(info: VehicleInfo) {
        mVehicleInfo = info
        VehicleInfoDatabase.databaseWriteExecutor.execute { mVehicleInfoDao.updateVehicleInfo(info) }
    }

    fun setTokenId(info: TokenId) {
        mTokenIdInfo = info
        TokenIdDatabase.databaseWriteExecutor.execute { mTokenInfoDao.updateTokenId(info) }
    }
}