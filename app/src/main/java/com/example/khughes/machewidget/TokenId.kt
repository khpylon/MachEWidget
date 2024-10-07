package com.example.khughes.machewidget

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokenid_info")
class TokenId {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    var tokenId: String = ""
    var programState: String = Constants.STATE_INITIAL_STATE
    var accessToken: String = ""
    var refreshToken: String = ""
    var expiresIn: Long = 0
    var users: Long = 0
}