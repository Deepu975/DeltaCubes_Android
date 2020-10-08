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

import com.ca.Utils.CSDbFields;
import com.app.deltacubes.R;
import com.cavox.konverz.MainActivity;
import com.cavox.konverz.ManageUserActivity;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;


public class MessageinfoAdapter extends CursorAdapter {

	private Context context;

	public MessageinfoAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.context = context;
	}


	@Override
	public void bindView(View convertView, final Context context, Cursor cursor) {

		com.mikhaellopez.circularimageview.CircularImageView image = (com.mikhaellopez.circularimageview.CircularImageView) convertView.findViewById(R.id.imageView6);
		TextView title = (TextView) convertView.findViewById(R.id.text1);
		TextView secondary = (TextView) convertView.findViewById(R.id.text2);
		TextView third = (TextView) convertView.findViewById(R.id.text3);

		String destnumber = "";
		String destname = "";
		String deliveredtime = "";
		String readtime = "";
		String id = "";



		String chatid = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_GROUP_CHAT_CHATID));
long deliverdtime = cursor.getLong(cursor.getColumnIndex(CSDbFields.KEY_GROUP_CHAT_DELIVERED_TIME));
		long readdtime = cursor.getLong(cursor.getColumnIndex(CSDbFields.KEY_GROUP_CHAT_READ_TIME));
		destnumber = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_CHAT_DESTINATION_LOGINID));



		if(!destnumber.equals("")) {
			Cursor cur1 = CSDataProvider.getContactCursorByNumber(destnumber);
			if(cur1.getCount()>0) {
				cur1.moveToNext();
				destname = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
				id = cur1.getString(cur1.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
			}

			if(destname.equals("")) {
				destname = destnumber;
			}

			cur1.close();
		}


		if(deliverdtime == 0) {
			deliveredtime = "Not delivered";
		} else {
			deliveredtime = "Delivered:"+getFormattedTime(deliverdtime);
		}

		if(readdtime == 0) {
			readtime = "Not read";
		} else {
			readtime = "Read:"+getFormattedTime(readdtime);
		}

		title.setText(destname);
		secondary.setText(deliveredtime);
		third.setText(readtime);


		//String description = "Hey there! I am using Konverz";
		String description = "";
		String picid = "";
		Cursor cur2 = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER,destnumber);
		if(cur2.getCount()>0) {
			cur2.moveToNext();
			picid = cur2.getString(cur2.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
			description = cur2.getString(cur2.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_DESCRIPTION));

			new ImageDownloaderTask(image).execute("app",picid,id);
		}
		cur2.close();
		if(description.equals("")) {
			//description = "Hey there! I am using Konverz";
			description = "";
		}


		final String mynumber = destnumber;
		final String mydescription = description;
		final String mypicid = picid;
		final String myname = destname;
		final String myid = id;

		image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(context, ManageUserActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("name", myname);
				intent.putExtra("number", mynumber);
				intent.putExtra("description", mydescription);
				intent.putExtra("picid", mypicid);
				intent.putExtra("nativecontactid", myid);
				context.startActivity(intent);

			}
		});




	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View convertView = inflater.inflate(R.layout.contact_row_layout4, parent, false);

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
				if(params[0].equals("app")) {
					photo = CSDataProvider.getImageBitmap(params[1]);
					if(photo == null) {
						photo = utils.loadContactPhoto(Long.parseLong(params[2]));
					}
				} else if(params[0].equals("native")) {
					photo = utils.loadContactPhoto(Long.parseLong(params[1]));
				}


                if(photo == null) {
					photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
				} else {
					scaleit = true;
					//ImageView imageView = imageViewReference.get();
					//imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				}
			} catch (Exception e) {
				photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
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

	private String getFormattedTime(long dateStr) {
		try {
			return new SimpleDateFormat("dd-MM-yy hh:mm:ss a").format(dateStr);
		} catch (Exception e) {
			utils.logStacktrace(e);
		}
		return "";
	}
}