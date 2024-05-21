package com.example.khughes.machewidget

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var info: InfoRepository

class ReminderActivity : ComponentActivity() {

    private fun isPHEVorBEV(vehicle: VehicleInfo): Boolean {
        val status = vehicle.carStatus
        val propulsionType = status.propulsion
        return !status.isPropulsionICEOrHybrid(propulsionType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val activity = this

        lifecycleScope.launch {
            info = getInfo(applicationContext)

            // Find the BEV and PHEV vehicles
            val vinList: MutableList<String> = mutableListOf()
            for (vehicle in info.vehicles) {
                if (isPHEVorBEV(vehicle)) {
                    vinList.add(vehicle.vin!!)
                }
            }

            // If no vehicles found, display a dialog instead
            if (vinList.size == 0) {
                AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogCustom))
                    .setTitle(getString(R.string.misc_error_message))
                    .setMessage(getString(R.string.activity_reminder_noEVs_description))
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _: DialogInterface?, _: Int -> finish() }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            } else {
                setContent {
                    MacheWidgetTheme {
                        ChargingReminder(vinList)
                    }
                }
            }
        }
    }

    private suspend fun getInfo(context: Context): InfoRepository =
        coroutineScope {
            withContext(Dispatchers.IO) { InfoRepository(context) }
        }

    companion object {
        const val HOUR_MASK = 0x1f
        const val NOTIFICATION_BIT = 0x20
    }
}


//private const val HOUR_MASK = 0x1f
//private const val NOTIFICATION_BIT = 0x20
//
//private fun isPHEVorBEV(vehicle: VehicleInfo): Boolean {
//    val status = vehicle.carStatus
//    val propulsionType = status.propulsion
//    return !status.isPropulsionICEOrHybrid(propulsionType)
//}
//
//private fun isNotificationEnabled(chargeHour: Int) =
//    (chargeHour and NOTIFICATION_BIT) != 0
//
//private fun getHour(chargeHour: Int) = (chargeHour and HOUR_MASK)
//
//private fun getLevel(level: Int) : Int {
//    return if((level < 15) or (level > 80) or (level % 5 != 0)) 15 else level
//}
//
//private fun levelToPosition(level: Int) = (level - 15) / 5
//
//private fun positionToLevel(position: Int) = (position * 5) + 15

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun CustomSpinner (
//    parentOptions: List<String>,
//    onValueChangedEvent: (String) -> Unit,
//    ) {
//    var expandedState by remember { mutableStateOf(false) }
//    var selectedOption by remember { mutableStateOf(parentOptions[0]) }
//
//    // === 1
//    Column (modifier = Modifier.fillMaxWidth())
//    {
//        ExposedDropdownMenuBox(
//            expanded = expandedState,
//            onExpandedChange = { expandedState = !expandedState },
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//
//            // === 2
//            TextField(
//                value = selectedOption,
//                onValueChange = {},
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState) },
//                readOnly = true,
//                modifier = Modifier.menuAnchor()
//            )
//
//            // === 3
//            ExposedDropdownMenu(expanded = expandedState,
//                onDismissRequest = { expandedState = false }) {
//
//                // === 4
//                parentOptions.forEachIndexed { index, text ->
//                    DropdownMenuItem(
//                        text = { Text(text = text) },
//                        onClick = {
//                            selectedOption = parentOptions[index]
//                            onValueChangedEvent(selectedOption)
//                            expandedState = false
//                        }
//                    )
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChargingReminder(vehicleVINs: MutableList<String>) {

    fun isNotificationEnabled(chargeHour: Int) =
        (chargeHour and ReminderActivity.NOTIFICATION_BIT) != 0

    fun getHour(chargeHour: Int) = (chargeHour and ReminderActivity.HOUR_MASK)

    fun getLevel(level: Int): Int {
        return if ((level < 15) or (level > 80) or (level % 5 != 0)) 15 else level
    }

    fun levelToPosition(level: Int) = (level - 15) / 5

    fun positionToLevel(position: Int) = (position * 5) + 15

    val context = LocalContext.current

    var currentVehicle: VehicleInfo = info.getVehicleByVIN(vehicleVINs[0])
    var notificationEnabled by remember { mutableStateOf(isNotificationEnabled(currentVehicle.chargeHour)) }
    var hour = getHour(currentVehicle.chargeHour)
    var level = getLevel(currentVehicle.chargeThresholdLevel)

    var vin by remember { mutableStateOf(vehicleVINs[0]) }

    val hours = mutableListOf("12 am")
    for (i in 1..11) {
        hours.add("$i am")
    }
    hours.add("12 pm")
    for (i in 1..11) {
        hours.add("$i pm")
    }

    val levels = mutableListOf<String>()
    for (i in 15..80 step 5) {
        levels.add("$i %")
    }

    fun updateVehicle() {
        val chargeHour = hour or (if (notificationEnabled) ReminderActivity.NOTIFICATION_BIT else 0)
        currentVehicle.let {
            it.chargeThresholdLevel = level
            it.chargeHour = chargeHour
            info.setVehicle(currentVehicle)
            if (isNotificationEnabled(chargeHour)) {
                ReminderReceiver.setAlarm(
                    context = context,
                    it.vin!!,
                    getHour(chargeHour)
                )
            } else {
                ReminderReceiver.cancelAlarm(context, it.vin!!)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.action_reminder)) })
        },
        modifier = Modifier.fillMaxSize()
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

            // textview with description

            Text(
                text = stringResource(id = R.string.reminder_instructions),
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp, bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Vehicle VIN
            CustomSpinner(initialLabel = vin,
                items = vehicleVINs)
            { item ->
                updateVehicle()
                vin = item
                currentVehicle = info.getVehicleByVIN(vin)
                notificationEnabled =
                    isNotificationEnabled(currentVehicle.chargeHour)
                hour = getHour(currentVehicle.chargeHour)
                level = getLevel(currentVehicle.chargeThresholdLevel)
            }

            // Notification enabled switch
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            {
                Text(
                    stringResource(id = R.string.activity_reminder_notification_enabled),
                    modifier = Modifier.padding(all = 4.dp)
                )
                Switch(
                    checked = notificationEnabled,
                    onCheckedChange = {
                        notificationEnabled = !notificationEnabled
                        updateVehicle()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                    ),
                    modifier = Modifier.padding(all = 4.dp),
                )
            }

            // Notification time
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                Text(
                    stringResource(id = R.string.activity_reminder_notification_time),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )

                CustomSpinner(
                    initialLabel = hours[hour],
                    items = hours
                )
                { item ->
                    hour = hours.indexOf(item)
                    updateVehicle()
                }
            }

            // Threshold charge level
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            )
            {
                Text(
                    stringResource(id = R.string.activity_reminder_threshold_battery_level),
                    modifier = Modifier
                        .padding(all = 4.dp)
                )

                val index = levelToPosition(level)

                CustomSpinner(
                    initialLabel = levels[index],
                    items = levels)
                { item ->
                    level = positionToLevel(levels.indexOf(item))
                    updateVehicle()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun LightPreview() {
    MacheWidgetTheme {
        ChargingReminder(mutableListOf(""))
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DarkPreview() {
    MacheWidgetTheme {
        ChargingReminder(mutableListOf(""))
    }
}
