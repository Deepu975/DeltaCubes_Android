package com.cavox.adapaters;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.cursoradapter.widget.CursorAdapter;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.dao.CSChatContact;
import com.ca.dao.CSChatLocation;
import com.app.deltacubes.R;
import com.cavox.konverz.MainActivity;
import com.cavox.utils.utils;
import com.ca.wrapper.CSChat;
import com.ca.wrapper.CSDataProvider;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.cavox.utils.GlobalVariables.LOG;

public class ConversationAdapterOld extends CursorAdapter {
	CSChat CSChatObj = new CSChat();
	static String sentimagedirectory = "";
	static String recvimagedirectory = "";
	static String destination = "";
	/*private Context context;
	private Cursor cursor;*/
Context mycontext;
	public ConversationAdapterOld(Context context, Cursor c, int flags, String mydestination) {
		super(context, c, flags);
		/*this.context = context;
		cursor = c;*/
		mycontext = context;
		sentimagedirectory = utils.getSentImagesDirectory();
		recvimagedirectory = utils.getReceivedImagesDirectory();
		destination = mydestination;
	}



	@Override
	public void bindView(final View convertView, Context context, final Cursor cursor) {
		try {
			final ViewHolder holder;
			String prevDate = null;
			if (convertView.getTag() == null) {
				holder = new ViewHolder();
				holder.sendLayout = (RelativeLayout) convertView.findViewById(R.id.send_layout);
            holder.receiveLayout = (RelativeLayout) convertView.findViewById(R.id.receive_layout);
            holder.sendMessage = (TextView) convertView.findViewById(R.id.chat_send_text);
            holder.receiveMessage = (TextView) convertView.findViewById(R.id.chat_receive_text);
            holder.sendTime = (TextView) convertView.findViewById(R.id.chat_send_time);
            holder.receiveTime = (TextView) convertView.findViewById(R.id.chat_receive_time);
            holder.dateView = (TextView) convertView.findViewById(R.id.dateView);
            holder.sentIcon = (ImageView) convertView.findViewById(R.id.sent_icon);
            holder.deliveredIcon = (ImageView) convertView.findViewById(R.id.delivered_icon);
            holder.sentIcon1 = (ImageView) convertView.findViewById(R.id.sent_icon1);
            holder.deliveredIcon1 = (ImageView) convertView.findViewById(R.id.delivered_icon1);
            holder.failedtext = (ImageView) convertView.findViewById(R.id.failedimage);
            holder.sent_imagelayout = (RelativeLayout) convertView.findViewById(R.id.sent_imagelayout);
            holder.sent_imageview = (ImageView) convertView.findViewById(R.id.sent_imageview);
            holder.receive_image_layout = (RelativeLayout) convertView.findViewById(R.id.receive_image_layout);
            holder.recv_imageview = (ImageView) convertView.findViewById(R.id.recv_imageview);

            holder.recvimagetext = (TextView) convertView.findViewById(R.id.recvimagetext);
            holder.sentimagetext = (TextView) convertView.findViewById(R.id.sentimagetext);
			holder.statusandtime_layout = (RelativeLayout) convertView.findViewById(R.id.statusandtime);

				holder.progressBar = (com.github.lzyzsd.circleprogress.DonutProgress) convertView.findViewById(R.id.progressBar);
            holder.recv_downloadimage = (ImageView) convertView.findViewById(R.id.downloadimage);


			} else {
				holder = (ViewHolder) convertView.getTag();
			}


			Long dateStr = cursor.getLong(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_TIME));
			String currentDate = getFormattedDate(dateStr);
			String shortTimeStr = getFormattedTime1(dateStr);
			if (cursor.getPosition() > 0 && cursor.moveToPrevious()) {
				prevDate = getFormattedDate(cursor.getLong(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_TIME)));
				cursor.moveToNext();
			}
			if (prevDate == null || !prevDate.equals(currentDate)) {
				holder.dateView.setVisibility(View.VISIBLE);
				if(DateUtils.isToday(dateStr)) {
					holder.dateView.setText("Today");
				} else if(isYesterday(dateStr)) {
					holder.dateView.setText("Yesterday");
				} else {
					holder.dateView.setText(currentDate);
				}
			}
			else {
				holder.dateView.setVisibility(View.GONE);
			}






			if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_IS_SENDER)) == 1) {
				holder.sendLayout.setVisibility(View.VISIBLE);
				holder.statusandtime_layout.setVisibility(View.VISIBLE);

				holder.receiveLayout.setVisibility(View.GONE);
                holder.failedtext.setVisibility(View.GONE);
				holder.sentimagetext.setVisibility(View.GONE);

				if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTPLAIN || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTHTML || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_UNKNOWN_CHAT_TYPE) {
                    holder.sent_imagelayout.setVisibility(View.GONE);
					holder.sendMessage.setText(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE)));

				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_LOCATION) {
					holder.sendMessage.setVisibility(View.GONE);

					String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
					String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
					int thumbstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


					CSChatLocation location = CSChatObj.getLocationFromChatID(chatid);


/*
					String message = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    JSONObject jsonObj = new JSONObject(message);

                    String address = jsonObj.getString("address");
                    String lat = jsonObj.getString("lat");
                    String lon = jsonObj.getString("lon");
                    Double lattitude = Double.valueOf(lat);
                    Double longnitude = Double.valueOf(lon);
*/
					String address = location.getAddress();
					//Double lattitude = location.getLat();
					//Double longnitude = location.getLng();


                    //LOG.info("lattitude"+lattitude);
                    //LOG.info("longnitude"+longnitude);
                    //LOG.info("address"+address);

                    holder.sentimagetext.setVisibility(View.VISIBLE);
                    holder.sentimagetext.setText(address);


					if (thumbkey != null && !thumbkey.equals("")&&thumbstatus == 1) {
						new ImageDownloaderTask(holder.sent_imageview).execute("app",thumbkey,"");
					}


                }
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_IMAGE) {
					holder.sendMessage.setVisibility(View.GONE);

					//String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
					//String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
					//String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
					//int thumbstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));
					String uploadfilepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_UPLOAD_FILE_PATH));
					Bitmap myBitmap = BitmapFactory.decodeFile(uploadfilepath);
					holder.sent_imageview.setImageBitmap(myBitmap);

					//if (thumbkey != null && !thumbkey.equals("")&&thumbstatus == 1) {
						//new ImageDownloaderTask(holder.sent_imageview).execute("app",thumbkey,"");
					//}

					holder.sentimagetext.setVisibility(View.VISIBLE);
					String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					holder.sentimagetext.setText(contenttype);


				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_VIDEO) {
					holder.sendMessage.setVisibility(View.GONE);
					String filepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_UPLOAD_FILE_PATH));
					Bitmap myBitmap = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Video.Thumbnails.MICRO_KIND);

					holder.sent_imageview.setImageBitmap(myBitmap);

					holder.sentimagetext.setVisibility(View.VISIBLE);
					String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					holder.sentimagetext.setText(contenttype);


				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_CONTACT) {

					holder.sent_imagelayout.setVisibility(View.GONE);

					//String message = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));


					String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
             CSChatContact contact = CSChatObj.getContactFromChatID(chatid);

					//JSONObject jsonObj = new JSONObject(message);
					//JSONArray array = jsonObj.getJSONArray("numbers");
					//JSONArray array1 = jsonObj.getJSONArray("labels");

					List array = contact.getNumbers();
					List array1 = contact.getLabels();


					String finalmessage = "";
					for (int i = 0; i < array.size(); i++) {
						//LOG.info("MEssage number:" + array.get(i).toString());
						//LOG.info("labels:" + array.getJSONObject(i).getString("labels"));
						if(finalmessage.equals("")) {
							finalmessage =  contact.getName() + "\n" + "<u>"+array.get(i).toString() + "</u>"+ "\n" + array1.get(i).toString();

						} else {
							finalmessage = "\n\n"+finalmessage + contact.getName() + "\n" + "<u>"+array.get(i).toString()+ "</u>" + "\n" + array1.get(i).toString();
						}
					}

					holder.sendMessage.setText(Html.fromHtml(finalmessage));


				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_DOCUMENT) {
					holder.sendMessage.setVisibility(View.GONE);
					holder.sent_imageview.setImageResource(R.drawable.defaultocicon);

					holder.sentimagetext.setVisibility(View.VISIBLE);
					String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					holder.sentimagetext.setText(contenttype);

				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_AUDIO) {
					holder.sendMessage.setVisibility(View.GONE);

					holder.sent_imageview.setImageResource(R.drawable.defaultauioicon);

					holder.sentimagetext.setVisibility(View.VISIBLE);
					String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					holder.sentimagetext.setText(contenttype);
				}



				holder.sendTime.setText(shortTimeStr);
				holder.sentIcon.setVisibility(View.INVISIBLE);
				holder.deliveredIcon.setVisibility(View.INVISIBLE);
				holder.sentIcon1.setVisibility(View.INVISIBLE);
				holder.deliveredIcon1.setVisibility(View.INVISIBLE);

						if (cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS)) == CSConstants.MESSAGE_NOT_SENT) {
					holder.sentIcon.setVisibility(View.INVISIBLE);
					holder.deliveredIcon.setVisibility(View.INVISIBLE);
					holder.failedtext.setVisibility(View.VISIBLE);
				} else if (cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS)) == CSConstants.MESSAGE_SENT) {
					holder.sentIcon.setVisibility(View.VISIBLE);
					holder.deliveredIcon.setVisibility(View.INVISIBLE);
				} else if (cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS)) == CSConstants.MESSAGE_DELIVERED) {
					holder.sentIcon.setVisibility(View.VISIBLE);
					holder.deliveredIcon.setVisibility(View.VISIBLE);
				} else if (cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS)) == CSConstants.MESSAGE_READ) {
					//LOG.info("Chat status setting as read:"+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE)));
					holder.sentIcon.setVisibility(View.INVISIBLE);
					holder.deliveredIcon.setVisibility(View.INVISIBLE);
					holder.sentIcon1.setVisibility(View.VISIBLE);
					holder.deliveredIcon1.setVisibility(View.VISIBLE);

				}

			} else {

                holder.recv_downloadimage.setVisibility(View.GONE);
				holder.progressBar.setVisibility(View.GONE);
				holder.sendLayout.setVisibility(View.GONE);
				holder.statusandtime_layout.setVisibility(View.GONE);

				holder.receiveLayout.setVisibility(View.VISIBLE);
				holder.receiveMessage.setVisibility(View.VISIBLE);


				holder.receive_image_layout.setVisibility(View.GONE);
				holder.receiveTime.setText(shortTimeStr);


				if(cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS))== CSConstants.MESSAGE_RECEIVED   || cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS))== CSConstants.MESSAGE_DELIVERED_ACK) {
					CSChatObj.sendReadReceipt(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID)));
					//CSDataProvider.updateChatMessageReadStatus(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID)), CSConstants.MESSAGE_READ);
					this.changeCursor(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID,destination));
					notifyDataSetChanged();
					return;
				}

				if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTPLAIN || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTHTML || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_UNKNOWN_CHAT_TYPE) {

					holder.receiveMessage.setText(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE)));

				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_LOCATION) {
					holder.receiveMessage.setVisibility(View.GONE);
					holder.receive_image_layout.setVisibility(View.VISIBLE);

					String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
					String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));


					CSChatLocation location = CSChatObj.getLocationFromChatID(chatid);
					String address = location.getAddress();
					//LOG.info("Location recv address:"+address);
					//Double lattitude = location.getLat();
					//Double longnitude = location.getLng();
					holder.recvimagetext.setText(address);
//CSChatObj.downloadFile();

					if (thumbkey != null && !thumbkey.equals("")) {
						new ImageDownloaderTask(holder.recv_imageview).execute("app",thumbkey,"");
					}

					//holder.sent_imageview.

				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_IMAGE) {
					holder.receiveMessage.setVisibility(View.GONE);
                    holder.receive_image_layout.setVisibility(View.VISIBLE);

					String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
					String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
					String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
					int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


					String downloadedfilepath = utils.getReceivedImagesDirectory()+"/"+mainkey;
					if(new File(downloadedfilepath).exists()) {
						Bitmap myBitmap = BitmapFactory.decodeFile(downloadedfilepath);
						holder.recv_imageview.setImageBitmap(myBitmap);
						holder.recv_downloadimage.setVisibility(View.GONE);
					} else {
						holder.recv_downloadimage.setVisibility(View.VISIBLE);
					}

					 if(thumstatus == 1&&!new File(downloadedfilepath).exists()) {

						if (thumbkey != null && !thumbkey.equals("")) {
							new ImageDownloaderTask(holder.recv_imageview).execute("app",thumbkey,"");
						}

					}


					LOG.info("Yes i am here yes");

					holder.recvimagetext.setVisibility(View.VISIBLE);
					String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					holder.recvimagetext.setText(contenttype);

				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_VIDEO) {
					holder.receiveMessage.setVisibility(View.GONE);
					holder.receive_image_layout.setVisibility(View.VISIBLE);

					String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
					String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
					String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
					int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


					String downloadedfilepath = utils.getReceivedImagesDirectory()+"/"+mainkey;
					if(new File(downloadedfilepath).exists()) {
						holder.recv_downloadimage.setVisibility(View.GONE);
					} else {
						holder.recv_downloadimage.setVisibility(View.VISIBLE);
					}

					if(thumstatus == 1) {

						if (thumbkey != null && !thumbkey.equals("")) {
							new ImageDownloaderTask(holder.recv_imageview).execute("app",thumbkey,"");
						}

					}


					LOG.info("Yes i am here yes");

					holder.recvimagetext.setVisibility(View.VISIBLE);
					String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					holder.recvimagetext.setText(contenttype);







				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_CONTACT) {

					//holder.receive_image_layout.setVisibility(View.GONE);

					String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
					CSChatContact contact = CSChatObj.getContactFromChatID(chatid);

					List array = contact.getNumbers();
					List array1 = contact.getLabels();


					String finalmessage = "";
					for (int i = 0; i < array.size(); i++) {
						//LOG.info("MEssage number:" + array.get(i).toString());
						//LOG.info("labels:" + array.getJSONObject(i).getString("labels"));
						if(finalmessage.equals("")) {
							finalmessage =  contact.getName() + "\n" + "<u>"+array.get(i).toString() + "</u>"+ "\n" + array1.get(i).toString();

						} else {
							finalmessage = "\n\n"+finalmessage + contact.getName() + "\n" + "<u>"+array.get(i).toString()+ "</u>" + "\n" + array1.get(i).toString();
						}
					}

					holder.receiveMessage.setText(Html.fromHtml(finalmessage));




				}
				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_DOCUMENT) {
					holder.receiveMessage.setVisibility(View.GONE);
					holder.receive_image_layout.setVisibility(View.VISIBLE);

					String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
					String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
					String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
					int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


					String downloadedfilepath = utils.getReceivedImagesDirectory()+"/"+mainkey;
					if(new File(downloadedfilepath).exists()) {
						holder.recv_downloadimage.setVisibility(View.GONE);
						holder.recv_imageview.setImageResource(R.drawable.defaultocicon);
					} else {
						holder.recv_downloadimage.setVisibility(View.VISIBLE);
					}
/*
					if(thumstatus == 1) {

						if (thumbkey != null && !thumbkey.equals("")) {
							new ImageDownloaderTask(holder.recv_imageview).execute("app",thumbkey,"");
						}

					}
*/

					LOG.info("Yes i am here yes");

					holder.recvimagetext.setVisibility(View.VISIBLE);
					String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					holder.recvimagetext.setText(contenttype);
				}

				else if(cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_AUDIO) {
					holder.receiveMessage.setVisibility(View.GONE);
					holder.receive_image_layout.setVisibility(View.VISIBLE);

					String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
					String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
					String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
					int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


					String downloadedfilepath = utils.getReceivedImagesDirectory()+"/"+mainkey;
					if(new File(downloadedfilepath).exists()) {
						holder.recv_downloadimage.setVisibility(View.GONE);
						holder.recv_imageview.setImageResource(R.drawable.defaultauioicon);
					} else {
						holder.recv_downloadimage.setVisibility(View.VISIBLE);
					}

/*
					if(thumstatus == 1) {

						if (thumbkey != null && !thumbkey.equals("")) {
							new ImageDownloaderTask(holder.recv_imageview).execute("app",thumbkey,"");
						}

					}
*/

					LOG.info("Yes i am here yes");

					holder.recvimagetext.setVisibility(View.VISIBLE);
					String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					holder.recvimagetext.setText(contenttype);
				}






                holder.recv_downloadimage.setOnClickListener(new View.OnClickListener() {


                    public void onClick(View v) {
                        try {
                            String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
							String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
							LOG.info(" v.getId() from adaptor:" +  v.getId());
							LOG.info(" chatid from adaptor:" +  chatid);

							holder.progressBar.setVisibility(View.VISIBLE);
							holder.recv_downloadimage.setVisibility(View.INVISIBLE);
							//CSChatObj.downloadFile(chatid,utils.getReceivedImagesDirectory()+"/"+mainkey);
							CSChatObj.downloadFile(chatid);




                        } catch (Exception ex) {
                            utils.logStacktrace(ex);
                        }
                    }


                });








			}
			convertView.setTag(holder);


		}catch (Exception ex) {
			utils.logStacktrace(ex);
		}
	}
	private String getFormattedDate1(String dateStr) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		Date date = null;
		try {
			date = sdf1.parse(dateStr);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			utils.logStacktrace(e);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String shortDate = sdf.format(date);
		return shortDate;
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
			return new SimpleDateFormat("hh:mm:ss a").format(dateStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View convertView = inflater.inflate(R.layout.chat_item, parent, false);
		return convertView;
	}
	class ViewHolder {
		RelativeLayout sendLayout;
		RelativeLayout receiveLayout;
		TextView dateView;
		TextView sendTime;
		TextView receiveTime;
		TextView sendMessage;
		TextView receiveMessage;
		TextView time;
		ImageView sentIcon;
		ImageView deliveredIcon;
		ImageView sentIcon1;
		ImageView deliveredIcon1;
		ImageView failedtext;
		RelativeLayout sent_imagelayout;
		ImageView sent_imageview;
		RelativeLayout receive_image_layout;
		ImageView recv_imageview;
		TextView recvimagetext;
		TextView sentimagetext;
		RelativeLayout statusandtime_layout;
		 com.github.lzyzsd.circleprogress.DonutProgress progressBar;
		 ImageView recv_downloadimage;
	}


	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	@Override
	protected void onContentChanged() {
		// TODO Auto-generated method stub
		super.onContentChanged();
	}
	public static boolean isYesterday(long date) {
		Calendar now = Calendar.getInstance();
		Calendar cdate = Calendar.getInstance();
		cdate.setTimeInMillis(date);
		now.add(Calendar.DATE,-1);
		return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
				&& now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
				&& now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
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
				} else {
					photo = utils.loadContactPhoto(Long.parseLong(params[1]));
				}
				if(params[0].equals("app")&&photo == null) {
					photo = utils.loadContactPhoto(Long.parseLong(params[2]));
				}

				if(photo == null) {
					photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.imageplaceholder);
				} else {
					scaleit = true;
					//ImageView imageView = imageViewReference.get();
					//imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				}
			} catch (Exception e) {
				photo = BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.imageplaceholder);
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



	public void startAnimating(ProgressBar progressBar) {
		ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
		animation.setDuration (5000); //in milliseconds
		animation.setInterpolator (new DecelerateInterpolator());
		animation.start ();
	}

	public void stopAnimating(ProgressBar progressBar) {
		progressBar.clearAnimation();
	}



}
