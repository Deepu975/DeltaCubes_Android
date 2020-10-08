package com.cavox.konverz;


import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.multidex.MultiDex;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.app.deltacubes.R;
import com.ca.Utils.CSConstants;
import com.ca.Utils.CSEvents;
import com.cavox.utils.GlobalVariables;
import com.ca.wrapper.CSCall;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;
import com.ca.Utils.CSDbFields;
import com.cavox.utils.ManageAudioFocus;
import com.cavox.utils.PreferenceProvider;
import com.cavox.utils.utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.cavox.utils.GlobalVariables.LOG;

//import com.cavox.uiutils.UIActions;

public class CallScreenActivity extends AppCompatActivity {
    boolean ischeckpermission = false;
    static boolean donotring = false;
    boolean callansweredflag = false;
    int notificationid = 0;
    String displayname = "";
    final Handler h1 = new Handler();
    Runnable RunnableObj1;
    int delay = 1000;
    long remainingtimeoffet = 35;
    ManageAudioFocus manageAudioFocus = new ManageAudioFocus();
    String sDstMobNu = "";
    String callid = "";
    int callactive = 0;
    //String sdp = "";
    String callType = "audio";
    String srcnumber = "";
    CSClient CSClientObj = new CSClient();
    long callstarttime = 0;
    ImageView AnswerCall;
    ImageView EndCall;
    TextView CallType;
    TextView multiplecallalerttext;
    CircularImageView calleravathar;
    CSCall CSCallsObj = new CSCall();
    boolean callanswerstarted = false;
    boolean secondcall = false;
    public static Ringtone ringtone;// = RingtoneManager.getRingtone(MainActivity.context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

    private TextView mContactNameTv, mContactNumberTv;
    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    long[] pattern = {0, 1500, 2500, 1500, 2500, 1500, 2500, 1500, 2500, 1500, 2500, 1500, 2500, 1500, 2500, 1500, 2500, 1500, 2500, 1500, 2500}; //for 45 sec
    Vibrator vibrator = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callscreen);

        try {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            callstarttime = CSClientObj.getTime();


            AnswerCall = findViewById(R.id.button4);
            EndCall = findViewById(R.id.button5);
            CallType = (TextView) findViewById(R.id.textView10);
            calleravathar = (CircularImageView) findViewById(R.id.avathar);
            multiplecallalerttext = (TextView) findViewById(R.id.textView15);
            mContactNameTv = findViewById(R.id.contact_name_tv);
            mContactNumberTv = findViewById(R.id.contact_number_tv);
            mContactNumberTv.setSelected(true);
            mContactNameTv.setSelected(true);

            sDstMobNu = getIntent().getStringExtra("sDstMobNu");
            callid = getIntent().getStringExtra("callid");
            callactive = getIntent().getIntExtra("callactive", 0);
            //sdp = getIntent().getStringExtra("sdp");
            callType = getIntent().getStringExtra("callType");
            srcnumber = getIntent().getStringExtra("srcnumber");
            callstarttime = getIntent().getLongExtra("callstarttime", CSClientObj.getTime());
            secondcall = getIntent().getBooleanExtra("secondcall", false);








            GlobalVariables.incallcount = GlobalVariables.incallcount + 1;
            String nativecontactid = "";
            displayname = "";
            Cursor ccfr = CSDataProvider.getContactCursorByNumber(srcnumber);
            if (ccfr.getCount() > 0) {

                ccfr.moveToNext();
                displayname = ccfr.getString(ccfr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
                nativecontactid = ccfr.getString(ccfr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID));


            }
            ccfr.close();
            String notificationName=displayname;
            if(displayname.equals("")){
                notificationName=srcnumber;
            }
            if (callType.equals("video")) {
                notificationid = utils.notifyIncomigCall(getApplicationContext(), notificationName, "Incoming video call", secondcall);
                manageAudioFocus.requestAudioFocus(callid, srcnumber,"video");
            } else if (callType.equals("pstn")) {
                notificationid = utils.notifyIncomigCall(getApplicationContext(), notificationName, "Incoming pstn call", secondcall);
                manageAudioFocus.requestAudioFocus(callid, srcnumber,"pstn");

                LOG.info("DIDNUMBER:"+getIntent().getStringExtra("didnumber"));
            }
            else {
                notificationid = utils.notifyIncomigCall(getApplicationContext(), notificationName, "Incoming audio call", secondcall);
                manageAudioFocus.requestAudioFocus(callid, srcnumber,"audio");
            }
            registerReceiver(NotificationReceiver, new IntentFilter("NotificationHandleReceiver"));

            boolean vibrate = false;
            donotring = false;

            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            LOG.info("am mode:" + am.getRingerMode());
            int ringervoulume = am.getStreamVolume(AudioManager.MODE_RINGTONE);
            LOG.info("am ringervoulume:" + ringervoulume);


            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    donotring = true;
                    //Log.i("MyApp","Silent mode");
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    vibrate = true;
                    //Log.i("MyApp","Vibrate mode");
                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    //Log.i("MyApp","Normal mode");
                    break;
            }
            if (ringervoulume == 0) {
                donotring = true;
            }

            if (vibrate) {
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //vibrator.vibrate(VibrationEffect.createOneShot(50000, VibrationEffect.DEFAULT_AMPLITUDE));
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                    //vibrator.vibrate(pattern, -1);
                } else {
                    //deprecated in API 26
                    //vibrator.vibrate(500);
                    vibrator.vibrate(pattern, -1);
                }
            }


            if (!secondcall) {
                multiplecallalerttext.setVisibility(View.INVISIBLE);
            }


            long presenttime = CSClientObj.getTime();
            callansweredflag = false;
            remainingtimeoffet = 50;//-((presenttime-callstarttime)/1000);
            if (remainingtimeoffet < 0) {
                remainingtimeoffet = 0;
            }

            LOG.info("remainingtimeoffet:" + remainingtimeoffet);
            LOG.info("sDstMobNu:" + sDstMobNu);
            LOG.info("callid:" + callid);
            LOG.info("callactive:" + callactive);
            //LOG.info("sdp:"+sdp);
            LOG.info("callType:" + callType);
            LOG.info("srcnumber:" + srcnumber);


            //callednumber.setText(displayname);
            mContactNameTv.setText(displayname);
            mContactNumberTv.setText(srcnumber);
            if (callType.equals("video")) {
                CallType.setText("Video Call");
                CSCallsObj.sendVideoCallRinging(srcnumber, callid, secondcall);
            } else if(callType.equals("audio")) {
                CallType.setText("Audio Call");
                CSCallsObj.sendVoiceCallRinging(srcnumber, callid, secondcall);
            } else if(callType.equals("pstn")) {
                CallType.setText("Pstn Call");

            }
            RunnableObj1 = new Runnable() {
                int i = 0;

                public void run() {
                    h1.postDelayed(this, delay);
                    //LOG.info("printing at 1 sec");
                    CallScreenActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            i++;
                            if (i >= remainingtimeoffet || i >= 55) {


                                endCall(srcnumber, callid);


                            }
                        }
                    });
                }
            };
            h1.postDelayed(RunnableObj1, delay);


            AnswerCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {


                        if(vibrator != null) {
                            vibrator.cancel();
                        }
                        h1.removeCallbacks(RunnableObj1);
                        LOG.info("App incallcount:" + GlobalVariables.incallcount);
                        if (GlobalVariables.answeredcallcount > 0) {

                            AnswerCall.setEnabled(false);
                            //  AnswerCall.setAlpha(0.5f);


                            //  EndCall.setAlpha(0.5f);
                            EndCall.setEnabled(false);

                            //for (String activecallid:GlobalVariables.activecallids) {
                            Intent intentt = new Intent("TerminateForSecondCall");
                            intentt.putExtra("callid", GlobalVariables.lastcallid);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentt);
                            //}

//GlobalVariables.activecallids.clear();

                        } else {
                            answerCall();
                        }


//Thread.sleep(3000);

						/*if(GlobalVariables.lastcalltype.equals("video")) {
							CSCallsObj.endVideoCall(GlobalVariables.lastdestnationnumber,GlobalVariables.lastcallid, AppConstants.E_UserTerminated, AppConstants.UserTerminated);
							//CSPaymentsObj.directAudioVideoCallEndReq(srcnumber,2, callid,CSClientObj.getTime());
						} else {
							CSCallsObj.endAudioCall(GlobalVariables.lastdestnationnumber,GlobalVariables.lastcallid, AppConstants.E_UserTerminated, AppConstants.UserTerminated);
							//CSPaymentsObj.directAudioVideoCallEndReq(srcnumber, 1, callid,CSClientObj.getTime());
						}
						*/


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            EndCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        endCall(srcnumber, callid);

                    } catch (Exception ex) {
                        utils.logStacktrace(ex);
                    }
                }
            });

            //toolbar.setTitle("Subscribers");

            //setSupportActionBar(toolbar);

            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			/*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();

				}
			});
			*/
            String picid = "";
            Cursor cur = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, srcnumber);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                picid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
            }
            cur.close();

            new ImageDownloaderTask(calleravathar).execute("app", picid, nativecontactid);

            PreferenceProvider preferenceProvider = new PreferenceProvider(MainActivity.context);
            preferenceProvider.remove(callid + " incallnotification");
LOG.info("CallScreenActivity on create done");
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        return;
    }

    public void endCall(String srcnumber, String callid) {
        try {

            if (ringtone != null) {
                ringtone.stop(); ringtone = null;
            }
            if(vibrator != null) {
                vibrator.cancel();
            }
            h1.removeCallbacks(RunnableObj1);

            if (callType.equals("video")) {
                //LOG.info("Test here call id:"+callid);
                CSCallsObj.endVoiceCall(srcnumber, callid, CSConstants.E_UserBusy, CSConstants.UserBusy);
                //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber,2, callid,CSClientObj.getTime());
            } else if (callType.equals("pstn")) {
                //LOG.info("Test here call id:"+callid);
                CSCallsObj.endPstnCall(srcnumber, callid);
                //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber,2, callid,CSClientObj.getTime());
            } else {
                CSCallsObj.endVoiceCall(srcnumber, callid, CSConstants.E_UserBusy, CSConstants.UserBusy);
                //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber, 1, callid,CSClientObj.getTime());
            }


           
            h1.removeCallbacks(RunnableObj1);
            GlobalVariables.incallcount = GlobalVariables.incallcount - 1;
            if (GlobalVariables.incallcount < 0) {
                GlobalVariables.incallcount = 0;
            }
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);

            //Intent intent1 = new Intent(UIActions.INCOMINGCALLDONE.getKey());
            //LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent1);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationid);

            callansweredflag = true;
            manageAudioFocus.abandonAudioFocus();
            
            finish();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateUI(String str) {

        try {
            if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                LOG.info("NetworkError receieved");
                //Toast.makeText(this, "NetworkError", Toast.LENGTH_SHORT).show();

                if (ringtone != null) {
                    ringtone.stop(); ringtone = null;
                }
                if(vibrator != null) {
                    vibrator.cancel();
                }
                h1.removeCallbacks(RunnableObj1);

                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
                GlobalVariables.incallcount = GlobalVariables.incallcount - 1;
                if (GlobalVariables.incallcount < 0) {
                    GlobalVariables.incallcount = 0;
                }
                if (callType.equals("video")) {
                    CSCallsObj.endVideoCall(srcnumber, callid, CSConstants.E_UnKnown, CSConstants.UnKnown);
                    //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber,2, callid,CSClientObj.getTime());
                } else if (callType.equals("pstn")) {
                    //LOG.info("Test here call id:"+callid);
                    CSCallsObj.endPstnCall(srcnumber, callid);
                    //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber,2, callid,CSClientObj.getTime());
                } else {
                    CSCallsObj.endVoiceCall(srcnumber, callid, CSConstants.E_UnKnown, CSConstants.UnKnown);
                    //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber, 1, callid,CSClientObj.getTime());
                }
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationid);

                callansweredflag = true;
                manageAudioFocus.abandonAudioFocus();
                
                finish();


            }

        } catch (Exception ex) {
        }
    }


    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver in call screen:" + intent.getAction().toString());
                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    updateUI(CSEvents.CSCLIENT_NETWORKERROR);
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_LOGIN_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        // AnswerCall.setAlpha(1);
                        AnswerCall.setEnabled(true);

                        //  EndCall.setAlpha(1);
                        EndCall.setEnabled(true);
                        //AnswerCall.performClick();//temp change
                    }
                } else if (intent.getAction().equals(CSEvents.CSCALL_CALLTERMINATED)) {
                    if (GlobalVariables.lastcallid.equals(intent.getStringExtra("callid"))) {
                        answerCall();
                    }
                }
				/*
				else if (intent.getAction().equals(CSEvents.TerminateForSecondCall")) {
					if(callid.equals(intent.getStringExtra("callid"))) {
						EndCall.performClick();
					}
				}
				*/
                else if (intent.getAction().equals(CSEvents.CSCALL_CALLENDED)) {

                    LOG.info("mycallid local id:" + callid);
                    LOG.info("callid from callback:" + intent.getStringExtra("callid"));
                    LOG.info("endStatus from callback:" + intent.getIntExtra("endStatus", 0));
                    LOG.info("endReason from callback:" + intent.getStringExtra("endReason"));

                    if (callid.equals(intent.getStringExtra("callid"))) {
                        h1.removeCallbacks(RunnableObj1);
                        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
                        //Intent intent1 = new Intent(UIActions.INCOMINGCALLDONE.getKey());
                        //LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent1);
                        GlobalVariables.incallcount = GlobalVariables.incallcount - 1;
                        if (GlobalVariables.incallcount < 0) {
                            GlobalVariables.incallcount = 0;
                        }
                       /* NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(notificationid);*/
                        callansweredflag = true;
                        manageAudioFocus.abandonAudioFocus();
                        if (ringtone != null) {
                            ringtone.stop(); ringtone = null;
                        }
                        if(vibrator != null) {
                            vibrator.cancel();
                        }
                        finish();
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
            
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);

            MainActivityReceiverObj = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            IntentFilter filter4 = new IntentFilter(CSEvents.CSCALL_CALLENDED);
            IntentFilter filter5 = new IntentFilter(CSEvents.CSCLIENT_LOGIN_RESPONSE);
            IntentFilter filter6 = new IntentFilter(CSEvents.CSCALL_CALLTERMINATED);


            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter4);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter5);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter6);

            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter1);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter2);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter3);

            //IntentFilter filter7 = new IntentFilter(CSEvents.TerminateForSecondCall);
//LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter7);

            try {

                if (ringtone != null) {
                    if(!ringtone.isPlaying()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ringtone.setLooping(true);
                        }
                        ringtone.play();
                    }
                } else {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    //ringtone.
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ringtone.setLooping(true);
                    }
                    ringtone.play();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

           /* NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationid);*/


            LOG.info("TEST callid:" + callid);
            if (!CSCallsObj.isCallAnswerable(callid)) {

                LOG.info("TEST callid not exists");

                h1.removeCallbacks(RunnableObj1);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
                GlobalVariables.incallcount = GlobalVariables.incallcount - 1;
                if (GlobalVariables.incallcount < 0) {
                    GlobalVariables.incallcount = 0;
                }
                callansweredflag = true;
                manageAudioFocus.abandonAudioFocus();
         
                finish();
            } else {
                LOG.info("TEST callid exists");
            }

            //String strr = CSDataProvider.getSetting(CSDbFields.KEY_SETINGS_LOGINSTATUS);
            if (!CSDataProvider.getLoginstatus()) {
                //  AnswerCall.setAlpha(0.5f);
                AnswerCall.setEnabled(false);

                //  EndCall.setAlpha(0.5f);
                EndCall.setEnabled(false);
            } else {
                //AnswerCall.performClick();//temp change
            }

            checkpermissions();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
LOG.info("on pause is called");
        try {
if(!ischeckpermission) {
    if (ringtone != null) {
        ringtone.stop(); ringtone = null;
    }
    if (vibrator != null) {
        vibrator.cancel();
    }
} else {
    ischeckpermission = false;
}
            //LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
            /*if (!callansweredflag) {
                if (callType.equals("video")) {
                    notificationid = utils.notifycallinprogress(getApplicationContext(), displayname, "Incoming Video call", "", "", 0);
                } else if(callType.equals("pstn")) {
                    notificationid = utils.notifycallinprogress(getApplicationContext(), displayname, "Incoming PSTN call", "", "", 0);
                } else {
                    notificationid = utils.notifycallinprogress(getApplicationContext(), displayname, "Incoming Audio call", "", "", 0);
                }
            }*/
            callansweredflag = false;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
/*
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            if(ringtone.isPlaying()) {
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                LOG.info("am mode:"+am.getRingerMode());
                int ringervoulume = am.getStreamVolume(AudioManager.MODE_RINGTONE);
                LOG.info("am ringervoulume:"+ringervoulume);

            }
            //Toast.makeText(CallScreenActivity.this,"Up working",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {

            if(ringtone.isPlaying()) {

            }
            //Toast.makeText(CallScreenActivity.this,"Down working", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.exitmenu, menu);
        return true;
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
                if (params[0].equals("app")) {
                    photo = CSDataProvider.getImageBitmap(params[1]);
                } else {
                    photo = utils.loadContactPhoto(Long.parseLong(params[1]));
                }
                if (params[0].equals("app") && photo == null) {
                    photo = utils.loadContactPhoto(Long.parseLong(params[2]));
                }
                if (photo == null) {
                    photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
                }


            } catch (Exception e) {
                photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);

                //utils.logStacktrace(e);
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
                        imageView.setImageBitmap(bitmap);
                    } else {
						/*TextDrawable drawable2 = TextDrawable.builder()
				                .buildRound("A", Color.RED);*/
						/*Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
						imageView.setImageDrawable(placeholder);*/
                    }
                }
            }
        }
    }

    public boolean showalert(String result) {
        try {
            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(CallScreenActivity.this);
            successfullyLogin.setMessage("New Call");
            successfullyLogin.setPositiveButton("End present call and Answer",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                        }
                    });
            successfullyLogin.setNegativeButton("Ignore",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                        }
                    });

            successfullyLogin.show();

            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationid);
        unregisterReceiver(NotificationReceiver);
        super.onDestroy();
    }

    public void answerCall() {
        try {
            Intent intent1;
            if (callType.equals("video")) {
                intent1 = new Intent(MainActivity.context, PlayNewVideoCallActivity.class);
            } else if(callType.equals("pstn")) {
                intent1 = new Intent(MainActivity.context, PlaySipCallActivity.class);
            } else {
                intent1 = new Intent(MainActivity.context, PlayNewAudioCallActivity.class);
            }
            intent1.putExtra("isinitiatior", false);
            intent1.putExtra("sDstMobNu", sDstMobNu);
            intent1.putExtra("callactive", callactive);
            //intent.putExtra("sdp", sdp);
            intent1.putExtra("callType", callType);
            intent1.putExtra("srcnumber", srcnumber);

            intent1.putExtra("callid", callid);
            callanswerstarted = true;
            startActivity(intent1);
            GlobalVariables.incallcount = GlobalVariables.incallcount - 1;
            if (GlobalVariables.incallcount < 0) {
                GlobalVariables.incallcount = 0;
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationid);
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
            //Intent intent1 = new Intent(UIActions.INCOMINGCALLANSWERED.getKey());
            //LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent1);
            callansweredflag = true;
            if (ringtone != null) {
                ringtone.stop(); ringtone = null;
            }
            if(vibrator != null) {
                vibrator.cancel();
            }
            finish();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    BroadcastReceiver NotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("CallScreenActivity", "NotificationReceiver called");
            String action = intent.getAction();
            if (action.equals("NotificationHandleReceiver")) {
                String actionToperFrom = intent.getStringExtra("operationToPerform");
                Log.i("CallScreenActivity", "operationToPerform " + actionToperFrom);
                if (actionToperFrom.equals("EndCall")) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(notificationid);
                    EndCall.performClick();
                } else if (actionToperFrom.equals("AnswerCall")) {
                    if(callType.equals("video")&&(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)) {
                        //checkpermissions();
                    } else if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                        //checkpermissions();
                    } else {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(notificationid);
                        AnswerCall.performClick();
                    }
                }
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        LOG.info("onRequestPermissionsResult:"+requestCode);
      
        switch (requestCode) {
            case 101:
                if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                   if(callType.equals("video")) {
                       if (callType.equals("video")) {
                           Toast.makeText(CallScreenActivity.this, "Camera & Microphone permission needed", Toast.LENGTH_LONG).show();
                       } else {
                           Toast.makeText(CallScreenActivity.this, "Microphone permission needed", Toast.LENGTH_LONG).show();
                       }
                       endCall(srcnumber, callid);
                   }
                }
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                    if (callType.equals("video")) {
                        Toast.makeText(CallScreenActivity.this, "Camera & Microphone permission needed", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CallScreenActivity.this, "Microphone permission needed", Toast.LENGTH_LONG).show();
                    }
                    endCall(srcnumber, callid);
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
private void checkpermissions() {
    ischeckpermission = true;
    LOG.info("checkpermissions is called");
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
        ArrayList<String> allpermissions = new ArrayList<String>();
        if (callType.equals("video")) {
            allpermissions.add(android.Manifest.permission.CAMERA);
            //Toast.makeText(CallScreenActivity.this, "Camera & Microphone permission needed", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(CallScreenActivity.this, "Microphone permission needed", Toast.LENGTH_LONG).show();
        }
        allpermissions.add(android.Manifest.permission.RECORD_AUDIO);
        ArrayList<String> requestpermissions = new ArrayList<String>();

        for (String permission : allpermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                requestpermissions.add(permission);
            }
        }
        if (requestpermissions.size() > 0) {
            ActivityCompat.requestPermissions(CallScreenActivity.this, requestpermissions.toArray(new String[requestpermissions.size()]), 101);
        }
    }
}
}