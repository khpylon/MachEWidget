package com.example.khughes.machewidget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.databinding.ActivityColorBinding
import com.skydoves.colorpickerview.listeners.ColorListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private lateinit var binding: ActivityColorBinding
private var mVehicleInfo: VehicleInfo? = null
private lateinit var info: InfoRepository
private lateinit var arrayList: MutableList<String>

private var wireframeMode = VehicleColor.WIREFRAME_WHITE

class ColorActivity : AppCompatActivity() {

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
        TooltipCompat.setTooltipText(
            binding.autoImage,
            getString(R.string.activity_color_autoImage_hint)
        )

        binding.colorPickerView.setColorListener(ColorListener { color: Int, _: Boolean ->
            binding.colorValue.text = buildString {
                append(getString(R.string.activity_color_rgb_value))
                append(
                    Integer.toHexString(color).uppercase()
                        .substring(2)
                )
            }
            drawVehicle(color and VehicleColor.ARGB_MASK or wireframeMode)
        })
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
                info.getVehicleByVIN(VIN).let {
                    setCheckedButton(it.colorValue)
                    binding.colorPickerView.setInitialColor(it.colorValue)
                    setAutoButton(it.vin!!)
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
                setAutoButton(mVehicleInfo!!.vin!!)
            } else {
                arrayList.clear()
                for (vehicle in vehicles) {
                    arrayList.add(vehicle.vin!!)
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
