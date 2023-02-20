package com.example.khughes.machewidget

import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import com.example.khughes.machewidget.AccessToken
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.Headers

interface AccessTokenService {
    @FormUrlEncoded
    @Headers(
        "Content-Type: application/x-www-form-urlencoded",
        "Accept-Language: en-US",
        "Authorization: Basic ZWFpLWNsaWVudDo="
    )
    @POST("token")
    fun getAccessToken(
        @Field("client_id") client: String?,
        @Field("grant_type") grant: String?,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Call<AccessToken?>?
}