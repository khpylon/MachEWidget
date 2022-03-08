package com.example.khughes.machewidget;

import android.content.Context;

import androidx.preference.PreferenceManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkServiceGenerators {

    private static Context mContext;

    private static final HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                public void log(String message) {
                    // If the line contains login credentials, scrub them before logging
                    if (message.contains("grant_type=password")) {
                        int index = message.indexOf("&username");
                        if (index > 0) {
                            message = message.substring(0, index);
                            message += "&username=<redacted>&password=<redacted>";
                        }
                    }
                    LogFile.d(mContext, "OkHttp3", message);
                }
            })
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    // Generators for account authentication

    private static final String FORD_BASE_URL = "https://fcis.ice.ibmcloud.com/v1.0/endpoint/default/";

    private static final Retrofit.Builder fordBuilder =
            new Retrofit.Builder()
                    .baseUrl(FORD_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit fordRetrofit = fordBuilder.build();

    private static final OkHttpClient.Builder fordHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createFordService(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!fordHttpClient.interceptors().contains(logging)) {
            fordHttpClient.addInterceptor(logging);
            fordBuilder.client(fordHttpClient.build());
            fordRetrofit = fordBuilder.build();
        }
        return fordRetrofit.create(serviceClass);
    }

    private static final String OATH2_BASE_URL = "https://api.mps.ford.com/api/oauth2/v1/";

    private static final Retrofit.Builder OAuth2Builder =
            new Retrofit.Builder()
                    .baseUrl(OATH2_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit OAuth2Retrofit = OAuth2Builder.build();

    private static final OkHttpClient.Builder OAuth2HttpClient =
            new OkHttpClient.Builder();

    public static <S> S createOAuth2Service(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!OAuth2HttpClient.interceptors().contains(logging)) {
            OAuth2HttpClient.addInterceptor(logging);
            OAuth2Builder.client(OAuth2HttpClient.build());
            OAuth2Retrofit = OAuth2Builder.build();
        }
        return OAuth2Retrofit.create(serviceClass);
    }

    // Generator for car status

    private static final String CAR_STATUS_BASE_URL = "https://usapi.cv.ford.com/api/";

    private static final Retrofit.Builder carStatusBuilder =
            new Retrofit.Builder()
                    .baseUrl(CAR_STATUS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit carStatusRetrofit = carStatusBuilder.build();

    private static final OkHttpClient.Builder carStatusHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createCarStatusService(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!carStatusHttpClient.interceptors().contains(logging)) {
            carStatusHttpClient.addInterceptor(logging);
            carStatusBuilder.client(carStatusHttpClient.build());
            carStatusRetrofit = carStatusBuilder.build();
        }
        return carStatusRetrofit.create(serviceClass);
    }

    // Generator for over-the-air update status

    private static final String OTA_STATUS_BASE_URL = "https://www.digitalservices.ford.com/owner/api/v2/ota/";

    private static final Retrofit.Builder OTAStatusBuilder =
            new Retrofit.Builder()
                    .baseUrl(OTA_STATUS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit OTAStatusRetrofit = OTAStatusBuilder.build();

    private static final OkHttpClient.Builder OTAStatusHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createOTAStatusService(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!OTAStatusHttpClient.interceptors().contains(logging)) {
            OTAStatusHttpClient.addInterceptor(logging);
            OTAStatusBuilder.client(OTAStatusHttpClient.build());
            OTAStatusRetrofit = OTAStatusBuilder.build();
        }
        return OTAStatusRetrofit.create(serviceClass);
    }

    // Generator for vehicle commands

    private static final String COMMAND_BASE_URL = "https://usapi.cv.ford.com/api/";

    private static final Retrofit.Builder commandBuilder =
            new Retrofit.Builder()
                    .baseUrl(COMMAND_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit commandRetrofit = commandBuilder.build();

    private static final OkHttpClient.Builder commandHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createCommandService(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!commandHttpClient.interceptors().contains(logging)) {
            commandHttpClient.addInterceptor(logging);
            commandBuilder.client(commandHttpClient.build());
            commandRetrofit = commandBuilder.build();
        }
        return commandRetrofit.create(serviceClass);
    }
}