package com.example.khughes.machewidget

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.khughes.machewidget.databinding.ActivityReminderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var binding: ActivityReminderBinding

private const val HOUR_MASK = 0x1f
private const val NOTIFICATION_BIT = 0x20

class ReminderActivity : AppCompatActivity() {

    var info: InfoRepository? = null
    var currentVehicle: VehicleInfo? = null
    private var isEnabled: Boolean = false
    var hour: Int = 0
    var level: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val activity = this

        lifecycleScope.launch {
            info = getInfo(applicationContext)
            // Find the BEV and PHEV vehicles
            val VINs: MutableList<String> = mutableListOf()
            info?.let {
                for (vehicle in it.vehicles) {
                    if (isPHEVorBEV(vehicle)) {
                        VINs.add(vehicle.vin)
                    }
                }
            }

            // If no vehicles found, display a dialog instead
            if (VINs.size == 0) {
                AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogCustom))
                    .setTitle("Error")
                    .setMessage("No PHEVs or BEVs were found.")
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _: DialogInterface?, _: Int -> finish() }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            } else {
                binding.VINSpinner.adapter =
                    ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, VINs)
                binding.VINSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            // Set text color of VIN number
                            (parent?.getChildAt(0) as TextView?)?.setTextColor(getColor(R.color.quantum_white_secondary_text))

                            // If there is a vehicle, see if we need to update the database
                            currentVehicle?.let { updateVehicle() }
                            currentVehicle = null

                            // Find the new info and populate the UI
                            val VIN = parent?.getItemAtPosition(position) as String
                            info?.let {
                                val vehicle = it.getVehicleByVIN(VIN)
                                val isEnabled = isNotificationEnabled(vehicle.chargeHour)

                                binding.batteryNotification.isChecked = isEnabled
                                binding.hourSetting.isEnabled = isEnabled
                                binding.batteryLevel.isEnabled = isEnabled
                                binding.hourSetting.setSelection(getHour(vehicle.chargeHour))
                                binding.batteryLevel.setSelection(levelToPosition(vehicle.chargeThresholdLevel))
                                currentVehicle = vehicle
                            }
                        }
                    }
            }
        }

        val hours = mutableListOf("12 am")
        for (i in 1..11 ) {
            hours.add("$i am")
        }
        hours.add("12 pm")
        for (i in 1..11 ) {
            hours.add("$i pm")
        }
        binding.hourSetting.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, hours.toTypedArray())
        binding.hourSetting.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent?.getChildAt(0) as TextView?)?.setTextColor(getColor(R.color.quantum_white_secondary_text))
                // update alarm time
                hour = position
            }
        }

        val levels = mutableListOf<String>()
        for (i in 15..80 step 5) {
            levels.add("$i %")
        }
        binding.batteryLevel.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, levels.toTypedArray())
        binding.batteryLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent?.getChildAt(0) as TextView?)?.setTextColor(getColor(R.color.quantum_white_secondary_text))
                // update the charging threshold
                level = positionToLevel(position)
            }
        }

        binding.batteryNotification.setOnCheckedChangeListener { _, isChecked ->
            binding.hourSetting.isEnabled = isChecked
            binding.batteryLevel.isEnabled = isChecked
            isEnabled = isChecked
        }
    }

    override fun onPause() {
        super.onPause()
        currentVehicle?.let {
            if (isNotificationEnabled(it.chargeHour) != isEnabled
                || getHour(it.chargeHour) != hour
                || it.chargeThresholdLevel != level
            ) {
                updateVehicle()
            }
        }
    }

    private fun updateVehicle() {
        val chargeHour = hour or (if (isEnabled) NOTIFICATION_BIT else 0)
        currentVehicle?.let {
            it.chargeThresholdLevel = level
            it.chargeHour = chargeHour
            info?.setVehicle(currentVehicle)
            if (isNotificationEnabled(chargeHour)) {
                ReminderReceiver.setAlarm(
                    applicationContext,
                    it.vin,
                    getHour(chargeHour)
                )
            } else {
                ReminderReceiver.cancelAlarm(applicationContext, it.vin)
            }
        }
    }

    private fun isPHEVorBEV(vehicle: VehicleInfo): Boolean {
        val status = vehicle.carStatus
        status?.let {
            val propulsionType = it.propulsion
            return !it.isPropulsionICEOrHybrid(propulsionType)
        }
        return true
    }

    private inline fun isNotificationEnabled(chargeHour: Int) =
        (chargeHour and NOTIFICATION_BIT) != 0

    private inline fun getHour(chargeHour: Int) = (chargeHour and HOUR_MASK)

    private inline fun levelToPosition(level: Int) = (level - 15) / 5

    private inline fun positionToLevel(position: Int) = (position * 5) + 15

    private suspend fun getInfo(context: Context): InfoRepository =
        coroutineScope {
            withContext(Dispatchers.IO) { InfoRepository(context) }
        }

}