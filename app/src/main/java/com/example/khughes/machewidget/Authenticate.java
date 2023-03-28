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
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    private static String generateCodeChallange(String codeVerifier) {
        byte[] bytes;
        byte[] digest = new byte[0];
        try {
            bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
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

            String codeVerifier = generateCodeVerifier();
            String codeChallenge = generateCodeChallange(codeVerifier);

            // first URL
            String url1 = "https://sso.ci.ford.com/v1.0/endpoint/default/authorize?redirect_uri=fordapp://userauthorized&response_type=code&scope=openid&max_age=3600&client_id=9fb503e0-715b-47e8-adfd-ad4b7770f73b&code_challenge="
                    + codeChallenge + "&code_challenge_method=S256";

            Request request = new Request.Builder()
                    .url(url1)
                    .headers(defaultHeaders)
                    .addHeader("Content-type", "application/json")
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            if (response.code() != 302) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: first GET request didn't return 302 response");
                return null;
            }

            RequestBody requestBody;
            Headers.Builder headers;

            cookies.addAll(response.headers().values("set-cookie"));
            String newUrl = response.headers().get("location");

            if (!newUrl.startsWith("fordapp://userauthorized")) {
                LogFile.d(context, MainActivity.CHANNEL_ID, "302 redirect found: proceeding with long login");

                // Second URL
                headers = new Headers.Builder();
                headers.addAll(defaultHeaders);
                for (String cookie : cookies) {
                    headers.add("cookie", cookie);
                }
                request = new Request.Builder()
                        .url(newUrl)
                        .headers(headers.build())
                        .build();
                client = ssoCiHttpClient
                        .cookieJar(cookieJar)
                        .build();
                response = client.newCall(request).execute();
                if (response.code() != 302) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: second GET request didn't return 302 response");
                    return null;
                }
                cookies.addAll(response.headers().values("set-cookie"));
                newUrl = response.headers().get("location");

                // Third URL - this should return a 200 code and be the actual login webpage
                headers = new Headers.Builder();
                headers.addAll(defaultHeaders);
                for (String cookie : cookies) {
                    headers.add("cookie", cookie);
                }
                request = new Request.Builder()
                        .url(newUrl)
                        .headers(headers.build())
                        .build();
                response = client.newCall(request).execute();
                if (response.code() != 200) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: third GET request didn't return 200 response");
                    return null;
                }

                String thing = response.body().string();
                final String loginUrlString = "data-ibm-login-url=\"";
                thing = thing.substring(thing.indexOf(loginUrlString) + loginUrlString.length() + 1);
                thing = thing.substring(0, thing.indexOf("\""));

                // Fourth URL - perform the login with our credentials
                newUrl = "https://sso.ci.ford.com/" + thing;

                requestBody = new FormBody.Builder()
                        .add("operation", "verify")
                        .add("login-form-type", "password")
                        .add("username", username)
                        .add("password", password)
                        .build();

                request = new Request.Builder()
                        .url(newUrl)
                        .post(requestBody)
                        .headers(defaultHeaders)
                        .addHeader("application-id", applicationId)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();
                client = ssoCiHttpClient
                        .cookieJar(cookieJar)
                        .build();
                response = client.newCall(request).execute();
                if (response.code() == 200) {
                    thing = response.body().string();
                    final String errorString = "data-ibm-login-error-text=\"";
                    thing = thing.substring(thing.indexOf(errorString) + errorString.length());
//                    thing = thing.substring(0, thing.indexOf("\""));
                    if (thing.startsWith(ACCOUNT_BAD_USER_OR_PASSWORD)) {
                        return ACCOUNT_BAD_USER_OR_PASSWORD;
                    }
                    else if (thing.startsWith(ACCOUNT_DISABLED_CODE)) {
                        return ACCOUNT_DISABLED_CODE;
                    }
                }
                if (response.code() != 302) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: fourth POST request didn't return 302 response");
                    return null;
                }
                cookies.addAll(response.headers().values("set-cookie"));
                newUrl = response.headers().get("location");

                // Fifth URL
                headers = new Headers.Builder();
                headers.addAll(defaultHeaders);
                for (String cookie : cookies) {
                    headers.add("cookie", cookie);
                }
                request = new Request.Builder()
                        .url(newUrl)
                        .headers(headers.build())
                        .build();
                response = client.newCall(request).execute();
                if (response.code() != 302) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: fifth GET request didn't return 302 response");
                    return null;
                }

                newUrl = response.headers().get("location");
                if (!newUrl.startsWith("fordapp://userauthorized")) {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: fifth GET request doesn't contain location \"fordapp://userauthorized\"");
                    return null;
                }
                cookies.addAll(response.headers().values("set-cookie"));
            }

            final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
            final String[] pairs = newUrl.split("\\?")[1].split("&");
            for (String pair : pairs) {
                final int idx = pair.indexOf("=");
                final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, new LinkedList<String>());
                }
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                query_pairs.get(key).add(value);
            }
            String myCode = query_pairs.get("code").get(0);
            String myGrantID = query_pairs.get("grant_id").get(0);

            newUrl = "https://sso.ci.ford.com/oidc/endpoint/default/token";

            requestBody = new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("grant_type", "authorization_code")
                    .add("redirect_uri", "fordapp://userauthorized")
                    .add("grant_id", myGrantID)
                    .add("code", myCode)
                    .add("code_verifier", codeVerifier)
                    .build();

            headers = new Headers.Builder();
            headers.addAll(defaultHeaders);
            for (String cookie : cookies) {
                headers.add("cookie", cookie);
            }

            request = new Request.Builder()
                    .url(newUrl)
                    .headers(headers.build())
                    .addHeader("application-id", applicationId)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(requestBody)
                    .build();
            client = ssoCiHttpClient
                    .cookieJar(cookieJar)
                    .build();
            response = client.newCall(request).execute();

            if (response.code() != 200) {
                LogFile.e(context, MainActivity.CHANNEL_ID, "Authorization failed: final POST request didn't return 200 response");
                return null;
            } else {
                LogFile.d(context, MainActivity.CHANNEL_ID, "Authorization successful");
                Gson gson = new GsonBuilder().create();
                AccessToken accessToken = gson.fromJson(response.body().string(), AccessToken.class);
                return accessToken.getAccessToken();
            }
        } catch (IOException e) {
            LogFile.e(context, MainActivity.CHANNEL_ID, "exception in Authenticate.newAuthenticate: ", e);
        }
        return null;
    }
}
