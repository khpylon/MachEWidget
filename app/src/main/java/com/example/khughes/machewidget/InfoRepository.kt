package com.example.khughes.machewidget

import android.content.Context
import com.example.khughes.machewidget.db.TokenIdDao
import com.example.khughes.machewidget.db.TokenIdDatabase
import com.example.khughes.machewidget.db.VehicleInfoDao
import com.example.khughes.machewidget.db.VehicleInfoDatabase

class InfoRepository internal constructor(mContext: Context) {
    private val mVehicleInfoDao: VehicleInfoDao
    private val mTokenInfoDao: TokenIdDao
    var vehicles: MutableList<VehicleInfo>
    private var mVehicleInfo: VehicleInfo? = null
    private val mTokenIdList: MutableList<TokenId>
    private var mTokenIdInfo: TokenId? = null

    init {
        mVehicleInfoDao = VehicleInfoDatabase.getInstance(mContext).vehicleInfoDao()
        mTokenInfoDao = TokenIdDatabase.getInstance(mContext).tokenIdDao()
        vehicles = mVehicleInfoDao.findVehicleInfo().toMutableList()
        mTokenIdList = mTokenInfoDao.findTokenIds().toMutableList()
        mTokenIdInfo = if (mTokenIdList.size > 0) mTokenIdList[0] else null

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

    fun getTokenId(tokenId: String?): TokenId? {
        if (tokenId != null) {
            for (tokenIdInfo in mTokenIdList) {
                if (tokenId == tokenIdInfo.tokenId) {
                    mTokenIdInfo = tokenIdInfo
                    return tokenIdInfo
                }
            }
        }
        return null
    }

    fun getActiveTokenId(): TokenId? {
        if (mTokenIdInfo == null && mTokenIdList.size > 0) {
            mTokenIdInfo = mTokenIdList.first()
        }
        return mTokenIdInfo
    }

    fun getVehicleById(vehicleId: String?): VehicleInfo {
        if (vehicleId != null) {
            for (vehicleInfo in vehicles) {
                if (vehicleId == vehicleInfo.carStatus.vehicle.vehicleId) {
                    return vehicleInfo
                }
            }
        }
        return VehicleInfo()
    }

    fun insertVehicle(info: VehicleInfo) {
        mVehicleInfo = info
        VehicleInfoDatabase.databaseWriteExecutor.execute { mVehicleInfoDao.insertVehicleInfo(info) }
        vehicles.add(info)
    }

    fun setVehicle(info: VehicleInfo) {
        mVehicleInfo = info
        VehicleInfoDatabase.databaseWriteExecutor.execute { mVehicleInfoDao.updateVehicleInfo(info) }
    }

    fun insertTokenId(info: TokenId) {
        mTokenIdInfo = info
        TokenIdDatabase.databaseWriteExecutor.execute { mTokenInfoDao.insertTokenId(info) }
        mTokenIdList.add(info)
    }

    fun setTokenId(info: TokenId) {
        mTokenIdInfo = info
        TokenIdDatabase.databaseWriteExecutor.execute { mTokenInfoDao.updateTokenId(info) }
    }
}