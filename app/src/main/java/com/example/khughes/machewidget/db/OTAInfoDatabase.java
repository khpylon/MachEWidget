package com.example.khughes.machewidget.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.khughes.machewidget.OTAInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {OTAInfo.class}, version = 1)
public abstract class OTAInfoDatabase extends RoomDatabase {
    private static OTAInfoDatabase instance;

    public abstract OTAInfoDao otaInfoDao();

    private static class DatabaseCallback extends RoomDatabase.Callback {

        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    }

    public static synchronized OTAInfoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), OTAInfoDatabase.class, "otainfo_db")
                    .fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .addCallback(new DatabaseCallback())
                    .build();
        }
        return instance;
    }

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

}
