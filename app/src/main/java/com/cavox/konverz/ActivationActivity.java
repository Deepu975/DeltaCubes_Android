package com.cavox.konverz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.multidex.MultiDex;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.ca.Utils.CSEvents;
import com.ca.Utils.CSConstants;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.utils;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;
import com.ca.wrapper.CSGroups;


import static com.cavox.utils.GlobalVariables.LOG;


public class ActivationActivity extends Activity {
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    Handler h = new Handler();
    int delay = 20000;
    Runnable RunnableObj;
    private TextView number;
    TextView resendcode;
    TextView textView6;
    private Button button_continue;
    private static EditText activation_code;
    //String username = GlobalVariables.phoneNumber;
    //String password = GlobalVariables.pass;
    CSClient CSClientObj = new CSClient();
    final Handler h1 = new Handler();
    Runnable RunnableObj1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        number = (TextView) findViewById(R.id.code_number);
        resendcode = (TextView) findViewById(R.id.resendcode);
        textView6 = (TextView) findViewById(R.id.textView6);
        button_continue = (Button) findViewById(R.id.button_continue);
        activation_code = (EditText) findViewById(R.id.activation_code);
        number.setText(GlobalVariables.phoneNumber);
        final LinearLayout mainLayoutt = (LinearLayout) findViewById(R.id.mainlayout);
        ;

        //IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //registerReceiver(smsReceiver, intentFilter);
        showTimer();

        button_continue.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!utils.isinternetavailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!activation_code.getText().toString().isEmpty()) {
                    button_continue.setEnabled(false);
                    showprogressbar();
                    CSClientObj.activate(getIntent().getStringExtra("phoneNumber"), activation_code.getText().toString());
                } else {
                    activation_code.setError(getResources().getString(R.string.error_empty_number));
                }
            }
        });

        resendcode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!utils.isinternetavailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                    return;
                }
                showTimer();
                showprogressbar();

				/*
				CSClientObj.reset();
				CSAppDetails csAppDetails = new CSAppDetails("iamLive","did_19e01bd3_b7d6_44fd_81ac_034ef7fcf6fb","aid_8656e0f6_c982_4485_8ca7_656780b53d34");
				CSClientObj.initialize(GlobalVariables.server, GlobalVariables.port,csAppDetails);
*/
                new CSClient().reSendActivationCode(false);


                //LOG.info("GlobalVariables.pass:"+GlobalVariables.pass);
                //LOG.info("GlobalVariables.phoneNumber:"+GlobalVariables.phoneNumber);
                //CSClientObj.signUp(GlobalVariables.phoneNumber,GlobalVariables.pass,false);
            }
        });

        mainLayoutt.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayoutt.getWindowToken(), 0);
                } catch (Exception ex) {
                    utils.logStacktrace(ex);
                }
            }


        });

    }

    public void showTimer() {

        try {
            RunnableObj1 = new Runnable() {
                int i = 120;

                public void run() {
                    h1.postDelayed(this, 1000);
                    try {

                        resendcode.setVisibility(View.INVISIBLE);
                        textView6.setVisibility(View.VISIBLE);

                        String minutes = "00";
                        String seconds = "00";
                        int x = i--;
                        int mins = (x) / 60;
                        int sec = (x) % 60;
                        if (mins < 10) {
                            minutes = "0" + String.valueOf(mins);
                        } else {
                            minutes = String.valueOf(mins);
                        }
                        if (sec < 10) {
                            seconds = "0" + String.valueOf(sec);
                        } else {
                            seconds = String.valueOf(sec);
                        }
                        textView6.setText("Resend Code in:" + minutes + ":" + seconds);
                        //textView6.setText("Resend Code in:"+i--);

                        if (i == 119) { // remove later after enabling automatic reading
                            try {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(activation_code, InputMethodManager.SHOW_IMPLICIT);
                            } catch (Exception ex) {
                            }
                        }


                    } catch (Exception ex) {
                        utils.logStacktrace(ex);
                    }

                    try {
                        ActivationActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (i <= 0) {
                                    h1.removeCallbacks(RunnableObj1);
                                    resendcode.setVisibility(View.VISIBLE);
                                    textView6.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } catch (Exception ex) {
                    }
                }
            };
            h1.postDelayed(RunnableObj1, 1000);
        } catch (Exception ex) {
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 933) {
            button_continue.setEnabled(true);
            Intent intent = new Intent();
            setResult(998, intent);
            dismissprogressbar();
            finish();
        }

    }


    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver");
                button_continue.setEnabled(true);

                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    dismissprogressbar();
                    //Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();

                } else if (intent.getAction().equals(CSEvents.CSCLIENT_LOGIN_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {

                        GlobalVariables.isalreadysignedup = true;
                        dismissprogressbar();
                        //finish();
                        CSGroups CSGroupsObj = new CSGroups();
                        CSGroupsObj.pullMyGroupsList();
                        Intent intentt = new Intent(getApplicationContext(), UserProfileActivity.class);
                        intentt.putExtra("isComingFromActivation", true);
                        startActivityForResult(intentt, 933);
                    }
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_ACTIVATION_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        String region = getIntent().getStringExtra("region");
                        if (region == null || region.equals("")) {
                            region = "+91";
                        }
                        CSClientObj.login(GlobalVariables.phoneNumber, GlobalVariables.pass);
                    } else {
                        dismissprogressbar();
                        Toast.makeText(getApplicationContext(), "Wrong Code.", Toast.LENGTH_SHORT).show();
                    }

                } else if (intent.getAction().equals(CSEvents.CSCLIENT_SIGNUP_RESPONSE) || intent.getAction().equals(CSEvents.CSCLIENT_RESEND_ACTIVATION_CODE_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        dismissprogressbar();
                    } else {
                        dismissprogressbar();
                        Toast.makeText(ActivationActivity.this, "SignUp failure", Toast.LENGTH_SHORT).show();

                    }
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_INITILIZATION_RESPONSE)) {
                    //String strr = CSDataProvider.getSetting(CSDbFields.KEY_SETINGS_ISALREADYSIGNEDUP);
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        if (!CSDataProvider.getSignUpstatus()) {
                            CSClientObj.signUp(GlobalVariables.phoneNumber, GlobalVariables.pass, false);
                        }
                    } else {
                        dismissprogressbar();
                        int retcode = intent.getIntExtra(CSConstants.RESULTCODE, 0);
                        LOG.info("INITILIZATIONFAILURE CODE:" + retcode);
                        Toast.makeText(getApplicationContext(), "INITILIZATIONFAILURE", Toast.LENGTH_SHORT).show();
                    }
                }
/*
				else if(intent.getAction().equals(CSEvents.CSCLIENT_INACTIVE_OPCODE)) {
					dismissprogressbar();
					Toast.makeText(getApplicationContext(), CSEvents.CSCLIENT_INACTIVE_OPCODE, Toast.LENGTH_SHORT).show();
				} else if(intent.getAction().equals(CSEvents.CSCLIENT_WRONG_OPCODE)) {
					dismissprogressbar();
					Toast.makeText(getApplicationContext(), CSEvents.CSCLIENT_WRONG_OPCODE, Toast.LENGTH_SHORT).show();

				} else if(intent.getAction().equals(CSEvents.CSCLIENT_LIMIT_EXCEEDED)) {
					dismissprogressbar();
					Toast.makeText(getApplicationContext(), CSEvents.CSCLIENT_LIMIT_EXCEEDED, Toast.LENGTH_SHORT).show();

				} else if(intent.getAction().equals(CSEvents.CSCLIENT_INITILIZATIONFAILURE)) {
					dismissprogressbar();
					Toast.makeText(getApplicationContext(), CSEvents.CSCLIENT_INITILIZATIONFAILURE, Toast.LENGTH_SHORT).show();

				} else if(intent.getAction().equals(CSEvents.CSCLIENT_INITALREADYINPROGRESS)) {
					dismissprogressbar();
					Toast.makeText(getApplicationContext(), CSEvents.CSCLIENT_INITALREADYINPROGRESS, Toast.LENGTH_SHORT).show();

				} else if(intent.getAction().equals(CSEvents.CSCLIENT_NOINTERNET)) {
					dismissprogressbar();
					Toast.makeText(getApplicationContext(), CSEvents.CSCLIENT_NOINTERNET, Toast.LENGTH_SHORT).show();
				}
*/

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
            //if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                //getApplicationContext().getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, this.smsobserverobj);
            //}
            MainActivityReceiverObj = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            IntentFilter filter8 = new IntentFilter(CSEvents.CSCLIENT_LOGIN_RESPONSE);
            IntentFilter filter15 = new IntentFilter(CSEvents.CSCLIENT_ACTIVATION_RESPONSE);
            IntentFilter filter16 = new IntentFilter(CSEvents.CSCLIENT_RESEND_ACTIVATION_CODE_RESPONSE);


            IntentFilter filter1 = new IntentFilter(CSEvents.CSCLIENT_SIGNUP_RESPONSE);

            IntentFilter filter9 = new IntentFilter(CSEvents.CSCLIENT_INITILIZATION_RESPONSE);
/*			IntentFilter filter10 = new IntentFilter(CSEvents.CSCLIENT_INACTIVE_OPCODE);
			IntentFilter filter11 = new IntentFilter(CSEvents.CSCLIENT_WRONG_OPCODE);
			IntentFilter filter12 = new IntentFilter(CSEvents.CSCLIENT_LIMIT_EXCEEDED);
			IntentFilter filter13 = new IntentFilter(CSEvents.CSCLIENT_INITILIZATIONFAILURE);
			IntentFilter filter18 = new IntentFilter(CSEvents.CSCLIENT_INITALREADYINPROGRESS);
			IntentFilter filter19 = new IntentFilter(CSEvents.CSCLIENT_NOINTERNET);
*/

            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter8);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter15);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter16);


            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter1);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter2);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter9);
			/*LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter10);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter11);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter12);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter13);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter18);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter19);
*/


        } catch (Exception ex) {
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);

            //getApplicationContext().getContentResolver().unregisterContentObserver(this.smsobserverobj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            //unregisterReceiver(smsReceiver);
            h1.removeCallbacks(RunnableObj1);
        } catch (Exception ex) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        button_continue.setEnabled(true);
        Intent intent = new Intent();
        setResult(998, intent);
        dismissprogressbar();
        finish();
        return;
    }
    /*
        BroadcastReceiver smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                final Bundle bundle = intent.getExtras();


                try {
                    if (bundle != null) {
                        final Object[] pdusObj = (Object[]) bundle.get("pdus");
                        for (int i = 0; i < pdusObj.length; i++) {
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String message = currentMessage.getDisplayMessageBody();

                            Log.e("OTP Message is:", "" + message);

                            try {
                                if (message.contains("Activation Code For Konverz")) {

                                    String[] mystrings = message.split(" ");
                                    activation_code.setText(mystrings[4]);
                                    activation_code.setSelection(mystrings[4].length());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };


        public ContentObserver smsobserverobj = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                try {
                    super.onChange(selfChange);
                    Uri smsURI = Uri.parse("content://sms");
                    Cursor cursor = getApplicationContext().getContentResolver().query(smsURI, null, null, null, null);
                    cursor.moveToNext();

                    //MESSAGE_TYPE_ALL    = 0;
                    //MESSAGE_TYPE_INBOX  = 1;
                    //MESSAGE_TYPE_SENT   = 2;
                    //MESSAGE_TYPE_DRAFT  = 3;
                    //MESSAGE_TYPE_OUTBOX = 4;
                    //MESSAGE_TYPE_FAILED = 5;
                    //MESSAGE_TYPE_QUEUED = 6;

                    int type = cursor.getInt(cursor.getColumnIndex("type"));
                    long mytime = new Date().getTime();
                    long acttime = cursor.getLong(cursor.getColumnIndex("date"));
                    LOG.info("verifydate: " + (mytime - acttime));


                    if (type == 1 && (mytime - acttime) < 30000) {

                        String address = cursor.getString(cursor.getColumnIndex("address"));
                        String body = cursor.getString(cursor.getColumnIndex("body"));
                        if (address.contains("")) {

                            if (body.contains("Activation Code")) {
                                String[] mystrings = body.split(" ");
                                activation_code.setText(mystrings[4]);
                                cursor.close();
                            }

                        }

                    }

                } catch (Exception ex) {
                    utils.logStacktrace(ex);
                }
            }

        };
    */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void showprogressbar() {
        try {
            if (getApplicationContext() != null && progressBar == null) {
                progressBarStatus = 0;
                progressBar = new ProgressDialog(ActivationActivity.this);
                progressBar.setCancelable(false);
                progressBar.setMessage("Please Wait..");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                //progressBar.setMax(time);
                progressBar.show();

                h = new Handler();
                RunnableObj = new Runnable() {

                    public void run() {
                        h.postDelayed(this, delay);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                button_continue.setEnabled(true);
                                dismissprogressbar();
                                //Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();


                            }
                        });
                    }
                };
                h.postDelayed(RunnableObj, delay);


            }
        } catch (Exception ex) {

        }
    }

    public void dismissprogressbar() {
        try {
            button_continue.setEnabled(true);
            LOG.info("dismissprogressbar1 " + progressBar);
            if (progressBar != null) {
                progressBar.dismiss();
                progressBar = null;
            }
            if (h != null) {
                h.removeCallbacks(RunnableObj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean showalert(String result) {
        try {
            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(ActivationActivity.this);
            successfullyLogin.setMessage(result);
            successfullyLogin.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        }
                    });
            successfullyLogin.setNegativeButton("Ok",
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

}
