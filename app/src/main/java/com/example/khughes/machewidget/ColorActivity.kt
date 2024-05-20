package com.example.khughes.machewidget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var info: InfoRepository
private var vehicles: List<VehicleInfo> = mutableListOf()

class ColorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            info = getInfo(applicationContext)
            vehicles = info.vehicles

            setContent {
                MacheWidgetTheme {
                    ChooseColor()
                }
            }

        }
    }

    // When the activity ends, make sure widgets are updated
    override fun onStop() {
        super.onStop()
        CarStatusWidget.updateWidget(this)
    }

}

private suspend fun getInfo(context: Context): InfoRepository =
    coroutineScope {
        withContext(Dispatchers.IO) { InfoRepository(context) }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseColor() {
    val context = LocalContext.current
    var vehicleIndex by remember { mutableIntStateOf(0) }
    var vehicleInfo = vehicles[vehicleIndex]
    var vehicleImages = Vehicle.getVehicle(vehicleInfo.vin).horizontalDrawables
    var vehicleColor by remember { mutableIntStateOf(vehicleInfo.colorValue and VehicleColor.ARGB_MASK) }
    var wireframe by remember { mutableIntStateOf(vehicleInfo.colorValue and VehicleColor.WIREFRAME_MASK) }
    var hexColor by remember {
        mutableStateOf(
            Integer.toHexString(vehicleInfo.colorValue or 0xff000000.toInt()).uppercase()
                .substring(2)
        )
    }
    var initialColor by remember { mutableIntStateOf(vehicleInfo.colorValue and VehicleColor.ARGB_MASK) }

    // This seems like a kludge; it forces HexColorPicker and BrightnessSlider to reposition the wheet
    var recomposeColorpicker by remember { mutableStateOf(false) }

    val controller = rememberColorPickerController()
    val bitmap = Bitmap.createBitmap(225, 100, Bitmap.Config.ARGB_8888)

    var isVINListVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.action_color)) })
        },
        modifier = Modifier.fillMaxSize()
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // IF there is more than one vehicle, show the VIN and set up a onclick callback to change
            if (vehicles.size > 1) {


                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {

                    Text(
                        text = vehicleInfo.vin!!,
                        color = Color.White,
                        modifier = Modifier
                            .padding(all = 4.dp)
                            .clickable {
                                isVINListVisible = true
                            }
                    )

                    DropdownMenu(
                        expanded = isVINListVisible,
                        onDismissRequest = { isVINListVisible = false }) {
                        vehicles.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item.vin!!) },
                                onClick = {
                                    vehicleInfo = item
                                    vehicleColor = vehicleInfo.colorValue and VehicleColor.ARGB_MASK
                                    wireframe = vehicleInfo.colorValue and VehicleColor.WIREFRAME_MASK
                                    hexColor =
                                        Integer
                                            .toHexString(vehicleInfo.colorValue or 0xff000000.toInt())
                                            .uppercase()
                                            .substring(2)
                                    initialColor = vehicleInfo.colorValue and VehicleColor.ARGB_MASK
                                    vehicleImages = Vehicle.getVehicle(vehicleInfo.vin).horizontalDrawables
                                    recomposeColorpicker = !recomposeColorpicker
                                    isVINListVisible = false
                                }
                            )
                        }
                    }

                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.arrow_drop_down),
                        contentDescription = "",
                    )
                }
            }

//            AndroidView(
//                modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
//                factory = { context ->
//                    // Creates view
//                    ColorPickerView(context).apply {
//                        // Sets up listeners for View -> Compose communication
//                        setColorListener(ColorListener { color: Int, _: Boolean ->
//                            val text = buildString {
//                                append(context.resources.getString(R.string.activity_color_rgb_value))
//                                append(
//                                    Integer.toHexString(color).uppercase()
//                                        .substring(2)
//                                )
//                            }
//                            vehicleColor = color
////                            drawVehicle(color and VehicleColor.ARGB_MASK or wireframeMode)
//                        })
//                    }
//                },
//                update = { view ->
//                    // View's been inflated or state read in this block has been updated
//                    // Add logic here if necessary
//
//                    // As selectedItem is read here, AndroidView will recompose
//                    // whenever the state changes
//                    // Example of Compose -> View communication
////                    view.attachBrightnessSlider(binding.brightnessSlide)
//                }
//            )

            Image(
                bitmap = bitmap.asImageBitmap(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(10.dp),
                contentDescription = "",
            )

            VehicleColor.drawColoredVehicle(
                context,
                bitmap,
                vehicleColor or wireframe,
                ArrayList(),
                true,
                vehicleImages
            )

            Text(
                text = context.resources.getString(R.string.activity_color_rgb_value) + hexColor,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(10.dp)
            )

            key(recomposeColorpicker) {
                HsvColorPicker(
                    modifier = Modifier
                        .size(if (vehicles.size == 1) 300.dp else 240.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(10.dp),
                    initialColor = Color(initialColor),
                    controller = controller,
                    onColorChanged = {
                        if (it.fromUser) {
                            vehicleColor = (it.color.toArgb() and VehicleColor.ARGB_MASK)
                            hexColor = it.hexCode.substring(2)
                        }
                    },
                )

                BrightnessSlider(
                    modifier = Modifier
                        .width(if (vehicles.size == 1) 300.dp else 240.dp)
                        .padding(10.dp)
                        .height(30.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    initialColor = Color(vehicleInfo.colorValue and VehicleColor.ARGB_MASK),
                    controller = controller,
                )
            }


            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25F),
                        shape = RoundedCornerShape(10.dp)
                    )
            )
            {
                Text(
                    text = context.getString(R.string.activity_color_wireframe_color),
                    modifier = Modifier
                        .height(24.dp)
                        .align(Alignment.CenterHorizontally)
                )

                key(wireframe) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        // Set up everything for the radio buttons
                        val whiteString =
                            context.resources.getString(R.string.activity_color_white)
                        val blackString =
                            context.resources.getString(R.string.activity_color_black)
                        val autoString =
                            context.resources.getString(R.string.activity_color_auto)
                        val radioOptions = listOf(whiteString, blackString, autoString)
                        val wireframeModes = listOf(
                            VehicleColor.WIREFRAME_WHITE,
                            VehicleColor.WIREFRAME_BLACK,
                            VehicleColor.WIREFRAME_AUTO
                        )
                        var selectedOption by remember {
                            mutableStateOf(
                                radioOptions[when (wireframe) {
                                    VehicleColor.WIREFRAME_WHITE -> 0
                                    VehicleColor.WIREFRAME_BLACK -> 1
                                    else -> 2
                                }]
                            )
                        }

                        radioOptions.forEach { buttonName ->
                            RadioButton(
                                selected = (buttonName == selectedOption),
                                onClick = {
                                    selectedOption = buttonName
                                    val index = radioOptions.indexOf(selectedOption)
                                    wireframe = wireframeModes[index]
                                },
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(start = 8.dp)
                            )
                            Text(
                                text = buttonName,
                                modifier = Modifier
                                    .padding(start = 2.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                val buttonColors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )

                // "Save" button
                Button(
                    onClick = {
                        // Store the current color information
                        vehicleInfo.colorValue = vehicleColor or wireframe
                        info.setVehicle(vehicleInfo)
                    },
                    colors = buttonColors,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = context.getString(R.string.activity_color_save),
                    )
                }

                // "Reset" button
                Button(
                    onClick = {
                        // Reload the initial values
                        vehicleColor = vehicleInfo.colorValue and VehicleColor.ARGB_MASK
                        wireframe = vehicleInfo.colorValue and VehicleColor.WIREFRAME_MASK
                        hexColor = Integer.toHexString(vehicleInfo.colorValue or 0xff000000.toInt())
                            .uppercase().substring(2)
                        recomposeColorpicker = !recomposeColorpicker
                    },
                    colors = buttonColors,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Text(text = context.getString(R.string.activity_color_reset))
                }

                // "Auto" button: only applies if there is an image of the vehicle to read
                if (VehicleImages.getImage(context, vehicleInfo.vin!!, 4) != null) {
                    Button(
                        onClick = {
                            // Save current color, so we cab locate a color to read in the image
                            val oldColor = vehicleInfo.colorValue
                            vehicleInfo.colorValue = android.graphics.Color.WHITE

                            // If this returns true, use the returned color information
                            if (VehicleColor.scanImageForColor(context, vehicleInfo)) {
                                vehicleColor = vehicleInfo.colorValue and VehicleColor.ARGB_MASK
                                wireframe = vehicleInfo.colorValue and VehicleColor.WIREFRAME_MASK
                                hexColor =
                                    Integer.toHexString(vehicleInfo.colorValue or 0xff000000.toInt())
                                        .uppercase().substring(2)
                                initialColor = vehicleInfo.colorValue and VehicleColor.ARGB_MASK
                                recomposeColorpicker = !recomposeColorpicker
                            }

                            // reset to the original color
                            vehicleInfo.colorValue = oldColor
                        },
                        colors = buttonColors,
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(text = context.getString(R.string.activity_color_auto_button))
                    }
                }
            }
        }
    }
}

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

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun LightPreview() {
    MacheWidgetTheme {
        ChooseColor()
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DarkPreview() {
    MacheWidgetTheme {
        ChooseColor()
    }
}
