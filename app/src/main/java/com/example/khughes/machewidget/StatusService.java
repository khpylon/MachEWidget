package com.example.khughes.machewidget;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StatusService {
    @Headers({"Accept: application/json",
            "Accept-Encoding: gzip, deflate, br",
            "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110"})
    @GET("vehicles/v4/{VIN}/status")
    Call<CarStatus> getStatus(@Header("auth-token") String token,
                              @Header("Accept-Language") String language,
                              @Header("Application-Id") String APID,
                              @Path("VIN") String VIN);
}
