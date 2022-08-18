package com.example.khughes.machewidget

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class AccessToken {
    @SerializedName("access_token")
    @Expose
    var accessToken: String? = null

    @SerializedName("refresh_token")
    @Expose
    var refreshToken: String? = null

    @SerializedName("grant_id")
    @Expose
    var grantId: String? = null

    @SerializedName("token_type")
    @Expose
    var tokenType: String? = null

    @SerializedName("expires_in")
    @Expose
    var expiresIn: Int? = null

    @SerializedName("profile")
    @Expose
    var userProfile: UserProfile? = null

    @Generated("jsonschema2pojo")
    inner class UserProfile {
        @SerializedName("userGuid")
        @Expose
        var userGuid: String? = null

        @SerializedName("country")
        @Expose
        var country: String? = null

        @SerializedName("language")
        @Expose
        var language: String? = null

        @SerializedName("uomSpeed")
        @Expose
        var uomSpeed: String? = null

        @SerializedName("uomDistance")
        @Expose
        var uomDistance: Int? = null

        @SerializedName("uomPressure")
        @Expose
        var uomPressure: String? = null
    }
}