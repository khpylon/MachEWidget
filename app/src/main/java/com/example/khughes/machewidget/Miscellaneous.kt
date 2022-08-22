package com.example.khughes.machewidget

import android.content.Context
import android.graphics.*
import android.icu.text.MessageFormat
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.db.UserInfoDatabase
import com.example.khughes.machewidget.db.VehicleInfoDatabase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

class VehicleColor {
    companion object {
        const val ARGB_MASK: Int = 0xffffff  // only use RGB components
        const val WIREFRAME_MASK = 0x03 shl 24
        const val WIREFRAME_WHITE = 0
        const val WIREFRAME_BLACK = 1 shl 24
        const val WIREFRAME_AUTO = 2 shl 24

        // Attempt to automatically choose the color of the vehicle for the widget
        @JvmStatic
        fun scanImageForColor(context: Context, vehicleInfo: VehicleInfo): Boolean {
            // If vehicle color has been set, do nothing
            if ((vehicleInfo.colorValue and ARGB_MASK) != (Color.WHITE and ARGB_MASK)) {
                return false
            }

            // If the vehicle image doesn't exist, do nothing
            val VIN = vehicleInfo.vin
            val bmp = Utils.getVehicleImage(context, VIN, 4)
            if (bmp == null || vehicleInfo.colorValue != Color.WHITE) {
                return false
            }

            // Based on the vehicle type, choose a small image patch to sample
            val(startx,starty) = Vehicle.getVehicle(VIN).offsetPositions

            // get the RBG value of each pixel in the patch
            val RGB = IntArray(3)
            val patchSize = 10
            for (y in 0..patchSize) {
                for (x in 0..patchSize) {
                    val color = bmp.getPixel(startx + x, starty + y)
                    RGB[0] += color.shr(16) and 0xff
                    RGB[1] += color.shr(8) and 0xff
                    RGB[2] += color and 0xff
                }
            }

            // average the components
            RGB[0] /= patchSize * patchSize
            RGB[1] /= patchSize * patchSize
            RGB[2] /= patchSize * patchSize

            // Set the color and exit
            vehicleInfo.colorValue = (((RGB[0] shl 16) or (RGB[1] shl 8) or RGB[2])
                    and ARGB_MASK) or WIREFRAME_AUTO
            return true
        }

        @JvmStatic
        fun isFirstEdition(context: Context, VIN: String): Boolean {
            // If the vehicle isn't a Mach-E, nevermind
            if (!(Vehicle.getVehicle(VIN) is MachE)) {
                return false
            }

            // If the vehicle image doesn't exist, do nothing
            val bmp = Utils.getVehicleImage(context, VIN, 4) ?: return false

            // Check if a pixel on the side view mirror is black or colored
            val color = bmp.getPixel(220, 152)
            val RGB = arrayOf(
                (color shr 16) and 0xff,
                (color shr 8) and 0xff,
                color and 0xff
            )

            return RGB[0] > 0x08 || RGB[1] > 0x08 || RGB[2] > 0x08
        }

        @JvmStatic
        fun drawColoredVehicle(
            context: Context, bmp: Bitmap, color: Int, whatsOpen: MutableList<Int>,
            useColor: Boolean, vehicleImages: Map<String, Int>
        ) {
            // Create base canvas the size of the image
            val canvas = Canvas(bmp)
            val paint = Paint()
            var bmp2: Bitmap
            var canvas2: Canvas

            val drawableId = vehicleImages[Vehicle.BODY_PRIMARY]
            if (drawableId != null && useColor) {
                val drawable = AppCompatResources.getDrawable(context, drawableId)
                drawable?.let {
                    bmp2 = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                    )
                    canvas2 = Canvas(bmp2)

                    // Fill with the primary color mask
                    paint.color = color and ARGB_MASK
                    // Set the alpha based on whether something is open
                    paint.alpha = if (whatsOpen.isEmpty()) 0xff else 0xbf
                    paint.style = Paint.Style.FILL
                    canvas.drawPaint(paint)

                    // Draw the primary body in color
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas2)
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
                    canvas.drawBitmap(bmp2, 0f, 0f, paint)
                }

                // If secondary colors exist, add them
                val secondary = vehicleImages[Vehicle.BODY_SECONDARY]
                secondary?.let {
                    val icon = AppCompatResources.getDrawable(context, it)
                    icon?.let {
                        icon.setBounds(0, 0, canvas.width, canvas.height)
                        icon.draw(canvas)
                    }
                }
            }

            // Draw anything that's open
            for (id in whatsOpen) {
                val icon = context.getDrawable(id)
                icon?.let {
                    icon.setBounds(0, 0, canvas.width, canvas.height)
                    icon.draw(canvas)
                }
            }

            // Create a second bitmap the same size as the primary
            bmp2 = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
            canvas2 = Canvas(bmp2)

            // If not using colors, draw wireframe in white
            if (!useColor) {
                paint.color = Color.WHITE
            }
            // Figure out whether wireframe should be drawn light or dark
            else {
                val hsl = FloatArray(3)
                ColorUtils.colorToHSL(color and ARGB_MASK, hsl)
                val wireframeMode = color and WIREFRAME_MASK
                paint.color =
                    if (wireframeMode == WIREFRAME_WHITE) Color.WHITE
                    else if (wireframeMode == WIREFRAME_BLACK) Color.BLACK
                    else if (hsl[2] > 0.5) Color.BLACK
                    else Color.WHITE
            }
            paint.alpha = 0xff
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)

            // Fill with a contrasting color
            paint.style = Paint.Style.FILL
            canvas2.drawPaint(paint)

            // Draw the wireframe body
            val drawable = AppCompatResources.getDrawable(
                context,
                vehicleImages[Vehicle.WIREFRAME]!!
            )
            drawable?.let {
                val bmp3 = Bitmap.createBitmap(
                    drawable.intrinsicWidth, drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas3 = Canvas(bmp3)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas3)

                // Set the wireframe's color
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
                canvas2.drawBitmap(bmp3, 0f, 0f, paint)
            }

            // Draw wireframe over the colored body
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            canvas.drawBitmap(bmp2, 0f, 0f, paint)
        }
    }
}

class PrefManagement {

    private lateinit var jsonOutput: String

    fun savePrefs(context: Context) {
        GlobalScope.launch {
            jsonOutput = getInfo(context)
            val inStream: InputStream = ByteArrayInputStream(
                jsonOutput.toByteArray(
                    StandardCharsets.UTF_8
                )
            )
            val outputFilename = Utils.writeExternalFile(
                context,
                inStream,
                "fsw_settings-",
                Constants.APPLICATION_JSON
            )
            Toast.makeText(
                context,
                MessageFormat.format(
                    "Settings file \"{0}.json\" copied to Download folder.",
                    outputFilename
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val JSON_SETTINGS_VERSION = 3
    }

    private suspend fun getInfo(context: Context): String =
        coroutineScope {
            withContext(Dispatchers.IO) {
                val jsonData = LinkedHashMap<String, Any>()
                jsonData.put("version", JSON_SETTINGS_VERSION)

                // Save the default preferences
                var prefs = PreferenceManager.getDefaultSharedPreferences(context).all
                val prefData = LinkedHashMap<String, Array<String>>()
                for(key in prefs.keys) {
                    val value = prefs[key]
                    val dataType = when (value) {
                        is String -> "String"
                        is Long -> "Long"
                        is Int -> "Integer"
                        else -> "Boolean"
                    }
                    prefData.put(key, arrayOf( dataType, value.toString()))
                }
                jsonData.put("prefs", prefData.clone())
                prefData.clear()

                // Save the shared preferences
                prefs = context.getSharedPreferences(StoredData.TAG, Context.MODE_PRIVATE).all
                for(key in prefs.keys) {
                    val value = prefs[key]
                    val dataType = when (value) {
                        is String -> "String"
                        is Long -> "Long"
                        is Int -> "Integer"
                        else -> "Boolean"
                    }
                    prefData.put(key, arrayOf( dataType, value.toString()))
                }
                jsonData.put(StoredData.TAG, prefData.clone())
                prefData.clear()

                // Save database entries
                jsonData.put("users", UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo())
                jsonData.put("vehicles", VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo())
                GsonBuilder().create().toJson(jsonData)
           }
        }

}
