package com.example.khughes.machewidget.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.khughes.machewidget.VehicleInfo
import java.util.concurrent.Executors

@Database(entities = [VehicleInfo::class], version = 9)
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
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                    )
                    .fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }
            return instance as VehicleInfoDatabase
        }

        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

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

        private val MIGRATION_7_8: Migration = object : Migration(7,8) {
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

        private const val newTable =
            "CREATE TABLE `new_vehicle_info` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `VIN` TEXT, " +
                    "`userId` TEXT, `nickname` TEXT, `lastRefreshTime` INTEGER NOT NULL, " +
                    "`lastUpdateTime` INTEGER NOT NULL, `lastOTATime` INTEGER NOT NULL DEFAULT 0, " +
                    "`lastLVBStatus` TEXT, `lastTPMSStatus` TEXT, " +
                    "`lastChargeStatus` TEXT NOT NULL DEFAULT '', `lastDTE` REAL NOT NULL, " +
                    "`lastFuelLevel` REAL NOT NULL, `supportsOTA` INTEGER NOT NULL, " +
                    "`initialForcedRefreshTime` INTEGER NOT NULL DEFAULT 0, " +
                    "`lastForcedRefreshTime` INTEGER NOT NULL DEFAULT 0, " +
                    "`forcedRefreshCount` INTEGER NOT NULL DEFAULT 0, " +
                    "`enabled` INTEGER NOT NULL DEFAULT 1, " +
                    "`colorValue` INTEGER NOT NULL DEFAULT 0xffffffff, " +
                    "`chargeHour` INTEGER NOT NULL DEFAULT 0, " +
                    "`chargeThresholdLevel` INTEGER NOT NULL DEFAULT 7, " +
                    "`otaAlertStatus` TEXT, `car_version` TEXT, `car_status` INTEGER, " +
                    "`car_lastRefresh` TEXT, `car_lastModifiedDate` TEXT, " +
                    "`car_chargePower` REAL NOT NULL DEFAULT 0, " +
                    "`car_chargeEnergy` REAL NOT NULL DEFAULT 0, " +
                    "`car_chargeType` TEXT NOT NULL DEFAULT '', " +
                    "`car_initialDte` REAL NOT NULL DEFAULT 0, `car_alarm_value` TEXT, " +
                    "`car_lockstatus_value` TEXT, `car_odometer_value` REAL, " +
                    "`car_fuelLevel` REAL, `car_distanceToEmpty` REAL, `car_latitude` TEXT, " +
                    "`car_longitude` TEXT, `car_gpsState` TEXT, `car_remoteStartDuration` INTEGER, " +
                    "`car_remoteStartTime` INTEGER, `car_remotestartstatus_value` INTEGER, " +
                    "`car_deepsleep_value` INTEGER, `car_batteryfilllevel_value` REAL, " +
                    "`car_elvehdte_value` REAL, `car_chargingstatus_value` TEXT, " +
                    "`car_plugstatus_value` INTEGER, `car_chargeendtime_value` TEXT, " +
                    "`car_ignitionstatus_value` TEXT, `car_driverwindow_value` TEXT, " +
                    "`car_passwindow_value` TEXT, `car_reardriverwindow_value` TEXT, " +
                    "`car_rearpasswindow_value` TEXT, `car_driverdoor_value` TEXT, " +
                    "`car_passengerdoor_value` TEXT, `car_rightreardoor_value` TEXT, " +
                    "`car_leftreardoor_value` TEXT, `car_hooddoor_value` TEXT, " +
                    "`car_tailgate_value` TEXT, `car_batteryhealth_value` TEXT, " +
                    "`car_batterystatusactual_value` REAL, `batterystatusactual_percent` REAL, " +
                    "`car_leftfronttirepressure_value` TEXT, `car_rightfronttirestatus_value` TEXT, " +
                    "`car_rightfronttirepressure_value` TEXT, " +
                    "`car_outerleftreartirestatus_value` TEXT, " +
                    "`car_outerleftreartirepressure_value` TEXT, " +
                    "`car_outerrightreartirestatus_value` TEXT, " +
                    "`car_outerrightreartirepressure_value` TEXT, " +
                    "`car_exhaustFluidLevel_value` TEXT, `car_ureaRange_value` TEXT, " +
                    "`ota_oemCorrelationId` TEXT, `ota_deploymentId` TEXT, " +
                    "`ota_deploymentCreationDate` TEXT, `ota_deploymentExpirationTime` TEXT, " +
                    "`ota_otaTriggerExpirationTime` TEXT, `ota_communicationPriority` TEXT, " +
                    "`ota_type` TEXT, `ota_triggerType` TEXT, `ota_inhibitRequired` INTEGER, " +
                    "`ota_additionalConsentLevel` INTEGER, `ota_tmcEnvironment` TEXT, " +
                    "`ota_deploymentFinalConsumerAction` TEXT, `ota_aggregateStatus` TEXT, " +
                    "`ota_detailedStatus` TEXT, `ota_dateTimestamp` TEXT, `ota_language` TEXT," +
                    " `ota_languageCode` TEXT, `ota_languageCodeMobileApp` TEXT, `ota_text` TEXT)"
        private const val fields = "`id`, `VIN`, `userId`, `nickname`, `lastRefreshTime`, `lastUpdateTime`, `lastOTATime`, " +
                "`lastLVBStatus`, `lastTPMSStatus`, `lastChargeStatus`, `lastDTE`, `lastFuelLevel`, " +
                "`supportsOTA`, `initialForcedRefreshTime`, `lastForcedRefreshTime`, `forcedRefreshCount`, " +
                "`enabled`, `colorValue`, `chargeHour`, `chargeThresholdLevel`, `otaAlertStatus`, " +
                "`car_version`, `car_status`, `car_lastRefresh`, `car_lastModifiedDate`, `car_chargePower`, " +
                "`car_chargeEnergy`, `car_chargeType`, `car_initialDte`, `car_alarm_value`, `car_lockstatus_value`, " +
                "`car_odometer_value`, `car_fuelLevel`, `car_distanceToEmpty`, `car_latitude`, `car_longitude`, " +
                "`car_gpsState`, `car_remoteStartDuration`, `car_remoteStartTime`, `car_remotestartstatus_value`, " +
                "`car_deepsleep_value`, `car_batteryfilllevel_value`, `car_elvehdte_value`, " +
                "`car_chargingstatus_value`, `car_plugstatus_value`, `car_chargeendtime_value`, " +
                "`car_ignitionstatus_value`, `car_driverwindow_value`, `car_passwindow_value`, " +
                "`car_reardriverwindow_value`, `car_rearpasswindow_value`, `car_driverdoor_value`, " +
                "`car_passengerdoor_value`, `car_rightreardoor_value`, `car_leftreardoor_value`, " +
                "`car_hooddoor_value`, `car_tailgate_value`, `car_batteryhealth_value`, " +
                "`car_batterystatusactual_value`, " +
                "`car_leftfronttirestatus_value`, `car_leftfronttirepressure_value`, " +
                "`car_rightfronttirestatus_value`, `car_rightfronttirepressure_value`, `car_outerleftreartirestatus_value`, " +
                "`car_outerleftreartirepressure_value`, `car_outerrightreartirestatus_value`, " +
                "`car_outerrightreartirepressure_value`, `car_exhaustFluidLevel_value`, `car_ureaRange_value`"

        private val MIGRATION_8_9: Migration = object : Migration(8,9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(newTable)
                // Copy the data
                database.execSQL(
                    "INSERT INTO new_vehicle_info (${fields}) SELECT ${fields} FROM vehicle_info"
                )
                // Remove the old table
                database.execSQL("DROP TABLE vehicle_info")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE new_vehicle_info RENAME TO vehicle_info")
            }
        }
        private val MIGRATION_9_10: Migration = object : Migration(9,10) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
    }
}