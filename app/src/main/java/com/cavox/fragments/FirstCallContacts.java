package com.cavox.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.ContactsContract;

import com.cavox.konverz.AddContactActivity;
import com.cavox.konverz.ChatAdvancedActivity;
import com.cavox.konverz.CreateGroupActivity;
import com.cavox.konverz.GroupChatAdvancedActivity;
import com.cavox.konverz.MainActivity;
import com.cavox.konverz.ManageGroupActivity;
import com.cavox.konverz.ManageUserActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;
import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;
import com.cavox.adapaters.CorgAdapter;
import com.cavox.adapaters.SimpleTextAdapter;
import com.app.deltacubes.R;
import com.cavox.utils.utils;
import com.cavox.views.CustomEditText;
import com.cavox.views.CustomTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.cavox.utils.GlobalVariables.LOG;


public class FirstCallContacts extends Fragment {
    public static long mLastClickTime = 0;
    //ListView mListView;
    FloatingActionButton addcontact;
    static CustomEditText editText;
    private ImageView mSearchCancelImg;
    CustomTextView normalcontacts;
    CustomTextView appcontacts;
    public static int contactstypetoload = 1;//0- normal contacts 1 for app contacts
    CorgAdapter appContactsAdapter;
    static FragmentActivity mActivity;
    TextView mytextview;
    private LinearLayout mAddContctMenu;
    CSClient CSClientObj = new CSClient();
    private LinearLayout mAddNewContact, mAddNewGroup, mAddNewDirectConatct, mTransparentBg;
    private static boolean isShareIntentCalled = false;
    RecyclerView rv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getActivity().getWindow().setBackgroundDrawableResource(R.color.background);
        } catch (Exception ex) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LOG.info("onViewCreated:");
        //mListView = (ListView) view.findViewById(R.id.appcontacts1);
        addcontact = view.findViewById(R.id.addcontact);
        editText = (CustomEditText) view.findViewById(R.id.editText);
        mytextview = (TextView) view.findViewById(R.id.textview);
        normalcontacts = (CustomTextView) view.findViewById(R.id.text1);
        appcontacts = (CustomTextView) view.findViewById(R.id.text2);
        mSearchCancelImg = view.findViewById(R.id.contacts_search_cancel_img);
        mAddContctMenu = view.findViewById(R.id.add_contact_menu);
        mAddNewContact = view.findViewById(R.id.add_new_contact_layout);
        mAddNewGroup = view.findViewById(R.id.add_group_layout);
        mAddNewDirectConatct = view.findViewById(R.id.add_direct_contact_layout);
        mTransparentBg = view.findViewById(R.id.transparent_bg);
        rv = (RecyclerView) view.findViewById(R.id.chat_layout);

        //RelativeLayout bottomlayout = (RelativeLayout) view.findViewById(R.id.bottomlayout);

        //String showappcontacts = CSDataProvider.getSetting(CSDbFields.KEY_SETINGS_SIGNINTYPE);

       /*
        if(showappcontacts.equals("0")) {
            bottomlayout.setVisibility(View.GONE);
        }
*/

        try {
            ContentResolver cr = MainActivity.context.getContentResolver();


            if (contactstypetoload == 0) {
                appContactsAdapter = new CorgAdapter(MainActivity.context, CSDataProvider.getContactsCursor());
                setAdapter(appContactsAdapter);
                normalcontacts.setBackgroundResource(R.drawable.uilib_bg1);
                appcontacts.setBackgroundResource(R.drawable.uilib_bg2);
            } else {
                appContactsAdapter = new CorgAdapter(MainActivity.context, CSDataProvider.getContactsAndGroupsCursor());
                setAdapter(appContactsAdapter);
                normalcontacts.setBackgroundResource(R.drawable.uilib_bg2);
                appcontacts.setBackgroundResource(R.drawable.uilib_bg1);
            }

            mAddNewDirectConatct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addcontact.performClick();
                    Intent creategroupintent = new Intent(MainActivity.context, AddContactActivity.class);
                    creategroupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.context.startActivity(creategroupintent);

                }
            });
            mAddNewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addcontact.performClick();
                    Intent creategroupintent = new Intent(MainActivity.context, CreateGroupActivity.class);
                    creategroupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.context.startActivity(creategroupintent);

                }
            });
            mAddNewContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addcontact.performClick();
                    Intent intent = new Intent(Intent.ACTION_INSERT,
                            ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 9199);

                }
            });


            normalcontacts.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        contactstypetoload = 0;

                        //appContactsAdapter = new CorgAdapter(MainActivity.context, CSDataProvider.getContactsCursor(), 0);
                        //setAdapter(appContactsAdapter);
                        if (editText.getText().toString().equals("")) {
                            appContactsAdapter.swapCursorAndNotifyDataSetChanged(CSDataProvider.getContactsCursor());
                            //appContactsAdapter.notifyDataSetChanged();
                        } else {
                            editText.setText(editText.getText().toString());
                        }
                        normalcontacts.setBackgroundResource(R.drawable.uilib_bg1);
                        appcontacts.setBackgroundResource(R.drawable.uilib_bg2);
                        if (CSDataProvider.getContactsCursor().getCount() == 0) {
                            mytextview.setVisibility(View.VISIBLE);
                        } else {
                            mytextview.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {

                    }
                }
            });
            mTransparentBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addcontact.performClick();
                }
            });
            appcontacts.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        contactstypetoload = 1;
                        //appContactsAdapter = new CorgAdapter(MainActivity.context, CSDataProvider.getContactsAndGroupsCursor(), 0);
                        //setAdapter(appContactsAdapter);
                        if (editText.getText().toString().equals("")) {
                            appContactsAdapter.swapCursorAndNotifyDataSetChanged(CSDataProvider.getContactsAndGroupsCursor());
                            //appContactsAdapter.notifyDataSetChanged();
                        } else {
                            editText.setText(editText.getText().toString());
                        }
                        normalcontacts.setBackgroundResource(R.drawable.uilib_bg2);
                        appcontacts.setBackgroundResource(R.drawable.uilib_bg1);
                        if (CSDataProvider.getContactsAndGroupsCursor().getCount() == 0) {
                            mytextview.setVisibility(View.VISIBLE);
                        } else {
                            mytextview.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {

                    }
                }
            });

            addcontact.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
/*

                    showoptions("Options");

*/
                    if (mAddContctMenu.getVisibility() == View.VISIBLE) {
                        final Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
                        mAddContctMenu.setAnimation(slideUp);
                        mAddContctMenu.setVisibility(View.GONE);
                        addcontact.setImageResource((R.drawable.ic_person_add_24));
                        mTransparentBg.setVisibility(View.GONE);
                    } else {
                        final Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                        mAddContctMenu.setAnimation(slideUp);
                        mAddContctMenu.setVisibility(View.VISIBLE);
                        addcontact.setImageResource((R.drawable.ic_cross));
                        mTransparentBg.setVisibility(View.VISIBLE);
                    }
                }
            });

            mSearchCancelImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editText != null && editText.getText().toString().length() > 0) {
                        editText.setText("");
                    }
                }
            });
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    LOG.info("Yes on touch up:" + editText.getText().toString());
                    if (charSequence.length() > 0) {
                        mSearchCancelImg.setVisibility(View.VISIBLE);
                    } else {
                        mSearchCancelImg.setVisibility(View.GONE);
                    }
                    refreshview();
                }

                @Override
                public void afterTextChanged(Editable editable) {


                }
            });


        } catch (Exception ex) {

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 954) {
            LOG.info("onActivityResult called here");
        }

    }

    public void refreshview() {

        try {


            if (appContactsAdapter == null) {
                if (contactstypetoload == 0) {
                    appContactsAdapter = new CorgAdapter(MainActivity.context, CSDataProvider.getContactsCursor());
                    setAdapter(appContactsAdapter);
                    normalcontacts.setBackgroundResource(R.drawable.uilib_bg1);
                    appcontacts.setBackgroundResource(R.drawable.uilib_bg2);
                } else {
                    appContactsAdapter = new CorgAdapter(MainActivity.context, CSDataProvider.getContactsAndGroupsCursor());
                    setAdapter(appContactsAdapter);
                    normalcontacts.setBackgroundResource(R.drawable.uilib_bg2);
                    appcontacts.setBackgroundResource(R.drawable.uilib_bg1);
                }
            }


            if (!editText.getText().toString().equals("")) {
                if (contactstypetoload == 0) {
                    appContactsAdapter.swapCursorAndNotifyDataSetChanged(CSDataProvider.getSearchContactsCursor(editText.getText().toString()));
                    //appContactsAdapter.notifyDataSetChanged();
                } else {
                    appContactsAdapter.swapCursorAndNotifyDataSetChanged(CSDataProvider.getSearchContactsAndGroupsCursor(editText.getText().toString()));
                    //appContactsAdapter.notifyDataSetChanged();
                }
            } else {
                if (contactstypetoload == 0) {
                    appContactsAdapter.swapCursorAndNotifyDataSetChanged(CSDataProvider.getContactsCursor());
                    //appContactsAdapter.notifyDataSetChanged();
                } else {
                    appContactsAdapter.swapCursorAndNotifyDataSetChanged(CSDataProvider.getContactsAndGroupsCursor());
                    //appContactsAdapter.notifyDataSetChanged();
                }
            }
            updateUI("updatenologtext");
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    public void updateUI(String str) {

        try {

            if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                //LOG.info("NetworkError receieved");
                ////Toast.makeText(MainActivity.context, "NetworkError", Toast.LENGTH_SHORT).show();
            } else if (str.equals("updatenologtext")) {
                LOG.info("updatenologtext");
                Cursor ccr;
                if (!editText.getText().toString().equals("")) {
                    if (contactstypetoload == 0) {
                        ccr = CSDataProvider.getSearchContactsCursor(editText.getText().toString());
                    } else {
                        ccr = CSDataProvider.getSearchContactsAndGroupsCursor(editText.getText().toString());
                    }

                } else {
                    if (contactstypetoload == 0) {
                        ccr = CSDataProvider.getContactsCursor();
                    } else {
                        ccr = CSDataProvider.getContactsAndGroupsCursor();
                    }
                }

                if (ccr.getCount() <= 0) {
                    mytextview.setVisibility(View.VISIBLE);
                } else {
                    mytextview.setVisibility(View.INVISIBLE);
                }
                ccr.close();

            } else if (str.equals(CSEvents.CSCONTACTS_ISAPPCONTACT_RESPONSE) || str.equals(CSEvents.CSCONTACTS_CONTACTSUPDATED) || str.equals(CSEvents.CSCLIENT_IMAGESDBUPDATED) || str.equals(CSEvents.CSCLIENT_USERPROFILECHANGED) || str.equals(CSEvents.CSCONTACTSANDGROUPS_CANDGUPDATED)) {
                LOG.info("isAppContactRessuccess or imagesdbupdated or contactsupdated");


                refreshview();
                //appContactsAdapter.swapCursorAndNotifyDataSetChanged(CSDataProvider.getContactsCursor());
                ////appContactsAdapter.notifyDataSetChanged();
                updateUI("updatenologtext");
            }
        } catch (Exception ex) {
        }

    }


    public boolean showoptions(final String number) {
        try {
            final ArrayList<String> grpoptions = new ArrayList<>();
            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(getActivity());
            successfullyLogin.setTitle("Options");
            successfullyLogin.setCancelable(true);
            grpoptions.clear();


            grpoptions.add("New Contact");
            grpoptions.add("New Group");
            grpoptions.add("New Direct Contact");

            SimpleTextAdapter simpleTextAdapter = new SimpleTextAdapter(MainActivity.context, grpoptions);
            successfullyLogin.setAdapter(simpleTextAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            String finalaction = grpoptions.get(which);
                            LOG.info("finalaction:" + finalaction);
                            LOG.info("number:" + number);
                            if (finalaction.equals("New Contact")) {
                                Intent intent = new Intent(Intent.ACTION_INSERT,
                                        ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(intent, 9199);

                            } else if (finalaction.equals("New Group")) {
                                Intent creategroupintent = new Intent(MainActivity.context, CreateGroupActivity.class);
                                creategroupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MainActivity.context.startActivity(creategroupintent);
                            } else if (finalaction.equals("New Direct Contact")) {

                                Intent creategroupintent = new Intent(MainActivity.context, AddContactActivity.class);
                                creategroupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MainActivity.context.startActivity(creategroupintent);

                                //CSClientObj.addContact("test","test direct contact",2);
                                //CSClientObj.addContact("testmail1@connectarena.com","test direct mail contact",2);
                                //CSClientObj.addContact("testmail2@connectarena.com","test direct mail contact",2);

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

    public static void showappcontactsoptions(final String number, final String name, boolean isappcontact) {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            final Dialog dialog = new Dialog(mActivity);
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

            //pstnLayout.setVisibility(View.GONE);

            if (isappcontact) {
                share_app_layoutview.setVisibility(View.GONE);
            } else {
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

            share_app_layoutview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                    try {
                        List<Intent> targetShareIntents = new ArrayList<Intent>();
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        PackageManager pm = mActivity.getPackageManager();
                        List<ResolveInfo> resInfos = pm.queryIntentActivities(shareIntent, 0);
                        if (!resInfos.isEmpty()) {


                            System.out.println("Have package");
                            for (ResolveInfo resInfo : resInfos) {
                                String packageName = resInfo.activityInfo.packageName;
                                Log.i("Package Name", packageName);

                                if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana")
                                        || packageName.contains("com.whatsapp") || packageName.contains("com.google.android.apps.plus")
                                        || packageName.contains("com.google.android.talk") || packageName.contains("com.slack")
                                        || packageName.contains("com.google.android.gm") || packageName.contains("com.facebook.orca")
                                        || packageName.contains("com.yahoo.mobile") || packageName.contains("com.skype.raider")
                                        || packageName.contains("com.android.mms") || packageName.contains("com.linkedin.android")
                                        || packageName.contains("com.google.android.apps.messaging") || packageName.contains("com.google.android.apps.docs")) {
                                    Intent intent = new Intent();

                                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                                    intent.putExtra("AppName", resInfo.loadLabel(pm).toString());
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    intent.putExtra(Intent.EXTRA_TEXT, "Download Konverz from https://play.google.com/store/apps/details?id=com.app.deltacubes");
                                    intent.setPackage(packageName);
                                    targetShareIntents.add(intent);
                                }
                            }
                            if (!targetShareIntents.isEmpty()) {
                                Collections.sort(targetShareIntents, new Comparator<Intent>() {
                                    @Override
                                    public int compare(Intent o1, Intent o2) {
                                        return o1.getStringExtra("AppName").compareTo(o2.getStringExtra("AppName"));
                                    }
                                });
                                isShareIntentCalled = true;
                                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Invite a friend via...");
                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                                mActivity.startActivity(chooserIntent);
                            } else {
                                Toast.makeText(mActivity, "No app to invite a friend.", Toast.LENGTH_LONG).show();
                            }


                        }


                                    /*  Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Konverz");
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "Download Konverz from https://play.google.com/store/apps/details?id=com.app.deltacubes");
                                    startActivity(Intent.createChooser(sharingIntent, "Invite Friend Via"));*/

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

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
                    //Intent intent = new Intent(mActivity, ChatActivity.class);
                    Intent intent = new Intent(mActivity, ChatAdvancedActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("Sender", number);
                    intent.putExtra("IS_GROUP", false);
                    MainActivity.context.startActivity(intent);

                }
            });
            pstnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                    utils.donewPstncall(number, mActivity);
                    //Toast.makeText(mActivity, "No Credit!", Toast.LENGTH_SHORT).show();

                }
            });
            if (dialog != null)
                dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void showgroupoptions(final String groupid) {
        try {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            final Dialog dialog = new Dialog(mActivity);
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
            LinearLayout groupoptionsLayout = dialog.findViewById(R.id.group_options_layout);
            LinearLayout callOptionsLayout = dialog.findViewById(R.id.call_options_layout);
            callOptionsLayout.setVisibility(View.GONE);
            groupoptionsLayout.setVisibility(View.VISIBLE);
            LinearLayout manageGroupLayout = dialog.findViewById(R.id.manage_group_layout);
            LinearLayout groupChatLayout = dialog.findViewById(R.id.group_chat_layout);
            TextView cancelTv = dialog.findViewById(R.id.dialog_cancel_tv);
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                }
            });

            manageGroupLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                    Intent intentt = new Intent(MainActivity.context, ManageGroupActivity.class);
                    intentt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentt.putExtra("grpid", groupid);
                    mActivity.startActivity(intentt);

                }
            });
            groupChatLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                    //Intent intent = new Intent(mActivity, ChatActivityGroup.class);
                    Intent intent = new Intent(mActivity, GroupChatAdvancedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Sender", groupid);
                    intent.putExtra("IS_GROUP", true);
                    intent.putExtra("grpname", utils.getGroupname(groupid));
                    MainActivity.context.startActivity(intent);

                }
            });

            if (dialog != null)
                dialog.show();
        } catch (Exception ex) {
            utils.logStacktrace(ex);

        }

    }

    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver in contacts:" + intent.getAction().toString());


                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    updateUI(CSEvents.CSCLIENT_NETWORKERROR);
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_IMAGESDBUPDATED)) {
                    updateUI(CSEvents.CSCLIENT_IMAGESDBUPDATED);
                } else if (intent.getAction().equals(CSEvents.CSCONTACTS_ISAPPCONTACT_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        updateUI(CSEvents.CSCONTACTS_ISAPPCONTACT_RESPONSE);
                    }
                } else if (intent.getAction().equals(CSEvents.CSCONTACTSANDGROUPS_CANDGUPDATED)) {
                    updateUI(CSEvents.CSCONTACTSANDGROUPS_CANDGUPDATED);
                } else if (intent.getAction().equals(CSEvents.CSCONTACTS_CONTACTSUPDATED)) {
                    updateUI(CSEvents.CSCONTACTS_CONTACTSUPDATED);
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_USERPROFILECHANGED)) {
                    updateUI(CSEvents.CSCLIENT_USERPROFILECHANGED);
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

            MainActivityReceiverObj = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            IntentFilter filter5 = new IntentFilter(CSEvents.CSCONTACTS_ISAPPCONTACT_RESPONSE);
            IntentFilter filter6 = new IntentFilter(CSEvents.CSCLIENT_IMAGESDBUPDATED);
            IntentFilter filter7 = new IntentFilter(CSEvents.CSCONTACTS_CONTACTSUPDATED);
            IntentFilter filter8 = new IntentFilter(CSEvents.CSCLIENT_USERPROFILECHANGED);
            IntentFilter filter9 = new IntentFilter(CSEvents.CSCONTACTSANDGROUPS_CANDGUPDATED);

            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter);
            //LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj,filter5);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter6);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter7);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter8);
            LocalBroadcastManager.getInstance(MainActivity.context).registerReceiver(MainActivityReceiverObj, filter9);
            if (isShareIntentCalled) {
                isShareIntentCalled = false;
            } else {
                hideKeyBoard();
                refreshview();
            }
            Cursor cursor = CSDataProvider.getContactsAndGroupsCursor();
            cursor.close();


           /* for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                Log.i("FirstCallContacts" , "Name is " + cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACTORGROUP_NAME))+" number "+cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACTORGROUP_DESC)));
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
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
 /*
    public void donewVideocall(String numbertodial) {

        try {
if(CSClientObj.getNetworkstatus()) {
    if (!numbertodial.equals("") && !numbertodial.equals(GlobalVariables.phoneNumber)) {

        Intent intent = new Intent(mActivity, PlayNewVideoCallActivity.class);
        intent.putExtra("dstnumber", numbertodial);
        intent.putExtra("isinitiatior", true);
        startActivityForResult(intent, 954);
    } else {
        Toast.makeText(mActivity, "No valid Number", Toast.LENGTH_SHORT).show();
    }
} else {
   utils.showSettingsAlert("Couldn't place call. Please check internet connection and try again",mActivity);
}
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }



    public void donewvoicecall(String numbertodial) {

        try {

            LOG.info("Doing new outgoing call");

            if(CSClientObj.getNetworkstatus()) {
            if (!numbertodial.equals("")&&!numbertodial.equals(GlobalVariables.phoneNumber)) {


                Intent intent = new Intent(mActivity, PlayNewAudioCallActivity.class);
                intent.putExtra("dstnumber", numbertodial);
                intent.putExtra("isinitiatior", true);
                startActivityForResult(intent, 954);
            } else {
                Toast.makeText(mActivity, "No valid Number", Toast.LENGTH_SHORT).show();
            }
            } else {
                utils.showSettingsAlert("Couldn't place call. Please check internet connection and try again",mActivity);
            }

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
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
        } catch(Exception ex) {
            return false;
        }

    }*/

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
        editText.clearFocus();
        editText.setText("");
    }

    private void setAdapter(CorgAdapter appContactsAdapter) {
        try {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.context);
            //rv.addItemDecoration(new DividerItemDecoration(ChatAdvancedActivity.this, LinearLayoutManager.VERTICAL));
            //rv.addItemDecoration(new MyDividerItemDecoration(ChatAdvancedActivity.this, DividerItemDecoration.VERTICAL, 36));

            //DividerItemDecoration divider = new DividerItemDecoration(MainActivity.context, DividerItemDecoration.VERTICAL);
            //divider.setDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.custom_divider));
            //rv.addItemDecoration(divider);
            rv.setNestedScrollingEnabled(false);
            rv.setLayoutManager(mLayoutManager);
            rv.setItemAnimator(new DefaultItemAnimator());
            rv.setAdapter(appContactsAdapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void handleclick(int position, int action) {
        try {

            String searchstring = editText.getText().toString();

            if (action == 1) {
                Cursor cur;
                if (contactstypetoload == 0) {
                    if (searchstring.equals("")) {
                        cur = CSDataProvider.getContactsCursor();
                    } else {
                        cur = CSDataProvider.getSearchContactsCursor(searchstring);
                    }
                    LOG.info("Number of contacts:" + cur.getCount());
                    cur.moveToPosition(position);

                    String number = "";
                    String name = "";
                    name = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
                    number = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER));
                    String contactid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID));
                    int isappcontact = cur.getInt(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_IS_APP_CONTACT));
                    LOG.info("Number to call:" + number);
                    cur.close();

                    if (!number.equals("")) {


                        if (isappcontact == 0) {


                            showappcontactsoptions(number, name, false);

                        } else {
                            showappcontactsoptions(number, name, true);
                        }
                    }


                } else {
                    if (searchstring.equals("")) {
                        cur = CSDataProvider.getContactsAndGroupsCursor();
                    } else {
                        cur = CSDataProvider.getSearchContactsAndGroupsCursor(searchstring);
                    }


                    cur.moveToPosition(position);

                    String iscontactorgroup = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_IS_CONTACTORGROUP));
                    if (iscontactorgroup.equals(CSConstants.GROUP)) {

                        String groupid = "";
                        groupid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_ID));
                        LOG.info("groupid:" + groupid);
                        showgroupoptions(groupid);

                    } else {
                        String number = "";
                        String name = "";
                        name = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_NAME));
                        number = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
                        LOG.info("Number to call:" + number);
                        showappcontactsoptions(number, name, true);


                    }
                    cur.close();
                }
            } else {

                Cursor cursor;


                if (FirstCallContacts.contactstypetoload == 0) {


                    if (searchstring.equals("")) {
                        cursor = CSDataProvider.getContactsCursor();
                    } else {
                        cursor = CSDataProvider.getSearchContactsCursor(searchstring);
                    }
                    cursor.moveToPosition(position);


                    String id = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER));
                    int isappcontact = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_IS_APP_CONTACT));
                    boolean isapppcontact = false;
                    if (isappcontact == 1) {
                        isapppcontact = true;
                    } else {
                        isapppcontact = false;
                    }


                    if (isapppcontact) {

                        showManageUser(number, name, id);
                    } else {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Konverz");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Download Konverz from https://play.google.com/store/apps/details?id=com.app.deltacubes");
                        mActivity.startActivity(sharingIntent);
                    }


                } else {

                    if (searchstring.equals("")) {
                        cursor = CSDataProvider.getContactsAndGroupsCursor();
                    } else {
                        cursor = CSDataProvider.getSearchContactsAndGroupsCursor(searchstring);
                    }
                    cursor.moveToPosition(position);

                    final String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
                    String id = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACTORGROUP_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_NAME));
                    String iscontactorgroup = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_IS_CONTACTORGROUP));

                    if (iscontactorgroup.equals(CSConstants.GROUP)) {
                        Intent intent = new Intent(mActivity, ManageGroupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("grpid", id);
                        mActivity.startActivity(intent);

                    } else {
                        showManageUser(number, name, id);
                    }
                }
                cursor.close();
            }

            try {

                //final View kjhkj = view;
                View view = mActivity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    LOG.info("but view is null hideKeyboard");
                }
            } catch (Exception ex) {
                utils.logStacktrace(ex);
            }

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }


    private static void showManageUser(String number, String name, String nativecontactid) {
        try {
            String description = "";
            String picid = "";
            Cursor cur = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                picid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
                description = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_DESCRIPTION));

            }
            cur.close();

            Intent intent = new Intent(mActivity, ManageUserActivity.class);
            Log.i("C", "Conatct number " + number);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("name", name);
            intent.putExtra("number", number);
            intent.putExtra("description", description);
            intent.putExtra("picid", picid);
            intent.putExtra("nativecontactid", nativecontactid);
            mActivity.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
