package com.example.khughes.machewidget

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.core.os.ConfigurationCompat
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.CarStatus.CarStatus
import com.example.khughes.machewidget.CarStatusWidget.Companion.updateWidget
import com.example.khughes.machewidget.LogFile.e
import com.example.khughes.machewidget.LogFile.i
import com.example.khughes.machewidget.NetworkServiceGenerators.createAcctAutonomicService
import com.example.khughes.machewidget.NetworkServiceGenerators.createAPIMPSService
import com.example.khughes.machewidget.NetworkServiceGenerators.createApiAutonomicService
import com.example.khughes.machewidget.NetworkServiceGenerators.createDIGITALSERVICESService
import com.example.khughes.machewidget.NetworkServiceGenerators.createUSAPICVService
import com.example.khughes.machewidget.Notifications.Companion.checkLVBStatus
import com.example.khughes.machewidget.Notifications.Companion.checkTPMSStatus
import com.example.khughes.machewidget.Vehicle.Companion.getModelYear
import com.example.khughes.machewidget.db.UserInfoDatabase
import com.example.khughes.machewidget.db.VehicleInfoDatabase
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.file.Files
import java.text.MessageFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class NetworkCalls {

    companion object {

        const val COMMAND_SUCCESSFUL = "Command successful."
        const val COMMAND_FAILED = "Command failed."
        const val COMMAND_NO_NETWORK = "Network error."
        const val COMMAND_EXCEPTION = "Exception occurred."
        const val COMMAND_REMOTE_START_LIMIT = "Cannot extend remote start time without driving."

        private const val CMD_STATUS_SUCCESS = 200
        private const val CMD_STATUS_INPROGRESS = 552
        private const val CMD_STATUS_FAILED = 411
        private const val CMD_REMOTE_START_LIMIT = 590

        @JvmStatic
        fun getAccessToken(
            handler: Handler,
            context: Context?,
            username: String?,
            password: String?
        ) {
            val t = Thread {
                val intent = getAccessToken(context!!, username, password!!)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
            t.start()
        }

        private fun getAccessToken(context: Context, username: String?, password: String): Intent {
            val data = Intent()
            var nextState = Constants.STATE_ATTEMPT_TO_GET_ACCESS_TOKEN
            var stage = 1
            val userDao = UserInfoDatabase.getInstance(context).userInfoDao()
            val userInfo = UserInfo()
//            val vehInfoDao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
//            val vehicles = vehInfoDao.findVehicleInfo()
            var userId: String? = null
            var token: String? = null

            if (username == null) {
                e(
                    context,
                    MainActivity.CHANNEL_ID,
                    "NetworkCalls.getAccessToken() called with null username?"
                )
            } else if (checkInternetConnection(context)) {
                for (retry in 2 downTo 0) {
                    try {
                        val OAuth2Client = createAPIMPSService(APIMPSService::class.java, context)
                        var accessToken: AccessToken
                        var call: Call<AccessToken?>?

                        // Start by getting token we need for OAuth2 authentication
                        if (stage == 1) {
                            token = Authenticate.newAuthenticate(context, username, password)
                            if (token == null) {
                                continue
                            } else if (token == Authenticate.ACCOUNT_BAD_USER_OR_PASSWORD) {
                                nextState = Constants.STATE_ATTEMPT_TO_GET_ACCESS_TOKEN
                                break
                            } else if (token == Authenticate.ACCOUNT_DISABLED_CODE) {
                                nextState = Constants.STATE_ACCOUNT_DISABLED
                                break
                            }
                            stage = 2
                        }

                        // Next, try to get the actual token
                        if (stage == 2) {
                            val jsonObject = JSONObject().put("idpToken", token)
                            val body = jsonObject.toString()
                                .toRequestBody("application/json; charset=utf-8".toMediaType())
                            call = OAuth2Client.getAccessToken(body)
                            val response = call!!.execute()
                            if (!response.isSuccessful) {
                                continue
                            }
                            accessToken = response.body()!!
                            userId = UUID.nameUUIDFromBytes(accessToken.userId!!.toByteArray())
                                .toString()

                            // Create user profile
                            userInfo.userId = userId
                            userInfo.accessToken = accessToken.accessToken
                            userInfo.refreshToken = accessToken.refreshToken
                            val time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(
                                accessToken.expiresIn!!.toLong()
                            )
                            val nextTime =
                                time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            userInfo.expiresIn = nextTime
                            val locale =
                                ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
                            if (locale == null) {
                                userInfo.country = Locale.US.country
                                userInfo.language = Locale.US.toLanguageTag()
                            } else {
                                userInfo.country = locale.country
                                userInfo.language = locale.toLanguageTag()
                            }
                            var units: String
                            if (locale == null || locale.country == Locale.US.country) {
                                userInfo.uomSpeed = "MPH"
                                userInfo.uomPressure = "PSI"
                                userInfo.uomDistance = 1
                                units = context.resources.getString(R.string.units_mphpsi)
                            } else {
                                userInfo.uomSpeed = "KPH"
                                userInfo.uomPressure = "BAR"
                                userInfo.uomDistance = 0
                                units = context.resources.getString(R.string.units_kphbar)
                            }
                            val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
                            edit.putString(context.resources.getString(R.string.units_key), units)
                            edit.commit()

                            val acctAutoClient = createAcctAutonomicService(
                                AcctAutonomicService::class.java,
                                context
                            )
                            call = acctAutoClient.getAccessToken(
                                userInfo.accessToken!!,
                                "fordpass", "fordpass-prod",
                                "urn:ietf:params:oauth:grant-type:token-exchange",
                                "urn:ietf:params:oauth:token-type:jwt"
                            )
                            val response2 = call!!.execute()
                            accessToken = response2.body()!!
                            userInfo.autoAccessToken = accessToken.accessToken
                            val autoTime = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(
                                accessToken.expiresIn!!.toLong()
                            )
                            val autoNextTime =
                                autoTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            userInfo.autoExpiresIn = autoNextTime

//                            if (vehicles.size > 0) {
//                                val VIN = vehicles[0].vin
//                                val apiAutoClient = createApiAutonomicService(ApiAutonomicService::class.java, context)
//                                val newcall = apiAutoClient.getStatus(VIN, "01-01-1970 00:00:00","Bearer " + autoAccessToken!!)
//                                val response = newcall!!.execute()
//                                val thestring = response.body()
//                                val status = thestring as NewCarStatus
//                                val cat = NewCarStatus.getCarStatus(status)
//                                LogFile.e(context,"tag",status.vin)
//                            }

                            userInfo.programState = nextState
                            userDao.deleteAllUserInfo()
                            userDao.insertUserInfo(userInfo)

                            // Remove any existing vehicles associated with other user IDs
                            val dao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
                            for (info in dao.findVehicleInfo()) {
                                if (info.userId != userId) {
                                    dao.deleteVehicleInfoByVIN(info.vin!!)
                                }
                            }
                            nextState =
                                if (VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
                                        .findVINsByUserId(userId).isEmpty()
                                ) {
                                    Constants.STATE_HAVE_TOKEN
                                } else {
                                    Constants.STATE_HAVE_TOKEN_AND_VIN
                                }

                            data.putExtra("userId", userId)
                            break
                        }
                    } catch (ee: SocketTimeoutException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "java.net.SocketTimeoutException in NetworkCalls.getAccessToken"
                        )
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            MessageFormat.format("    {0} retries remaining", retry)
                        )
                        try {
                            Thread.sleep((3 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                    } catch (e3: UnknownHostException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "java.net.UnknownHostException in NetworkCalls.getAccessToken"
                        )
                        break
                    } catch (e1: java.lang.IllegalStateException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "IllegalStateException in NetworkCalls.getAccessToken: ", e1
                        )
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            MessageFormat.format("    {0} retries remaining", retry)
                        )
                        try {
                            Thread.sleep((3 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                    } catch (e: java.lang.Exception) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.getAccessToken: ",
                            e
                        )
                        break
                    }
                }
            }
            userId?.let { userDao.updateProgramState(nextState, it) }
            data.putExtra("action", nextState)
            return data
        }

        private fun refreshAccessToken(
            context: Context,
            _userId: String,
            refreshToken: String
        ): Intent {
            var userId = _userId
            val data = Intent()
            var nextState = Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN
            val dao = UserInfoDatabase.getInstance(context)
                .userInfoDao()
            if (checkInternetConnection(context)) {
                val jsonObject = JSONObject().put("refresh_token", refreshToken)
                val body = jsonObject.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
                for (retry in 2 downTo 0) {
                    try {
                        val OAuth2Client = createAPIMPSService(APIMPSService::class.java, context)
                        val call = OAuth2Client.refreshAccessToken(body)
                        val response = call!!.execute()
                        if (response.isSuccessful) {
                            i(context, MainActivity.CHANNEL_ID, "refresh successful")
                            var accessToken = response.body()
                            val userInfo = dao.findUserInfo(userId)
                            val token = accessToken!!.accessToken

                            // If user ID is different, then we're using the old API. Update the databases entries
                            // with the new user ID and store in global preferences
                            val newUserId =
                                UUID.nameUUIDFromBytes(accessToken.userId!!.toByteArray())
                                    .toString()
                            if (newUserId != userId) {
                                val vehicleDao =
                                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
                                val vehicles = vehicleDao.findVehicleInfoByUserId(userId)
                                for (vehicle in vehicles) {
                                    vehicle.userId = newUserId
                                    vehicleDao.updateVehicleInfo(vehicle)
                                }
                                userInfo!!.userId = newUserId
                                userId = newUserId
                                val sharedPref =
                                    PreferenceManager.getDefaultSharedPreferences(context)
                                sharedPref.edit()
                                    .putString(
                                        context.resources.getString(R.string.userId_key),
                                        userId
                                    )
                                    .apply()
                            }
                            userInfo!!.accessToken = token
                            userInfo.refreshToken = accessToken.refreshToken
                            val time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(
                                accessToken.expiresIn!!.toLong()
                            )
                            val nextTime =
                                time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            userInfo.expiresIn = nextTime

                            val acctAutoClient = createAcctAutonomicService(
                                AcctAutonomicService::class.java,
                                context
                            )
                            val call = acctAutoClient.getAccessToken(
                                userInfo.accessToken!!,
                                "fordpass", "fordpass-prod",
                                "urn:ietf:params:oauth:grant-type:token-exchange",
                                "urn:ietf:params:oauth:token-type:jwt"
                            )
                            val response2 = call!!.execute()
                            accessToken = response2.body()!!
                            userInfo.autoAccessToken = accessToken.accessToken
                            val autoTime = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(
                                accessToken.expiresIn!!.toLong()
                            )
                            val autoNextTime =
                                autoTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            userInfo.autoExpiresIn = autoNextTime

                            dao.updateUserInfo(userInfo)
                            data.putExtra("userId", userId)
                            nextState = Constants.STATE_HAVE_TOKEN_AND_VIN
                        } else if (response.code() == Constants.HTTP_INTERNAL_SERVER_ERROR) {
                            i(context, MainActivity.CHANNEL_ID, response.raw().toString())
                            i(
                                context,
                                MainActivity.CHANNEL_ID,
                                "refresh unsuccessful, will attempt again"
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                            continue
                        } else {
                            i(context, MainActivity.CHANNEL_ID, response.raw().toString())
                            i(
                                context,
                                MainActivity.CHANNEL_ID,
                                "refresh unsuccessful, will retry later"
                            )
                            //                        nextState = Constants.STATE_ATTEMPT_TO_GET_ACCESS_TOKEN;
                        }
                        break
                    } catch (ee: SocketTimeoutException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "java.net.SocketTimeoutException in NetworkCalls.refreshAccessToken"
                        )
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            MessageFormat.format("    {0} retries remaining", retry)
                        )
                        try {
                            Thread.sleep((3 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                    } catch (e3: UnknownHostException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "java.net.UnknownHostException in NetworkCalls.refreshAccessToken"
                        )
                        break
                    } catch (e1: java.lang.IllegalStateException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "IllegalStateException in NetworkCalls.refreshAccessToken(): ", e1
                        )
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            MessageFormat.format("    {0} retries remaining", retry)
                        )
                        try {
                            Thread.sleep((3 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                    } catch (e: Exception) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.refreshAccessToken: ",
                            e
                        )
                        break
                    }
                }
            }
            dao.updateProgramState(nextState, userId)
            data.putExtra("action", nextState)
            return data
        }

        fun getStatus(handler: Handler, context: Context?, userId: String?) {
            val t = Thread {
                val intent = getStatus(context!!, userId!!)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
            t.start()
        }

        private fun getStatus(context: Context, userId: String): Intent {
            val userDao = UserInfoDatabase.getInstance(context)
                .userInfoDao()
            val infoDao = VehicleInfoDatabase.getInstance(context)
                .vehicleInfoDao()
            var userInfo = userDao.findUserInfo(userId)
            val data = Intent()
            var nextState = Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN
            LogFile.d(context, MainActivity.CHANNEL_ID, "userId = $userId")
            LogFile.d(
                context,
                MainActivity.CHANNEL_ID,
                "getting status for " + infoDao.findVehicleInfoByUserId(userId).size + " vehicles"
            )
            for (info in infoDao.findVehicleInfoByUserId(userId)) {
                val VIN = info.vin
                if (!info.isEnabled) {
                    i(
                        context, MainActivity.CHANNEL_ID,
                        "$VIN is disabled: skipping"
                    )
                    continue
                } else {
                    i(
                        context, MainActivity.CHANNEL_ID,
                        "getting status for VIN $VIN"
                    )
                }

                val forceUpdate = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(context.resources.getString(R.string.forceUpdate_key), false)
                if (forceUpdate) {
                    val time = LocalDateTime.now(ZoneId.systemDefault())
                    val nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    val lasttime = info.lastRefreshTime
                    LogFile.d(
                        context, MainActivity.CHANNEL_ID,
                        "last refresh was " + (nowtime - lasttime) / (1000 * 60) + " min ago"
                    )
                    LogFile.d(
                        context, MainActivity.CHANNEL_ID,
                        "last refresh was " + (nowtime - lasttime) / (1000 * 60) + " min ago"
                    )
                    if ((nowtime - lasttime) / (1000 * 60) > 6 * 60 &&
                        !(info.carStatus.vehiclestatus.deepSleepInProgress?.value ?: true) &&
                        (info.carStatus.vehiclestatus.battery?.batteryStatusActual?.value
                            ?: 0.0) > 12.0
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            updateStatus(context, info.vin)
                        }
                    }
                }
                var statusUpdated = false
//                val supportsOTA = info.isSupportsOTA

                if (checkInternetConnection(context) && checkForRefresh(context, userInfo!!)) {
                    userInfo = userDao.findUserInfo(userId)
                    val language = userInfo!!.language
                    val token = userInfo.accessToken

                    for (retry in 2 downTo 0) {
                        try {
                            // Try to get the latest car status
                            val statusClient =
                                createApiAutonomicService(ApiAutonomicService::class.java, context)
                            val autoAccessToken = userInfo.autoAccessToken
                            val callStatus = statusClient.getStatus(
                                VIN,
                                "01-01-1970 00:00:00",
                                "Bearer " + autoAccessToken
                            )
                            val responseStatus = callStatus!!.execute()

                            if (responseStatus.isSuccessful) {
                                i(context, MainActivity.CHANNEL_ID, "status successful.")
                                val newCar = responseStatus.body() as NewCarStatus
                                val car = NewCarStatus.getCarStatus(newCar)

                                val update = updateChargingStatus(context, info.carStatus, car, newCar)
                                if (update != 0) {
                                    data.putExtra(
                                        context.getString(R.string.dcfc_active),
                                        true
                                    )
                                }

                                val lastRefreshTime = Calendar.getInstance()
                                val sdf =
                                    SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
                                sdf.timeZone = TimeZone.getTimeZone("UTC")
                                lastRefreshTime.time = sdf.parse(car.lastRefresh) as Date
                                val currentRefreshTime =
                                    lastRefreshTime.toInstant().toEpochMilli()

                                // If the charging status changes, reset the old charge station info so we know to update it later
                                val priorRefreshTime = info.lastRefreshTime
                                if (priorRefreshTime <= currentRefreshTime) {
                                    info.carStatus = car
                                    info.setLastUpdateTime()
                                    info.lastRefreshTime = currentRefreshTime
                                    statusUpdated = true
                                }

                                checkLVBStatus(context, car, info)
                                checkTPMSStatus(context, car, info)
                                i(context, MainActivity.CHANNEL_ID, "got status")
                                nextState = Constants.STATE_HAVE_TOKEN_AND_VIN
                            } else {
                                i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    responseStatus.raw().toString()
                                )
                                i(context, MainActivity.CHANNEL_ID, "status UNSUCCESSFUL.")
                                // For either of these client errors, we probably need to refresh the access token
                                if (responseStatus.code() == Constants.HTTP_BAD_REQUEST) {
                                    nextState = Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN
                                }
                            }

                            // Don't bother checking the OTA status if we've seen the vehicle doesn't support them
//                        if (supportsOTA) {
//                            // Try to get the OTA update status
//                            Call<OTAStatus> callOTA = OTAstatusClient.getOTAStatus(token, language, Constants.APID, country, VIN);
//                            Response<OTAStatus> responseOTA = callOTA.execute();
//                            if (responseOTA.isSuccessful()) {
//                                LogFile.i(context, MainActivity.CHANNEL_ID, "OTA status successful.");
//                                OTAStatus status = responseOTA.body();
//
//                                // Check to see if it looks like the vehicle support OTA updates
//                                if (!Misc.OTASupportCheck(status.getOtaAlertStatus())) {
//                                    LogFile.i(context, MainActivity.CHANNEL_ID, "This vehicle doesn't support OTA updates.");
//                                    info.setSupportsOTA(false);
//                                }
//                                // Only save the status if there is something in the fuseResponse
//                                else if (status.getOtaAlertStatus() != null && status.getFuseResponseList() != null) {
//
//                                    // Look for an OTA record for this vehicle and correlation Id
//                                    List<OTAInfo> otaInfoList = otaDao.findOTAInfoByCorrelationId(
//                                            status.getFuseResponse().getFuseResponseList().get(0).getOemCorrelationId(), VIN);
//                                    Boolean match = false;
//
//                                    // If there are records, see if anything matches the current OTA information
//                                    if (otaInfoList != null && otaInfoList.size() > 0) {
//                                        String date = status.getFuseResponse().getFuseResponseList().get(0).getDeploymentCreationDate();
//                                        String currentUTCOTATime = status.getOTADateTime();
//                                        long currentOTATime = OTAViewActivity.convertDateToMillis(currentUTCOTATime);
//                                        // Look for a matching OTA update on this vehicle
//
//                                        for (OTAInfo otaInfo : otaInfoList) {
//                                            // If the deployment creation dates are equal, we have a match
//                                            if (otaInfo.getResponseList().getDeploymentCreationDate().equals(date)) {
//                                                // If the information is newer, update the database
//                                                String thisUTCOTATime = otaInfo.toOTAStatus().getOTADateTime();
//                                                long thisOTATime = OTAViewActivity.convertDateToMillis(thisUTCOTATime);
//                                                if (currentOTATime > thisOTATime) {
//                                                    otaInfo.fromOTAStatus(status);
//                                                    otaDao.updateOTAInfo(otaInfo);
//                                                }
//
//                                                // In any case, don't create a new database entry
//                                                match = true;
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                    // If there wasn't a match, create a new entry
//                                    if (match == false) {
//                                        OTAInfo otaInfo = new OTAInfo(VIN);
//                                        otaInfo.fromOTAStatus(status);
//                                        otaDao.insertOTAInfo(otaInfo);
//                                    }
//                                    info.fromOTAStatus(status);
//                                }
//                                statusUpdated = true;
//                            } else {
//                                try {
//                                    if (responseOTA.errorBody().string().contains("UpstreamException")) {
//                                        OTAStatus status = new OTAStatus();
//                                        status.setError("UpstreamException");
//                                    }
//                                } catch (Exception e) {
//                                    LogFile.e(context, MainActivity.CHANNEL_ID, "exception in NetworkCalls.getStatus: ", e);
//                                }
//                                LogFile.i(context, MainActivity.CHANNEL_ID, responseStatus.raw().toString());
//                                LogFile.i(context, MainActivity.CHANNEL_ID, "OTA UNSUCCESSFUL.");
//                            }
//                        } else {
//                            LogFile.i(context, MainActivity.CHANNEL_ID, "OTA not supported: skipping check");
//                        }
                            i(context, MainActivity.CHANNEL_ID, "OTA currently bypassed")

                            // If the vehicle info changed, commit
                            if (statusUpdated) {
                                infoDao.updateVehicleInfo(info)
                            }

//                        // get vehicle color information
//                        Call<VehicleInfo> thing = statusClient.getVehicleInfo(token, Constants.APID, VIN);
//                        Response<VehicleInfo> othething = thing.execute();
                            break
                        } catch (e2: SocketTimeoutException) {
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.getStatus",
                                e2
                            )
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                MessageFormat.format("    {0} retries remaining", retry)
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                        } catch (e2: IllegalStateException) {
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.getStatus",
                                e2
                            )
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                MessageFormat.format("    {0} retries remaining", retry)
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                        } catch (e3: UnknownHostException) {
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                "java.net.UnknownHostException in NetworkCalls.getStatus"
                            )
                            // If the vehicle info changed, commit
                            if (statusUpdated) {
                                infoDao.updateVehicleInfo(info)
                            }
                            break
                        } catch (e: java.lang.Exception) {
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.getStatus: ",
                                e
                            )
                            // If the vehicle info changed, commit
                            if (statusUpdated) {
                                infoDao.updateVehicleInfo(info)
                            }
                            break
                        }
                    }
                }
            }
            userDao.updateProgramState(nextState, userId)
            data.putExtra("action", nextState)
            return data
        }

        // Decide what to do with any charging data
        fun updateChargingStatus(
            context: Context,
            infoCarStatus: CarStatus,
            car: CarStatus,
            newCar: NewCarStatus
        ) : Int {
            if (car.vehiclestatus.plugStatus?.value == 1) {
                // If vehicle is charging and we're supposed to grab data, do so
                val queryCharging =
                    PreferenceManager.getDefaultSharedPreferences(context)
                        .getBoolean(
                            context.resources
                                .getString(R.string.check_charging_key), false
                        )
                if (queryCharging &&
                    car.vehiclestatus.chargingStatus!!.value == Constants.CHARGING_STATUS_CHARGING_DC
                ) {
                    val chargeInfo = DCFCInfo()

                    // If there is no plug-in time, then we must have started charging
                    if (infoCarStatus.vehiclestatus.pluginTime == "") {
                        car.vehiclestatus.pluginTime = car.vehiclestatus.lastRefresh!!
                        car.vehiclestatus.initialDte = car.vehiclestatus.elVehDTE!!.value!!
                    }
                    // Otherwise re-use the prior information
                    else {
                        car.vehiclestatus.pluginTime = infoCarStatus.vehiclestatus.pluginTime
                        car.vehiclestatus.initialDte = infoCarStatus.vehiclestatus.initialDte
                    }

                    // While charging, retain the original battery energy value.  This
                    // is needed to calculate how much energy was added.
                    if (car.vehiclestatus.chargingStatus!!.value != Constants.CHARGING_STATUS_COMPLETE) {
                        car.vehiclestatus.xevBatteryEnergyRemaining =
                            infoCarStatus.vehiclestatus.xevBatteryEnergyRemaining
                    }

                    // Calculate the amount of energy added for thsi update
                    car.vehiclestatus.chargeEnergy = (newCar.metrics.xevBatteryEnergyRemaining!!.value - car.vehiclestatus.xevBatteryEnergyRemaining) * 1000

                    val reportDCFC =
                        PreferenceManager.getDefaultSharedPreferences(context)
                            .getBoolean(
                                context.resources
                                    .getString(R.string.check_dcfastcharging_key),
                                false
                            )
                    if (reportDCFC) {
                        chargeInfo.plugInTime = car.vehiclestatus.pluginTime
                        chargeInfo.power = car.vehiclestatus.chargePower
                        chargeInfo.chargeType = car.vehiclestatus.chargeType
                        chargeInfo.energy = (newCar.metrics.xevBatteryEnergyRemaining!!.value
                                - car.vehiclestatus.xevBatteryEnergyRemaining) * 1000
                        chargeInfo.time = newCar.metrics.xevBatteryEnergyRemaining.updateTime
                        chargeInfo.energy = car.vehiclestatus.chargeEnergy
                        chargeInfo.initialDte = car.vehiclestatus.initialDte
                        chargeInfo.currentDte = car.vehiclestatus.elVehDTE?.value
                        chargeInfo.batteryFillLevel = car.vehiclestatus.batteryFillLevel?.value

                        DCFC.updateChargingSession(
                            context,
                            chargeInfo
                        )
                    }
                    i(
                        context,
                        MainActivity.CHANNEL_ID,
                        "received charge status response: power = "
                                + car.vehiclestatus.chargePower +
                                ", energy = " + car.vehiclestatus.chargeEnergy
                    )
                    return 1
                }
            } else {
                if (car.vehiclestatus.pluginTime != "") {
                    car.vehiclestatus.pluginTime = ""
                }
            }
            return 0
        }

        fun getStatus(
            handler: Handler,
            context: Context?,
            userInfo: UserInfo?,
            VIN: String?,
            nickname: String?
        ) {
            val t = Thread {
                val intent = getStatus(context!!, userInfo!!, VIN!!, nickname!!)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
            t.start()
        }

        private fun getStatus(
            context: Context,
            tmpUserInfo: UserInfo,
            VIN: String,
            nickname: String
        ): Intent {
            val userDao = UserInfoDatabase.getInstance(context)
                .userInfoDao()
            val infoDao = VehicleInfoDatabase.getInstance(context)
                .vehicleInfoDao()
            val userId = tmpUserInfo.userId
            val language = tmpUserInfo.language
            val data = Intent()
            var nextState = Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN
            LogFile.d(context, MainActivity.CHANNEL_ID, "userId = $userId")
            LogFile.d(
                context,
                MainActivity.CHANNEL_ID,
                "getting status for " + infoDao.findVehicleInfoByUserId(
                    userId!!
                ).size + " vehicles"
            )

//        VehicleInfo info = infoDao.findVehicleInfoByVIN(VIN);
            i(
                context, MainActivity.CHANNEL_ID,
                "getting status for VIN $VIN"
            )
            if (checkInternetConnection(context) && checkForRefresh(context, tmpUserInfo)) {
                val userInfo = userDao.findUserInfo(userId)
                val token = userInfo!!.accessToken

                val statusClient =
                    createApiAutonomicService(ApiAutonomicService::class.java, context)

                for (retry in 2 downTo 0) {
                    try {
                        // Try to get the latest car status
                        val autoAccessToken = userInfo.autoAccessToken
                        val callStatus = statusClient.getStatus(
                            VIN,
                            "01-01-1970 00:00:00",
                            "Bearer " + autoAccessToken
                        )
                        val responseStatus = callStatus!!.execute()

                        if (responseStatus.isSuccessful) {
                            i(context, MainActivity.CHANNEL_ID, "status successful.")
                            val newCar = responseStatus.body() as NewCarStatus
                            val car = NewCarStatus.getCarStatus(newCar)

                            val lastRefreshTime = Calendar.getInstance()
                            val cal = Calendar.getInstance()
                            val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            cal.time = sdf.parse(car.lastRefresh) as Date
                            val currentRefreshTime =
                                lastRefreshTime.toInstant().toEpochMilli()


//                            val lastRefreshTime = Calendar.getInstance()
//                            val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
//                            sdf.timeZone = TimeZone.getTimeZone("UTC")
//                            var currentRefreshTime: Long = 0
//                            try {
//                                lastRefreshTime.time = sdf.parse(car.lastRefresh)
//                                currentRefreshTime = lastRefreshTime.toInstant().toEpochMilli()
//                            } catch (e: ParseException) {
//                                e(
//                                    context,
//                                    MainActivity.CHANNEL_ID,
//                                    "exception in NetworkCalls.getStatus: ",
//                                    e
//                                )
//                            }

                            val info = VehicleInfo()
                            info.vin = VIN
                            info.nickname = nickname
                            info.userId = userId
                            info.carStatus = car
                            info.setLastUpdateTime()
                            info.lastRefreshTime = currentRefreshTime
                            infoDao.insertVehicleInfo(info)

                            // If this vehicle is electric, update global setting
                            if (car.isPropulsionElectric(car.propulsion) ||
                                car.isPropulsionPHEV(car.propulsion)
                            ) {
                                val appInfo = StoredData(context)
                                appInfo.electricVehicles = true
                            }
                            i(context, MainActivity.CHANNEL_ID, "got status")
                            nextState = Constants.STATE_HAVE_TOKEN_AND_VIN
                        } else {
                            i(
                                context,
                                MainActivity.CHANNEL_ID,
                                responseStatus.raw().toString()
                            )
                            i(context, MainActivity.CHANNEL_ID, "status UNSUCCESSFUL.")
                            nextState = Constants.STATE_ACCOUNT_DISABLED
                        }
                        break
                    } catch (e2: SocketTimeoutException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.getStatus",
                            e2
                        )
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            MessageFormat.format("    {0} retries remaining", retry)
                        )
                        try {
                            Thread.sleep((3 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                    } catch (e2: java.lang.IllegalStateException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.getStatus",
                            e2
                        )
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            MessageFormat.format("    {0} retries remaining", retry)
                        )
                        try {
                            Thread.sleep((3 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                    } catch (e3: UnknownHostException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "java.net.UnknownHostException in NetworkCalls.getStatus"
                        )
                        // If the vehicle info changed, commit
                        break
                    } catch (e: java.lang.Exception) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.getStatus: ",
                            e
                        )
                        // If the vehicle info changed, commit
                        break
                    }
                }
            }
            userDao.updateProgramState(nextState, userId)
            data.putExtra("action", nextState)
            return data
        }

        @JvmStatic
        fun getVehicleImage(context: Context, VIN: String, country: String) {
            // Create the images folder if necessary
            val imageDir = File(context.dataDir, Constants.IMAGES_FOLDER)
            if (!imageDir.exists()) {
                imageDir.mkdir()
            }
            val modelYear = getModelYear(VIN).toString()
            if (checkInternetConnection(context)) {
                for (angle in 1..5) {
                    val image = File(imageDir, VIN + "_angle" + angle + ".png")
                    if (!image.exists()) {
                        val t = Thread {
                            for (retry in 2 downTo 0) {
                                try {
                                    val vehicleImageClient =
                                        createDIGITALSERVICESService(
                                            DigitalServicesService::class.java,
                                            context
                                        )
                                    val call =
                                        vehicleImageClient.getVehicleImage(
                                            Constants.APID,
                                            VIN,
                                            modelYear,
                                            country,
                                            angle.toString()
                                        )
                                    val response = call!!.execute()
                                    if (response.isSuccessful) {
                                        Files.copy(
                                            response.body()!!.byteStream(),
                                            image.toPath()
                                        )
                                        i(
                                            context,
                                            MainActivity.CHANNEL_ID,
                                            "vehicle image $angle successful."
                                        )
                                        updateWidget(context)
                                    } else {
                                        i(
                                            context,
                                            MainActivity.CHANNEL_ID,
                                            response.raw().toString()
                                        )
                                        if (response.code() == Constants.HTTP_BAD_REQUEST) {
                                            i(
                                                context,
                                                MainActivity.CHANNEL_ID,
                                                "vehicle image $angle UNSUCCESSFUL."
                                            )
                                        }
                                    }
                                    break
                                } catch (ee: SocketTimeoutException) {
                                    e(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        "java.net.SocketTimeoutException in NetworkCalls.getVehicleImage"
                                    )
                                    e(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        MessageFormat.format("    {0} retries remaining", retry)
                                    )
                                    try {
                                        Thread.sleep((3 * 1000).toLong())
                                    } catch (_: InterruptedException) {
                                    }
                                } catch (e3: UnknownHostException) {
                                    e(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        "java.net.UnknownHostException in NetworkCalls.getVehicleImage"
                                    )
                                    break
                                } catch (e2: java.lang.IllegalStateException) {
                                    e(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        "exception in NetworkCalls.getVehicleImage",
                                        e2
                                    )
                                    e(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        MessageFormat.format("    {0} retries remaining", retry)
                                    )
                                    try {
                                        Thread.sleep((3 * 1000).toLong())
                                    } catch (_: InterruptedException) {
                                    }
                                } catch (e: java.lang.Exception) {
                                    e(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        "exception in NetworkCalls.getVehicleImage: ",
                                        e
                                    )
                                    break
                                }
                            }
                        }
                        t.start()
                    }
                }
            }
        }

        fun remoteStart(handler: Handler, context: Context, VIN: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent: Intent =
                    execCommand(context, VIN, "v5", "engine", "start", "put")
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        fun remoteStop(handler: Handler, context: Context, VIN: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent: Intent =
                    execCommand(context, VIN, "v5", "engine", "start", "delete")
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        fun lockDoors(handler: Handler, context: Context, VIN: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent: Intent =
                    execCommand(context, VIN, "v2", "doors", "lock", "put")
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        fun unlockDoors(handler: Handler, context: Context, VIN: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent: Intent =
                    execCommand(context, VIN, "v2", "doors", "lock", "delete")
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        private fun checkForRefresh(context: Context, user: UserInfo): Boolean {
            val MILLIS = 1000

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val delayInMillis = sharedPref.getString(
                context.resources.getString(R.string.update_frequency_key),
                "10"
            )!!.toInt() * 60 * MILLIS

            val userId = user.userId as String
            val timeout = user.expiresIn
            val time = LocalDateTime.now(ZoneId.systemDefault())
            val nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            return if (true) { // (timeout - delayInMillis - 10 * MILLIS < nowtime) {
                val intent = refreshAccessToken(
                    context,
                    userId,
                    user.refreshToken!!
                )
                val action = intent.extras?.getString("action")
                action == Constants.STATE_HAVE_TOKEN_AND_VIN
            } else {
                true
            }
        }

        private suspend fun execCommand(
            context: Context,
            VIN: String,
            version: String,
            component: String,
            operation: String,
            request: String
        ): Intent = coroutineScope {
            withContext(Dispatchers.IO) {
                val vehInfo = VehicleInfoDatabase.getInstance(context)
                    .vehicleInfoDao().findVehicleInfoByVIN(VIN)
                val tmpUserInfo = UserInfoDatabase.getInstance(context)
                    .userInfoDao().findUserInfo(vehInfo!!.userId!!)
                val data = Intent()
                if (!checkInternetConnection(context)) {
                    data.putExtra("action", COMMAND_NO_NETWORK)
                } else if (!checkForRefresh(context, tmpUserInfo!!)) {
                    data.putExtra("action", COMMAND_EXCEPTION)
                } else {
                    // Get the user info again in case a refresh updated the access token
                    val userInfo = UserInfoDatabase.getInstance(context)
                        .userInfoDao().findUserInfo(vehInfo.userId!!)
                    val token = userInfo!!.accessToken
                    try {
                        val commandServiceClient = createUSAPICVService(
                            USAPICVService::class.java, context
                        )
                        val call: Call<CommandStatus?>? = if (request == "put") {
                            commandServiceClient.putCommand(
                                token, Constants.APID,
                                version, VIN, component, operation
                            )
                        } else {
                            commandServiceClient.deleteCommand(
                                token, Constants.APID,
                                version, VIN, component, operation
                            )
                        }
                        val response = call!!.execute()
                        if (response.isSuccessful) {
                            val status = response.body()
                            if (status!!.status == CMD_STATUS_SUCCESS) {
                                i(context, MainActivity.CHANNEL_ID, "CMD send successful.")
                                if (Looper.myLooper() == null) {
                                    Looper.prepare()
                                }
                                Toast.makeText(context, "Command transmitted.", Toast.LENGTH_SHORT)
                                    .show()
                                data.putExtra(
                                    "action",
                                    execResponse(
                                        context,
                                        token!!,
                                        VIN,
                                        component,
                                        operation,
                                        status.commandId!!
                                    )
                                )
                            } else if (status.status == CMD_REMOTE_START_LIMIT) {
                                i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    "CMD send UNSUCCESSFUL."
                                )
                                data.putExtra("action", COMMAND_REMOTE_START_LIMIT)
                            } else {
                                data.putExtra("action", COMMAND_EXCEPTION)
                                i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    "CMD send unknown response."
                                )
                                i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    response.raw().toString()
                                )
                            }
                        } else {
                            data.putExtra("action", COMMAND_FAILED)
                            i(context, MainActivity.CHANNEL_ID, "CMD send UNSUCCESSFUL.")
                            i(context, MainActivity.CHANNEL_ID, response.raw().toString())
                        }
                    } catch (e2: java.lang.IllegalStateException) {
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.execCommand",
                            e2
                        )
                    } catch (e: java.lang.Exception) {
                        data.putExtra("action", COMMAND_EXCEPTION)
                        e(
                            context,
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.execCommand: ",
                            e
                        )
                    }
                }
                data
            }
        }

        private fun execResponse(
            context: Context,
            token: String,
            VIN: String,
            component: String,
            operation: String,
            idCode: String
        ): String? {
            // Delay 5 seconds before starting to check on status
            try {
                Thread.sleep((5 * 1000).toLong())
            } catch (e: InterruptedException) {
                e(
                    context,
                    MainActivity.CHANNEL_ID,
                    "exception in NetworkCalls.execResponse: ",
                    e
                )
            }
            val commandServiceClient = createUSAPICVService(
                USAPICVService::class.java, context
            )
            return try {
                for (retries in 0..9) {
                    val call = commandServiceClient.getCommandResponse(
                        token,
                        Constants.APID, VIN, component, operation, idCode
                    )
                    val response = call!!.execute()
                    if (response.isSuccessful) {
                        val status = response.body()
                        when (status!!.status) {
                            CMD_STATUS_SUCCESS -> {
                                i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    "CMD response successful."
                                )
                                return COMMAND_SUCCESSFUL
                            }

                            CMD_STATUS_FAILED -> {
                                i(context, MainActivity.CHANNEL_ID, "CMD response failed.")
                                return COMMAND_FAILED
                            }

                            CMD_STATUS_INPROGRESS -> i(
                                context,
                                MainActivity.CHANNEL_ID,
                                "CMD response waiting."
                            )

                            else -> {
                                i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    "CMD response unknown: status = " + status.status
                                )
                                return COMMAND_FAILED
                            }
                        }
                    } else {
                        i(context, MainActivity.CHANNEL_ID, response.raw().toString())
                        i(context, MainActivity.CHANNEL_ID, "CMD response UNSUCCESSFUL.")
                        return COMMAND_FAILED
                    }
                    Thread.sleep((2 * 1000).toLong())
                }
                i(context, MainActivity.CHANNEL_ID, "CMD timeout?")
                COMMAND_FAILED
            } catch (e: java.lang.Exception) {
                e(
                    context,
                    MainActivity.CHANNEL_ID,
                    "exception in NetworkCalls.execResponse: ",
                    e
                )
                COMMAND_EXCEPTION
            }
        }

        @JvmStatic
        fun updateStatus(handler: Handler, context: Context?, VIN: String?) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent = updateStatus(context, VIN)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        private suspend fun updateStatus(
            context: Context?,
            VIN: String?
        ): Intent = coroutineScope {
            withContext(Dispatchers.IO) {
                val data = Intent()
                val userDao = UserInfoDatabase.getInstance(context!!)
                    .userInfoDao()
                val infoDao = VehicleInfoDatabase.getInstance(context)
                    .vehicleInfoDao()
                val vehInfo = infoDao.findVehicleInfoByVIN(VIN!!)
                val tmpUserInfo = userDao.findUserInfo(vehInfo!!.userId!!)
                if (!checkInternetConnection(context)) {
                    data.putExtra("action", COMMAND_NO_NETWORK)
                } else if (!checkForRefresh(context, tmpUserInfo!!)) {
                    data.putExtra("action", COMMAND_EXCEPTION)
                } else {
                    // Get the user info again in case a refresh updated the access token
                    val userInfo = UserInfoDatabase.getInstance(context)
                        .userInfoDao().findUserInfo(vehInfo.userId!!)
                    val token = userInfo!!.accessToken

                    while (true) {
                        try {
                            val commandServiceClient = createUSAPICVService(
                                USAPICVService::class.java, context
                            )
                            val call: Call<CommandStatus?>? =
                                commandServiceClient.putStatus(token, Constants.APID, VIN)
                            val response = call!!.execute()
                            if (response.isSuccessful) {
                                val status = response.body()
                                if (status!!.status == CMD_STATUS_SUCCESS) {
                                    i(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        "statusrefresh send successful."
                                    )
                                    if (Looper.myLooper() == null) {
                                        Looper.prepare()
                                    }
                                    Toast.makeText(
                                        context,
                                        "Command transmitted.",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    data.putExtra(
                                        "action",
                                        pollStatus(context, token, vehInfo, status.commandId)
                                    )
                                } else if (status.status == CMD_REMOTE_START_LIMIT) {
                                    i(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        "statusrefresh send UNSUCCESSFUL."
                                    )
                                    data.putExtra("action", COMMAND_REMOTE_START_LIMIT)
                                } else {
                                    data.putExtra("action", COMMAND_EXCEPTION)
                                    i(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        "statusrefresh send unknown response."
                                    )
                                    i(context, MainActivity.CHANNEL_ID, response.raw().toString())
                                }
                            } else {
                                data.putExtra("action", COMMAND_FAILED)
                                i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    "statusrefresh send UNSUCCESSFUL."
                                )
                                i(context, MainActivity.CHANNEL_ID, response.raw().toString())
                            }
                            break
                        } catch (e1: java.lang.IllegalStateException) {
                            data.putExtra("action", COMMAND_EXCEPTION)
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                "IllegalStateException in NetworkCalls.updateStatus(): ", e1
                            )
                        } catch (e: java.lang.Exception) {
                            data.putExtra("action", COMMAND_EXCEPTION)
                            e(
                                context,
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.updateStatus(): ",
                                e
                            )
                        }
                        Thread.sleep((3 * 1000).toLong())
                    }
                }
                data
            }
        }

        private fun pollStatus(
            context: Context?,
            token: String?,
            vehInfo: VehicleInfo,
            idCode: String?
        ): String? {
            // Delay 5 seconds before starting to check on status
            try {
                Thread.sleep((10 * 1000).toLong())
            } catch (e: InterruptedException) {
                e(context!!, MainActivity.CHANNEL_ID, "exception in NetworkCalls.pollStatus: ", e)
            }
            val VIN = vehInfo.vin
            val commandServiceClient = createUSAPICVService(
                USAPICVService::class.java, context
            )
            return try {
                for (retries in 0..9) {
                    val call = commandServiceClient.pollStatus(token, Constants.APID, VIN, idCode)
                    val response = call!!.execute()
                    if (response.isSuccessful) {
                        val status = response.body()
                        when (status!!.status) {
                            CMD_STATUS_SUCCESS -> {
                                i(context!!, MainActivity.CHANNEL_ID, "poll response successful.")
                                val now = Instant.now().toEpochMilli()
                                vehInfo.lastForcedRefreshTime = now
                                var count = vehInfo.forcedRefreshCount
                                if (count == 0L) {
                                    vehInfo.initialForcedRefreshTime = now
                                }
                                vehInfo.forcedRefreshCount = ++count
                                VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
                                    .updateVehicleInfo(vehInfo)
                                return COMMAND_SUCCESSFUL
                            }

                            CMD_STATUS_FAILED -> {
                                i(context!!, MainActivity.CHANNEL_ID, "poll response failed.")
                                return COMMAND_FAILED
                            }

                            CMD_STATUS_INPROGRESS -> i(
                                context!!, MainActivity.CHANNEL_ID, "poll response waiting."
                            )

                            else -> {
                                i(
                                    context!!,
                                    MainActivity.CHANNEL_ID,
                                    "poll response unknown: status = " + status.status
                                )
                                return COMMAND_FAILED
                            }
                        }
                    } else {
                        i(context!!, MainActivity.CHANNEL_ID, response.raw().toString())
                        i(context, MainActivity.CHANNEL_ID, "poll response UNSUCCESSFUL.")
                        return COMMAND_FAILED
                    }
                    Thread.sleep((3 * 1000).toLong())
                }
                i(context!!, MainActivity.CHANNEL_ID, "poll timeout?")
                COMMAND_FAILED
            } catch (e: java.lang.Exception) {
                e(context!!, MainActivity.CHANNEL_ID, "exception in NetworkCalls.pollStatus(): ", e)
                COMMAND_EXCEPTION
            }
        }

        @JvmStatic
        private fun checkInternetConnection(context: Context): Boolean {
            // Get Connectivity Manager
            val connManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Details about the currently active default data network
            return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                @Suppress("DEPRECATION")
                val networkInfo = connManager.activeNetworkInfo
                networkInfo?.let {
                    @Suppress("DEPRECATION")
                    networkInfo.isConnected && networkInfo.isAvailable
                } ?: false
            } else {
                val networkInfo = connManager.activeNetwork ?: return false
                val networkCapabilities = connManager.getNetworkCapabilities(networkInfo)
                networkCapabilities?.let {
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                } ?: false
            }
        }
    }
}