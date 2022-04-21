package com.example.khughes.machewidget;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import org.commonmark.node.StrongEmphasis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

// Copied from https://mobikul.com/zip-unzip-file-folder-android-programmatically/

public class ZipManager {
    private static final int BUFFER_SIZE = 6 * 1024;

    private static void zipSubFolder(ZipOutputStream out, File folder,
                                     int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte[] data = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public static File zipSharedPrefs(Context context) throws IOException {
        File zipFile = File.createTempFile("temp", ".zip");
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        File sourceDir = new File(context.getDataDir(), Constants.SHAREDPREFS_FOLDER);
        zipSubFolder(out, sourceDir, sourceDir.getAbsolutePath().length()+1);
        out.setComment(Constants.FSVERSION_1);
        out.close();
        return zipFile;
    }

    public static void unzip(Context context, Uri zipFile) throws IOException, SettingFileException {
        File dataDir = context.getDataDir();
        File fromDir = new File(dataDir, Constants.SHAREDPREFS_FOLDER);

        InputStream inStream = context.getContentResolver().openInputStream(zipFile);
        File tmpfile = File.createTempFile("temp", ".zip");
        String x = tmpfile.toPath().toString();
        OutputStream outStream = new FileOutputStream(tmpfile);
        {
            int len;
            byte[] buffer = new byte[65536];
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inStream.close();
            outStream.close();
        }

        ZipFile localZipFile = new ZipFile(tmpfile);
        String comment = localZipFile.getComment();
        localZipFile.close();
        if (comment == null || !comment.equals(Constants.FSVERSION_1)) {
            tmpfile.delete();
            throw new SettingFileException();
        }

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpfile));
        try {
            ZipEntry entry;

            while ((entry = zin.getNextEntry()) != null) {
                String path = fromDir + File.separator + entry.getName();

                if (entry.isDirectory()) {
                    File unzipFile = new File(path);
                    if (!unzipFile.isDirectory()) {
                        unzipFile.mkdirs();
                    }
                } else {
                    FileOutputStream fout = new FileOutputStream(path, false);

                    try {
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                        zin.closeEntry();
                    } finally {
                        fout.close();
                    }
                }
            }
        } finally {
            zin.close();
            tmpfile.delete();
        }
    }

    public static class SettingFileException extends Exception {

    }

    public static void zipStuff(Context context) {
        File zipFile = null;
        try {
            zipFile = zipSharedPrefs(context);
            InputStream inputStream = new FileInputStream(zipFile);
            Uri fileCollection = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                fileCollection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, "prefs.zip");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/zip");
            ContentResolver resolver = context.getContentResolver();

            Uri uri = resolver.insert(fileCollection, contentValues);
            OutputStream outStream = resolver.openOutputStream(uri);
            int len;
            byte[] buffer = new byte[65536];
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            inputStream.close();
            outStream.close();
        } catch (Exception e) {
            LogFile.e(context, MainActivity.CHANNEL_ID, "Exception in ZipManager.zipStuff()", e);
        } finally {
            if (zipFile != null) {
                zipFile.delete();
            }
        }
    }

}