package com.example.khughes.machewidget;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AccessTokenService {
    @FormUrlEncoded
    @Headers({"Content-Type: application/x-www-form-urlencoded",
            "Accept-Language: en-US",
            "Authorization: Basic ZWFpLWNsaWVudDo="})
    @POST("token")
    Call<AccessToken> getAccessToken(@Field("client_id") String client,
                                     @Field("grant_type") String grant,
                                     @Field("username") String username,
                                     @Field("password") String password
    );

    @Headers({"Content-Type: application/json",
            "Accept-Language: en-US", "Application-Id: "+ Constants.APID,
            "Authorization: Basic ZWFpLWNsaWVudDo="})
    @PUT("token")
    Call<AccessToken> getAccessToken(@Body RequestBody token);

    @Headers({"Content-Type: application/json",
            "Accept-Language: en-US", "Application-Id: "+ Constants.APID,
            "Authorization: Basic ZWFpLWNsaWVudDo="})
    @PUT("refresh")
    Call<AccessToken> refreshAccessToken(@Body RequestBody token);


}
