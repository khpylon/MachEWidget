package com.example.khughes.machewidget

import android.app.Application
import android.content.Context
import com.example.khughes.machewidget.db.TokenIdDao
import com.example.khughes.machewidget.db.TokenIdDatabase
import com.example.khughes.machewidget.db.VehicleInfoDao
import com.example.khughes.machewidget.db.VehicleInfoDatabase

class FordStatusWidget : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}

class Repository(
    private val vehicleDao: VehicleInfoDao,
    private val tokenIdDao: TokenIdDao,
) {
    val vehicles = vehicleDao.liveDataVehicleInfo

    suspend fun addVehicleInfo(info: VehicleInfo)
    {
        vehicleDao.insertVehicleInfo(info)
    }

    suspend fun removeVehicleInfo(info: VehicleInfo)
    {
        vehicleDao.deleteVehicleInfoByVehicleId(info.carStatus.vehicle.vehicleId)
    }

    suspend fun updateVehicleInfo(info: VehicleInfo)
    {
        vehicleDao.updateVehicleInfo(info)
    }
}

object Graph {
    private lateinit var vehicleDb: VehicleInfoDatabase
    private lateinit var tokenIdDb: TokenIdDatabase

    val repository by lazy {
        Repository(
            vehicleDao = vehicleDb.vehicleInfoDao(),
            tokenIdDao = tokenIdDb.tokenIdDao()
        )
    }

    fun provide(context: Context) {
        vehicleDb = VehicleInfoDatabase.getInstance(context)
        tokenIdDb = TokenIdDatabase.getInstance(context)
    }
}