package com.example.khughes.machewidget;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface DigitalServicesService {
    @Headers({"Accept: application/json",
            "Accept-Encoding: gzip, deflate, br",
            "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
            "Referer: https://ford.com", "Origin: https://ford.com'"})
    @GET("owner/api/v2/ota/status")
    Call<OTAStatus> getOTAStatus(@Header("auth-token") String token,
                                 @Header("Accept-Language") String language,
                                 @Header("Application-Id") String APID,
                                 @Query("country") String country,
                                 @Query("vin") String VIN);
}
