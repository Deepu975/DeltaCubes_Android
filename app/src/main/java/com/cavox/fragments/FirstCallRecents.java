package com.cavox.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;

import com.cavox.konverz.ChatAdvancedActivity;
import com.cavox.konverz.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;
import com.app.deltacubes.R;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;
import com.cavox.adapaters.FirstCallRecentsAdapter;
import com.cavox.adapaters.SimpleTextAdapter;


import java.util.ArrayList;
import java.util.List;

import static com.cavox.utils.GlobalVariables.LOG;


public class FirstCallRecents extends Fragment {

    ListView mListView;
    FloatingActionButton removeallcallog;
    FirstCallRecentsAdapter appContactsAdapter;
    static FragmentActivity mActivity;
    TextView mytextview;
    private long mLastClickTime = 0;

    public FirstCallRecents() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recents, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        removeallcallog = view.findViewById(R.id.removeallcallog);
        mytextview = (TextView) view.findViewById(R.id.textview);
        mListView = (ListView) view.findViewById(R.id.appcontacts1);

        appContactsAdapter = new FirstCallRecentsAdapter(MainActivity.context, CSDataProvider.getCallLogCursorGroupedByNumberAndDirection(), 0);
        mListView.setAdapter(appContactsAdapter);


//LOG.info("RECENTS ON CREATE");


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Cursor cur = CSDataProvider.getCallLogCursorGroupedByNumberAndDirection();
                    cur.moveToPosition(position);
                    String number = "";
                    String name = "";
                    number = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_NUMBER));
                    name = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_NAME));
                    LOG.info("Number to call:" + number);
                    cur.close();
                    if (!number.equals("")) {
                        int isappcontact = 0;
                        Cursor cur1 = CSDataProvider.getContactCursorByNumber(number);
                        if (cur1.getCount() > 0) {
                            cur1.moveToNext();
                            isappcontact = cur1.getInt(cur1.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_IS_APP_CONTACT));
                        }
                        cur1.close();
                        if (isappcontact == 0) {
                            //showappcontactsoptions(number,name,false);
                        } else {
                            showappcontactsoptions(number, name, true);
                        }


                    }
                } catch (Exception ex) {
                    utils.logStacktrace(ex);
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Cursor cur = CSDataProvider.getCallLogCursorGroupedByNumberAndDirection();
                cur.moveToPosition(pos);
                String number = "";
                String direction = "";
                number = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_NUMBER));
                direction = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_DIR));

                LOG.info("Number to call:" + number);
                cur.close();

                //showdeletealert(number);
                showoptions(pos, number, direction);
                return true;
            }
        });
        removeallcallog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOG.info("showing alert:");

                Cursor ccr = CSDataProvider.getCallLogCursorGroupedByNumberAndDirection();
                if (ccr.getCount() > 0) {
                    showalert();
                }
                ccr.close();

            }
        });
    }

    public boolean showalert() {
        try {
            AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(getActivity());
            successfullyLogin.setTitle("Confirmation");
            successfullyLogin.setCancelable(true);
            successfullyLogin.setMessage("Delete All History?");

            successfullyLogin.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            CSDataProvider.deleteAllCallLog();

                            appContactsAdapter.changeCursor(CSDataProvider.getCallLogCursorGroupedByNumberAndDirection());
                            appContactsAdapter.notifyDataSetChanged();
                            updateUI("updatenologtext");

                            MainActivity.updateRecentsBadgeCount();
                        }
                    });

            successfullyLogin.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            //LOG.info("position Don't");

                            //IAmliveCoreObj.creategetsimpleFile(getFilesDir().getPath(),"/signupalertt");

                        }
                    });
            LOG.info("showing alert1:");
            successfullyLogin.show();

            return true;
        } catch (Exception ex) {
            utils.logStacktrace(ex);
            return false;
        }

    }


    public void showappcontactsoptions(final String number, final String name, final boolean isappcontact) {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.call_options_dialog);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));

            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            LinearLayout groupChatLayout = dialog.findViewById(R.id.group_options_layout);
            LinearLayout callOptionsLayout = dialog.findViewById(R.id.call_options_layout);
            groupChatLayout.setVisibility(View.GONE);
            LinearLayout audioCallLayout = dialog.findViewById(R.id.audio_call_layout);
            LinearLayout share_app_layoutview = dialog.findViewById(R.id.share_app_layout);
            LinearLayout videoCallLayout = dialog.findViewById(R.id.video_call_layout);
            LinearLayout chatLayout = dialog.findViewById(R.id.chat_layout);
            LinearLayout pstnLayout = dialog.findViewById(R.id.pstn_call_layout);
            TextView cancelTv = dialog.findViewById(R.id.dialog_cancel_tv);
            pstnLayout.setVisibility(View.GONE);
            share_app_layoutview.setVisibility(View.GONE);

            if (!isappcontact) {
                audioCallLayout.setVisibility(View.GONE);
                videoCallLayout.setVisibility(View.GONE);
                chatLayout.setVisibility(View.GONE);

            }
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                }
            });
            audioCallLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                    utils.donewvoicecall(number, mActivity);
                }
            });

            videoCallLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                    utils.donewVideocall(number, mActivity);

                }
            });
            chatLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();

                    /*Intent intent = new Intent(mActivity, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NUMBER, number);
                    intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NAME, name);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MainActivity.context.startActivity(intent);
*/
                    Intent intent = new Intent(mActivity, ChatAdvancedActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("Sender", number);
                    intent.putExtra("IS_GROUP", false);
                    mActivity.startActivity(intent);

                    /*
                    Intent intent = new Intent(mActivity, ChatAdvancedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Sender", number);
                    intent.putExtra("IS_GROUP", false);
                    MainActivity.context.startActivity(intent);
                    */
                }
            });

            if (dialog != null)
                dialog.show();
        } catch (
                Exception ex) {
            utils.logStacktrace(ex);

        }

    }

    /*
    public void donewVideocall(String numbertodial) {

        try {

            if (!numbertodial.equals("")&&!numbertodial.equals(GlobalVariables.phoneNumber)) {

                Intent intent = new Intent(mActivity, PlayNewVideoCallActivity.class);
                intent.putExtra("dstnumber", numbertodial);
                intent.putExtra("isinitiatior", true);
                startActivityForResult(intent, 954);
            } else {
                Toast.makeText(mActivity, "No valid Number", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }



    public void donewvoicecall(String numbertodial) {

        try {

            LOG.info("Doing new outgoing call");


            if (!numbertodial.equals("")&&!numbertodial.equals(GlobalVariables.phoneNumber)) {


                Intent intent = new Intent(mActivity, PlayNewAudioCallActivity.class);
                intent.putExtra("dstnumber", numbertodial);
                intent.putExtra("isinitiatior", true);
                startActivityForResult(intent, 954);
            } else {
                Toast.makeText(mActivity, "No valid Number", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }
    */
    public boolean showoptions(final int position, final String number, final String direction) {
        try {
            final ArrayList<String> grpoptions = new ArrayList<String>();

            AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(getActivity());
            successfullyLogin.setCancelable(true);
            grpoptions.clear();


            grpoptions.add("Delete");
            successfullyLogin.setTitle("Confirmation");
            Cursor csr = CSDataProvider.getContactCursorByNumber(number);
            if (csr.getCount() > 0) {
                csr.moveToNext();
                String idd = csr.getString(csr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID));
                String rawnumber = csr.getString(csr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_RAW_NUMBER));
                LOG.info("My id:" + idd);
                if (!iscontactexists(idd, rawnumber)) {
                    grpoptions.add("Add To Contacts");
                    successfullyLogin.setTitle("Options");
                }

            } else {
                grpoptions.add("Add To Contacts");
                successfullyLogin.setTitle("Options");
            }
            csr.close();


            SimpleTextAdapter simpleTextAdapter = new SimpleTextAdapter(MainActivity.context, grpoptions);
            successfullyLogin.setAdapter(simpleTextAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            String finalaction = grpoptions.get(which);
                            LOG.info("finalaction:" + finalaction);
                            LOG.info("number:" + number);
                            if (finalaction.equals("Delete")) {
                                LOG.info("In Delete:" + position);
                                //CSDataProvider.deletecallogbyposition(position);
                                //CSDataProvider.deletecallogbyfilter(CSDbFields.KEY_CALLLOG_NUMBER,number);
                                Cursor ccr = CSDataProvider.getCallLogCursorByTwoFilters(CSDbFields.KEY_CALLLOG_NUMBER, number, CSDbFields.KEY_CALLLOG_DIR, direction);
                                while (ccr.moveToNext()) {
                                    int rowid = ccr.getInt(ccr.getColumnIndexOrThrow(CSDbFields.KEY_ID));
                                    CSDataProvider.deleteCallLogByRowId(rowid);
                                }
                                ccr.close();

                                appContactsAdapter.changeCursor(CSDataProvider.getCallLogCursorGroupedByNumberAndDirection());
                                appContactsAdapter.notifyDataSetChanged();
                                updateUI("updatenologtext");
                                MainActivity.updateRecentsBadgeCount();
                            } else if (finalaction.equals("Add To Contacts")) {
                                Intent intent = new Intent(Intent.ACTION_INSERT);
                                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                                //intent.putExtra(ContactsContract.Intents.Insert.NAME, "FirstCall");

                                intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);

                                startActivity(intent);
                            }
                        }
                    });
            successfullyLogin.show();

            return true;
        } catch (Exception ex) {
            utils.logStacktrace(ex);
            return false;
        }

    }

    public boolean showdeletealert(final String number) {
        try {
            AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(getActivity());
            successfullyLogin.setTitle("Confirmation");

            successfullyLogin.setMessage("Delete Entry?");

            successfullyLogin.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            CSDataProvider.deleteCallLogByFilter(CSDbFields.KEY_CALLLOG_NUMBER, number);

                            appContactsAdapter.changeCursor(CSDataProvider.getCallLogCursorGroupedByNumberAndDirection());
                            appContactsAdapter.notifyDataSetChanged();
                            updateUI("updatenologtext");
                        }
                    });

            successfullyLogin.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            //LOG.info("position Don't");

                            //IAmliveCoreObj.creategetsimpleFile(getFilesDir().getPath(),"/signupalertt");

                        }
                    });
            successfullyLogin.show();

            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public void updateUI(String str) {

        try {

            if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                LOG.info("NetworkError receieved");
                ////Toast.makeText(MainActivity.context, "NetworkError", Toast.LENGTH_SHORT).show();
            } else if (str.equals(CSEvents.CSCONTACTS_ISAPPCONTACT_RESPONSE) || str.equals(CSEvents.CSCONTACTS_CONTACTSUPDATED) || str.equals(CSEvents.CSCHAT_CHATUPDATED) || str.equals(CSEvents.CSCLIENT_USERPROFILECHANGED) || str.equals(CSEvents.CSCLIENT_IMAGESDBUPDATED)) {
                LOG.info("contactsupdated");
                appContactsAdapter.changeCursor(CSDataProvider.getCallLogCursorGroupedByNumberAndDirection());
                appContactsAdapter.notifyDataSetChanged();
                updateUI("updatenologtext");
            } else if (str.equals("updatenologtext")) {
                LOG.info("updatenologtext");
                Cursor ccr = CSDataProvider.getCallLogCursorGroupedByNumberAndDirection();
                if (ccr.getCount() <= 0) {
                    mytextview.setVisibility(View.VISIBLE);
                    removeallcallog.setVisibility(View.INVISIBLE);
                } else {
                    mytextview.setVisibility(View.INVISIBLE);
                    removeallcallog.setVisibility(View.VISIBLE);
                }
                ccr.close();

            }


        } catch (Exception ex) {
        }

    }

    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver in recents fragmemt " + intent.getAction());


                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    updateUI(CSEvents.CSCLIENT_NETWORKERROR);
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_IMAGESDBUPDATED)) {
                    updateUI(CSEvents.CSCLIENT_IMAGESDBUPDATED);
                } else if (intent.getAction().equals(CSEvents.CSCONTACTS_ISAPPCONTACT_RESPONSE)) {

                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        updateUI(CSEvents.CSCONTACTS_ISAPPCONTACT_RESPONSE);
                    }
                } else if (intent.getAction().equals(CSEvents.CSCONTACTS_CONTACTSUPDATED)) {
                    updateUI(CSEvents.CSCONTACTS_CONTACTSUPDATED);
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_USERPROFILECHANGED)) {
                    updateUI(CSEvents.CSCLIENT_USERPROFILECHANGED);
                } else if (intent.getAction().equals(CSEvents.CSCALL_CALLLOGUPDATED)) {
                    appContactsAdapter.changeCursor(CSDataProvider.getCallLogCursorGroupedByNumberAndDirection());
                    appContactsAdapter.notifyDataSetChanged();
                }

            } catch (Exception ex) {
            }
        }


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Code here
        //LOG.info("On attach called9");

        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }


    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Code here
            //LOG.info("On attach called3");

            mActivity = (FragmentActivity) activity;

        }
    }


    MainActivityReceiver MainActivityReceiverObj = new MainActivityReceiver();

    @Override
    public void onResume() {
        super.onResume();

        try {
            //LOG.info("RECENTS ON RESUME);
            MainActivityReceiverObj = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            IntentFilter filter5 = new IntentFilter(CSEvents.CSCONTACTS_ISAPPCONTACT_RESPONSE);
            IntentFilter filter6 = new IntentFilter(CSEvents.CSCLIENT_IMAGESDBUPDATED);
            IntentFilter filter7 = new IntentFilter(CSEvents.CSCONTACTS_CONTACTSUPDATED);
            IntentFilter filter8 = new IntentFilter(CSEvents.CSCALL_CALLLOGUPDATED);
            IntentFilter filter10 = new IntentFilter(CSEvents.CSCLIENT_USERPROFILECHANGED);

            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter5);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter6);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter7);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter8);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter10);

            appContactsAdapter.changeCursor(CSDataProvider.getCallLogCursorGroupedByNumberAndDirection());
            appContactsAdapter.notifyDataSetChanged();
            updateUI("updatenologtext");

            Cursor ccr = CSDataProvider.getCallLogCursorGroupedByNumberAndDirection();
            if (ccr.getCount() <= 0) {
                removeallcallog.setVisibility(View.INVISIBLE);
            } else {
                removeallcallog.setVisibility(View.VISIBLE);
            }
            ccr.close();


            /***********test***********/
/*
            Cursor cur = CSDataProvider.getCallLogCursor();
            while (cur.moveToNext()) {
                String number = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_NUMBER));
                String directionn = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_DIR));
                String callid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_CALLID));


                LOG.info("callid:"+callid);
                LOG.info("directionn:"+directionn);
                LOG.info("number:"+number);


            }

            cur.close();
*/

            /********end test*********/


        } catch (Exception ex) {
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            LocalBroadcastManager.getInstance(MainActivity.context).unregisterReceiver(MainActivityReceiverObj);
        } catch (Exception ex) {
        }


    }

    public boolean iscontactexists(String id, String number) {
        boolean retvalue = false;
        String phone = "";
        try {
            List numbers = new ArrayList<String>();

            Cursor cursor = MainActivity.context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id}, null);

            while (cursor.moveToNext()) {
                phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                numbers.add(phone);
            }

            cursor.close();
            if (numbers.contains(number)) {
                retvalue = true;
            } else {
                retvalue = false;
            }
            numbers.clear();
        } catch (Exception ex) {

        }
        return retvalue;
    }


    public boolean shownwalert(String result) {
        try {
            AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(mActivity);
            successfullyLogin.setMessage(result);
            successfullyLogin.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        }
                    });
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
}
