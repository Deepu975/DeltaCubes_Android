package com.cavox.paymentgateways.payzone;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.ca.dao.CSExplicitEventReceivers;
import androidx.multidex.MultiDex;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSEvents;
import com.ca.dao.CSAppDetails;
import com.app.deltacubes.R;
import com.cavox.konverz.MainActivity;
import com.cavox.konverz.UserProfileActivity;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.PreferenceProvider;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSGroups;

import java.util.ArrayList;

import static com.cavox.utils.GlobalVariables.LOG;

public class PayZoneActivity extends AppCompatActivity
{
	Handler h1 = new Handler();
	Runnable RunnableObj1;
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	Handler h = new Handler();
	int delay = 80000;
	Runnable RunnableObj;
boolean tellmenwerror = false;
	EditText urlobj;
	EditText urlobj1;
	EditText urlobj2;
	String username = "";
	String password = "";
	String mobile = "";
	boolean showpassword = false;
	//IAmLiveCoreSend IAmLiveCoreSendObj = new IAmLiveCoreSend();
	//IAmLiveCore IAmLiveCoreObj = new IAmLiveCore();
	//ClientServer ClientServerObj = new ClientServer();
	CSClient CSClientObj = new CSClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.direct_login);
		try {
			System.out.println("onViewCreated:");
			showpassword = false;
			final Button submitobj = findViewById(R.id.button6);

			final RelativeLayout mainLayoutt = (RelativeLayout) findViewById(R.id.mainlayout);
			EditText server = (EditText) findViewById(R.id.server);
			urlobj = (EditText) findViewById(R.id.editText2);
			urlobj1 = (EditText) findViewById(R.id.editText1);

			//server.setText(GlobalVariables.server+":"+GlobalVariables.port+":"+GlobalVariables.appname+":"+GlobalVariables.appid);//temp


			PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
			boolean dontshowagain = pf.getPrefBoolean("registerreceiversnew");
			if(!dontshowagain) {
				CSClientObj.registerExplicitEventReceivers(new CSExplicitEventReceivers("com.cavox.receivers.CSUserJoined","com.cavox.receivers.CSCallReceiver","com.cavox.receivers.CSChatReceiver","com.cavox.receivers.CSGroupNotificationReceiver","com.cavox.receivers.CSCallMissed"));
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
				allpermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
				allpermissions.add(android.Manifest.permission.VIBRATE);
				allpermissions.add(android.Manifest.permission.READ_PHONE_STATE);

				ArrayList<String> requestpermissions = new ArrayList<String>();

				for (String permission : allpermissions) {
					if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
						requestpermissions.add(permission);
					}
				}
				if(requestpermissions.size()>0) {
					ActivityCompat.requestPermissions(this, requestpermissions.toArray(new String[requestpermissions.size()]), 101);
				}
				}



			try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String version = pInfo.versionName;
			final TextView versionview = (TextView) findViewById(R.id.textView13);
			versionview.setText("Version:"+version);
		} catch(Exception ex) {}

			final View kjhkj = mainLayoutt;

			mainLayoutt.setOnClickListener(new View.OnClickListener() {


				public void onClick(View v) {
					//System.out.println("SR_LOG:SUBMIT");

					try {
						if (kjhkj != null) {
							System.out.println("ok here1");
							InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
							System.out.println("ok here2");
							//imm.h
							imm.hideSoftInputFromWindow(kjhkj.getWindowToken(), 0);
						} else {
							System.out.println("but view is null hideKeyboard");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}


			});

			if (submitobj != null)
				submitobj.setOnClickListener(new View.OnClickListener() {


					public void onClick(View v) {
						//System.out.println("SR_LOG:SUBMIT");
						tellmenwerror = true;
						try {
							if (urlobj.getText().toString().equals("")) {
								urlobj.setError("Username shouldn't be empty");
								/*Toast.makeText(getApplicationContext(), "Username shouldn't be empty", Toast.LENGTH_SHORT).show();*/
							} else if (urlobj1.getText().toString().equals("")) {
								urlobj1.setError("Password shouldn't be empty");
								/*Toast.makeText(getApplicationContext(), "Password shouldn't be empty", Toast.LENGTH_SHORT).show();*/
							} else if (urlobj.getText().toString().contains("\n")) {
								urlobj.setError("Username should be valid");
								/*Toast.makeText(getApplicationContext(), "Username should be valid", Toast.LENGTH_SHORT).show();*/
							} else if (urlobj1.getText().toString().contains("\n")) {
								urlobj1.setError("Password should be valid");
								/*Toast.makeText(getApplicationContext(), "Password should be valid", Toast.LENGTH_SHORT).show();*/
							} else {

								username = urlobj.getText().toString();
								password = urlobj1.getText().toString();
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
//end tmp changes
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
									pf.setPrefString("server", GlobalVariables.server);
									pf.setPrefint("port", GlobalVariables.port);
									pf.setPrefString("appname",GlobalVariables.appname);
									pf.setPrefString("appid",GlobalVariables.appid);
								}

								showprogressbar();

								GlobalVariables.server = pf.getPrefString("server");
								GlobalVariables.port = pf.getPrefInt("port");
								GlobalVariables.appname = pf.getPrefString("appname");
								GlobalVariables.appid = pf.getPrefString("appid");

								//CSAppDetails csAppDetails = new CSAppDetails("iamLive","did_19e01bd3_b7d6_44fd_81ac_034ef7fcf6fb","aid_8656e0f6_c982_4485_8ca7_656780b53d34");
								//CSAppDetails csAppDetails = new CSAppDetails("iamLive","aid_8656e0f6_c982_4485_8ca7_656780b53d34");                         //for go4sip
								CSAppDetails csAppDetails = new CSAppDetails(GlobalVariables.appname,GlobalVariables.appid);
								CSClientObj.initialize(GlobalVariables.server, GlobalVariables.port, csAppDetails);

								//IAmLiveCoreSendObj.registerSipUserReq(username, password, "server", "5061", GlobalVariables.brandpin,Constants.TRANSACTION_COUNTER++);
							}

						} catch (Exception ex) {
							ex.printStackTrace();
							Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
						}

					}


				});


			urlobj1.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					final int DRAWABLE_LEFT = 0;
					final int DRAWABLE_TOP = 1;
					final int DRAWABLE_RIGHT = 2;
					final int DRAWABLE_BOTTOM = 3;

					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (event.getRawX() >= (urlobj1.getRight() - urlobj1.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
							if (showpassword) {
								urlobj1.setTransformationMethod(PasswordTransformationMethod.getInstance());
								showpassword = false;
								urlobj1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eyenot, 0);

							} else {
								urlobj1.setTransformationMethod(null);
								showpassword = true;
								urlobj1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);

							}
							return true;
						}
					}
					return false;
				}
			});


		} catch(Exception ex){}

	}

	public void showprogressbar() {
		try {
			if(getApplicationContext()!=null) {
				progressBarStatus = 0;
				progressBar = new ProgressDialog(PayZoneActivity.this);
				progressBar.setCancelable(false);
				progressBar.setMessage("Please Wait..");
				progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressBar.setProgress(0);
				//progressBar.setMax(time);
				progressBar.show();

				h = new Handler();
				RunnableObj = new Runnable(){

					public void run(){
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
			System.out.println("dismissprogressbar");
			if(progressBar!=null) {
				progressBar.dismiss();

			}
			if(h!=null) {
				h.removeCallbacks(RunnableObj);
			}
		} catch(Exception ex){}
	}




	public class MainActivityReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			try {

				LOG.info("Yes Something receieved in RecentReceiver:"+intent.getAction().toString());


				if(intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
					dismissprogressbar();
					Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();

				}

				else if(intent.getAction().equals(CSEvents.CSCLIENT_INITILIZATION_RESPONSE)) {
					//String strr = CSDataProvider.getSignUpstatus();
					dismissprogressbar();
					//LOG.info("here 1");
					if(intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
						//LOG.info("here 2");
						CSClientObj.enableNativeContacts(true,91);
						//LOG.info("here 3");
						CSClientObj.login(urlobj.getText().toString(),urlobj1.getText().toString());
						//LOG.info("here 4");
					} else {
						//LOG.info("here 5");
						int retcode = intent.getIntExtra(CSConstants.RESULTCODE,0);
						LOG.info("INITILIZATIONFAILURE CODE:"+retcode);
						if(retcode == CSConstants.E_409_NOINTERNET) {
							showalert("No Internet Available");
						} else {
							Toast.makeText(getApplicationContext(), "INITILIZATIONFAILURE", Toast.LENGTH_SHORT).show();
						}
					}
				}
				else if(intent.getAction().equals(CSEvents.CSCLIENT_LOGIN_RESPONSE)) {
					dismissprogressbar();

					if(intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {

						PreferenceProvider pf = new PreferenceProvider(getApplicationContext());port:
						pf.setPrefboolean("isLogin", true);
						Log.i("DirectLoginActivity", "onReceive: ");
						GlobalVariables.isalreadysignedup = true;
						CSGroups CSGroupsObj = new CSGroups();
						CSGroupsObj.pullMyGroupsList();
						Intent intentt = new Intent(getApplicationContext(), UserProfileActivity.class);
						startActivityForResult(intentt, 933);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();

					}
				}
			} catch(Exception ex){}
		}



	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 933) {
			Intent intent = new Intent();
			setResult(639, intent);
			dismissprogressbar();
			finish();
		}

	}

	MainActivityReceiver MainActivityReceiverObj = new MainActivityReceiver();
	@Override
	public void onResume() {
		super.onResume();

		try{

			GlobalVariables.loginretries = 0;


			IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
			IntentFilter filter1 = new IntentFilter(CSEvents.CSCLIENT_SIGNUP_RESPONSE);
			IntentFilter filter2 = new IntentFilter(CSEvents.CSCLIENT_LOGIN_RESPONSE);
			IntentFilter filter3 = new IntentFilter(CSEvents.CSCLIENT_INITILIZATION_RESPONSE);
			MainActivity.context = getApplicationContext();

			LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj,filter);
			LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj,filter1);
			LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj,filter2);
			LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj,filter3);

		} catch(Exception ex) {}

	}
	@Override
	public void onPause() {
		super.onPause();

		try {
			//IAmLiveCore.cancelalarm();
			//Utils.handleonpause();
			LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
		} catch(Exception ex) {}


	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		//GlobalVariables.isinitdone = false;
		//GlobalVariables.isappforeground = false;
		System.out.print("Yes in on back pressed");
		finish();
		return;
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
	    
	        //System.out.println("SR_TEMP_LOG:exited");
	        return true;
	       
	        default:
	        return super.onOptionsItemSelected(item);
	        }*/
		return true;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public boolean showalert(String result) {
		try {
			android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(PayZoneActivity.this);
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
		} catch(Exception ex) {
			return false;
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 101:
				for(int i=0;i<permissions.length;i++) {
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
}
