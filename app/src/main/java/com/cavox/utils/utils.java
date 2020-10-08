package com.cavox.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.core.app.NotificationCompat;
import androidx.loader.content.CursorLoader;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.wrapper.CSCall;
import com.app.deltacubes.R;
import com.ca.wrapper.CSChat;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;
import com.cavox.konverz.CallScreenActivity;
import com.cavox.konverz.ChatAdvancedActivity;
import com.cavox.konverz.EmptyActivity;
import com.cavox.konverz.GroupChatAdvancedActivity;
import com.cavox.konverz.MainActivity;
import com.cavox.konverz.ManageGroupActivity;
import com.cavox.konverz.PlayNewAudioCallActivity;
import com.cavox.konverz.PlayNewVideoCallActivity;
import com.cavox.konverz.PlaySipCallActivity;
import com.cavox.konverz.ShowUserLogActivity;
import com.cavox.receivers.DismissCallNotificationHandler;
import com.cavox.receivers.InComingCallHandlingReceiver;
import com.cavox.receivers.MissedCallNotificationHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


import static com.cavox.utils.GlobalVariables.LOG;

public class utils {

    private static int notificationId = 3;

    public static int notifyUserChat1(Context context, String userdisplaystring, String channelname, String channelid, String channelrole, int reqcode) {

        Random rand = new Random();
        int notificationid = rand.nextInt(1000001);
        try {

            LOG.info("I am in NotifyChat" + channelid);
/*
            Intent intent = new Intent(context, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NUMBER, channelid);
            intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NAME, userdisplaystring);
            intent.setAction(Long.toString(System.currentTimeMillis()));
*/
            Intent intent = new Intent(context, ChatAdvancedActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Sender", channelid);
            intent.putExtra("IS_GROUP", false);
            intent.setAction(Long.toString(System.currentTimeMillis()));

            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


/*
			Notification noti = new Notification.Builder(context)
					.setContentTitle(userdisplaystring)
					.setContentText(channelname)
					.setSound(uri)
					.setSmallIcon(R.mipmap.ic_launcher)
					.setContentIntent(pIntent).build();
*/
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                    .setContentTitle(userdisplaystring) // title for notification
                    .setContentText(channelname) // message for notification
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(uri)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setCategory(Notification.CATEGORY_MESSAGE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.ic_notification_transaperent);
                mBuilder.setColor(context.getResources().getColor(R.color.theme_color));
            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //noti.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(notificationid, mBuilder.build());

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
        return notificationid;
    }

    public static int notifyIncomigCall(Context context, String conatctName, String callType, boolean secondcall) {
        int notificationId = 5;
        createNotificationChannel(context);
        Intent intent = new Intent(context, EmptyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Long.toString(System.currentTimeMillis()));


        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentAction = new Intent(context, InComingCallHandlingReceiver.class);
        intentAction.setAction("EndCall");
        PendingIntent endCallIntent = PendingIntent.getBroadcast(context, 1, intentAction, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentAction1 = new Intent(context, InComingCallHandlingReceiver.class);
        intentAction1.setAction("AnswerCall");
        PendingIntent answerCallIntent = PendingIntent.getBroadcast(context, 1, intentAction1, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "Default")
                .setContentTitle(conatctName)
                .setContentText(callType)
                .addAction(0, "Decline", endCallIntent)
                .addAction(0, "Answer", answerCallIntent)
                .setOngoing(true)
                .setContentIntent(pIntent)
                .setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            noti.setSmallIcon(R.drawable.ic_notification_transaperent);
            noti.setColor(context.getResources().getColor(R.color.theme_color));
        } else {
            noti.setSmallIcon(R.mipmap.ic_launcher);
        }
        if (secondcall) {
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            inboxStyle.addLine("On Clicking Answer any existing ");
            inboxStyle.addLine("call/s will be disconnected.");
            noti.setStyle(inboxStyle);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, noti.build());
        return notificationId;
    }

    public static int notifyUserChat1(Context context, String userdisplaystring, String channelname, String channelid, int isgroupmessage, String name) {
        Log.i("util", "notifyUserChat: " + channelid + " " + isgroupmessage + " " + name);
        Random rand = new Random();
        PreferenceProvider preferenceProvider = new PreferenceProvider(context);

        int notificationid = 0;
        try {
            if (isgroupmessage == 0) {
                notificationid = Integer.parseInt(channelid.substring((channelid.length() / 2), channelid.length()).replace("+", ""));

            } else {
                notificationid = preferenceProvider.getPrefInt(name);
                if (notificationid == 0) {
                    notificationid = rand.nextInt(1000001);
                    preferenceProvider.setPrefint(name, notificationid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            String notificationMessage = "";
            if (isgroupmessage == 0) {
                notificationMessage = preferenceProvider.getPrefString(channelid);
                if (notificationMessage.equals("")) {
                    notificationMessage = channelname;
                } else {
                    notificationMessage = notificationMessage + "|" + channelname;
                }
                preferenceProvider.setPrefString(channelid, notificationMessage);
            } else {
                notificationMessage = preferenceProvider.getPrefString(channelid);
                if (notificationMessage.equals("")) {
                    notificationMessage = channelname;
                } else {
                    notificationMessage = notificationMessage + "|" + channelname;
                }
                preferenceProvider.setPrefString(channelid, notificationMessage);
            }
            String[] notificationMessages = notificationMessage.split("\\|");


            createNotificationChannel(context);
            LOG.info("I am in NotifyChat" + channelid);
            Intent intent = new Intent();
            //Cursor ccr = CSDataProvider.getChatCursorFilteredByNumberAndUnreadMessages(channelid);
            //int unreadCount = ccr.getCount();
            if (isgroupmessage == 0) {
                /*
                intent = new Intent(context, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NUMBER, channelid);
                intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NAME, userdisplaystring);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(Long.toString(System.currentTimeMillis()));
                */
                intent = new Intent(context, ChatAdvancedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("Sender", channelid);
                intent.putExtra("IS_GROUP", false);
                intent.setAction(Long.toString(System.currentTimeMillis()));
            } else {
                intent = new Intent(context, GroupChatAdvancedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Sender", channelid);
                intent.putExtra("grpname", name);
                intent.putExtra("IS_GROUP", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(Long.toString(System.currentTimeMillis()));
            }


            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "Default")
                    //Notification noti = new Notification.Builder(context)
                    .setContentTitle(userdisplaystring)
                    .setContentText(channelname)
                    .setSound(uri)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti.setSmallIcon(R.drawable.ic_notification_transaperent);
                noti.setColor(context.getResources().getColor(R.color.theme_color));
            } else {
                noti.setSmallIcon(R.mipmap.ic_launcher);
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            for (int i = notificationMessages.length - 1; i >= 0; i--) {
                inboxStyle.addLine(notificationMessages[i]);
            }
            noti.setStyle(inboxStyle);
            noti.setNumber(notificationMessages.length);
            // hide the notification after its selected
            //noti.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(notificationid, noti.build());

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
        return notificationid;
    }



    public static int notifyUserChat(Context context, String destinationid, String[] notificationMessages, int isgroupmessage,  String title,String lastfinalmessage) {
        Log.i("util", "notifyUserChat: " + destinationid + " " + isgroupmessage + " " );

        String entityId = destinationid;
        //int notificationid = new Random().nextInt(1000001);
        int notificationid = 1;



        try {



            createNotificationChannel(context);
            LOG.info("I am in NotifyChat" + destinationid);
            Intent intent = new Intent();

            if (isgroupmessage == 0) {
                intent = new Intent(context, ChatAdvancedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Sender", destinationid);
                intent.putExtra("IS_GROUP", false);
                intent.setAction(Long.toString(System.currentTimeMillis()));
            } else {
                intent = new Intent(context, GroupChatAdvancedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Sender", destinationid);
                intent.putExtra("grpname", title);
                intent.putExtra("IS_GROUP", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(Long.toString(System.currentTimeMillis()));
            }


            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "Default")
                    //Notification noti = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(lastfinalmessage)
                    .setSound(uri)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti.setSmallIcon(R.drawable.ic_notification_transaperent);
                noti.setColor(context.getResources().getColor(R.color.theme_color));
            } else {
                noti.setSmallIcon(R.mipmap.ic_launcher);
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            for (int i = notificationMessages.length - 1; i >= 0; i--) {
                inboxStyle.addLine(notificationMessages[i]);
            }
            noti.setStyle(inboxStyle);
            noti.setNumber(notificationMessages.length);

            notificationManager.notify(entityId,notificationid, noti.build());



        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
        return notificationid;
    }











    public static int notifyCallMissed(Context context, String number, String name, String callid, String calldirection, String calltime, int reqcode) {
        Random rand = new Random();
        PreferenceProvider preferenceProvider = new PreferenceProvider(context);

        int notificationid = 0;
        try {
            {
                notificationid = preferenceProvider.getPrefInt(number + "Missed");
                if (notificationid == 0) {
                    notificationid = rand.nextInt(1000001);
                    preferenceProvider.setPrefint(number + "Missed", notificationid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      //  Log.i(TAG, "notifyCallMissed: " + isNotificationVisible(context, notificationid));
        try {
            createNotificationChannel(context);
            //LOG.info("I am in notifyCallMissed" + name + " direction " + calldirection);

            Intent intent = new Intent(context, MissedCallNotificationHandler.class);
            intent.putExtra("number", number);
            intent.putExtra("name", name);
            intent.putExtra("id", callid);
            intent.putExtra("direction", calldirection);
            Intent intent1 = new Intent(context, ShowUserLogActivity.class);
            intent1.putExtra("number", number);
            intent1.putExtra("name", name);
            intent1.putExtra("id", callid);
            intent1.putExtra("direction", calldirection);
            LOG.info("I am in userdisplaystring name" + name);
            String userdisplaystring = "";
            if (name.equals("")) {
                LOG.info("yes name is null" + number);
                userdisplaystring = number;
            } else {
                userdisplaystring = name;
            }
            LOG.info("I am in userdisplaystring" + userdisplaystring);

            String mycalldirection = "";
            if (calldirection.equals(CSConstants.MISSED_VIDEO_CALL)) {
                mycalldirection = "missed video call";
            } else {
                mycalldirection = "missed audio call";
            }
            String description = mycalldirection;

            String notificationMessage = "";
            {
                notificationMessage = preferenceProvider.getPrefString(number + "MissedData");
                if (notificationMessage.equals("")) {
                    notificationMessage = mycalldirection;
                } else {
                    notificationMessage = notificationMessage + "|" + mycalldirection;
                }
                preferenceProvider.setPrefString(number + "MissedData", notificationMessage);
            }
            String[] notificationMessages = notificationMessage.split("\\|");


            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pIntent1 = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Notification noti = new Notification.Builder(context)
            NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "Default")

                    .setContentTitle(userdisplaystring)
                    .setContentText(description)
                    .setSound(uri)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent1)
                    .setAutoCancel(true)
                    .setDeleteIntent(pIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // hide the notification after its selected
            //noti.flags |= Notification.FLAG_AUTO_CANCEL;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti.setSmallIcon(R.drawable.ic_notification_transaperent);
                noti.setColor(context.getResources().getColor(R.color.theme_color));
            } else {
                noti.setSmallIcon(R.mipmap.ic_launcher);
            }

            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            for (int i = notificationMessages.length - 1; i >= 0; i--) {
                inboxStyle.addLine(notificationMessages[i]);
            }
            noti.setStyle(inboxStyle);
            noti.setNumber(notificationMessages.length);

            noti.setStyle(inboxStyle);

            notificationManager.notify(notificationid, noti.build());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return notificationid;
    }

    public static int notifycallinprogress1(Context context, String calle, String description, String callee, String calltype, int reqcode) {

        Random rand = new Random();
        int notificationid = rand.nextInt(1000001);
        try {
            createNotificationChannel(context);
            LOG.info("I am in notifycallinprogress");

            Intent intent = new Intent(context, EmptyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Long.toString(System.currentTimeMillis()));


            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //  Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Notification noti = new Notification.Builder(context)
            NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "Default")
                    .setContentTitle(calle)
                    .setContentText(description)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti.setSmallIcon(R.drawable.ic_notification_transaperent);
                noti.setColor(context.getResources().getColor(R.color.theme_color));
            } else {
                noti.setSmallIcon(R.mipmap.ic_launcher);
            }
            // hide the notification after its selected
            Notification notification = noti.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(notificationid, notification);

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
        return notificationid;
    }
    public static int notifycallinprogress(Context context, String calle, String description, String callee, String calltype, int reqcode) {

        Random rand = new Random();
        int notificationid = rand.nextInt(1000001);
        try {
            createNotificationChannel(context);
            LOG.info("I am in notifycallinprogress");

            Intent intent = new Intent(context, EmptyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Long.toString(System.currentTimeMillis()));


/*
            Intent intent = new Intent(context, ManageGroupActivity.class);
            intent.putExtra("grpid", callee);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Long.toString(System.currentTimeMillis()));
*/

            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //  Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Notification noti = new Notification.Builder(context)
            NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "Default")
                    .setContentTitle(calle)
                    .setContentText(description)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti.setSmallIcon(R.drawable.ic_notification_transaperent);
                noti.setColor(context.getResources().getColor(R.color.theme_color));
            } else {
                noti.setSmallIcon(R.mipmap.ic_launcher);
            }
            // hide the notification after its selected
            Notification notification = noti.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;

            notificationManager.notify(notificationid, notification);

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
        return notificationid;
    }

    public static int notifygroupnotification(Context context, String displaystring, String grpname, String grpid) {

        Random rand = new Random();
        int notificationid = rand.nextInt(1000001);
        try {
            createNotificationChannel(context);
            LOG.info("I am in notifygroupnotification");

            Intent intent = new Intent(context, ManageGroupActivity.class);
            intent.putExtra("grpid", grpid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Long.toString(System.currentTimeMillis()));


            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder noti = new NotificationCompat.Builder(context, "Default")
                    .setContentTitle(displaystring)
                    .setContentText(grpname)
                    .setSound(uri)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // hide the notification after its selected
            //noti.flags |= Notification.FLAG_AUTO_CANCEL;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti.setSmallIcon(R.drawable.ic_notification_transaperent);
                noti.setColor(context.getResources().getColor(R.color.theme_color));
            } else {
                noti.setSmallIcon(R.mipmap.ic_launcher);
            }
            notificationManager.notify(notificationid, noti.build());

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
        return notificationid;
    }

    public static boolean isinternetavailable1(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();

            return activeNetworkInfo != null
                    && activeNetworkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isinternetavailable(Context context) {
        try {
            //LOG.info("test network place1");
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //LOG.info("test network place2");
             if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                 try {
                     //LOG.info("test network place3");
                     int exitValue = -1;
                     try {
                         Runtime runtime = Runtime.getRuntime();
                         Process ipProcess = runtime.exec("/system/bin/ping -c 1 -W 2 8.8.8.8");
                         exitValue = ipProcess.waitFor();
                         //LOG.info("test network place4");
                     } catch (Exception ex) {ex.printStackTrace();}

                     if (exitValue == 0) {
                         //LOG.info("test network place5");
                         return true;
                     } else {
                         //LOG.info("test network place6");
                         InetAddress ipAddr = InetAddress.getByName("google.com");
                         if(ipAddr.equals("")) {
                             //LOG.info("test network place7");
                             return false;
                         } else {
                             //LOG.info("test network place8");
                             return true;
                         }
                     }

                 } catch (Exception e) {
                     e.printStackTrace();
                     //LOG.info("test network place9");
                     return false;
                 }

            } else {
                 //LOG.info("test network place10");
                 return false;
             }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DeltaCubes";
            String description = "Default Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
    }

    private static void createIncallNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DeltaCubes";
            String description = "Default Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Android 10 Calls", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            channel.setLightColor(Color.GRAY);
            channel.enableLights(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            //channel.setSound(uri, audioAttributes);


            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
    }

    public static Bitmap getImage(byte[] image) {
        try {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        } catch (Exception ex) {
            //utils.logStacktrace(ex);
            return null;
        }
    }

    public static Bitmap loadContactPhoto(long id, Context context) {
        try {
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
            InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(
                    context.getContentResolver(), uri, true);
            return BitmapFactory.decodeStream(stream);
        } catch (Exception ex) {
            return null;
        }
    }


    public static Bitmap loadContactPhoto(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
            InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(
                    MainActivity.context.getContentResolver(), uri, true);
            return BitmapFactory.decodeStream(stream);
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isYesterday(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);
        now.add(Calendar.DATE, -1);
        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result = "";
        try {

            Cursor cursor = null;

            if (contentURI != null) {
                cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            }


            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {

                LOG.info("Test count:" + cursor.getCount());


                cursor.moveToFirst();

                //LOG.info("Test col1 name:"+cursor.getColumnName(0));
                //LOG.info("Test col2 name:"+cursor.getColumnName(1));

                //LOG.info("Test col1 data:"+cursor.getColumnIndex(cursor.getColumnName(0)));
                //LOG.info("Test col2 data:"+cursor.getColumnIndex(cursor.getColumnName(1)));

                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            result = getRealDocPathFromURI(context, contentURI);
        }
        return result;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
   /* @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
*/

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * This method checks whether given file exist or not in provided path. If
     * not exist, it will create file and directories even.
     *
     * @return file
     */

    public static String getRealDocPathFromURI(Context context, Uri imageUri) {
        String attachmentpath = "";
        try {
            String extension = "";
            ContentResolver cR = context.getContentResolver();
            if (imageUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                //If scheme is a content
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(imageUri));
            } else {
                //If scheme is a File
                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(imageUri.getPath())).toString());

            }

            String filename = "";
            try {

                Cursor cursor = context.getContentResolver().query(imageUri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception ex) {
                    logStacktrace(ex);
                } finally {
                    cursor.close();
                }


                //String file = imageUri.getPath();
                if (extension == null) {
                    int i = filename.lastIndexOf('.');
                    if (i > 0) {
                        extension = filename.substring(i + 1);
                    }
                }
            } catch (Exception ex) {
                logStacktrace(ex);
            }


            if (filename.equals("")) {
                if (extension == null) {
                    extension = "";
                }
                filename = "imgvid_" + String.valueOf(new Date().getTime()) + "." + extension;
            }


            InputStream input = context.getContentResolver().openInputStream(imageUri);
            CSChat CSChatObj = new CSChat();
            try {
                if (new File(utils.getSentImagesDirectory(), filename).exists()) {
                    String[] filenames = filename.split("\\.");
                    String t_name = filenames[0];
                    String t_extention = filenames[1];
                    filename = t_name + "_" + String.valueOf(new Date().getTime()) + "." + t_extention;
                }
            } catch (Exception ex) {
                logStacktrace(ex);
            }
            File file = new File(utils.getSentImagesDirectory(), filename);
            try {

                OutputStream output = new FileOutputStream(file);
                try {
                    try {
                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                        int read;

                        if (input != null) {
                            while ((read = input.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }
                        }
                        output.flush();
                    } finally {
                        output.close();
                        attachmentpath = file.getAbsolutePath();
                    }
                } catch (Exception e) {
                    logStacktrace(e); // handle exception, define IOException and others
                }

            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (Exception e) {
                    logStacktrace(e);
                }
            }
        } catch (Exception e) {
            logStacktrace(e);
        }
        return attachmentpath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public static boolean showSettingsAlert(String result, Activity callingactivity) {
        try {
            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(callingactivity);
            successfullyLogin.setMessage(result);
            successfullyLogin.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainActivity.context.startActivity(intent);
                        }
                    });
            successfullyLogin.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                        }
                    });

            successfullyLogin.show();

            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public static boolean copyorreplacefile(File src, File dst) {
        try {

            if(dst.exists()) {
                LOG.info("overwriting the file");
                dst.delete();
            } else {
                LOG.info("copying the file");
            }


            dst.createNewFile();

            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            return true;
        } catch(Exception ex) {
            //IAmLiveCore.logStacktrace(ex);
            return false;
        }
    }

    public static String getSentImagesDirectory() {

        try {


            String imagedirectorysent = "DeltaCubes" + "/Images/Sent";
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + imagedirectorysent;


			/*
				CSChat CSChatObj = new CSChat();
				System.out.println("Dir structure:"+CSChatObj.getDirectoryListing().toString());

				JSONObject json = CSChatObj.getDirectoryListing();
				Iterator<String> iter = json.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					System.out.println("key:"+key);
					try {
						Object value = json.get(key);
						System.out.println("value:"+value);
					} catch (Exception e) {
						// Something went wrong!
					}
				}
*/


        } catch (Exception ex) {
            return "";
        }
    }

    public static String getReceivedImagesDirectory() {

        try {

            String imagedirectoryreceived = "DeltaCubes" + "/Images/Received";
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + imagedirectoryreceived;

        } catch (Exception ex) {
            return "";
        }
    }
    public static boolean setFileTrasferPathsHelper() {
        try {



                 GlobalVariables.chatappname = MainActivity.context.getApplicationInfo().loadLabel(MainActivity.context.getPackageManager()).toString();


            GlobalVariables.imagedirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Images";
                GlobalVariables.imagedirectorysent = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Images/Sent";//used for location and doc
                GlobalVariables.imagedirectoryreceived = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Images/Received"; //used for location and thumbainal internally


                //video
                GlobalVariables.videodirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Videos";
                GlobalVariables.videodirectorysent = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Videos/Sent";//used for location and doc
                GlobalVariables.videodirectoryreceived = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Videos/Received"; //used for location and thumbainal internally

                //audio
                GlobalVariables.audiodirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Audios";
                GlobalVariables.audiodirectorysent = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Audios/Sent";//used for location and doc
                GlobalVariables.audiodirectoryreceived = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Audios/Received"; //used for location and thumbainal internally

                //Documents
                GlobalVariables.docsdirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Documents";
                GlobalVariables.docsdirectorysent = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Documents/Sent";//used for location and doc
                GlobalVariables.docsdirectoryreceived = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Documents/Received"; //used for location and thumbainal internally

                //profiles
                GlobalVariables.profilesdirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Profile Photos";
                //GlobalVariables.profilesdirectorysent = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname+"/Profile Photos/Sent";//used for location and doc
                //GlobalVariables.profilesdirectoryreceived = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname+"/Profile Photos/Received"; //used for location and thumbainal internally

                GlobalVariables.thumbnailsdirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Thumbnails";

                GlobalVariables.recordingsdirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ GlobalVariables.chatappname + "/Recordings";



        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;

    }

    public static boolean showSimpleAlert(String result, Activity callingactivity) {
        try {
            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(callingactivity);
            successfullyLogin.setMessage(result);

            successfullyLogin.setNegativeButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                        }
                    });

            successfullyLogin.show();

            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public static String getGroupname(String groupid) {
        String name = "";

        try {
            Cursor ccfr = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, groupid);

            //LOG.info("ccfr.getCount():"+ccfr.getCount());
            if (ccfr.getCount() > 0) {
                ccfr.moveToNext();
                name = ccfr.getString(ccfr.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_NAME));
            }
            ccfr.close();
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }

        return name;
    }

    public static boolean showIntentAlert(String result, Activity callingactivity, final Intent intent) {
        try {
            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(callingactivity);
            successfullyLogin.setMessage(result);

            successfullyLogin.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainActivity.context.startActivity(intent);

                        }
                    });
            successfullyLogin.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                        }
                    });
            successfullyLogin.show();
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public static void donewPstncall(String numbertodial, Activity callingactivity) {

        try {
            TelephonyManager telManager = (TelephonyManager) MainActivity.context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
                utils.showSimpleAlert("Couldn't place call while in native call.", callingactivity);
                return;
            }

            if (com.cavox.utils.GlobalVariables.incallcount <= 0) {

                LOG.info("Doing new outgoing pstn call");
                CSClient CSClientObj = new CSClient();
                if (CSClientObj.getLoginstatus()) {

                    CSClientObj.registerForPSTNCalls();//temp

                    if (!numbertodial.equals("") && !numbertodial.equals(GlobalVariables.phoneNumber)) {
                        Intent intent = new Intent(callingactivity, PlaySipCallActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("dstnumber", numbertodial);
                        intent.putExtra("isinitiatior", true);
                        callingactivity.startActivityForResult(intent, 956);
                    } else {
                        Toast.makeText(callingactivity, "No valid Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showSettingsAlert("Couldn't place call. Please check internet connection and try again", callingactivity);
                }
            } else {
                showSimpleAlert("Couldn't place another call while in call.", callingactivity);
            }
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    public static void donewVideocall(String numbertodial, Activity callingactivity) {

        try {
            //new CSCall().setIceTransportsType(CSConstants.IceTransportsType.RELAY);//for go4sip
            TelephonyManager telManager = (TelephonyManager) MainActivity.context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
                utils.showSimpleAlert("Couldn't place call while in native call.", callingactivity);
                return;
            }

            if (com.cavox.utils.GlobalVariables.incallcount <= 0) {
                LOG.info("Doing new outgoing video call");
                CSClient CSClientObj = new CSClient();
                if (CSClientObj.getLoginstatus()) {
                    if (!numbertodial.equals("") && !numbertodial.equals(GlobalVariables.phoneNumber)) {

                        Intent intent = new Intent(callingactivity, PlayNewVideoCallActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("dstnumber", numbertodial);
                        intent.putExtra("isinitiatior", true);
                        callingactivity.startActivityForResult(intent, 954);
                    } else {
                        Toast.makeText(callingactivity, "No valid Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showSettingsAlert("Couldn't place call. Please check internet connection and try again", callingactivity);
                }
            } else {
                showSimpleAlert("Couldn't place another call while in call.", callingactivity);
            }
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }


    public static void donewvoicecall(String numbertodial, Activity callingactivity) {

        try {
//new CSCall().setIceTransportsType(CSConstants.IceTransportsType.RELAY);//for go4sip
            TelephonyManager telManager = (TelephonyManager) MainActivity.context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
                utils.showSimpleAlert("Couldn't place call while in native call.", callingactivity);
                return;
            }


            if (com.cavox.utils.GlobalVariables.incallcount <= 0) {
                LOG.info("Doing new outgoing voice call");
                CSClient CSClientObj = new CSClient();
                if (CSClientObj.getLoginstatus()) {
                    if (!numbertodial.equals("") && !numbertodial.equals(GlobalVariables.phoneNumber)) {
                        Intent intent = new Intent(callingactivity, PlayNewAudioCallActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("dstnumber", numbertodial);
                        intent.putExtra("isinitiatior", true);
                        callingactivity.startActivityForResult(intent, 954);
                    } else {
                        Toast.makeText(callingactivity, "No valid Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utils.showSettingsAlert("Couldn't place call. Please check internet connection and try again", callingactivity);
                }
            } else {
                utils.showSimpleAlert("Couldn't place another call while in call.", callingactivity);
            }
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    public static void startCall(Context context, Intent intent, boolean secondcall) {
        try {
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    try {
                        //new CSCall().setIceTransportsType(CSConstants.IceTransportsType.RELAY);//for go4sip
                        MainActivity.context = context.getApplicationContext();
                        //if(!CSDataProvider.getUINotificationsMuteStatus()) {
                        //LOG.info("here1");
                        //MainActivity.context = context.getApplicationContext();
                        LOG.info("CSCallReceiver event receieved");
                        String sDstMobNu = intent.getStringExtra("sDstMobNu");
                        String callid = intent.getStringExtra("callid");
                        int callactive = intent.getIntExtra("callactive", 0);
                        //String sdp = intent.getStringExtra("sdp");
                        String callType = intent.getStringExtra("callType");
                        String srcnumber = intent.getStringExtra("srcnumber");
                        String didnumber = intent.getStringExtra("didnumber");
                        CSClient CSClientObj = new CSClient();
                        long callstarttime = intent.getLongExtra("callstarttime", CSClientObj.getTime());
/*
                        String calldirection = "";
                        Cursor cur = CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_CALLID, callid);
                        if (cur.getCount() > 0) {
                            cur.moveToNext();
                            calldirection = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_DIR));
                        }
                        cur.close();
*/

                        long presenttime = CSClientObj.getTime();
                        long timedifference = (presenttime - callstarttime) / 1000;
                        LOG.info("YES timedifference:" + timedifference);

                        if (timedifference > 50) {
                        //PreferenceProvider preferenceProvider = new PreferenceProvider(context);
                        //if (false && preferenceProvider.getPrefBoolean(PreferenceProvider.IS_CALL_IN_CALLING_STATE)) {


                            String Name = "";

                            Cursor contactcursor = CSDataProvider.getContactCursorByNumber(srcnumber);
                            if (contactcursor.getCount() > 0) {
                                contactcursor.moveToNext();
                                Name = contactcursor.getString(contactcursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
                            }
                            contactcursor.close();

                            if (Name.equals("")) {
                                Name = srcnumber;
                            }
                            String calldirection1 = "";
                            Log.i("Utils", "run: Start call retunring " + calldirection1);
                            CSCall CSCallsObj = new CSCall();
                            if (callType.equals("video")) {
                                //LOG.info("Test here call id:"+callid);
                                calldirection1 = "MISSED VIDEO CALL";
                                CSCallsObj.endVideoCall(srcnumber, callid, CSConstants.E_UserBusy, CSConstants.UserBusy);
                                //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber,2, callid,CSClientObj.getTime());
                            } else if(callType.equals("audio")) {
                                calldirection1 = "MISSED AUDIO CALL";
                                CSCallsObj.endVoiceCall(srcnumber, callid, CSConstants.E_UserBusy, CSConstants.UserBusy);
                                //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber, 1, callid,CSClientObj.getTime());
                            } else {
                                calldirection1 = "MISSED PSTN CALL";
                                CSCallsObj.endPstnCall(srcnumber, callid);
                                //CSPaymentsObj.directAudioVideoCallEndReq(srcnumber, 1, callid,CSClientObj.getTime());
                            }

                            notifyCallMissed(context, srcnumber, Name, callid, calldirection1, "", 0);
                            LOG.info("YES calldirection:" + calldirection1);
                            return;
                        }
                        //LOG.info("YES calldirection:" + calldirection);

                        /*
                        //CSClient CSClientObj = new CSClient();
                        long presenttime = CSClientObj.getTime();
                        long timedifference = (presenttime - callstarttime) / 1000;
                        LOG.info("YES timedifference:" + timedifference);

                        if (timedifference < 50) {

								if (GlobalVariables.callringinginprogress) {
									CSCall CSCallsObj = new CSCall();
									if (callType.equals("video")) {
										CSCallsObj.endVideoCall(srcnumber, callid, AppConstants.E_UserBusy, AppConstants.UserBusy);
										//CSPaymentsObj.directAudioVideoCallEndReq(srcnumber,2, callid,CSClientObj.getTime());
									} else {
										CSCallsObj.endAudioCall(srcnumber, callid, AppConstants.E_UserBusy, AppConstants.UserBusy);
										//CSPaymentsObj.directAudioVideoCallEndReq(srcnumber, 1, callid,CSClientObj.getTime());
									}

									return;
								}




*/
                            //Intent intent1;// = new Intent(context.getApplicationContext(), CallScreenActivity.class);

                            //if(secondcall) {
                        //Intent intent1 = new Intent(context.getApplicationContext(), CallScreenActivity.class);
                            //} else {
                            //intent1 = new Intent(context.getApplicationContext(), CallScreenActivity.class);

//}
                        //if(true) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && new App().getActivityStackCount()<=0) {
                            showCallSensitiveNotofication(secondcall,false,sDstMobNu,callactive,callType,srcnumber,callid,callstarttime);
                        } else {
                            Intent intent1 = new Intent(context.getApplicationContext(), CallScreenActivity.class);
                            if (App.getActivityStackCount() > 0) {
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            } else {
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            }

                            intent1.putExtra("secondcall", secondcall);
                            intent1.putExtra("isinitiatior", false);
                            intent1.putExtra("sDstMobNu", sDstMobNu);
                            intent1.putExtra("callactive", callactive);
                            //intent1.putExtra("sdp", sdp);
                            intent1.putExtra("callType", callType);
                            intent1.putExtra("srcnumber", srcnumber);
                            //LOG.info("Call id in call end call back here1:" + callid);

                            intent1.putExtra("callid", callid);
                            intent1.putExtra("callstarttime", callstarttime);
                            if (didnumber != null) {
                                intent1.putExtra("didnumber", didnumber);
                            }
                            context.startActivity(intent1);
                        /*} else {
                            LOG.info("YES CALL SCREEN SKIPPED.");
                        }*/

                            //}
                        }
                    } catch (Exception ex) {
                        utils.logStacktrace(ex);
                    }
                }
            }).start();
        } catch (Exception ex) {

        }
    }

    public static void logStacktrace(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            LOG.warn(sStackTrace);
        } catch (Exception ex) {
        }
    }

    /**
     * Converting timestamp to date, comparing for Today and Yesterday
     *
     * @param timeStamp
     * @return
     */
    public static String getDateForChat(long timeStamp, Context context) {

        try {
            DateFormat sdf = new SimpleDateFormat(GlobalVariables.ONLY_DATE_FORMAT);
            boolean is24fromat = android.text.format.DateFormat.is24HourFormat(context);
            DateFormat sdfForTime = null;
            if (is24fromat) {
                sdfForTime = new SimpleDateFormat(GlobalVariables.TIME_FORMAT_24HR);
            } else {
                sdfForTime = new SimpleDateFormat(GlobalVariables.TIME_FORMAT);
            }

            Date netDate = (new Date(timeStamp));

            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(timeStamp);

            Calendar now = Calendar.getInstance();


            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
                return sdfForTime.format(netDate);
            } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
                return "Yesterday";
            }

            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static Bitmap getGoogleMapThumbnail(String lati, String longi) {


        try {
            String URL = "http://maps.google.com/maps/api/staticmap?center=" + lati + "," + longi + "&zoom=16&size=200x200&sensor=false&markers=color:blue%" + lati + "," + longi + "&key=AIzaSyBGqiHDk9Qy-akUPC3yrzTDAtGzzadmghs";
            //URL = URLEncoder.encode(URL, "UTF-8");
            LOG.info("MAP URL:" + URL);

            java.net.URL url = new java.net.URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method will return the date from the given time stamp
     *
     * @param dateStr
     * @return
     */
    public static String getTiemStamp(long dateStr) {
        try {
            return new SimpleDateFormat("hh:mm:ss a").format(dateStr);
        } catch (Exception e) {
            utils.logStacktrace(e);
        }
        return "";
    }

    private static boolean isNotificationVisible(Context context, int MY_ID) {
        Intent notificationIntent = new Intent(context, ShowUserLogActivity.class);
        PendingIntent test = PendingIntent.getActivity(context, MY_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }


    public static void showCallSensitiveNotofication(boolean secondcall,boolean isinitiatior,String sDstMobNu,int callactive,String callType,String srcnumber,String callid,long callstarttime) {

        createIncallNotificationChannel(MainActivity.context);

        String title = "Incoming "+callType+" call";

        LOG.info("Android 10 call notification");

        String name = srcnumber;
        Cursor ccfr = CSDataProvider.getContactCursorByNumber(srcnumber);
        if (ccfr.getCount() > 0) {
            ccfr.moveToNext();
            name = ccfr.getString(ccfr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
        }
        ccfr.close();


        Intent intent = new Intent(MainActivity.context, DismissCallNotificationHandler.class);
        intent.putExtra("callType", callType);
        intent.putExtra("srcnumber", srcnumber);
        intent.putExtra("callid", callid);
        PendingIntent deleteIntent = PendingIntent.getBroadcast(MainActivity.context, 10, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intent1 = new Intent(MainActivity.context, CallScreenActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("secondcall", secondcall);
        intent1.putExtra("isinitiatior", false);
        intent1.putExtra("sDstMobNu", sDstMobNu);
        intent1.putExtra("callactive", callactive);
        intent1.putExtra("callType", callType);
        intent1.putExtra("srcnumber", srcnumber);
        intent1.putExtra("callid", callid);
        intent1.putExtra("callstarttime", callstarttime);
        intent1.setAction(Long.toString(System.currentTimeMillis()));
        //LOG.info("here2");
        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        if(CallScreenActivity.ringtone == null) {
    CallScreenActivity.ringtone = RingtoneManager.getRingtone(MainActivity.context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
}
if(!CallScreenActivity.ringtone.isPlaying()) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        CallScreenActivity.ringtone.setLooping(true);
    }
    CallScreenActivity.ringtone.play();
}


        //PendingIntent pIntent = PendingIntent.getActivity(MainActivity.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
/*
        Intent intentAction = new Intent(MainActivity.context, InComingCallHandlingReceiverA10.class);
        intentAction.setAction("EndCall");
        PendingIntent endCallIntent = PendingIntent.getBroadcast(MainActivity.context, 1, intentAction, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent intentAction1 = new Intent(MainActivity.context, InComingCallHandlingReceiverA10.class);
        intentAction1.setAction("AnswerCall");
        PendingIntent answerCallIntent = PendingIntent.getBroadcast(MainActivity.context, 1, intentAction1, PendingIntent.FLAG_CANCEL_CURRENT);
*/

        //LOG.info("here3");
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(MainActivity.context,"Android 10 Calls")
                        //.setSound(uri)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(name)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setDefaults(0)
                        .setAutoCancel(true)
                        //.addAction(R.drawable.uiendcall1, "Decline", endCallIntent)
                        //.addAction(R.drawable.ui_answer_call, "Answer", answerCallIntent)//getPendingIntent(Constants.ACCEPT_CALL,callId,callType,number)) //getPendingIntent(Constants.ACCEPT_CALL,callId,callType))
                        .setDeleteIntent(deleteIntent) //onDismissPendingIntent)
                        .setContentIntent(pIntent)
                        .setTimeoutAfter(30000)
                        .setFullScreenIntent(pIntent, true);
        Notification incomingCallNotification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager) MainActivity.context.getSystemService(Context.NOTIFICATION_SERVICE);

        PreferenceProvider preferenceProvider = new PreferenceProvider(MainActivity.context);
        int  notificationid = new Random().nextInt(1000001);
        preferenceProvider.setPrefint(callid + " incallnotification", notificationid);


        notificationManager.notify(notificationid, incomingCallNotification);
        //LOG.info("here4");
    }

}
