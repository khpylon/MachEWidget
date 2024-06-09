package com.example.khughes.machewidget

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.Misc.Companion.copyStreams
import com.example.khughes.machewidget.Misc.Companion.writeExternalFile
import java.io.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.text.MessageFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object LogFile {
    private const val LOGFILENAME = "fsw_logfile"
    private const val BACKUPLOGFILENAME = LOGFILENAME + ".0"
    private const val LOGFILE_SIZE = 2500000

    private lateinit var mContext: WeakReference<Context>

    fun defineContext(context: Context) {
        mContext = WeakReference(context)
    }

    fun clearLogFile(context: Context, moveBackup: Boolean) {
        try {
            val backupLogFile = File(context.dataDir, BACKUPLOGFILENAME)
            backupLogFile.delete()
            backupLogFile.createNewFile()
            val logFile = File(context.dataDir, LOGFILENAME)
            if (moveBackup) {
                val inStream: InputStream = FileInputStream(logFile)
                val outStream: OutputStream = FileOutputStream(backupLogFile)
                copyStreams(inStream, outStream)
                inStream.close()
                outStream.close()
            }
            logFile.delete()
            logFile.createNewFile()
        } catch (e: Exception) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.clearLogFile()", e)
        }
    }

    fun appendToLogFile(context: Context, tag: String?, message: String?) {
        val verbose = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.resources.getString(R.string.logging_key), false)
        if (verbose) {
            try {
                val logFile = File(context.dataDir, LOGFILENAME)
                if (logFile.length() > LOGFILE_SIZE) {
                    clearLogFile(context, true)
                }
                val outputStream = FileOutputStream(logFile, true)
                val printstream = PrintStream(outputStream)
                val time = LocalDateTime.now(ZoneId.systemDefault())
                val timeText = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss", Locale.US))
                printstream.println(MessageFormat.format("{0} {1}: {2}", timeText, tag, message))
                outputStream.close()
            } catch (e: Exception) {
                Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.createLogFile()", e)
            }
        }
    }

//    @JvmStatic
//    fun d(context: Context, tag: String, message: String?) {
//        appendToLogFile(context, "D $tag", message)
//    }
//
//    @JvmStatic
//    fun e(context: Context, tag: String, message: String?) {
//        appendToLogFile(context, "E $tag", message)
//    }
//
//    @JvmStatic
//    fun e(context: Context, tag: String, message: String, e: Exception) {
//        val sw = StringWriter()
//        val pw = PrintWriter(sw)
//        e.printStackTrace(pw)
//        appendToLogFile(
//            context, "E $tag", """
//     $message
//     $sw
//     """.trimIndent()
//        )
//    }

    @JvmStatic
    fun i(context: Context, tag: String, message: String?) {
        appendToLogFile(context, "I $tag", message)
    }

    @JvmStatic
    fun d(tag: String, message: String?) {
        val context = mContext.get()
        context?.let{ appendToLogFile(context, "D $tag", message) }
    }

    @JvmStatic
    fun e(tag: String, message: String?) {
        val context = mContext.get()
        context?.let{ appendToLogFile(context, "E $tag", message) }
    }

    @JvmStatic
    fun e(tag: String, message: String, e: Exception) {
        val context = mContext.get()
        context?.let {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            appendToLogFile(
                context, "E $tag", """
     $message
     $sw
     """.trimIndent()
            )
        }
    }

    @JvmStatic
    fun i(tag: String, message: String?) {
        val context = mContext.get()
        context?.let{ appendToLogFile(context, "I $tag", message) }
    }

    @JvmStatic
    fun copyLogFile(context: Context): String {
        return try {
            // Copy all logfile contents to a temporary file
            val tmpLogfile = File.createTempFile("temp", ".zip")
            val outStream: OutputStream = FileOutputStream(tmpLogfile)
            val backupLogFile = File(context.dataDir, BACKUPLOGFILENAME)
            if (backupLogFile.exists()) {
                val inputStream: InputStream = FileInputStream(backupLogFile)
                copyStreams(inputStream, outStream)
                inputStream.close()
            }
            val logFile = File(context.dataDir, LOGFILENAME)
            val inputStream: InputStream = FileInputStream(logFile)
            copyStreams(inputStream, outStream)
            inputStream.close()
            outStream.close()

            // Copy the temp file to the output file, then get rid of the temp file.
            val outputFilename = writeExternalFile(
                context,
                FileInputStream(tmpLogfile),
                LOGFILENAME + "-",
                Constants.TEXT_PLAINTEXT
            )
            tmpLogfile.delete()
            MessageFormat.format("Log file \"{0}.txt\" copied to Download folder.", outputFilename)
        } catch (e: FileNotFoundException) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyLogFile()", e)
            "The log file doesn't exist."
        } catch (e: Exception) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyLogFile()", e)
            "An error occurred saving the log file."
        }
    }
}