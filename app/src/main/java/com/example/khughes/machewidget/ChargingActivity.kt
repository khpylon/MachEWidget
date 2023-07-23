package com.example.khughes.machewidget

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import java.lang.Double.max
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.pow


class ChargingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MacheWidgetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val info: MutableList<DCFCUpdate> = mutableListOf()
                    val update1 = DCFCUpdate()

                    update1.time = "2023-04-13T00:30:15Z"
                    update1.energy = 7202.9414
                    update1.power = 56376.013
                    update1.batteryFillLevel = 48.5
                    info.add(update1)

                    val update2 = DCFCUpdate()
                    update2.time = "2023-04-13T00:34:15Z"
                    update2.energy = 5000.0
                    update2.power = 60000.0
                    update2.batteryFillLevel = 65.0
                    info.add(update2)

                    val update3 = DCFCUpdate()
                    update3.time = "2023-04-13T00:40:15Z"
                    update3.energy = 2134.6881
                    update3.power = 66647.21
                    update3.batteryFillLevel = 91.0
                    info.add(update3)

                    Greeting(info)
                }
            }
        }
    }
}

private
fun getEpochMillis(time: String): Long {
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    cal.time = sdf.parse(time)
    return cal.toInstant().toEpochMilli()
}

@Composable
private fun Graph(info: List<DCFCUpdate>, textColor: Color, modifier: Modifier) {
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
    var maxValue: Double = 0.0
    var units = buttonText[2]

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

    val log = Math.log10(maxValue)
    val whole = log.toInt().toDouble()
    val fraction = log - whole
    var scale = 10.0;
    scale = scale.pow(whole)
    if (fraction< Math.log10(2.5)) {
        scale *= .25
    } else if (fraction < Math.log(5.0)) {
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5F)),
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5F)),
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
                val canvasWidth = size.width
                val canvasHeight = size.height
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
                    whatInfo + " ($units)",
                    400f, 30f,
                    textPaint
                )
                drawContext.canvas.nativeCanvas.restore()

                /** placing y axis points */
                var i  = 0.0
                while (i  < maxYScale) {
                    val index = i

                    val text = "$index"
                    textPaint.getTextBounds(text, 0, text.length, rect)

                    val y = (bottom + (top - bottom) * index / maxYScale).toFloat()
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
                for (i in 0..info.size-1) {
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

                for (i in 0..info.size-1) {
                    val xTime = (getEpochMillis(info[i].time!!) - chargeBeginTime) / 1000
                    val yPower = values[i]
                    drawCircle(
                        color = Color.Red,
                        radius = 10.0f,
                        center = Offset(
                            x = (left + (right - left) * xTime / elapsedSeconds).toFloat(),
                            y = (bottom + (top - bottom) * yPower / maxYScale).toFloat()
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    info: MutableList<DCFCUpdate>, modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 12.dp),
    ) {
//        Box (modifier = Modifier.fillMaxWidth()
//                .height(IntrinsicSize.Max)) {
//            Button(
//
//                onClick = { /* Do something */ },
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.0F)),
//                modifier = Modifier.align(Alignment.CenterStart)
////            modifier = Modifier
////                .height(
////                    48.dp
////                )
////                .width(
////                    148.dp
////                ),
////            shape = RoundedCornerShape(10.dp)
//            ) {
//                Text(text = "<< Power",color = MaterialTheme.colorScheme.secondary )
//            }
//            Button(
//                onClick = { /* Do something */ },
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.0F)),
//                modifier = Modifier.align(Alignment.Center)
//            ) {
//                Text(text = "Energy",color = MaterialTheme.colorScheme.secondary
//                )
//            }
//            Button(
//                onClick = { /* Do something */ },
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.0F)),
//                modifier = Modifier.align(Alignment.CenterEnd)
//            ) {
//                Text(text = "SOC >> ",color = MaterialTheme.colorScheme.secondary )
//            }
//
//        }

        Graph(
            info, MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Vehicle: Howard",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Session date: 24 June",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Charging network: EVGo",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Location: 142 Main Street, Golden CO",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Session start time: 10:04 PDT",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Session duration: 42 min",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Max power: 162 kW",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Average power: 105 kW",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Energy added: 47.2 kWh",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Range added: 104 miles",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "SOC: 42% -> 79%",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LightPreview() {
    MacheWidgetTheme {
        val info: MutableList<DCFCUpdate> = mutableListOf()
        val update1 = DCFCUpdate()

        update1.time = "2023-04-13T00:30:15Z"
        update1.energy = 7202.9414
        update1.power = 56376.013
        update1.batteryFillLevel = 48.5
        info.add(update1)

        val update2 = DCFCUpdate()
        update2.time = "2023-04-13T00:34:15Z"
        update2.energy = 5000.0
        update2.power = 60000.0
        update2.batteryFillLevel = 65.05
        info.add(update2)

        val update3 = DCFCUpdate()
        update3.time = "2023-04-13T00:40:15Z"
        update3.energy = 2134.6881
        update3.power = 66647.21
        update3.batteryFillLevel = 91.0
        info.add(update3)

        Greeting(info)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DarkPreview() {
    MacheWidgetTheme {
        val info: MutableList<DCFCUpdate> = mutableListOf()
        val update1 = DCFCUpdate()

        update1.time = "2023-04-13T00:30:15Z"
        update1.energy = 7202.9414
        update1.power = 56376.013
        update1.batteryFillLevel = 48.5
        info.add(update1)

        val update2 = DCFCUpdate()
        update2.time = "2023-04-13T00:34:15Z"
        update2.energy = 5000.0
        update2.power = 60000.0
        update2.batteryFillLevel = 65.0
        info.add(update2)

        val update3 = DCFCUpdate()
        update3.time = "2023-04-13T00:40:15Z"
        update3.energy = 2134.6881
        update3.power = 66647.21
        update3.batteryFillLevel = 91.0
        info.add(update3)

        Greeting(info)
    }
}