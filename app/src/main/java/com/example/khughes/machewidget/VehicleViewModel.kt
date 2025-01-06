package com.example.khughes.machewidget

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.khughes.machewidget.db.VehicleInfoDao
import com.example.khughes.machewidget.db.VehicleInfoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class VehicleViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val mRepository: VehicleRepository = VehicleRepository(application?.applicationContext as Context)
    val allVehicles: Flow<List<VehicleIds>>

    init {
        allVehicles = mRepository.allVehicles
    }

    fun setEnable(vehicleId: String, value: Boolean) {
        mRepository.enable(vehicleId, value)
    }

    fun setModel(vehicleId: String, value: Vehicle.Companion.Model) {
        viewModelScope.launch {
            mRepository.setModel(vehicleId, value)
        }
    }

    fun removeVehicle(vehicleId: String) {
        mRepository.remove(vehicleId)
    }

    internal inner class VehicleRepository(context: Context) {
        private val mVehDao: VehicleInfoDao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
        private var mContext: Context
        var allVehicles: Flow<List<VehicleIds>>

        init {
            allVehicles = mVehDao.liveDataVehicleInfo
            mContext = context
        }

        fun enable(vehicleId: String, value: Boolean) {
            viewModelScope.launch {
                mVehDao.updateEnable(vehicleId, value)
            }
        }

        fun setModel(vehicleId: String, value: Vehicle.Companion.Model) {
            viewModelScope.launch(Dispatchers.IO) {
                mVehDao.updateModel(vehicleId, value)
//                mVehDao.liveDataVehicleInfo.collectLatest {
//                    allVehicles = it
//                }
            }
        }

        fun remove(vehicleId: String) {
            viewModelScope.launch{
                mVehDao.deleteVehicleInfoByVehicleId(vehicleId)
                allVehicles = mVehDao.liveDataVehicleInfo
                // Force recalculation of electric vehicles
                val info = InfoRepository(mContext)
                info.vehicles.size
            }
        }
    }
}