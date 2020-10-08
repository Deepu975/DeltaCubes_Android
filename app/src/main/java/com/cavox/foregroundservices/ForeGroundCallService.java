package com.cavox.foregroundservices;
import android.app.Notification;
        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
        import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.app.deltacubes.R;
import com.cavox.konverz.EmptyActivity;
import com.cavox.utils.GlobalVariables;

import java.util.ArrayList;


public class ForeGroundCallService extends Service {
    public static final String CHANNEL_ID = "ForeGroundServiceCallNotifications";
    CharSequence name = "Konverz";
    String description = "ForeGroundServiceNotifications Channel";



    private final IBinder mBinder = new ForeGroundCallServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            //String entityId = CHANNEL_ID;
            int notificationid = 100;
            createNotificationChannel(getApplicationContext());

        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
/*
            ArrayList<String> messageList = new ArrayList<>();
            messageList.add(title+":"+description);
            String[] notificationMessages = messageList.toArray(new String[messageList.size()]);
            */
            Intent emptyActivityIntent = new Intent(getApplicationContext(), EmptyActivity.class);
            emptyActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emptyActivityIntent.setAction(Long.toString(System.currentTimeMillis()));

            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, emptyActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder noti = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    //Notification noti = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(description)
                    //.setSound(uri)
                    //.setOnlyAlertOnce(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti.setSmallIcon(R.drawable.ic_notification_transaperent);
                noti.setColor(getApplicationContext().getResources().getColor(R.color.theme_color));
            } else {
                noti.setSmallIcon(R.mipmap.ic_launcher);
            }

            /*NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            for (int i = notificationMessages.length - 1; i >= 0; i--) {
                inboxStyle.addLine(notificationMessages[i]);
            }
            noti.setStyle(inboxStyle);
            noti.setNumber(notificationMessages.length);
*/

            //notificationManager.notify(entityId,notificationid, noti.build());



        startForeground(notificationid, noti.build());


        //do heavy work on a background thread

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalVariables.LOG.info("ForeGroundCallService onDestroy called");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ForeGroundCallServiceBinder extends Binder {
        ForeGroundCallService getService() {
            return ForeGroundCallService.this;
        }
    }


    private void createNotificationChannel(Context context) {

        try {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}