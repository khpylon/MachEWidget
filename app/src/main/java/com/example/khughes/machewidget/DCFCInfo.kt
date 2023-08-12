package com.example.khughes.machewidget

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
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

    constructor()

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
class DCFCSession(session: DCFCInfo, var updates: MutableList<DCFCUpdate>) {
    @SerializedName("vin")
    var VIN: String? = ""
    var chargeType: String? = ""
    var plugInTime: String? = ""
    var initialDte: Double? = 0.0
    var chargeLocationName: String? = ""
    var network: String? = ""

    init {
        VIN = session.VIN
        chargeType = session.chargeType
        plugInTime = session.plugInTime
        chargeLocationName = session.chargeLocationName
        initialDte = session.initialDte
        chargeLocationName = session.chargeLocationName
        network = session.network
    }
}

class DCFC {

    companion object {

        private const val OLDCHARGINGSESSIONFILENAME = "dcfcsession.txt"
        private const val OLDCHARGINGFILENAME = "dcfc.txt"
        private const val CHARGINGSESSIONFILENAME = "dcfcsession.json"

        const val CHARGINGFILENAME = "dcfclog.json"

        private var mutex = Mutex()

        @JvmStatic
        fun renameLogFiles(context: Context) {
            if( File(context.dataDir, OLDCHARGINGFILENAME).exists() ) {
                val oldLogFile = File(context.dataDir, OLDCHARGINGFILENAME)
                val newLogFile = File(context.dataDir, CHARGINGFILENAME)
                oldLogFile.renameTo(newLogFile)
            }
            if( File(context.dataDir, OLDCHARGINGSESSIONFILENAME).exists() ) {
                val oldLogFile = File(context.dataDir, OLDCHARGINGSESSIONFILENAME)
                val newLogFile = File(context.dataDir, CHARGINGSESSIONFILENAME)
                oldLogFile.renameTo(newLogFile)
            }
            val sessionFIle = File(context.dataDir, CHARGINGSESSIONFILENAME)
            if( sessionFIle.exists() ) {
                mergeChargingSessions(context)
            }
        }

        private fun mergeChargingSessions(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                mutex.lock()
                try {

                    // Create input and output file things
                    val sessionFile = File(context.dataDir, CHARGINGSESSIONFILENAME)
                    val inputStream: InputStream = FileInputStream(sessionFile)
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val logFile = File(context.dataDir, CHARGINGFILENAME)
                    val outputStream: OutputStream = FileOutputStream(logFile, true)
                    val printStream = PrintStream(outputStream)

                    // This list contains the key for each session in chronological order
                    val order = mutableListOf<String>()
                    // This map stores the DCFC info for each session
                    val sessionMap: MutableMap<String, MutableList<DCFCInfo>> = mutableMapOf()

                    // Process each entry in the DCFC session file
                    val gson = GsonBuilder().create()
                    for (line in reader.lineSequence()) {
                        // Use the info's VIN and plug-in time as a key for the session
                        val info = gson.fromJson(line, DCFCInfo::class.java) as DCFCInfo
                        val key = info.VIN + info.plugInTime

                        // If data for the session exists, add it to the list
                        if (sessionMap.containsKey(key)) {
                            sessionMap[key]!!.add(info)
                        }
                        // Otherwise create a new session
                        else {
                            order.add(key)
                            sessionMap[key] = mutableListOf(info)
                        }
                    }

                    val sessionList: MutableList<DCFCSession> = mutableListOf()
                    // Go through the session in chronological order and process them
                    for (key in order) {
                        val updates = mutableListOf<DCFCUpdate>()
                        val session = sessionMap[key]!![0]
                        for (info in sessionMap[key]!!) {
                            updates.add(DCFCUpdate(info))
                        }
                        sessionList.add(DCFCSession(session, updates))
                        val dcfcSession = DCFCSession(session, updates)
                        val message = gson.toJson(dcfcSession)
                        printStream.println(message)
                    }

                    // Clean up
                    inputStream.close()
                    outputStream.close()
                    sessionFile.delete()
                } catch (_: FileNotFoundException) {
                } catch (e: Exception) {
                    LogFile.e(
                        context,
                        MainActivity.CHANNEL_ID,
                        "exception in DCFC.mergeChargingSessions()",
                        e
                    )
                }
                mutex.unlock()
            }
        }

        @JvmStatic
        fun logFileExists(context: Context): Boolean {
            val logDCFC = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.dcfclog_key), false)

            return logDCFC && File(context.dataDir, CHARGINGFILENAME).exists()
        }


        @JvmStatic
        fun eraseLogFile(context: Context) {
            val logFile = File(context.dataDir, CHARGINGFILENAME)
            logFile.delete()
        }

        // Append a new entry to the DCFC file
        @JvmStatic
        fun updateChargingSession(context: Context, chargeInfo: DCFCInfo) {
            val logDCFC = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.dcfclog_key), false)

            if (logDCFC) {
                CoroutineScope(Dispatchers.IO).launch {
               //     mutex.lock()

                    var updated = false
                    val update = DCFCUpdate(chargeInfo)
                    try {

                        // Create input and output file things
                        val newLogFile = File(context.dataDir,  CHARGINGFILENAME)
                        val oldLogFile = File(context.dataDir,"tmpfile")

                        newLogFile.renameTo(oldLogFile)

                        val inputStream: InputStream = FileInputStream(oldLogFile)
                        val reader = BufferedReader(InputStreamReader(inputStream))

                        val outputStream: OutputStream = FileOutputStream(newLogFile, true)
                        val printStream = PrintStream(outputStream)

                        // Read entry in the DCFC file
                        val gson = GsonBuilder().create()
                        for (line in reader.lineSequence()) {
                            // Use the info's VIN and plug-in time as a key for the session
                            val session = gson.fromJson(line, DCFCSession::class.java) as DCFCSession

                            // If this session matches the current info, add the update and write it 
                            if(session.VIN == chargeInfo.VIN && session.plugInTime == chargeInfo.plugInTime) {
                                session.updates.add(update)
                                val message = gson.toJson(session)
                                printStream.println(message)
                                updated = true
                            }
                            // Otherwise just copy the data
                            else {
                                printStream.println(line)
                            }
                        }

                        // If we didn't find a matching session, create a new one
                        if(!updated) {
                            val session = DCFCSession(chargeInfo, mutableListOf(update))
                            val message = gson.toJson(session)
                            printStream.println(message)
                        }

                        // Finish the updates
                        printStream.flush()
                        inputStream.close()
                        outputStream.close()

                        // Replace the original file with the updated one
                        oldLogFile.delete()
                    } catch (_: FileNotFoundException) {
                    } catch (e: Exception) {
                        LogFile.e(context,
                            MainActivity.CHANNEL_ID,
                            "exception in DCFC.updateChargingSession()",
                            e
                        )
                    }
                }
            //    mutex.unlock()
            }
        }

        // Append a new entry to the DCFC file
        @JvmStatic
        fun getChargingSessions(context: Context) : MutableList<DCFCSession> {
            var activeSessions: MutableList<DCFCSession> = mutableListOf()
            val futureResult: Deferred<MutableList<DCFCSession>> = GlobalScope.async {
                mutex.lock()

                try {

                    // Create input and output file things
                    val logFile = File(context.dataDir, CHARGINGFILENAME)
                    val inputStream: InputStream = FileInputStream(logFile)
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    // Read entry in the DCFC file
                    val gson = GsonBuilder().create()
                    for (line in reader.lineSequence()) {
                        val session = gson.fromJson(line, DCFCSession::class.java) as DCFCSession
                        if(session.updates.size > 1) {
                            activeSessions.add(session)
                        }
                    }

                    inputStream.close()
                } catch (_: FileNotFoundException) {
                } catch (e: Exception) {
                    LogFile.e(context,
                        MainActivity.CHANNEL_ID,
                        "exception in DCFC.getChargingSessions()",
                        e
                    )
                }
                mutex.unlock()
                activeSessions

            }
            runBlocking { activeSessions = futureResult.await() }
            return activeSessions
        }


        @JvmStatic
        fun purgeChargingData(context: Context) {
            try {
                // Assign 180 days prior to now as cut-off time
                val cutOffTime =
                    Instant.now().minusSeconds(TimeUnit.DAYS.toSeconds(180)).toEpochMilli()
                val gson = GsonBuilder().create()
                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat(Constants.CHARGETIMEFORMAT, Locale.ENGLISH)
                val oldLogFile = File(context.dataDir, CHARGINGFILENAME)
                val newLogFile = File(context.dataDir, "tmplogfile")

                val inputStream: InputStream = FileInputStream(oldLogFile)
                val outputStream: OutputStream = FileOutputStream(newLogFile)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val printStream = PrintStream(outputStream)

                // process each entry in the log file
                for (line in reader.lineSequence()) {
                    val data = gson.fromJson(line, DCFCSession::class.java)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    var thisTime = cutOffTime
                    try {
                        data.plugInTime?.let { sdf.parse(it)?.let { cal.time = it } }
                        thisTime = cal.toInstant().toEpochMilli()
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
                LogFile.e(context,MainActivity.CHANNEL_ID, "exception in DCFC.purgeChargingData()", e)
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
