package com.cavox.receivers;

import com.cavox.konverz.MainActivity;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.utils;
import com.ca.wrapper.CSCall;
import com.ca.wrapper.CSClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.cavox.utils.GlobalVariables.LOG;

public class CSCallReceiver extends BroadcastReceiver
{
	//CSCall CSCallsObj = new CSCall();
	//CSClient CSClientObj = new CSClient();
    @Override
    public void onReceive(final Context context, final Intent intent)
    {

MainActivity.context = context;
		LOG.info("App incallcount from app:"+GlobalVariables.incallcount);
    	if(GlobalVariables.incallcount>0) {
			utils.startCall(context,intent,true);
		} else {
			utils.startCall(context,intent,false);
		}


    } 










}