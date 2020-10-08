package com.cavox.adapaters;

import android.content.ContentUris;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ca.Utils.CSDbFields;
import com.ca.wrapper.CSDataProvider;
import com.app.deltacubes.R;
import com.cavox.konverz.MainActivity;
import com.cavox.utils.utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;


public class FirstCallChatsAdapter1 extends CursorAdapter {

	private Context context;

	public FirstCallChatsAdapter1(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.context = context;
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {

		try {


			CircularImageView image = (CircularImageView) convertView.findViewById(R.id.imageView6);
			TextView title = (TextView) convertView.findViewById(R.id.text1);
			TextView secondary = (TextView) convertView.findViewById(R.id.text2);
			TextView time = (TextView) convertView.findViewById(R.id.time);
			TextView unreadcount = (TextView) convertView.findViewById(R.id.unreadcount);

			//String id = "";//cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
            int isgroupmessage = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_IS_GROUP_MESSAGE));
            String grpid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DESTINATION_GROUPID));

			String name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DESTINATION_NAME));


			String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DESTINATION_LOGINID));

			String lastmessage = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
			int chattype = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MSG_TYPE));
			int issender = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_IS_SENDER));

			Long dateStr = cursor.getLong(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_TIME));
			String currentDate = utils.getDateForChat(dateStr,context);
			time.setText(currentDate);

			Cursor ccr = null;
			if(isgroupmessage == 0) {
				 ccr = CSDataProvider.getChatCursorFilteredByNumberAndUnreadMessages(number);
			} else {
				ccr = CSDataProvider.getChatCursorFilteredByNumberAndUnreadMessages(grpid);
			}

			if (ccr.getCount() > 0) {
				unreadcount.setVisibility(View.VISIBLE);
				unreadcount.setText(String.valueOf(ccr.getCount()));
				time.setTextColor(context.getResources().getColor(R.color.chat_unread_color));
				//secondary.setTypeface(null, Typeface.BOLD);

			} else {
				unreadcount.setVisibility(View.INVISIBLE);
				unreadcount.setText(String.valueOf(ccr.getCount()));
				time.setTextColor(context.getResources().getColor(R.color.black));
				//secondary.setTypeface(null, Typeface.NORMAL);
			}
			ccr.close();
//LOG.info("cor name:"+name);
			if (name.equals("")) {
                if(isgroupmessage == 0) {
                    name = number;
                } else {

                    Cursor cor = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID,grpid);
					//LOG.info("cor count:"+cor.getCount());
                    if(cor.getCount()>0) {
                        cor.moveToNext();
                        name = cor.getString(cor.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_NAME));
                    }
                    cor.close();

                              if(name.equals("")) {
                                  name = "Unknown Group";
                              }



                }
			}
			title.setText(name);
			secondary.setText("");
			if(chattype == 0) {
				secondary.setText(lastmessage);
			} else if(chattype == 1) {
				secondary.setText(lastmessage);
			} else if(chattype == 2) {
				secondary.setText(lastmessage);
			} else if(chattype == 3) {
				if(issender == 0) {
					secondary.setText("Location");
				} else {
					secondary.setText("Location");
				}
			} else if(chattype == 4) {
				if(issender == 0) {
					secondary.setText("Photo");
				} else {
					secondary.setText("Photo");
				}
			} else if(chattype == 5) {
					if (issender == 0) {
						secondary.setText("Video");
					} else {
					secondary.setText("Video");
					}

			} else if(chattype == 6) {
					if (issender == 0) {
						secondary.setText("Contact");
					} else {
					secondary.setText("Contact");
				}
			} else if(chattype == 7) {
					if (issender == 0) {
						secondary.setText("Document");
					} else {
					secondary.setText("Document");
				}
			} else if(chattype == 8) {
					if (issender == 0) {
						secondary.setText("Audio");
					} else {
					secondary.setText("Audio");
				}
			}












            if(isgroupmessage == 0) {
                String nativecontactid = "";
                Cursor cur = CSDataProvider.getContactCursorByNumber(number);
                if(cur.getCount()>0) {
                    cur.moveToNext();
                    nativecontactid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID));
                }
                cur.close();

                String picid = "";
                Cursor cur1 = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
                //LOG.info("Yes count:"+cur1.getCount());
                if (cur1.getCount() > 0) {
                    cur1.moveToNext();
                    picid = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
                }
                cur1.close();

                new ImageDownloaderTask(image).execute("app", picid, nativecontactid);
            } else {
                String picid = "";
                Cursor cur1 = CSDataProvider.getGroupsCursorByFilter(CSDbFields.KEY_GROUP_ID, grpid);
                //LOG.info("Yes count:"+cur1.getCount());
                if (cur1.getCount() > 0) {
                    cur1.moveToNext();
                    picid = cur1.getString(cur1.getColumnIndexOrThrow(CSDbFields.KEY_GROUP_PROFILE_PIC));
                }
                cur1.close();

                new ImageDownloaderTask(image).execute("group", picid);
            }
		} catch (Exception ex) {
			utils.logStacktrace(ex);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View convertView = inflater.inflate(R.layout.chats_row_layout, parent, false);

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
			try
			{

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


			}catch (Exception e) {
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
	public static Bitmap loadContactPhoto(long  id) {
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

	private String getFormattedDate(long dateStr) {

		try {


			return new SimpleDateFormat("dd-MM-yyyy").format(dateStr);
		} catch (Exception e) {
			utils.logStacktrace(e);
		}
		return "";
	}
	private String getFormattedTime(long dateStr) {

		try {
			return new SimpleDateFormat("hh:mm a").format(dateStr);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			utils.logStacktrace(e);
		}

		return "";
	}
}