package com.example.khughes.machewidget

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class StoredData(private val mContext: Context) {
    // Getters/setters for specific attributes
    private fun commitWait(edit: SharedPreferences.Editor) {
        for (i in 0..9) {
            if (edit.commit()) {
                return
            }
        }
    }

    var leftAppPackage: String?
        get() {
            val pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
            return pref.getString(LEFTAPPPACKAGE, "com.ford.fordpass")
        }
        set(name) {
            val edit = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit()
            edit.putString(LEFTAPPPACKAGE, name)
            commitWait(edit)
        }
    var rightAppPackage: String?
        get() {
            val pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
            return pref.getString(RIGHTAPPPACKAGE, null)
        }
        set(name) {
            val edit = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit()
            edit.putString(RIGHTAPPPACKAGE, name)
            commitWait(edit)
        }
    var latestVersion: String?
        get() {
            val pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
            return pref.getString(LATESTVERSION, "")
        }
        set(name) {
            val edit = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit()
            edit.putString(LATESTVERSION, name)
            commitWait(edit)
        }

    fun setLastAlarmTime() {
        val edit = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit()
        val nowtime =
            LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        commitWait(edit.putLong(LASTALARMTIME, nowtime))
    }

    val lastAlarmTime: Long
        get() = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE).getLong(LASTALARMTIME, 0)
    var batteryNotification: Long
        get() = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
            .getLong(BATTERYNOTIFICATION, 0)
        set(time) {
            val edit = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit()
            commitWait(edit.putLong(BATTERYNOTIFICATION, time))
        }

    fun getCounter(key: String?): Int {
        val pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        return pref.getInt(key, 0)
    }

    fun incCounter(key: String?) {
        val pref = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        val edit = pref.edit()
        val value = pref.getInt(key, 0) + 1
        commitWait(edit.putInt(key, value))
    }

    companion object {
        const val TAG = "saveAppInfo"
        private const val LASTALARMTIME = "LastAlarmTime"
        private const val LEFTAPPPACKAGE = "LeftAppPackage"
        private const val RIGHTAPPPACKAGE = "RightAppPackage"
        private const val LATESTVERSION = "LatestVersion"
        private const val BATTERYNOTIFICATION = "BatteryOptNotificationTime"
        const val STATUS_NOT_LOGGED_IN = "Logged out"
        const val STATUS_LOG_OUT = "Log out"
        const val STATUS_LOG_IN = "Log in"
        const val STATUS_VEHICLE_INFO = "Vehicle Info"
        const val STATUS_UPDATED = "Updated"
        const val STATUS_UNKNOWN = "Unknown"

        // When the list above changes, be sure to change this function also
        val keys: ArrayList<String>
            get() = ArrayList(
                listOf(
                    LASTALARMTIME,
                    LEFTAPPPACKAGE,
                    RIGHTAPPPACKAGE,
                    LATESTVERSION,
                    BATTERYNOTIFICATION,
                    STATUS_NOT_LOGGED_IN,
                    STATUS_LOG_OUT,
                    STATUS_LOG_IN,
                    STATUS_VEHICLE_INFO,
                    STATUS_UPDATED,
                    STATUS_UNKNOWN
                )
            )
    }
}