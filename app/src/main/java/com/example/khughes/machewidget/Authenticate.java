package com.example.khughes.machewidget;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class Authenticate {

    private static Context mContext;

    public static final String ACCOUNT_BAD_USER_OR_PASSWORD = "CSIAH0303E";
    public static final String ACCOUNT_DISABLED_CODE = "CSIAH0320E";

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

    private static String generateCodeVerifier() throws UnsupportedEncodingException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] code1 = new byte[32];
        secureRandom.nextBytes(code1);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code1);
    }

    private static String generateCodeChallange(String code1) {
        byte[] bytes;
        byte[] digest = new byte[0];
        try {
            bytes = code1.getBytes(StandardCharsets.US_ASCII);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    public static String newAuthenticate(Context context, String username, String password) {
        mContext = context;
        try {
            ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

            OkHttpClient.Builder ssoCiHttpClient = new OkHttpClient.Builder();

            final String applicationId = "71A3AD0A-CF46-4CCF-B473-FC7FE5BC4592";
            final String clientId = "9fb503e0-715b-47e8-adfd-ad4b7770f73b";

            Headers defaultHeaders = new Headers.Builder()
                    .add("Accept-Language", "en-US,en;q=0.9")
                    .add("Accept", "*/*")
                    .add("User-Agent: FordPass/5 CFNetwork/1327.0.4 Chrome/96.0.4664.110")
                    .add("host", "sso.ci.ford.com")
                    .build();

            ssoCiHttpClient.addInterceptor(logging);
            ssoCiHttpClient.followRedirects(false);

            OkHttpClient client = ssoCiHttpClient
                    .cookieJar(cookieJar)
                    .build();
            List<String> cookies = new ArrayList<>();

            String code1 = generateCodeVerifier();
            String codeVerifier = generateCodeChallange(code1);

            String FORD_LOGIN_URL = "https://login.ford.com/";

            String country_code = "EN-US";
            String short_country_code = "USA";
            String url1 = FORD_LOGIN_URL + "4566605f-43a7-400a-946e-89cc9fdb0bd7/B2C_1A_SignInSignUp_"+country_code+
                    "/oauth2/v2.0/authorize?redirect_uri=fordapp://userauthorized&response_type=code&max_age=3600&"+
                    "scope=%2009852200-05fd-41f6-8c21-d36d3497dc64%20openid&client_id=09852200-05fd-41f6-8c21-d36d3497dc64&"+
                    "code_challenge="+codeVerifier+
                    "&code_challenge_method=S256&ui_locales="+country_code+
                    "&language_code="+country_code+
                    "&country_code="+short_country_code+
                    "&ford_application_id=5C80A6BB-CF0D-4A30-BDBF-FC804B5C1A98";

            Headers loginHeaders = new Headers.Builder()
                    .add("Accept-Language", "en-US")
                    .add("Accept", "*/*")
                    .add("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Mobile/15E148 Safari/604.1")
                    .build();

            Request request = new Request.Builder()
                    .url(url1)
                    .headers(loginHeaders)
                    .build();
            okhttp3.Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: first GET request didn't return 200 response");
                return null;
            }

            String responseBody = response.body().string().replaceAll("\"", "'");

            RequestBody requestBody;
            Headers.Builder headers;

            int start = responseBody.indexOf("{", responseBody.indexOf("var SETTINGS = ") );
            int ending = responseBody.lastIndexOf("}", responseBody.indexOf("\r", start));

            String json = responseBody.substring(start, ending+1);
            Gson ggson = new GsonBuilder().create();
            Map settings = ggson.fromJson(json, Map.class);

            String transId = settings.get("transId").toString();
            String csrfToken = settings.get("csrf").toString();

            String urlp = FORD_LOGIN_URL + "4566605f-43a7-400a-946e-89cc9fdb0bd7/B2C_1A_SignInSignUp_" + country_code +
                    "/SelfAsserted?tx="+ transId +
                    "&p=B2C_1A_SignInSignUp_en-AU";

            requestBody = new FormBody.Builder()
                    .add("request_type", "RESPONSE")
                    .add("signInName", username)
                    .add("password", password)
                    .build();

            request = new Request.Builder()
                    .url(urlp)
                    .post(requestBody)
                    .headers(loginHeaders)
                    .addHeader("Origin", "https://login.ford.com")
                    .addHeader("Referer", url1)
                    .addHeader("X-Csrf-Token", csrfToken)
                    .build();
            client = ssoCiHttpClient
                    .cookieJar(cookieJar)
                    .build();
            response = client.newCall(request).execute();

            if (response.code() != 200) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: first GET request didn't return 200 response");
                return null;
            }

            cookies.addAll(response.headers().values("set-cookie"));
            String newUrl = FORD_LOGIN_URL +
                    "4566605f-43a7-400a-946e-89cc9fdb0bd7/B2C_1A_SignInSignUp_" + country_code +
                    "/api/CombinedSigninAndSignup/confirmed?rememberMe=false&csrf_token=" + csrfToken;

            request = new Request.Builder()
                    .url(newUrl)
                    .headers(loginHeaders)
                    .build();
            client = ssoCiHttpClient
                    .cookieJar(cookieJar)
                    .followRedirects(false)
                    .build();
            response = client.newCall(request).execute();

            if (response.code() != 302) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: first GET request didn't return 302 response");
                return null;
            }

            newUrl = response.headers().get("location").replace("fordapp://userauthorized/?code=","");

            requestBody = new FormBody.Builder()
                    .add("client_id", "09852200-05fd-41f6-8c21-d36d3497dc64")
                    .add("grant_type", "authorization_code")
                    .add("code_verifier", code1)
                            .add("code", newUrl)
                    .add("redirect_uri", "fordapp://userauthorized")
                    .build();

            request = new Request.Builder()
                    .url(FORD_LOGIN_URL + "4566605f-43a7-400a-946e-89cc9fdb0bd7/B2C_1A_SignInSignUp_" + country_code + "/oauth2/v2.0/token")
                    .post(requestBody)
                    .headers(loginHeaders)
                    .addHeader("Origin", "https://login.ford.com")
                    .addHeader("Referer", url1)
                    .addHeader("X-Csrf-Token", csrfToken)
                    .build();
            client = ssoCiHttpClient
                    .cookieJar(cookieJar)
                    .build();
            response = client.newCall(request).execute();

            if (response.code() != 200) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: final POST request didn't return 200 response");
                return null;
            }
            LogFile.d(context, MainActivity.CHANNEL_ID, "Authorization successful");
            Gson gson = new GsonBuilder().create();
            AccessToken accessToken = gson.fromJson(response.body().string(), AccessToken.class);
            return accessToken.getAccessToken();
        } catch (IOException e) {
            LogFile.e(context, MainActivity.CHANNEL_ID, "exception in Authenticate.newAuthenticate: ", e);
        }
        return null;
    }
}
