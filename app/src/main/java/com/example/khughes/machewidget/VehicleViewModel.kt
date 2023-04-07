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

    fun setEnable(VIN: String, value: Boolean) {
        mRepository.enable(VIN, value)
    }

    fun removeVehicle(VIN: String) {
        mRepository.remove(VIN)
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
        val allVehicles: LiveData<List<VehicleIds>>

        init {
            mVehDao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
            this.allVehicles = mVehDao.liveDataVehicleInfo
        }

        fun enable(VIN: String, value: Boolean) {
            Thread { mVehDao.updateEnable(VIN, value) }.start()
        }

        fun remove(VIN: String) {
            Thread { mVehDao.deleteVehicleInfoByVIN(VIN) }.start()
        }
    }
}