package com.cavox.konverz;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.app.deltacubes.R;
import com.ca.wrapper.CSClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.multidex.MultiDex;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;
import com.cavox.adapaters.ManageGroupAppContactsAdapter;
import com.cavox.adapaters.SimpleTextAdapter;
import com.cavox.utils.AppConstants;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;
import com.ca.wrapper.CSGroups;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.cavox.utils.GlobalVariables.LOG;

//import com.cavox.uiutils.UIActions;

public class ManageGroupActivity extends AppCompatActivity {
    static String grpid = "";
    static int is_grp_active = 0;
    static String owner = CSConstants.GROUPADMINSOMEONEELSE;
    String MyRole = "";
    String grpname = "";
    String grpdescription = "";
    String grppicid = "";

    ManageGroupAppContactsAdapter mGroupContactsAdapter;
    ListView mListView;
    ImageView grpimg;
    NestedScrollView scrollView;
    TextView groupcountview;
    Toolbar toolbar;
    FloatingActionButton fab;
    TextView subtitle;
    CSGroups IAmLiveCoreSendObj = new CSGroups();
    Button exitordeletegrp;
    TextView addmember;
    private RelativeLayout mProfileLayout;
    private ImageView profile_image;
    /**
     * ImageLoader variables
     */
    DisplayImageOptions mOptions;
    ImageLoader mImageLoader;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_scrolling);
        try {
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            exitordeletegrp = (Button) findViewById(R.id.exit_grp);
            RelativeLayout golive = (RelativeLayout) findViewById(R.id.ivContactItem6);
            grpimg = (ImageView) findViewById(R.id.grpimg);
            groupcountview = (TextView) findViewById(R.id.group_count);
            addmember = (TextView) findViewById(R.id.addmember);
            mListView = (ListView) findViewById(R.id.appcontacts);
            scrollView = (NestedScrollView) findViewById(R.id.scrollView);
            addmember.setVisibility(View.GONE);
            ImageView goLiveTV = (ImageView) findViewById(R.id.ivContactItem5);
            subtitle = (TextView) findViewById(R.id.subtitle);
            mProfileLayout = findViewById(R.id.profile_layout);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            profile_image = findViewById(R.id.profile_image);
            collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
            //toolbar.setSubtitle("Yes its subtitle");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            MainActivity.context = getApplicationContext();
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));

            mOptions = new DisplayImageOptions.Builder()
                    /*.showImageOnLoading(R.drawable.img_profile_default)
                    .showImageForEmptyUri(R.drawable.img_profile_default)
                    .showImageOnFail(R.drawable.img_profile_default)*/
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            grpid = getIntent().getStringExtra("grpid");

            GlobalVariables.phoneNumber = CSDataProvider.getLoginID();

            Cursor cur = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, grpid);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                owner = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_ADMIN));
            }
            cur.close();


            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (verticalOffset == 0) {
                        //LOG.info("Offset 0");
                        subtitle.setPadding(72, 0, 0, 0);
                    } else {
                        subtitle.setPadding(112, 0, 0, 0);
                        //LOG.info("Offset 1");
                    }
                }
            });


            LOG.info("Manage Group GroupId:" + grpid);
            LOG.info("Manage Group owner:" + owner);

            updateGrpNameAndImage();
            setCountView();


            Cursor cur1 = CSDataProvider.getGroupContactsCursorFilteredByGroupIdandNumber(grpid, GlobalVariables.phoneNumber);
            if (cur1.getCount() > 0) {

                cur1.moveToNext();

                MyRole = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_GROUPCONTACTS_ROLE));

            }

            cur1.close();

            Cursor cor = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, grpid);
            if (cor.getCount() > 0) {
                cor.moveToNext();
                is_grp_active = cor.getInt(cor.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_IS_ACTIVE));
                if(is_grp_active == 1) {
                   if(cor.getInt(cor.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_IS_BLOCKED))==0) {
                       is_grp_active = 1;
                    } else {
                       is_grp_active = 0;
                   }
                }

                System.out.println("TEST IS GROUP ACTIVE MANAGE GROUP:" + is_grp_active);
            }
            cor.close();


            LOG.info("MyRole:" + MyRole);


            if (is_grp_active == 1) {
                if (MyRole.equals(CSConstants.ADMIN)) {

                    MyRole = "SuperAdmin";
                    exitordeletegrp.setText("DELETE GROUP");
                    addmember.setVisibility(View.VISIBLE);
				/*
				addmember.setVisibility(View.VISIBLE);
                GlobalVariables.phoneNumber = CSDataProvider.getSetting(CSDbFields.KEY_SETINGS_PHONENUMBER);
                if(owner.equals(GlobalVariables.phoneNumber)) {
	MyRole = "SuperAdmin";
	exitordeletegrp.setText("DELETE GROUP");
} else {
	exitordeletegrp.setText("EXIT GROUP");
}
*/


                } else if (MyRole.equals(CSConstants.MEMBER)) {
                    fab.setVisibility(View.GONE);
                    addmember.setVisibility(View.GONE);
                    exitordeletegrp.setText("EXIT GROUP");
                }
            } else {
                disableUI();
            }


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();

                }
            });

            mProfileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (profile_image.getVisibility() == View.VISIBLE) {
                        try {
                           /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();*/
                            Intent intent = new Intent(ManageGroupActivity.this, ShowImageActivity.class);
                            intent.putExtra("contactName", grpname);
                            intent.putExtra("myBitMap", grpid);
                            intent.putExtra("isGroup", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent,45);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                    }
                }
            });

            golive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        //goLive(grpid);
                    } catch (Exception ex) {

                    }
                }
            });
            addmember.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (!utils.isinternetavailable(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intentt = new Intent(ManageGroupActivity.this, ShowAppContactsMultiSelectActivity.class);
                        intentt.putExtra("uiaction", AppConstants.UIACTION_ADDCONTACTSTOGROUP);
                        startActivityForResult(intentt, 891);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (!utils.isinternetavailable(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intentt = new Intent(ManageGroupActivity.this, EditGroupInfoActivity.class);
                        intentt.putExtra("grpid", grpid);
                        startActivityForResult(intentt,46);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            exitordeletegrp.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (!utils.isinternetavailable(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (is_grp_active == 1) {
                            if (MyRole.equals("SuperAdmin")) {
                                showalert(0);

                            } else {
                                showalert(1);
                            }
                        } else {
                            CSDataProvider.deleteGroup(grpid);
                            CSDataProvider.deleteGroupContacts(grpid);
                            onBackPressed();
                        }
                    } catch (Exception ex) {

                    }
                }
            });

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if (is_grp_active == 1) {


                        Cursor cursor = CSDataProvider.getGroupContactsCursorByFilter(CSDbFields.KEY_GROUPCONTACTS_GROUP_ID, grpid);
                        cursor.moveToPosition(position);
                        String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_GROUPCONTACTS_CONTACT));
                        String contactrole = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_GROUPCONTACTS_ROLE));

                        if (cursor.getCount() > 0) {
                            if (MyRole.equals("SuperAdmin")) {
                                showoptions(0, number, contactrole);
                            } else if (MyRole.equals(CSConstants.ADMIN)) {
                                showoptions(1, number, contactrole);
                            } else if (MyRole.equals(CSConstants.MEMBER)) {
                                showoptions(2, number, contactrole);
                            }
                        }
                        cursor.close();
                    } else {
                        shownomemberalert();
                    }


                }
            });
            goLiveTV.requestFocus();


        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 891 && resultCode == Activity.RESULT_OK && data != null) {
            LOG.info("Got contactnumbers to add to group");

            List<String> numbers = data.getStringArrayListExtra("contactnumbers");
            IAmLiveCoreSendObj.addMembersToGroup(grpid, numbers);
            numbers.clear();


        } else if (requestCode == 823) {
            recreate();
            //updateUI(CSEvents.imageanddes");
        }
        updateGrpNameAndImage();
    }

    ;


    public void setListviewheight() {

        try {
            Cursor cur = CSDataProvider.getGroupContactsCursorByFilter(CSDbFields.KEY_GROUPCONTACTS_GROUP_ID, grpid);
            int count = cur.getCount();
            LinearLayout.LayoutParams list = (LinearLayout.LayoutParams) mListView.getLayoutParams();
            list.height = (int) getResources().getDimension(R.dimen._100sdp) * count;//like int  200
            mListView.setLayoutParams(list);
            mListView.requestLayout();
            cur.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void updateGrpNameAndImage() {

        try {
            Cursor cur = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, grpid);
            if (cur.getCount() > 0) {

                cur.moveToNext();
                grpname = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_NAME));
                grpdescription = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_DESC));
                grppicid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_PROFILE_PIC));
                LOG.info("grpname ok:" + grpname);
                LOG.info("grpdescription ok:" + grpdescription);
                try {
			/*toolbar = (Toolbar) findViewById(R.id.toolbar);
			toolbar.setTitle(grpname);
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

                    getSupportActionBar().setTitle(grpname);
                    collapsingToolbarLayout.setTitle(grpname);
                    subtitle.setText(grpdescription);
                    applyAnimationToSubTitle();
			/*runOnUiThread(new Runnable() {
				@Override
				public void run() {
					toolbar = (Toolbar) findViewById(R.id.toolbar);
					toolbar.setTitle(grpname);
					setSupportActionBar(toolbar);
					getSupportActionBar().setDisplayHomeAsUpEnabled(true);
					//getSupportActionBar().setTitle(grpname);
				}});
*/
                } catch (Exception ex) {
                    utils.logStacktrace(ex);
                }

		/*runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LOG.info("runOnUiThread:"+grpname);
				toolbar = (Toolbar) findViewById(R.id.toolbar);
				setSupportActionBar(toolbar);
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setTitle(grpname);
			}
		});
*/
                //getSupportActionBar().setSubtitle(grpdescription);
                //toolbar.setTitle(grpname);
                //toolbar.setSubtitle(grpdescription);
                //setSupportActionBar(toolbar);
                //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                String filepath = CSDataProvider.getImageFilePath(grppicid);
                mImageLoader.clearDiskCache();
                mImageLoader.clearMemoryCache();
                mImageLoader.loadImage("file://" + filepath, mOptions, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        // cache is now warmed up
                        if (loadedImage != null) {
                            profile_image.setVisibility(View.VISIBLE);
                            profile_image.setImageBitmap(loadedImage);
                            grpimg.setVisibility(View.GONE);
                        } else {
                            new ImageDownloaderTask(grpimg).execute(grppicid);
                        }

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        new ImageDownloaderTask(grpimg).execute(grppicid);
                    }
                });

               /* if (grppicid != null && !grppicid.equals("")) {
                    new ImageDownloaderTask(grpimg).execute(grppicid);
                }*/
            } else {
                LOG.info("not setting grp name:" + cur.getCount());
            }
            cur.close();

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    public void updateGrpNameAndImage1(String imageid) {

        try {
            Cursor cur = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, grpid);
            if (cur.getCount() > 0) {

                cur.moveToNext();
                grpname = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_NAME));
                grpdescription = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_DESC));
                grppicid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_PROFILE_PIC));
                LOG.info("grpname ok:" + grpname);
                LOG.info("grpdescription ok:" + grpdescription);

                if (grppicid.equals(imageid)) {

                    try {
                        getSupportActionBar().setTitle(grpname);
                    } catch (Exception ex) {
                        utils.logStacktrace(ex);
                    }

                    if (grppicid != null && !grppicid.equals("")) {
                        new ImageDownloaderTask(grpimg).execute(grppicid);
                    }

                } else {
                    LOG.info("not setting grp name:" + cur.getCount());
                }
            } else {
                LOG.info("not setting grp name:" + cur.getCount());
            }
            cur.close();

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    public void setCountView() {

        try {
            int groupcontactscount = 0;
            Cursor cr = CSDataProvider.getContactsCursor();
            int totalcount = cr.getCount();
            cr.close();
            Cursor cur2 = CSDataProvider.getGroupContactsCursorByFilter(CSDbFields.KEY_GROUPCONTACTS_GROUP_ID, grpid);
            groupcontactscount = cur2.getCount();
            cur2.close();
            groupcountview.setText(groupcontactscount + " Participants");
        } catch (Exception ex) {

        }
    }

    public void updateUI(String str) {

        try {
            if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                LOG.info("NetworkError receieved");
                //Toast.makeText(this, "NetworkError", Toast.LENGTH_SHORT).show();
            } else if (str.equals(CSEvents.CSGROUPS_EXITGROUP_RESPONSE)) {

                LOG.info("CSGROUPS_EXITGROUP_RESPONSE receieved:" + grpid);
                disableUI();

            } else if (str.equals(CSEvents.CSGROUPS_DELETEGROUP_RESPONSE)) {
                LOG.info("CSGROUPS_DELETEGROUP_RESPONSE receieved:" + grpid);
                CSDataProvider.deleteGroup(grpid);
                CSDataProvider.deleteGroupContacts(grpid);
                onBackPressed();

            } else if (str.equals(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE)) {
                is_grp_active = 1;
                mGroupContactsAdapter = new ManageGroupAppContactsAdapter(ManageGroupActivity.this, CSDataProvider.getGroupContactsCursorByFilter(CSDbFields.KEY_GROUPCONTACTS_GROUP_ID, grpid), 0);
                setListviewheight();
                mListView.setAdapter(mGroupContactsAdapter);
                setCountView();

                Cursor ccr = CSDataProvider.getGroupContactsCursorByFilter(CSDbFields.KEY_GROUPCONTACTS_GROUP_ID, grpid);
                if (ccr.getCount() > 0) {
                    while (ccr.moveToNext()) {
                        String number = ccr.getString(ccr.getColumnIndexOrThrow(CSDbFields.KEY_GROUPCONTACTS_CONTACT));
                        if (number.equals(GlobalVariables.phoneNumber)) {
                            String role = ccr.getString(ccr.getColumnIndexOrThrow(CSDbFields.KEY_GROUPCONTACTS_ROLE));
                            if (role.equals(CSConstants.ADMIN)) {
                                if (fab != null) {
                                    fab.setVisibility(View.VISIBLE);
                                }

                                MyRole = "SuperAdmin";
                                exitordeletegrp.setText("DELETE GROUP");
                                addmember.setVisibility(View.VISIBLE);
                            } else {
                                if (fab != null) {
                                    fab.setVisibility(View.GONE);
                                }
                                addmember.setVisibility(View.GONE);
                                exitordeletegrp.setText("EXIT GROUP");
                            }
                            break;
                        }
                    }
                }
                ccr.close();
            } else if (str.equals(CSEvents.CSGROUPS_DELETEDFROMGROUP)) {
                //onBackPressed();
                disableUI();
            } else if (str.equals(CSEvents.CSCLIENT_IMAGESDBUPDATED)) {
                //recreate();
                updateGrpNameAndImage();
            } else if (str.equals(CSEvents.CSGROUPS_GROUPINFO_UPDATED)) {
                //recreate();
                updateGrpNameAndImage();
            } else if (str.equals("imageanddes")) {

                updateGrpNameAndImage();
                //recreate();
            } else if (str.equals(CSEvents.CSGROUPS_GROUPDELETEDTOGROUP)) {


            } else if (str.equals(CSEvents.CSGROUPS_UPDATEGROUPINFO_RESPONSE)) {
                updateGrpNameAndImage();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                LOG.info("Yes Something receieved in RecentReceiver");
                if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
                    updateUI(CSEvents.CSCLIENT_NETWORKERROR);
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_EXITGROUP_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        updateUI(CSEvents.CSGROUPS_EXITGROUP_RESPONSE);
                    }
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_DELETEGROUP_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        updateUI(CSEvents.CSGROUPS_DELETEGROUP_RESPONSE);
                    }
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        if (intent.getStringExtra("groupid").equals(grpid)) {
                            updateUI(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE);
                        }
                    }
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_DELETEDFROMGROUP)) {
                    if (intent.getStringExtra("groupid").equals(grpid)) {
                        updateUI(CSEvents.CSGROUPS_DELETEDFROMGROUP);
                    }
                } else if (intent.getAction().equals(CSEvents.CSCLIENT_IMAGESDBUPDATED)) {
                    LOG.info("imagesdbupdated receieved:");
                    updateGrpNameAndImage();
                    //recreate();
                    //updateUI(CSEvents.CSCLIENT_IMAGESDBUPDATED");
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_GROUPINFO_UPDATED)) {
                    LOG.info("CSGROUPS_GROUPINFO_UPDATED receieved:");
                    updateGrpNameAndImage();
                    recreate();
                    //updateUI(CSEvents.CSGROUPS_GROUPINFO_UPDATED");
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_ADDADMINSTOGROUP_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        updateUI(CSEvents.CSGROUPS_ADDADMINSTOGROUP_RESPONSE);
                    }
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_DELADMINSTOGROUP_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        updateUI(CSEvents.CSGROUPS_DELADMINSTOGROUP_RESPONSE);
                    }
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_GROUPDELETEDTOGROUP)) {
                    if (intent != null) {
                        if (intent.getStringExtra("groupid").equals(grpid)) {
                            //onBackPressed();
                            disableUI();
                        }
                    }

                    updateUI(CSEvents.CSGROUPS_GROUPDELETEDTOGROUP);
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_DELETEUSERFROMGROUP_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        updateUI(CSEvents.CSGROUPS_DELETEUSERFROMGROUP_RESPONSE);
                    }
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_ADDMEMBERS_TOGROUP_RESPONSE)) {
                    if (intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
                        updateUI(CSEvents.CSGROUPS_ADDMEMBERS_TOGROUP_RESPONSE);
                        String groupid = intent.getStringExtra("groupid");
                        if (groupid.equals(grpid)) {
                            //IAmLiveCoreSendObj.pullGroupDetails(grpid);
                        }
                    } else {
                        Toast.makeText(ManageGroupActivity.this, "Add contacts failed", Toast.LENGTH_SHORT).show();

                    }
                } else if (intent.getAction().equals(CSEvents.CSGROUPS_UPDATEGROUPINFO_RESPONSE)) {
                    updateUI(CSEvents.CSGROUPS_UPDATEGROUPINFO_RESPONSE);
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

            //mListView.setLayoutDirection();

            mGroupContactsAdapter = new ManageGroupAppContactsAdapter(ManageGroupActivity.this, CSDataProvider.getGroupContactsCursorByFilter(CSDbFields.KEY_GROUPCONTACTS_GROUP_ID, grpid), 0);
            setListviewheight();
            mListView.setAdapter(mGroupContactsAdapter);

            MainActivityReceiverObj = new MainActivityReceiver();
            IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
            IntentFilter filter1 = new IntentFilter(CSEvents.CSGROUPS_EXITGROUP_RESPONSE);
            IntentFilter filter2 = new IntentFilter(CSEvents.CSGROUPS_DELETEGROUP_RESPONSE);
            IntentFilter filter3 = new IntentFilter(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE);
            IntentFilter filter4 = new IntentFilter(CSEvents.CSGROUPS_DELETEDFROMGROUP);
            IntentFilter filter5 = new IntentFilter(CSEvents.CSCLIENT_IMAGESDBUPDATED);
            IntentFilter filter6 = new IntentFilter(CSEvents.CSGROUPS_GROUPINFO_UPDATED);

            IntentFilter filter7 = new IntentFilter(CSEvents.CSGROUPS_ADDADMINSTOGROUP_RESPONSE);
            //IntentFilter filter8 = new IntentFilter(CSEvents.CSGROUPS_ADDADMINSTOGROUP_RESPONSE);
            IntentFilter filter9 = new IntentFilter(CSEvents.CSGROUPS_GROUPDELETEDTOGROUP);
            IntentFilter filter10 = new IntentFilter(CSEvents.CSGROUPS_DELETEUSERFROMGROUP_RESPONSE);
            IntentFilter filter11 = new IntentFilter(CSEvents.CSGROUPS_ADDMEMBERS_TOGROUP_RESPONSE);
            IntentFilter filter12 = new IntentFilter(CSEvents.CSGROUPS_UPDATEGROUPINFO_RESPONSE);
            //IntentFilter filter12 = new IntentFilter(CSEvents.addcontactstogroupfailure);


            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter1);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter2);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter3);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter4);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter5);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter6);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter7);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter8);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter9);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter10);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter11);
            //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter12);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj, filter12);

            IAmLiveCoreSendObj.pullGroupDetails(grpid);


        } catch (Exception ex) {
            ex.printStackTrace();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.exitmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		/* switch (item.getItemId()) {
	        case R.id.exit:
	        	
	        	
	        finish();
	    
	        //LOG.info("SR_TEMP_LOG:exited");
	        return true;
	       
	        default:
	        return super.onOptionsItemSelected(item);
	        }*/
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            //Intent intent = new Intent(UIActions.MANAGEGRPDONE.getKey());
            //LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent);
            finish();
        } catch (Exception ex) {

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public boolean showoptions(final int action, final String number, final String contactrole) {
        try {
            final ArrayList<String> grpoptions = new ArrayList<>();

            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(ManageGroupActivity.this);
            successfullyLogin.setTitle("Options");
            successfullyLogin.setCancelable(true);
            grpoptions.clear();

//0-->superadmin
//1--->Admin
//2-->Member

            switch (action) {
                case 0:

                    if (contactrole.equals(CSConstants.ADMIN) && !number.equals(GlobalVariables.phoneNumber)) {
                        grpoptions.add("Remove as Admin");
                        grpoptions.add("Remove Member");
                    } else if (contactrole.equals(CSConstants.MEMBER) && !number.equals(GlobalVariables.phoneNumber)) {
                        grpoptions.add("Assign as Admin");
                        grpoptions.add("Remove Member");
                    } else if (contactrole.equals(CSConstants.ADMIN) && number.equals(GlobalVariables.phoneNumber)) {
                        grpoptions.add("Exit Group");
                    }
                    grpoptions.add("View Member Details");
                    break;
                case 1:
                    if (contactrole.equals(CSConstants.ADMIN)) {
                        //grpoptions.add("Remove as Admin");
                        //grpoptions.add("Remove Member");
                    } else if (contactrole.equals(CSConstants.MEMBER)) {
                        //grpoptions.add("Assign as Admin");
                        grpoptions.add("Remove Member");
                    }

                    grpoptions.add("View Member Details");

                    break;
                case 2:
                    grpoptions.add("View Member Details");
                    break;
            }

			/*for(String str:grpoptions) {
				LOG.info("Group permissions:"+str);
			}*/

            SimpleTextAdapter simpleTextAdapter = new SimpleTextAdapter(ManageGroupActivity.this, grpoptions);
            successfullyLogin.setAdapter(simpleTextAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            String finalaction = grpoptions.get(which);
                            LOG.info("finalaction:" + finalaction);
                            LOG.info("number:" + number);
                            if (finalaction.equals("Assign as Admin")) {
                                if (!utils.isinternetavailable(getApplicationContext())) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                LOG.info("In Assign as Admin");
                                List<String> numbers = new ArrayList<String>();
                                numbers.add(number);
                                IAmLiveCoreSendObj.addAdminsToGroup(grpid, numbers);
                                numbers.clear();
                            } else if (finalaction.equals("Remove as Admin")) {
                                if (!utils.isinternetavailable(getApplicationContext())) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                LOG.info("In Remove as Admin");
                                IAmLiveCoreSendObj.deleteAdminFromGroup(grpid, number);
                            } else if (finalaction.equals("Remove Member")) {
                                if (!utils.isinternetavailable(getApplicationContext())) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                LOG.info("In Remove Member");
                                IAmLiveCoreSendObj.deleteMemberFromGroup(grpid, number);
                            } else if (finalaction.equals("View Member Details")) {
                                LOG.info("View Member Details");
                                showMemberDetails(number);
                            } else if (finalaction.equals("Exit Group")) {
                                if (!utils.isinternetavailable(getApplicationContext())) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable_message), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                LOG.info("Exit Group");
                                IAmLiveCoreSendObj.exitFromGroup(grpid);
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

    public boolean showMemberDetails(final String number) {
        try {


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String userName = "";
                    String Status = "";
                    String Mobile = number;
                    AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(ManageGroupActivity.this);

                    if(number.equals(GlobalVariables.phoneNumber)) {
                        Cursor cur = CSDataProvider.getSelfProfileCursor();//ProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
                        if (cur.getCount() > 0) {
                            cur.moveToNext();
                            userName = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_USERNAME));
                            Status = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_DESCRIPTION));
                        }
                        cur.close();
                    } else {

                        Cursor cur = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
                        if (cur.getCount() > 0) {
                            cur.moveToNext();
                            userName = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_USERNAME));
                            Status = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_DESCRIPTION));
                        }
                        cur.close();
                    }
                    if (userName.equals("")) {
                        userName = "Not Available";
                    }
                    if (Status.equals("")) {
                        Status = "Not Available";
                    }
                    if (Mobile.equals("")) {
                        Mobile = "Not Available";
                    }
                    successfullyLogin.setMessage("username:" + userName + "\n" + "Status:" + Status + "\n" + "Mobile:" + Mobile + "\n");

                    successfullyLogin.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                }
                            });

                    successfullyLogin.show();
                }
            });
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

	/*
	public void goLive(String grpid) {

		try {
			ArrayList<String> numbers = new ArrayList<String>();
			ArrayList<String> groupids = new ArrayList<String>();
			ArrayList<String> channelids = new ArrayList<String>();

			groupids.add(grpid);

			IAmLiveCoreSendObj.pullGroupDetails(grpid);



			if (!GlobalVariables.ispeerconnectionclosed) {
				GlobalVariables.peerConnectionClient.close();
				GlobalVariables.ispeerconnectionclosed = true;
			}
			GlobalVariables.ispeerconnectionclosed = false;
			Intent intent1 = new Intent(MainActivity.context, PlayStreamActivity.class);
			intent1.putExtra("streamdescription", "AirYou stream");
			intent1.putExtra("isinitiatior", true);
			intent1.putStringArrayListExtra("contacts",numbers);
			intent1.putStringArrayListExtra("groups",groupids);
			intent1.putStringArrayListExtra("channels",channelids);
			startActivity(intent1);
		} catch (Exception ex) {

		}
	}
*/

    public boolean showalert(final int action) {
        try {
            android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(ManageGroupActivity.this);

            successfullyLogin.setCancelable(true);
            switch (action) {
                case 0:
                    successfullyLogin.setTitle("Delete Group?");
                    break;
                case 1:
                    successfullyLogin.setTitle("Exit From Group?");
                    break;
            }

            //0-->DeleteGroup
            //1-->ExitFromGroup
            successfullyLogin.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            switch (action) {
                                case 0:
                                    IAmLiveCoreSendObj.deleteGroup(grpid);
                                    break;
                                case 1:
                                    IAmLiveCoreSendObj.exitFromGroup(grpid);
                                    break;
                            }
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

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap photo = null;
            try {
				/*InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
						ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf((Long.parseLong(params[0])))));
*/

                photo = CSDataProvider.getImageBitmap(params[0]);
                if (photo == null) {
                    photo = BitmapFactory.decodeResource(ManageGroupActivity.this.getResources(), R.drawable.defaultgroup);
                }
				/*
				InputStream inputStream = new ByteArrayInputStream(CSDataProvider.getImageByFilter(CSDbFields.KEY_IMAGEID,params[0]));
				if (inputStream != null) {
					photo = BitmapFactory.decodeStream(inputStream);
				} else {
					photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.default_streamviewer);
				}
				if (inputStream != null)
					inputStream.close();
*/
            } catch (Exception e) {
                utils.logStacktrace(e);
            }
            return photo;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            try {

                if (isCancelled()) {
                    bitmap = null;
                }
                if (imageViewReference != null) {
                    ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        if (bitmap != null) {
                            profile_image.setVisibility(View.VISIBLE);
                            profile_image.setImageBitmap(bitmap);
                            imageView.setVisibility(View.GONE);
                        } else {
                            profile_image.setVisibility(View.GONE);
					/*	TextDrawable drawable2 = TextDrawable.builder()
				                .buildRound("A", Color.RED);
						Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
						imageView.setImageDrawable(placeholder);*/
                        }
                    }
                }

            } catch (Exception ex) {
                utils.logStacktrace(ex);
            }
        }
    }


    public void disableUI() {
        try {
            is_grp_active = 0;
            fab.setVisibility(View.GONE);
            addmember.setVisibility(View.GONE);
            exitordeletegrp.setText("DELETE GROUP");


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean shownomemberalert() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    android.app.AlertDialog.Builder successfullyLogin = new android.app.AlertDialog.Builder(ManageGroupActivity.this);
                    //successfullyLogin.setTitle("Confirmation");
                    //successfullyLogin.setCancelable(false);
                    successfullyLogin.setMessage("No longer a member of this group");

                    successfullyLogin.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //onBackPressed();
                                }
                            });
                    successfullyLogin.show();
                }
            });

            return true;
        } catch (Exception ex) {
            return false;
        }

    }


    private void applyAnimationToSubTitle() {
        try {



            subtitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            subtitle.setMarqueeRepeatLimit(1);
            subtitle.setSelected(true);



            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {

                        subtitle.setText(subtitle.getText().toString());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, 2950);



        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
