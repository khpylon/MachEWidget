package com.example.khughes.machewidget;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        String unmodifiedFilePath = folder.getPath();
        String relativePath = unmodifiedFilePath
                .substring(basePathLength);
        ZipEntry entry = new ZipEntry(relativePath + "/");
        out.putNextEntry(entry);
        out.closeEntry();
        File[] fileList = folder.listFiles();
        BufferedInputStream origin;
        for (File file : fileList) {
            unmodifiedFilePath = file.getPath();
            relativePath = unmodifiedFilePath
                    .substring(basePathLength);
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte[] data = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                entry = new ZipEntry(relativePath);
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
        zipSubFolder(out, sourceDir, sourceDir.getParentFile().getAbsolutePath().length() + 1);
        sourceDir = new File(context.getDataDir(), Constants.DATABASES_FOLDER);
        zipSubFolder(out, sourceDir, sourceDir.getParentFile().getAbsolutePath().length() + 1);
        out.setComment(Constants.FSVERSION_1);
        out.close();
        return zipFile;
    }

    public static void unzip(Context context, Uri zipFile) throws IOException, SettingFileException {
        File fromDir = context.getDataDir();

        // Close databases before we overwrite them
        VehicleInfoDatabase.closeInstance();
        UserInfoDatabase.closeInstance();

        InputStream inStream = context.getContentResolver().openInputStream(zipFile);
        File tmpfile = File.createTempFile("temp", ".zip");
        OutputStream outStream = new FileOutputStream(tmpfile);
        Utils.copyStreams(inStream, outStream);
        inStream.close();
        outStream.close();

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
                path.replace(Constants.OLDAPPNAME, context.getPackageName());

                File unzipFile = new File(path);

                if (entry.isDirectory()) {
                    if (!unzipFile.isDirectory()) {
                        unzipFile.mkdirs();
                    }
                } else {
                    if (unzipFile.exists()) {
                        unzipFile.delete();
                    }
                    FileOutputStream fout = new FileOutputStream(path, false);
                    try {
                        byte[] buffer = new byte[65536];
                        int len;
                        while ((len = zin.read(buffer)) != -1) {
                            fout.write(buffer, 0, len);
                        }
                        zin.closeEntry();
                    } finally {
                        fout.close();
                    }
                    unzipFile.setLastModified(entry.getLastModifiedTime().toMillis());
                }
            }
            Toast.makeText(context, "Settings restored.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Unable to restore settings.", Toast.LENGTH_SHORT).show();
            LogFile.e(context, MainActivity.CHANNEL_ID, "Exception in ZipManager.unzipStuff()", e);
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