package com.cavox.konverz;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.multidex.MultiDex;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.app.deltacubes.R;
import com.ca.Utils.CSEvents;
import com.ca.wrapper.CSClient;
import com.cavox.utils.PreferenceProvider;

public class StartActivity extends AppCompatActivity
{

	private Toolbar toolbar;
	NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_layout);

		try {


			Button signup = findViewById(R.id.signUpButton);
			Button login =findViewById(R.id.loginButton);

			if(getIntent().getStringExtra("reset")!=null) {
				if (getIntent().getStringExtra("reset").equals("yes")) {
					new CSClient().reset();
				}
			}

			notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
			pf.setPrefboolean("isLogin", false);
			signup.setOnClickListener(new View.OnClickListener() {


				public void onClick(View v) {
					try {
						Intent intentt = new Intent(getApplicationContext(), SignUpActivity.class);
						startActivityForResult(intentt,638);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}


			});

			login.setOnClickListener(new View.OnClickListener() {


				public void onClick(View v) {
					try {
						Intent intentt = new Intent(getApplicationContext(), DirectLoginActivity.class);
						startActivityForResult(intentt,639);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}


			});

		} catch(Exception ex){}

	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


System.out.println("Yes onActivityResult:"+requestCode);


		if(requestCode == 638||requestCode ==639){
			PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
			boolean register = pf.getPrefBoolean("isLogin");
			System.out.println("Yes onActivityResult register:"+register);
			if (register) {
				startActivity(new Intent(getApplicationContext(),MainActivity.class));
			finish();
			} else {
				}

		}
	}



	public class MainActivityReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			try {

				System.out.println("Yes Something receieved in RecentReceiver");
				if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {

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

			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter);
			notificationManager.cancelAll();

		} catch(Exception ex) {}

	}
	@Override
	public void onPause() {
		super.onPause();

		try {
			LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
		} catch(Exception ex) {}


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


}
