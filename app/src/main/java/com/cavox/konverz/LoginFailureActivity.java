package com.cavox.konverz;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.app.deltacubes.R;
import com.ca.wrapper.CSClient;

import java.io.File;

import static com.cavox.utils.GlobalVariables.LOG;

public class LoginFailureActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginfailure);

		try {
			Button continuebutton = (Button) findViewById(R.id.button_continue);
			Button cancelbutton = (Button) findViewById(R.id.cancel_button);
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			toolbar.setTitle("Konverz");
			toolbar.setSubtitle("Registration failure alert!");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		continuebutton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						CSClient CSClientObj = new CSClient();
						boolean resetstatus = CSClientObj.reset();
						LOG.info("resetstatus:"+resetstatus);

						NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
						notificationManager.cancelAll();

						Intent intent = new Intent(getApplicationContext(), StartActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent);
						finish();

					} catch (Exception ex) {
						Toast.makeText(LoginFailureActivity.this, "Error", Toast.LENGTH_SHORT).show();
					}
				}
			});
			cancelbutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
finish();
					} catch (Exception ex) {

					}
				}
			});
		} catch(Exception ex){}

	}



	public void clearApplicationData() {
		try {
		File cacheDirectory = getCacheDir();
		File applicationDirectory = new File(cacheDirectory.getParent());
		if (applicationDirectory.exists()) {
			String[] fileNames = applicationDirectory.list();
			for (String fileName : fileNames) {
				if (!fileName.equals("lib")) {
					LOG.info("fileName to delete:"+fileName);
					deleteFile(new File(applicationDirectory, fileName));
				}
			}
		}
		} catch(Exception ex){}
	}

	public static boolean deleteFile(File file) {
		boolean deletedAll = true;
		try {
		if (file != null) {
			if (file.isDirectory()) {
				String[] children = file.list();
				for (int i = 0; i < children.length; i++) {
					deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
				}
			} else {
				deletedAll = file.delete();
			}
		}
	} catch(Exception ex){}
		return deletedAll;
	}








	@Override
	public void onResume() {
		super.onResume();


	}
	@Override
	public void onPause() {
		super.onPause();

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
