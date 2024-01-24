package com.example.khughes.machewidget.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.khughes.machewidget.UserInfo

@Database(entities = [UserInfo::class], version = 4)
abstract class UserInfoDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao

    companion object {
        private var instance: UserInfoDatabase? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): UserInfoDatabase {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    UserInfoDatabase::class.java,
                    "userinfo_db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
//                    .addMigrations(MIGRATION_4_3)
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }
            return instance as UserInfoDatabase
        }

        private const val newTable =
            "CREATE TABLE `new_user_info` ( `id` INTEGER NOT NULL, `userId` TEXT, " +
                    "`programState` TEXT, `accessToken` TEXT, `refreshToken` TEXT, " +
                    "`expiresIn` INTEGER NOT NULL, `country` TEXT, `language` TEXT, `uomSpeed` TEXT, " +
                    "`uomDistance` INTEGER NOT NULL, `uomPressure` TEXT, `lastModified` TEXT, " +
                    "PRIMARY KEY(`id`)) "
        private const val fields =
            "`userId`, `programState`, `accessToken`, `refreshToken`, `expiresIn`, " +
                    "`country`, `language`, `uomSpeed`, `uomDistance`, `uomPressure`, `lastModified`"
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(newTable)
                // Copy the data
                database.execSQL(
                    "INSERT INTO new_user_info ($fields) SELECT $fields FROM user_info"
                )
                // Remove the old table
                database.execSQL("DROP TABLE user_info")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE new_user_info RENAME TO user_info")
            }
        }

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column
                database.execSQL(
                    "ALTER TABLE user_info ADD COLUMN autoAccessToken TEXT"
                )
                database.execSQL(
                    "ALTER TABLE user_info ADD COLUMN autoRefreshToken TEXT"
                )
                database.execSQL(
                    "ALTER TABLE user_info ADD COLUMN autoExpiresIn INTEGER DEFAULT 0 NOT NULL"
                )
            }
        }

//        private val MIGRATION_4_3: Migration = object : Migration(4, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Create the new table
//                database.execSQL(newTable)
//                // Copy the data
//                database.execSQL(
//                    "INSERT INTO new_user_info ($fields) SELECT $fields FROM user_info"
//                )
//                // Remove the old table
//                database.execSQL("DROP TABLE user_info")
//                // Change the table name to the correct one
//                database.execSQL("ALTER TABLE new_user_info RENAME TO user_info")
//            }
//        }

    }
}