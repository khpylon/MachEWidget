package com.example.khughes.machewidget.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.khughes.machewidget.VehicleInfo
import java.util.concurrent.Executors

@Database(entities = [VehicleInfo::class], version = 8)
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
                    "vehicleinfo_db"
                )
                    .addMigrations(
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                    )
                    .fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }
            return instance as VehicleInfoDatabase
        }

        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN initialForcedRefreshTime INTEGER DEFAULT 0 NOT NULL"
                )
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN lastForcedRefreshTime INTEGER DEFAULT 0 NOT NULL"
                )
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN forcedRefreshCount INTEGER DEFAULT 0 NOT NULL"
                )
            }
        }
        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN colorValue INTEGER DEFAULT 0xffffffff NOT NULL"
                )
            }
        }
        private val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN chargeHour INTEGER DEFAULT 0 NOT NULL"
                )
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN chargeThresholdLevel INTEGER DEFAULT 7 NOT NULL"
                )
            }
        }
        private val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN car_chargePower REAL DEFAULT 0 NOT NULL"
                )
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN car_chargeEnergy REAL DEFAULT 0 NOT NULL"
                )
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN car_chargeType TEXT DEFAULT '' NOT NULL"
                )
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN car_initialDte REAL DEFAULT 0 NOT NULL"
                )
            }
        }
        private val MIGRATION_7_8: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN car_ureaRange_value TEXT DEFAULT '' NOT NULL"
                )
                database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN car_exhaustFluidLevel_value TEXT DEFAULT '' NOT NULL"
                )
            }
        }

    }
}