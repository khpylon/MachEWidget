package com.example.khughes.machewidget;

import android.content.Context;

import androidx.preference.PreferenceManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkServiceGenerators {

    private static final HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    // Generators for account authentication

    private static final String FORD_BASE_URL = "https://fcis.ice.ibmcloud.com/v1.0/endpoint/default/";

    private static final Retrofit.Builder fordBuilder =
            new Retrofit.Builder()
                    .baseUrl(FORD_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit fordRetrofit = fordBuilder.build();

    private static OkHttpClient.Builder fordHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createFordService(
            Class<S> serviceClass, Context context) {
        Boolean verbose = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.okhttp3_key), false);
        if (verbose && !fordHttpClient.interceptors().contains(logging)) {
            fordHttpClient.addInterceptor(logging);
            fordBuilder.client(fordHttpClient.build());
            fordRetrofit = fordBuilder.build();
        } else if (!verbose && fordHttpClient.interceptors().contains(logging)) {
            fordHttpClient = new OkHttpClient.Builder();
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

    private static OkHttpClient.Builder OAuth2HttpClient =
            new OkHttpClient.Builder();

    public static <S> S createOAuth2Service(
            Class<S> serviceClass, Context context) {
        Boolean verbose = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.okhttp3_key), false);
        if (verbose && !OAuth2HttpClient.interceptors().contains(logging)) {
            OAuth2HttpClient.addInterceptor(logging);
            OAuth2Builder.client(OAuth2HttpClient.build());
            OAuth2Retrofit = OAuth2Builder.build();
        }
        if (!verbose && OAuth2HttpClient.interceptors().contains(logging)) {
            OAuth2HttpClient = new OkHttpClient.Builder();
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

    private static OkHttpClient.Builder carStatusHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createCarStatusService(
            Class<S> serviceClass, Context context) {
        Boolean verbose = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.okhttp3_key), false);
        if (verbose && !carStatusHttpClient.interceptors().contains(logging)) {
            carStatusHttpClient.addInterceptor(logging);
            carStatusBuilder.client(carStatusHttpClient.build());
            carStatusRetrofit = carStatusBuilder.build();
        } else if (!verbose && carStatusHttpClient.interceptors().contains(logging)) {
            carStatusHttpClient = new OkHttpClient.Builder();
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

    private static OkHttpClient.Builder OTAStatusHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createOTAStatusService(
            Class<S> serviceClass, Context context) {
        Boolean verbose = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.okhttp3_key), false);
        if (verbose && !OTAStatusHttpClient.interceptors().contains(logging)) {
            OTAStatusHttpClient.addInterceptor(logging);
            OTAStatusBuilder.client(OTAStatusHttpClient.build());
            OTAStatusRetrofit = OTAStatusBuilder.build();
        } else if (!verbose && OTAStatusHttpClient.interceptors().contains(logging)) {
            OTAStatusHttpClient = new OkHttpClient.Builder();
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
            Class<S> serviceClass) {
        if (BuildConfig.DEBUG && !commandHttpClient.interceptors().contains(logging)) {
            commandHttpClient.addInterceptor(logging);
            commandBuilder.client(commandHttpClient.build());
            commandRetrofit = commandBuilder.build();
        }

        return commandRetrofit.create(serviceClass);
    }
}