package com.example.khughes.machewidget

import okhttp3.RequestBody
import com.example.khughes.machewidget.AccessToken
import com.example.khughes.machewidget.UserDetails
import retrofit2.Call
import retrofit2.http.*

interface APIMPSService {
    @Headers(
        "Content-Type: application/json",
        "Accept-Language: en-US",
        "Application-Id: " + Constants.APID,
        "Authorization: Basic ZWFpLWNsaWVudDo="
    )
    @POST("token/v2/cat-with-ci-access-token")
    fun getAccessToken(@Body token: RequestBody?): Call<AccessToken?>?

    @Headers(
        "Content-Type: application/json",
        "Accept-Language: en-US",
        "Application-Id: " + Constants.APID,
        "Authorization: Basic ZWFpLWNsaWVudDo="
    )
    @POST("token/v2/cat-with-refresh-token")
    fun refreshAccessToken(@Body token: RequestBody?): Call<AccessToken?>?

//    @Headers(
//        "Content-Type: application/json",
//        "Accept-Language: en-US",
//        "Application-Id: " + Constants.APID,
//        "Authorization: Basic ZWFpLWNsaWVudDo="
//    )
//    @GET("users")
//    fun getUserProfile(@Header("auth-token") token: String?): Call<AccessToken?>?
//
//    @Headers("locale: en-US", "user-agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110")
//    @POST("expdashboard/v1/details")
//    fun getUserDetails(
//        @Header("auth-token") token: String?,
//        @Header("application-id") APID: String?,
//        @Header("countrycode") country: String?,
//        @Body data: RequestBody?
//    ): Call<UserDetails?>?
}