package com.cavox.konverz;


import android.content.Context;
import android.os.Bundle;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.app.deltacubes.R;
import com.ca.Utils.CSDbFields;
import com.cavox.adapaters.MessageinfoAdapter;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;

import static com.cavox.utils.GlobalVariables.LOG;

public class GroupMessageInfoActivity extends AppCompatActivity
{

	String chatid = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupmessageinfo);

		try {
			ListView mListView = (ListView) findViewById(R.id.messageinfolistview);
//			LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			toolbar.setTitle("Message info");

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

			chatid = getIntent().getStringExtra("chatid");
			LOG.info("chatid to process:"+chatid);

			//MatrixCursor psuedoGroups = new MatrixCursor(new String[] { CSDbFields.KEY_ID, CSDbFields.KEY_GROUP_CHAT_CHATID,CSDbFields.KEY_GROUP_CHAT_DESTINATION_NUMBER,CSDbFields.KEY_GROUP_CHAT_DELIVERED_TIME,CSDbFields.KEY_GROUP_CHAT_READ_TIME});
			//psuedoGroups.newRow().add(0).add("All Contacts");
			//psuedoGroups.newRow().add(1).add("Favorites");




			MessageinfoAdapter appContactsAdapter = new MessageinfoAdapter(MainActivity.context, CSDataProvider.getGroupChatMessageInfoCursorByFilter(CSDbFields.KEY_GROUP_CHAT_CHATID,chatid), 0);
			mListView.setAdapter(appContactsAdapter);


/*
			LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View child = vi.inflate(R.layout.contact_row_layout4, null);
			TextView textView = (TextView) child.findViewById(R.id.text1);
			textView.setText("your text");
			layout.addView(child);

			mListView.addFooterView(findViewById(R.id.layout));*/
		} catch(Exception ex){
			utils.logStacktrace(ex);
		}

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
