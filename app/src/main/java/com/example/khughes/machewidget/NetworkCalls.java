package com.example.khughes.machewidget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class NetworkCalls {
    private static AccessTokenService fordClient = AccessTokenServiceGenerator.createFordService(AccessTokenService.class);
    private static AccessTokenService OAuth2Client = AccessTokenServiceGenerator.createOAuth2Service(AccessTokenService.class);
    private static StatusService statusClient = StatusServiceGenerator.createService(StatusService.class);
    private static OTAStatusService OTAstatusClient = OTAStatusServiceGenerator.createService(OTAStatusService.class);

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
        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);
//        ProgramStateMachine state = new ProgramStateMachine(appInfo.getProgramState());
        ProgramStateMachine state = new ProgramStateMachine(ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN);
        ProgramStateMachine.States nextState;

        if (!MainActivity.checkInternetConnection(context)) {
            nextState = state.FSM(false, false, false, false, false);
//             nextState = state.networkDown();
        } else {
            Call<AccessToken> call = fordClient.getAccessToken(Constants.CLIENTID, "password", username, password);
            try {
                Response<AccessToken> response = call.execute();
                if (response.isSuccessful()) {
                    AccessToken accessToken = response.body();
                    data.putExtra("access_token", accessToken.getAccessToken());
//                    data.putExtra("refresh_token", accessToken.getRefreshToken());
//                    data.putExtra("expires", accessToken.getExpiresIn());

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
                            appInfo.setLanguage(accessToken.getUserProfile().getLanguage());
                            appInfo.setCountry(accessToken.getUserProfile().getCountry());
                            appInfo.setPressureUnits(accessToken.getUserProfile().getUomPressure());
                            appInfo.setDistanceUnits(accessToken.getUserProfile().getUomDistance());
                            appInfo.setSpeedUnits(accessToken.getUserProfile().getUomSpeed());
                            nextState = state.FSM(true, true, false, false, false);
//                     nextState = state.loginGood();
                        } else {
                            nextState = state.FSM(true, false, false, false, false);
//                     nextState = state.networkUpLoginBad();
                        }
                    } catch (IOException e) {
                        nextState = state.FSM(true, false, false, false, false);
//                 nextState = state.networkUpLoginBad();
                    }
                } else {
                    nextState = state.FSM(true, false, false, false, false);
//                     nextState = state.networkUpLoginBad();
                }
            } catch (IOException e) {
                nextState = state.FSM(true, false, false, false, false);
//                 nextState = state.networkUpLoginBad();
                Log.e(MainActivity.CHANNEL_ID, "exception in NetworkCalls.getAccessToken: ", e);
            }
        }
        appInfo.setProgramState(nextState);
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
        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);
//        ProgramStateMachine state = new ProgramStateMachine(appInfo.getProgramState());
        ProgramStateMachine state = new ProgramStateMachine(ProgramStateMachine.States.ATTEMPT_TO_GET_ACCESS_TOKEN);
        ProgramStateMachine.States nextState;

        if (!MainActivity.checkInternetConnection(context)) {
            nextState = state.FSM(false, false, false, false, false);
//             nextState = state.networkDown();
        } else {
            Map<String, String> jsonParams = new ArrayMap<>();
            jsonParams.put("refresh_token", token);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

            Call<AccessToken> call = OAuth2Client.refreshAccessToken(body);
            try {
                Response<AccessToken> response = call.execute();
                Log.i(MainActivity.CHANNEL_ID, "refresh here....");
                if (response.isSuccessful()) {
                    Log.i(MainActivity.CHANNEL_ID, "refresh successful");
                    AccessToken accessToken = response.body();
                    data.putExtra("access_token", accessToken.getAccessToken());
                    data.putExtra("refresh_token", accessToken.getRefreshToken());
                    data.putExtra("expires", accessToken.getExpiresIn());
                    nextState = state.FSM(true, true, true, false, false);
//                     nextState = state.goodVIN();
                } else {
                    nextState = state.FSM(true, false, true, false, false);
//                     nextState = state.loginBad();
                }
            } catch (IOException e) {
                nextState = state.FSM(true, false, false, false, false);
//                 nextState = state.loginBad();
                Log.e(MainActivity.CHANNEL_ID, "exception in NetworkCalls.refreshAccessToken: ", e);
            }
        }
        appInfo.setProgramState(nextState);
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
        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);
//        ProgramStateMachine state = new ProgramStateMachine(appInfo.getProgramState());
        ProgramStateMachine state = new ProgramStateMachine(ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS);
        ProgramStateMachine.States nextState;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String VIN = sharedPref.getString(context.getResources().getString(R.string.VIN_key), "Null");

        if (!MainActivity.checkInternetConnection(context)) {
            nextState = state.FSM(false, true, false, false, false);
//             nextState = state.networkDown();
        } else {
            Call<CarStatus> call = statusClient.getStatus(token, VIN);
            try {
                Response<CarStatus> response = call.execute();
                if (response.isSuccessful()) {
                    Log.i(MainActivity.CHANNEL_ID, "status successful....");
//                    String tmp =response.toString();
                    CarStatus car = response.body();
                    if (car.getStatus() == Constants.SERVER_ERROR) {
                        nextState = state.FSM(true, true, false, false, true);
//                         nextState = state.serverDown();
                        Log.i(MainActivity.CHANNEL_ID, "server is broken");
                    } else if (car.getVehiclestatus() != null) {
                        appInfo.setCarStatus(car);
                        Notifications.checkLVBStatus(context, car);
                        Notifications.checkTPMSStatus(context, car);
                        nextState = state.FSM(true, true, true, false, false);
//                        nextState = state.goodVIN();
                        Log.i(MainActivity.CHANNEL_ID, "got status");
                    } else {
                        nextState = state.FSM(true, true, false, false, false);
//                         nextState = state.badVIN();
                        Log.i(MainActivity.CHANNEL_ID, "vehicle status is null");
                    }
                } else {
                    nextState = state.FSM(true, true, false, false, false);
//                    nextState = state.loginBad();
                    Log.i(MainActivity.CHANNEL_ID, response.raw().toString());
                    Log.i(MainActivity.CHANNEL_ID, "status UNSUCCESSFUL....");
                }
            } catch (IOException e) {
                nextState = state.FSM(true, true, false, false, false);
//                 nextState = state.loginBad();
                Log.e(MainActivity.CHANNEL_ID, "exception in NetworkCalls.getStatus: ", e);
            }
        }
        appInfo.setProgramState(nextState);
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
        Intent data = new Intent();
        StoredData appInfo = new StoredData(context);
//        ProgramStateMachine state = new ProgramStateMachine(appInfo.getProgramState());
        ProgramStateMachine state = new ProgramStateMachine(ProgramStateMachine.States.ATTEMPT_TO_GET_VEHICLE_STATUS);
        ProgramStateMachine.States nextState;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String VIN = sharedPref.getString(context.getResources().getString(R.string.VIN_key), "Null");
        String country = appInfo.getCountry();

        if (!MainActivity.checkInternetConnection(context)) {
            nextState = state.FSM(false, true, false, false, false);
//             nextState = state.networkDown();
        } else {
            Call<OTAStatus> call = OTAstatusClient.getOTAStatus(token, country, VIN);
            try {
                Response<OTAStatus> response = call.execute();
                if (response.isSuccessful()) {
                    Log.i(MainActivity.CHANNEL_ID, "OTA status successful....");
                    appInfo.setOTAStatus(response.body());
                } else {
                    nextState = state.FSM(true, true, false, false, false);
//                    nextState = state.loginBad();
                    Log.i(MainActivity.CHANNEL_ID, response.raw().toString());
                    Log.i(MainActivity.CHANNEL_ID, "OTA UNSUCCESSFUL....");
                }
            } catch (IOException e) {
                nextState = state.FSM(true, true, false, false, false);
//                 nextState = state.loginBad();
                Log.e(MainActivity.CHANNEL_ID, "exception in NetworkCalls.getOTAStatus: ", e);
            }
        }
        nextState = state.FSM(true, true, true, false, false);
        appInfo.setProgramState(nextState);
        data.putExtra("action", nextState.name());
        return data;
    }

}
