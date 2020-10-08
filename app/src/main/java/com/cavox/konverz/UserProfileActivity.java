package com.cavox.konverz;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.multidex.MultiDex;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.ca.Utils.CSEvents;
import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.wrapper.CSChat;
import com.cavox.utils.GlobalVariables;

import com.cavox.utils.utils;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;

import com.nostra13.universalimageloader.core.DisplayImageOptions;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.cavox.utils.GlobalVariables.LOG;

public class UserProfileActivity extends Activity {

    ImageView button_done;
    EditText user_name;
    EditText presence_text;
    ImageView userImage;
    private ImageView mAddImage;
    EditText mobilenumber;
    // The primary interface we will using for the IM service
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    Handler h = new Handler();
    int delay = 30000;
    Runnable RunnableObj;
    String temppath = "tempprofile";
    CSClient CSClientObj = new CSClient();
    String filepath = "";
    AlertDialog ad;
    static String imageFilePath = "";
    private TextView mStatusTextCountTv;
    private ImageView mEditProfileImg;
    private ImageView mSelfProfileImg;
    //private Bitmap mBitmap;
    private boolean isCameForEdit = false;
    private String username = "";
    private String presence = "";
    private String savedFilePath = "";
    private final String TAG = "UserProfileActivity";
    private boolean isTempFileCreated = false;

    String selfprofileimageid = "";

    /**
     * ImageLoader variables
     */
    DisplayImageOptions mOptions;
    //ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        userImage = findViewById(R.id.user_image);
        mSelfProfileImg = findViewById(R.id.self_profile_img);
        button_done = findViewById(R.id.button_done);
        user_name = (EditText) findViewById(R.id.user_name);
        presence_text = (EditText) findViewById(R.id.user_presenceText);
        mobilenumber = (EditText) findViewById(R.id.user_number);
        mStatusTextCountTv = findViewById(R.id.status_text_count_tv);
        mEditProfileImg = findViewById(R.id.edit_profile_img);
        mAddImage = findViewById(R.id.textView1);


        utils.setFileTrasferPathsHelper();

//new CSClient().reset();

        /*
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        }
*/


//new CSClient().deleteContact("A1contact");

        /********************start testing**************/

/*
LOG.info("test PendingUploads cnt1:"+CSDataProvider.getChatCursorForPendingUploads().getCount());
        LOG.info("test PendingDownloads cnt2:"+CSDataProvider.getChatCursorForPendingDownloads().getCount());
        LOG.info("test PendingUPnDownloads cnt3:"+CSDataProvider.getChatCursorForPendingUPnDownloads().getCount());

        LOG.info("test auto download enabled:"+new CSChat().isAutoDownloadOfFilesInChatEnabled());
        LOG.info("test pref nw:"+new CSChat().getPreferredNWToDownloadFilesInChat());



        LOG.info("test auto download enabled:"+new CSChat().enableAutoDownloadOfFilesInChat(true));
        LOG.info("test pref nw:"+new CSChat().setPreferredNWToDownloadFilesInChat(CSConstants.CHATFTDOWNLOADPREFNW.ALL));

        LOG.info("test auto download enabled:"+new CSChat().isAutoDownloadOfFilesInChatEnabled());
        LOG.info("test pref nw:"+new CSChat().getPreferredNWToDownloadFilesInChat());
*/


/*
        try {
        GlobalVariables.chatappname = MainActivity.context.getApplicationInfo().loadLabel(MainActivity.context.getPackageManager()).toString();
        String myimagedirectory = MainActivity.context.getExternalMediaDirs().toString()+ File.separator+ GlobalVariables.chatappname + "/Images";

        String filepath =myimagedirectory+"/test.txt";
            FileInputStream fis = new FileInputStream(filepath);
            int length = (int) new File(filepath).length();
            byte[] buffer = new byte[length];
            fis.read(buffer, 0, length);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        /********************end testing**************/


















        mOptions = new DisplayImageOptions.Builder()
                /*.showImageOnLoading(R.drawable.img_profile_default)
                .showImageForEmptyUri(R.drawable.img_profile_default)
                .showImageOnFail(R.drawable.img_profile_default)*/
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        try {

            isCameForEdit = getIntent().getBooleanExtra("isCameForEdit", false);
            if (isCameForEdit) {
                button_done.setVisibility(View.GONE);
                presence_text.setEnabled(false);
                user_name.setEnabled(false);
                mAddImage.setVisibility(View.GONE);
                mEditProfileImg.setVisibility(View.VISIBLE);
            } else {
                button_done.setVisibility(View.VISIBLE);
                presence_text.setEnabled(true);
                user_name.setEnabled(true);
                mAddImage.setVisibility(View.VISIBLE);
                mEditProfileImg.setVisibility(View.GONE);
            }

            GlobalVariables.phoneNumber = CSDataProvider.getLoginID();

            mobilenumber.setText(GlobalVariables.phoneNumber);
            mobilenumber.setEnabled(false);


            Cursor cur = CSDataProvider.getSelfProfileCursor();
            if (cur.getCount() > 0) {
               setProfile();
            } else {
               showprogressbar();
               new CSClient().getSelfProfile();
            }
            cur.close();



            mEditProfileImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    button_done.setVisibility(View.VISIBLE);
                    presence_text.setEnabled(true);
                    user_name.setEnabled(true);
                    mAddImage.setVisibility(View.VISIBLE);
                    mEditProfileImg.setVisibility(View.GONE);
                }
            });
            presence_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mStatusTextCountTv.setText("" + (150 - presence_text.getText().toString().length()));
                    if (mStatusTextCountTv.getText().toString().trim().equals("0")) {
                        mStatusTextCountTv.setVisibility(View.GONE);
                    } else {
                        mStatusTextCountTv.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mSelfProfileImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userImage.getVisibility() == View.GONE) {
                        try {
                            Dialog dialogView = new Dialog(UserProfileActivity.this);
                            dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogView.setCancelable(true);
                            dialogView.setContentView(R.layout.image_dialog);
                            dialogView.getWindow().setBackgroundDrawable(
                                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialogView.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                                    ViewGroup.LayoutParams.FILL_PARENT);
                            ImageView imageView = dialogView.findViewById(R.id.dialog_image_view);
                            //imageView.setImageBitmap(mBitmap);
String myfilepath = "";
                            Cursor cur = CSDataProvider.getSelfProfileCursor();
                            if (cur.getCount() > 0) {
                                cur.moveToNext();
                                String imageid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_PROFILEPICID));
                                myfilepath = CSDataProvider.getImageFilePath(imageid);
                            }
                            cur.close();
                            if(new File(myfilepath).exists()) {

                                 Glide.with(UserProfileActivity.this)
                                        .load(Uri.fromFile(new File(myfilepath)))
                                        //.apply(new RequestOptions().error(R.drawable.defaultgroup).override(200, 200).apply(RequestOptions.circleCropTransform()))
                                        //.apply(RequestOptions.circleCropTransform())
                                        .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(myfilepath).length() + new File(myfilepath).lastModified()))))
                                        .into(imageView);
                            }
                            if (dialogView != null) {
                                dialogView.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
            });

            button_done.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (isCameForEdit) {
                            button_done.setVisibility(View.GONE);
                            presence_text.setEnabled(false);
                            user_name.setEnabled(false);
                            mAddImage.setVisibility(View.GONE);
                            mEditProfileImg.setVisibility(View.VISIBLE);
                        }
                        if (!utils.isinternetavailable(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (user_name.getText().toString().trim().length() == 0) {
                            user_name.setError("Please enter profile name");
                            return;
                        }


                        String name = user_name.getText().toString();
                        String prsence = presence_text.getText().toString();

                        Log.i(TAG, "onClick: present value " + filepath + " " + name + " " + prsence);
                        Log.i(TAG, "onClick: previous value " + savedFilePath + " " + username + " " + presence);
                        if (isCameForEdit && savedFilePath.equals(filepath) && name.equals(username) && prsence.equals(presence)) {
                            Log.i(TAG, "onClick: returning from same values ");
                            return;
                        }

                        Log.i(TAG, "onClick: saving profile ");
                        showprogressbar();
                        CSClientObj.setProfile(name, prsence, filepath);
                        username = name;
                        presence = prsence;

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

	/*userImage.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			i.setType("image/*");
			startActivityForResult(i, 999);
		}
	});

*/

            mAddImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //popupusertoselectimagesource();
                    showoptions();
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Bitmap getImageBitmap(String imageid, int reqWidth, int reqHeight) {
        try {
            boolean isthumbfile = true;
            Cursor cur = CSDataProvider.getImagesCursorByFilter(CSDbFields.KEY_IMAGEID, imageid);
            //LOG.info("profile test: count:"+cur.getCount());

            if (cur.getCount() > 0) {
                cur.moveToNext();

                int savedinsdcard = cur.getInt(cur.getColumnIndexOrThrow(CSDbFields.KEY_IMAGESAVEDINSDCARD));
                //LOG.info("profile test: savedinsdcard:"+savedinsdcard);

                if (savedinsdcard == 1) {
                    String filepath = "";
                    //String filepath = cur.getString(cur.getColumnIndexOrThrow(IAmLiveDataProvider.KEY_imagefilepath));

                    filepath = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_IMAGEFILETHUMBNAILPATH));
                    //LOG.info("profile test: thumb filepath:"+filepath);


                    if (filepath == null || filepath.equals("")) {
                        isthumbfile = false;
                        filepath = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_IMAGEFILEPATH));
                        //LOG.info("profile test: filepath:"+filepath);
                    }

                    //isthumbfile = false;//temp remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/1mb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/2mb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/5mb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/10mb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/15mb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/20mb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/30mb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/50kb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/100kb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/200kb.jpg";//temp remove remove after test
                    //filepath = "/storage/emulated/0/Konverz/testimages/500kb.jpg";//temp remove remove after test

                    if (filepath != null && !filepath.equals("")) {
                        Bitmap bm = null;
                        if (isthumbfile) {
                            bm = BitmapFactory.decodeFile(filepath);
                        } else {
                            getscaleddownBitmap(filepath, reqWidth, reqHeight);
                        }
                        cur.close();
                        return bm;
                    } else {
                        return null;
                    }
                } else {
                    byte[] image = cur.getBlob(cur.getColumnIndexOrThrow(CSDbFields.KEY_IMAGEDATA));
                    cur.close();
                    return BitmapFactory.decodeByteArray(image, 0, image.length);
                }

            } else {
                cur.close();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Bitmap getscaleddownBitmap(String filename, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        try {

            int SampleSize = 2;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, options);
            SampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            //LOG.info("bitmap optimization: image size:"+new File(filename).length());
            //LOG.info("bitmap optimization: image height:"+options.outHeight);
            //LOG.info("bitmap optimization: image width:"+options.outWidth);
            //LOG.info("bitmap optimization: calculated sample size:"+SampleSize);

            options.inSampleSize = SampleSize;
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeFile(filename, options);
            //LOG.info("bitmap optimization: bitmap size:" + bitmap.getByteCount());
            return bitmap;
        } catch (Exception ex) {
            return bitmap;
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {


            if (requestCode == 221) {

		/*
		Bitmap photo = (Bitmap) data.getExtras().get("data");
		Uri tempUri = getImageUri(getApplicationContext(), photo);
		//LOG.info("tempUri:" + tempUri);
		filepath = utils.getRealPathFromURI(getApplicationContext(),tempUri);
*/

                filepath = new String(imageFilePath);


                //String tempfilepath =

                imageFilePath = "";
                int orientation = checkOrientation(filepath);
                LOG.info("checkOrientation:" + orientation);
                if (orientation != 0) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                        bitmap = rotateImage(bitmap, orientation);
                        if (new File(filepath).exists()) {
                            new File(filepath).delete();
                        }
                        OutputStream os1 = new BufferedOutputStream(new FileOutputStream(filepath));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os1);
                        os1.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }


            } else if (requestCode == 999) {
                if (data != null) {
                    Uri selectedImageURI = data.getData();
                    filepath = utils.getRealPathFromURI(getApplicationContext(), selectedImageURI);
                    int orientation = checkOrientation(filepath);
                    LOG.info("checkOrientation:" + orientation);
                    if (orientation != 0) {
                        try {
                            isTempFileCreated = true;
                            File tempFilePath = createImageFile();
                            copyFile(new File(filepath), tempFilePath);
                            filepath = tempFilePath.toString();
                            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                            bitmap = rotateImage(bitmap, orientation);
                            if (new File(filepath).exists()) {
                                new File(filepath).delete();
                            }
                            OutputStream os1 = new BufferedOutputStream(new FileOutputStream(filepath));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os1);
                            os1.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    LOG.info("data is null");

                }
            }
            LOG.info("File path:" + filepath);
            LOG.info("orifinal File length:" + new File(filepath).length());
            if (filepath.equals("")) {
                Toast.makeText(UserProfileActivity.this, "No Image Set", Toast.LENGTH_SHORT).show();
            } else {
                if (new File(filepath).length() > 10000000) {
                    filepath = "";
                    Toast.makeText(UserProfileActivity.this, "File Size limit excedded", Toast.LENGTH_SHORT).show();
                } else {
                    loadImage(filepath);
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hideKeyboard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception ex) {
        }

    }

    public void updateUI(String str) {

        try {
            if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                LOG.info("NetworkError receieved");

                //Toast.makeText(this, "NetworkError", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver UserProfileActivity");
                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    //updateUI(CSEvents.CSCLIENT_NETWORKERROR);
                    dismissprogressbar();
                }  else if (intent.getAction().equals(CSEvents.CSCLIENT_GETSELFPROFILE_RESPONSE)) {
                   selfprofileimageid = intent.getStringExtra("sProfilePicId");
                    LOG.info("selfprofileimageid:"+selfprofileimageid);
                   if(selfprofileimageid.equals("")) {
                       dismissprogressbar();
                       setProfile();
                   }
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_IMAGESDBUPDATED)) {

                    if(!selfprofileimageid.equals("") && intent.getStringExtra("imageid").equals(selfprofileimageid)) {
                       dismissprogressbar();
                       setProfile();
                    }

                }

                else if (intent.getAction().equals(CSEvents.CSPROFILE_DOWNLOADSUCCESS)) {


                }
                else if (intent.getAction().equals(CSEvents.CSPROFILE_DOWNLOADPROGRESS)) {
                    if(!selfprofileimageid.equals("") && intent.getStringExtra("imageid").equals(selfprofileimageid)) {
                        progressBar.setProgress(intent.getIntExtra("transferpercentage",0));
                    }

                }
                else if (intent.getAction().equals(CSEvents.CSPROFILE_DOWNLOADFAILED)) {
                    if(!selfprofileimageid.equals("") && intent.getStringExtra("imageid").equals(selfprofileimageid)) {
                        dismissprogressbar();
                        Toast.makeText(UserProfileActivity.this, "Profile download failed", Toast.LENGTH_SHORT).show();
                    }
                }






                else if (intent.getAction().equals(CSEvents.CSPROFILE_UPLOADPROGRESS)) {

if(progressBar != null && intent.getStringExtra("filepath").equals(filepath)) {
    progressBar.setProgress(intent.getIntExtra("transferpercentage",0));
}

                } else if (intent.getAction().equals(CSEvents.CSPROFILE_UPLOADFILEFAILED)) {
                    dismissprogressbar();
                    Toast.makeText(UserProfileActivity.this, "Profile updated failed", Toast.LENGTH_SHORT).show();


                } else if (intent.getAction().equals(CSEvents.CSPROFILE_UPLOADSUCCESS)) {

                } else if (intent.getAction().equals(CSEvents.CSCLIENT_SETPROFILE_RESPONSE)) {
                    Log.i(TAG, "onReceive: saving profile completed");
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        LOG.info("setProfileRessuccess");
                        savedFilePath = filepath;

                        if (isTempFileCreated && new File(filepath).exists()) {
                            new File(filepath).delete();
                            isTempFileCreated = false;

                        }
                        if (new File(temppath).exists()) {
                            new File(temppath).delete();
                        }
                        dismissprogressbar();
                        Toast.makeText(UserProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        if (!isCameForEdit) {
                            onBackPressed();
                        }
                    } else {
                        LOG.info("setProfileResfailure");
                        if (new File(temppath).exists()) {
                            new File(temppath).delete();
                        }
                        Toast.makeText(UserProfileActivity.this, "Profile updated failed", Toast.LENGTH_SHORT).show();
                        dismissprogressbar();
                    }

                }


            } catch (Exception ex) {
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
            IntentFilter filter1 = new IntentFilter(CSEvents.CSCLIENT_SETPROFILE_RESPONSE);
            IntentFilter filter2 = new IntentFilter(CSEvents.CSPROFILE_UPLOADPROGRESS);
            IntentFilter filter3 = new IntentFilter(CSEvents.CSPROFILE_UPLOADFILEFAILED);
            IntentFilter filter4 = new IntentFilter(CSEvents.CSPROFILE_UPLOADSUCCESS);

            IntentFilter filter5 = new IntentFilter(CSEvents.CSCLIENT_GETSELFPROFILE_RESPONSE);
            IntentFilter filter6 = new IntentFilter(CSEvents.CSCLIENT_IMAGESDBUPDATED);
            filter6.addAction(CSEvents.CSPROFILE_DOWNLOADSUCCESS);
            filter6.addAction(CSEvents.CSPROFILE_DOWNLOADPROGRESS);
            filter6.addAction(CSEvents.CSPROFILE_DOWNLOADFAILED);






            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter1);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter2);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter3);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter4);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter5);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter6);

/*
			CSClientObj.startFileLogging(Environment.getExternalStorageDirectory()+"/connecttext.log", CSConstants.LOG_LEVEL_DEBUG,true);
			CSClientObj.startwritingLogcatToFile(Environment.getExternalStorageDirectory()+"/tempconnecttext.log",true);
LOG.info("LOG TEST");

			try {
				int x = 1/0;
			} catch (Exception ex) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				String sStackTrace = sw.toString();
				LOG.warn(sStackTrace);
			}

Cursor cur = CSDataProvider.getGroupsCursor();;
			while(cur.moveToNext()) {
				System.out.println("TEST IS GROUP NAME:" + cur.getString(cur.getColumnIndexOrThrow("group_name")));
				System.out.println("TEST IS GROUP ACTIVE:" + cur.getInt(cur.getColumnIndexOrThrow("group_is_active")));
			}
			cur.close();
*/


        } catch (Exception ex) {
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        try {
			 /*
			 CSClientObj.stopFileLogging();
			 CSClientObj.stopwritingLogcatToFile();
*/
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
        } catch (Exception ex) {
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismissprogressbar();
        Intent intent = new Intent();
        setResult(933, intent);
        finish();
        return;
    }

    /*
@Override
protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
}
*/
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


    public void showoptions() {
        final Dialog dialog = new Dialog(UserProfileActivity.this);
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

        if (!isProFilePicAvailable()) {
            removeProfilePicImg.setVisibility(View.GONE);
        }

        removeProfilePicImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                filepath = "";
                userImage.setVisibility(View.VISIBLE);
                mSelfProfileImg.setVisibility(View.GONE);
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
                            ActivityCompat.requestPermissions(UserProfileActivity.this, requestpermissions.toArray(new String[requestpermissions.size()]), 101);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", createImageFile()));
                    } else {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(intent, 221);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showprogressbar() {
        try {
            if (getApplicationContext() != null) {
                progressBarStatus = 0;
                progressBar = new ProgressDialog(UserProfileActivity.this);
                progressBar.setCancelable(false);
                progressBar.setMessage("Please Wait while updating profile..");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();
/*
                h = new Handler();
                RunnableObj = new Runnable() {

                    public void run() {
                        h.postDelayed(this, delay);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                dismissprogressbar();
                                //Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();


                            }
                        });
                    }
                };
                h.postDelayed(RunnableObj, delay);
*/

            }
        } catch (Exception ex) {

        }
    }

    public void dismissprogressbar() {
        try {
            LOG.info("dismissprogressbar4");
            if (progressBar != null) {
                progressBar.dismiss();

            }
            if (h != null) {
                h.removeCallbacks(RunnableObj);
            }
        } catch (Exception ex) {
        }
    }

    private File createImageFile() {
        //LOG.info("test place2");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        LOG.info("storageDir:"+GlobalVariables.profilesdirectory);
        File storageDir = new File(GlobalVariables.profilesdirectory);

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

    private boolean isProFilePicAvailable() {
        try {

            Cursor cur = CSDataProvider.getSelfProfileCursor();
            if (cur.getCount() > 0) {
                cur.moveToNext();
                String imageid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_PROFILEPICID));
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

    public int checkOrientation(String imagePath) {
        int orientation = 0;
        try {

            ExifInterface exif = new ExifInterface(imagePath);

            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    orientation = 0;

                    break;
                default:
                    LOG.info("orientation is invalid");
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return orientation;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * copies content from source file to destination file
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    private void copyFile(File sourceFile, File destFile) {
        try {

            if (!sourceFile.exists()) {
                return;
            }
            FileChannel source = null;
            FileChannel destination = null;
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void loadImage(String imagefilepath) {
        if(new File(imagefilepath).exists()) {
            mSelfProfileImg.setVisibility(View.VISIBLE);
            userImage.setVisibility(View.GONE);
            Glide.with(UserProfileActivity.this)
                    .load(Uri.fromFile(new File(imagefilepath)))
                    //.apply(new RequestOptions().error(R.drawable.defaultgroup).override(200, 200).apply(RequestOptions.circleCropTransform()))
                    //.apply(RequestOptions.circleCropTransform())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(imagefilepath).length() + new File(imagefilepath).lastModified()))))
                    .into(mSelfProfileImg);
        } else {
            userImage.setVisibility(View.VISIBLE);
            mSelfProfileImg.setVisibility(View.GONE);
        }
    }


    private void setProfile() {
        try {
            Cursor cur = CSDataProvider.getSelfProfileCursor();
            if (cur.getCount() > 0) {
                cur.moveToNext();
                String imageid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_PROFILEPICID));
                username = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_USERNAME));
                presence = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_DESCRIPTION));
                filepath = CSDataProvider.getImageFilePath(imageid);
                savedFilePath = filepath;
                user_name.setText(username);
                presence_text.setText(presence);
                mStatusTextCountTv.setText("" + (150 - presence.length()));
                LOG.info("setProfile filepath:"+filepath);
                    loadImage(filepath);
            }
            cur.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
