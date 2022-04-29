package com.example.khughes.machewidget;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkServiceGenerators {

    private static Context mContext;

    private static final String IBMCLOUD_BASE_URL = "https://fcis.ice.ibmcloud.com/v1.0/endpoint/default/";
    private static final String APIMPS_BASE_URL = "https://api.mps.ford.com/api/";
    private static final String USAPICV_BASE_URL = "https://usapi.cv.ford.com/api/";
    private static final String DIGITALSERVICES_BASE_URL = "https://www.digitalservices.ford.com/";

    private static final HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                public void log(String message) {
                    // If the line contains login credentials, scrub them before logging
                    if (message.contains("grant_type=password")) {
                        int index = message.indexOf("&username");
                        if (index > 0) {
                            message = message.substring(0, index);
                            message += "&username=**redacted**&password=**redacted**";
                        }
                    }
                    if (message.contains("UserProfile\":{")) {
                        message = message.replaceAll("\"UserProfile\":.[^}]*.[^}]*.[^}]*.", "\"UserProfile\":**redacted**");
                    }
                    if (message.contains("\"userId\"")) {
                        message = message.replaceAll("\"userId\":.[^\"]*.", "\"userId\":**redacted**");
                    }
                    LogFile.d(mContext, "OkHttp3", message);
                }
            })
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static final Retrofit.Builder ibmCloudBuilder =
            new Retrofit.Builder()
                    .baseUrl(IBMCLOUD_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit ibmCloudRetrofit = ibmCloudBuilder.build();

    private static final OkHttpClient.Builder ibmCloudHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createIBMCloudService(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!ibmCloudHttpClient.interceptors().contains(logging)) {
            ibmCloudHttpClient.addInterceptor(logging);
            ibmCloudBuilder.client(ibmCloudHttpClient.build());
            ibmCloudRetrofit = ibmCloudBuilder.build();
        }
        return ibmCloudRetrofit.create(serviceClass);
    }

    private static final Retrofit.Builder APIMPS =
            new Retrofit.Builder()
                    .baseUrl(APIMPS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit APIMPSRetrofit = APIMPS.build();

    private static final OkHttpClient.Builder APIMPSHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createAPIMPSService(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!APIMPSHttpClient.interceptors().contains(logging)) {
            APIMPSHttpClient.addInterceptor(logging);
            APIMPS.client(APIMPSHttpClient.build());
            APIMPSRetrofit = APIMPS.build();
        }
        return APIMPSRetrofit.create(serviceClass);
    }

    private static final Retrofit.Builder USAPICVBuilder =
            new Retrofit.Builder()
                    .baseUrl(USAPICV_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit USAPICVRetrofit = USAPICVBuilder.build();

    private static final OkHttpClient.Builder USASPICVHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createUSAPICVService(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!USASPICVHttpClient.interceptors().contains(logging)) {
            USASPICVHttpClient.addInterceptor(logging);
            USAPICVBuilder.client(USASPICVHttpClient.build());
            USAPICVRetrofit = USAPICVBuilder.build();
        }
        return USAPICVRetrofit.create(serviceClass);
    }

    private static final Retrofit.Builder DIGITALSERVICESBuilder =
            new Retrofit.Builder()
                    .baseUrl(DIGITALSERVICES_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit DIGITALSERVICESRetrofit = DIGITALSERVICESBuilder.build();

    private static final OkHttpClient.Builder DIGITALSERVICESHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createDIGITALSERVICESService(
            Class<S> serviceClass, Context context) {
        mContext = context;
        if (!DIGITALSERVICESHttpClient.interceptors().contains(logging)) {
            DIGITALSERVICESHttpClient.addInterceptor(logging);
            DIGITALSERVICESBuilder.client(DIGITALSERVICESHttpClient.build());
            DIGITALSERVICESRetrofit = DIGITALSERVICESBuilder.build();
        }
        return DIGITALSERVICESRetrofit.create(serviceClass);
    }
}