package com.cavox.receivers;


import android.util.Log;

import com.ca.wrapper.CSClient;
import com.cacore.services.CACommonService;
import com.cavox.konverz.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static int count = 0;
    CSClient csClient = new CSClient();
    @Override
    public void onCreate() {
        super.onCreate();
        // register and un register receiver
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {
            Log.e(TAG, "Message Type: " + remoteMessage);
            //Log.e(TAG, "Notification Object:" + remoteMessage.getNotification());
            MainActivity.context = getApplicationContext();

            csClient.processPushMessage(getApplicationContext(), remoteMessage);


            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.e("JSON_OBJECT", object.toString());



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.i("FireBaseInstance", "Refreshed Token: " + mToken);
        csClient.processInstanceID(getApplicationContext(),mToken);
}

}


