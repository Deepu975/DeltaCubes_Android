package com.cavox.konverz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.ca.Utils.CSConstants;
import com.ca.Utils.CSEvents;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;

import static com.cavox.utils.GlobalVariables.LOG;

public class ResetPasswordActivity extends AppCompatActivity {
	boolean showpassword = false;
	boolean showpassword1 = false;
	boolean showpassword2 = false;
	 Button button_done;
	 EditText user_name;
	 EditText presence_text;
	//CircularImageView userImage;
	EditText mobilenumber;
	// The primary interface we will using for the IM service
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	Handler h = new Handler();
	int delay = 20000;
	Runnable RunnableObj;
	String temppath = "tempprofile";
	CSClient CSClientObj = new CSClient();
	String filepath = "";
	AlertDialog ad;
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpassword);
		//userImage = (CircularImageView)findViewById(R.id.user_image);
		button_done = (Button)findViewById(R.id.button_done);
		user_name = (EditText)findViewById(R.id.user_name);
		presence_text = (EditText)findViewById(R.id.user_presenceText);
		mobilenumber = (EditText)findViewById(R.id.user_number);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Change Password");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


try {




	toolbar.setNavigationOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();

		}
	});
	mobilenumber.setOnTouchListener(new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int DRAWABLE_LEFT = 0;
			final int DRAWABLE_TOP = 1;
			final int DRAWABLE_RIGHT = 2;
			final int DRAWABLE_BOTTOM = 3;

			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (event.getRawX() >= (mobilenumber.getRight() - mobilenumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
					if (showpassword) {
						mobilenumber.setTransformationMethod(PasswordTransformationMethod.getInstance());
						showpassword = false;
						mobilenumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.viewpasswordnot, 0);

					} else {
						mobilenumber.setTransformationMethod(null);
						showpassword = true;
						mobilenumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.viewpassword, 0);

					}
					return true;
				}
			}
			return false;
		}
	});


	user_name.setOnTouchListener(new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int DRAWABLE_LEFT = 0;
			final int DRAWABLE_TOP = 1;
			final int DRAWABLE_RIGHT = 2;
			final int DRAWABLE_BOTTOM = 3;

			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (event.getRawX() >= (user_name.getRight() - user_name.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
					if (showpassword1) {
						user_name.setTransformationMethod(PasswordTransformationMethod.getInstance());
						showpassword1 = false;
						user_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.viewpasswordnot, 0);

					} else {
						user_name.setTransformationMethod(null);
						showpassword1 = true;
						user_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.viewpassword, 0);

					}
					return true;
				}
			}
			return false;
		}
	});

	presence_text.setOnTouchListener(new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int DRAWABLE_LEFT = 0;
			final int DRAWABLE_TOP = 1;
			final int DRAWABLE_RIGHT = 2;
			final int DRAWABLE_BOTTOM = 3;

			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (event.getRawX() >= (presence_text.getRight() - presence_text.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
					if (showpassword2) {
						presence_text.setTransformationMethod(PasswordTransformationMethod.getInstance());
						showpassword2 = false;
						presence_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.viewpasswordnot, 0);

					} else {
						presence_text.setTransformationMethod(null);
						showpassword2 = true;
						presence_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.viewpassword, 0);

					}
					return true;
				}
			}
			return false;
		}
	});









	button_done.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				String oldpassword  = CSDataProvider.getPassword();
String enteredoldpassword = mobilenumber.getText().toString();
String newpassword = user_name.getText().toString();
String reeneteredpassword = presence_text.getText().toString();


if(CSDataProvider.getLoginstatus()) {
	//if (oldpassword.equals(enteredoldpassword)) {
		if (true) {
		if (!newpassword.equals("")) {
			if (newpassword.equals(reeneteredpassword)) {
				if(!oldpassword.equals(newpassword)) {
					new CSClient().updatePassword(oldpassword, newpassword);
				} else {
					Toast.makeText(ResetPasswordActivity.this, "New Password shouldn't match with Old Password", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(ResetPasswordActivity.this, "New Password Doesn't match with Re Entered New Password", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(ResetPasswordActivity.this, "New Password shouldn't be empty", Toast.LENGTH_SHORT).show();
		}
	} else {
		//System.out.println("old pass:"+oldpassword);
		Toast.makeText(ResetPasswordActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
	}
} else {
	Toast.makeText(ResetPasswordActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
}


			} catch (Exception ex) {
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



	userImage.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			//popupusertoselectimagesource();
			showoptions();
		}
	});
*/



} catch (Exception ex) {

}
	}

	/*
	public boolean creategetsimpleFile(String Path, String Filename) {
		try {
			File file = new File(Path + Filename);

			if (!file.exists()) {
				file.createNewFile();
				FileOutputStream stream = new FileOutputStream(file);
				stream.write("Test".getBytes(Charset.forName("UTF-8")));
				stream.close();
			} else {

			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
*/

	private void hideKeyboard() {
		try {
			View view = this.getCurrentFocus();
			if (view != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		} catch (Exception ex) {}

	}

	public class MainActivityReceiver extends BroadcastReceiver
	{

		@Override
	    public void onReceive(Context context, Intent intent)
	    {
	    	try {

	    		LOG.info("Yes Something receieved in RecentReceiver");
	    		if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
	    			//updateUI(CSEvents.CSCLIENT_NETWORKERROR);
	    		} else if(intent.getAction().equals(CSEvents.CSCLIENT_UPDATEPASSWORD_RESPONSE)) {

					if(intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
						LOG.info("update password success");
						Toast.makeText(ResetPasswordActivity.this, "Password Successfully updated", Toast.LENGTH_SHORT).show();
						onBackPressed();
					} else {
						Toast.makeText(ResetPasswordActivity.this, "Update Password Failed", Toast.LENGTH_SHORT).show();

					}

				}



	    	} catch(Exception ex){}
	    }
	    }
	MainActivityReceiver MainActivityReceiverObj = new MainActivityReceiver();
	@Override
	public void onResume() {
		super.onResume();

		try{

			MainActivityReceiverObj =  new MainActivityReceiver();
			IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
			IntentFilter filter1 = new IntentFilter(CSEvents.CSCLIENT_UPDATEPASSWORD_RESPONSE);

			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter1);

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



		} catch(Exception ex) {}

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
	 } catch(Exception ex) {}


	        }

	 @Override
		public void onBackPressed() {
			super.onBackPressed();
			finish();
			return;
		}

}
