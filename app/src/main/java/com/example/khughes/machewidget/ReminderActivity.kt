package com.example.khughes.machewidget

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.khughes.machewidget.databinding.ActivityReminderBinding
import kotlinx.coroutines.*


private lateinit var binding: ActivityReminderBinding

private val HOUR_MASK = 0x1f
private val NOTIFICATION_BIT = 0x20

class ReminderActivity : AppCompatActivity() {

    var info: InfoRepository? = null
    var currentVehicle: VehicleInfo? = null
    var settingChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            info = getInfo(applicationContext)
            // Find the BEV and PHEV vehicles
            var VINs: MutableList<String> = mutableListOf()
            for (vehicle in info!!.vehicles) {
                if (isPHEVorBEV(vehicle)) {
                    VINs.add(vehicle.vin)
                }
            }

            binding.VINSpinner.adapter =
                ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, VINs)
            binding.VINSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (parent?.getChildAt(0) as TextView).setTextColor(getColor(R.color.quantum_white_100))
                    currentVehicle?.let { updateVehicle( )}
                    val VIN = parent?.getItemAtPosition(position) as String
                    var vehicle  = info!!.getVehicleByVIN(VIN)
                    val isEnabled =  isNotificationEnabled(vehicle.chargeHour)

                    binding.batteryNotification.isChecked = isEnabled
                    binding.hourSetting.setSelection( getHour(vehicle.chargeHour ))
                    binding.hourSetting.isEnabled = isEnabled
                    binding.batteryLevel.setSelection((vehicle.chargeThresholdLevel-15)/5)
                    binding.batteryLevel.isEnabled = isEnabled
                    settingChanged= false
                    currentVehicle = vehicle
                }
            }
        }

        val hours = arrayOf(
            "12 am",
            "1 am",
            "2 am",
            "3 am",
            "4 am",
            "5 am",
            "6 am",
            "7 am",
            "8 am",
            "9 am",
            "10 am",
            "11 am",
            "12 pm",
            "1 pm",
            "2 pm",
            "3 pm",
            "4 pm",
            "5 pm",
            "6 pm",
            "7 pm",
            "8 pm",
            "9 pm",
            "10 pm",
            "11 pm"
        )
        binding.hourSetting.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, hours)
        binding.hourSetting.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent?.getChildAt(0) as TextView).setTextColor(getColor(R.color.quantum_white_secondary_text))

                // locate the correct vehicle
                val VIN = binding.VINSpinner.selectedItem.toString()
                var vehicle = info!!.getVehicleByVIN(VIN)

                // update alarm time
                val hour = position
                if( getHour(vehicle.chargeHour) != hour ) {
                    vehicle.chargeHour = hour or (vehicle.chargeHour and NOTIFICATION_BIT)
                    settingChanged = true
                }
            }
        }

        val levels = arrayOf(
            "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", "55%", "60%", "65%",
            "70%", "75%", "80%"
        )
        binding.batteryLevel.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, levels)
        binding.batteryLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent?.getChildAt(0) as TextView).setTextColor(getColor(R.color.quantum_white_secondary_text))

                // locate the correct vehicle
                val VIN = binding.VINSpinner.selectedItem.toString()
                var vehicle = info!!.getVehicleByVIN(VIN)
                // update the charginc threshold
                val level = position * 5 + 15;
                info!!.setVehicle(vehicle)
                if( vehicle.chargeThresholdLevel != level ) {
                    vehicle.chargeThresholdLevel = level
                    settingChanged = true
                }
            }
        }

        binding.batteryNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            // locate the correct vehicle
            val VIN = binding.VINSpinner.selectedItem.toString()
            var vehicle = info!!.getVehicleByVIN(VIN)

            if( isChecked != isNotificationEnabled(vehicle.chargeHour )) {
                vehicle.chargeHour = vehicle.chargeHour xor NOTIFICATION_BIT
                settingChanged = true
            }
            binding.hourSetting.isEnabled = isChecked
            binding.batteryLevel.isEnabled = isChecked
        }
    }

    override fun onPause() {
        super.onPause()
        if(settingChanged) {
           updateVehicle()
        }
    }

    private fun updateVehicle() {
        info!!.setVehicle(currentVehicle)
        val chargeHour = currentVehicle!!.chargeHour
        if( isNotificationEnabled(chargeHour) ) {
            ReminderReceiver.setAlarm(applicationContext, currentVehicle!!.vin, getHour(chargeHour));
        } else {
            ReminderReceiver.cancelAlarm(applicationContext, currentVehicle!!.vin);
        }
    }

    private fun isPHEVorBEV(vehicle: VehicleInfo): Boolean {
        val status = vehicle.carStatus
        val propulsionType = status.propulsion
        return status.isPropulsionICEOrHybrid(propulsionType) == false
    }

    private inline fun isNotificationEnabled(chargeHour : Int) = (chargeHour and NOTIFICATION_BIT) != 0

    private inline fun getHour(chargeHour : Int) = (chargeHour and HOUR_MASK)

    private suspend fun getInfo(context: Context): InfoRepository =
        coroutineScope {
            async(Dispatchers.IO) { InfoRepository(context) }.await()
        }

}