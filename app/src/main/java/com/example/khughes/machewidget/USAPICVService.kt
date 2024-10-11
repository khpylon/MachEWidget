package com.example.khughes.machewidget

import com.example.khughes.machewidget.CommandStatus
import com.example.khughes.machewidget.VehicleInfo
import retrofit2.Call
import retrofit2.http.*

interface USAPICVService {
//    @Headers(
//        "Accept: application/json",
//        "Accept-Encoding: gzip, deflate, br",
//        "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110"
//    )
//    @GET("vehicles/v5/{VIN}/status")
//    fun getStatus(
//        @Header("auth-token") token: String?,
//        @Header("Accept-Language") language: String?,
//        @Header("Application-Id") APID: String?,
//        @Path("VIN") VIN: String?
//    ): Call<CarStatus?>?
//
//    @Headers(
//        "Accept: application/json",
//        "Accept-Encoding: gzip, deflate, br",
//        "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110"
//    )
//    @PUT("vehicles/v5/{VIN}/status")
//    fun putStatus(
//        @Header("auth-token") token: String?,
//        @Header("Application-Id") APID: String?,
//        @Path("VIN") VIN: String?
//    ): Call<CommandStatus?>?
//
//    @Headers(
//        "Accept: application/json",
//        "Accept-Encoding: gzip, deflate, br",
//        "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110"
//    )
//    @GET("vehicles/v5/{VIN}/statusrefresh/{CommandId}")
//    fun pollStatus(
//        @Header("auth-token") token: String?,
//        @Header("Application-Id") APID: String?,
//        @Path("VIN") VIN: String?,
//        @Path("CommandId") commandId: String?
//    ): Call<CarStatus?>?
//
//    @Headers(
//        "Content-Type: application/json",
//        "Accept-Encoding: gzip, deflate, br",
//        "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110"
//    )
//    @PUT("vehicles/{APIVERSION}/{VIN}/{COMPONENT}/{OPERATION}")
//    fun putCommand(
//        @Header("auth-token") token: String?,
//        @Header("Application-Id") APID: String?,
//        @Path("APIVERSION") version: String?,
//        @Path("VIN") VIN: String?,
//        @Path("COMPONENT") component: String?,
//        @Path("OPERATION") operation: String?
//    ): Call<CommandStatus?>?
//
//    @Headers(
//        "Accept: application/json",
//        "Accept-Encoding: gzip, deflate, br",
//        "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
//        "Referer: https://ford.com",
//        "Origin: https://ford.com'"
//    )
//    @DELETE("vehicles/{APIVERSION}/{VIN}/{COMPONENT}/{OPERATION}")
//    fun deleteCommand(
//        @Header("auth-token") token: String?,
//        @Header("Application-Id") APID: String?,
//        @Path("APIVERSION") version: String?,
//        @Path("VIN") VIN: String?,
//        @Path("COMPONENT") component: String?,
//        @Path("OPERATION") operation: String?
//    ): Call<CommandStatus?>?
//
//    @Headers(
//        "Accept: application/json",
//        "Accept-Encoding: gzip",
//        "Connection: Keep-Alive",
//        "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
//        "Referer: https://ford.com",
//        "Origin: https://ford.com'"
//    )
//    @GET("vehicles/v3/{VIN}/{COMPONENT}/{OPERATION}/{CODE}")
//    fun getCommandResponse(
//        @Header("auth-token") token: String?,
//        @Header("Application-Id") APID: String?,
//        @Path("VIN") VIN: String?,
//        @Path("COMPONENT") component: String?,
//        @Path("OPERATION") operation: String?,
//        @Path("CODE") code: String?
//    ): Call<CommandStatus?>?
//
//    @Headers(
//        "Accept: application/json",
//        "Accept-Encoding: gzip",
//        "Connection: Keep-Alive",
//        "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
//        "Referer: https://ford.com",
//        "Origin: https://ford.com'"
//    )
//    @GET("users/vehicles/{VIN}/detail")
//    fun  // ?lrdt=01-01-1970%2000:00:00"
//            getVehicleInfo(
//        @Header("auth-token") token: String?,
//        @Header("Application-Id") APID: String?,
//        @Path("VIN") VIN: String?
//    ): Call<VehicleInfo?>?
}