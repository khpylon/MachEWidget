package com.example.khughes.machewidget;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface APIMPSService {

    @Headers({"Content-Type: application/json",
            "Accept-Language: en-US", "Application-Id: " + Constants.APID,
            "Authorization: Basic ZWFpLWNsaWVudDo="})
    @PUT("oauth2/v1/token")
    Call<AccessToken> getAccessToken(@Body RequestBody token);

    @Headers({"Content-Type: application/json",
            "Accept-Language: en-US", "Application-Id: " + Constants.APID,
            "Authorization: Basic ZWFpLWNsaWVudDo="})
    @PUT("oauth2/v1/refresh")
    Call<AccessToken> refreshAccessToken(@Body RequestBody token);

    @Headers({
            "locale: en-US",
            "user-agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
    })
    @POST("expdashboard/v1/details")
    Call<UserDetails> getUserDetails(@Header("auth-token") String token,
                                      @Header("application-id") String APID,
                                      @Header("countrycode") String country,
                                      @Body RequestBody data);

    @Headers({"Accept: application/json",
            "Accept-Encoding: gzip, deflate, br",
            "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
            "Referer: https://ford.com", "Origin: https://ford.com'"})
    @GET("vpoi/chargestations/v3/plugstatus")
    Call<ChargeStation> getChargeStation(@Header("auth-token") String token,
                                         @Header("Application-Id") String APID,
                                         @Header("vin") String VIN);
}
