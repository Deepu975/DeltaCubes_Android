package com.cavox.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Looper;
import android.text.format.DateUtils;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.cavox.konverz.MainActivity;
import com.cavox.utils.utils;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.cavox.utils.GlobalVariables.LOG;

public class CSGroupNotificationReceiver extends BroadcastReceiver
{
	CSClient CSClientObj = new CSClient();
    @Override
    public void onReceive(final Context context, final Intent intent)
    {

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
    	try {
            MainActivity.context = context.getApplicationContext();


/*
            if(MainActivity.context == null) {
                LOG.info("MainActivity.context is null in receiver");
            } else {
                LOG.info("MainActivity.context is not null in receiver");
            }
*/
            LOG.info("CSGroupNotificationReceiver Received:" + intent.getStringExtra("grpid"));
            int notificationtype = intent.getIntExtra("reqcode",0);
            String grpid = intent.getStringExtra("grpid");
            String displaystring = intent.getStringExtra("displaystring");

            if(notificationtype == CSConstants.E_DELETED_FROM_GROUP_RQT || notificationtype == CSConstants.E_GROUP_DELETED_TO_GROUP_RQT) {
                String deletedgrpname = intent.getStringExtra("grpname");
    //utils.notifycallinprogress(context, displaystring, deletedgrpname, grpid,"",0);
                utils.notifygroupnotification(context, displaystring, deletedgrpname, grpid);
} else {

     String grpname = "";
    Cursor cur = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, grpid);
    if (cur.getCount() > 0) {
        cur.moveToNext();
        grpname = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_NAME));
    }
    cur.close();

    ArrayList numberslist = intent.getStringArrayListExtra("numberslist");
    if (numberslist == null) {
        utils.notifygroupnotification(context, displaystring, grpname, grpid);
    } else {
        //for (int i = 0; i < numberslist.size(); i++) {
            utils.notifygroupnotification(context, displaystring, grpname, grpid);
        //}
    }
}
		} catch(Exception ex){
			utils.logStacktrace(ex);
		}
            }
        }).start();
    } 

}