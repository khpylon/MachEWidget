package com.example.khughes.machewidget;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkServiceGenerators {

    private static Context mContext;

    private static final String IBMCLOUD_BASE_URL = "https://fcis.ice.ibmcloud.com/v1.0/endpoint/default/";

    private static final String SSOCI_BASE_URL = "https://sso.ci.ford.com/";

    private static final String APIMPS_BASE_URL = "https://api.mps.ford.com/api/";
    private static final String USAPICV_BASE_URL = "https://usapi.cv.ford.com/api/";
    private static final String DIGITALSERVICES_BASE_URL = "https://www.digitalservices.ford.com/";

    private static final HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                public void log(String message) {
                    // If the line contains login credentials, scrub them before logging
                    if (message.contains("login-form-type=password")) {
                        int index = message.indexOf("&username");
                        if (index > 0) {
                            message = message.substring(0, index);
                            message += "&username=**redacted**&password=**redacted**";
                        }
                    }
                    if (message.contains("UserProfile\":{")) {
                        message = message.replaceAll("\"profile\":.[^}]*.[^}]*.[^}]*.", "\"profile\":**redacted**");
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

    private static final Retrofit.Builder ssoCiBuilder =
            new Retrofit.Builder()
                    .baseUrl(SSOCI_BASE_URL);

    private static Retrofit ssoCiRetrofit = ssoCiBuilder
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    private static final OkHttpClient.Builder ssoCiHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createSsoCiService(
            Class<S> serviceClass, Context context, boolean redirects) {
        mContext = context;
        if (!ssoCiHttpClient.interceptors().contains(logging)) {
            ssoCiHttpClient.addInterceptor(logging);
            ssoCiBuilder.client(ssoCiHttpClient.build());
            ssoCiRetrofit = ssoCiBuilder.build();
        }
        return ssoCiRetrofit.create(serviceClass);
    }

    public static void ssoCiHttpClientSetFollowRedirects(boolean follow) {
        ssoCiHttpClient.followRedirects( follow );
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