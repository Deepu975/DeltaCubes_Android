package com.cavox.konverz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;
import com.ca.Utils.CSConstants;

import com.cavox.utils.GlobalVariables;
import com.cavox.utils.utils;
import com.cavox.views.RoundedImageView;
import com.ca.wrapper.CSDataProvider;
import com.ca.wrapper.CSGroups;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import static com.cavox.utils.GlobalVariables.LOG;

//import com.cavox.uiutils.UIActions;


public class EditGroupInfoActivity extends Activity {
    CSGroups CSGroupsObj = new CSGroups();
    private Button button_nex;
    private EditText group_name;
    private EditText status_text;
    private RoundedImageView groupImage;
    String filepath = "";
    private String mGroupId;
    String imageid = "";
    AlertDialog ad;
    private ProgressDialog progressBar;
    private String savedFilePath = "";
    private String username = "";
    private String presence = "";
    private String imageFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group);
        try {
            groupImage = (RoundedImageView) findViewById(R.id.user_image);
            button_nex = (Button) findViewById(R.id.button_done);
            group_name = (EditText) findViewById(R.id.user_name);
            status_text = (EditText) findViewById(R.id.user_presenceText);
            button_nex.setText("Done");
            mGroupId = getIntent().getStringExtra("grpid");
utils.setFileTrasferPathsHelper();
            Cursor cur = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, mGroupId);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                imageid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_PROFILE_PIC));
                username = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_NAME));
                presence = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_DESC));
                filepath = CSDataProvider.getImageFilePath(imageid);
                savedFilePath = filepath;
                group_name.setText(username);
                status_text.setText(presence);
                Bitmap mybitmap = CSDataProvider.getImageBitmap(imageid);
                if (mybitmap != null) {
                    groupImage.setImageBitmap(mybitmap);
                }
            }
            cur.close();

            button_nex.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        //CSGroupsObj.deleteGroupPhoto(mGroupId);

                    } catch (Exception ex) {
                        utils.logStacktrace(ex);
                    }

                    return true;
                }
            });

            button_nex.setOnClickListener(new OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    if (!utils.isinternetavailable(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (group_name.getText().toString().isEmpty()) {
                        group_name.setError(getResources().getString(R.string.error_empty_group));
                        return;
                    }
                    String name = group_name.getText().toString();
                    String prsence = status_text.getText().toString();

                    //Log.i(TAG, "onClick: present value " + filepath + " " + name + " " + prsence);
                    //Log.i(TAG, "onClick: previous value " + savedFilePath + " " + username + " " + presence);
                    if (savedFilePath.equals(filepath) && name.equals(username) && prsence.equals(presence)) {
                        //Log.i(TAG, "onClick: returning from same values ");
                        onBackPressed();
                        return;
                    }
                    button_nex.setEnabled(false);
                    showprogressbar();
                    CSGroupsObj.updateGroupInfo(name, prsence, filepath, mGroupId);

                }
            });
/*
		groupImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				i.setType("image/*");
				startActivityForResult(i, 999);
			}
		});
			*/
            groupImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //popupusertoselectimagesource();
                    showoptions();
                }
            });
        } catch (Exception ex) {
        }
    }


    public void showoptions() {
        final Dialog dialog = new Dialog(EditGroupInfoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_update_dialog);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout galleryImage = dialog.findViewById(R.id.galley_img);
        LinearLayout cameraImg = dialog.findViewById(R.id.camera_img);
        LinearLayout removeProfilePicImg = dialog.findViewById(R.id.remove_profile_img);
        TextView cancelTv = dialog.findViewById(R.id.dialog_cancel_tv);

        if (!isGroupProFilePicAvailable()) {
            removeProfilePicImg.setVisibility(View.GONE);
        }
        removeProfilePicImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                filepath = "";
                showprogressbar();
                CSGroupsObj.deleteGroupPhoto(mGroupId);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        cameraImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", createImageFile()));
                    } else {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(intent, 221);
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        ArrayList<String> allpermissions = new ArrayList<String>();
                        allpermissions.add(android.Manifest.permission.CAMERA);
                        allpermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        ArrayList<String> requestpermissions = new ArrayList<String>();

                        for (String permission : allpermissions) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                                requestpermissions.add(permission);
                            }
                        }
                        if (requestpermissions.size() > 0) {
                            ActivityCompat.requestPermissions(EditGroupInfoActivity.this, requestpermissions.toArray(new String[requestpermissions.size()]), 101);
                        }
                    }

                }
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });
        galleryImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, 999);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        cancelTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        if (dialog != null)
            dialog.show();
    }

    private File createImageFile() {
        //LOG.info("test place2");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(utils.getSentImagesDirectory());

        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        //LOG.info("test place3");
        try {
            imageFilePath = image.getAbsolutePath();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //LOG.info("test place4");
        if (image == null) {
            LOG.info("image file is null");
        } else {
            LOG.info("image file is  not null path is:" + imageFilePath);
        }
        return image;
    }

    public boolean popupusertoselectimagesource() {
        try {

            final AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(EditGroupInfoActivity.this);
            final Button Camera = new Button(EditGroupInfoActivity.this);
            final Button Gallery = new Button(EditGroupInfoActivity.this);
            //Camera.setBackgroundColor(MainActivity.context.getResources().getColor(R.color.colorPrimary));
            //Camera.setTextColor(MainActivity.context.getResources().getColor(R.color.color_white));
            Camera.setText("Camera");
            //Gallery.setBackgroundColor(MainActivity.context.getResources().getColor(R.color.colorPrimary));
            //Gallery.setTextColor(MainActivity.context.getResources().getColor(R.color.color_white));
            Gallery.setText("Gallery");

            LinearLayout layout = new LinearLayout(EditGroupInfoActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            //final EditText titleBox = new EditText(UserProfileActivity.this);

            layout.addView(Camera);
            layout.addView(Gallery);

            successfullyLogin.setView(layout);


            successfullyLogin.setTitle("Pls select");


            Camera.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                    startActivityForResult(intent, 221);
                    if (ad != null) {
                        ad.cancel();
                    }
                }
            });

            Gallery.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startActivityForResult(i, 999);
                    if (ad != null) {
                        ad.cancel();
                    }
                }
            });


            successfullyLogin.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {


                        }
                    });
            ad = successfullyLogin.show();

            return true;
        } catch (Exception ex) {
            return false;
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 999 || requestCode == 221) {

                    if (requestCode == 221) {
                        filepath = new String(imageFilePath);
                        imageFilePath = "";
                    } else {
                        Uri selectedImageURI = data.getData();
                        filepath = utils.getRealPathFromURI(getApplicationContext(), selectedImageURI);
                    }
                    LOG.info("File path:" + filepath);
                    LOG.info("orifinal File length:" + new File(filepath).length());
                    if (filepath.equals("")) {
                        Toast.makeText(EditGroupInfoActivity.this, "No Image Set", Toast.LENGTH_SHORT).show();
                    } else {
                        if (new File(filepath).length() > 10000000) {
                            filepath = "";
                            Toast.makeText(EditGroupInfoActivity.this, "File Size limit excedded", Toast.LENGTH_SHORT).show();
                        } else {

                            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                            try {

                                bitmap = modifyOrientation(bitmap, filepath);
                                bitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1280, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                groupImage.setImageBitmap(bitmap);
                            }
                        }
                    }
                }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * This is used for change orientation of image
     *
     * @param bitmap              change orientation
     * @param image_absolute_path path
     * @return which returns bitmap image
     * @throws IOException exception
     */
    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws
            IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    /**
     * This is used for rotate image whatever you want
     *
     * @param bitmap  this is used for rotate image
     * @param degrees this is used for rotate image
     * @return which returns bitmap iamge
     */
    static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * This is used for flip image
     *
     * @param bitmap     flip image
     * @param horizontal this is used for flip image
     * @param vertical   this is used for flip image
     * @return which returns bitmap
     */
    static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /*
    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        try { 	Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            } }catch (Exception ex) {

        }
        return result;
    }
    */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    public void updateDB() {

        try {
            //CSDataProvider.updateGroupInfo(mGroupId, group_name.getText().toString(), status_text.getText().toString());
            CSDataProvider.updateGroupByFilter(mGroupId, CSDbFields.KEY_GROUP_NAME, group_name.getText().toString());
            CSDataProvider.updateGroupByFilter(mGroupId, CSDbFields.KEY_GROUP_DESC, status_text.getText().toString());
            if (!imageid.equals("")) {
                CSDataProvider.updateGroupByFilter(mGroupId, CSDbFields.KEY_GROUP_PROFILE_PIC, imageid);
            }

        } catch (Exception ex) {
        }
    }

    public void updateUI(String str) {

        try {
            if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                LOG.info("NetworkError receieved");
                dismissprogressbar();
                button_nex.setEnabled(true);
                //Toast.makeText(this, "NetworkError", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        //Intent intent1 = new Intent(UIActions.EDITGRPDONE.getKey());
        //LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent1);

        finish();
        return;
    }

    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver EditGroupInfoActivity");
                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    updateUI(CSEvents.CSCLIENT_NETWORKERROR);
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_UPDATEGROUPINFO_RESPONSE)) {
                    dismissprogressbar();
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        Toast.makeText(getApplicationContext(), "Details updated", Toast.LENGTH_SHORT).show();
                    } else {
                        LOG.info("updategrpinfofailure");
                        button_nex.setEnabled(true);
                        Toast.makeText(EditGroupInfoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        LOG.info("pullGroupDetailsRessuccess");
                        onBackPressed();
                    } else {
                        LOG.info("pullGroupDetailsResfailure");
                        button_nex.setEnabled(true);
                        Toast.makeText(EditGroupInfoActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    }

                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    MainActivityReceiver MainActivityReceiverObj = new MainActivityReceiver();

    @Override
    public void onResume() {
        super.onResume();

        try {

            MainActivityReceiverObj = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            IntentFilter filter1 = new IntentFilter(CSEvents.CSGROUPS_UPDATEGROUPINFO_RESPONSE);
            //IntentFilter filter2 = new IntentFilter(CSEvents.updategrpinfofailure);
            IntentFilter filter3 = new IntentFilter(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE);
            //IntentFilter filter4 = new IntentFilter(CSEvents.pullGroupDetailsResfailure);


            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter1);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter2);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter3);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter4);

        } catch (Exception ex) {
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            dismissprogressbar();
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public void showprogressbar() {
        try {
            if (getApplicationContext() != null) {
                progressBar = new ProgressDialog(EditGroupInfoActivity.this);
                progressBar.setCancelable(false);
                progressBar.setMessage("Please Wait..");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                //progressBar.setMax(time);
                progressBar.show();

               /* h = new Handler();
                RunnableObj = new Runnable() {

                    public void run() {
                        h.postDelayed(this, delay);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                dismissprogressbar();
                                Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();


                            }
                        });
                    }
                };
                h.postDelayed(RunnableObj, delay);*/


            }
        } catch (Exception ex) {

        }
    }

    public void dismissprogressbar() {
        try {
            LOG.info("dismissprogressbar3");
            if (progressBar != null) {
                progressBar.dismiss();

            }
           /* if (h != null) {
                h.removeCallbacks(RunnableObj);
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private boolean isGroupProFilePicAvailable() {
        try {

            Cursor cur = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, mGroupId);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                imageid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_PROFILE_PIC));
                if (imageid != null && imageid.length() > 0) {
                    if(new File(GlobalVariables.profilesdirectory+File.separator+imageid).exists()) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            cur.close();


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

}
