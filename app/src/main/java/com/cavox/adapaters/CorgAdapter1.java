package com.cavox.adapaters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.cursoradapter.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.cavox.fragments.FirstCallContacts;
import com.app.deltacubes.R;
import com.cavox.konverz.MainActivity;
import com.cavox.konverz.ManageGroupActivity;
import com.cavox.konverz.ManageUserActivity;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;

import java.lang.ref.WeakReference;


public class CorgAdapter1 extends CursorAdapter {

    private Context context;

    public CorgAdapter1(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }


    @Override
    public void bindView(View convertView, final Context context, Cursor cursor) {


        TextView title = (TextView) convertView.findViewById(R.id.text1);
        TextView secondary = (TextView) convertView.findViewById(R.id.text2);

        String id = "";
        String name = "";


        if (FirstCallContacts.contactstypetoload == 0) {
            id = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
            name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER));
            title.setText(name);
            secondary.setText(number);
            // image.setEnabled(false);
            com.mikhaellopez.circularimageview.CircularImageView image = (com.mikhaellopez.circularimageview.CircularImageView) convertView.findViewById(R.id.imageView6);

           Cursor cursor1=CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER,number);
           if (cursor1.moveToNext()){
               id=cursor1.getString(cursor1.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
               new ImageDownloaderTask(image).execute("app", id);
               image.setEnabled(false);
           }else {
               image.setEnabled(true);
               new ImageDownloaderTask(image).execute("native", id);
           }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "DeltaCubes");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Download DeltaCubes from https://play.google.com/store/apps/details?id=com.app.deltacubes");
                        context.startActivity(sharingIntent);
                }
            });
        } else {
            // image.setEnabled(true);
            id = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACTORGROUP_ID));
            name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_NAME));
            String iscontactorgroup = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_IS_CONTACTORGROUP));

            if (iscontactorgroup.equals(CSConstants.GROUP)) {

                String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
                String picid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_PICID));

                title.setText(name);
                secondary.setText(number);
                com.mikhaellopez.circularimageview.CircularImageView image = (com.mikhaellopez.circularimageview.CircularImageView) convertView.findViewById(R.id.imageView6);
                new ImageDownloaderTask(image).execute("group", picid, id);
                final String mygrpid = id;
                image.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, ManageGroupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("grpid", mygrpid);
                        context.startActivity(intent);

                    }
                });

            } else {
                final String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
                title.setText(name);
                //String description = "Hey there! I am using Konverz";
                //Log.i("CorgAdapter"," conatt name "+name+" number "+number);
                String description = "";
                String picid = "";
                com.mikhaellopez.circularimageview.CircularImageView image = (com.mikhaellopez.circularimageview.CircularImageView) convertView.findViewById(R.id.imageView6);
                Cursor cur = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
                if (cur.getCount() > 0) {
                    cur.moveToNext();
                    picid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
                    description = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_DESCRIPTION));
                    //if (picid != null && !picid.equals("")) {
                    //LOG.info("TEST PIC number:"+number);
                    //LOG.info("TEST PIC name:"+name);
                    //LOG.info("TEST PIC ID:"+picid);
                    new ImageDownloaderTask(image).execute("app", picid, id);
                    //}
                }

                cur.close();
                if (description.equals("")) {
                    //description = "Hey there! I am using Konverz";
                    description = "";
                }
                secondary.setText(description);

                final String mydescription = description;
                final String mypicid = picid;
                final String myname = name;
                final String myid = id;

                image.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        {
                            {
                                Intent intent = new Intent(context, ManageUserActivity.class);
                                Log.i("C", "Conatct number " + number);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("name", myname);
                                intent.putExtra("number", number);
                                intent.putExtra("description", mydescription);
                                intent.putExtra("picid", mypicid);
                                intent.putExtra("nativecontactid", myid);
                                context.startActivity(intent);
                            }
                        }

                    }
                });


            }
        }


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View convertView = inflater.inflate(R.layout.contact_row_layout3, parent, false);

        return convertView;
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        boolean scaleit = false;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap photo = null;
            try {
                if (params[0].equals("app")) {
                    photo = CSDataProvider.getImageBitmap(params[1]);
                    if (photo == null) {
                        photo = utils.loadContactPhoto(Long.parseLong(params[2]));
                    }
                } else if (params[0].equals("native")) {
                    photo = utils.loadContactPhoto(Long.parseLong(params[1]));
                } else if (params[0].equals("group") && photo == null) {
                    photo = CSDataProvider.getImageBitmap(params[1]);
                }

                if (photo == null) {
                    if (!params[0].equals("group")) {
                        photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
                    } else {
                        photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultgroup);
                    }
                }
            } catch (Exception e) {
                try {
                    if (photo == null) {
                        if (!params[0].equals("group")) {
                            photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
                        } else {
                            photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultgroup);
                        }
                    }
                } catch (Exception ex) {
                }
            }
            return photo;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
						/*if(scaleit) {
							//LOG.info("Yes scaleit is true");
							imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
						} else {
							imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
							imageView.setPadding(0,10,0,35);
						}*/
                        //imageView.setImageBitmap(getRoundedCornerBitmap(bitmap, 10));
                    } else {
						/*TextDrawable drawable2 = TextDrawable.builder()
				                .buildRound("A", Color.RED);*/
						/*Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
						imageView.setImageDrawable(placeholder);*/
                    }
                }
            }
        }
    }



}