package com.example.khughes.machewidget;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OTAStatusServiceGenerator {

    private static final String FORD_BASE_URL = "https://www.digitalservices.ford.com/owner/api/v2/ota/";

//    https://www.digitalservices.ford.com/owner/api/v2/ota/status?country=USA&vin='+VIN

    private static Retrofit.Builder fordBuilder =
            new Retrofit.Builder()
                    .baseUrl(FORD_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit fordRetrofit = fordBuilder.build();

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        if (BuildConfig.DEBUG && !httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            fordBuilder.client(httpClient.build());
            fordRetrofit = fordBuilder.build();
        }

        return fordRetrofit.create(serviceClass);
    }
}