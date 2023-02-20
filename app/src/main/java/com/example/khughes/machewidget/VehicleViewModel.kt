package com.example.khughes.machewidget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.khughes.machewidget.db.VehicleInfoDao
import com.example.khughes.machewidget.db.VehicleInfoDatabase

class VehicleViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val mRepository: VehicleRepository
    val allVehicles: LiveData<List<VehicleIds>>

    init {
        mRepository = VehicleRepository(application)
        allVehicles = mRepository.allVehicles
    }

    fun setEnable(VIN: String?, value: Boolean) {
        mRepository.enable(VIN, value)
    }

    fun countEnabledVehicle(): Int {
        var count = 0
        for (id in allVehicles.value!!) {
            if (id.isEnabled) {
                ++count
            }
        }
        return count
    }

    internal inner class VehicleRepository(application: Application?) {
        private val mVehDao: VehicleInfoDao
        val allVehicles: LiveData<List<VehicleIds>>

        init {
            mVehDao = VehicleInfoDatabase.getInstance(application).vehicleInfoDao()
            this.allVehicles = mVehDao.liveDataVehicleInfo
        }

        fun enable(VIN: String?, value: Boolean) {
            Thread { mVehDao.updateEnable(VIN, value) }.start()
        }
    }
}