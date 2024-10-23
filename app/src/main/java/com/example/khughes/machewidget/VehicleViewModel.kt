package com.example.khughes.machewidget

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.khughes.machewidget.db.VehicleInfoDao
import com.example.khughes.machewidget.db.VehicleInfoDatabase

class VehicleViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val mRepository: VehicleRepository
    val allVehicles: LiveData<List<VehicleIds>>

    init {
        mRepository = VehicleRepository(application?.applicationContext as Context)
        allVehicles = mRepository.allVehicles
    }

    fun setEnable(vehicleId: String, value: Boolean) {
        mRepository.enable(vehicleId, value)
    }

    fun setModel(vehicleId: String, value: Vehicle.Companion.Model) {
        mRepository.setModel(vehicleId, value)
    }

    fun removeVehicle(vehicleId: String) {
        mRepository.remove(vehicleId)
    }

    fun countEnabledVehicle(): Int {
        var count = 0
        for (id in allVehicles.value!!) {
            if (id.enabled) {
                ++count
            }
        }
        return count
    }

    internal inner class VehicleRepository(context: Context) {
        private val mVehDao: VehicleInfoDao
        private var mContext: Context
        val allVehicles: LiveData<List<VehicleIds>>

        init {
            mVehDao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
            this.allVehicles = mVehDao.liveDataVehicleInfo
            mContext = context
        }

        fun enable(vehicleId: String, value: Boolean) {
            Thread { mVehDao.updateEnable(vehicleId, value) }.start()
        }

        fun setModel(vehicleId: String, value: Vehicle.Companion.Model) {
            Thread { mVehDao.updateModel(vehicleId, value) }.start()
        }

        fun remove(vehicleId: String) {
            Thread {
                mVehDao.deleteVehicleInfoByVehicleId(vehicleId)
                // Force recalculation of electric vehicles
                val info = InfoRepository(mContext)
                info.vehicles.size
            }.start()
        }
    }
}