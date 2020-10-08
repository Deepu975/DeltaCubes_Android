package com.cavox.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import com.ca.Utils.CSConstants;
import com.ca.wrapper.CSCall;
import com.cavox.konverz.CallScreenActivity;
import com.cavox.konverz.MainActivity;
import com.cavox.utils.PreferenceProvider;

import static com.cavox.utils.GlobalVariables.LOG;

public class DismissCallNotificationHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       try {
           MainActivity.context = context.getApplicationContext();
           LOG.info("EndtheCallNotificationHandler");
           String callType = intent.getStringExtra("callType");

           CSCall CSCallsObj = new CSCall();
           if (callType.equals("video")) {
               //LOG.info("Test here call id:"+callid);

               CSCallsObj.endVideoCall(intent.getStringExtra("srcnumber"), intent.getStringExtra("callid"), CSConstants.E_UserBusy, CSConstants.UserBusy);

           } else if (callType.equals("audio")) {

               CSCallsObj.endVoiceCall(intent.getStringExtra("srcnumber"), intent.getStringExtra("callid"), CSConstants.E_UserBusy, CSConstants.UserBusy);
               //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber, 1, callid,CSClientObj.getTime());
           } else {

               CSCallsObj.endPstnCall(intent.getStringExtra("srcnumber"), intent.getStringExtra("callid"));
               //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber, 1, callid,CSClientObj.getTime());
           }


           PreferenceProvider preferenceProvider = new PreferenceProvider(MainActivity.context);
           preferenceProvider.remove(intent.getStringExtra("callid") + " incallnotification");

           if(CallScreenActivity.ringtone != null&&CallScreenActivity.ringtone.isPlaying()) {
               CallScreenActivity.ringtone.stop();
               CallScreenActivity.ringtone = null;
           }


       } catch (Exception ex) {
           ex.printStackTrace();
       }
       }
}
