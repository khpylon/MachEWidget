package com.example.khughes.machewidget

import retrofit2.http.GET
import com.example.khughes.machewidget.OTAStatus.OTAStatus
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface DigitalServicesService {
    @Headers(
        "Accept: application/json",
        "Accept-Encoding: gzip, deflate, br",
        "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
        "Referer: https://ford.com",
        "Origin: https://ford.com",
        "Consumer-Key: Z28tbmEtZm9yZA=="
    )
    @GET("owner/api/v2/ota/status")
    fun getOTAStatus(
        @Header("auth-token") token: String?,
        @Header("Accept-Language") language: String?,
        @Header("Application-Id") APID: String?,
        @Query("country") country: String?,
        @Query("vin") VIN: String?
    ): Call<OTAStatus?>?

    @Headers(
        "accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
        "accept-encoding: gzip, deflate, br",
        "accept-language: en-US,en;q=0.9",
        "connection: keep-alive",
        "host: www.digitalservices.ford.com",
        "sec-ch-ua: \"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"",
        "sec-ch-ua-mobile: ?0",
        "sec-ch-ua-platform: Linux",
        "sec-fetch-dest: document",
        "sec-fetch-mode: navigate",
        "sec-fetch-site: none",
        "sec-fetch-user: ?1",
        "upgrade-insecure-requests: 1",
        "user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
    )
    @GET("fs/api/v2/vehicles/image/full")
    fun getVehicleImage(
        @Header("Application-Id") APID: String?,
        @Query("vin") VIN: String?,
        @Query("year") modelYear: String?,
        @Query("countryCode") country: String?,
        @Query("angle") angle: String?
    ): Call<ResponseBody?>?
}