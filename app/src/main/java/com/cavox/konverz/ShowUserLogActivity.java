package com.cavox.konverz;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.multidex.MultiDex;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.deltacubes.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.ca.Utils.CSEvents;
import com.ca.Utils.CSDbFields;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;
import com.cavox.adapaters.FirstCallRecentsDetailLogAdapter;

import com.cavox.utils.PreferenceProvider;
import com.cavox.utils.utils;

import java.io.File;

import static com.cavox.konverz.MainActivity.context;
import static com.cavox.utils.GlobalVariables.LOG;

public class ShowUserLogActivity extends AppCompatActivity {
    String managecontactnumber = "";
    String managedirection = "";

    ListView mListView;
    Toolbar toolbar;
    //FloatingActionButton fab;
    //TextView subtitle;
    CSClient CSClientObj = new CSClient();
    FirstCallRecentsDetailLogAdapter appContactsAdapter;
    private TextView mCallLogsNameTv;
    private ImageView mCallLogsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlog);
        try {

            //ImageView profileimage = (ImageView) findViewById(R.id.grpimg);
            //AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

            //subtitle = (TextView) findViewById(R.id.subtitle);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCallLogsNameTv = findViewById(R.id.call_logs_name_tv);
            mCallLogsImage = findViewById(R.id.user_profle_image);
            managecontactnumber = getIntent().getStringExtra("number");
            managedirection = getIntent().getStringExtra("direction");
            String name = getIntent().getStringExtra("name");
            String id = getIntent().getStringExtra("id");
            if (name.equals("")) {
                name = managecontactnumber;
            }
            Log.i("ShowUserLogActivity", "call log number " + managecontactnumber);
            getProfilePicture();
            getSupportActionBar().setTitle("Call Details");
            //getSupportActionBar().setSubtitle(managecontactnumber);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCallLogsNameTv.setText(name);
            //subtitle.setText(managecontactnumber);

            PreferenceProvider preferenceProvider = new PreferenceProvider(getApplicationContext());
            preferenceProvider.setPrefString(managecontactnumber + "MissedData", "");
            //new ImageDownloaderTask(profileimage).execute(id);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();

                }
            });


            mListView = (ListView) findViewById(R.id.appcontacts1);
            LOG.info("managecontactnumber:" + managecontactnumber);
            LOG.info("managedirection:" + managedirection);
            appContactsAdapter = new FirstCallRecentsDetailLogAdapter(getApplicationContext(), CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_NUMBER, managecontactnumber), 0);
            //setListviewheight();
            mListView.setAdapter(appContactsAdapter);

            Cursor cur = CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_NUMBER, managecontactnumber);
            cur.close();


/*
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(verticalOffset == 0){
                        //LOG.info("Offset 0");
                        subtitle.setPadding(72,0,0,0);
                    }else {
                        subtitle.setPadding(112,0,0,0);
                        //LOG.info("Offset 1");
                    }
                }
            });
*/
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 888) {
            LOG.info("onActivityResult called here");

        }
    }


    public void updateUI(String str) {

        try {
            if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                LOG.info("NetworkError receieved");
                //Toast.makeText(this, "NetworkError", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
        }
    }


    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver ShowUserLogActivity");
                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    updateUI(CSEvents.CSCLIENT_NETWORKERROR);
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


            MainActivityReceiverObj = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            appContactsAdapter.changeCursor(CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_NUMBER, managecontactnumber));
            appContactsAdapter.notifyDataSetChanged();

            Cursor cur = CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_NUMBER, managecontactnumber);
            cur.close();

        } catch (Exception ex) {
        }

    }

    private void getProfilePicture() {
        String nativecontactid = "";
        Cursor cur = CSDataProvider.getContactCursorByNumber(managecontactnumber);
        if (cur.getCount() > 0) {
            cur.moveToNext();
            nativecontactid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID));
        }
        cur.close();

        String picid = "";
        Cursor cur1 = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, managecontactnumber);
        if (cur1.getCount() > 0) {
            cur1.moveToNext();
            picid = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
        }
        cur1.close();
        String filepath = CSDataProvider.getImageFilePath(picid);
        Glide.with(ShowUserLogActivity.this)
                .load(Uri.fromFile(new File(filepath)))
                .apply(new RequestOptions().error(R.drawable.defaultcontact))
                .apply(RequestOptions.circleCropTransform())
                .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(filepath).length()+new File(filepath).lastModified()))))
                .into(mCallLogsImage);
        //new ImageDownloaderTask(mCallLogsImage).execute("app", picid, nativecontactid);
    }
    public static void showCallTypeDia(Activity context, String managecontactnumber) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.call_type_dialog);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (dialog != null) {
                dialog.show();
            }
            LinearLayout pstnLayout=dialog.findViewById(R.id.pstnCallLayout);
            LinearLayout appCallLayout=dialog.findViewById(R.id.appCallLayout);
            appCallLayout.setOnClickListener(view -> {
                dialog.dismiss();
                // CallMethodHelper.processAudioCall(context, numberToDial, "AUDIO");
                utils.donewvoicecall(managecontactnumber,context );
            });
            pstnLayout.setOnClickListener(view -> {
                dialog.dismiss();
                utils.donewPstncall(managecontactnumber, context);
                // CallMethodHelper.processAudioCall(context, numberToDial, "PSTN");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.exitmenu, menu);
        getMenuInflater().inflate(R.menu.chatmenu1, menu);


        String isApp = "0";
        Cursor cr = CSDataProvider.getContactCursorByNumber(managecontactnumber);
        String id = "";
        if (cr.getCount() > 0) {
            cr.moveToNext();
            isApp = cr.getString(cr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_IS_APP_CONTACT));
            id = cr.getString(cr.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
        } else {
            isApp = "0";
        }
        cr.close();

        if(isApp.equalsIgnoreCase("0")){
            menu.findItem(R.id.videocall).setVisible(false);
            menu.findItem(R.id.chat).setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.audiocall:

                String isApp = "0";
                Cursor cr = CSDataProvider.getContactCursorByNumber(managecontactnumber);
                String id = "";
                if (cr.getCount() > 0) {
                    cr.moveToNext();
                    isApp = cr.getString(cr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_IS_APP_CONTACT));
                    id = cr.getString(cr.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
                } else {
                    isApp = "0";
                }
                cr.close();
                LOG.info("Calllog typeiff " + isApp);
                if(isApp.equals("0")){
                    utils.donewPstncall(managecontactnumber, ShowUserLogActivity.this);
                }
                else{
                    showCallTypeDia(ShowUserLogActivity.this, managecontactnumber);
                }
               // utils.donewPstncall(number, mActivity);

               // utils.donewvoicecall(managecontactnumber, ShowUserLogActivity.this);


                return true;

            case R.id.videocall:
               // item.setVisible(false);
                utils.donewVideocall(managecontactnumber, ShowUserLogActivity.this);
                return true;

            case R.id.chat:

                /*
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NUMBER, managecontactnumber);
                intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NAME, mCallLogsNameTv.getText().toString().trim());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
*/
                Intent intent = new Intent(ShowUserLogActivity.this, ChatAdvancedActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("Sender", managecontactnumber);
                intent.putExtra("IS_GROUP", false);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {

            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
        } catch (Exception ex) {
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            finish();
        } catch (Exception ex) {

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }





}
