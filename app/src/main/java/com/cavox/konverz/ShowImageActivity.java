package com.cavox.konverz;

import android.database.Cursor;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.app.deltacubes.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.ca.Utils.CSDbFields;
import com.ca.wrapper.CSDataProvider;
import com.cavox.views.ZoomView;

import java.io.File;


public class ShowImageActivity extends AppCompatActivity {
    private ZoomView mUserImage;
    private Toolbar toolbar;
    private String mContactNumber = "";
    /**
     * ImageLoader variables
     */
    //DisplayImageOptions mOptions;
    //ImageLoader mImageLoader;
    private boolean isGroupprofile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        toolbar = findViewById(R.id.toolbar);
        mUserImage = findViewById(R.id.show_image);
        setSupportActionBar(toolbar);

        /*
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        }

        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

                */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String userName = getIntent().getStringExtra("contactName");
        mContactNumber = getIntent().getStringExtra("myBitMap");
        isGroupprofile = getIntent().getBooleanExtra("isGroup", false);
        Log.i("ShowImageActivity", "CotactName " + userName);
        getSupportActionBar().setTitle(userName);
        mUserImage.setMaxZoom(4f);


        String picid = "";
        if (isGroupprofile) {
            Cursor cur1 = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, mContactNumber);
            if (cur1.getCount() > 0) {
                cur1.moveToNext();
                picid = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_PROFILE_PIC));
            }
            cur1.close();

            String filepath = CSDataProvider.getImageFilePath(picid);

            Glide.with(ShowImageActivity.this)
                    .load(Uri.fromFile(new File(filepath)))
                    .apply(new RequestOptions().error(R.drawable.defaultgroup))
                    //.apply(RequestOptions.circleCropTransform())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(filepath).length()+new File(filepath).lastModified()))))
                    .into(mUserImage);
        } else {
            Cursor cur1 = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, mContactNumber);
            //LOG.info("Yes count:"+cur1.getCount());
            if (cur1.getCount() > 0) {
                cur1.moveToNext();
                picid = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
            }
            cur1.close();


            String filepath = CSDataProvider.getImageFilePath(picid);

            Glide.with(ShowImageActivity.this)
                    .load(Uri.fromFile(new File(filepath)))
                    .apply(new RequestOptions().error(R.drawable.defaultcontact))
                    //.apply(RequestOptions.circleCropTransform())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(filepath).length()+new File(filepath).lastModified()))))
                    .into(mUserImage);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
