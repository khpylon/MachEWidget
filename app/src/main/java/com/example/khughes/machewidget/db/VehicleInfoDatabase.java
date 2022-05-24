package com.example.khughes.machewidget.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.khughes.machewidget.VehicleInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = VehicleInfo.class, version = 2)
public abstract class VehicleInfoDatabase extends RoomDatabase {
    private static VehicleInfoDatabase instance;

    public static synchronized VehicleInfoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), VehicleInfoDatabase.class, "vehicleinfo_db")
                    .addMigrations(MIGRATION_1_2)
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build();
        }
        return instance;
    }

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static void closeInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public abstract VehicleInfoDao vehicleInfoDao();

    private static final String newTable = "CREATE TABLE `new_vehicle_info` (`id` INTEGER NOT NULL, `VIN` TEXT, `userId` TEXT, `nickname` TEXT, `lastRefreshTime` INTEGER NOT NULL, " +
            "`lastUpdateTime` INTEGER NOT NULL, `lastLVBStatus` TEXT, `lastTPMSStatus` TEXT, `lastDTE` REAL NOT NULL, `lastFuelLevel` REAL NOT NULL, " +
            "`supportsOTA` INTEGER NOT NULL, `otaAlertStatus` TEXT, `car_version` TEXT, `car_status` INTEGER, `car_lastRefresh` TEXT, `car_lastModifiedDate` TEXT, " +
            "`car_lockstatus_value` TEXT, `car_alarm_value` TEXT, `car_odometer_value` REAL, `car_fuelLevel` REAL, `car_distanceToEmpty` REAL, " +
            "`car_latitude` TEXT, `car_longitude` TEXT, `car_gpsState` TEXT, `car_remoteStartDuration` INTEGER, `car_remoteStartTime` INTEGER, " +
            "`car_remotestartstatus_value` INTEGER, `car_batteryhealth_value` TEXT, `car_batterystatusactual_value` INTEGER, `car_leftfronttirestatus_value` TEXT, " +
            "`car_leftfronttirepressure_value` TEXT, `car_rightfronttirestatus_value` TEXT, `car_rightfronttirepressure_value` TEXT, " +
            "`car_outerleftreartirestatus_value` TEXT, `car_outerleftreartirepressure_value` TEXT, `car_outerrightreartirestatus_value` TEXT, " +
            "`car_outerrightreartirepressure_value` TEXT, `car_deepsleep_value` INTEGER, `car_batteryfilllevel_value` REAL, `car_elvehdte_value` REAL, " +
            "`car_chargingstatus_value` TEXT, `car_plugstatus_value` INTEGER, `car_chargeendtime_value` TEXT, `car_driverwindow_value` TEXT, " +
            "`car_passwindow_value` TEXT, `car_reardriverwindow_value` TEXT, `car_rearpasswindow_value` TEXT, `car_rightreardoor_value` TEXT, " +
            "`car_leftreardoor_value` TEXT, `car_driverdoor_value` TEXT, `car_passengerdoor_value` TEXT, `car_hooddoor_value` TEXT, `car_tailgate_value` TEXT, " +
            "`car_ignitionstatus_value` TEXT, `ota_oemCorrelationId` TEXT, `ota_deploymentId` TEXT, `ota_deploymentCreationDate` TEXT, " +
            "`ota_deploymentExpirationTime` TEXT, `ota_otaTriggerExpirationTime` TEXT, `ota_communicationPriority` TEXT, `ota_type` TEXT, `ota_triggerType` TEXT, " +
            "`ota_inhibitRequired` INTEGER, `ota_additionalConsentLevel` INTEGER, `ota_tmcEnvironment` TEXT, `ota_deploymentFinalConsumerAction` TEXT, " +
            "`ota_aggregateStatus` TEXT, `ota_detailedStatus` TEXT, `ota_dateTimestamp` TEXT, `ota_language` TEXT, `ota_languageCode` TEXT, " +
            "`ota_languageCodeMobileApp` TEXT, `ota_text` TEXT, " +
            "PRIMARY KEY(`id`)) ";

    private static final String fields = "`VIN`, `userId`, `nickname`, `lastRefreshTime`, `lastUpdateTime`, " +
            "`lastLVBStatus`, `lastTPMSStatus`, `lastDTE`, `lastFuelLevel`, " +
            "`supportsOTA`, `otaAlertStatus`, `car_version`, `car_status`, " +
            "`car_lastRefresh`, `car_lastModifiedDate`, `car_lockstatus_value`, " +
            "`car_alarm_value`, `car_odometer_value`, `car_fuelLevel`, " +
            "`car_distanceToEmpty`, `car_latitude`, `car_longitude`, " +
            "`car_gpsState`, `car_remoteStartDuration`, `car_remoteStartTime`, " +
            "`car_remotestartstatus_value`, `car_batteryhealth_value`, " +
            "`car_batterystatusactual_value`, `car_leftfronttirestatus_value`, " +
            "`car_leftfronttirepressure_value`, `car_rightfronttirestatus_value`, " +
            "`car_rightfronttirepressure_value`, `car_outerleftreartirestatus_value`, " +
            "`car_outerleftreartirepressure_value`, " +
            "`car_outerrightreartirestatus_value`, " +
            "`car_outerrightreartirepressure_value`, `car_deepsleep_value`, " +
            "`car_batteryfilllevel_value`, `car_elvehdte_value`, " +
            "`car_chargingstatus_value`, `car_plugstatus_value`, " +
            "`car_chargeendtime_value`, `car_driverwindow_value`, " +
            "`car_passwindow_value`, `car_reardriverwindow_value`, " +
            "`car_rearpasswindow_value`, `car_rightreardoor_value`, " +
            "`car_leftreardoor_value`, `car_driverdoor_value`, " +
            "`car_passengerdoor_value`, `car_hooddoor_value`, `car_tailgate_value`, " +
            "`car_ignitionstatus_value`, `ota_oemCorrelationId`, `ota_deploymentId`, " +
            "`ota_deploymentCreationDate`, `ota_deploymentExpirationTime`, " +
            "`ota_otaTriggerExpirationTime`, `ota_communicationPriority`, `ota_type`, " +
            "`ota_triggerType`, `ota_inhibitRequired`, `ota_additionalConsentLevel`, " +
            "`ota_tmcEnvironment`, `ota_deploymentFinalConsumerAction`, " +
            "`ota_aggregateStatus`, `ota_detailedStatus`, `ota_dateTimestamp`, " +
            "`ota_language`, `ota_languageCode`, `ota_languageCodeMobileApp`, `ota_text`";

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(newTable);
            // Copy the data
            database.execSQL(
                    "INSERT INTO new_vehicle_info (" + fields + ") SELECT " + fields + " FROM vehicle_info");
            // Remove the old table
            database.execSQL("DROP TABLE vehicle_info");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE new_vehicle_info RENAME TO vehicle_info");
            // Add new column
            database.execSQL(
                    "ALTER TABLE vehicle_info ADD COLUMN enabled INTEGER DEFAULT 1 NOT NULL");
        }
    };
}
