package com.example.khughes.machewidget

import androidx.room.Entity
import androidx.room.PrimaryKey

// Token IDs are used to handle the tokens necessary to access API endpoints.  The current FordConnect API returns one
// vehicle per authentication so multiple vehicles require multiple authentications, each with unique tokens that must
// be used and maintained.  The API also suggests that at some point multiple vehicles with a single authentication,
// which would require coordination between the vehicles to know the current access and refresh tokens.  This database
// is used to store the information received from a single authentication session.
//
// For each session, a unique "token ID" string is created.  That string is stored for each vehicle found during
// that session through the Vehicle List endpoint.  When an API access is performed for a vehicle, the token ID is used
// to locate and manage that token.  As long as each vehicle retrieves the tokens from the database immediately before
// an API access, the correct values will be used.

@Entity(tableName = "tokenid_info")
class TokenId {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    var tokenId: String? = ""
    var programState: String? = Constants.STATE_INITIAL_STATE
    var accessToken: String? = ""
    var refreshToken: String? = ""
    var expiresIn: Long = 0
    var users = 0
}