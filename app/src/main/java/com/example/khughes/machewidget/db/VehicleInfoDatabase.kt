package com.example.khughes.machewidget.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.khughes.machewidget.DoorConverters
import com.example.khughes.machewidget.VehicleInfo
import java.util.concurrent.Executors

@Database(entities = [VehicleInfo::class], version = 1)
@TypeConverters(DoorConverters::class)
abstract class VehicleInfoDatabase : RoomDatabase() {
    abstract fun vehicleInfoDao(): VehicleInfoDao

    companion object {
        private var instance: VehicleInfoDatabase? = null
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): VehicleInfoDatabase {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    VehicleInfoDatabase::class.java,
                    "vehicleInfo_db"
                )
                    .fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }
            return instance as VehicleInfoDatabase
        }

        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

    }
}