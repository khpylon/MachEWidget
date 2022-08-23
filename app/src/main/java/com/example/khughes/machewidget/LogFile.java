package com.example.khughes.machewidget;

import android.content.Context;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogFile {

    private static final String LOGFILENAME = "fsw_logfile";
    private static final String BACKUPLOGFILENAME = LOGFILENAME + ".0";

    private static final int LOGFILE_SIZE = 750000;

    public static void clearLogFile(Context context, boolean moveBackup) {
        try {
            File backupLogFile = new File(context.getDataDir(), BACKUPLOGFILENAME);
            backupLogFile.delete();
            backupLogFile.createNewFile();
            File logFile = new File(context.getDataDir(), LOGFILENAME);
            if (moveBackup) {
                InputStream inStream = new FileInputStream(logFile);
                OutputStream outStream = new FileOutputStream(backupLogFile);
                Misc.copyStreams(inStream, outStream);
                inStream.close();
                outStream.close();
            }
            logFile.delete();
            logFile.createNewFile();
        } catch (Exception e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.clearLogFile()", e);
        }
    }

    public static void appendToLogFile(Context context, String tag, String message) {
        boolean verbose = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.logging_key), false);
        if (verbose) {
            try {
                File logFile = new File(context.getDataDir(), LOGFILENAME);
                if (logFile.length() > LOGFILE_SIZE) {
                    clearLogFile(context, true);
                }
                FileOutputStream outputStream = new FileOutputStream(logFile, true);
                PrintStream printstream = new PrintStream(outputStream);
                LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
                String timeText = time.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss", Locale.US));
                printstream.println(MessageFormat.format("{0} {1}: {2}", timeText, tag, message));
                outputStream.close();
            } catch (Exception e) {
                Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.createLogFile()", e);
            }
        }
    }

    public static void d(Context context, String tag, String message) {
        appendToLogFile(context, "D " + tag, message);
    }

    public static void e(Context context, String tag, String message) {
        appendToLogFile(context, "E " + tag, message);
    }

    public static void e(Context context, String tag, String message, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        appendToLogFile(context, "E " + tag, message + "\n" + sw.toString());
    }

    public static void i(Context context, String tag, String message) {
        appendToLogFile(context, "I " + tag, message);
    }

    public static String copyLogFile(Context context) {
        try {
            LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());

            // Copy all logfile contents to a temporary file
            File tmpLogfile = File.createTempFile("temp", ".zip");
            OutputStream outStream = new FileOutputStream(tmpLogfile);
            File backupLogFile = new File(context.getDataDir(), BACKUPLOGFILENAME);
            if (backupLogFile.exists()) {
                InputStream inputStream = new FileInputStream(backupLogFile);
                Misc.copyStreams(inputStream, outStream);
                inputStream.close();
            }
            File logFile = new File(context.getDataDir(), LOGFILENAME);
            InputStream inputStream = new FileInputStream(logFile);
            Misc.copyStreams(inputStream, outStream);
            inputStream.close();
            outStream.close();

            // Copy the temp file to the output file, then get rid of the temp file.
            String outputFilename = Misc.writeExternalFile (context,  new FileInputStream(tmpLogfile), LOGFILENAME+"-", Constants.TEXT_PLAINTEXT);
            tmpLogfile.delete();

            return MessageFormat.format("Log file \"{0}.txt\" copied to Download folder.", outputFilename);
        } catch (FileNotFoundException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyLogFile()", e);
            return "The log file doesn't exist.";
        } catch (Exception e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyLogFile()", e);
            return "An error occurred saving the log file.";
        }
    }
}
