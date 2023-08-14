package com.example.khughes.machewidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.TimeZone


val VINA = "3FMTK3R75MMA00001"
val VINB = "3FMTK3R75MMA00002"

class TestReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        testStuff(context)
    }

    companion object {

        private val REQUESTCODE = 90125
        private val VIN1_info = "{ \"vin\": \"3FMTK3R75MMA00001\", \"chargeLocationName\": \"ChargePoint - Sparks NV\", \"chargeType\": \"DcCharging\", \"initialDte\": 186, \"network\": \"ChargePoint\", \"plugInTime\": \"2023-08-09T11:24:00Z\"}"
        private val VIN2_info = "{ \"vin\": \"3FMTK3R75MMA00002\", \"chargeLocationName\": \"EVGo - Elp Grove\", \"chargeType\": \"DcCharging\", \"initialDte\": 186, \"network\": \"EVGo\", \"plugInTime\": \"2023-08-09T11:27:00Z\"}"
        private val VIN1_updates = listOf(

            "{ \"time\": \"2023-08-09T11:25:29Z\", \"power\": 110554.8, \"energy\": 6649.6 }",
            "{ \"time\": \"2023-08-09T11:26:03Z\", \"power\": 111165.6, \"energy\": 10200.3 }",
            "{ \"time\": \"2023-08-09T11:26:36Z\", \"power\": 111165.6, \"energy\": 10200.3 }",
            "{ \"time\": \"2023-08-09T11:27:13Z\", \"power\": 111165.6, \"energy\": 10200.3 }",
            "{ \"time\": \"2023-08-09T11:27:47Z\", \"power\": 111776.4, \"energy\": 12932.1 }",
            "{ \"time\": \"2023-08-09T11:28:20Z\", \"power\": 111776.4, \"energy\": 12932.1 }",
            "{ \"time\": \"2023-08-09T11:28:56Z\", \"power\": 111776.4, \"energy\": 12932.1 }",
            "{ \"time\": \"2023-08-09T11:29:32Z\", \"power\": 112387.2, \"energy\": 15710.3 }",
            "{ \"time\": \"2023-08-09T11:30:05Z\", \"power\": 112998, \"energy\": 17687.8 }",
            "{ \"time\": \"2023-08-09T11:30:39Z\", \"power\": 112998, \"energy\": 17687.8 }",
            "{ \"time\": \"2023-08-09T11:31:13Z\", \"power\": 112998, \"energy\": 17687.8 }",
            "{ \"time\": \"2023-08-09T11:31:49Z\", \"power\": 113088, \"energy\": 21092.1 }",
            "{ \"time\": \"2023-08-09T11:32:22Z\", \"power\": 110519.9, \"energy\": 22115.7 }",
            "{ \"time\": \"2023-08-09T11:32:57Z\", \"power\": 107759.7, \"energy\": 23112 }",
            "{ \"time\": \"2023-08-09T11:33:30Z\", \"power\": 105542.8, \"energy\": 23938.8 }",
            "{ \"time\": \"2023-08-09T11:34:04Z\", \"power\": 101925, \"energy\": 24972.4 }")

        private val VIN2_updates = listOf(
            "{ \"time\": \"2023-08-09T11:27:26Z\", \"power\": 97389.6, \"energy\": 27625.5 }",
            "{ \"time\": \"2023-08-09T11:27:59Z\", \"power\": 96124.8, \"energy\": 28782 }",
            "{ \"time\": \"2023-08-09T11:28:33Z\", \"power\": 95898.3, \"energy\": 29104.4 }",
            "{ \"time\": \"2023-08-09T11:29:07Z\", \"power\": 95145.6, \"energy\": 30267 }",
            "{ \"time\": \"2023-08-09T11:29:40Z\", \"power\": 94509.8, \"energy\": 31501.3 }",
            "{ \"time\": \"2023-08-09T11:30:15Z\", \"power\": 94509.8, \"energy\": 31501.3 }",
            "{ \"time\": \"2023-08-09T11:30:50Z\", \"power\": 94509.8, \"energy\": 31501.3 }",
            "{ \"time\": \"2023-08-09T11:31:25Z\", \"power\": 94000, \"energy\": 33798.7 }",
            "{ \"time\": \"2023-08-09T11:31:59Z\", \"power\": 93496, \"energy\": 34369.9 }",
            "{ \"time\": \"2023-08-09T11:32:33Z\", \"power\": 92476, \"energy\": 35917.2 }",
            "{ \"time\": \"2023-08-09T11:33:06Z\", \"power\": 91718, \"energy\": 36503.3 }",
            "{ \"time\": \"2023-08-09T11:33:41Z\", \"power\": 90668, \"energy\": 37487.8 }",
            "{ \"time\": \"2023-08-09T11:34:16Z\", \"power\": 89382.6, \"energy\": 38608.6 }",
            "{ \"time\": \"2023-08-09T11:35:02Z\", \"power\": 93000, \"energy\": 20479.2 }"
        )

        @JvmStatic
        fun testStuff(context: Context) {
            val sessions = DCFC.getChargingSessions(context)

            val gson = GsonBuilder().create()

            val lastIndex = sessions.lastIndex
            LogFile.e(context,MainActivity.CHANNEL_ID, "last VIN is " + sessions[lastIndex].VIN)

            if (sessions[lastIndex].VIN != VINB) {
                setAlarm(context, 15)
                val session = sessions[lastIndex]
                val info = gson.fromJson(VIN1_info, DCFCInfo::class.java) as DCFCInfo

                if (session.VIN != VINA) {
                    // state 1 -> state 2
                    val update = gson.fromJson(VIN1_updates[0], DCFCUpdate::class.java) as DCFCUpdate
                    info.time = update.time
                    info.power = update.power
                    info.energy = update.energy
                    DCFC.updateChargingSession(context, info)
                    LogFile.e(context,MainActivity.CHANNEL_ID, "state 1")
                    LogFile.e(context,MainActivity.CHANNEL_ID, gson.toJson(info))
                } else {
                    // state 2
                    val update = gson.fromJson(
                        VIN1_updates[session.updates.size],
                        DCFCUpdate::class.java
                    ) as DCFCUpdate
                    info.time = update.time
                    info.power = update.power
                    info.energy = update.energy

                    DCFC.updateChargingSession(context, info)
                    LogFile.e(context,MainActivity.CHANNEL_ID, "state 2")
                    LogFile.e(context,MainActivity.CHANNEL_ID, gson.toJson(info))

                    if (session.updates.lastIndex == 5 || session.updates.lastIndex == 6) {
                        // state 2 -> state 3
                        try {
                            Thread.sleep((2 * 1000).toLong())
                        } catch (_: InterruptedException) {
                        }
                        val info = gson.fromJson(VIN2_info, DCFCInfo::class.java) as DCFCInfo
                        val update =
                            gson.fromJson(VIN2_updates[session.updates.lastIndex - 5], DCFCUpdate::class.java) as DCFCUpdate
                        info.time = update.time
                        info.power = update.power
                        info.energy = update.energy
                        DCFC.updateChargingSession(context, info)
                        LogFile.e(context,MainActivity.CHANNEL_ID, gson.toJson(info))
                    }
                }
            } else {
                LogFile.e(context,MainActivity.CHANNEL_ID, "states 3 thru 5?")
                val session2 = sessions[lastIndex]
                if(session2.updates.size < VIN2_updates.size) {
                    setAlarm(context, 15)
                    val session1 = sessions[lastIndex-1]
                    if(session1.updates.size < VIN1_updates.size) {
                        val info = gson.fromJson(VIN1_info, DCFCInfo::class.java) as DCFCInfo
                        val update = gson.fromJson(
                            VIN1_updates[session1.updates.size],
                            DCFCUpdate::class.java
                        ) as DCFCUpdate
                        info.time = update.time
                        info.power = update.power
                        info.energy = update.energy
                        DCFC.updateChargingSession(context, info)
                        LogFile.e(context,MainActivity.CHANNEL_ID, gson.toJson(info))
                        val delay = Random().nextInt(3000).toLong()

                        try {
                            Thread.sleep(1000+delay)
                        } catch (_: InterruptedException) {
                        }
                    }
                    val info = gson.fromJson(VIN2_info, DCFCInfo::class.java) as DCFCInfo
                    val update = gson.fromJson(
                        VIN2_updates[session2.updates.size],
                        DCFCUpdate::class.java
                    ) as DCFCUpdate
                    info.time = update.time
                    info.power = update.power
                    info.energy = update.energy
                    DCFC.updateChargingSession(context, info)
                    LogFile.e(context,MainActivity.CHANNEL_ID, gson.toJson(info))
                } else {
                    LogFile.e(context,MainActivity.CHANNEL_ID, "all done?")

                }
            }

//                val info = DCFCInfo()
//            info.VIN = VIN
//            if (VIN == VINA) {
//                info.plugInTime = "2023-08-10T16:55:15Z"
//                info.initialDte = 186.0
//                info.network = "Electrify America"
//                info.chargeLocationName = info.network + " - Trinity Parkway"
//                val baseTime = getEpochMillis(info.plugInTime!!)
//                info.time = setEpochMillis(baseTime +  30*1000)
//                info.power = 15000.0
//                info.energy = 10000.0
//                info.currentDte = 100.0
//                info.batteryFillLevel = 25.0
//                DCFC.updateChargingSession(context, info)
//            } else {
//                info.plugInTime = "2023-08-12T16:55:30Z"
//                info.initialDte = 186.0
//                info.network = "EVGol"
//                info.chargeLocationName = info.network + " - Elk Grove"
//                val baseTime = getEpochMillis(info.plugInTime!!)
//                info.time = setEpochMillis(baseTime +  30*1000)
//                info.power = 12500.0
//                info.energy = 1000.0
//                info.currentDte = 200.0
//                info.batteryFillLevel = 45.0
//                DCFC.updateChargingSession(context, info)
//            }
//
//            if(sessions.size > 0 ) {
//                var i = 0
//                while(i < sessions.lastIndex && sessions[i].chargeLocationName != info.chargeLocationName) { ++i }
//                val session = sessions[i]
//                if ( session.plugInTime != info.plugInTime) {
//                    DCFC.updateChargingSession(context, info)
//                    setAlarm(context, if (VIN == VINA) VINB else VINA)
//                } else {
//                    val index = session.updates.size
//                    if (index < 20) {
//                        val baseUpdate = session.updates[session.updates.lastIndex]
//                        val baseTime = getEpochMillis(baseUpdate.time!!)
//                        info.time = setEpochMillis(baseTime + 30*1000)
//                        info.power = if (index < 5) 15000.0 else 10000.0
//                        info.energy = baseUpdate.energy?.plus(1000.0)
//                        info.currentDte = baseUpdate.dte?.plus(10.0)
//                        info.batteryFillLevel = baseUpdate.batteryFillLevel?.plus(2.0)
//                        DCFC.updateChargingSession(context, info)
//                        setAlarm(context, if (VIN == VINA) VINB else VINA)
//                    }
//                }
//            }
        }

        fun cancelAlarm(context: Context, VIN: String) {
            val intent = Intent(context, TestReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, REQUESTCODE,
                intent, PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        private
        fun getEpochMillis(time: String): Long {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            cal.time = sdf.parse(time) as Date
            return cal.toInstant().toEpochMilli()
        }

        fun setEpochMillis(millis: Long): String {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            cal.timeInMillis = millis
            return sdf.format(cal.time)
        }


        fun setAlarm(context: Context, delay : Long) {
            // Get the hour right now to see if this alarm should happen today or tomorrow
            val nextTime = Instant.now().plusSeconds(delay).toEpochMilli()

            val intent = Intent(context, TestReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 123,
                intent, PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule the alarm
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent)
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                nextTime,
                DateUtils.SECOND_IN_MILLIS,
                pendingIntent
            )
        }
    }
}