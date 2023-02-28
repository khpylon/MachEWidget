package com.example.khughes.machewidget

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.LogFile.e
import com.example.khughes.machewidget.db.VehicleInfoDao
import com.example.khughes.machewidget.db.UserInfoDao
import com.example.khughes.machewidget.db.VehicleInfoDatabase
import com.example.khughes.machewidget.db.UserInfoDatabase

class InfoRepository internal constructor(mContext: Context) {
    private val mVehicleInfoDao: VehicleInfoDao
    private val mUserInfoDao: UserInfoDao
    val vehicles: List<VehicleInfo>
    private var mVehicleInfo: VehicleInfo? = null
    private val mUserList: List<UserInfo>
    var user: UserInfo

    init {
        mVehicleInfoDao = VehicleInfoDatabase.getInstance(mContext).vehicleInfoDao()
        mUserInfoDao = UserInfoDatabase.getInstance(mContext).userInfoDao()
        vehicles = mVehicleInfoDao.findVehicleInfo()
        mUserList = mUserInfoDao.findUserInfo()
        val userId = PreferenceManager.getDefaultSharedPreferences(
            mContext
        ).getString(mContext.resources.getString(R.string.userId_key), null)
        user = UserInfo()
        if (userId != null) {
            user = mUserInfoDao.findUserInfo(userId) ?: UserInfo()
        } else {
            e(
                mContext,
                MainActivity.CHANNEL_ID,
                "InfoRepository(): default settings userId is null"
            )
            val users = mUserInfoDao.findUserInfo()
            if (users.size > 0) {
                user = users[0]
                e(
                    mContext,
                    MainActivity.CHANNEL_ID,
                    "InfoRepository(): fallback to userId " + user.userId
                )
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

    fun setVehicle(info: VehicleInfo?) {
        mVehicleInfo = info
        VehicleInfoDatabase.databaseWriteExecutor.execute { mVehicleInfoDao.updateVehicleInfo(info) }
    }
}