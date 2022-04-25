package com.example.khughes.machewidget;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private static final String LOGFILENAME = "mache_logfile";

    public static void clearLogFile(Context context, boolean moveBackup) {
        try {
            File backupLogFile = new File(context.getDataDir(), LOGFILENAME + ".bak");
            backupLogFile.delete();
            File logFile = new File(context.getDataDir(), LOGFILENAME);
            if (moveBackup) {
                InputStream inStream = new FileInputStream(logFile);
                OutputStream outStream = new FileOutputStream(backupLogFile);
                Utils.copyStreams(inStream, outStream);
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
        if (verbose && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                File logFile = new File(context.getDataDir(), LOGFILENAME);
                if (logFile.length() > 750000) {
                    File logFile2 = new File(context.getDataDir(), LOGFILENAME + ".bak");
                    logFile2.delete();
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
            Uri fileCollection = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                fileCollection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
            LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
            String logFilename =  LOGFILENAME + "-" + time.format(DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss", Locale.US));
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, logFilename);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(fileCollection, contentValues);
            if (uri == null) {
                throw new IOException("Couldn't create MediaStore Entry");
            }
            OutputStream outStream = resolver.openOutputStream(uri);
            File logFile = new File(context.getDataDir(), LOGFILENAME+".bak");
            if(logFile.exists()) {
                InputStream inputStream = new FileInputStream(logFile);
                Utils.copyStreams(inputStream, outStream);
                inputStream.close();
            }
            logFile = new File(context.getDataDir(), LOGFILENAME);
            InputStream inputStream = new FileInputStream(logFile);
            Utils.copyStreams(inputStream, outStream);
            inputStream.close();
            outStream.close();
            clearLogFile(context, true);
            return null;
        } catch (FileNotFoundException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyLogFile()", e);
            return "The log file doesn't exist.";
        } catch (Exception e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyLogFile()", e);
            return "An error occurred saving the log file.";
        }
    }
}
