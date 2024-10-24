package com.example.khughes.machewidget

import android.content.Context
import com.example.khughes.machewidget.LogFile.d
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference

object NetworkServiceGenerators {
    private lateinit var mContext: WeakReference<Context>

    private const val B2LOGIN_BASE_URL = "https://dah2vb2cprod.b2clogin.com/"
    private const val APIMPS_BASE_URL = "https://api.mps.ford.com/api/"

    private val logging = HttpLoggingInterceptor { message ->
        var result = message
        // remove client secret and client ID
        if (result.contains(FordConnectConstants.CLIENTID)) {
            result = message.replace(FordConnectConstants.CLIENTID,"**redacted**")
        }
        if (result.contains(FordConnectConstants.CLIENTSECRET)) {
            result = message.replace(FordConnectConstants.CLIENTSECRET,"**redacted**")
        }
        if (result.contains("\"userId\"")) {
            result =
                message.replace("\"userId\":.[^\"]*.".toRegex(), "\"userId\":**redacted**")
        }
        mContext.get()?.let { d("OkHttp3", result) }
    }
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val OAUTH2 = Retrofit.Builder()
        .baseUrl(B2LOGIN_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private var OAUTH2Retrofit = OAUTH2.build()
    private val OAUTH2HttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    @JvmStatic
    fun <S> createOAUTH2Service(
        serviceClass: Class<S>?, context: Context?
    ): S {
        mContext = WeakReference(context)
        if (!OAUTH2HttpClient.interceptors().contains(logging)) {
            OAUTH2HttpClient.addInterceptor(logging)
            OAUTH2.client(OAUTH2HttpClient.build())
            OAUTH2Retrofit = OAUTH2.build()
        }
        return OAUTH2Retrofit.create(serviceClass as Class<S>)
    }

    private val APIMPS = Retrofit.Builder()
        .baseUrl(APIMPS_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private var APIMPSRetrofit = APIMPS.build()
    private val APIMPSHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    @JvmStatic
    fun <S> createAPIMPSService(
        serviceClass: Class<S>?, context: Context?
    ): S {
        mContext = WeakReference(context)
        if (!APIMPSHttpClient.interceptors().contains(logging)) {
            APIMPSHttpClient.addInterceptor(logging)
            APIMPS.client(APIMPSHttpClient.build())
            APIMPSRetrofit = APIMPS.build()
        }
        return APIMPSRetrofit.create(serviceClass as Class<S>)
    }
}