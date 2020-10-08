package com.cavox.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.app.deltacubes.R;
import com.github.lzyzsd.circleprogress.Utils;

import java.io.File;

public class ChatMethodHelper {

    private static Dialog mPopupCloseDialog;
    /**
     * Getting image path from URI.
     *
     * @param uri
     * @return path
     */
    public static String getImagePath(Context context, Uri uri) {
        /*String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);*/

        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;

    }

    /**
     * This method checks whether given file exist or not in provided path. If
     * not exist, it will create file and directories even.
     *
     * @param path
     * @param fileName
     * @return file
     */
    public static File createFile(String path, String fileName) {
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        File file = new File(path, fileName);
        return file;
    }
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
   /* @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
try {
    final boolean isaboveorKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    // DocumentProvider
    if (isaboveorKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/"
                        + split[1];
            }

        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {
            LOG.info("isDownloadsDocument");
            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
            LOG.info("isMediaDocument");
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                LOG.info("isMediaDocument type audio");
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                LOG.info("isMediaDocument type audio:"+contentUri);
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{split[1]};

            return getDataColumn(context, contentUri, selection,
                    selectionArgs);
        }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
        return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
        return uri.getPath();
    }
} catch (Exception ex) {
    ex.printStackTrace();
}
        return null;
    }
*/
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    /**
     * Depends on mime type it will redirect to that particular player.
     *
     * @param path sdcard path
     */
    public static void openPlayer(Context context, String path) {
        try {

            Log.i("ChatMethodHelper", "File Path: " + path);
            File file = new File(path);

            if (file.exists()) {
                Intent myIntent = new Intent(
                        Intent.ACTION_VIEW);
                String extension = MimeTypeMap.getFileExtensionFromUrl(Uri
                        .fromFile(file).toString());
                String mimetype = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(extension);
                myIntent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file), mimetype);
                myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(myIntent);

            } else {
                Toast.makeText(context,
                        context.getResources().getString(R.string.media_not_available),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getResources().getString(R.string.media_player_nt_available),
                    Toast.LENGTH_SHORT).show();
        }
    }
    public static void showLockScreenNotificationsMIUI(Context context) {
        try {
            PreferenceProvider mPreferenceProvider = new PreferenceProvider(context);
            boolean isAlreadyGiven = mPreferenceProvider.getPrefBoolean(PreferenceProvider.DONT_SHOW_LOCK_SCREEN_NOTIFICATION_MIUI);
            if ("xiaomi".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !isAlreadyGiven) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", context.getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", context.getPackageName());
                    intent.putExtra("app_uid", context.getApplicationInfo().uid);
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                } else {
                    return;
                }
                invokePermissionDialog("Enable Lock screen notifications to get the notifications while phone locked", intent, context, mPreferenceProvider);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void invokePermissionDialog(final String message, final Intent intent,Context context,PreferenceProvider mPreferenceProvider) {

        if (mPopupCloseDialog == null) {
            try {
                mPopupCloseDialog = new Dialog(context);

                mPopupCloseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mPopupCloseDialog.setContentView(R.layout.alertdialog_close);
                mPopupCloseDialog.setCancelable(false);
                mPopupCloseDialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));


                Button yes = (Button) mPopupCloseDialog
                        .findViewById(R.id.btn_alert_ok);
                Button no = (Button) mPopupCloseDialog
                        .findViewById(R.id.btn_alert_cancel);


                TextView tv_title = (TextView) mPopupCloseDialog
                        .findViewById(R.id.tv_alert_title);
                TextView tv_message = (TextView) mPopupCloseDialog
                        .findViewById(R.id.tv_alert_message);
                tv_message.setText(message);


             /*   Typeface text_bold = Utils.getTypefaceBold(context);
                Typeface text_regular = Utils.getTypefaceRegular(context);
                tv_title.setTypeface(text_bold);
                tv_message.setTypeface(text_regular);

                yes.setTypeface(text_regular);
                no.setTypeface(text_regular);*/


                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mPreferenceProvider.setPrefboolean(PreferenceProvider.DONT_SHOW_LOCK_SCREEN_NOTIFICATION_MIUI, true);

                        mPopupCloseDialog.dismiss();
                        mPopupCloseDialog = null;


                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mPopupCloseDialog.dismiss();
                        mPopupCloseDialog = null;

                    }
                });

                if (mPopupCloseDialog != null)
                    mPopupCloseDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
