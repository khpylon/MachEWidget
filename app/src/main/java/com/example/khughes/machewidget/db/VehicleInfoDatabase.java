package com.example.khughes.machewidget.db;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.khughes.machewidget.CarStatus.CarStatus;
import com.example.khughes.machewidget.VehicleInfo;

import java.util.concurrent.Semaphore;

@Database(entities = VehicleInfo.class, version = 1)
public abstract class VehicleInfoDatabase extends RoomDatabase {
    private static VehicleInfoDatabase instance;

    public static synchronized VehicleInfoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), VehicleInfoDatabase.class, "vehicleinfo_db")
                    .fallbackToDestructiveMigration().setJournalMode(JournalMode.TRUNCATE).build();
        }
        return instance;
    }

    public abstract VehicleInfoDao vehicleInfoDao();
}
