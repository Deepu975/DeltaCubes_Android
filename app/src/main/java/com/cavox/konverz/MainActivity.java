package com.cavox.konverz;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.app.deltacubes.R;
import com.cavox.fragments.FirstCallDialPad;
import com.cavox.paymentgateways.payzone.Global;
import com.cavox.paymentgateways.payzone.PaymentForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;
import com.ca.Utils.CSExplicitEvents;
import com.ca.dao.CSAppDetails;
import com.ca.dao.CSExplicitEventReceivers;
import com.ca.wrapper.CSCall;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;
import com.cavox.fragments.FirstCallChats;
import com.cavox.fragments.FirstCallContacts;
import com.cavox.fragments.FirstCallNewSettings;
import com.cavox.fragments.FirstCallRecents;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.PreferenceProvider;
import com.cavox.utils.utils;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

import static com.cavox.utils.GlobalVariables.LOG;

//import com.vx.testlib.Testit;

public class MainActivity extends AppCompatActivity {
    static public TabLayout tabLayout;
    public static ViewPager viewPager;
    Handler h1 = new Handler();
    Runnable RunnableObj1;
    public static NotificationManager notificationManager;
    public static Context context;
    //public static boolean showloginfailure = false;
    CSClient CSClientObj = new CSClient();
    public static boolean syncConatct = false;
    static boolean enabledialpad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if(enabledialpad) {
            tabLayout.getTabAt(0).setCustomView(R.layout.recents_tab);
            tabLayout.getTabAt(1).setCustomView(R.layout.dialpad_tab);
            tabLayout.getTabAt(2).setCustomView(R.layout.contacts_tab);
            tabLayout.getTabAt(3).setCustomView(R.layout.chat_tab);
            tabLayout.getTabAt(4).setCustomView(R.layout.settings_tab);
            viewPager.setCurrentItem(3);
        } else {
            tabLayout.getTabAt(0).setCustomView(R.layout.recents_tab);
            //tabLayout.getTabAt(1).setCustomView(R.layout.dialpad_tab);
            tabLayout.getTabAt(1).setCustomView(R.layout.contacts_tab);
            tabLayout.getTabAt(2).setCustomView(R.layout.chat_tab);
            tabLayout.getTabAt(3).setCustomView(R.layout.settings_tab);
            viewPager.setCurrentItem(2);
        }



        syncConatct = true;


        String arch = System.getProperty("os.arch");
        LOG.info("arch:" + arch);
        LOG.info("MODEL:" + android.os.Build.MODEL);
        //LOG.info("ABI:" + android.os.Build.SUPPORTED_ABIS[0]);//String ABI = Build.CPU_ABI;




        //setLogBackConfiguration();

        //LOG.info("info arch:" + arch);
        //LOG.error("error arch:" + arch);

        PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
        boolean dontshowagain = pf.getPrefBoolean("registerreceiversnew");
        LOG.info("registerreceiversnew:" + dontshowagain);
        notificationManager.cancelAll();
        CSExplicitEventReceivers ccs = CSClientObj.getRegisteredExplicitEventReceivers();
        LOG.info("registerreceivers getCSChatReceiverReceiver:" + ccs.getCSChatReceiverReceiver());
        LOG.info("registerreceivers getCSCallReceiverReceiver:" + ccs.getCSCallReceiverReceiver());
        LOG.info("registerreceivers getCSGroupNotificationReceiverReceiver:" + ccs.getCSGroupNotificationReceiverReceiver());
        LOG.info("registerreceivers getCSCallMissedReceiver:" + ccs.getCSCallMissedReceiver());
        LOG.info("registerreceivers getCSUserJoinedReceiver:" + ccs.getCSUserJoinedReceiver());

        boolean record =  pf.getPrefBoolean("recordall");
if(record) {
    GlobalVariables.callrecord = CSConstants.CALLRECORD.RECORD;
} else {
    GlobalVariables.callrecord = CSConstants.CALLRECORD.DONTRECORD;
}
        //GlobalVariables.callrecord pf.getPrefBoolean("recordall");

        if (!pf.getPrefBoolean(PreferenceProvider.IS_NEW_SERVER_INITILIZED)) {
            pf.setPrefboolean(PreferenceProvider.IS_NEW_SERVER_INITILIZED, true);

            GlobalVariables.server = pf.getPrefString("server");
            GlobalVariables.port = pf.getPrefInt("port");
            GlobalVariables.appid = pf.getPrefString("appid");
            GlobalVariables.appname = pf.getPrefString("appname");

            LOG.info("GlobalVariables.server:"+GlobalVariables.server);
            LOG.info("GlobalVariables.port:"+GlobalVariables.port);
            LOG.info("GlobalVariables.appname:"+GlobalVariables.appname);
            LOG.info("GlobalVariables.appid:"+GlobalVariables.appid);

            CSAppDetails csAppDetails = new CSAppDetails(GlobalVariables.appname,GlobalVariables.appid);
            //CSAppDetails csAppDetails = new CSAppDetails("iamLive", "aid_8656e0f6_c982_4485_8ca7_656780b53d34");
            //CSAppDetails csAppDetails = new CSAppDetails("iamlivedbnew","iamlivedbnew","iamlivedbnew");
            CSClientObj.initialize(GlobalVariables.server, GlobalVariables.port, csAppDetails);
        }

        if (ccs.getCSChatReceiverReceiver().equals("") || !dontshowagain) {
            CSClientObj.registerExplicitEventReceivers(new CSExplicitEventReceivers("com.cavox.receivers.CSUserJoined", "com.cavox.receivers.CSCallReceiver", "com.cavox.receivers.CSChatReceiver", "com.cavox.receivers.CSGroupNotificationReceiver", "com.cavox.receivers.CSCallMissed"));




            PreferenceProvider pff = new PreferenceProvider(getApplicationContext());
            pff.setPrefboolean("registerreceiversnew", true);
        }


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ArrayList<String> allpermissions = new ArrayList<String>();
            allpermissions.add(android.Manifest.permission.CAMERA);
            allpermissions.add(android.Manifest.permission.READ_CONTACTS);
            allpermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            allpermissions.add(android.Manifest.permission.RECORD_AUDIO);
            allpermissions.add(android.Manifest.permission.READ_PHONE_STATE);
            allpermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            allpermissions.add(android.Manifest.permission.VIBRATE);
            allpermissions.add(android.Manifest.permission.READ_PHONE_STATE);

            ArrayList<String> requestpermissions = new ArrayList<String>();

            for (String permission : allpermissions) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                    requestpermissions.add(permission);
                }
            }
            if (requestpermissions.size() > 0) {
                ActivityCompat.requestPermissions(this, requestpermissions.toArray(new String[requestpermissions.size()]), 101);
            }
        }

        new CSCall().setPreferredAudioCodec(CSConstants.PreferredAudioCodec.G729);
        new CSCall().enableDefaultlocalVideoPreviewUX(true);
        new CSCall().setIceTransportsType(CSConstants.IceTransportsType.ALL);
        new CSClient().registerForPSTNCalls();
        //new CSCall().enableCallStats(true);




        utils.setFileTrasferPathsHelper();
        getoptimizerPermissions();
        try {
            //new PaymentForm().processPayment(new Global());
            //new CSCall().autoUploadAllRecordingsToServer(false);

           /* Cursor cur = CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_IS_CALL_RECORDED,1);
            LOG.info("recordings count:"+cur.getCount());
            while (cur.moveToNext()) {
                LOG.info("Recording file path:"+cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_RECORDING_FILEPATH)));

            }
            cur.close();
*/

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //direct user to other settings
//        startActivity(new Intent("miui.intent.action.APP_PERM_EDITOR").putExtra("extra_pkgname", getPackageName()))

        //subscribeToPushTopic();
        //startActivity(new Intent(this, PaymentActivitySandBox.class));




        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //updatetabs(tab.getPosition(),true);

if(tab.getPosition() == 0) {
            try {
                Cursor cursor = CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_MISSEDCALL_NOTIFIED, 0);

                LOG.info("RECENTS SELECTED count:" + cursor.getCount());
                while (cursor.moveToNext()) {
                    String direction = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_DIR));
                    LOG.info("RECENTS SELECTED direction:" + direction);
                    //if(direction.contains("MISSED")) {

                    String callid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_CALLID));
                    CSDataProvider.updateCallLogbyCallidAndFilter(callid, CSDbFields.KEY_CALLLOG_MISSEDCALL_NOTIFIED, 1);
                    //LOG.info("RECENTS SELECTED callid:"+callid);
                    //}
                    //LOG.info("RECENTS SELECTED count:"+cursor.getCount());
                }
                cursor.close();
            } catch (Exception ex) {
                utils.logStacktrace(ex);
            }

}

if(enabledialpad) {
    if (tab.getPosition() == 0 || tab.getPosition() == 3) {
        updateChatBadgeCount();
        updateRecentsBadgeCount();
    }
} else {
    if (tab.getPosition() == 0 || tab.getPosition() == 2) {
        updateChatBadgeCount();
        updateRecentsBadgeCount();
    }
}

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        LOG.info(permissions[i] + ":permission granted");
                        if (permissions[i].equals("android.permission.READ_CONTACTS")) {
                            LOG.info("syncing contacts from app");
                            CSClientObj.syncNativeContacts(91);
                        }
                    } else {
                        LOG.info(permissions[i] + ":permission denied");
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    public void subscribeToPushTopic() {
        try {

            FirebaseMessaging.getInstance().subscribeToTopic("konverz")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Success";
                            if (!task.isSuccessful()) {
                                msg = "Failure";
                            }
                            //Log.d(TAG, msg);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }












    public void getoptimizerPermissions() {
        try {
            PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
            boolean dontshowagain = pf.getPrefBoolean("dontshowagain");


            if (!dontshowagain) {
                //boolean checkpermission = false;


                String manufacturer = android.os.Build.MANUFACTURER;
                int apilevel = Build.VERSION.SDK_INT;

                LOG.info("manufacturer:" + manufacturer);
                LOG.info("apilevel:" + apilevel);

                Intent intent = new Intent();
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
                } else if ("huawei".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                } else if ("samsung".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity"));
                } else if ("asus".equalsIgnoreCase(manufacturer)) {

/*
                    PackageManager pm = getPackageManager();
                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                    for (ApplicationInfo packageInfo : packages)
                    {
                        LOG.info("Installed package :" + packageInfo.packageName);
                        LOG.info("Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
                    }*/


                } else if ("sony".equalsIgnoreCase(manufacturer)) {

                } else if ("htc".equalsIgnoreCase(manufacturer)) {

                } else if ("lenovo".equalsIgnoreCase(manufacturer)) {

                } else if (apilevel >= 23) {

                }


                List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.size() <= 0) {
                    utils.showSettingsAlert("Enable autostart permission to receive notifications",MainActivity.this);
                    //return;
                } else {
                    utils.showIntentAlert("Enable autostart permission to receive notifications", MainActivity.this, intent);
                }

                //if(!checkpermission) {



                PreferenceProvider pff = new PreferenceProvider(getApplicationContext());
                pff.setPrefboolean("dontshowagain", true);
                //}

            }

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(enabledialpad) {
            adapter.addFragment(new FirstCallRecents(), "");
            adapter.addFragment(new FirstCallDialPad(), "");
            adapter.addFragment(new FirstCallContacts(), "");
            adapter.addFragment(new FirstCallChats(), "");
            adapter.addFragment(new FirstCallNewSettings(), "");
        } else {
            adapter.addFragment(new FirstCallRecents(), "");
            //adapter.addFragment(new FirstCallDialPad(), "");
            adapter.addFragment(new FirstCallContacts(), "");
            adapter.addFragment(new FirstCallChats(), "");
            adapter.addFragment(new FirstCallNewSettings(), "");
        }
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                GlobalVariables.tab_selected = position;
                hideKeyboard();
                //updatetabs(position);
            }

            @Override



            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void reTry() {

        try {

            LOG.info("NetworkError receieved:" + GlobalVariables.loginretries);


            try {

                GlobalVariables.loginretries++;


                if (GlobalVariables.loginretries <= 5000) {

                    RunnableObj1 = new Runnable() {
                        public void run() {
                            LOG.info("Sending retry Time:" + CSClientObj.getTime() / 1000);


                            PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
                            GlobalVariables.server = pf.getPrefString("server");
                            GlobalVariables.port = pf.getPrefInt("port");
                            GlobalVariables.appid = pf.getPrefString("appid");
                            GlobalVariables.appname = pf.getPrefString("appname");

                            LOG.info("GlobalVariables.server:"+GlobalVariables.server);
                            LOG.info("GlobalVariables.port:"+GlobalVariables.port);
                            LOG.info("GlobalVariables.appname:"+GlobalVariables.appname);
                            LOG.info("GlobalVariables.appid:"+GlobalVariables.appid);

                            //CSAppDetails csAppDetails = new CSAppDetails("iamLive","did_19e01bd3_b7d6_44fd_81ac_034ef7fcf6fb","aid_8656e0f6_c982_4485_8ca7_656780b53d34");
                            //CSAppDetails csAppDetails = new CSAppDetails("iamLive", "aid_8656e0f6_c982_4485_8ca7_656780b53d34");
                            //CSAppDetails csAppDetails = new CSAppDetails("iamlivedbnew","iamlivedbnew","iamlivedbnew");
                            CSAppDetails csAppDetails = new CSAppDetails(GlobalVariables.appname,GlobalVariables.appid);

                            CSClientObj.initialize(GlobalVariables.server, GlobalVariables.port, csAppDetails);

                            h1.removeCallbacks(RunnableObj1);
                        }
                    };

                    if (GlobalVariables.loginretries == 1) {
                        h1.postDelayed(RunnableObj1, 1000);
                    } else if (GlobalVariables.loginretries <= 10) {
                        h1.postDelayed(RunnableObj1, 3000);
                    } else if (GlobalVariables.loginretries == 11) {
                        h1.postDelayed(RunnableObj1, 10000);
                    } else if (GlobalVariables.loginretries == 12) {
                        h1.postDelayed(RunnableObj1, 15000);
                    } else if (GlobalVariables.loginretries >= 13) {
                        h1.postDelayed(RunnableObj1, 30000);
                    } else {
                        h1.postDelayed(RunnableObj1, 30000);
                    }
                    //h1.postDelayed(RunnableObj1, delay);

                } else {
                    //GlobalVariables.loginretries = 0;
                    //GlobalVariables.loginretriesavoidloop = 0;

                }


            } catch (Exception ex) {
                utils.logStacktrace(ex);
            }

        } catch (Exception ex) {
        }
    }


    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                LOG.info("receieved:" + intent.getAction().toString());
                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    reTry();
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_INITILIZATION_RESPONSE)) {
                    LOG.info("receieved result:" + intent.getStringExtra(CSConstants.RESULT));
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_FAILURE)) {
                        LOG.info("receieved result1:" + intent.getIntExtra(CSConstants.RESULTCODE, 0));
                        reTry();
                    }
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_LOGIN_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {

                        GlobalVariables.phoneNumber = CSDataProvider.getLoginID();
                        GlobalVariables.pass = CSDataProvider.getPassword();
                        GlobalVariables.loginretries = 0;

/*
                        LOG.info("updating password old:"+GlobalVariables.pass);
                        PreferenceProvider pf = new PreferenceProvider(getApplicationContext());
                        boolean updatepassword = pf.getPrefBoolean("updatepassword7");
if(!updatepassword) {
    pf.setPrefboolean("updatepassword7",true);
    LOG.info("updating password");
    CSClientObj.updatePassword(GlobalVariables.pass,"okjusttest12345");
}
*/

                    } else {
                        //LOG.info("loginfailure receieved:"+showloginfailure);
                        //if(showloginfailure) {
                        Intent intent1 = new Intent(getApplicationContext(), LoginFailureActivity.class);
                        startActivity(intent1);
                        //}
                    }
                } else if (intent.getAction().equals(CSExplicitEvents.CSChatReceiver)) {
                    updateChatBadgeCount();
                } else if (intent.getAction().equals(CSEvents.CSCALL_CALLLOGUPDATED)) {
                    updateRecentsBadgeCount();
                }

            } catch (Exception ex) {
            }
        }
    }

    MainActivityReceiver MainActivityReceiverObj = new MainActivityReceiver();


    @Override
    public void onResume() {
        super.onResume();

        try {

            GlobalVariables.loginretries = 0;

            IntentFilter filter1 = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            //IntentFilter filter2 = new IntentFilter(CSEvents.CSCLIENT_NOINTERNET);

            IntentFilter filter3 = new IntentFilter(CSEvents.CSCLIENT_LOGIN_RESPONSE);
            IntentFilter filter6 = new IntentFilter(CSEvents.CSCALL_CALLLOGUPDATED);

            IntentFilter filter5 = new IntentFilter(CSExplicitEvents.CSChatReceiver);
            IntentFilter filter9 = new IntentFilter(CSEvents.CSCLIENT_INITILIZATION_RESPONSE);


            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter1);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter2);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter3);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter4);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter6);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter9);

            getApplicationContext().registerReceiver(MainActivityReceiverObj, filter5);


            //String strr = CSDataProvider.getSetting(CSDbFields.KEY_SETINGS_LOGINSTATUS);
            LOG.info("CSDataProvider.getLoginstatus():" + CSDataProvider.getLoginstatus());
            if (!CSDataProvider.getLoginstatus()) {
                LOG.info("Sending init from mainactivity onresume");
                //CSClientObj.initialize(GlobalVariables.server, GlobalVariables.port,GlobalVariables.brandpin);
            }

            updateChatBadgeCount();
            updateRecentsBadgeCount();

            PlayNewAudioCallActivity PlayNewAudioCallActivityObj = new PlayNewAudioCallActivity();
            PlayNewVideoCallActivity PlayNewVideoCallActivityObj = new PlayNewVideoCallActivity();
            PlayNewAudioCallActivityObj.stopringbacktone();
            PlayNewVideoCallActivityObj.stopringbacktone();

        } catch (Exception ex) {
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        try {

            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
            getApplicationContext().unregisterReceiver(MainActivityReceiverObj);
            //showloginfailure = false;
        } catch (Exception ex) {
        }


    }

/*
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
*/


    private void hideKeyboard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    public static void updateChatBadgeCount() {
        try {

            Cursor cr = CSDataProvider.getChatCursorForUnreadMessages();
            int chat_unread_count = cr.getCount();
            cr.close();

            TextView badgecount;// = (TextView) tabLayout.getTabAt(2).getCustomView().findViewById(R.id.badge_textView);

            if(enabledialpad) {
                badgecount = (TextView) tabLayout.getTabAt(3).getCustomView().findViewById(R.id.badge_textView);

            } else {
                badgecount = (TextView) tabLayout.getTabAt(2).getCustomView().findViewById(R.id.badge_textView);

            }

            if (chat_unread_count <= 0) {
                badgecount.setVisibility(View.GONE);
            } else {
                badgecount.setVisibility(View.VISIBLE);
                badgecount.setText(String.valueOf(chat_unread_count));
            }

        } catch (Exception ex) {
        }

    }

    public static void updateRecentsBadgeCount() {
        try {

            Cursor cr = CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_MISSEDCALL_NOTIFIED, 0);
            int chat_unread_count = cr.getCount();
            cr.close();

            TextView badgecount = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.badge_textView);
            if (chat_unread_count <= 0) {
                badgecount.setVisibility(View.GONE);
            } else {
                badgecount.setVisibility(View.VISIBLE);
                badgecount.setText(String.valueOf(chat_unread_count));


            }
        } catch (Exception ex) {
        }
    }

    public void setLogBackConfiguration() {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            JoranConfigurator configurator = new JoranConfigurator();
            InputStream configStream = getAssets().open("logback1.xml");
            configurator.setContext(loggerContext);
            configurator.doConfigure(configStream); // loads logback file
            configStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
