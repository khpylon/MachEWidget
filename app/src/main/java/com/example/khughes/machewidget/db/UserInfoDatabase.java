package com.example.khughes.machewidget.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.khughes.machewidget.UserInfo;
import com.example.khughes.machewidget.VehicleInfo;

@Database(entities = UserInfo.class, version = 1)
public abstract class UserInfoDatabase extends RoomDatabase {
    private static UserInfoDatabase instance;

    public static synchronized UserInfoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), UserInfoDatabase.class, "userinfo_db")
                    .fallbackToDestructiveMigration().setJournalMode(JournalMode.TRUNCATE).build();
        }
        return instance;
    }

    public abstract UserInfoDao userInfoDao();
}
