package com.cavox.konverz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import com.app.deltacubes.R;
import com.ca.wrapper.CSDataProvider;
import com.cavox.utils.utils;

import static com.cavox.utils.GlobalVariables.LOG;


public class SplashActivity extends Activity
	 {



		 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.info("SplashActivity oncreate before setContentView");
	    setContentView(R.layout.activity_splash);

		 MainActivity.context = getApplicationContext();


		//String isAlreadySignedup = CSDataProvider.getSignUpstatus();
LOG.info("SplashActivity oncreate after setContentView");
		if (CSDataProvider.getSignUpstatus()) {
			startActivity(new Intent(getApplicationContext(),  MainActivity.class));
/*
			Intent intent = new Intent(getApplicationContext(), ChatAdvancedActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NUMBER, "+919492084600");
			intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NAME, "A");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			getApplicationContext().startActivity(intent);
			*/
		} else {
			//startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
			startActivity(new Intent(getApplicationContext(),  StartActivity.class));
		}
		finish();
}
	
	

	
	@Override
	public void onBackPressed() {
	}
	
	

	
	@Override
	public void onResume() {
		super.onResume();
	  return;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		return;
		
	}

	/*
		 @Override
		 protected void attachBaseContext(Context base) {
			 super.attachBaseContext(base);
			 MultiDex.install(this);
		 }
	*/
}
