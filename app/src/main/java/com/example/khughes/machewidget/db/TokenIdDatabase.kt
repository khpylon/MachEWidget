package com.example.khughes.machewidget.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.khughes.machewidget.TokenId

@Database(entities = [TokenId::class], version = 4)
abstract class TokenIdDatabase : RoomDatabase() {
    abstract fun tokenIdDao(): TokenIdDao

    companion object {
        private var instance: TokenIdDatabase? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): TokenIdDatabase {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    TokenIdDatabase::class.java,
                    "tokenid_db"
                )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }
            return instance as TokenIdDatabase
        }
    }
}