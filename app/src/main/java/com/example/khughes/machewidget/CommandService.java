package com.example.khughes.machewidget;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface CommandService {
    @Headers({"Content-Type: application/json", "Accept-Language: en-US",
            "Accept-Encoding: gzip, deflate, br", "Application-Id: " + Constants.APID,
            "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110"})
    @PUT("vehicles/{VIN}/{COMPONENT}/{OPERATION}")
    Call<CommandStatus> putCommand(@Header("auth-token") String token, @Path("VIN") String VIN,
                                   @Path("COMPONENT") String component,
                                   @Path("OPERATION") String operation
    );

    @Headers({"Accept: application/json", "Accept-Language: en-US",
            "Accept-Encoding: gzip, deflate, br", "Application-Id: " + Constants.APID,
            "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
            "Referer: https://ford.com", "Origin: https://ford.com'"})
    @DELETE("vehicles/{VIN}/{COMPONENT}/{OPERATION}")
    Call<CommandStatus> deleteCommand(@Header("auth-token") String token, @Path("VIN") String VIN,
                                      @Path("COMPONENT") String component,
                                      @Path("OPERATION") String operation
    );

}
