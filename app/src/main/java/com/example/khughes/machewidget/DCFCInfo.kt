package com.example.khughes.machewidget

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.annotation.Generated

// Information read from Ford, except for currentDte, batteryFillLevel, and time
@Generated("jsonschema2pojo")
class DCFCInfo {
    @SerializedName("vin")
    var VIN: String? = ""
    var chargeType: String? = ""
    var plugInTime: String? = ""
    var initialDte: Double? = 0.0
    var energy: Double? = 0.0
    var power: Double? = 0.0
    var chargeLocationName: String? = ""
    var network: String? = ""
    var currentDte: Double? = 0.0
    var batteryFillLevel: Double? = 0.0
    var time: String? = ""
}

// Information saved as charging progresses.
@Generated("jsonschema2pojo")
class DCFCUpdate {
    var time: String? = ""
    var energy: Double? = 0.0
    var power: Double? = 0.0
    var dte: Double? = 0.0
    var batteryFillLevel: Double? = 0.0

    constructor() {
    }

    constructor(session: DCFCInfo) {
        time = session.time
        energy = session.energy
        power = session.power
        dte = session.currentDte
        batteryFillLevel = session.batteryFillLevel
    }
}

// Information saved for a single charging session.
@Generated("jsonschema2pojo")
class DCFCSession {
    @SerializedName("vin")
    var VIN: String? = ""
    var chargeType: String? = ""
    var plugInTime: String? = ""
    var initialDte: Double? = 0.0
    var chargeLocationName: String? = ""
    var network: String? = ""
    lateinit var updates: MutableList<DCFCUpdate>

    constructor(session: DCFCInfo, updates: MutableList<DCFCUpdate>) {
        VIN = session.VIN
        chargeType = session.chargeType
        plugInTime = session.plugInTime
        chargeLocationName = session.chargeLocationName
        initialDte = session.initialDte
        chargeLocationName = session.chargeLocationName
        network = session.network
        this.updates = updates
    }
}

class DCFC {

    companion object {

        const val CHARGINGSESSIONFILENAME = "dcfcsession.txt"
        const val CHARGINGFILENAME = "dcfc.txt"

        @JvmStatic
        fun logFileExists(context: Context): Boolean {
            val logDCFC = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.dcfclog_key), false)

            return logDCFC && File(context.dataDir, CHARGINGFILENAME).exists()
        }

        @JvmStatic
        fun sessionFileExists(context: Context): Boolean {
            return File(context.dataDir, CHARGINGSESSIONFILENAME).exists()
        }

        @JvmStatic
        fun eraseLogFile(context: Context) {
            val logFile = File(context.dataDir, CHARGINGFILENAME)
            logFile.delete()
            val sessionFile = File(context.dataDir, CHARGINGSESSIONFILENAME)
            sessionFile.delete()
        }

        // Append a new entry to the DCFC file
        @JvmStatic
        fun updateChargingSession(context: Context, chargeInfo: DCFCInfo) {
            val logDCFC = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.dcfclog_key), false)

            if (logDCFC) {
                try {
                    val logFile = File(context.dataDir, CHARGINGSESSIONFILENAME)
                    val outputStream = FileOutputStream(logFile, true)
                    val printStream = PrintStream(outputStream)
                    val message = GsonBuilder().create().toJson(chargeInfo)
                    printStream.println(message)
                    outputStream.close()
                } catch (_: FileNotFoundException) {
                } catch (e: Exception) {
                    Log.e(MainActivity.CHANNEL_ID, "exception in DCFC.updateChargingSession()", e)
                }
            }
        }

        @JvmStatic
        private fun writeSession(
            session: DCFCInfo,
            updates: MutableList<DCFCUpdate>,
            printStream: PrintStream
        ) {
            val gson = GsonBuilder().create()
            val dcfcSession = DCFCSession(session, updates)
            val message = gson.toJson(dcfcSession)
            printStream.println(message)
        }

        @JvmStatic
        fun consolidateChargingSessions(context: Context) {
            val logDCFC = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.dcfclog_key), false)

            if (logDCFC) {
                try {
                    val gson = GsonBuilder().create()
                    val sessionFile = File(context.dataDir, CHARGINGSESSIONFILENAME)
                    val logFile = File(context.dataDir, CHARGINGFILENAME)
                    val inputStream: InputStream = FileInputStream(sessionFile)
                    val outputStream: OutputStream = FileOutputStream(logFile, true)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val printStream = PrintStream(outputStream)

                    var session = DCFCInfo()
                    val updates = mutableListOf<DCFCUpdate>()
                    var lastTime = ""
                    for (line in reader.lineSequence()) {
                        session = gson.fromJson(line, DCFCInfo::class.java)
                        if (lastTime != "" && lastTime != session.plugInTime) {
                            writeSession(session, updates, printStream)
                            updates.clear()
                        }
                        lastTime = session.plugInTime!!
                        updates.add(DCFCUpdate(session))
                    }

                    writeSession(session, updates, printStream)
                    inputStream.close()
                    outputStream.close()
                    sessionFile.delete()
                } catch (_: FileNotFoundException) {
                } catch (e: Exception) {
                    Log.e(
                        MainActivity.CHANNEL_ID,
                        "exception in DCFC.consolidateChargingSessions()",
                        e
                    )
                }
            }
        }

        @JvmStatic
        fun pseudoConsolidateChargingSessions(context: Context) : DCFCSession? {
            var dcfcSession: DCFCSession? = null
            try {
                val gson = GsonBuilder().create()
                val sessionFile = File(context.dataDir, CHARGINGSESSIONFILENAME)
                val inputStream: InputStream = FileInputStream(sessionFile)
                val reader = BufferedReader(InputStreamReader(inputStream))

                var session = DCFCInfo()
                val updates = mutableListOf<DCFCUpdate>()
                var lastTime = ""
                for (line in reader.lineSequence()) {
                    session = gson.fromJson(line, DCFCInfo::class.java)
                    if (lastTime != "" && lastTime != session.plugInTime) {
                        updates.clear()
                    }
                    lastTime = session.plugInTime!!
                    updates.add(DCFCUpdate(session))
                }

                inputStream.close()
                dcfcSession = DCFCSession(session, updates)
            } catch (_: FileNotFoundException) {
            } catch (e: Exception) {
                Log.e(
                    MainActivity.CHANNEL_ID,
                    "exception in DCFC.pseudoConsolidateChargingSessions()",
                    e
                )
            }
            return dcfcSession
        }

        @JvmStatic
        fun purgeChargingData(context: Context) {
            try {
                // Assign 180 days prior to now as cut-off time
                val cutOffTime =
                    Instant.now().minusSeconds(TimeUnit.DAYS.toSeconds(180)).toEpochMilli()
                val gson = GsonBuilder().create()
                val cal = Calendar.getInstance();
                val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH);
                val oldLogFile = File(context.dataDir, CHARGINGFILENAME)
                val newLogFile = File(context.dataDir, "tmplogfile")

                val inputStream: InputStream = FileInputStream(oldLogFile)
                val outputStream: OutputStream = FileOutputStream(newLogFile)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val printStream = PrintStream(outputStream)

                // process each entry in the log file
                for (line in reader.lineSequence()) {
                    val data = gson.fromJson(line, DCFCSession::class.java)
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
                    var thisTime = cutOffTime
                    try {
                        cal.setTime(sdf.parse(data.plugInTime));
                        thisTime = cal.toInstant().toEpochMilli();
                    } catch (_: ParseException) {
                    }
                    // Copy all times after the cut-off time
                    if (thisTime >= cutOffTime) {
                        printStream.println(line)
                    }
                }

                // Finish the updates
                printStream.flush()
                inputStream.close()
                outputStream.close()

                // Replace the original file with the updated one
                oldLogFile.delete()
                newLogFile.renameTo(oldLogFile)
            } catch (_: FileNotFoundException) {
            } catch (e: Exception) {
                Log.e(MainActivity.CHANNEL_ID, "exception in DCFC.updateChargingData()", e)
            }
        }

    }

}
//class TestExclStrat : ExclusionStrategy {
//    override fun shouldSkipClass(arg0: Class<*>?): Boolean {
//        return false
//    }
//
//    override fun shouldSkipField(f: FieldAttributes): Boolean {
//        return  f.name in listOf("VIN", "chargeType", "initialDte",
//            "finalDte", "chargeLocationName", "network")
//    }
//}
