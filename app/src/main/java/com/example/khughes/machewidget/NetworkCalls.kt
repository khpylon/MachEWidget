package com.example.khughes.machewidget

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import com.example.khughes.machewidget.CarStatusWidget.Companion.updateWidget
import com.example.khughes.machewidget.NetworkServiceGenerators.createAPIMPSService
import com.example.khughes.machewidget.NetworkServiceGenerators.createOAUTH2Service
import kotlinx.coroutines.*
import retrofit2.Call
import java.io.File
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.file.Files
import java.text.MessageFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import java.util.Locale
import java.util.UUID

class NetworkCalls {

    companion object {

        const val COMMAND_SUCCESSFUL = "Command successful."
        private const val COMMAND_FAILED = "Command failed."
        private const val COMMAND_EXCEPTION = "Exception occurred."

        private const val CMD_STATUS_COMPLETED = "COMPLETED"
        private const val CMD_STATUS_SUCCESS = "SUCCESS"
        private const val CMD_STATUS_INPROGRESS = "INPROGRESS"
        private const val CMD_STATUS_FAILED = "FAILED"
        private const val CMD_STATUS_QUEUED = "QUEUED"

        private const val OPERATION_STARTENGINE = "startEngine"
        private const val OPERATION_STOPENGINE = "stopEngine"
        private const val OPERATION_LOCKDOORS = "stopEngine"
        private const val OPERATION_UNLOCKDOORS = "stopEngine"
        private const val OPERATION_STATUS = "status"
        private const val OPERATION_STATUSREFRESH = "statusrefresh"

        suspend fun getAccessToken(
            context: Context?,
            code: String,
            info: InfoRepository
        ): Message = withContext(Dispatchers.IO) {
            val intent = getAccessToken(context!!, code, info)
            val m = Message.obtain()
            m.data = intent.extras
            m
        }

        private fun getAccessToken(
            context: Context,
            code: String,
            info: InfoRepository
        ): Intent {
            val data = Intent()
            var stage = 1

            val tokenId = TokenId()
            var token: String? = null

            if (checkInternetConnection(context)) {
                for (retry in 2 downTo 0) {
                    try {
                        val OAuth2Client = createOAUTH2Service(OAuth2Service::class.java, context)
                        var accessToken: AccessToken
                        var call: Call<AccessToken?>?

                        // Start by getting token we need for OAuth2 authentication
                        if (stage == 1) {
//                            token = Authenticate.newAuthenticate(context, "", "")
//                            if (token != null) {
//                                stage = 2
//                            }
                            token = code
                            stage = 2
                        }

                        // Next, try to get the actual token
                        if (stage == 2) {
                            call = OAuth2Client.getAccessToken(
                                token = "B2C_1A_signup_signin_common",
                                grantType = "authorization_code",
                                clientId = FordConnectConstants.CLIENTID,
                                clientSecret = FordConnectConstants.CLIENTSECRET,
                                code = token!!,
                                redirectURL = "https%3A%2F%2Flocalhost%3A3000"
                            )
                            val response = call!!.execute()
                            if (!response.isSuccessful) {
                                continue
                            }

                            // Create tokenId
                            accessToken = response.body()!!
                            token = UUID.randomUUID().toString()
                            tokenId.tokenId = token
                            tokenId.accessToken = "Bearer " + accessToken.accessToken!!
                            tokenId.refreshToken = accessToken.refreshToken!!

                            val time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(
                                accessToken.expiresIn!!.toLong()
                            )
                            val nextTime =
                                time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            tokenId.expiresIn = nextTime
                            tokenId.programState = Constants.STATE_HAVE_TOKEN_AND_VIN
                            info.insertTokenId(tokenId)
                            break
                        }
                    } catch (ee: SocketTimeoutException) {
                        LogFile.e(
                            MainActivity.CHANNEL_ID,
                            "java.net.SocketTimeoutException in NetworkCalls.getAccessToken"
                        )
                        LogFile.e(
                            MainActivity.CHANNEL_ID,
                            MessageFormat.format("    {0} retries remaining", retry)
                        )
                        try {
                            Thread.sleep((3 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                    } catch (e3: UnknownHostException) {
                        LogFile.e(
                            MainActivity.CHANNEL_ID,
                            "java.net.UnknownHostException in NetworkCalls.getAccessToken"
                        )
                        break
                    } catch (e1: java.lang.IllegalStateException) {
                        LogFile.e(
                            MainActivity.CHANNEL_ID,
                            "IllegalStateException in NetworkCalls.getAccessToken: ", e1
                        )
                        LogFile.e(
                            MainActivity.CHANNEL_ID,
                            MessageFormat.format("    {0} retries remaining", retry)
                        )
                        try {
                            Thread.sleep((3 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                    } catch (e: java.lang.Exception) {
                        LogFile.e(
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.getAccessToken: ",
                            e
                        )
                        break
                    }
                }
            }
            data.putExtra("tokenId", tokenId.tokenId)
            return data
        }

        private fun refreshAccessToken(
            context: Context,
            tokenId: String,
            info: InfoRepository,
        ): Intent {
            val data = Intent()
            var nextState = Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN
            val tokenInfo = info.getTokenId(tokenId)

            tokenInfo?.let {
                if (checkInternetConnection(context)) {
                    for (retry in 2 downTo 0) {
                        try {
                            val OAuth2Client =
                                createOAUTH2Service(OAuth2Service::class.java, context)
                            val call = OAuth2Client.refreshAccessToken(
                                token = "B2C_1A_signup_signin_common",
                                grantType = "refresh_token",
                                clientId = FordConnectConstants.CLIENTID,
                                clientSecret = FordConnectConstants.CLIENTSECRET,
                                refreshToken = tokenInfo.refreshToken!!
                            )
                            val response = call!!.execute()
                            if (response.isSuccessful) {
                                LogFile.i(MainActivity.CHANNEL_ID, "refresh successful")
                                val accessToken = response.body()

                                tokenInfo.accessToken = "Bearer " + accessToken!!.accessToken
                                tokenInfo.refreshToken = accessToken.refreshToken
                                val time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(
                                    accessToken.expiresIn!!.toLong()
                                )
                                val nextTime =
                                    time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                tokenInfo.expiresIn = nextTime

                                nextState = Constants.STATE_HAVE_TOKEN_AND_VIN
                            } else if (response.code() == Constants.HTTP_INTERNAL_SERVER_ERROR) {
                                LogFile.i(MainActivity.CHANNEL_ID, response.raw().toString())
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "refresh unsuccessful, will attempt again"
                                )
                                try {
                                    Thread.sleep((3 * 1000).toLong())
                                } catch (_: InterruptedException) {
                                }
                                continue
                            } else {
                                LogFile.i(MainActivity.CHANNEL_ID, response.raw().toString())
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "refresh unsuccessful, will retry later"
                                )
                                //                        nextState = Constants.STATE_ATTEMPT_TO_GET_ACCESS_TOKEN;
                            }
                            break
                        } catch (ee: SocketTimeoutException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "java.net.SocketTimeoutException in NetworkCalls.refreshAccessToken"
                            )
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                MessageFormat.format("    {0} retries remaining", retry)
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                        } catch (e3: UnknownHostException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "java.net.UnknownHostException in NetworkCalls.refreshAccessToken"
                            )
                            break
                        } catch (e1: java.lang.IllegalStateException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "IllegalStateException in NetworkCalls.refreshAccessToken(): ", e1
                            )
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                MessageFormat.format("    {0} retries remaining", retry)
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                        } catch (e: Exception) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.refreshAccessToken: ",
                                e
                            )
                            break
                        }
                    }
                }
                tokenInfo.programState = nextState
                info.setTokenId(tokenInfo)
            }
            data.putExtra("action", nextState)
            return data
        }

        suspend fun getVehicleList(
            context: Context,
            tokenId: String,
            info: InfoRepository
        ) = withContext(Dispatchers.IO) {

            info.getTokenId(tokenId)?.let {
                val APIMPSClient = createAPIMPSService(APIMPSService::class.java, context)

                if (checkForRefresh(context, it.tokenId!!, info)) {
                    for (retry in 2 downTo 0) {

                        val callVehicleList =
                            APIMPSClient.getVehicleList(it.accessToken!!)
                        val responseVehicleList = callVehicleList!!.execute()
                        if (!responseVehicleList.isSuccessful) {
                            continue
                        }

                        val newCars = responseVehicleList.body()
                        if (newCars != null) {
                            it.users = newCars.vehicles.size
                            info.setTokenId(it)
                            for (vehicle in newCars.vehicles) {
                                val vehicleId = vehicle.vehicleId
                                val existingVehicle = info.getVehicleById(vehicleId)
                                if (existingVehicle.carStatus.vehicle.vehicleId == "") {
                                    val callVehicle = APIMPSClient.getStatus(
                                        vehicleId,
                                        it.accessToken!!
                                    )
                                    val responseVehicle = callVehicle!!.execute()
                                    if (responseVehicle.isSuccessful) {
                                        val vehicleInfo = VehicleInfo()
                                        vehicleInfo.carStatus = responseVehicle.body()!!
                                        vehicleInfo.tokenId = it.tokenId
                                        if (vehicleInfo.carStatus.vehicle.nickName == "") {
                                            vehicleInfo.carStatus.vehicle.nickName = Constants.NO_NICKNAME
                                        }
                                        info.insertVehicle(vehicleInfo)

                                        getVehicleImage(
                                            context = context,
                                            vehicleId = vehicleInfo.carStatus.vehicle.vehicleId,
                                            info = info
                                        )
                                    }
                                } else {
                                    val tokenIdInfo = info.getTokenId(existingVehicle.tokenId)
                                    tokenIdInfo?.let {
                                        info.removeTokenIdUser(tokenIdInfo)
                                    }
                                    existingVehicle.tokenId = tokenId
                                    info.setVehicle(existingVehicle)
                                }
                            }
                            break
                        }
                    }
                }
            }
        }

        fun getStatus(
            context: Context,
            info: InfoRepository
        ): Intent {
            val data = Intent()
            var nextState = Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN

            // Iterate through all the vehicles in the database
            for (vehicle in info.vehicles) {

                // Get the vehicles ID
                val vehicleId = vehicle.carStatus.vehicle.vehicleId

                // Skip any vehicle which isn't enabled for use
                if (!vehicle.isEnabled) {
                    LogFile.i(
                        MainActivity.CHANNEL_ID,
                        "$vehicleId is disabled: skipping"
                    )
                    continue
                } else {
                    LogFile.i(
                        MainActivity.CHANNEL_ID,
                        "getting status for vehicleId $vehicleId"
                    )
                }

                // Assume the update won't succeed
                var statusUpdated = false

                if (checkForRefresh(context, vehicle.tokenId!!, info)) {

                    // Get token info for this vehicle
                    val accessToken = info.getTokenId(vehicle.tokenId)!!.accessToken

                    for (retry in 2 downTo 0) {
                        try {
                            // Try to get the latest car status
                            val statusClient =
                                createAPIMPSService(APIMPSService::class.java, context)

                            val callStatus = statusClient.getStatus(
                                vehicleId = vehicle.carStatus.vehicle.vehicleId,
                                accessToken = accessToken!!
                            )
                            val responseStatus = callStatus!!.execute()

                            if (responseStatus.isSuccessful) {
                                LogFile.i(MainActivity.CHANNEL_ID, "status successful.")

                                val car = responseStatus.body()

                                // TODO: does FordConnect API provide fast charging info?
//                                val update = updateChargingStatus(context, vehicle.carStatus, car, newCar)
//                                if (update != 0) {
//                                    data.putExtra(
//                                        context.getString(R.string.dcfc_active),
//                                        true
//                                    )
//                                }

                                // TODO: this probably isn't the correct format
                                val formatter = DateTimeFormatter.ofPattern(
                                    Constants.STATUSTIMEFORMAT,
                                    Locale.ENGLISH
                                )
                                val lastRefreshTIme =
                                    LocalDateTime.parse(car!!.vehicle.lastUpdated, formatter)
                                        .atZone(ZoneOffset.UTC)
                                        .withZoneSameInstant(ZoneId.systemDefault())
                                val currentRefreshTime = lastRefreshTIme.toInstant().toEpochMilli()

                                // If the charging status changes, reset the old charge station info so we know to update it later
                                val priorRefreshTime = vehicle.lastRefreshTime
                                if (priorRefreshTime <= currentRefreshTime) {
                                    if (car.vehicle.nickName == "") {
                                        car.vehicle.nickName = Constants.NO_NICKNAME
                                    }
                                    vehicle.carStatus = car
                                    vehicle.setLastUpdateTime()
                                    vehicle.lastRefreshTime = currentRefreshTime
                                    statusUpdated = true
                                }

                                // TODO: FordConnect API doesn't provide this?
//                                checkLVBStatus(context, car, info)
//                                checkTPMSStatus(context, car, info)

                                LogFile.i(MainActivity.CHANNEL_ID, "got status")
                                nextState = Constants.STATE_HAVE_TOKEN_AND_VIN
                            } else {
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    responseStatus.raw().toString()
                                )
                                LogFile.i(MainActivity.CHANNEL_ID, "status UNSUCCESSFUL.")
                                // For either of these client errors, we probably need to refresh the access token
                                if (responseStatus.code() == Constants.HTTP_BAD_REQUEST) {
                                    nextState = Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN
                                }
                            }

                            // If the vehicle info changed, commit
                            if (statusUpdated) {
                                info.setVehicle(vehicle)
                            }

                            break
                        } catch (e2: SocketTimeoutException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.getStatus",
                                e2
                            )
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                MessageFormat.format("    {0} retries remaining", retry)
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                        } catch (e2: IllegalStateException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.getStatus",
                                e2
                            )
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                MessageFormat.format("    {0} retries remaining", retry)
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                        } catch (e3: UnknownHostException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "java.net.UnknownHostException in NetworkCalls.getStatus"
                            )
                            // If the vehicle info changed, commit
                            if (statusUpdated) {
                                info.setVehicle(vehicle)
                            }
                            break
                        } catch (e: java.lang.Exception) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.getStatus: ",
                                e
                            )
                            // If the vehicle info changed, commit
                            if (statusUpdated) {
                                info.setVehicle(vehicle)
                            }
                            break
                        }
                    }
                }
            }
            data.putExtra("action", nextState)
            return data
        }

        // TODO: FordConnect API doesn't supply fast Charging info
        // Decide what to do with any charging data
//        fun updateChargingStatus(
//            context: Context,
//            infoCarStatus: CarStatus,
//            car: CarStatus,
//            newCar: NewCarStatus
//        ) : Int {
//            // It appears all vehicles contain a "xevPlugChargerStatus" field, and for
//            // gas or diesel its value is "CONNECTED".  So check that charging status
//            // information was also provided before checking the plug status
//            if (car.vehicle.vehicleStatus.chargingStatus != null &&
//                car.vehicle.vehicleStatus.plugStatus.value) {
//                // If vehicle is charging and we're supposed to grab data, do so
//                val queryCharging =
//                    PreferenceManager.getDefaultSharedPreferences(context)
//                        .getBoolean(
//                            context.resources
//                                .getString(R.string.check_charging_key), false
//                        )
//                if (queryCharging &&
//                    car.vehicle.vehicleStatus.chargingStatus!!.value == Constants.CHARGING_STATUS_CHARGING_DC
//                ) {
//                    val chargeInfo = DCFCInfo()
//
//                    // If there is no plug-in time, then we must have started charging
//                    if (infoCarStatus.vehicle.vehicleStatus.pluginTime == "") {
//                        car.vehicle.vehicleStatus.pluginTime = car.vehiclestatus.lastRefresh!!
//                        car.vehicle.vehicleStatus.initialDte = car.vehiclestatus.elVehDTE!!.value!!
//                    }
//                    // Otherwise re-use the prior information
//                    else {
//                        car.vehicle.vehicleStatus.pluginTime = infoCarStatus.vehiclestatus.pluginTime
//                        car.vehicle.vehicleStatus.initialDte = infoCarStatus.vehiclestatus.initialDte
//                    }
//
//                    // While charging, retain the original battery energy value.  This
//                    // is needed to calculate how much energy was added.
//                    if (car.vehicle.vehicleStatus.chargingStatus!!.value != Constants.CHARGING_STATUS_COMPLETE) {
//                        car.vehicle.vehicleStatus.xevBatteryEnergyRemaining =
//                            infoCarStatus.vehiclestatus.xevBatteryEnergyRemaining
//                    }
//
//                    // Calculate the amount of energy added for thsi update
//                    car.vehicle.vehicleStatus.chargeEnergy = (newCar.metrics.xevBatteryEnergyRemaining!!.value - car.vehiclestatus.xevBatteryEnergyRemaining) * 1000
//
//                    val reportDCFC =
//                        PreferenceManager.getDefaultSharedPreferences(context)
//                            .getBoolean(
//                                context.resources
//                                    .getString(R.string.check_dcfastcharging_key),
//                                false
//                            )
//                    if (reportDCFC) {
//                        chargeInfo.VIN = car.vehicle.vehicleStatus.vin
//                        chargeInfo.plugInTime = car.vehicle.vehicleStatus.pluginTime
//                        chargeInfo.power = car.vehicle.vehicleStatus.chargePower
//                        chargeInfo.chargeType = car.vehicle.vehicleStatus.chargeType
//                        chargeInfo.energy = (newCar.metrics.xevBatteryEnergyRemaining!!.value
//                                - car.vehicle.vehicleStatus.xevBatteryEnergyRemaining) * 1000
//                        chargeInfo.time = newCar.metrics.xevBatteryEnergyRemaining.updateTime
//                        chargeInfo.energy = car.vehicle.vehicleStatus.chargeEnergy
//                        chargeInfo.initialDte = car.vehicle.vehicleStatus.initialDte
//                        chargeInfo.currentDte = car.vehicle.vehicleStatus.elVehDTE?.value
//                        chargeInfo.batteryFillLevel = car.vehicle.vehicleStatus.batteryFillLevel?.value
//
//                        DCFC.updateChargingSession(
//                            context,
//                            chargeInfo
//                        )
//                    }
//                    LogFile.i(
//                        MainActivity.CHANNEL_ID,
//                        "received charge status response: power = "
//                                + car.vehicle.vehicleStatus.chargePower +
//                                ", energy = " + car.vehicle.vehicleStatus.chargeEnergy
//                    )
//                    return 1
//                }
//            } else {
//                if (car.vehicle.vehicleStatus.pluginTime != "") {
//                    car.vehicle.vehicleStatus.pluginTime = ""
//                }
//            }
//            return 0
//        }

        @JvmStatic
        fun getVehicleImage(
            context: Context,
            vehicleId: String,
            info: InfoRepository
        ) {
            // Create the images folder if necessary
            val imageDir = File(context.dataDir, Constants.IMAGES_FOLDER)
            if (!imageDir.exists()) {
                imageDir.mkdir()
            }

            // If the image doesn't exist, then attempt to download it
            val image = File(imageDir, "$vehicleId.png")
            if (!image.exists()) {

                // Find the vehicle's info
                val vehicle = info.getVehicleById(vehicleId)
                if (checkForRefresh(context = context, tokenId = vehicle.tokenId!!, info = info)) {
                    for (retry in 2 downTo 0) {
                        try {
                            val vehicleImageClient =
                                createAPIMPSService(
                                    APIMPSService::class.java,
                                    context
                                )
                            val call =
                                vehicleImageClient.getVehicleImage(
                                    vehicleId = vehicleId,
                                    accessToken = info.getTokenId(vehicle.tokenId)!!.accessToken!!,
                                    make = vehicle.carStatus.vehicle.make,
                                    model = vehicle.carStatus.vehicle.modelName,
                                    year = vehicle.carStatus.vehicle.modelYear
                                )
                            val response = call!!.execute()
                            if (response.isSuccessful) {
                                Files.copy(
                                    response.body()!!.byteStream(),
                                    image.toPath()
                                )
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "get vehicle image successful."
                                )
                                updateWidget(context)
                                return
                            } else {
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    response.raw().toString()
                                )
                                if (response.code() == Constants.HTTP_BAD_REQUEST) {
                                    LogFile.i(
                                        MainActivity.CHANNEL_ID,
                                        "get vehicle image UNSUCCESSFUL."
                                    )
                                }
                            }
                            Thread.sleep((1000).toLong())
                        } catch (ee: SocketTimeoutException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "java.net.SocketTimeoutException in NetworkCalls.getVehicleImage"
                            )
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                MessageFormat.format("    {0} retries remaining", retry)
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                        } catch (e3: UnknownHostException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "java.net.UnknownHostException in NetworkCalls.getVehicleImage"
                            )
                            return
                        } catch (e2: java.lang.IllegalStateException) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.getVehicleImage",
                                e2
                            )
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                MessageFormat.format("    {0} retries remaining", retry)
                            )
                            try {
                                Thread.sleep((3 * 1000).toLong())
                            } catch (_: InterruptedException) {
                            }
                        } catch (e: java.lang.Exception) {
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "exception in NetworkCalls.getVehicleImage: ",
                                e
                            )
                            return
                        }
                    }
                }
            }
        }

        fun remoteStart(handler: Handler, context: Context, vehicleId: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent: Intent =
                    execCommand(context, vehicleId, OPERATION_STARTENGINE)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        fun remoteStop(handler: Handler, context: Context, vehicleId: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent: Intent =
                    execCommand(context, vehicleId, OPERATION_STOPENGINE)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        fun lockDoors(handler: Handler, context: Context, vehicleId: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent: Intent =
                    execCommand(context, vehicleId, OPERATION_LOCKDOORS)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        fun unlockDoors(handler: Handler, context: Context, vehicleId: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent: Intent =
                    execCommand(context, vehicleId, OPERATION_UNLOCKDOORS)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        private suspend fun execCommand(
            context: Context,
            vehicleId: String,
            operation: String,
        ): Intent = coroutineScope {
            withContext(Dispatchers.IO) {
                val data = Intent()
                val info = InfoRepository(context)
                val vehicle = info.getVehicleById(vehicleId)
                if (checkForRefresh(context, vehicle.tokenId!!, info)) {
                    // Get the user info again in case a refresh updated the access token
                    try {
                        val commandServiceClient = createAPIMPSService(
                            APIMPSService::class.java, context
                        )

                        // Get token info for this vehicle
                        val accessToken = info.getTokenId(vehicle.tokenId)!!.accessToken

                        val call = commandServiceClient.postOperation(
                            vehicleId = vehicleId,
                            operation = operation,
                            accessToken = accessToken!!
                        )
                        val response = call!!.execute()
                        if (response.isSuccessful) {
                            val status = response.body()
                            if (status!!.status == CMD_STATUS_SUCCESS) {
                                LogFile.i(MainActivity.CHANNEL_ID, "CMD send successful.")
                                if (Looper.myLooper() == null) {
                                    Looper.prepare()
                                }
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.networkcalls_command_transmitted),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                // TODO: return operation and command ID in extra ?
                                data.putExtra(
                                    "action",
                                    execResponse(
                                        context = context,
                                        vehicleInfo = vehicle,
                                        operation = operation,
                                        info = info,
                                        idCode = status.commandId!!
                                    )
                                )
//                            } else if (status.status == CMD_REMOTE_START_LIMIT) {
//                                LogFile.i(
//                                    context,
//                                    MainActivity.CHANNEL_ID,
//                                    "CMD send UNSUCCESSFUL."
//                                )
//                                data.putExtra("action", COMMAND_REMOTE_START_LIMIT)
                            } else {
                                data.putExtra("action", COMMAND_EXCEPTION)
                                LogFile.i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    "CMD send unknown response."
                                )
                                LogFile.i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    response.raw().toString()
                                )
                            }
                        } else {
                            data.putExtra("action", COMMAND_FAILED)
                            LogFile.i(context, MainActivity.CHANNEL_ID, "CMD send UNSUCCESSFUL.")
                            LogFile.i(context, MainActivity.CHANNEL_ID, response.raw().toString())
                        }
                    } catch (e2: java.lang.IllegalStateException) {
                        LogFile.e(
                            MainActivity.CHANNEL_ID,
                            "exception in NetworkCalls.execCommand",
                            e2
                        )
                    } catch (e: java.lang.Exception) {
                        data.putExtra("action", COMMAND_EXCEPTION)
                        LogFile.e(
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
            vehicleInfo: VehicleInfo,
            operation: String,
            info: InfoRepository,
            idCode: String
        ): String? {
            // Delay 5 seconds before starting to check on status
            try {
                Thread.sleep((5 * 1000).toLong())
            } catch (e: InterruptedException) {
                LogFile.e(
                    MainActivity.CHANNEL_ID,
                    "exception in NetworkCalls.execResponse: ",
                    e
                )
            }
            val commandServiceClient = createAPIMPSService(
                APIMPSService::class.java, context
            )
            return try {
                for (retries in 0..9) {
                    val call = commandServiceClient.getOperationStatus(
                        vehicleId = vehicleInfo.carStatus.vehicle.vehicleId,
                        operation = operation,
                        startCommandId = idCode,
                        accessToken = info.getTokenId(vehicleInfo.tokenId!!)!!.accessToken!!
                    )
                    val response = call!!.execute()
                    if (response.isSuccessful) {
                        val status = response.body()
                        when (status!!.commandStatus) {
                            CMD_STATUS_COMPLETED -> {
                                LogFile.i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    "CMD response successful."
                                )
                                return COMMAND_SUCCESSFUL
                            }

                            CMD_STATUS_FAILED -> {
                                LogFile.i(context, MainActivity.CHANNEL_ID, "CMD response failed.")
                                return COMMAND_FAILED
                            }

                            CMD_STATUS_INPROGRESS -> LogFile.i(
                                context,
                                MainActivity.CHANNEL_ID,
                                "CMD response waiting."
                            )

                            else -> {
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "CMD response unknown: status = " + status.status
                                )
                                return COMMAND_FAILED
                            }
                        }
                    } else {
                        LogFile.i(MainActivity.CHANNEL_ID, response.raw().toString())
                        LogFile.i(MainActivity.CHANNEL_ID, "CMD response UNSUCCESSFUL.")
                        return COMMAND_FAILED
                    }
                    Thread.sleep((5 * 1000).toLong())
                }
                LogFile.i(context, MainActivity.CHANNEL_ID, "CMD timeout?")
                COMMAND_FAILED
            } catch (e: java.lang.Exception) {
                LogFile.e(
                    MainActivity.CHANNEL_ID,
                    "exception in NetworkCalls.execResponse: ",
                    e
                )
                COMMAND_EXCEPTION
            }
        }

        @JvmStatic
        fun updateStatus(
            handler: Handler,
            context: Context?,
            vehicleId: String?
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                val intent = updateStatus(context = context!!, vehicleId = vehicleId!!)
                val m = Message.obtain()
                m.data = intent.extras
                handler.sendMessage(m)
            }
        }

        private suspend fun updateStatus(
            context: Context,
            vehicleId: String
        ): Intent = coroutineScope {
            withContext(Dispatchers.IO) {
                val data = Intent()
                val info = InfoRepository(context)
                val vehInfo = info.getVehicleById(vehicleId)
                if (checkForRefresh(
                        context = context,
                        tokenId = vehInfo.tokenId!!,
                        info = info
                    )
                ) {
                    while (true) {
                        try {
                            val commandServiceClient = createAPIMPSService(
                                APIMPSService::class.java, context
                            )
                            val call =
                                commandServiceClient.postOperation(
                                    vehicleId = vehicleId,
                                    operation = OPERATION_STATUS,
                                    accessToken = info.getTokenId(vehInfo.tokenId!!)!!.accessToken!!
                                )
                            val response = call!!.execute()
                            if (response.isSuccessful) {
                                val status = response.body()
                                if (status!!.status == CMD_STATUS_SUCCESS) {
                                    LogFile.i(
                                        MainActivity.CHANNEL_ID,
                                        "updatestatus send successful."
                                    )
                                    if (Looper.myLooper() == null) {
                                        Looper.prepare()
                                    }
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.networkcalls_command_transmitted),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    data.putExtra(
                                        "action",
                                        pollStatus(
                                            context = context,
                                            vehicleId = vehicleId,
                                            info = info,
                                            idCode = status.commandId
                                        )
                                    )
                                } else {
                                    data.putExtra("action", COMMAND_EXCEPTION)
                                    LogFile.i(
                                        MainActivity.CHANNEL_ID,
                                        "updatestatus returned unknown response."
                                    )
                                    LogFile.i(
                                        context,
                                        MainActivity.CHANNEL_ID,
                                        response.raw().toString()
                                    )
                                }
                            } else {
                                data.putExtra("action", COMMAND_FAILED)
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "updatestatus send UNSUCCESSFUL."
                                )
                                LogFile.i(
                                    context,
                                    MainActivity.CHANNEL_ID,
                                    response.raw().toString()
                                )
                            }
                            break
                        } catch (e1: java.lang.IllegalStateException) {
                            data.putExtra("action", COMMAND_EXCEPTION)
                            LogFile.e(
                                MainActivity.CHANNEL_ID,
                                "IllegalStateException in NetworkCalls.updateStatus(): ", e1
                            )
                        } catch (e: java.lang.Exception) {
                            data.putExtra("action", COMMAND_EXCEPTION)
                            LogFile.e(
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
            context: Context,
            vehicleId: String,
            idCode: String?,
            info: InfoRepository
        ): String? {
            // Delay 5 seconds before starting to check on status
            try {
                Thread.sleep((10 * 1000).toLong())
            } catch (e: InterruptedException) {
                LogFile.e(MainActivity.CHANNEL_ID, "exception in NetworkCalls.pollStatus: ", e)
            }
            val commandServiceClient = createAPIMPSService(
                APIMPSService::class.java, context
            )
            val vehicle = info.getVehicleById(vehicleId)
            return try {
                for (retries in 0..9) {
                    val call = commandServiceClient.getOperationStatus(
                        vehicleId = vehicleId,
                        operation = OPERATION_STATUSREFRESH,
                        startCommandId = idCode!!,
                        accessToken = info.getTokenId(vehicle.tokenId)!!.accessToken!!
                    )
                    val response = call!!.execute()
                    if (response.isSuccessful) {
                        val status = response.body()
                        when (status!!.status) {
                            CMD_STATUS_SUCCESS -> {
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "poll response successful."
                                )
                                val now = Instant.now().toEpochMilli()
                                vehicle.lastForcedRefreshTime = now
                                var count = vehicle.forcedRefreshCount
                                if (count == 0L) {
                                    vehicle.initialForcedRefreshTime = now
                                }
                                vehicle.forcedRefreshCount = ++count
                                info.setVehicle(vehicle)
                                return COMMAND_SUCCESSFUL
                            }

                            CMD_STATUS_FAILED -> {
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "poll response failed."
                                )
                                return COMMAND_FAILED
                            }

                            CMD_STATUS_QUEUED -> {
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "poll response failed."
                                )
                            }

                            CMD_STATUS_INPROGRESS -> {
                                LogFile.i(
                                    MainActivity.CHANNEL_ID, "poll response waiting."
                                )
                            }

                            else -> {
                                LogFile.i(
                                    MainActivity.CHANNEL_ID,
                                    "poll response unknown: status = " + status.status
                                )
                                return COMMAND_FAILED
                            }
                        }
                    } else {
                        LogFile.i(MainActivity.CHANNEL_ID, response.raw().toString())
                        LogFile.i(MainActivity.CHANNEL_ID, "poll response UNSUCCESSFUL.")
                        return COMMAND_FAILED
                    }
                    Thread.sleep((3 * 1000).toLong())
                }
                LogFile.i(MainActivity.CHANNEL_ID, "poll timeout?")
                COMMAND_FAILED
            } catch (e: java.lang.Exception) {
                LogFile.e(MainActivity.CHANNEL_ID, "exception in NetworkCalls.pollStatus(): ", e)
                COMMAND_EXCEPTION
            }
        }

        private fun checkForRefresh(
            context: Context,
            tokenId: String,
            info: InfoRepository
        ): Boolean {
            val intent = refreshAccessToken(
                context = context,
                tokenId = tokenId,
                info = info
            )
            val action = intent.extras?.getString("action")
            return action == Constants.STATE_HAVE_TOKEN_AND_VIN
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
                val netStatus = networkInfo?.let {
                    @Suppress("DEPRECATION")
                    networkInfo.isConnected && networkInfo.isAvailable
                } ?: false
                LogFile.d(
                    MainActivity.CHANNEL_ID,
                    "NetworkCalls.checkInternetConnection() returns $netStatus"
                )
                netStatus
            } else {
                val netStatus = if (connManager.activeNetwork == null) {
                    false
                } else {
                    val networkInfo = connManager.activeNetwork ?: return false
                    val networkCapabilities = connManager.getNetworkCapabilities(networkInfo)
                    if (networkCapabilities == null) {
                        false
                    } else {
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                    }
                }
                LogFile.d(
                    MainActivity.CHANNEL_ID,
                    "NetworkCalls.checkInternetConnection() returns $netStatus"
                )
                netStatus
            }
        }
    }
}
