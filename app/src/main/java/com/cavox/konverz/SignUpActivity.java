package com.cavox.konverz;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.app.deltacubes.R;
import com.ca.dao.CSExplicitEventReceivers;

import androidx.multidex.MultiDex;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSEvents;
import com.ca.dao.CSAppDetails;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.PreferenceProvider;
import com.cavox.utils.utils;
//import com.ca.wrapper.CSChannels;
//import com.ca.wrapper.CSStream;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import com.vx.testlib.Testit;

import static com.cavox.utils.GlobalVariables.LOG;


public class SignUpActivity extends Activity implements AdapterView.OnItemSelectedListener {
    boolean showpassword = false;
    private TextView button_continue;
    private Button button_country;
    //private TextView code;
    private EditText number;
    private EditText userpassword;
    EditText server;
    //private String region;
    static String countryCode = "+91";

    Handler h1 = new Handler();
    Runnable RunnableObj1;
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    Handler h = new Handler();
    int delay = 20000;
    Runnable RunnableObj;
    CSClient CSClientObj = new CSClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            button_continue = (TextView) findViewById(R.id.loginBtn);
            button_country = (Button) findViewById(R.id.button_country);
            number = (EditText) findViewById(R.id.number);
            userpassword = (EditText) findViewById(R.id.password);
            final RelativeLayout mainLayoutt = (RelativeLayout) findViewById(R.id.mainlayout);
            server = (EditText) findViewById(R.id.server);
            final Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinner.getBackground().setColorFilter(getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_ATOP);

            //server.setText(GlobalVariables.server+":"+GlobalVariables.port+":"+GlobalVariables.appname+":"+GlobalVariables.appid);//temp

            PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
            boolean dontshowagain = pf.getPrefBoolean("registerreceiversnew");
            if (!dontshowagain) {
                CSClientObj.registerExplicitEventReceivers(new CSExplicitEventReceivers("com.cavox.receivers.CSUserJoined", "com.cavox.receivers.CSCallReceiver", "com.cavox.receivers.CSChatReceiver", "com.cavox.receivers.CSGroupNotificationReceiver", "com.cavox.receivers.CSCallMissed"));
                PreferenceProvider pff = new PreferenceProvider(getApplicationContext());
                pff.setPrefboolean("registerreceiversnew", true);
            }


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                ArrayList<String> allpermissions = new ArrayList<String>();
                allpermissions.add(android.Manifest.permission.CAMERA);
                allpermissions.add(android.Manifest.permission.READ_CONTACTS);
                allpermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                allpermissions.add(android.Manifest.permission.RECORD_AUDIO);
                allpermissions.add(android.Manifest.permission.READ_PHONE_STATE);
                //allpermissions.add(android.Manifest.permission.READ_SMS);
                //allpermissions.add(Manifest.permission.RECEIVE_SMS);
                allpermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                allpermissions.add(android.Manifest.permission.VIBRATE);
                allpermissions.add(android.Manifest.permission.READ_PHONE_STATE);

                ArrayList<String> requestpermissions = new ArrayList<String>();

                for (String permission : allpermissions) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                        requestpermissions.add(permission);
                    }
                }
                if (requestpermissions.size() > 0) {
                    ActivityCompat.requestPermissions(this, requestpermissions.toArray(new String[requestpermissions.size()]), 101);
                }
            }

            List<String> categories = new ArrayList<String>();
            //categories.add("pid_4df0c551_11b2_4530_8a78_10b83c1986f1");
            //categories.add("pid_f2f9ba00_ae1c_4820_af35_7845e38e92eb");
            //categories.add("pid_c29b18cb_373c_4544_b6be_d42b02db4f8f");
            categories.add("");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(this);
            //spinner.setSelection(0);


            userpassword.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (userpassword.getRight() - userpassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (showpassword) {
                                userpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                showpassword = false;
                                userpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_hide, 0);
                            } else {
                                userpassword.setTransformationMethod(null);
                                showpassword = true;
                                userpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_show, 0);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });


            //number.setText("9492084600");//temp


            button_continue.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!utils.isinternetavailable(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (number.getText().toString().equals("")) {
                        LOG.info("Please Enter a Valid Number");
                        number.setError("Please Enter a Valid Number");
                    } /*else if(userpassword.getText().toString().equals("")) {
                    userpassword.setError("Please Enter a Password");
                }*/ else {
                        GlobalVariables.phoneNumber = CSClientObj.getInternationalFormatNumber(number.getText().toString(), countryCode);
                        if (GlobalVariables.phoneNumber != null) {
                            if (userpassword.getText().toString().equals("")) {
                                GlobalVariables.pass = String.valueOf(new Random().nextInt(1000000));
                            } else {
                                GlobalVariables.pass = userpassword.getText().toString();
                            }

                            try {
                                PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
                                if (!server.getText().toString().equals("")) {
                                    String[] serverport = server.getText().toString().split(":");
                                    pf.setPrefString("server", serverport[0]);
                                    pf.setPrefint("port", Integer.parseInt(serverport[1]));
                                    pf.setPrefString("appname",serverport[2]);
                                    pf.setPrefString("appid",serverport[3]);

/*
                                    //start tmp changes
                                    //String[] serverport = server.getText().toString().split(":");
                                    pf.setPrefString("server", GlobalVariables.server);
                                    pf.setPrefint("port", GlobalVariables.port);
                                    pf.setPrefString("appname",GlobalVariables.appname);
                                    pf.setPrefString("appid",server.getText().toString());
// end tmp changes

 */

                                    GlobalVariables.server = pf.getPrefString("server");
                                    GlobalVariables.port = pf.getPrefInt("port");
                                    GlobalVariables.appname = pf.getPrefString("appname");
                                    GlobalVariables.appid = pf.getPrefString("appid");
                                } else {
                                    pf.setPrefString("server", GlobalVariables.server);
                                    pf.setPrefint("port", GlobalVariables.port);
                                    pf.setPrefString("appname",GlobalVariables.appname);
                                    pf.setPrefString("appid",GlobalVariables.appid);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                pf.setPrefString("server", GlobalVariables.server);
                                pf.setPrefint("port", GlobalVariables.port);
                                pf.setPrefString("appname",GlobalVariables.appname);
                                pf.setPrefString("appid",GlobalVariables.appid);
                            }

                            showalert();
                        } else {
                            LOG.info("Please Enter a Valid Number Number");
                            number.setError("Please Enter a Valid Number");
                        }
                    }
                }
            });

            button_country.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), ZoneSelectActivity.class);
                    startActivityForResult(intent, 999);
                }
            });


            detectCountryCode();


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

        } catch (Exception ex) {

        }


    }

    public void detectCountryCode() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String countryisoCode = tm.getSimCountryIso();
            if (countryisoCode == null || countryisoCode.equals("")) {
                countryisoCode = "in";
            }
            countryCode = getZipCodeBySimCountryIso(countryisoCode);
            if (countryCode == null || countryCode.equals("+") || countryisoCode.equals("")) {
                countryCode = "+91";
            }
            String countryName = getcountrynameBySimCountryIso(countryisoCode);
            button_country.setText(countryName != null && !countryName.isEmpty() ? countryName + "(" + countryCode + ")" : "Country");
        } catch (Exception ex) {
        }
    }


    public String getZipCodeByName(String countryName) {
        String CountryZipCode = "";
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[2].trim().equalsIgnoreCase(countryName.trim())) {
                CountryZipCode = g[1];
                break;
            }
        }
        return "+" + CountryZipCode;
    }

    public String getZipCodeBySimCountryIso(String countryName) {
        String CountryZipCode = "";
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[0].trim().equalsIgnoreCase(countryName.trim())) {
                CountryZipCode = g[1];
                break;
            }
        }
        return "+" + CountryZipCode;
    }

    public String getcountrynameBySimCountryIso(String countryName) {
        String CountryZipCode = "";
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[0].trim().equalsIgnoreCase(countryName.trim())) {
                CountryZipCode = g[2];
                break;
            }
        }
        return CountryZipCode;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999) {
            if (null != data && data.getExtras().containsKey("zone")) {
                String countryName = data.getStringExtra("zone");
                countryCode = getZipCodeByName(data.getStringExtra("zone"));
                button_country.setText(countryName != null && !countryName.isEmpty() ? countryName + "(" + countryCode + ")" : "Country");
            }
        } else if (requestCode == 998) {
            if (!GlobalVariables.isalreadysignedup) {
                CSClientObj.reset();
            }
            if (CSDataProvider.getLoginstatus()) {
                //Intent intent=new Intent();
                //setResult(638,intent);
                //finish();
                PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
                pf.setPrefboolean("isLogin", true);

                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public boolean showalert() {
        try {


            //Testit TestitObj = new Testit();
            //TestitObj.testprint();


            Builder successfullyLogin = new Builder(SignUpActivity.this);
            successfullyLogin.setTitle("Confirmation");

            successfullyLogin.setMessage("A verification code will be sent by SMS to this number " + GlobalVariables.phoneNumber);

            successfullyLogin.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            if (!utils.isinternetavailable(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            showprogressbar();


                            PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
                            GlobalVariables.server = pf.getPrefString("server");
                            GlobalVariables.port = pf.getPrefInt("port");
                            GlobalVariables.appname = pf.getPrefString("appname");
                            GlobalVariables.appid = pf.getPrefString("appid");

                            LOG.info("GlobalVariables.server:"+GlobalVariables.server);
                            LOG.info("GlobalVariables.port:"+GlobalVariables.port);
                            LOG.info("GlobalVariables.appname:"+GlobalVariables.appname);
                            LOG.info("GlobalVariables.appid:"+GlobalVariables.appid);




                            //CSAppDetails csAppDetails = new CSAppDetails("iamLive","did_19e01bd3_b7d6_44fd_81ac_034ef7fcf6fb","aid_8656e0f6_c982_4485_8ca7_656780b53d34");
                            //CSAppDetails csAppDetails = new CSAppDetails("iamLive", "aid_8656e0f6_c982_4485_8ca7_656780b53d34");                          //for go4sip
                            //CSAppDetails csAppDetails = new CSAppDetails("iamlivedbnew","iamlivedbnew","iamlivedbnew");
                            CSAppDetails csAppDetails = new CSAppDetails(GlobalVariables.appname,GlobalVariables.appid);

                            CSClientObj.initialize(GlobalVariables.server, GlobalVariables.port, csAppDetails);
                        }
                    });

            successfullyLogin.setNegativeButton("Cancel",
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

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver SignUpActivity");
                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();
                    dismissprogressbar();
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_SIGNUP_RESPONSE)) {

                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {

                        //CSDataProvider.updateSetting(CSDbFields.KEY_SETINGS_RANDOMID, GlobalVariables.pass);
                        dismissprogressbar();
                        Intent intentt = new Intent(getApplicationContext(), ActivationActivity.class);
                        intentt.putExtra("phoneNumber", GlobalVariables.phoneNumber);
                        intentt.putExtra("region", countryCode);
                        startActivityForResult(intentt, 998);
                    } else {
                        dismissprogressbar();
                        int retcode = intent.getIntExtra("retcode", 0);
                        if (retcode == CSConstants.E_422_UNPROCESSABLE_ENTITY) {
                            Toast.makeText(SignUpActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "SignUp Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_INITILIZATION_RESPONSE)) {
                    //String strr = CSDataProvider.getSignUpstatus();
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {

                        CSClientObj.enableNativeContacts(true, 91);
                        if (!CSDataProvider.getSignUpstatus()) {
                            CSClientObj.signUp(GlobalVariables.phoneNumber, GlobalVariables.pass, false);
                        }
                    } else {
                        dismissprogressbar();
                        int retcode = intent.getIntExtra(CSConstants.RESULTCODE, 0);
                        LOG.info("INITILIZATIONFAILURE CODE:" + retcode);
                        if (retcode == CSConstants.E_409_NOINTERNET) {
                            shownwalert("No Internet Available");
                        } else {
                            Toast.makeText(getApplicationContext(), "INITILIZATIONFAILURE", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

				/*else if(intent.getAction().equals(CSEvents.CSCLIENT_INACTIVE_OPCODE)) {
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
					//Toast.makeText(getApplicationContext(), CSEvents.nointernet, Toast.LENGTH_SHORT).show();
					shownwalert("No Internet Available");
				}
	    	*/
            } catch (Exception ex) {
            }
        }
    }

    MyReceiver MyReceiverObj = new MyReceiver();

    @Override
    public void onResume() {
        super.onResume();

        try {

            MyReceiverObj = new MyReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            IntentFilter filter1 = new IntentFilter(CSEvents.CSCLIENT_SIGNUP_RESPONSE);

            IntentFilter filter9 = new IntentFilter(CSEvents.CSCLIENT_INITILIZATION_RESPONSE);
			/*IntentFilter filter10 = new IntentFilter(CSEvents.CSCLIENT_INACTIVE_OPCODE);
			IntentFilter filter11 = new IntentFilter(CSEvents.CSCLIENT_WRONG_OPCODE);
			IntentFilter filter12 = new IntentFilter(CSEvents.CSCLIENT_LIMIT_EXCEEDED);
			IntentFilter filter13 = new IntentFilter(CSEvents.CSCLIENT_INITILIZATIONFAILURE);
			IntentFilter filter18 = new IntentFilter(CSEvents.CSCLIENT_INITALREADYINPROGRESS);
			IntentFilter filter19 = new IntentFilter(CSEvents.CSCLIENT_NOINTERNET);
*/
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj, filter);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj, filter1);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj,filter2);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj, filter9);
			/*LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj,filter10);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj,filter11);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj,filter12);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj,filter13);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj,filter18);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MyReceiverObj,filter19);
*/
            //CSDataProvider.deletesettings();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MyReceiverObj);

        } catch (Exception ex) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(996, intent);
        finish();
        return;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public boolean shownwalert(String result) {
        try {
            Builder successfullyLogin = new Builder(SignUpActivity.this);
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


    public void showprogressbar() {
        try {
            if (getApplicationContext() != null) {
                progressBarStatus = 0;
                progressBar = new ProgressDialog(SignUpActivity.this);
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
                                dismissprogressbar();
                                Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();


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
            LOG.info("dismissprogressbar3");
            if (progressBar != null) {
                progressBar.dismiss();

            }
            if (h != null) {
                h.removeCallbacks(RunnableObj);
            }
        } catch (Exception ex) {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        LOG.info(permissions[i] + ":permission granted");
                    } else {
                        LOG.info(permissions[i] + ":permission denied");
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        LOG.info("item got selected:"+item);
        //server.setText(GlobalVariables.server+":"+GlobalVariables.port+":"+GlobalVariables.appname+":"+item);//temp

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        LOG.info("Nothing got selected");
        //server.setText("");
    }

}



	