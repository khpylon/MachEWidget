package com.example.khughes.machewidget

import com.google.gson.annotations.Expose
import javax.annotation.Generated

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

@Generated("jsonschema2pojo")
data class VehicleList (
    val status: String,
    @Expose
    val vehicles: List<VehicleData>,
)

@Generated("jsonschema2pojo")
data class VehicleData(
    val vehicleId: String,
    val make: String,
    val modelName: String,
    val modelYear: String,
    val color: String,
    val nickName: String,
    val modemEnabled: Boolean,
    val vehicleAuthorizationIndicator: Long,
    val serviceCompatible: Boolean,
    val engineType: String,
)

interface APIMPSService {

    @Headers(
        "Accept: application/json",
        "Content-type: application/json",
        "Application-id: " + FordConnectConstants.APID
    )
    @GET("fordconnect/v3/vehicles")
    fun getVehicleList (@Header("Authorization") accessToken: String) : Call<VehicleList>?

    @Headers(
        "Application-id: " + FordConnectConstants.APID
    )
    @GET("fordconnect/v3/vehicles/{vehicleID}")
    fun getStatus (
        @Path("vehicleID") vehicleId: String,
        @Header("Authorization") accessToken: String
    ) : Call<CarStatus>?

    @Headers(
        "Application-id: " + FordConnectConstants.APID
    )
    @GET("fordconnect/v3/vehicles/{vehicleID}/vin")
    fun getVIN (
        @Path("vehicleID") vehicleId: String,
        @Header("Authorization") accessToken: String
    ) : Call<ResponseBody>?

    @Headers(
        "Application-id: " + FordConnectConstants.APID
    )
    @GET("fordconnect/v1/vehicles/{vehicleID}/images/full")
    fun getVehicleImage(
        @Path("vehicleID") vehicleId: String,
        @Header("Authorization") accessToken: String,
        @Query("make") make: String?,
        @Query("model") model: String?,
        @Query("year") year: String?,
    ): Call<ResponseBody?>?


}