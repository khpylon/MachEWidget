package com.example.khughes.machewidget

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import android.graphics.Paint
import android.icu.text.MessageFormat
import android.os.Build
import android.os.Bundle
import android.os.FileObserver
import android.view.ContextThemeWrapper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

@RequiresApi(Build.VERSION_CODES.Q)
class DCFCFileObserver(path: String, cb: () -> Unit) :
    FileObserver(
        File(path), CLOSE_WRITE//, MODIFY + CREATE + DELETE
    ) {
    private var callback = cb

    override fun onEvent(event: Int, path: String?) {
        if (path == DCFC.CHARGINGFILENAME) {
            if (event == CLOSE_WRITE || event == MODIFY) {
                callback()
            }
        }
    }
}

class ChargingActivity : ComponentActivity() {

    private lateinit var directoryFileObserver: DCFCFileObserver

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            this::directoryFileObserver.isInitialized
        ) {
            directoryFileObserver.stopWatching()
        }
    }

    private var defaultLanguage: Locale? = null

    private fun getContextForLanguage(context: Context): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return context

        if (defaultLanguage == null) {
            defaultLanguage = Resources.getSystem().configuration.locales[0]
        }
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val languageTag =
            sharedPref.getString(context.resources.getString(R.string.language_key), "")
        val locale = if (languageTag!!.isEmpty()) {
            defaultLanguage as Locale
        } else {
            Locale.forLanguageTag(languageTag)
        }
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(getContextForLanguage(newBase))
    }

    private lateinit var sessions: MutableList<DCFCSession>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = applicationContext

        sessions = DCFC.getChargingSessions(context = context).toMutableStateList()

        if (sessions.size == 0) {
            val error: String
            val message: String

            // If there is a log file, it must be empty, so explain what to do
            if (DCFC.logFileExists(context = context)) {
                error = getString(R.string.activity_charging_logfile_error)
                message = getString(R.string.activity_charging_logfile_message)
            }

            // Otherwise some settings are disabled.  Explain which need to be enabled.
            else {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)

                val chargeKey = context.resources.getString(R.string.check_charging_key)
                val chargeTitle = context.resources.getString(R.string.check_charging)
                val checkDCFCKey = context.resources.getString(R.string.check_dcfastcharging_key)
                val dcfcTitle = context.resources.getString(R.string.check_dcfastcharging_title)
                val logDCFCKey = context.resources.getString(R.string.dcfclog_key)
                val logDCFCTitle = context.resources.getString(R.string.dcfclog)

                val checkCharging = prefs.getBoolean(chargeKey, false)
                val checkDCFC = prefs.getBoolean(checkDCFCKey, false)
                val logDCFC = prefs.getBoolean(logDCFCKey, false)

                if (!checkCharging) {
                    error = getString(R.string.activity_charging_three_missing_settings_error)
                    val pattern =
                        getString(R.string.activity_charging_three_missing_settings_pattern)
                    message = MessageFormat.format(pattern, chargeTitle, dcfcTitle, logDCFCTitle)
                } else if (!checkDCFC) {
                    error = getString(R.string.activity_charging_two_missing_settings_error)
                    val pattern =
                        getString(R.string.activity_charging_two_missing_settings_pattern)
                    message = MessageFormat.format(pattern, dcfcTitle, logDCFCTitle)
                } else if (!logDCFC) {
                    error = getString(R.string.activity_charging_one_missing_setting_error)
                    val pattern = getString(R.string.activity_charging_one_missing_setting_pattern)
                    message = MessageFormat.format(pattern, logDCFCTitle)
                } else {
                    error = getString(R.string.activity_charging_unexpected_issue_error)
                    message = getString(R.string.activity_charging_unexpected_issue_description)
                    finish()
                }
            }

            // Create the basic dialog
            val dialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle(error)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)

            // If the log file is there, it's just an information dialog
            if (DCFC.logFileExists(context = context)) {
                dialog.setPositiveButton(
                    android.R.string.ok
                ) { _: DialogInterface?, _: Int -> finish() }
            }

            // If settings are disabled, ask if the user wants to enable them.
            else {
                dialog.setPositiveButton(
                    android.R.string.ok
                ) { _: DialogInterface?, _: Int ->
                    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    val edit = prefs.edit()

                    val chargeKey = context.resources.getString(R.string.check_charging_key)
                    val checkDCFCKey =
                        context.resources.getString(R.string.check_dcfastcharging_key)
                    val logDCFCKey = context.resources.getString(R.string.dcfclog_key)

                    edit.putBoolean(chargeKey, true).apply()
                    edit.putBoolean(checkDCFCKey, true).apply()
                    edit.putBoolean(logDCFCKey, true).apply()
                    finish()
                }
                    .setNegativeButton(
                        android.R.string.cancel
                    ) { _: DialogInterface?, _: Int -> finish() }
            }
            dialog.show()
        }

        // Otherwise we're ready do go: display some stats
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                directoryFileObserver = DCFCFileObserver(
                    File(applicationContext.dataDir.toString()).toString(),
                    this::loadData
                )
                directoryFileObserver.startWatching()
            }

            setContent {
                MacheWidgetTheme(dynamicColor = false) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen(sessions)
                    }
                }
            }
        }
    }

    private fun loadData() {
        val currentSessions = DCFC.getChargingSessions(context = applicationContext)
        if (sessions.size != currentSessions.size) {
            sessions.clear()
            sessions.addAll(currentSessions)
        } else {
            for (i in 0..sessions.lastIndex) {
                if (sessions[i].updates.size != currentSessions[i].updates.size) {
                    sessions[i] = currentSessions[i]
                }
            }
        }
    }

}

private
fun getEpochMillis(timeStr: String): Long {
    val formatter = DateTimeFormatter.ofPattern(Constants.CHARGETIMEFORMAT, Locale.getDefault())
    val time = LocalDateTime.parse(timeStr, formatter).atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.systemDefault())
    return time.toInstant().toEpochMilli()
}

@Composable
private fun Graph(info: MutableList<DCFCUpdate>, textColor: Color, modifier: Modifier) {
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = textColor.toArgb()
            textSize = density.run {
                12.sp.toPx()
            }
        }
    }

    val energy = stringResource(R.string.activity_charging_energy_button)
    val power = stringResource(R.string.activity_charging_power_button)
    val soc = stringResource(R.string.activity_charging_soc_button)
    val seconds = stringResource(R.string.activity_charging_seconds_label)

    var whatInfo by rememberSaveable { mutableStateOf(energy) }
    val buttonText = when (whatInfo) {
        energy -> {
            // the first two entries are button labels.  The last is the units to display on the graph
            listOf(power, soc, "kW")
        }

        power -> {
            listOf(soc, energy, "kWh")
        }

        else -> {
            listOf(energy, power, "%")
        }
    }

    val values = mutableListOf<Double>()
    var maxValue = 0.0
    val units = buttonText[2]

    for (item in info) {
        when (whatInfo) {
            energy -> {
                values.add(item.energy!! / 1000f)
            }

            power -> {
                values.add(item.power!! / 1000f)
            }

            else -> {
                values.add(item.batteryFillLevel!!)
            }
        }
        maxValue = if (maxValue < values[values.lastIndex]) values[values.lastIndex] else maxValue
    }

    val log = log10(maxValue)
    val whole = log.toInt().toDouble()
    val fraction = log - whole
    var scale = (10.0).pow(whole)
    if (fraction < log10(2.5)) {
        scale *= .25
    } else if (fraction < log10(5.0)) {
        scale *= .5
    }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
            ) {
                Button(
                    onClick = { whatInfo = buttonText[0] },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Text(text = "<< " + buttonText[0])
                }
                Button(
                    onClick = { /* Do something */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.0F),
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = whatInfo, fontSize = 20.sp)
                }
                Button(
                    onClick = { whatInfo = buttonText[1] },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Text(text = buttonText[1] + " >>")
                }

            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val rect = android.graphics.Rect()

                val startTime = info[0].time as String
                val endTime = info[info.lastIndex].time as String
                val chargeBeginTime = getEpochMillis(startTime)
                val chargeEndTime = getEpochMillis(endTime)
                val elapsedSeconds = (chargeEndTime - chargeBeginTime) / 1000

                val maxYScale = maxValue * 1.05f

                val paddingSpace = Dp(20f)
                val top = 60f
                val bottom = size.height - 90
                val left = 150f
                val right = size.width - paddingSpace.toPx()

                val xAxisSpace = (right - left) / 10

                /* placing x axis points */
                for (i in 0..10) {
                    val index = (i.toDouble() / 10f * elapsedSeconds.toDouble()).toInt()

                    val text = "$index"
                    textPaint.getTextBounds(text, 0, text.length, rect)

                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        left + xAxisSpace * i - rect.width() / 2,
                        size.height - 55, textPaint
                    )
                    drawLine(
                        start = Offset(x = left + xAxisSpace * i, y = bottom),
                        end = Offset(x = left + xAxisSpace * i, y = top),
                        color = Color.LightGray,
                        strokeWidth = 4f
                    )
                }

                textPaint.getTextBounds(seconds, 0, seconds.length, rect)
                drawContext.canvas.nativeCanvas.drawText(
                    seconds,
                    (left + (right - left) / 2) - rect.width() / 2,
                    size.height - 15, textPaint
                )

                drawContext.canvas.nativeCanvas.save()
                drawContext.canvas.nativeCanvas.rotate(270f, 500f, 500f)
                drawContext.canvas.nativeCanvas.drawText(
                    "$whatInfo ($units)",
                    400f, 30f,
                    textPaint
                )
                drawContext.canvas.nativeCanvas.restore()

                /* placing y axis points */
                var i = 0.0
                while (i < maxYScale) {
                    val text = "$i"
                    textPaint.getTextBounds(text, 0, text.length, rect)

                    val y = (bottom + (top - bottom) * i / maxYScale).toFloat()
                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        left - 20 - rect.width(),
                        y + 10,
                        textPaint
                    )
                    drawLine(
                        start = Offset(x = left, y = y),
                        end = Offset(x = right, y = y),
                        color = Color.LightGray,
                        strokeWidth = 4f
                    )
                    i += scale
                }

                val offsets = mutableListOf<Offset>()
                for (i in 0 until info.size) {
                    val xTime = (getEpochMillis(info[i].time!!) - chargeBeginTime) / 1000
                    val yPower = values[i]
                    offsets += Offset(
                        x = (left + (right - left) * xTime / elapsedSeconds),
                        y = (bottom + (top - bottom) * yPower / maxYScale).toFloat()
                    )
                }

                for (i in 0..offsets.size - 2) {
                    drawLine(
                        start = offsets[i],
                        end = offsets[i + 1],
                        color = Color.Blue,
                        strokeWidth = 4f
                    )
                }

                for (i in 0 until info.size) {
                    val xTime = (getEpochMillis(info[i].time!!) - chargeBeginTime) / 1000
                    val yPower = values[i]
                    drawCircle(
                        color = Color.Red,
                        radius = 10.0f,
                        center = Offset(
                            x = left + (right - left) * xTime / elapsedSeconds,
                            y = (bottom + (top - bottom) * yPower / maxYScale).toFloat()
                        )
                    )
                }
            }
        }
    }


}

@Composable
fun MainScreen(sessions: MutableList<DCFCSession>) {
    // index identifies which session to display
    var index by rememberSaveable { mutableStateOf(sessions.lastIndex) }
    // count is the total number of sessions
    var count by rememberSaveable { mutableStateOf(sessions.lastIndex) }
    // If the number of sessions increases, always display the most recent (last)
    if (count != sessions.size) {
        index = sessions.lastIndex
        count = sessions.size
    }

    val session = sessions[index]
    val pluginTime = getEpochMillis(session.plugInTime!!)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 12.dp),
    ) {
        Graph(
            session.updates, MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )

        if (session.VIN != null) {
            Text(
                text = stringResource(R.string.vehicle_label) + session.VIN,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
            )
        }

        val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
        val zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(pluginTime), ZoneId.systemDefault())
        val format = if (locale?.country == "US") {
            Constants.LOCALTIMEFORMATUS
        } else {
            Constants.LOCALTIMEFORMAT
        }
        val time = zdt.format(DateTimeFormatter.ofPattern(format,locale!!))

        Text(
            text = stringResource(R.string.activity_charging_session_start_label) + " $time",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

//        Text(
//            text = "Charging network: " + session.network,
//            color = MaterialTheme.colorScheme.secondary,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        var location = session.chargeLocationName as String
//        val prefix = session.network + " - "
//        if (location.startsWith(prefix, ignoreCase = false)) {
//            location = location.substring(prefix.length)
//        }
//
//        Text(
//            text = "Location: " + location.substring(0, min(35, location.length)),
//            color = MaterialTheme.colorScheme.secondary,
//            modifier = Modifier.fillMaxWidth()
//        )

        val maxPowerStr = String.format(locale,
            "%.2f",
            (session.updates.maxBy { it.power!! }.power as Double) / 1000
        )
        Text(
            text = stringResource(R.string.max_power_label) + "$maxPowerStr kW",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val avgPowerStr = String.format(locale,
            "%.2f",
            session.updates.sumOf { it.power!! } / 1000 / session.updates.size
        )
        Text(
            text = stringResource(R.string.average_power_label) + "$avgPowerStr kW",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val energyStr = String.format(locale,
            "%.2f",
            (session.updates.maxBy { it.energy!! }.energy as Double) / 1000
        )
        Text(
            text = stringResource(R.string.energy_added_label) + "$energyStr kWh",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )


        // Get conversion factors and descriptions for measurement units
        val distanceConversion: Double
        val distanceUnits: String
        if(locale == Locale.US) {
            distanceConversion = Constants.KMTOMILES
            distanceUnits = "miles"
        } else {
            distanceConversion = 1.0
            distanceUnits = "km"
        }

        val initialDTE = String.format(locale, "%.1f", session.initialDte!! * distanceConversion)
        val finalDTE = String.format(locale,
            "%.1f", session.updates[session.updates.lastIndex].dte!!
                    * distanceConversion
        )

        Text(
            text = stringResource(R.string.range_label) + "$initialDTE $distanceUnits -> $finalDTE $distanceUnits",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val initialSOC = session.updates[0].batteryFillLevel!!
        val finalSOC = session.updates[session.updates.lastIndex].batteryFillLevel!!

        Text(
            text = stringResource(R.string.activity_charging_soc_label) + " $initialSOC% -> $finalSOC%",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
            ) {
                if (index > 0) {
                    Button(
                        onClick = { --index },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.5F
                            )

                        ),
                        modifier = Modifier.align(Alignment.CenterStart),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "<<",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(10.dp)
                ) {
                    val of = LocalContext.current.resources.getString(R.string.activity_charging_of_label)
                    Text(
                        text = "${index + 1} $of $count",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                if (index < sessions.lastIndex) {
                    Button(
                        onClick = { ++index },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.5F
                            )
                        ),
                        modifier = Modifier.align(Alignment.CenterEnd),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = ">>", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LightPreview() {
    MacheWidgetTheme(dynamicColor = false) {
        val updates: MutableList<DCFCUpdate> = mutableListOf()
        val update1 = DCFCUpdate()

        update1.time = "2023-04-13T00:30:15Z"
        update1.energy = 7202.9414
        update1.power = 56376.013
        update1.batteryFillLevel = 48.5
        updates.add(update1)

        val update2 = DCFCUpdate()
        update2.time = "2023-04-13T00:34:15Z"
        update2.energy = 5000.0
        update2.power = 60000.0
        update2.batteryFillLevel = 65.0
        updates.add(update2)

        val update3 = DCFCUpdate()
        update3.time = "2023-04-13T00:40:15Z"
        update3.energy = 2134.6881
        update3.power = 66647.21
        update3.batteryFillLevel = 91.0
        updates.add(update3)

        val info = DCFCInfo()
        info.VIN = "3FMTK3R75MMA00001"
        info.plugInTime = "2023-04-12T16:55:21Z"
        info.initialDte = 186.0
        info.chargeLocationName = "ChargePoint - City of Pacific Grove Lot"
        info.network = "ChargePoint"

        val session = DCFCSession(info, updates)
        MainScreen(mutableListOf(session))
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DarkPreview() {
    MacheWidgetTheme(dynamicColor = false) {
        val updates: MutableList<DCFCUpdate> = mutableListOf()
        val update1 = DCFCUpdate()

        update1.time = "2023-04-13T00:30:15Z"
        update1.energy = 7202.9414
        update1.power = 56376.013
        update1.batteryFillLevel = 48.5
        updates.add(update1)

        val update2 = DCFCUpdate()
        update2.time = "2023-04-13T00:34:15Z"
        update2.energy = 5000.0
        update2.power = 60000.0
        update2.batteryFillLevel = 65.0
        updates.add(update2)

        val update3 = DCFCUpdate()
        update3.time = "2023-04-13T00:40:15Z"
        update3.energy = 2134.6881
        update3.power = 66647.21
        update3.batteryFillLevel = 91.0
        updates.add(update3)

        val info = DCFCInfo()
        info.VIN = "3FMTK3R75MMA00001"
        info.plugInTime = "2023-04-12T16:55:21Z"
        info.initialDte = 186.0
        info.chargeLocationName = "ChargePoint - City of Pacific Grove Lot"
        info.network = "ChargePoint"

        val session = DCFCSession(info, updates)
        MainScreen(mutableListOf(session))
    }
}