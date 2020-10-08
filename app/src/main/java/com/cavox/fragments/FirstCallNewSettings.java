package com.cavox.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;

import com.ca.wrapper.CSClient;
import com.ca.wrapper.CSDataProvider;
import com.cavox.adapaters.SettingsAdapter;
import com.app.deltacubes.R;
import com.cavox.konverz.ChatFtSettingsActivity;
import com.cavox.konverz.InfoActivity;
import com.cavox.konverz.ResetPasswordActivity;
import com.cavox.konverz.StartActivity;
import com.cavox.konverz.UserProfileActivity;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;

import static com.cavox.utils.GlobalVariables.LOG;

public class FirstCallNewSettings extends Fragment {
    public static boolean showlogoptions = false;
    public static boolean fileloggingstarted = false;
    ArrayList<String> settings = new ArrayList<>();
    SettingsAdapter settingsAdapter;
    ListView listView;
    static FragmentActivity mActivity;
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    Handler h = new Handler();
    Runnable RunnableObj;
    private RelativeLayout mProfileLayout;
    private ImageView mProfileImage;
    private TextView mProfileNameTv, mProfileStatusTv;
    int delay = 30000;
    /**
     * ImageLoader variables
     */
    DisplayImageOptions mOptions;
    ImageLoader mImageLoader;
    private String TAG = "FirstCallNewSettings";

    public FirstCallNewSettings() {
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
        return inflater.inflate(R.layout.simplelist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LOG.info("onViewCreated:");

        CSClient CSClientobj = new CSClient();

        listView = (ListView) view.findViewById(R.id.settingslistview);
        mProfileImage = view.findViewById(R.id.profile_image);
        mProfileLayout = view.findViewById(R.id.profile_layout);
        mProfileNameTv = view.findViewById(R.id.profile_name_tv);
        mProfileStatusTv = view.findViewById(R.id.profile_status_tv);
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        }
        //listView.setItemsCanFocus(true);
        mOptions = new DisplayImageOptions.Builder()
                /*.showImageOnLoading(R.drawable.img_profile_default)
                .showImageForEmptyUri(R.drawable.img_profile_default)
                .showImageOnFail(R.drawable.img_profile_default)*/
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        getSelfProfile();
        settings.clear();
        settings.add("Change Password");
        settings.add("Record All Calls");
        settings.add("Chat Files Download Settings");
        settings.add("About");
        if (showlogoptions) {
            if (!settings.contains("Send Recent Logs")) {
                settings.add("Send Recent Logs");
                if (fileloggingstarted) {
                    settings.add("Stop logging and send logs");
                } else {
                    settings.add("Start logging");
                }
                settings.add("Make all files public");
                settings.add("Enable Group Activity in Chat");
                settings.add("Enable CallLog Activity in Chat");
                settings.add("Enable User Activity in Chat");
                settings.add("Log out");
                settingsAdapter = new SettingsAdapter(mActivity, settings, 0);
                listView.setAdapter(settingsAdapter);
            }
        } else {
            settingsAdapter = new SettingsAdapter(mActivity, settings, 0);
            listView.setAdapter(settingsAdapter);
        }

        mProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, UserProfileActivity.class);
                intent.putExtra("isCameForEdit", true);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                try {
                    switch (position) {
                        case 0:
                            startActivity(new Intent(mActivity, ResetPasswordActivity.class));
                            break;
                        case 1:

                            break;

                        case 2:
                            startActivity(new Intent(mActivity, ChatFtSettingsActivity.class));
                            break;

                        case 3:
                            startActivity(new Intent(mActivity, InfoActivity.class));
                            break;

                        case 4:
                            String filepath = Environment.getExternalStorageDirectory() + "/konverz_recent_logcat.log";
                            startLogCapture(filepath);

                            ArrayList<Uri> uris = new ArrayList<Uri>();
                            //uris.add(Uri.fromFile(new File(filepath)));
                            uris.add(FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(filepath)));


                            shareViaEmail(uris, "Konverz recent logcat");

                            break;
                        case 5:

                            try {

                                String sdklogfilepath = Environment.getExternalStorageDirectory() + "/konverz_sdk_logs.log";
                                String logcatfilepath = Environment.getExternalStorageDirectory() + "/konverz_logcat.log";

                                if (!fileloggingstarted) {
                                    fileloggingstarted = true;
                                    settings.set(5, "Stop logging and send logs");


                                    CSClientobj.startFileLogging(sdklogfilepath, CSConstants.LOG_LEVEL_ALL, false);
                                    CSClientobj.startwritingLogcatToFile(logcatfilepath, false);

                                } else {
                                    fileloggingstarted = false;
                                    settings.set(5, "Start logging");

                                    CSClientobj.stopFileLogging();
                                    CSClientobj.stopwritingLogcatToFile();

                                    ArrayList<Uri> uris1 = new ArrayList<Uri>();
                                    //uris1.add(Uri.fromFile(new File(sdklogfilepath)));
                                    //uris1.add(Uri.fromFile(new File(logcatfilepath)));

                                    uris1.add(FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(sdklogfilepath)));
                                    uris1.add(FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(logcatfilepath)));

                                    shareViaEmail(uris1, "Konverz logs");

                                }

                                settingsAdapter = new SettingsAdapter(mActivity, settings, 0);
                                listView.setAdapter(settingsAdapter);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            break;
                        case 10:
                            showlogoutalert();
                            break;


                        default:
                            break;
                    }
                } catch (Exception ex) {

                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                Log.i(TAG,"pos:"+pos);
                switch (pos) {
                    case 3:
                        showlogoptions = true;
                        if (!settings.contains("Send Recent Logs")) {
                            settings.add("Send Recent Logs");
                            if (fileloggingstarted) {
                                settings.add("Stop logging and send logs");
                            } else {
                                settings.add("Start logging");
                            }
                            settings.add("Make all files public");
                            settings.add("Enable Group Activity in Chat");
                            settings.add("Enable CallLog Activity in Chat");
                            settings.add("Enable User Activity in Chat");
                            settings.add("Log out");
                            settingsAdapter = new SettingsAdapter(mActivity, settings, 0);
                            listView.setAdapter(settingsAdapter);
                        }
                        break;
                }
                return true;
            }
        });
        MainActivityReceiverObj = new MainActivityReceiver();
        IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
        IntentFilter filter1 = new IntentFilter(CSEvents.CSCLIENT_IMAGESDBUPDATED);
        IntentFilter filter2 = new IntentFilter(CSEvents.CSCLIENT_SETPROFILE_RESPONSE);

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(MainActivityReceiverObj, filter);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(MainActivityReceiverObj, filter1);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(MainActivityReceiverObj, filter2);
    }


    public void updateUI(String str) {

        try {

            if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                LOG.info("NetworkError receieved");

                //dismissprogressbar();
                //Toast.makeText(mActivity, "NetworkError", Toast.LENGTH_SHORT).show();


            }

        } catch (Exception ex) {
        }

    }

    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver in settings " + intent.getAction());


                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    updateUI(CSEvents.CSCLIENT_NETWORKERROR);
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_IMAGESDBUPDATED)) {
                    getSelfProfile();
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_SETPROFILE_RESPONSE)) {
                    getSelfProfile();
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_USERPROFILECHANGED)&& (intent.getStringExtra("number") == GlobalVariables.phoneNumber)) {
                    getSelfProfile();
                }
            } catch (Exception ex) {
                utils.logStacktrace(ex);
            }
        }


    }

    public boolean showalert(String result) {
        try {
            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(mActivity);
            successfullyLogin.setTitle(result);
            //successfullyLogin.setMessage("Looks your friends not yet using AirYou.");
            successfullyLogin.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

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

    public void showprogressbar() {
        try {
            if (mActivity != null) {
                progressBarStatus = 0;
                progressBar = new ProgressDialog(mActivity);
                progressBar.setCancelable(false);
                progressBar.setMessage("Please Wait..");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                //progressBar.setMax(time);
                progressBar.show();
                h = new Handler();
                RunnableObj = new Runnable() {
                    public void run() {
                        h.postDelayed(this, delay);
                        mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                dismissprogressbar();
                                Toast.makeText(mActivity, "NetworkError", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };
                h.postDelayed(RunnableObj, delay);
            }
        } catch (Exception ex) {
        }
    }

    public void dismissprogressbar() {
        try {
            LOG.info("dismissprogressbar5");
            if (progressBar != null) {
                progressBar.dismiss();

            }
            if (h != null) {
                h.removeCallbacks(RunnableObj);
            }
        } catch (Exception ex) {
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
            //Code here
            //LOG.info("On attach called");

            mActivity = (FragmentActivity) activity;

        }
    }


    MainActivityReceiver MainActivityReceiverObj = new MainActivityReceiver();

    @Override
    public void onResume() {
        super.onResume();

        try {


            getSelfProfile();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(MainActivityReceiverObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startLogCapture(String filepath) {
        try {
         new CSClient().getRecentLogcat(filepath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void shareViaEmail(ArrayList<Uri> uris, String subject) {
        try {
            //String subject = "Konverz Recent Logcat";
            //String filelocation = Environment.getExternalStorageDirectory()+"/konverz.log";
            //Intent intent = new Intent(Intent.ACTION_SENDTO);
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"srinivasarao@voxvalley.com"});
            intent.setType("text/plain");
            String message = "Describe the issue here..";
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            //intent.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filepath));
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            //intent.setData(Uri.parse("mailto:"+"srinivas@connectarena.com"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSelfProfile() {
        Cursor cur = CSDataProvider.getSelfProfileCursor();
        if (cur.getCount() > 0) {
            cur.moveToNext();
            String imageid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_PROFILEPICID));
            String username = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_USERNAME));
            String presence = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_DESCRIPTION));
            mProfileNameTv.setText(username);
            mProfileStatusTv.setText(presence);
            //Bitmap profileBitMap = CSDataProvider.getImageBitmap(imageid);
           /* if (profileBitMap != null) {
                LOG.info("setting profile image");
                mProfileImage.setImageBitmap(profileBitMap);
            } else {
                mProfileImage.setImageResource(R.drawable.defaultcontact);
                LOG.info("profile image is null");
            }*/
            String filepath = CSDataProvider.getImageFilePath(imageid);
            Glide.with(mActivity)
                    .load(Uri.fromFile(new File(filepath)))
                    .apply(new RequestOptions().error(R.drawable.defaultcontact))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(filepath).length()+new File(filepath).lastModified()))))
                    .into(mProfileImage);
        }
        cur.close();
    }
    public boolean showlogoutalert() {
        try {


            //Testit TestitObj = new Testit();
            //TestitObj.testprint();


            AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(mActivity);
            successfullyLogin.setTitle("Confirmation");

            successfullyLogin.setMessage("On Log out all chat or call log history will be lost. Do you want to log out?");

            successfullyLogin.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Intent intentt = new Intent(mActivity, StartActivity.class);
                            intentt.putExtra("reset","yes");
                            startActivity(intentt);
                            mActivity.finish();
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
}
