package com.example.khughes.machewidget;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkServiceGenerators {

    private static Context mContext;

//    private static final String SSOCI_BASE_URL = "https://sso.ci.ford.com/";

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

//    private static final Retrofit.Builder ssoCiBuilder =
//            new Retrofit.Builder()
//                    .baseUrl(SSOCI_BASE_URL);
//
//    private static Retrofit ssoCiRetrofit = ssoCiBuilder
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .build();
//
//    private static final OkHttpClient.Builder ssoCiHttpClient =
//            new OkHttpClient.Builder();
//
//    public static <S> S createSsoCiService(
//            Class<S> serviceClass, Context context, boolean redirects) {
//        mContext = context;
//        if (!ssoCiHttpClient.interceptors().contains(logging)) {
//            ssoCiHttpClient.addInterceptor(logging);
//            ssoCiBuilder.client(ssoCiHttpClient.build());
//            ssoCiRetrofit = ssoCiBuilder.build();
//        }
//        return ssoCiRetrofit.create(serviceClass);
//    }
//
//    public static void ssoCiHttpClientSetFollowRedirects(boolean follow) {
//        ssoCiHttpClient.followRedirects( follow );
//    }

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

    private static Retrofit USAPICVRetrofit = USAPICVBuilder.build();

    private static final OkHttpClient.Builder USASPICVHttpClient =
            new OkHttpClient.Builder();

    public static <S> S createUSAPICVService(
            Class<S> serviceClass, Context context
            //, UserInfo user
             ) {
        mContext = context;
        if (!USASPICVHttpClient.interceptors().contains(logging)) {
            USASPICVHttpClient.addInterceptor(logging);
//            USASPICVHttpClient.authenticator(new TokenAuthenticator(context,user));
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