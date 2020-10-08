package com.cavox.konverz;


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
import com.app.deltacubes.R;
import com.ca.Utils.CSEvents;
import com.ca.wrapper.CSDataProvider;
import com.cavox.utils.App;

import static com.cavox.utils.GlobalVariables.LOG;

public class EmptyActivity extends AppCompatActivity
{

	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty_layout);

		try {


			/*toolbar = (Toolbar) findViewById(R.id.toolbar);
			toolbar.setTitle("Groups");

			setSupportActionBar(toolbar);

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/

			LOG.info("stack count in Empty Activity:"+App.getActivityStackCount());
			int stackcount = App.getActivityStackCount();

if(stackcount == 0 && getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("chatnotification")) {
	if (CSDataProvider.getSignUpstatus()) {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	} else {
		startActivity(new Intent(getApplicationContext(), StartActivity.class));
	}
}
		finish();
		} catch(Exception ex){
			ex.printStackTrace();
		}

	}

	public void updateUI(String str) {

		try {
			if(str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
				LOG.info("NetworkError receieved");
				//Toast.makeText(this, "NetworkError", Toast.LENGTH_SHORT).show();
			}

		} catch(Exception ex){}
	}




	public class MainActivityReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			try {

				LOG.info("Yes Something receieved in RecentReceiver");
				if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
					updateUI(CSEvents.CSCLIENT_NETWORKERROR);
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

	        //LOG.info("SR_TEMP_LOG:exited");
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
