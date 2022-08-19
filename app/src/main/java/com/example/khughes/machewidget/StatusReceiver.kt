package com.example.khughes.machewidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.icu.text.MessageFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.format.DateUtils
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import java.text.ParseException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

private lateinit var info: InfoRepository
private const val MILLIS = 1000

class StatusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Set the next alarm
        nextAlarm(context)

        // Store time when we run the update;
        val appInfo = StoredData(context)
        appInfo.setLastAlarmTime()

        // Check if the user should be prompted to take a survey
        Notifications.surveyPrompt(context)

        // Check battery optimization
        Notifications.batteryOptimization(context)

        // Gather the app widget IDs for every widget
        val man = AppWidgetManager.getInstance(context)
        val appIds = ArrayList<Int>()
        appIds.addAll(
            Arrays.stream(
                man.getAppWidgetIds(
                    ComponentName(
                        context,
                        CarStatusWidget_1x5::class.java
                    )
                )
            ).boxed().collect(Collectors.toList())
        )
        appIds.addAll(
            Arrays.stream(
                man.getAppWidgetIds(
                    ComponentName(
                        context,
                        CarStatusWidget_2x5::class.java
                    )
                )
            ).boxed().collect(Collectors.toList())
        )
        appIds.addAll(
            Arrays.stream(
                man.getAppWidgetIds(
                    ComponentName(
                        context,
                        CarStatusWidget_5x5::class.java
                    )
                )
            ).boxed().collect(Collectors.toList())
        )

        // If any widgets have been removed, delete their VIN entry
        for (key in context.getSharedPreferences(
            Constants.WIDGET_FILE,
            Context.MODE_PRIVATE).all.keys) {
            if (key.startsWith(Constants.VIN_KEY)) {
                val appWidgetId = key.replace(Constants.VIN_KEY, "").toInt()
                if (!appIds.contains(appWidgetId)) {
                    context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit()
                        .remove(key).apply()
                }
            }
        }

        // If user doesn't want updates when Do Not Disturb is active, then see if it's active
        val zenMode = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(
                context.resources.getString(R.string.zenmode_key),
                false
            )
        if(zenMode) {
            try {
                // Non-zero when DnD is on
                if( Settings.Global.getInt(context.contentResolver, "zen_mode") != 0 ) {
                    return;
                }
            } catch (e: SettingNotFoundException) {
            }
        }

        // Do the actual update
        GlobalScope.launch {
            info = getInfo(context)

            val userInfo = info.getUser()
            if (userInfo == null) {
                LogFile.d(context, MainActivity.CHANNEL_ID, "StatusReceiver: no userinfo found")
            } else {
                val timeout = userInfo.expiresIn
                val time = LocalDateTime.now(ZoneId.systemDefault())
                val nowtime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                val userId = userInfo.userId
                val state = userInfo.programState

                LogFile.d(
                    context, MainActivity.CHANNEL_ID,
                    MessageFormat.format(
                        "StatusReceiver: time({0}), state({1}), battery optimization({2})",
                        (timeout - nowtime) / MILLIS,
                        state,
                        Utils.ignoringBatteryOptimizations(context)
                    )
                )

                // Check whether credentials are being saved
                val savingCredentials = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(
                        context.resources.getString(R.string.save_credentials_key),
                        true
                    )

                when ( state ) {
                    Constants.STATE_INITIAL_STATE -> {
                        LogFile.d(
                            context,
                            MainActivity.CHANNEL_ID,
                            "Initial state: how'd did the alarm go off (manual refresh)?"
                        )
                        cancelAlarm(context)
                        Notifications.loginRequired(context)
                        appInfo.incCounter(StoredData.STATUS_NOT_LOGGED_IN)
                    }
                    Constants.STATE_ATTEMPT_TO_GET_ACCESS_TOKEN -> {
                        if (savingCredentials) {
                            LogFile.d(
                                context,
                                MainActivity.CHANNEL_ID,
                                "Ok, trying to log in; wish me luck"
                            )
                            val encrypt = Encryption(context)
                            val username = encrypt.getPlaintextString(userInfo.username)
                            val password = encrypt.getPlaintextString(userInfo.password)
                            getAccess(context, username, password)
                            appInfo.incCounter(StoredData.STATUS_LOG_IN)
                        } else {
                            userInfo.programState = Constants.STATE_INITIAL_STATE
                            LogFile.d(
                                context,
                                MainActivity.CHANNEL_ID,
                                "Log-in required but credentials are not being saved: cancelling alarm"
                            )
                            cancelAlarm(context)
                            Notifications.loginRequired(context)
                            appInfo.incCounter(StoredData.STATUS_LOG_OUT)
                        }
                    }
                    Constants.STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN -> {
                        LogFile.d(context, MainActivity.CHANNEL_ID, "STILL need to refresh token")
                        getRefresh(context, userId, userInfo.refreshToken)
                        appInfo.incCounter(StoredData.STATUS_UPDATED)
                    }
                    Constants.STATE_HAVE_TOKEN -> {
                        LogFile.d(context, MainActivity.CHANNEL_ID, "need to get vehicle info")
                        getVehicleInfo(context, userId)
                        appInfo.incCounter(StoredData.STATUS_VEHICLE_INFO)
                    }
                    Constants.STATE_HAVE_TOKEN_AND_VIN -> {
                        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
                        val delayInMillis = sharedPref.getString(
                            context.resources.getString(R.string.update_frequency_key),
                            "10"
                        )!!.toInt() * 60 * MILLIS

                        // Find out when the list of vehicles was updated
                        var thenTime: Long = 0
                        val lastModified = userInfo.lastModified
                        lastModified?.let {
                            val cal = Calendar.getInstance()
                            val sdf = SimpleDateFormat(Constants.LASTMODIFIEDFORMAT, Locale.ENGLISH)
                            sdf.timeZone = TimeZone.getTimeZone("GMT")
                            try {
                                cal.time = sdf.parse(it)
                            } catch (e: ParseException) {
                            }
                            thenTime = cal.timeInMillis
                        }

                        // Since actions such as "Refresh" don't check the token's expiration, be sure to refresh if it would expire before
                        // the next update.
                        val calculation = timeout - delayInMillis - 5 * MILLIS - nowtime
                        LogFile.d(
                            context, MainActivity.CHANNEL_ID,
                            "Calculating time as $calculation"
                        )
                        if (timeout - delayInMillis - 5 * MILLIS < nowtime) {
                            LogFile.d(context, MainActivity.CHANNEL_ID, "Need to refresh token")
                            getRefresh(context, userId, userInfo.refreshToken)
                        } else if ((nowtime - thenTime) / (1000 * 60 * 60) > 1) {
                            LogFile.d(
                                context,
                                MainActivity.CHANNEL_ID,
                                "Going to check for vehicle changes"
                            )
                            getVehicleInfo(context, userId)
                        } else {
                            LogFile.d(
                                context,
                                MainActivity.CHANNEL_ID,
                                "Token good? Just grab info"
                            )
                            getStatus(context, userId)
                        }
                        appInfo.incCounter(StoredData.STATUS_UPDATED)
                    }
                    else -> {
                        if (savingCredentials) {
                            LogFile.d(
                                context,
                                MainActivity.CHANNEL_ID,
                                "Hmmm... How did I get here. Trying to login"
                            )
                            val encrypt = Encryption(context)
                            val username = encrypt.getPlaintextString(userInfo.username)
                            val password = encrypt.getPlaintextString(userInfo.password)
                            getAccess(context, username, password)
                        } else {
                            LogFile.d(
                                context,
                                MainActivity.CHANNEL_ID,
                                "Hmmm... How did I get here. Wish I could login"
                            )
                        }
                        appInfo.incCounter(StoredData.STATUS_UNKNOWN)
                    }
                }

                LogFile.d(
                    context, MainActivity.CHANNEL_ID,
                    MessageFormat.format(
                        "StatusReceiver status history: {0}({1}) {2}({3}) {4}({5}) {6}({7}) {8}({9}) {10}({11})",
                        StoredData.STATUS_NOT_LOGGED_IN,
                        appInfo.getCounter(StoredData.STATUS_NOT_LOGGED_IN),
                        StoredData.STATUS_LOG_OUT,
                        appInfo.getCounter(StoredData.STATUS_LOG_OUT),
                        StoredData.STATUS_LOG_IN,
                        appInfo.getCounter(StoredData.STATUS_LOG_IN),
                        StoredData.STATUS_UPDATED,
                        appInfo.getCounter(StoredData.STATUS_UPDATED),
                        StoredData.STATUS_VEHICLE_INFO,
                        appInfo.getCounter(StoredData.STATUS_VEHICLE_INFO),
                        StoredData.STATUS_UNKNOWN,
                        appInfo.getCounter(StoredData.STATUS_UNKNOWN)
                    )
                )

            }
        }
    }

    private fun getAccess(context: Context, username: String, password: String) {
        val h: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val action = bundle.getString("action")
                LogFile.i(
                    context, MainActivity.CHANNEL_ID,
                    "Access: $action"
                )
                if (action == Constants.STATE_HAVE_TOKEN) {
                    val userId = bundle.getString("usedId")
                    getVehicleInfo(context, userId)
                }
            }
        }
        NetworkCalls.getAccessToken(h, context, username, password)
    }

    private fun getRefresh(context: Context, userId: String, refreshToken: String) {
        val h: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val action = bundle.getString("action")
                LogFile.i(
                    context, MainActivity.CHANNEL_ID,
                    "Refresh: $action"
                )
                if (action == Constants.STATE_HAVE_TOKEN_AND_VIN) {
                    getStatus(context, userId)
                }
            }
        }
        NetworkCalls.refreshAccessToken(h, context, userId, refreshToken)
    }

    private fun getVehicleInfo(context: Context, userId: String?) {
        val h: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val action = bundle.getString("action")
                if (action == Constants.STATE_HAVE_TOKEN_AND_VIN) {
                    getStatus(context, userId)
                }
                LogFile.i(
                    context, MainActivity.CHANNEL_ID,
                    "VehicleInfo: $action"
                )
            }
        }
        NetworkCalls.getUserVehicles(h, context, userId)
    }

    private fun getStatus(context: Context, userId: String?) {
        val h: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val action = bundle.getString("action")
                LogFile.i(
                    context, MainActivity.CHANNEL_ID,
                    "Status: $action"
                )
                // Update the widgets, no mattery what.
                CarStatusWidget.updateWidget(context)
            }
        }
        NetworkCalls.getStatus(h, context, userId)
    }

    companion object {

        private fun getIntent(context: Context): Intent {
            return Intent(context, StatusReceiver::class.java).setAction("StatusReceiver")
        }

        @JvmStatic
        fun nextAlarm(context: Context) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val delay =
                sharedPref.getString(context.resources.getString(R.string.update_frequency_key), "10")!!
                    .toInt()
            if (delay != 0) {
                nextAlarm(context, delay * 60)
            }
        }

        @JvmStatic
        fun nextAlarm(context: Context, delay: Int) {
            val time = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(delay.toLong())
            val timeText = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss", Locale.US))
            LogFile.i(
                context, MainActivity.CHANNEL_ID,
                "StatusReceiver: next status alarm at $timeText"
            )
            val nextTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0,
                getIntent(context), PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.RTC_WAKEUP, nextTime] = pendingIntent
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                nextTime,
                DateUtils.SECOND_IN_MILLIS,
                pendingIntent
            )
        }

        @JvmStatic
        fun cancelAlarm(context: Context) {
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0,
                getIntent(context), PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(MainActivity.CHANNEL_ID, "StatusReceiver: cancelling status alarm")
        }

        // If no alarm is pending, start one
        @JvmStatic
        fun initateAlarm(context: Context) {
            if (PendingIntent.getBroadcast(
                    context, 0,
                    getIntent(context), PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                ) == null
            ) {
                nextAlarm(context)
            } else {
                Log.d(MainActivity.CHANNEL_ID, "StatusReceiver: no pending status alarm")
            }
        }
    }

    private suspend fun getInfo(context: Context): InfoRepository =
        coroutineScope {
            withContext(Dispatchers.IO) { InfoRepository(context) }
        }

}