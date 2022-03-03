package com.example.khughes.machewidget;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

    public static void clearLogFile(Context context) {
        try {
            File logFile = new File(context.getDataDir(), LOGFILENAME);
            logFile.delete();
            logFile.createNewFile();
        } catch (Exception e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.clearLogFile()", e);
        }
    }

    public static void appendToLogFile(Context context, String tag, String message) {
        Boolean verbose = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.okhttp3_key), false);
        if (verbose && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                File logFile = new File(context.getDataDir(), LOGFILENAME);
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

    public static boolean copyLogFile(Context context) {
        try {
            File logFile = new File(context.getDataDir(), LOGFILENAME);
            InputStream inputStream = new FileInputStream(logFile);
            Uri fileCollection = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                fileCollection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                        ? MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                        : MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, LOGFILENAME);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(fileCollection, contentValues);
            if (uri == null) {
                throw new IOException("Couldn't create MediaStore Entry");
            }
            OutputStream outStream = resolver.openOutputStream(uri);
            int len;
            byte[] buffer = new byte[65536];
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inputStream.close();
            outStream.close();
            clearLogFile(context);
            return true;
        } catch (Exception e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyLogFile()", e);
            return false;
        }
    }
}
