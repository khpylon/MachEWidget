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
    @POST("token/v2/cat-with-ci-access-token")
    Call<AccessToken> getAccessToken(@Body RequestBody token);

    @Headers({"Content-Type: application/json",
            "Accept-Language: en-US", "Application-Id: " + Constants.APID,
            "Authorization: Basic ZWFpLWNsaWVudDo="})
    @POST("token/v2/cat-with-refresh-token")
    Call<AccessToken> refreshAccessToken(@Body RequestBody token);

    @Headers({"Content-Type: application/json",
            "Accept-Language: en-US", "Application-Id: " + Constants.APID,
            "Authorization: Basic ZWFpLWNsaWVudDo="})
    @GET("users")
    Call<AccessToken> getUserProfile(@Header("auth-token") String token);

    @Headers({
            "locale: en-US",
            "user-agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
    })
    @POST("expdashboard/v1/details")
    Call<UserDetails> getUserDetails(@Header("auth-token") String token,
                                      @Header("application-id") String APID,
                                      @Header("countrycode") String country,
                                      @Body RequestBody data);
}
