package com.cavox.adapaters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;

import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavox.fragments.FirstCallContacts;
import com.app.deltacubes.R;
import com.ca.Utils.CSDbFields;
import com.cavox.konverz.ManageUserActivity;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;

import java.lang.ref.WeakReference;


public class FirstCallContactsAdapter extends CursorAdapter {

    private Context context;

    public FirstCallContactsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }


    @Override
    public void bindView(View convertView, final Context context, Cursor cursor) {

        com.mikhaellopez.circularimageview.CircularImageView image = (com.mikhaellopez.circularimageview.CircularImageView) convertView.findViewById(R.id.imageView6);
        TextView title = (TextView) convertView.findViewById(R.id.text1);
        TextView secondary = (TextView) convertView.findViewById(R.id.text2);

        String id = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));


        if (FirstCallContacts.contactstypetoload == 0) {
            String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER));
            title.setText(name);
            secondary.setText(number);
            new ImageDownloaderTask(image).execute("native", id);
        } else {

            final String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER));
            title.setText(name);
            //String description = "Hey there! I am using Konverz";
            String description = "";
            String picid = "";
            Cursor cur = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                picid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
                description = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_DESCRIPTION));
                //if (picid != null && !picid.equals("")) {
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

                    Intent intent = new Intent(context, ManageUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("name", myname);
                    intent.putExtra("number", number);
                    intent.putExtra("description", mydescription);
                    intent.putExtra("picid", mypicid);
                    intent.putExtra("nativecontactid", myid);
                    context.startActivity(intent);

                }
            });


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
                }


                if (photo == null) {
                    photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultcontact);
                } else {
                    scaleit = true;
                    //ImageView imageView = imageViewReference.get();
                    //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            } catch (Exception e) {
                photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultcontact);
                utils.logStacktrace(e);
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


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}