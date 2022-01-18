package com.example.khughes.machewidget;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccessTokenServiceGenerator {

    private static final String FORD_BASE_URL = "https://fcis.ice.ibmcloud.com/v1.0/endpoint/default/";

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

    public static <S> S createFordService(
            Class<S> serviceClass) {
        if (BuildConfig.DEBUG && !httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            fordBuilder.client(httpClient.build());
            fordRetrofit = fordBuilder.build();
        }

        return fordRetrofit.create(serviceClass);
    }

    private static final String OATH2_BASE_URL = "https://api.mps.ford.com/api/oauth2/v1/";

    private static Retrofit.Builder OAuth2Builder =
            new Retrofit.Builder()
                    .baseUrl(OATH2_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit OAuth2Retrofit = OAuth2Builder.build();

    private static OkHttpClient.Builder OAuth2HttpClient =
            new OkHttpClient.Builder();

    public static <S> S createOAuth2Service(
            Class<S> serviceClass) {
        if (BuildConfig.DEBUG && !OAuth2HttpClient.interceptors().contains(logging)) {
            OAuth2HttpClient.addInterceptor(logging);
            OAuth2Builder.client(OAuth2HttpClient.build());
            OAuth2Retrofit = OAuth2Builder.build();
        }

        return OAuth2Retrofit.create(serviceClass);
    }


}