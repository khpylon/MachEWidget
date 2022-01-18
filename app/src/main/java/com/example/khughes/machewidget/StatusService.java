package com.example.khughes.machewidget;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StatusService {
    @Headers({ "Accept: application/json", "Accept-Language: en-US", "Accept-Encoding: gzip, deflate, br",
            "Application-Id: " + Constants.APID, "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110" })
    @GET("vehicles/v4/{VIN}/status")
    Call<CarStatus> getStatus(@Header("auth-token") String token, @Path("VIN") String VIN);
}
