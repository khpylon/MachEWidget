package com.example.khughes.machewidget

import retrofit2.Call
import retrofit2.http.*

interface OAuth2Service {

    @FormUrlEncoded
    @POST("914d88b1-3523-4bf6-9be4-1b96b4f6f919/oauth2/v2.0/token")
    fun getAccessToken(
        @Query("p") token: String,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_url") redirectURL: String
    ): Call<AccessToken?>?

    @FormUrlEncoded
    @POST("914d88b1-3523-4bf6-9be4-1b96b4f6f919/oauth2/v2.0/token")
    fun refreshAccessToken(
        @Query("p") token: String?,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String
    ): Call<AccessToken?>?

}