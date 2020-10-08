package com.cavox.konverz;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import com.app.deltacubes.R;
import com.google.android.material.appbar.AppBarLayout;
import androidx.multidex.MultiDex;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;
import com.cavox.utils.utils;
import com.ca.wrapper.CSChat;
import com.ca.wrapper.CSDataProvider;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.cavox.utils.GlobalVariables.LOG;

public class ManageUserActivity extends AppCompatActivity {
    String managecontactnumber = "";
    NotificationManager notificationManager;
    private ImageView mContactProfileImage;
    int notificationid = 0;
    Toolbar toolbar;
    //FloatingActionButton fab;
    CSChat CSChatObj = new CSChat();
    TextView subtitle;
    private RelativeLayout mProfileLayout;
    private Bitmap mBitmap;
    private String contactName = "";
    /**
     * ImageLoader variables
     */
    DisplayImageOptions mOptions;
    ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcall_scrolling);
        try {
            LOG.info("onNewIntent onCreate manage user");
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            //fab = (FloatingActionButton) findViewById(R.id.fab);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            RelativeLayout doaudiocall = (RelativeLayout) findViewById(R.id.aclrl);
            RelativeLayout dovideocall = (RelativeLayout) findViewById(R.id.vclrl);
            RelativeLayout dochat = (RelativeLayout) findViewById(R.id.chrl);
            ImageView profileimage = (ImageView) findViewById(R.id.grpimg);
            //scrollView = (NestedScrollView) findViewById(R.id.scrollView);
            RelativeLayout uploadfile = (RelativeLayout) findViewById(R.id.chrl1);
            RelativeLayout downloadfile = (RelativeLayout) findViewById(R.id.chrl2);
            mContactProfileImage = findViewById(R.id.profile_image);
            mProfileLayout = findViewById(R.id.profile_layout);
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));

            mOptions = new DisplayImageOptions.Builder()
                    /*.showImageOnLoading(R.drawable.img_profile_default)
                    .showImageForEmptyUri(R.drawable.img_profile_default)
                    .showImageOnFail(R.drawable.img_profile_default)*/
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            TextView username = (TextView) findViewById(R.id.acltxtv1);
            TextView userdescription = (TextView) findViewById(R.id.acltxtv2);

            username.setText(getIntent().getStringExtra("number"));
            userdescription.setText(getIntent().getStringExtra("description"));

            subtitle = (TextView) findViewById(R.id.subtitle);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            managecontactnumber = getIntent().getStringExtra("number");
            contactName = getIntent().getStringExtra("name");
            LOG.info("Manage number managecontactnumber:" + managecontactnumber);
            if (!contactName.equals("")) {
                getSupportActionBar().setTitle(contactName);
            } else {
                contactName = managecontactnumber;
                getSupportActionBar().setTitle(managecontactnumber);
            }
            //subtitle.setText(managecontactnumber.toString().trim());
            String nativecontactid = "";
            Cursor cur = CSDataProvider.getContactCursorByNumber(managecontactnumber);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                nativecontactid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID));
            }
            cur.close();
            String picid = "";
            Cursor cur1 = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, managecontactnumber);
            //LOG.info("Yes count:"+cur1.getCount());
            if (cur1.getCount() > 0) {
                cur1.moveToNext();
                picid = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
            }
            cur1.close();

            Log.i("ManageUserActivity", "Profile Pic ID " + picid + " natve id " + nativecontactid);
            String filepath = CSDataProvider.getImageFilePath(picid);
            String finalPicid = picid;
            String finalNativecontactid = nativecontactid;
            Log.i("ManageUserActivity", "Profile Pic ID " + finalPicid + " natve id " + finalNativecontactid + " file path " + filepath);

            mImageLoader.clearDiskCache();
            mImageLoader.clearMemoryCache();
            mImageLoader.loadImage("file://" + filepath, mOptions, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    // cache is now warmed up
                    if (loadedImage != null) {
                        mContactProfileImage.setVisibility(View.VISIBLE);
                        mContactProfileImage.setImageBitmap(loadedImage);
                        mBitmap = loadedImage;
                        profileimage.setVisibility(View.GONE);
                    } else {
                        new ImageDownloaderTask(profileimage).execute("app", finalPicid, finalNativecontactid);
                    }

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    new ImageDownloaderTask(profileimage).execute("app", finalPicid, finalNativecontactid);
                }
            });
            Cursor imageCursor = CSDataProvider.getImagesCursorByFilter(CSDbFields.KEY_IMAGEID, picid);

           /* if (imageCursor.getCount() > 0) {
                imageCursor.moveToNext();
                String filepath = imageCursor.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_IMAGEFILEPATH));
                String finalPicid = picid;
                String finalNativecontactid = nativecontactid;

            }
*/

            //fab.setVisibility(View.GONE);

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (verticalOffset == 0) {
                        //LOG.info("Offset 0");
                        subtitle.setPadding(72, 0, 0, 0);
                    } else {
                        subtitle.setPadding(112, 0, 0, 0);
                        //LOG.info("Offset 1");
                    }
                }
            });


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();

                }
            });

            mProfileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mContactProfileImage.getVisibility() == View.VISIBLE) {
                        try {
                           /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();*/
                            Intent intent = new Intent(ManageUserActivity.this, ShowImageActivity.class);
                            intent.putExtra("contactName", contactName);
                            intent.putExtra("myBitMap", managecontactnumber);
                            intent.putExtra("isGroup", false);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                    }
                }
            });

            doaudiocall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        utils.donewvoicecall(managecontactnumber, ManageUserActivity.this);
                        //dovoicecall(managecontactnumber);

                    } catch (Exception ex) {

                    }
                }
            });
            dovideocall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        utils.donewVideocall(managecontactnumber, ManageUserActivity.this);
//doVideocall(managecontactnumber);

                    } catch (Exception ex) {

                    }
                }
            });
            dochat.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {

/*
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NUMBER, managecontactnumber);
                        intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NAME, contactName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.context.startActivity(intent);
                        */
                        Intent intent = new Intent(ManageUserActivity.this, ChatAdvancedActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra("Sender", managecontactnumber);
                        intent.putExtra("IS_GROUP", false);
                        startActivity(intent);


                        //Toast.makeText(ManageUserActivity.this, "Chat Not Enabled", Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        utils.logStacktrace(ex);
                    }
                }
            });
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }
	/*
    public void doVideocall(String numbertodial) {

        try {
donewVideocall(numbertodial);
		} catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    public void dovoicecall(String numbertodial) {

        try {
donewvoicecall(numbertodial);
		} catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }
*/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {


        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }


    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver ManageUserActivity");
                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {

                } else if (intent.getAction().equals(CSEvents.CSCLIENT_LOGIN_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        List<String> numbers = new ArrayList<String>();
                        numbers.add(getIntent().getStringExtra("number"));
                        CSChatObj.getPresence(numbers);
                    }
                } else if (intent.getAction().equals(CSEvents.CSCHAT_GETPRESENCE_RESPONSE)) {
                    try {
                        if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {

                            if (getIntent().getStringExtra("number").toString().equals(intent.getStringExtra("presencenumber"))) {
                                String presencestatus = intent.getStringExtra("presencestatus");
                                long lastseentime = intent.getLongExtra("lastseentime", 0);

                                LOG.info("presencestatus:" + presencestatus);
                                LOG.info("lastseentime:" + lastseentime);

                                if (presencestatus.equals("ONLINE")) {
                                    subtitle.setText("online");
                                } else {
                                    if (DateUtils.isToday(lastseentime)) {
                                        subtitle.setText("last seen Today at " + new SimpleDateFormat("hh:mm a").format(lastseentime));
                                    } else if (utils.isYesterday(lastseentime)) {
                                        subtitle.setText("last seen Yesterday at " + new SimpleDateFormat("hh:mm a").format(lastseentime));
                                    } else {
                                        subtitle.setText("last seen at " + new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(lastseentime));
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
            LOG.info("onNewIntent onResume manage user");
            notificationManager.cancelAll();

            MainActivityReceiverObj = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            IntentFilter filter1 = new IntentFilter(CSEvents.CSCLIENT_LOGIN_RESPONSE);
            IntentFilter filter2 = new IntentFilter(CSEvents.CSCHAT_GETPRESENCE_RESPONSE);


            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter1);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter2);

            //String strr = CSDataProvider.getSetting(CSDbFields.KEY_SETINGS_LOGINSTATUS);
            if (CSDataProvider.getLoginstatus()) {
                List<String> numbers = new ArrayList<String>();
                numbers.add(getIntent().getStringExtra("number"));
                CSChatObj.getPresence(numbers);
            }
        } catch (Exception ex) {
        }

    }



    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        LOG.info("onNewIntent is called Manage user");
try {

    finish();
    startActivity(intent);
    /*
    //setIntent(intent);
    Intent intent1 = new Intent(MainActivity.context, ManageUserActivity.class);
    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent1.putExtra("name", intent.getStringExtra("name"));
    intent1.putExtra("number", intent.getStringExtra("number"));
    intent1.putExtra("description", intent.getStringExtra("description"));
    intent1.putExtra("picid", intent.getStringExtra("picid"));
    intent1.putExtra("nativecontactid", intent.getStringExtra("nativecontactid"));
    //Intent i = new Intent();
    //i.setAction("do_nothing");
   // setIntent(i);
    startActivity(intent1);
    finish();
    */
} catch (Exception ex) {
    ex.printStackTrace();
}
        /*if(intent.getStringExtra("methodName").equals("myMethod"))
        {

        }
        */
    }



    @Override
    public void onPause() {
        super.onPause();

        try {
            LOG.info("onNewIntent onPause manage user");
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
        } catch (Exception ex) {
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.exitmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		/* switch (item.getItemId()) {
	        case R.id.exit:
	        	
	        	
	        finish();
	    
	        //LOG.info("SR_TEMP_LOG:exited");
	        return true;
	       
	        default:
	        return super.onOptionsItemSelected(item);
	        }*/
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {

            finish();

        } catch (Exception ex) {

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap photo = null;
            try {

                photo = utils.loadContactPhoto(Long.parseLong(params[2]));

            } catch (Exception e) {

                utils.logStacktrace(e);
            }
            return photo;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        mContactProfileImage.setVisibility(View.VISIBLE);
                        mContactProfileImage.setImageBitmap(bitmap);
                        mBitmap = bitmap;
                        imageView.setVisibility(View.GONE);
                    } else {
                        mContactProfileImage.setVisibility(View.GONE);
						/*TextDrawable drawable2 = TextDrawable.builder()
				                .buildRound("A", Color.RED);*/
						/*Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
						imageView.setImageDrawable(placeholder);*/
                    }
                }
            }
        }
    }




/*
	public void donewVideocall(String numbertodial) {

		try {

			if (!numbertodial.equals("")&&!numbertodial.equals(GlobalVariables.phoneNumber)) {

				Intent intent = new Intent(getApplicationContext(), PlayNewVideoCallActivity.class);
				intent.putExtra("dstnumber", numbertodial);
				intent.putExtra("isinitiatior", true);
				startActivityForResult(intent, 954);
			} else {
				Toast.makeText(getApplicationContext(), "No valid Number", Toast.LENGTH_SHORT).show();
			}

		} catch (Exception ex) {
			utils.logStacktrace(ex);
		}
	}



	public void donewvoicecall(String numbertodial) {

		try {

			LOG.info("Doing new outgoing call");


			if (!numbertodial.equals("")&&!numbertodial.equals(GlobalVariables.phoneNumber)) {


				Intent intent = new Intent(getApplicationContext(), PlayNewAudioCallActivity.class);
				intent.putExtra("dstnumber", numbertodial);
				intent.putExtra("isinitiatior", true);
				startActivityForResult(intent, 954);
			} else {
				Toast.makeText(getApplicationContext(), "No valid Number", Toast.LENGTH_SHORT).show();
			}


		} catch (Exception ex) {
			utils.logStacktrace(ex);
		}
	}
*/

}
