package com.example.khughes.machewidget.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.khughes.machewidget.UserInfo;

@Database(entities = UserInfo.class, version = 2)
public abstract class UserInfoDatabase extends RoomDatabase {
    private static UserInfoDatabase instance;

    public static synchronized UserInfoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), UserInfoDatabase.class, "userinfo_db")
                    .addMigrations(MIGRATION_1_2)
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build();
        }
        return instance;
    }

    public static void closeInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public abstract UserInfoDao userInfoDao();

    private static final String newTable = "CREATE TABLE `new_user_info` ( `id` INTEGER NOT NULL, `userId` TEXT, " +
            "`username` TEXT, `password` TEXT, `programState` TEXT, `accessToken` TEXT, `refreshToken` TEXT, " +
            "`expiresIn` INTEGER NOT NULL, `country` TEXT, `language` TEXT, `uomSpeed` TEXT, " +
            "`uomDistance` INTEGER NOT NULL, `uomPressure` TEXT, `lastModified` TEXT, " +
            "`sparetext1` TEXT, `sparetext2` TEXT, `sparetext3` TEXT, " +
            "`spareint1` INTEGER, `spareint2` INTEGER, `spareint3` INTEGER, " +
            "PRIMARY KEY(`id`)) ";

    private static final String fields = "`userId`, `username`, `password`, `programState`, `accessToken`, `refreshToken`, `expiresIn`, " +
            "`country`, `language`, `uomSpeed`, `uomDistance`, `uomPressure`, `lastModified`";

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(newTable);
            // Copy the data
            database.execSQL(
                    "INSERT INTO new_user_info (" + fields + ") SELECT " + fields + " FROM user_info");
            // Remove the old table
            database.execSQL("DROP TABLE user_info");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE new_user_info RENAME TO user_info");
        }
    };
}
