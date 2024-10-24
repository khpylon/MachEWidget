package com.example.khughes.machewidget

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import java.util.ArrayList

object ProfileManager : AppCompatActivity() {
    @JvmStatic
    fun changeProfile(context: Context, widget_VIN: String?): String? {
        val vehicleId = context.getSharedPreferences(Constants.WIDGET_FILE, MODE_PRIVATE)
            .getString(widget_VIN, null)
        val info = arrayOf<InfoRepository?>(null)
        val handler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val vehicles: MutableList<VehicleInfo> = ArrayList()

                // Get all vehicles owned by the user
                var index = 0
                for (tmp in info[0]!!.vehicles) {
                    if (tmp.carStatus.vehicle.vehicleId == vehicleId) {
                        index = vehicles.size
                    }
                    vehicles.add(tmp)
                }

                // If there's more than one vehicle, look through the list for the next enabled one
                if (vehicles.size > 1) {
                    do {
                        index = (index + 1) % vehicles.size
                    } while (!vehicles[index].isEnabled)
                    val newVehicleId = vehicles[index].carStatus.vehicle.vehicleId
                    // If the ID is new, apply changes.
                    if (vehicleId != newVehicleId) {
                        context.getSharedPreferences(Constants.WIDGET_FILE, MODE_PRIVATE).edit()
                            .putString(widget_VIN, newVehicleId).commit()
                        val nickname = vehicles[index].carStatus.vehicle.nickName
                        if (nickname != "") {
                            Toast.makeText(
                                context,
                                vehicles[index].carStatus.vehicle.nickName,
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        CarStatusWidget.updateWidget(context)
                    }
                }
            }
        }
        Thread {
            info[0] = InfoRepository(context)
            handler.sendEmptyMessage(0)
        }.start()
        return vehicleId
    }
}