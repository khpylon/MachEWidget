package com.example.khughes.machewidget

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
class UserInfo {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var userId: String? = ""
    var programState: String? = Constants.STATE_INITIAL_STATE
    var accessToken: String? = ""
    var refreshToken: String? = ""
    var expiresIn: Long = 0
    var country: String? = ""
    var language: String? = ""
    var uomSpeed: String? = ""
    var uomDistance = 0
    var uomPressure: String? = ""
    var lastModified: String? = ""
}