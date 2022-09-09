package com.example.khughes.machewidget;

import com.example.khughes.machewidget.OTAStatus.OTAStatus;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface DigitalServicesService {
    @Headers({"Accept: application/json",
            "Accept-Encoding: gzip, deflate, br",
            "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110",
            "Referer: https://ford.com", "Origin: https://ford.com",
            "Consumer-Key: Z28tbmEtZm9yZA=="})
    @GET("owner/api/v2/ota/status")
    Call<OTAStatus> getOTAStatus(@Header("auth-token") String token,
                                 @Header("Accept-Language") String language,
                                 @Header("Application-Id") String APID,
                                 @Query("country") String country,
                                 @Query("vin") String VIN);

    @Headers({"Accept: image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8",
            "Accept-Encoding: gzip, deflate, br",
            "User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110"})
    @GET("fs/api/v2/vehicles/image/full")
    Call<ResponseBody> getVehicleImage(@Header("auth-token") String token,
                                       @Header("Application-Id") String APID,
                                       @Query("vin") String VIN,
                                       @Query("year") String modelYear,
                                       @Query("countryCode") String country,
                                       @Query("angle") String angle);

}
