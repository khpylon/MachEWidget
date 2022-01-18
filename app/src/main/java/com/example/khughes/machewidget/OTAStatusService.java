package com.example.khughes.machewidget;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OTAStatusService {
    @Headers({"Accept: application/json", "Accept-Language: en-US", "Accept-Encoding: gzip, deflate, br",
            "Application-Id: " + Constants.APID, "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
            "Referer: https://ford.com", "Origin: https://ford.com'"})
    @GET("status")
    Call<OTAStatus> getOTAStatus(@Header("auth-token") String token, @Query("country") String country,
                              @Query("vin") String VIN);

    ////    https://www.digitalservices.ford.com/owner/api/v2/ota/status?country=USA&vin='+VIN
}
