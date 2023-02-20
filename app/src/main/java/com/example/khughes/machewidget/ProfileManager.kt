package com.example.khughes.machewidget

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.util.ArrayList

object ProfileManager : AppCompatActivity() {
    @JvmStatic
    fun changeProfile(context: Context, widget_VIN: String?): String? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val VIN = context.getSharedPreferences(Constants.WIDGET_FILE, MODE_PRIVATE)
            .getString(widget_VIN, null)
        val info = arrayOf<InfoRepository?>(null)
        val handler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val userId =
                    sharedPref.getString(context.resources.getString(R.string.userId_key), "")
                val vehicles: MutableList<VehicleInfo> = ArrayList()

                // Get all vehicles owned by the user
                var index = 0
                for (tmp in info[0]!!.vehicles) {
                    if (tmp.userId == userId) {
                        if (tmp.vin == VIN) {
                            index = vehicles.size
                        }
                        vehicles.add(tmp)
                    }
                }

                // If there's more than one VIN, look through the list for the next enabled one
                if (vehicles.size > 1) {
                    do {
                        index = (index + 1) % vehicles.size
                    } while (!vehicles[index].isEnabled)
                    val newVIN = vehicles[index].vin
                    // If the VIN is new, apply changes.
                    if (VIN != newVIN) {
                        context.getSharedPreferences(Constants.WIDGET_FILE, MODE_PRIVATE).edit()
                            .putString(widget_VIN, newVIN).commit()
                        Toast.makeText(context, vehicles[index].nickname, Toast.LENGTH_SHORT).show()
                        CarStatusWidget.updateWidget(context)
                    }
                }
            }
        }
        Thread {
            info[0] = InfoRepository(context)
            handler.sendEmptyMessage(0)
        }.start()
        return VIN
    }
}