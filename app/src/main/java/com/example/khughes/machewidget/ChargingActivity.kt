package com.example.khughes.machewidget

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.FileObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import com.google.gson.GsonBuilder
import java.io.File
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.log10
import kotlin.math.pow

@RequiresApi(Build.VERSION_CODES.Q)
class DCFCFileObserver(path: String, cb: (Boolean) -> Unit) :
    FileObserver(File(path), MODIFY + CREATE + DELETE) {
    private var callback = cb

    override fun onEvent(event: Int, path: String?) {
        if (path == DCFC.CHARGINGSESSIONFILENAME) {
            if (event == CREATE || event == MODIFY) {
                callback(true)
            } else if (event == DELETE) {
                callback(false)
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

    private lateinit var sessions: MutableList<DCFCSession>
    private var sessionActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            directoryFileObserver = DCFCFileObserver(
                File(applicationContext.dataDir.toString()).toString(),
                this::loadData
            )
            directoryFileObserver.startWatching()
        }

        val gson = GsonBuilder().create()
        sessions = listOf<DCFCSession>().toMutableStateList()
        for (line in File(applicationContext.dataDir, DCFC.CHARGINGFILENAME).readLines()) {
            sessions.add(gson.fromJson(line, DCFCSession::class.java))
        }

        val session = DCFC.pseudoConsolidateChargingSessions(context = applicationContext)
        session?.let {
            sessions[sessions.lastIndex] = it
            sessionActive = true
        }

        setContent {
            MacheWidgetTheme {
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

    private fun loadData(active: Boolean) {
        if (active) {
            val session = DCFC.pseudoConsolidateChargingSessions(context = applicationContext)
            session?.let {
                if (sessionActive) {
                    sessions[sessions.lastIndex] = it
                } else {
                    sessions.add(session)
                }
            }
        }
        sessionActive = active
    }
}

private
fun getEpochMillis(time: String): Long {
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    cal.time = sdf.parse(time) as Date
    return cal.toInstant().toEpochMilli()
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
    var whatInfo by rememberSaveable { mutableStateOf("Energy") }
    val buttonText = when (whatInfo) {
        "Energy" -> {
            listOf("Power", "SOC", "kW")
        }

        "Power" -> {
            listOf("SOC", "Energy", "kWh")
        }

        else -> {
            listOf("Energy", "Power", "%")
        }
    }

    val values = mutableListOf<Double>()
    var maxValue = 0.0
    val units = buttonText[2]

    for (item in info) {
        when (whatInfo) {
            "Energy" -> {
                values.add(item.energy!! / 1000f)
            }

            "Power" -> {
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5F)
                    ),
                    modifier = Modifier.align(Alignment.CenterStart),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "<< " + buttonText[0], color = MaterialTheme.colorScheme.secondary)
                }
                Button(
                    onClick = { /* Do something */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.0F)),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = whatInfo, color = MaterialTheme.colorScheme.secondary
                    )
                }
                Button(
                    onClick = { whatInfo = buttonText[1] },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5F)
                    ),
                    modifier = Modifier.align(Alignment.CenterEnd),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = buttonText[1] + " >>", color = MaterialTheme.colorScheme.secondary)
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

                /** placing x axis points */
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

                val text = "Seconds"
                textPaint.getTextBounds(text, 0, text.length, rect)
                drawContext.canvas.nativeCanvas.drawText(
                    text,
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

                /** placing y axis points */
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
    if (count < sessions.size) {
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
        Text(
            text = "Vehicle: " + session.VIN,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val sessionDay = Calendar.getInstance()
        val sdfDay = SimpleDateFormat(
            Constants.LOCALTIMEFORMATUS // "EEE, d MMM"
            , Locale.ENGLISH
        )
        sessionDay.timeInMillis = pluginTime

        Text(
            text = "Session start time: " + sdfDay.format(sessionDay.time),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Charging network: " + session.network,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        var location = session.chargeLocationName as String
        val prefix = session.network + " - "
        if (location.startsWith(prefix, ignoreCase = false)) {
            location = location.substring(prefix.length)
        }

        Text(
            text = "Location: " + location.substring(0, min(35, location.length)),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val maxPowerStr = String.format(
            "%.2f",
            (session.updates.maxBy { it.power!! }.power as Double) / 1000
        )
        Text(
            text = "Max power: $maxPowerStr kW",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val avgPowerStr = String.format(
            "%.2f",
            session.updates.sumOf { it.power!! } / 1000 / session.updates.size
        )
        Text(
            text = "Average power: $avgPowerStr kW",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val energyStr = String.format(
            "%.2f",
            (session.updates.maxBy { it.energy!! }.energy as Double) / 1000
        )
        Text(
            text = "Energy added: $energyStr kWh",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val distanceConversion = Constants.KMTOMILES
        val initialDTE = String.format("%.1f", session.initialDte!! * distanceConversion)
        val finalDTE = String.format(
            "%.1f", session.updates[session.updates.lastIndex].dte!!
                    * distanceConversion
        )

        val distanceUnits = "miles"
        Text(
            text = "Range: $initialDTE $distanceUnits -> $finalDTE $distanceUnits",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )

        val initialSOC = session.updates[0].batteryFillLevel!!
        val finalSOC = session.updates[session.updates.lastIndex].batteryFillLevel!!

        Text(
            text = "SOC: $initialSOC% -> $finalSOC%",
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
                    Text(
                        text = "${index + 1} of $count",
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
fun LightPreview() {
    MacheWidgetTheme {
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
fun DarkPreview() {
    MacheWidgetTheme {
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