package com.example.khughes.machewidget;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Handler;
import android.os.Message;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.ArrayMap;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class NetworkCalls {
    public static final String COMMAND_SUCCESSFUL = "Command successful.";
    public static final String COMMAND_FAILED = "Command failed.";
    public static final String COMMAND_NO_NETWORK = "Network error.";
    public static final String COMMAND_EXCEPTION = "Exception occurred.";

    public static void getAccessToken(Handler handler, Context context, String username, String password) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NetworkCalls.getAccessToken(context, username, password);
                Message m = Message.obtain();
                m.setData(intent.getExtras());
                handler.sendMessage(m);
            }
        });
        t.start();
    }

    private static Intent getAccessToken(Context context, String username, String password) {
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");

        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);
        ProgramStateMachine.States nextState = ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN;

        if (MainActivity.checkInternetConnection(context)) {
            AccessTokenService fordClient = NetworkServiceGenerators.createFordService(AccessTokenService.class, context);
            AccessTokenService OAuth2Client = NetworkServiceGenerators.createOAuth2Service(AccessTokenService.class, context);
            Call<AccessToken> call = fordClient.getAccessToken(Constants.CLIENTID, "password", username, password);
            try {
                Response<AccessToken> response = call.execute();
                if (response.isSuccessful()) {
                    AccessToken accessToken = response.body();
                    data.putExtra("access_token", accessToken.getAccessToken());

                    Map<String, String> jsonParams = new ArrayMap<>();
                    jsonParams.put("code", accessToken.getAccessToken());
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

                    call = OAuth2Client.getAccessToken(body);
                    try {
                        response = call.execute();
                        if (response.isSuccessful()) {
                            accessToken = response.body();
                            data.putExtra("access_token", accessToken.getAccessToken());
                            data.putExtra("refresh_token", accessToken.getRefreshToken());
                            data.putExtra("expires", accessToken.getExpiresIn());
                            appInfo.setLanguage(VIN, accessToken.getUserProfile().getLanguage());
                            appInfo.setCountry(VIN, accessToken.getUserProfile().getCountry());
                            appInfo.setPressureUnits(VIN, accessToken.getUserProfile().getUomPressure());
                            appInfo.setDistanceUnits(VIN, accessToken.getUserProfile().getUomDistance());
                            appInfo.setSpeedUnits(VIN, accessToken.getUserProfile().getUomSpeed());
                            nextState = ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS;
                        }
                    } catch (IOException e) {
                        LogFile.e(context,MainActivity.CHANNEL_ID, "exception in NetworkCalls.getAccessToken: ", e);
                    }
                }
            } catch (Exception e) {
                LogFile.e(context,MainActivity.CHANNEL_ID, "exception in NetworkCalls.getAccessToken: ", e);
            }
        }
        data.putExtra("action", nextState.name());
        return data;
    }

    public static void refreshAccessToken(Handler handler, Context context, String refreshToken) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NetworkCalls.refreshAccessToken(context, refreshToken);
                Message m = Message.obtain();
                m.setData(intent.getExtras());
                handler.sendMessage(m);
            }
        });
        t.start();
    }

    private static Intent refreshAccessToken(Context context, String token) {
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");

        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);
        ProgramStateMachine.States nextState = ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN;

        if (MainActivity.checkInternetConnection(context)) {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("refresh_token", token);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());
            AccessTokenService OAuth2Client = NetworkServiceGenerators.createOAuth2Service(AccessTokenService.class, context);
            Call<AccessToken> call = OAuth2Client.refreshAccessToken(body);
            try {
                Response<AccessToken> response = call.execute();
                LogFile.i(context,MainActivity.CHANNEL_ID, "refresh here....");
                if (response.isSuccessful()) {
                    LogFile.i(context,MainActivity.CHANNEL_ID, "refresh successful");
                    AccessToken accessToken = response.body();
                    data.putExtra("access_token", accessToken.getAccessToken());
                    data.putExtra("refresh_token", accessToken.getRefreshToken());
                    data.putExtra("expires", accessToken.getExpiresIn());
                    nextState = ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS;
                } else {
                    LogFile.i(context,MainActivity.CHANNEL_ID, response.raw().toString());
                    LogFile.i(context,MainActivity.CHANNEL_ID, "refresh unsuccessful, attempting to authorize");
                    nextState = ProgramStateMachine.States.INITIAL_STATE;
                }
            } catch (Exception e) {
                LogFile.e(context,MainActivity.CHANNEL_ID, "exception in NetworkCalls.refreshAccessToken: ", e);
            }
        }
        data.putExtra("action", nextState.name());
        return data;
    }

    public static void getStatus(Handler handler, Context context, String token) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NetworkCalls.getStatus(context, token);
                Message m = Message.obtain();
                m.setData(intent.getExtras());
                handler.sendMessage(m);
            }
        });
        t.start();
    }

    private static Intent getStatus(Context context, String token) {
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");

        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);
        ProgramStateMachine.States nextState = ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS;
        String language = appInfo.getLanguage(VIN);

        if (MainActivity.checkInternetConnection(context)) {
            StatusService statusClient = NetworkServiceGenerators.createCarStatusService(StatusService.class, context);
            Call<CarStatus> call = statusClient.getStatus(token, language, Constants.APID, VIN);
            try {
                Response<CarStatus> response = call.execute();
                if (response.isSuccessful()) {
                    LogFile.i(context,MainActivity.CHANNEL_ID, "status successful....");
//                    String tmp =response.toString();
                    CarStatus car = response.body();
                    if (car.getStatus() == Constants.HTTP_SERVER_ERROR) {
                        LogFile.i(context,MainActivity.CHANNEL_ID, "server is broken");
                    } else if (car.getVehiclestatus() != null) {
                        Calendar lastRefreshTime = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        try {
                            lastRefreshTime.setTime(sdf.parse(car.getLastRefresh()));
                        } catch (ParseException e) {
                            LogFile.e(context, MainActivity.CHANNEL_ID, "exception in exception in NetworkCalls.getStatus: ", e);
                        }
                        long priorRefreshTime = appInfo.getLastRefreshTime(VIN);
                        if( priorRefreshTime < lastRefreshTime.toInstant().toEpochMilli()) {
                            appInfo.setCarStatus(VIN, car);
                        }
                        Notifications.checkLVBStatus(context, car, VIN);
                        Notifications.checkTPMSStatus(context, car, VIN);
                        nextState = ProgramStateMachine.States.HAVE_TOKEN_AND_STATUS;
                        LogFile.i(context,MainActivity.CHANNEL_ID, "got status");
                    } else {
                        nextState = ProgramStateMachine.States.ATTEMPT_TO_GET_VIN_AGAIN;
                        LogFile.i(context,MainActivity.CHANNEL_ID, "vehicle status is null");
                    }
                } else {
                    LogFile.i(context,MainActivity.CHANNEL_ID, response.raw().toString());
                    LogFile.i(context,MainActivity.CHANNEL_ID, "status UNSUCCESSFUL....");
                    // For either of these client errors, we probably need to refresh the access token
                    if (response.code() == Constants.HTTP_BAD_REQUEST || response.code() == Constants.HTTP_UNAUTHORIZED) {
                        nextState = ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN;
                    }
                }
            } catch (Exception e) {
                LogFile.e(context,MainActivity.CHANNEL_ID, "exception in NetworkCalls.getStatus: ", e);
            }
        }
        data.putExtra("action", nextState.name());
        return data;
    }

    public static void getOTAStatus(Handler handler, Context context, String token) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NetworkCalls.getOTAStatus(context, token);
                Message m = Message.obtain();
                m.setData(intent.getExtras());
                handler.sendMessage(m);
            }
        });
        t.start();
    }

    private static Intent getOTAStatus(Context context, String token) {
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");

        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);

        String language = appInfo.getLanguage(VIN);
        String country = appInfo.getCountry(VIN);

        if (MainActivity.checkInternetConnection(context)) {
            OTAStatusService OTAstatusClient = NetworkServiceGenerators.createOTAStatusService(OTAStatusService.class, context);
            Call<OTAStatus> call = OTAstatusClient.getOTAStatus(token, language, Constants.APID, country, VIN);
            try {
                Response<OTAStatus> response = call.execute();
                if (response.isSuccessful()) {
                    LogFile.i(context,MainActivity.CHANNEL_ID, "OTA status successful....");
                    appInfo.setOTAStatus(VIN, response.body());
                } else {
                    try {
                        if (response.errorBody().string().contains("UpstreamException")) {
                            OTAStatus status = new OTAStatus();
                            status.setError("UpstreamException");
                        }
                    } catch (Exception e) {
                        LogFile.e(context,MainActivity.CHANNEL_ID, "exception in NetworkCalls.getOTAStatus: ", e);
                    }
                    LogFile.i(context,MainActivity.CHANNEL_ID, response.raw().toString());
                    LogFile.i(context,MainActivity.CHANNEL_ID, "OTA UNSUCCESSFUL....");
                }
            } catch (Exception e) {
                LogFile.e(context,MainActivity.CHANNEL_ID, "exception in NetworkCalls.getOTAStatus: ", e);
            }
        }
        return data;
    }

    public static void remoteStart(Handler handler, Context context, String token) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NetworkCalls.execCommand(context, token, "engine", "start", "put");
                Message m = Message.obtain();
                m.setData(intent.getExtras());
                handler.sendMessage(m);
            }
        });
        t.start();
    }

    public static void remoteStop(Handler handler, Context context, String token) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NetworkCalls.execCommand(context, token, "engine", "start", "delete");
                Message m = Message.obtain();
                m.setData(intent.getExtras());
                handler.sendMessage(m);
            }
        });
        t.start();
    }

    public static void lockDoors(Handler handler, Context context, String token) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NetworkCalls.execCommand(context, token, "doors", "lock", "put");
                Message m = Message.obtain();
                m.setData(intent.getExtras());
                handler.sendMessage(m);
            }
        });
        t.start();
    }

    public static void unlockDoors(Handler handler, Context context, String token) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = NetworkCalls.execCommand(context, token, "doors", "lock", "delete");
                Message m = Message.obtain();
                m.setData(intent.getExtras());
                handler.sendMessage(m);
            }
        });
        t.start();
    }

    private static Intent execCommand(Context context, String token, String component, String operation, String request) {
        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.VIN_key), "");

        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);
        String language = appInfo.getLanguage(VIN);

        if (!MainActivity.checkInternetConnection(context)) {
            data.putExtra("action", COMMAND_NO_NETWORK);
        } else {
//            CommandService commandServiceClient = CommandServiceGenerator.createService(CommandService.class);
            CommandService commandServiceClient = NetworkServiceGenerators.createCommandService(CommandService.class, context);
            Call<CommandStatus> call = null;
            if (request.equals("put")) {
                call = commandServiceClient.putCommand(token, language,
                        Constants.APID, VIN, component, operation);
            } else {
                call = commandServiceClient.deleteCommand(token, language,
                        Constants.APID, VIN, component, operation);
            }
            try {
                Response<CommandStatus> response = call.execute();
                if (response.isSuccessful()) {
                    data.putExtra("action", COMMAND_SUCCESSFUL);
                    LogFile.i(context,MainActivity.CHANNEL_ID, "CMD successful....");
                } else {
                    data.putExtra("action", COMMAND_FAILED);
                    LogFile.i(context,MainActivity.CHANNEL_ID, response.raw().toString());
                    LogFile.i(context,MainActivity.CHANNEL_ID, "CMD UNSUCCESSFUL....");
                }
            } catch (Exception e) {
                data.putExtra("action", COMMAND_EXCEPTION);
                LogFile.e(context,MainActivity.CHANNEL_ID, "exception in NetworkCalls.putCommand: ", e);
            }
        }
        return data;
    }

    // Code for encryption and decryption of personal data
    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final String KEY_ALIAS = "MacheEWidget";
    private static final int GCM_IV_LENGTH = 12;
    private static KeyStore keyStore = null;

    // Generate a key in the Android Keystore
    private static void generateKey() {
        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(KEY_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setRandomizedEncryptionRequired(false)
                                .build());
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the application's secret key; password is randomly generated for the app
    private static java.security.Key getSecretKey(Context context, char[] password) throws Exception {
        generateKey();
        return keyStore.getKey(KEY_ALIAS, password);
    }

    // Encrypt
    public static String encrypt(Context context, char[] password, String input) throws Exception {
        //Prepare the nonce
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        Cipher c = Cipher.getInstance(AES_MODE);
        Key key = getSecretKey(context, password);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        c.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        byte[] encodedBytes = c.doFinal(input.getBytes(StandardCharsets.UTF_8));

        // Put IV and cipherText into a Base64 String for storage
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encodedBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(encodedBytes);
        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    public static String decrypt(Context context, char[] password, String input) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_MODE);
        Key key = getSecretKey(context, password);

        // Get byte[] back from stored string
        byte[] cipherBytes = Base64.getDecoder().decode(input);

        // Pull the IV out of the packet
        GCMParameterSpec gcmIv = new GCMParameterSpec(128, cipherBytes, 0, GCM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmIv);

        // Everything else is the ciphertext
        byte[] plainText = cipher.doFinal(cipherBytes, GCM_IV_LENGTH, cipherBytes.length - GCM_IV_LENGTH);
        return new String(plainText, StandardCharsets.UTF_8);
    }


}
