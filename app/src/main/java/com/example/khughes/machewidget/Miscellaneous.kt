package com.example.khughes.machewidget

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.icu.text.MessageFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceManager
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.example.khughes.machewidget.db.TokenIdDatabase
import com.example.khughes.machewidget.db.VehicleInfoDatabase
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.*
import java.lang.Integer.min
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

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
            val vehicleId = vehicleInfo.carStatus.vehicle.vehicleId
            val bmp = VehicleImages.getImage(context, vehicleId)
            if (bmp == null || vehicleInfo.colorValue != Color.WHITE) {
                return false
            }

            // Based on the vehicle type, choose a small image patch to sample
            val (startx, starty) = Vehicle.getVehicle(vehicleInfo.modelId).offsetPositions

            // get the RBG value of each pixel in the patch
            val RGB = IntArray(3)
            val patchSize = 10
            for (y in 0..patchSize) {
                for (x in 0..patchSize) {
                    val color = bmp.getPixel(
                        min(startx + x, bmp.width - 1),
                        min(starty + y, bmp.height - 1)
                    )
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

        // TODO: maybe implement when we can get the VIN
//        @JvmStatic
//        fun isFirstEdition(context: Context, VIN: String): Boolean {
//            // If the vehicle isn't a Mach-E, nevermind
//            if (Vehicle.getVehicle(VIN) !is MachE) {
//                return false
//            }
//
//            // If the vehicle image doesn't exist, do nothing
//            val bmp = VehicleImages.getImage(context, VIN, 4) ?: return false
//
//            // Check if a pixel on the side view mirror is black or colored
//            val color = bmp.getPixel(220, 152)
//            val RGB = arrayOf(
//                (color shr 16) and 0xff,
//                (color shr 8) and 0xff,
//                color and 0xff
//            )
//
//            return RGB[0] > 0x08 || RGB[1] > 0x08 || RGB[2] > 0x08
//        }

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
                val icon = AppCompatResources.getDrawable(context, id)
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

// Save and restore settings/prefrences
class PrefManagement {

    companion object {
        // This is the latest version of the file format
        private const val JSON_SETTINGS_VERSION = 5
        // This is the first version of the setting files with the FordConnect API
        private const val JSON_SETTINGS_VERSION_FORDCONNECTAPI = 5
    }

    private lateinit var jsonOutput: String

    fun savePrefs(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            jsonOutput = readSettingInfo(context)
            val inStream: InputStream = ByteArrayInputStream(
                jsonOutput.toByteArray(
                    StandardCharsets.UTF_8
                )
            )
            val outputFilename = Misc.writeExternalFile(
                context,
                inStream,
                "fsw_settings-",
                Constants.APPLICATION_JSON
            )
            Toast.makeText(
                context,
                context.getString(R.string.settings_copied_to_download_description, outputFilename),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Throws(IOException::class)
    fun restorePrefs(context: Context, jsonFile: Uri) {
        // Try to construct a JsonObject from the JSON file
        val inStream = context.contentResolver.openInputStream(jsonFile)
        val json = StringBuilder()
        val reader = BufferedReader(InputStreamReader(inStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            json.append(line)
        }
        val jsonObject = JsonParser.parseString(json.toString()).asJsonObject

        CoroutineScope(Dispatchers.Main).launch {
            writeSettingInfo(context, jsonObject!!)
            Toast.makeText(context,
                context.getString(R.string.settings_restored_description), Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun writeSettingInfo(context: Context, jsonObject: JsonObject) =
        coroutineScope {
            withContext(Dispatchers.IO) {
                val gson = GsonBuilder().create()
                val imageDir = File(context.dataDir, Constants.IMAGES_FOLDER)
                if (!imageDir.exists()) {
                    imageDir.mkdir()
                }

                // Get the version of the backed-up data
                val versionItem: JsonPrimitive = jsonObject.getAsJsonPrimitive("version")
                val version = versionItem.asInt

                // Update users in the database, and remove all IDs from the current list
                // Don't try to restore for older user IDs
                if (version >= JSON_SETTINGS_VERSION_FORDCONNECTAPI) {

                    val info = InfoRepository(context)

                    // remove all token Ids from the database
                    val tokenIdDao = TokenIdDatabase.getInstance(context).tokenIdDao()
                    for (tokenId in tokenIdDao.findTokenIds() ) {
                        tokenIdDao.deleteTokenId(tokenId)
                    }

                    // Insert saved Token Ids
                    val tokenIds = jsonObject.getAsJsonArray("tokenIds")
                    val accessTokens : MutableMap<String,TokenId> = mutableMapOf()
                    for (items in tokenIds) {
                        val tokenIdInfo = gson.fromJson<TokenId>(
                            items.toString(),
                            object : TypeToken<TokenId?>() {}.type
                        )
                        tokenIdInfo.id = 0
                        info.insertTokenId(tokenIdInfo)
                        accessTokens[tokenIdInfo.tokenId!!] = tokenIdInfo
                    }

                    // remove all vehichles from the database
                    val vehicleInfoDao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao()
                    for (vehicle in vehicleInfoDao.findVehicleInfo() ) {
                        vehicleInfoDao.deleteVehicleInfoByVehicleId(vehicle.carStatus.vehicle.vehicleId)
                    }

                    // Insert saved vehicles
                    val vehicles = jsonObject.getAsJsonArray("vehicles")
                    val newVehicleIds : MutableList<String> = mutableListOf()
                    for (items in vehicles) {
                        val vehicleInfo = gson.fromJson<VehicleInfo>(
                            items.toString(),
                            object : TypeToken<VehicleInfo?>() {}.type
                        )
                        vehicleInfo.id = 0
                        info.insertVehicle(vehicleInfo)

                        newVehicleIds.add(vehicleInfo.carStatus.vehicle.vehicleId)

                        val vehicle = vehicleInfo.carStatus.vehicle

                        NetworkCalls.getVehicleImage(
                            context = context,
                            vehicleId = vehicle.vehicleId,
                            info = info,
                        )
                    }

                    // Update each widget instance to be sure there is a valid vehicle Id
                    val edit =
                        context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE)
                            .edit()
                    val widgetPrefs = context.getSharedPreferences(
                        Constants.WIDGET_FILE,
                        Context.MODE_PRIVATE
                    ).all

                    // If there aren't any new IDs, insert a blank one
                    if (newVehicleIds.isEmpty()) {
                        newVehicleIds.add("")
                    }

                    // If an old vehicle Id isn't among the new ones, substitute a new one
                    for (item in widgetPrefs) {
                        val key = item.key
                        val value = item.value
                        if (key.startsWith(Constants.VIN_KEY) && value !in newVehicleIds) {
                            edit.putString(key, newVehicleIds[0])
                        }
                    }
                    edit.apply()

                }

                // Update all the default preferences
                var edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
                val currentPrefs = PreferenceManager.getDefaultSharedPreferences(context).all
                var prefs = jsonObject.getAsJsonObject("prefs")

                for (item in prefs.entrySet()) {
                    val key = item.key
                    var value = item.value.asJsonArray

                    // If the key isn't used anymore, ignore it
                    if (!(key in currentPrefs.keys)) {
                        continue
                    }

                    // Version 2 didn't support "Integer" types
                    if (key == "surveyVersion" && version == 2) {
                        val tmp = value[1]
                        value = JsonArray()
                        value.add("Integer")
                        value.add(tmp)
                    }
                    when (value[0].asString) {
                        "String" -> edit.putString(key, value[1].asString).commit()
                        "Long" -> edit.putLong(key, value[1].asLong).commit()
                        "Integer" -> edit.putInt(key, value[1].asInt).commit()
                        else -> edit.putBoolean(key, value[1].asBoolean).commit()
                    }
                }

                // Update all the shared preferences
                edit = context.getSharedPreferences(StoredData.TAG, Context.MODE_PRIVATE).edit()
                val currentKeys = StoredData.keys
                prefs = jsonObject.getAsJsonObject(StoredData.TAG)
                for (item in prefs.entrySet()) {
                    val key = item.key
                    val value = item.value.asJsonArray

                    // If the key isn't used anymore, ignore it
                    if (!(key in currentKeys)) {
                        continue
                    }

                    when (value[0].asString) {
                        "String" -> edit.putString(key, value[1].asString).commit()
                        "Long" -> edit.putLong(key, value[1].asLong).commit()
                        "Integer" -> edit.putInt(key, value[1].asInt).commit()
                        else -> edit.putBoolean(key, value[1].asBoolean).commit()
                    }
                }

                // Make sure units use the new values
                Misc.updateUnits(context)

                // Restore per-app language settings on Android 13 and higher
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val language = PreferenceManager.getDefaultSharedPreferences(context).getString(
                        context.resources.getString(R.string.language_key), ""
                    )
                    val locale = LocaleListCompat.forLanguageTags(language)
                    AppCompatDelegate.setApplicationLocales(locale)
                }

                // Tell the widget to update
                CarStatusWidget.updateWidget(context)
            }
        }

    private suspend fun readSettingInfo(context: Context): String =
        coroutineScope {
            withContext(Dispatchers.IO) {
                val jsonData = LinkedHashMap<String, Any>()
                jsonData["version"] = JSON_SETTINGS_VERSION

                // Save the default preferences
                var prefs = PreferenceManager.getDefaultSharedPreferences(context).all
                val prefData = LinkedHashMap<String, Array<String>>()
                for (key in prefs.keys) {
                    val value = prefs[key]
                    val dataType = when (value) {
                        is String -> "String"
                        is Long -> "Long"
                        is Int -> "Integer"
                        else -> "Boolean"
                    }
                    prefData[key] = arrayOf(dataType, value.toString())
                }
                jsonData["prefs"] = prefData.clone()
                prefData.clear()

                // Save the shared preferences
                prefs = context.getSharedPreferences(StoredData.TAG, Context.MODE_PRIVATE).all
                for (key in prefs.keys) {
                    val value = prefs[key]
                    val dataType = when (value) {
                        is String -> "String"
                        is Long -> "Long"
                        is Int -> "Integer"
                        else -> "Boolean"
                    }
                    prefData[key] = arrayOf(dataType, value.toString())
                }
                jsonData[StoredData.TAG] = prefData.clone()
                prefData.clear()

                // Save database entries
                jsonData["tokenIds"] =
                    TokenIdDatabase.getInstance(context).tokenIdDao().findTokenIds()
                GsonBuilder().create().toJson(jsonData)

                jsonData["vehicles"] =
                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo()
                GsonBuilder().create().toJson(jsonData)
            }
        }
}

class VehicleImages {
    companion object {

        @JvmStatic
        fun deleteImages(context: Context, vehicleId: String) {
            val imageDir = File(context.dataDir, Constants.IMAGES_FOLDER)
            for (angle in 1..5) {
                val image = File(imageDir, "${vehicleId}.png")
                if (image.exists()) {
                    image.delete()
                }
            }
        }

        @JvmStatic
        fun getImage(context: Context, vehicleId: String): Bitmap? {
            val imageDir = File(context.dataDir, Constants.IMAGES_FOLDER)
            val image = File(imageDir, "${vehicleId}.png")
            return if (image.exists()) BitmapFactory.decodeFile(image.path) else null
        }

        @JvmStatic
        fun getRandomImage(context: Context, vehicleId: String): Bitmap? {
            return getImage(context, vehicleId)
        }
    }
}

class Misc {

    companion object {

        @JvmStatic
        fun copyStreams(inStream: InputStream, outStream: OutputStream) {
            try {
                var len: Int
                val buffer = ByteArray(65536)
                while (inStream.read(buffer).also { len = it } != -1) {
                    outStream.write(buffer, 0, len)
                }
            } catch (e: IOException) {
                Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyStream()", e)
            }
        }

        @JvmStatic
        fun writeExternalFile(
            context: Context,
            inStream: InputStream,
            baseFilename: String,
            mimeType: String?
        ): String {
            val time = LocalDateTime.now(ZoneId.systemDefault())
            val outputFilename =
                baseFilename + time.format(DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss", Locale.US))
            try {
                val outStream: OutputStream?
                val fileCollection: Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    fileCollection =
                        MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.Downloads.DISPLAY_NAME, outputFilename)
                    contentValues.put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    val resolver = context.contentResolver
                    val uri = resolver.insert(fileCollection, contentValues)
                        ?: throw IOException("Couldn't create MediaStore Entry")
                    outStream = resolver.openOutputStream(uri)
                } else {
                    val extension: String
                    extension =
                        when (mimeType) {
                            Constants.APPLICATION_JSON -> ".json"
                            Constants.APPLICATION_ZIP -> ".zip"
                            Constants.TEXT_HTML -> ".html"
                            else -> ".txt"
                        }
                    val outputFile = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        outputFilename + extension
                    )
                    outputFile.delete()
                    outputFile.createNewFile()
                    outStream = FileOutputStream(outputFile)
                }
                outStream?.let {
                    copyStreams(inStream, outStream)
                    outStream.close()
                }
            } catch (e: IOException) {
            }
            return outputFilename
        }

        // See if there was a crash, and if so dump the logcat output to a file
        @JvmStatic
        fun checkLogcat(context: Context): String? {
            try {
                // Dump the crash buffer and exit
                val process = Runtime.getRuntime().exec("logcat -d -b crash")
                val bufferedReader = BufferedReader(
                    InputStreamReader(process.inputStream)
                )
                val log = java.lang.StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    log.append("${line}\n")
                }

                // If we find something, write to logcat.txt file
                if (log.length > 0) {
                    val inStream: InputStream = ByteArrayInputStream(
                        log.toString().toByteArray(
                            StandardCharsets.UTF_8
                        )
                    )
                    val outputFilename = writeExternalFile(
                        context,
                        inStream,
                        "fsw_logcat-",
                        Constants.TEXT_PLAINTEXT
                    )

                    // Clear the crash log.
                    Runtime.getRuntime().exec("logcat -c")
                    return MessageFormat.format(context.getString(R.string.logcat_crashfile_formatstring),outputFilename)
                }
            } catch (e: IOException) {
            }
            return null
        }

        @JvmStatic
        fun elapsedSecondsToDescription(
            context: Context,
            seconds: Long
        ): String {
            val result = java.lang.StringBuilder()
            val minutes = seconds / 60
            val hours = minutes / 60
            val seconds_abbreviation = context.getString(R.string.seconds_abbreviation)
            val minutes_abbreviation = context.getString(R.string.minutes_abbreviation)
            val hour_abbreviation = context.getString(R.string.single_hour_abbr)
            val hours_abbreviation = context.getString(R.string.hours_abbr)
            // less than 1 minute
            if (minutes == 0L) {
                result.append("$seconds" + seconds_abbreviation)
            } else if (hours == 0L) {
                result.append("$minutes" + minutes_abbreviation)
                // not right on the minute
                if (seconds % 60 != 0L) {
                    result.append(", " + seconds % 60 + seconds_abbreviation)
                }
            } else {
                result.append(if (hours == 1L) hour_abbreviation else "$hours" + hours_abbreviation)
                // not right on the hour
                if (minutes % 60 != 0L) {
                    result.append(", " + minutes % 60 + minutes_abbreviation)
                }
            }
            return result.toString()
        }

        @JvmStatic
        fun elapsedMinutesToDescription(
            context: Context,
            interval: Long
        ): String {
            val hours = (interval / 60) % 24
            val days = interval / 60 / 24
            val minutes = interval % 60

            if (interval < 60) {
                return MessageFormat.format(
                    context.getString(R.string.elapsed_minutes_pattern),
                    minutes
                )
            } else if (interval == 60L) {
                return context.getString(R.string.elapsed_one_hour)
            } else if (interval < 120) {
                return MessageFormat.format(
                    context.getString(R.string.elapsed_one_hour_and_minutes_pattern),
                    minutes
                )
            } else if (days < 1) {
                return if ((minutes % 60) == 0L)
                    MessageFormat.format(
                        context.getString(R.string.elapsed_hours_pattern),
                        hours
                    )
                else
                    MessageFormat.format(
                        context.getString(R.string.elapsed_hours_and_minutes_pattern),
                        hours, minutes
                    )
            } else if (days == 1L) {
                return context.getString(R.string.elapsed_one_day)
            } else {
                return MessageFormat.format(
                    context.getString(R.string.elapsed_days_pattern),
                    days
                )
            }

        }

        @JvmStatic
        fun convertMillisToDate(millis: Long, format: String?): String {
            val time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            return time.format(DateTimeFormatter.ofPattern(format, Locale.ENGLISH))
        }

//        @JvmStatic
//        fun OTASupportCheck(alertStatus: String?): Boolean {
//            return alertStatus == null || !alertStatus.lowercase(Locale.getDefault())
//                .replace("[^a-z0-9]".toRegex(), "").contains("doesntsupport")
//        }

        @JvmStatic
        fun removeAPK(context: Context): File {
            val apkFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "app-release.apk"
            )
            apkFile.delete()
            return apkFile
        }

        // Check if we should display most recent survey information.
        @JvmStatic
        fun doSurvey(context: Context): Boolean {
            val surveyVersion_key = context.resources.getString(R.string.surveyVersion_key)
            val currentSurveyVersion =
                PreferenceManager.getDefaultSharedPreferences(context).getInt(surveyVersion_key, 0)
            return if (currentSurveyVersion <= Constants.SURVEY_VERSION) {
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putInt(surveyVersion_key, Constants.SURVEY_VERSION + 1).apply()
                true
            } else {
                false
            }
        }

        @Suppress("DEPRECATION")
        @JvmStatic
        fun checkDarkMode(context: Context, view: WebView) {
            val webViewPackageInfo = WebView.getCurrentWebViewPackage()
            val (major, _, _) = webViewPackageInfo!!.versionName.split(".").map { it.toInt() }
                .toTypedArray()
            if (major < 75) {
                LogFile.i(
                    context,
                    MainActivity.CHANNEL_ID,
                    "WebView version is ${webViewPackageInfo.versionName}, too old to support dark mode."
                )
            } else {
                val nightModeFlags =
                    context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                        && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)
                    ) {
                        WebSettingsCompat.setForceDark(
                            view.settings,
                            WebSettingsCompat.FORCE_DARK_ON
                        )
                    }
                } else {
                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                        && WebViewFeature.isFeatureSupported(
                            WebViewFeature.ALGORITHMIC_DARKENING
                        )
                    ) {
                        WebSettingsCompat.setAlgorithmicDarkeningAllowed(view.settings, true)
                    }
                }
            }
        }

        @JvmStatic
        fun ignoringBatteryOptimizations(context: Context): Boolean {
            val packageName = context.packageName
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return pm.isIgnoringBatteryOptimizations(packageName)
        }

        @JvmStatic
        fun isHibernating(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
                .getBoolean(context.resources.getString(R.string.hibernate_api_key), false)
        }

        // Units used to be read from user settings until the API was removed.  If these settings are using "FordPass settings" or "locale settings",
        // then examine the strings stored for the user and choose the appropriate setting value.
        @JvmStatic
        fun updateUnits(context: Context) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            val unitsKey = context.resources.getString(R.string.units_key)
            val units = Integer.parseInt(
                prefs.getString(unitsKey, context.resources.getString(R.string.units_mphpsi))
                    .toString()
            )
            if (units == 0) {
                CoroutineScope(Dispatchers.Main).launch {
                    val info = getInfo(context)
                    val map = mapOf(
                        "MPHPSI" to context.resources.getString(R.string.units_mphpsi),
                        "KPHPSI" to context.resources.getString(R.string.units_kphpsi),
                        "KPHKPA" to context.resources.getString(R.string.units_kphkpa),
                        "KPHBAR" to context.resources.getString(R.string.units_kphbar)
                    )

                    // TODO: these should be stored in preferences?
//                    val speed = info.user.uomSpeed
//                    val pressure = info.user.uomPressure
//                    val value = map.getOrDefault(speed + pressure, "2")
//                    prefs.edit().putString(unitsKey, value).apply()
                }
            }
        }

        private suspend fun getInfo(context: Context): InfoRepository =
            coroutineScope {
                withContext(Dispatchers.IO) { InfoRepository(context) }
            }

    }
}
