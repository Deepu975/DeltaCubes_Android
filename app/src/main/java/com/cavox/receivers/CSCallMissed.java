package com.cavox.receivers;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Looper;

import androidx.core.content.ContextCompat;
import android.text.format.DateUtils;

import com.cavox.konverz.CallScreenActivity;
import com.cavox.konverz.GetReadPhoneStatePermission;
import com.cavox.konverz.MainActivity;
import com.cavox.utils.PreferenceProvider;
import com.cavox.utils.utils;
import com.ca.wrapper.CSClient;

import java.text.SimpleDateFormat;
import java.util.Date;


import static com.cavox.utils.GlobalVariables.LOG;

public class CSCallMissed extends BroadcastReceiver {
    CSClient CSClientObj = new CSClient();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    MainActivity.context = context.getApplicationContext();
                    LOG.info("CSCallMissed Received:" + intent.getStringExtra("name"));
                    long time = intent.getLongExtra("time", new Date().getTime());
                    String calltime = "";
                    if (DateUtils.isToday(time)) {
                        calltime = "Today " + new SimpleDateFormat("hh:mm a").format(time);
                    } else if (utils.isYesterday(time)) {
                        calltime = "Yesterday " + new SimpleDateFormat("hh:mm a").format(time);
                    } else {
                        calltime = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(time);
                    }
                    String calldirection = intent.getStringExtra("direction");

                    LOG.info("CSCallMissed Received name before broadcast:" + intent.getStringExtra("name"));
                    utils.notifyCallMissed(context, intent.getStringExtra("number"), intent.getStringExtra("name"), intent.getStringExtra("callid"), calldirection, calltime, 0);


                    if(ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                        Intent contactPickerIntent = new Intent(context.getApplicationContext(), GetReadPhoneStatePermission.class);
                        contactPickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(contactPickerIntent);
                    }

                    PreferenceProvider preferenceProvider = new PreferenceProvider(MainActivity.context);
                    int notiid = preferenceProvider.getPrefInt(intent.getStringExtra("callid") + " incallnotification", 0);
                    if(preferenceProvider.getPrefInt(intent.getStringExtra("callid") + " incallnotification")!=0) {
                        if(CallScreenActivity.ringtone != null&&CallScreenActivity.ringtone.isPlaying()) {

                            CallScreenActivity.ringtone.stop();
                            CallScreenActivity.ringtone = null;
                        }
                    }
                    preferenceProvider.remove(intent.getStringExtra("callid") + " incallnotification");
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(notiid);

                } catch (Exception ex) {
                    utils.logStacktrace(ex);
                }
            }
        }).start();
    }

}