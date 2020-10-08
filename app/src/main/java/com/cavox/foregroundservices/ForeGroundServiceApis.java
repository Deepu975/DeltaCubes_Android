package com.cavox.foregroundservices;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;

public class ForeGroundServiceApis {

    public void startCallService(Context context,String title,String description) {
try {
    Intent i = new Intent(context, ForeGroundCallService.class);
    i.putExtra("title", title);
    i.putExtra("description", description);
    context.startService(i);
} catch (Exception ex) {
    ex.printStackTrace();
}

       /* // bind to the service.
        bindService(new Intent(this,
                CustomService.class), mConnection, Context.BIND_AUTO_CREATE);
                */
    }


    public void stopCallService(Context context) {
        try {
            Intent intent = new Intent(context, ForeGroundCallService.class);
            context.stopService(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

       /* // bind to the service.
        bindService(new Intent(this,
                CustomService.class), mConnection, Context.BIND_AUTO_CREATE);
                */
    }



    public void startChatService(Context context, String title, String description, ArrayList<String> messageList) {
        try {
            Intent i = new Intent(context, ForeGroundChatService.class);
            i.putExtra("title", title);
            i.putExtra("description", description);
            i.putStringArrayListExtra("messageList",messageList);
            context.startService(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

       /* // bind to the service.
        bindService(new Intent(this,
                CustomService.class), mConnection, Context.BIND_AUTO_CREATE);
                */
    }


    public void stopChatService(Context context) {
        try {
            Intent intent = new Intent(context, ForeGroundChatService.class);
            context.stopService(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

       /* // bind to the service.
        bindService(new Intent(this,
                CustomService.class), mConnection, Context.BIND_AUTO_CREATE);
                */
    }


}


