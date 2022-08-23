package com.example.khughes.machewidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.lifecycle.lifecycleScope
import com.example.khughes.machewidget.databinding.ActivityColorBinding
import com.skydoves.colorpickerview.listeners.ColorListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var binding: ActivityColorBinding
private var mVehicleInfo: VehicleInfo? = null
private lateinit var info: InfoRepository
private lateinit var arrayList: MutableList<String>

private var wireframeMode = VehicleColor.WIREFRAME_WHITE

class ColorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.radiogroup.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            val radioButton = radioGroup.findViewById<View>(i)
            wireframeMode = when (radioGroup.indexOfChild(radioButton)) {
                0 -> VehicleColor.WIREFRAME_WHITE
                1 -> VehicleColor.WIREFRAME_BLACK
                else -> VehicleColor.WIREFRAME_AUTO
            }
            binding.colorPickerView.setInitialColor(binding.colorPickerView.color)
        }

        binding.ok.setOnClickListener {
            mVehicleInfo?.let {
                    it.colorValue =
                        binding.colorPickerView.color and VehicleColor.ARGB_MASK or wireframeMode
                    info.setVehicle(it)
                    CarStatusWidget.updateWidget(applicationContext)
                }
        }

        binding.reset.setOnClickListener {
            mVehicleInfo?.let {
                setCheckedButton(it.colorValue)
                binding.colorPickerView.setInitialColor(it.colorValue)
            }
        }

        binding.autoImage.setOnClickListener {
            mVehicleInfo?.let {
                val oldColor = it.colorValue
                it.colorValue = Color.WHITE
                if (VehicleColor.scanImageForColor(this, it)) {
                    binding.colorPickerView.setInitialColor(it.colorValue)
                }
                it.colorValue = oldColor
            }
        }
        TooltipCompat.setTooltipText(binding.autoImage, "Use stored image as color source.")

        binding.colorPickerView.setColorListener(ColorListener { color: Int, _: Boolean ->
            binding.colorValue.text = "RGB value: #" + Integer.toHexString(color).uppercase()
                .substring(2)
            drawVehicle(color and VehicleColor.ARGB_MASK or wireframeMode)
        } )
        binding.colorPickerView.attachBrightnessSlider(binding.brightnessSlide)

        arrayList = mutableListOf()
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = arrayAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val VIN = parent.getItemAtPosition(position).toString()
                info.getVehicleByVIN(VIN)?.let {
                    setCheckedButton(it.colorValue)
                    binding.colorPickerView.setInitialColor(it.colorValue)
                    setAutoButton(it.vin)
                    mVehicleInfo = it
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        lifecycleScope.launch {
            info = getInfo(applicationContext)
            val vehicles = info.vehicles
            if (vehicles.size == 1) {
                binding.spinner.visibility = View.GONE
                mVehicleInfo = info.vehicles[0]
                setCheckedButton(mVehicleInfo!!.colorValue)
                binding.colorPickerView.setInitialColor(mVehicleInfo!!.colorValue)
                setAutoButton(mVehicleInfo!!.vin)
            } else {
                arrayList.clear()
                for (vehicle in vehicles) {
                    arrayList.add(vehicle.vin)
                }
                binding.spinner.adapter = arrayAdapter
            }
        }
    }

    private fun setAutoButton(VIN: String) {
        val bmp = VehicleImages.getImage(applicationContext, VIN, 4)
        binding.autoImage.visibility = if (bmp != null) View.VISIBLE else View.GONE
    }

    private fun setCheckedButton(color: Int) {
        val index: Int
        when (color and VehicleColor.WIREFRAME_MASK) {
            VehicleColor.WIREFRAME_WHITE -> 0
            VehicleColor.WIREFRAME_BLACK -> 1
            else -> 2
        }.also { index = it }
        (binding.radiogroup.getChildAt(index) as RadioButton).isChecked = true
    }

    private fun drawVehicle(color: Int) {
        mVehicleInfo?.let {
            val vehicleImages = Vehicle.getVehicle(it.vin).horizontalDrawables

            // Create base bitmap the size of the image
            val bmp = Bitmap.createBitmap(225, 100, Bitmap.Config.ARGB_8888)
            VehicleColor.drawColoredVehicle(
                applicationContext,
                bmp,
                color,
                ArrayList(),
                true,
                vehicleImages
            )
            binding.carImage.setImageBitmap(bmp)
        }
    }

    private suspend fun getInfo(context: Context): InfoRepository =
        coroutineScope {
            withContext(Dispatchers.IO) { InfoRepository(context) }
        }

}
