package com.cavox.adapaters;

import android.content.ContentUris;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import androidx.cursoradapter.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.app.deltacubes.R;
import com.cavox.konverz.MainActivity;
import com.cavox.konverz.ShowUserLogActivity;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;
import com.ca.Utils.CSDbFields;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class FirstCallRecentsAdapter extends CursorAdapter {

    private Context context;

    public FirstCallRecentsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
        try {
            CircularImageView image = (CircularImageView) convertView.findViewById(R.id.imageview);
            image.setVisibility(View.GONE);
            TextView title = (TextView) convertView.findViewById(R.id.text1);
            TextView secondary = (TextView) convertView.findViewById(R.id.text2);
            //TextView third = (TextView) convertView.findViewById(R.id.text3);
            ImageView dir = (ImageView) convertView.findViewById(R.id.imageview1);
            TextView text4 = (TextView) convertView.findViewById(R.id.text4);

            ImageView infoimageView = (ImageView) convertView.findViewById(R.id.infoimageView);
            final String name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_NAME));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_NUMBER));
            String directionn = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_DIR));
            String groupingdate = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_GROUPING_IDENTIFIER_DATE));

            //Cursor cs = CSDataProvider.getCallLogCursorByFilter(CSDbFields.KEY_CALLLOG_NUMBER,number);
            Cursor cs = CSDataProvider.getCallLogCursorByThreeFilters(CSDbFields.KEY_CALLLOG_NUMBER, number, CSDbFields.KEY_CALLLOG_DIR, directionn, CSDbFields.KEY_CALLLOG_GROUPING_IDENTIFIER_DATE, groupingdate);
            String count = String.valueOf(cs.getCount());
            cs.close();



/*
	String prevDate = "";
	Long dateStr = cursor.getLong(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_TIME));
	String currentDate = getFormattedDate(dateStr);
	String shortTimeStr = getFormattedTime1(dateStr);
	if (cursor.getPosition() > 0 && cursor.moveToPrevious()) {
		prevDate = getFormattedDate(cursor.getLong(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_TIME)));
		cursor.moveToNext();
	}
	if (prevDate == null || !prevDate.equals(currentDate)) {
		dateView.setVisibility(View.VISIBLE);
		if(DateUtils.isToday(dateStr)) {
			dateView.setText("Today");
		} else if(isYesterday(dateStr)) {
			dateView.setText("Yesterday");
		} else {
			dateView.setText(currentDate);
		}

	}
	else {
		dateView.setVisibility(View.GONE);
	}
*/


            final String number1 = number;

            number = number.replaceAll("[^0-9+]", "");
            String id = "";

            Cursor ccr = CSDataProvider.getContactCursorByNumber(number);
            if (ccr.getCount() > 0) {
                ccr.moveToNext();
                id = ccr.getString(ccr.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
            }
            ccr.close();
            final String id1 = id;

            String temp_str = "";
            if (name.equals("")) {
                temp_str = number;
            } else {
                temp_str = name;
            }

		/*
				if(directionn.contains("MISSED")) {
			Cursor cs1 = CSDataProvider.getCallLogCursorByThreeFilters(CSDbFields.KEY_CALLLOG_NUMBER,number,CSDbFields.KEY_CALLLOG_DIR,directionn,CSDbFields.KEY_CALLLOG_MISSEDCALL_NOTIFIED,0);
			int count1 = cs1.getCount();
			cs1.close();
			if(count1 <= 0) {
				title.setText(temp_str+"("+count+")");
			} else {
				title.setText(temp_str+"("+count+")"+"("+String.valueOf(count1)+" new)");
			}
		} else {
			title.setText(temp_str+"("+count+")");
		}
*/
            if (count.equals("0") || count.equals("1")) {
                title.setText(temp_str);
            } else {
                title.setText(temp_str);
            }


            String date = "";
            String time = "";
            String Duration = "";
            String cost = "";

            String formattedDate = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_TIME));
            long timme = Long.valueOf(formattedDate);

            date = new SimpleDateFormat("dd/MM/yyyy").format(timme);
            //time = new SimpleDateFormat("hh:mm:ss a").format(timme);
            time = new SimpleDateFormat("hh:mm a").format(timme);

            if (DateUtils.isToday(timme)) {
                date = "";
            } else if (isYesterday(timme)) {
                time="";
                date = "Yesterday";
            }else {
                time="";
            }


	/*
	String ds1 = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_DURATION));
	int du1 = Integer.parseInt(ds1);
	String minutes = "00";
	String seconds = "00";
	int mins = (du1) / 60;
	int sec = (du1) % 60;
	if (mins < 10) {
		minutes = "0" + String.valueOf(mins);
	} else {
		minutes = String.valueOf(mins);
	}
	if (sec < 10) {
		seconds = "0" + String.valueOf(sec);
	} else {
		seconds = String.valueOf(sec);
	}

	Duration = minutes + ":" + seconds;
*/

            String mysecondary = date + " " + time;// + " " + Duration;// + "|" + callcost;
            secondary.setText(mysecondary);
//third.setText(Duration);
            String direction = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_DIR));
            if (Integer.parseInt(count) > 1) {
                text4.setText(direction.toLowerCase() + "(" + count + ")");
            } else {
                text4.setText(direction.toLowerCase());
            }
            if (direction.contains("OUTGOING")) {
                dir.setImageDrawable(MainActivity.context.getResources().getDrawable(R.drawable.outgoingcall));
            } else if (direction.contains("INCOMING")) {
                dir.setImageDrawable(MainActivity.context.getResources().getDrawable(R.drawable.incomingorange));
            } else if (direction.contains("MISSED")) {
                dir.setImageDrawable(MainActivity.context.getResources().getDrawable(R.drawable.missedcall));
            } else {
                //dir.setImageDrawable(MainActivity.context.getResources().getDrawable(R.drawable.outgoingcall));
            }

            String nativecontactid = "";
            Cursor cur = CSDataProvider.getContactCursorByNumber(number);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                nativecontactid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID));
            }
            cur.close();

            String picid = "";
            Cursor cur1 = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
            if (cur1.getCount() > 0) {
                cur1.moveToNext();
                picid = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
            }
            cur1.close();


            //new ImageDownloaderTask(image).execute("app",picid,nativecontactid);


            final String mydirection = direction;
            infoimageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {

                        Intent intentt = new Intent(MainActivity.context, ShowUserLogActivity.class);
                        intentt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentt.putExtra("number", number1);
                        intentt.putExtra("name", name);
                        intentt.putExtra("id", id1);
                        intentt.putExtra("direction", mydirection);
                        MainActivity.context.startActivity(intentt);
                    } catch (Exception ex) {
//utils.logStacktrace(ex);
                    }
                }
            });


        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View convertView = inflater.inflate(R.layout.recents_row_layout, parent, false);

        return convertView;
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
                if (params[0].equals("app")) {
                    photo = CSDataProvider.getImageBitmap(params[1]);
                } else {
                    photo = utils.loadContactPhoto(Long.parseLong(params[1]));
                }
                if (params[0].equals("app") && photo == null) {
                    photo = utils.loadContactPhoto(Long.parseLong(params[2]));
                }

                if (photo == null) {
                    photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
                }
            } catch (Exception e) {
                photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
                //utils.logStacktrace(e);
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

    public static Bitmap loadContactPhoto(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
            InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(
                    MainActivity.context.getContentResolver(), uri);
            return BitmapFactory.decodeStream(stream);
        } catch (Exception ex) {
            return null;
        }


    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        try {
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
        } catch (Exception ex) {
            return null;
        }
    }

    private String getFormattedDate(long dateStr) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").format(dateStr);
        } catch (Exception e) {
            utils.logStacktrace(e);
        }
        return "";
    }

    private String getFormattedTime1(long dateStr) {

        try {
            return new SimpleDateFormat("hh:mm a").format(dateStr);
        } catch (Exception e) {
            utils.logStacktrace(e);
        }
        return "";
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
}