package com.cavox.adapaters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.app.deltacubes.R;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.ref.WeakReference;

import static com.cavox.utils.GlobalVariables.LOG;


public class ManageGroupAppContactsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context context;
	Cursor cursor;

	public ManageGroupAppContactsAdapter(Context context, Cursor c, int flags) {

		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.cursor = c;

	}

	public int getCount() {
		return cursor.getCount();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	CheckBox checkBox;
	CircularImageView image;
	TextView title;
	TextView secondary;

	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			final ViewHolder holder;

			//if (convertView == null) {
			//LOG.info("convertView is null");
			convertView = mInflater.inflate(R.layout.group_contacts_row, null);
			//LOG.info("Height of convertView:"+convertView.getHeight());
			//LOG.info("Height of convertView:"+convertView.getMeasuredHeight());
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.text1);
			holder.secondary = (TextView) convertView.findViewById(R.id.text2);
			holder.image = (CircularImageView) convertView.findViewById(R.id.imageview);
			holder.isadmin = (TextView) convertView.findViewById(R.id.isadmin);

			convertView.setTag(holder);
			//} else {
			////LOG.info("convertView is not null");
			//holder = (ViewHolder) convertView.getTag();
			//}

			//LOG.info("cursor count:" + cursor.getCount());
			holder.isadmin.setVisibility(View.GONE);
			cursor.moveToPosition(position);
			String id = "";
			String picid = "";
			String name = "";
			String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_GROUPCONTACTS_CONTACT));
			String role = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_GROUPCONTACTS_ROLE));

			holder.secondary.setText(number);
			if (role.equals(CSConstants.ADMIN)) {
				holder.isadmin.setVisibility(View.VISIBLE);
			} else {
				holder.isadmin.setVisibility(View.GONE);
			}

			if (number.equals(GlobalVariables.phoneNumber)) {
				name = "You";

				Cursor cur1 = CSDataProvider.getSelfProfileCursor();
				if (cur1.getCount() > 0) {
					cur1.moveToNext();
					picid = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_SELF_PROFILE_PROFILEPICID));
				}
				cur1.close();

			} else {
				Cursor crr1 = CSDataProvider.getContactsCursorByFilter(CSDbFields.KEY_CONTACT_NUMBER, number);
				if (crr1.getCount() > 0) {
					crr1.moveToNext();
					name = crr1.getString(crr1.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
					id =  crr1.getString(crr1.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID));
				}
				crr1.close();
				if (name.equals("")) {
					name = number;
					holder.secondary.setVisibility(View.INVISIBLE);
				}


				Cursor cur = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
				if (cur.getCount() > 0) {
					cur.moveToNext();
					picid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
				}
				cur.close();

			}


			Log.i("ManageGroupAppContacts","number "+number+" role "+role+" name "+name);

			holder.title.setText(name);


			new ImageDownloaderTask(holder.image).execute("app",picid,id);


		} catch (Exception ex) {
			utils.logStacktrace(ex);
		}

		return convertView;
	}


	static class ViewHolder {
		CircularImageView image;
		TextView title;
		TextView secondary;
		TextView isadmin;
	}

	/*

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {

		final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
		CircularImageView image = (CircularImageView) convertView.findViewById(R.id.imageview);
		TextView title = (TextView) convertView.findViewById(R.id.text1);
		TextView secondary = (TextView) convertView.findViewById(R.id.text2);
		title.setText(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME)));
		String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER));
		secondary.setText(number);


        Cursor cur = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER,number);
		if(cur.getCount()>0) {
			cur.moveToNext();
			String picid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));

			if (picid != null && !picid.equals("")) {
				new ImageDownloaderTask(image).execute(picid);
			}
		}
			cur.close();





		final int pos = cursor.getPosition();

		checkBox.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox);

				if (cb.isChecked()) {
					itemChecked.set(pos, true);
					// do some operations here
				} else if (!cb.isChecked()) {
					itemChecked.set(pos, false);
					// do some operations here
				}
			}
		});
		checkBox.setChecked(itemChecked.get(pos));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View convertView = inflater.inflate(R.layout.app_contact_row_layout, parent, false);

		return convertView;
	}
*/
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
				e.printStackTrace();
				if(context == null) {
					LOG.info("context is null");
				}
				photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaultcontact);
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
