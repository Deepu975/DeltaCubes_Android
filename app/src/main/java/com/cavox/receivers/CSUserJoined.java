package com.cavox.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.format.DateUtils;

import com.cavox.konverz.MainActivity;
import com.cavox.utils.utils;
import com.ca.wrapper.CSClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cavox.utils.GlobalVariables.LOG;

public class CSUserJoined extends BroadcastReceiver
{
	CSClient CSClientObj = new CSClient();
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
		MainActivity.context = context.getApplicationContext();
		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
    	try {
			//MainActivity.context = context.getApplicationContext();
			LOG.info("CSUserJoined Received:"+intent.getStringExtra("name"));
            String name = intent.getStringExtra("name");
            String number = intent.getStringExtra("number");

            if(name.equals("")) {
                name = number;
            }
	utils.notifycallinprogress1(context, "New User Joined",name, "", "",  0);

		} catch(Exception ex){
			utils.logStacktrace(ex);
		}
			}
		}).start();
    }
}