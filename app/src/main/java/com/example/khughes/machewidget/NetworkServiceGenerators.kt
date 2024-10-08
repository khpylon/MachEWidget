package com.example.khughes.machewidget

import android.content.Context
import com.example.khughes.machewidget.LogFile.d
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.min
import java.lang.ref.WeakReference

object NetworkServiceGenerators {
    private lateinit var mContext: WeakReference<Context>

    private const val B2LOGIN_BASE_URL = "https://dah2vb2cprod.b2clogin.com/"
    private const val APIMPS_BASE_URL = "https://api.mps.ford.com/api/"
    private const val USAPICV_BASE_URL = "https://usapi.cv.ford.com/api/"
    private const val DIGITALSERVICES_BASE_URL = "https://www.digitalservices.ford.com/"
    private const val APIAUTONOMIC_BASE_URL = "https://api.autonomic.ai/"
    private const val ACCOUNTAUTONOMIC_BASE_URL = "https://accounts.autonomic.ai/"

    private val logging = HttpLoggingInterceptor { message ->
        var result = message
        // If the line contains login credentials, scrub them before logging
        if (result.contains("login-form-type=password")) {
            val index = message.indexOf("&username")
            if (index > 0) {
                result = message.substring(0, index)
                result += "&username=**redacted**&password=**redacted**"
            }
        }
        if (result.contains("UserProfile\":{")) {
            result = message.replace(
                "\"profile\":.[^}]*.[^}]*.[^}]*.".toRegex(),
                "\"profile\":**redacted**"
            )
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

    private val USAPICVBuilder = Retrofit.Builder()
        .baseUrl(USAPICV_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    //    public static class TokenAuthenticator implements Authenticator {
    //
    //        private Context context;
    //        private UserInfo user;
    //
    //        public TokenAuthenticator(Context context, UserInfo user) {
    //            this.context = context;
    //            this.user = user;
    //        }
    //
    //        @Override
    //        public Request authenticate(Route route, Response response) throws IOException {
    //            // Refresh your access_token using a synchronous api request
    //
    //            AccessToken token = getNewToken(context);
    //            user.setAccessToken(token.getAccessToken());
    //            user.setRefreshToken(token.getRefreshToken());
    //            return response.request().newBuilder()
    //                    .header("auth-token", token.getAccessToken())
    //                    .build();
    //        }
    //
    //        private AccessToken getNewToken(Context context) throws IOException{
    //            Map<String, String> jsonParams = new ArrayMap<>();
    //            jsonParams.put("refresh_token", user.getRefreshToken());
    //            RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
    //            APIMPSService OAuth2Client = NetworkServiceGenerators.createAPIMPSService(APIMPSService.class, context);
    //
    //            Call<AccessToken> call = OAuth2Client.refreshAccessToken(body);
    //            retrofit2.Response<AccessToken> response = call.execute();
    //            return response.body();
    //        }
    //    }

    private var USAPICVRetrofit = USAPICVBuilder.build()
    private val USASPICVHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    @JvmStatic
    fun <S> createUSAPICVService(
        serviceClass: Class<S>?, context: Context? //, UserInfo user
    ): S {
        mContext = WeakReference(context)
        if (!USASPICVHttpClient.interceptors().contains(logging)) {
            USASPICVHttpClient.addInterceptor(logging)
            //            USASPICVHttpClient.authenticator(new TokenAuthenticator(context,user));
            USAPICVBuilder.client(USASPICVHttpClient.build())
            USAPICVRetrofit = USAPICVBuilder.build()
        }
        return USAPICVRetrofit.create(serviceClass as Class<S>)
    }

    private val DIGITALSERVICESBuilder = Retrofit.Builder()
        .baseUrl(DIGITALSERVICES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private var DIGITALSERVICESRetrofit = DIGITALSERVICESBuilder.build()
    private val DIGITALSERVICESHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    @JvmStatic
    fun <S> createDIGITALSERVICESService(
        serviceClass: Class<S>?, context: Context?
    ): S {
        mContext = WeakReference(context)
        if (!DIGITALSERVICESHttpClient.interceptors().contains(logging)) {
            DIGITALSERVICESHttpClient.addInterceptor(logging)
            DIGITALSERVICESBuilder.client(DIGITALSERVICESHttpClient.build())
            DIGITALSERVICESRetrofit = DIGITALSERVICESBuilder.build()
        }
        return DIGITALSERVICESRetrofit.create(serviceClass as Class<S>)
    }

    private val AcctAutonomicBuilder = Retrofit.Builder()
        .baseUrl(ACCOUNTAUTONOMIC_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private var AcctAutonomicRetrofit = AcctAutonomicBuilder.build()
    private val AcctAutonomicHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    @JvmStatic
    fun <S> createAcctAutonomicService(
        serviceClass: Class<S>?, context: Context?
    ): S {
        mContext = WeakReference(context)
        if (!AcctAutonomicHttpClient.interceptors().contains(logging)) {
            AcctAutonomicHttpClient.addInterceptor(logging)
            AcctAutonomicBuilder.client(AcctAutonomicHttpClient.build())
            AcctAutonomicRetrofit = AcctAutonomicBuilder.build()
        }
        return AcctAutonomicRetrofit.create(serviceClass as Class<S>)
    }

    private val ApiAutonomicBuilder = Retrofit.Builder()
        .baseUrl(APIAUTONOMIC_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private var ApiAutonomicRetrofit = ApiAutonomicBuilder.build()
    private val ApiAutonomicHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    @JvmStatic
    fun <S> createApiAutonomicService(
        serviceClass: Class<S>?, context: Context?
    ): S {
        mContext = WeakReference(context)
        if (!ApiAutonomicHttpClient.interceptors().contains(logging)) {
            ApiAutonomicHttpClient.addInterceptor(logging)
            ApiAutonomicBuilder.client(ApiAutonomicHttpClient.build())
            ApiAutonomicRetrofit = ApiAutonomicBuilder.build()
        }
        return ApiAutonomicRetrofit.create(serviceClass as Class<S>)
    }
}