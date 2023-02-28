package com.example.khughes.machewidget.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.khughes.machewidget.OTAInfo
import java.util.concurrent.Executors

@Database(entities = [OTAInfo::class], version = 1)
abstract class OTAInfoDatabase : RoomDatabase() {
    abstract fun otaInfoDao(): OTAInfoDao?
    private class DatabaseCallback : Callback() {
    }

    companion object {
        private var instance: OTAInfoDatabase? = null
        @Synchronized
        fun getInstance(context: Context): OTAInfoDatabase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    OTAInfoDatabase::class.java,
                    "otainfo_db"
                )
                    .fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .addCallback(DatabaseCallback())
                    .build()
            }
            return instance
        }

        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
    }
}