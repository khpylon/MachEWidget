package com.example.khughes.machewidget

import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AcctAutonomicService {
    @FormUrlEncoded
    @Headers(
        "Content-Type: application/x-www-form-urlencoded",
        "Accept: */*",
    )
    @POST("v1/auth/oidc/token")
    fun getAccessToken(
        @Field("subject_token") token: String,
        @Field("subject_issuer") issuer: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("subject_token_type") tokenType: String
    ): Call<AccessToken?>?

    @Headers(
        "Content-Type: application/json",
        "Accept-Language: en-US",
        "Application-Id: " + FordConnectConstants.APID,
        "Authorization: Basic ZWFpLWNsaWVudDo="
    )
    @POST("token/v2/cat-with-refresh-token")
    fun refreshAccessToken(
        @Body token: RequestBody?
    ): Call<AccessToken?>?

    @Headers(
        "Accept-Encoding: gzip",
        "Application-Id: " + FordConnectConstants.APID,
        "Content-Type: application/json",
        "Host: api.mps.ford.com",
        "User-Agent: okhttp/4.9.0"
    )
    @POST("cevs/v1/chargestatus/retrieve")
    fun getStatus(
        @Query("lrdt") lrdt: String?,
        @Header("auth-token") token: String?
    ): Call<DCFCInfo?>?

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