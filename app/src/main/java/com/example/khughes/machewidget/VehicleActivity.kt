package com.example.khughes.machewidget

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.khughes.machewidget.Vehicle.Companion.modelMap
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

private lateinit var mVehicleViewModel: VehicleViewModel
private lateinit var info: InfoRepository

class VehicleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            info = getInfo(applicationContext)

            setContent {
                MacheWidgetTheme {
                    ManageVehicle()
                }
            }
        }
    }

    private suspend fun getInfo(context: Context): InfoRepository =
        coroutineScope {
            withContext(Dispatchers.IO) { InfoRepository(context) }
        }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun AddNewVehicle(
//    vehicle: VehicleIds,
//    popupWidth: Float,
//    popupHeight: Float,
//    showPopup: Boolean,
//    focusRequester: FocusRequester,
//    onClickOutside: () -> Unit,
//) {
//    val context = LocalContext.current
//    var vin by remember { mutableStateOf("") }
//    var help by remember { mutableStateOf("") }
//    var recognizeVIN by remember { mutableStateOf(context.getString(R.string.activity_vehicle_enter_new_vehicle_information)) }
//    val firstColor = MaterialTheme.colorScheme.secondary
//    var recognizeVINColor by remember { mutableStateOf(firstColor) }
//
//    // popup
//    Popup(
//        alignment = Alignment.Center,
//        properties = PopupProperties(
//            focusable = true,
//            dismissOnBackPress = true,
//        ),
//        // to dismiss on click outside
//        onDismissRequest = { onClickOutside() },
//    ) {
//        val outlinedColors = OutlinedTextFieldDefaults.colors(
//            cursorColor = MaterialTheme.colorScheme.secondary,
//            focusedLabelColor = MaterialTheme.colorScheme.secondary,
//            unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
//            focusedBorderColor = MaterialTheme.colorScheme.secondary,
//            unfocusedBorderColor = Color.Gray,
//            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
////            focusedTextColor = MaterialTheme.colorScheme.secondary,
//        )
//
//        Box(
//            modifier = Modifier
//                .wrapContentSize()
//                .clip(RoundedCornerShape(8.dp))
//                .background(MaterialTheme.colorScheme.primaryContainer)
//        )
//        {
//            Column(
//                modifier = Modifier.padding(all = 10.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                Text(
//                    text = context.getString(R.string.activity_vehicle_enter_new_vehicle_information),
//                    color = MaterialTheme.colorScheme.secondary,
//                    fontSize = 14.sp
//                )
//                OutlinedTextField(
//                    value = vin,
//                    supportingText = {
//                        Text(
//                            text = help,
//                            //              color = MaterialTheme.colorScheme.secondary
//                        )
//                    },
//                    colors = outlinedColors,
//                    onValueChange =
//                    {
//                        // Check that the string isn't too long, and that if only contains alphanumeric characters
//                        if (it.length <= 12 && it.uppercase().replace("[A-Z0-9]".toRegex(), "")
//                                .isEmpty()
//                        ) {
//                            vin = it.uppercase()
//                            var message = ""
//                            if (Vehicle.isVINRecognized(vin)) {
//                                val model = Vehicle.getVehicle(vin).name
//                                if (model != "") {
//                                    message = "VIN appears to be for " +
//                                            (if ("AEIOU".contains(
//                                                    model.subSequence(
//                                                        0,
//                                                        1
//                                                    )
//                                                )
//                                            ) "an " else "a ") + model
//                                }
//                                recognizeVINColor = Color.Red
//                            } else {
//                                message = context.resources.getString(R.string.vehicles_vin)
//                                recognizeVINColor = firstColor
//                            }
//                            recognizeVIN = message
//                            val len = vin.length
//                            help =
//                                if (vin.length == 12) "" else context.getString(R.string.correct_VIN_length) + " $len/12"
//                        }
//                    },
//                    label = {
//                        Text(
//                            text = recognizeVIN,
//                            color = recognizeVINColor
//                        )
//                    },
//                    modifier = Modifier
//                        .focusRequester(focusRequester)
//
//                )
//                Button(
//                    onClick = {
//                        vehicle.vin = vin
//                        onClickOutside()
//                    }
//                )
//                {
//                    Text("Save")
//                }
//                Button(
//                    onClick = {
//                        onClickOutside()
//                    }
//                )
//                {
//                    Text("Cancel")
//                }
//            }
//            LaunchedEffect(Unit) {
//                focusRequester.requestFocus()
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageVehicle() {
    mVehicleViewModel = viewModel<VehicleViewModel>()
    val vehicles = mVehicleViewModel.allVehicles.observeAsState().value
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.action_vehicle)) })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
//                    focusRequester.requestFocus()
                    CoroutineScope(Dispatchers.Main).launch {
                        val msg = NetworkCalls.getAccessToken(context)
                        val bundle = msg.data
                        val tokenId = bundle.getString("tokenId")
                        if (tokenId!! != "") {
                            NetworkCalls.getVehicleList(context, tokenId)
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
                content = {
                    Icon(
                        imageVector = Icons.TwoTone.Add,
                        contentDescription = "Add FAB",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            )
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

            Text(
                text = stringResource(id = R.string.vehicle_instructions),
                fontSize = 14.sp,
                lineHeight = 16.sp
            )

            vehicles?.let {
                LazyColumn(
                    contentPadding = PaddingValues(2.dp)
                ) {
                    items(vehicles) { vehicle ->
                        VehicleDisplay(vehicle)
                    }
                }
            }

        }
    }
}

@Composable
fun VehicleModel(
    currentModel: String,
    onSelect: (Vehicle.Companion.Model) -> Unit
) {
    var isMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero )
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    var model by remember {
        mutableStateOf(currentModel )
    }

    Box (
        modifier = Modifier
            .clickable(onClick = {
                isMenuVisible = true
            })
    ) {
        Text(
            text = model,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
    DropdownMenu(
        expanded = isMenuVisible,
        onDismissRequest = { isMenuVisible = false},
        offset = pressOffset.copy(y = pressOffset.y - itemHeight)
    ) {

        val menuMap: MutableMap<String, Vehicle.Companion.Model> = mutableMapOf()
        modelMap.keys.forEach {
            menuMap[modelMap[it]!!.modelName] = it
        }
        menuMap.forEach {
            DropdownMenuItem(text = {
                Text(
                    text = it.key,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)

                )
            }, onClick = {
                onSelect(it.value)
                model = it.key
                isMenuVisible = false
            })
        }
    }
}

@Composable
fun VehicleDisplay(vehicle: VehicleIds) {
//    var popupVisible by remember { mutableStateOf(false) }
//    val focusRequester = remember { FocusRequester() }
//
//    if (popupVisible) {
//        AddNewVehicle(vehicle = vehicle,
//            500.0F, 500.0F, showPopup = false, onClickOutside = {
//                popupVisible = false
//            }, focusRequester = focusRequester
//        )
//    }

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    )
    {
        val checkBoxValue = remember { mutableStateOf(vehicle.enabled) }
        val context = LocalContext.current
        val photo = VehicleImages.getRandomImage(context = context, vehicle.vehicleId)

        Checkbox(checked = checkBoxValue.value,
            onCheckedChange = {
                vehicle.enabled = it
                checkBoxValue.value = it
                mVehicleViewModel.setEnable(vehicle.vehicleId!!, vehicle.enabled)
            }
        )
        photo?.let {
            Image(
                bitmap = photo.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .size(54.dp)
                    .padding(start = 4.dp)
            )
        }
        Column {
            Text(
                text = vehicle.nickname!!,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            VehicleModel(
                currentModel = modelMap [vehicle.modelId]!!.modelName,
                onSelect = {
                    vehicle.modelId = it
                    mVehicleViewModel.setModel(vehicle.vehicleId!!, vehicle.modelId)
                    CarStatusWidget.updateWidget(context)
                }
            )
//            Text(text = vehicle.vin!!,
//                fontSize = 10.sp,
//                modifier = Modifier
//                    .padding(horizontal = 8.dp)
//                    .clickable {
//                        popupVisible = true
//                    }
//            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun LightPreview() {
    MacheWidgetTheme {
        ManageVehicle()
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DarkPreview() {
    MacheWidgetTheme {
        ManageVehicle()
    }
}
