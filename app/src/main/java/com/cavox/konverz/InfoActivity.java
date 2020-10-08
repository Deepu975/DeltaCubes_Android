package com.cavox.konverz;


import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ListView;
import android.widget.TextView;

import com.app.deltacubes.R;
import com.ca.wrapper.CSClient;

import com.cavox.utils.utils;

public class InfoActivity extends AppCompatActivity
{
	String managecontactnumber = "";
	String managedirection = "";

	ListView mListView;
	Toolbar toolbar;
	//FloatingActionButton fab;
	//TextView subtitle;
	CSClient CSClientObj = new CSClient();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infoactivity);
		try {

			//toolbar = (Toolbar) findViewById(R.id.toolbar);
			//setSupportActionBar(toolbar);
			//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView sdkversion = (TextView) findViewById(R.id.textView7);
            TextView appversion = (TextView) findViewById(R.id.textView14);
CSClient CSClientObj = new CSClient();
			sdkversion.setText("SDK Version: "+CSClientObj.getVersion());
			appversion.setText("App Version: "+this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

			/*
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();

				}
			});
			*/


		} catch(Exception ex){utils.logStacktrace(ex);}
	}



	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}



}
