package com.cavox.adapaters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.app.deltacubes.R;
import com.cavox.konverz.MainActivity;
import com.cavox.konverz.ShowCORGMultiSelectActivity;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;

import org.json.JSONObject;

import java.lang.ref.WeakReference;


public class CORGMultiSelectAdapter extends CursorAdapter {
	private Context context;
	//public  ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();

	public CORGMultiSelectAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.context = context;
		//for (int i = 0; i < this.getCount(); i++) {
			//itemChecked.add(i, false); // initializes all items value with false
		//}
	}



	@Override
	public void bindView(View convertView, final Context context, Cursor cursor) {


		try {


			CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			com.mikhaellopez.circularimageview.CircularImageView image = (com.mikhaellopez.circularimageview.CircularImageView) convertView.findViewById(R.id.imageview);
			TextView title = (TextView) convertView.findViewById(R.id.text1);
			TextView secondary = (TextView) convertView.findViewById(R.id.text2);

			String id = "";
			String name = "";


			id = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACTORGROUP_ID));
			name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_NAME));
			String iscontactorgroup = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_IS_CONTACTORGROUP));
			if (iscontactorgroup.equals(CSConstants.GROUP)) {

				String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
				String picid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_PICID));

				title.setText(name);
				secondary.setText(number);
				new ImageDownloaderTask(image).execute("group", picid, id);

				JSONObject json = new JSONObject();
				json.put("id", id);
				json.put("type", CSConstants.GROUP);
				String message = json.toString();

				if(ShowCORGMultiSelectActivity.numbers.contains(message)) {
					checkBox.setChecked(true);
				} else {
					checkBox.setChecked(false);
				}

			} else {
				final String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
				title.setText(name);
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
				/*if (description.equals("")) {
					description = "Hey there! IamLive";
				}*/
				secondary.setText(description);


				JSONObject json = new JSONObject();
				json.put("id", number);
				json.put("type", CSConstants.CONTACT);
				String message = json.toString();
				if(ShowCORGMultiSelectActivity.numbers.contains(message)) {
					checkBox.setChecked(true);
				} else {
					checkBox.setChecked(false);
				}
			}





		} catch (Exception ex) {
			utils.logStacktrace(ex);
		}
	}





	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View convertView = inflater.inflate(R.layout.app_contact_row_layout, parent, false);

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

				else if(params[0].equals("group")&&photo == null) {
					photo = CSDataProvider.getImageBitmap(params[1]);
				}

				if(photo == null) {
					if(!params[0].equals("group")) {
						photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
					} else {
						photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultgroup);
					}
				}
			} catch (Exception e) {
				try {
					if(photo == null) {
						if(!params[0].equals("group")) {
							photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultcontact);
						} else {
							photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.defaultgroup);
						}
					}
				} catch (Exception ex){}
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