package com.taxi.nanny.utils;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.taxi.nanny.BuildConfig;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FilePath
{
    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    public static String getPath(final Context context, final Uri uri)
    {

        if (BuildConfig.DEBUG) {
            Log.e("TAG" + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );

            final boolean isKitKatPlus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKatPlus && DocumentsContract.isDocumentUri(context, uri))
            {
                // ExternalStorageProvider
                Log.e("kitKat", "kitkat methiod is worrking");

                if (isExternalStorageDocument(uri))
                {
                    Log.e("kitkat", "kitkat if condition is working");
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type))
                    {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else
                    {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri))
                {
                    Log.e("Isdownloaddocument", "isdownload document is working");
                    final String id = DocumentsContract.getDocumentId(uri);
                    Log.e("id", id);
                    if (id != null && id.startsWith("raw:")) {
                        return id.substring(4);
                    }


                    String[] contentUriPrefixesToTry = new String[]{
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                    };


                    for (String contentUriPrefix : contentUriPrefixesToTry)
                    {
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));

                        try {
                            String path = getDataColumn(context, contentUri, null, null);
                            Log.e("PathInside ",path);
                            if (path != null) {
                                return path;
                            }
                        } catch (Exception e) {}
                    }

                    // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                    String fileName = getFileName(context, uri);
                    Log.e("fileName ",fileName);
                    File cacheDir = getDocumentCacheDir(context);
                    Log.e("cacheDir ",cacheDir+"");
                    File file = generateFileName(fileName, cacheDir);
                    Log.e("file ",file+"");
                    String destinationPath = null;
                    if (file != null) {
                        destinationPath = file.getAbsolutePath();
                        Log.e("destinationPath ",destinationPath+"");
                        saveFileFromUri(context, uri, destinationPath);
                    }

                    return destinationPath;


                   /* final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    Log.e("contentUri", "getPath: " + contentUri);
                    return getDataColumn(context, contentUri, null, null);*/
                }
                // MediaProvider
                else if (isMediaDocument(uri))
                {
                    Log.e("isMediaDownload", "isMediadownload is working");
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type))
                    {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type))
                    {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]
                            {
                                    split[1]
                            };
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }

                /*
                 * check whether the document is selected from google drive
                 * then get the filename and path from the uri
                 *
                 * @Params uri
                 * */
                else if (isGoogleDocsUri(uri))
                {
                    Log.e("contentUri", "getPath: " + "GOOGLE DRIVE");
                    String filename = "";
                    String filePath = "";
                    String mimeType = context.getContentResolver().getType(uri);
                    if (mimeType == null)
                    {
                        String path = getPath(context, uri);
                        if (path == null)
                        {
//                            filename = FilenameUtils.getName(uri.toString());
                            filename = "ABC";
                        }
                        else
                        {
                            File file = new File(path);
                            filename = file.getName();
                        }
                    }
                    else
                    {
                        Cursor returnCursor = context.getContentResolver().
                                query(uri, null, null, null, null);
                        int nameIndex = returnCursor.
                                getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        returnCursor.moveToFirst();
                        filename = returnCursor.getString(nameIndex);
                    }
                    String path = makeDirectory(context);
                    File fileSave = new File(path);
                    filePath = fileSave.getAbsolutePath() + "/" + filename;
                    Log.e("contentUri", "getPath:filePath " + filePath);

                    try
                    {
                        copyFileStream(new File(filePath), uri, context);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return filePath;
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();
        }
        return null;
    }
    public static final String DOCUMENTS_DIR = "documents";

    public static File getDocumentCacheDir(@NonNull Context context) {
        File dir = new File(context.getCacheDir(), DOCUMENTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
//        logDir(context.getCacheDir());
//        logDir(dir);

        return dir;
    }

    @Nullable
    public static File generateFileName(@Nullable String name, File directory) {
        if (name == null) {
            return null;
        }

        File file = new File(directory, name);

        if (file.exists()) {
            String fileName = name;
            String extension = "";
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            }

            int index = 0;

            while (file.exists()) {
                index++;
                name = fileName + '(' + index + ')' + extension;
                file = new File(directory, name);
            }
        }

        try {
            if (!file.createNewFile()) {
                return null;
            }
        } catch (IOException e) {
            //Log.w(TAG, e);
            return null;
        }

        //logDir(directory);

        return file;
    }

    private static void saveFileFromUri(Context context, Uri uri, String destinationPath) {
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            bos = new BufferedOutputStream(new FileOutputStream(destinationPath, false));
            byte[] buf = new byte[1024];
            is.read(buf);
            do {
                bos.write(buf);
            } while (is.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getFileName(@NonNull Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String filename = null;

        if (mimeType == null && context != null) {
            String path = getPath(context, uri);
            if (path == null) {
                filename = getName(uri.toString());
            } else {
                File file = new File(path);
                filename = file.getName();
            }
        } else {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        }

        return filename;
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf('/');
        return filename.substring(index + 1);
    }


    private static String makeDirectory(Context context)
    {
        File directory = null;
        String profile_image_path = "";
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        if (Environment.getExternalStorageState().equals(Environment.DIRECTORY_DOWNLOADS))
        {
//            directory = new File(Environment.getExternalStorageDirectory() + "/surverybe_drive_files");
            directory = new File(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/Cryptted");
            if (!directory.exists())
                directory.mkdirs();
        } else
        {
            directory = context.getDir("Cryptted", context.MODE_PRIVATE);
            if (!directory.exists())
                directory.mkdirs();
        }

        if (directory != null)
        {
            File profile_image = new File(directory + File.separator + "Cryptted");
            if (!profile_image.exists())
                profile_image.mkdirs();

            profile_image_path = directory + File.separator + "Cryptted";

        }
        return profile_image_path;
    }

    private static void copyFileStream(File dest, Uri uri, Context context)
            throws IOException
    {
        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, length);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isGoogleDocsUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                DatabaseUtils.dumpCursor(cursor);
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}