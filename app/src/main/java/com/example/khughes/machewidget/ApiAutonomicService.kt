package com.example.khughes.machewidget

import com.example.khughes.machewidget.CarStatus.CarStatus
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiAutonomicService {
    @Headers(
        "Accept: */*",
        "Accept-Language: en-US",
        "User-Agent: okhttp/4.9.0",
        "Accept-Encoding: gzip, deflate, br",
        "Content-Type: application/json",
        "Application-Id: " + FordConnectConstants.APID,
    )
    @GET("v1/telemetry/sources/fordpass/vehicles/{VIN}")
    fun getStatus(
        @Path("VIN") VIN: String?,
        @Query("lrdt") lrdt: String?,
        @Header("authorization") token: String?,
    ): Call<NewCarStatus?>?

    @Headers(
        "Accept-Encoding: gzip",
        "Application-Id: " + FordConnectConstants.APID,
        "Content-Type: application/json",
        "Host: api.mps.ford.com",
        "User-Agent: okhttp/4.9.0",
        "locale: en-us"
    )
    @POST("expvehiclealerts/v2/details")
    fun getOTAInfo(@Body vin: RequestBody?, @Header("auth-token") token: String?,
                   @Header("countryCode") country: String?): Call<ResponseBody?>?

//    @Headers("locale: en-US", "user-agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110")
//    @POST("expdashboard/v1/details")
//    fun getUserDetails(
//        @Header("auth-token") token: String?,
//        @Header("application-id") APID: String?,
//        @Header("countrycode") country: String?,
//        @Body data: RequestBody?
//    ): Call<UserDetails?>?

//    @Headers(
//        "Content-Type: application/json",
//        "Accept-Language: en-US",
//        "Application-Id: " + FordConnectConstants.APID,
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