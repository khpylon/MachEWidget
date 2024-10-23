package com.example.khughes.machewidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val VEHICLE_ID = "VEHICLEID"

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val vehicleId = intent.getStringExtra(VEHICLE_ID)
        vehicleId?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val info = getInfo(context)
                val vehicle = info.getVehicleById(vehicleId)
                val threshold = vehicle.chargeThresholdLevel
                val actualLevel = vehicle.carStatus.vehicle.vehicleDetails.batteryChargeLevel?.value?.toInt() ?: threshold
                val pluggedIn = (vehicle.carStatus.vehicle.vehicleStatus.plugStatus?.value ?: 0) == 1

                LogFile.d(MainActivity.CHANNEL_ID,
                    "ReminderReceiver: threshold = "+threshold +
                    ", actualLevel = " + actualLevel +
                    ", pluggedIn = " + pluggedIn )
                if ((!pluggedIn || vehicle.carStatus.vehicle.vehicleStatus.chargingStatus?.value in arrayOf(
                        Constants.CHARGING_STATUS_PAUSED,
                        Constants.CHARGING_STATUS_NOT_READY
                    )) && actualLevel <= threshold
                ) {
                    LogFile.d(MainActivity.CHANNEL_ID, "ReminderReceiver: firing notification")
                    Notifications.chargeReminder(context)
                }
                setAlarm(context, vehicleId, vehicle.chargeHour and 0x1f)
            }
        }
    }

//    private suspend fun getInfo(context: Context): InfoRepository =
//        coroutineScope {
//            withContext(Dispatchers.IO) { InfoRepository(context) }
//        }

    companion object {

        fun cancelAlarm(context: Context, vehicleId: String) {
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, vehicleId.hashCode(),
                intent, PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        fun setAlarm(context: Context, vehicleId: String, hour: Int) {
            // Get the hour right now to see if this alarm should happen today or tomorrow
            val currentHour = LocalDateTime.now().atZone(ZoneId.systemDefault()).hour
            val nextTime =
                LocalDate.now().atTime(hour, 0).plusDays(if (hour > currentHour) 0 else 1)
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val time = LocalDate.now().atTime(hour, 0).plusDays(if (hour > currentHour) 0 else 1)
            val timeText = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss", Locale.US))
            LogFile.i(
                context, MainActivity.CHANNEL_ID,
                "ReminderReceiver: next reminder alarm at $timeText"
            )

            // Create intent including the VIN
            val intent = Intent(context, ReminderReceiver::class.java).putExtra(VEHICLE_ID, vehicleId)
            val pendingIntent = PendingIntent.getBroadcast(
                context, vehicleId.hashCode(),
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

        fun checkAlarms(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val info = getInfo(context)
                for (vehicle in info.vehicles) {
                    if((vehicle.chargeHour and ReminderActivity.NOTIFICATION_BIT) != 0) {
                        cancelAlarm(context, vehicleId = vehicle.carStatus.vehicle.vehicleId)
                        setAlarm(context, vehicleId = vehicle.carStatus.vehicle.vehicleId, vehicle.chargeHour and ReminderActivity.HOUR_MASK)
                    }
                }
            }
        }
        private suspend fun getInfo(context: Context): InfoRepository =
            coroutineScope {
                withContext(Dispatchers.IO) { InfoRepository(context) }
            }

    }
}
