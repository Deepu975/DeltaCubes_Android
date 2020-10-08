package com.cavox.adapaters;

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
import android.widget.ImageView;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationAdapterGroup extends CursorAdapter {
    CSChat CSChatObj = new CSChat();
    static String sentimagedirectory = "";
    static String recvimagedirectory = "";
    static String destination = "";
    /**
     * ImageLoader variables
     */
    DisplayImageOptions mOptions;
    ImageLoader mImageLoader;
    /*private Context context;
    private Cursor cursor;*/
    Context mycontext;

    public ConversationAdapterGroup(Context context, Cursor c, int flags, String mydestination) {
        super(context, c, flags);
		/*this.context = context;
		cursor = c;*/
        mycontext = context;
        sentimagedirectory = utils.getSentImagesDirectory();
        recvimagedirectory = utils.getReceivedImagesDirectory();
        destination = mydestination;
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        }

        mOptions = new DisplayImageOptions.Builder()
                /*.showImageOnLoading(R.drawable.img_profile_default)
                .showImageForEmptyUri(R.drawable.img_profile_default)
                .showImageOnFail(R.drawable.img_profile_default)*/
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    @Override
    public void bindView(final View convertView, Context context, final Cursor cursor) {
        try {

            final ViewHolder holder = new ViewHolder();
            holder.position = cursor.getPosition();
            String prevDate = null;
            try {
                //if (convertView.getTag() == null) {


                int viewType = this.getItemViewType(cursor);

                switch (viewType) {
                    case 0:

                        break;
                    case 1:
                        holder.dateView = (TextView) convertView.findViewById(R.id.dateView);

                        holder.sendLayout = (RelativeLayout) convertView.findViewById(R.id.send_layout);
                        holder.sendMessage = (TextView) convertView.findViewById(R.id.chat_send_text);


                        holder.statusandtime_layout = (RelativeLayout) convertView.findViewById(R.id.statusandtime);
                        holder.failedtext = (ImageView) convertView.findViewById(R.id.failedimage);
                        holder.sentIcon = (ImageView) convertView.findViewById(R.id.sent_icon);
                        holder.deliveredIcon = (ImageView) convertView.findViewById(R.id.delivered_icon);
                        holder.sentIcon1 = (ImageView) convertView.findViewById(R.id.sent_icon1);
                        holder.deliveredIcon1 = (ImageView) convertView.findViewById(R.id.delivered_icon1);
                        holder.sendTime = (TextView) convertView.findViewById(R.id.chat_send_time);

                        break;
                    case 2:
                        holder.dateView = (TextView) convertView.findViewById(R.id.dateView);

                        holder.sendLayout = (RelativeLayout) convertView.findViewById(R.id.send_layout);
                        holder.sent_imagelayout = (RelativeLayout) convertView.findViewById(R.id.sent_imagelayout);
                        holder.sent_imageview = (ImageView) convertView.findViewById(R.id.sent_imageview);
                        holder.sentimagetext = (TextView) convertView.findViewById(R.id.sentimagetext);

                        holder.statusandtime_layout = (RelativeLayout) convertView.findViewById(R.id.statusandtime);
                        holder.failedtext = (ImageView) convertView.findViewById(R.id.failedimage);
                        holder.sentIcon = (ImageView) convertView.findViewById(R.id.sent_icon);
                        holder.deliveredIcon = (ImageView) convertView.findViewById(R.id.delivered_icon);
                        holder.sentIcon1 = (ImageView) convertView.findViewById(R.id.sent_icon1);
                        holder.deliveredIcon1 = (ImageView) convertView.findViewById(R.id.delivered_icon1);
                        holder.sendTime = (TextView) convertView.findViewById(R.id.chat_send_time);
                        holder.progressBar1 = (com.github.lzyzsd.circleprogress.DonutProgress) convertView.findViewById(R.id.progressBar);
                        holder.sent_uploadimage = (ImageView) convertView.findViewById(R.id.uploadimage);
                        holder.progressBar1.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        holder.dateView = (TextView) convertView.findViewById(R.id.dateView);
                        holder.receiveLayout = (RelativeLayout) convertView.findViewById(R.id.receive_layout);
                        holder.receiveMessage = (TextView) convertView.findViewById(R.id.chat_receive_text);
                        holder.receiveTime = (TextView) convertView.findViewById(R.id.chat_receive_time);
                        holder.sender = (TextView) convertView.findViewById(R.id.sender);
                        break;
                    case 4:
                        holder.dateView = (TextView) convertView.findViewById(R.id.dateView);
                        holder.receiveLayout = (RelativeLayout) convertView.findViewById(R.id.receive_layout);
                        holder.receive_image_layout = (RelativeLayout) convertView.findViewById(R.id.receive_image_layout);
                        holder.recv_imageview = (ImageView) convertView.findViewById(R.id.recv_imageview);
                        holder.recvimagetext = (TextView) convertView.findViewById(R.id.recvimagetext);
                        holder.progressBar = (com.github.lzyzsd.circleprogress.DonutProgress) convertView.findViewById(R.id.progressBar);
                        holder.recv_downloadimage = (ImageView) convertView.findViewById(R.id.downloadimage);
                        holder.receiveTime = (TextView) convertView.findViewById(R.id.chat_receive_time);
                        holder.sender = (TextView) convertView.findViewById(R.id.sender);
                        break;
                }
                convertView.setTag(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID)));

                //} else {
                //holder = (ViewHolder) convertView.getTag();
                //}
                //holder.position = cursor.getPosition();
            } catch (Exception ex) {
                utils.logStacktrace(ex);
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
                if (DateUtils.isToday(dateStr)) {
                    holder.dateView.setText("Today");
                } else if (isYesterday(dateStr)) {
                    holder.dateView.setText("Yesterday");
                } else {
                    holder.dateView.setText(currentDate);
                }
            } else {
                holder.dateView.setVisibility(View.GONE);
            }


            if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_IS_SENDER)) == 1) {
				/*holder.sendLayout.setVisibility(View.VISIBLE);
				holder.statusandtime_layout.setVisibility(View.VISIBLE);

				holder.receiveLayout.setVisibility(View.GONE);
                holder.failedtext.setVisibility(View.GONE);
				holder.sentimagetext.setVisibility(View.GONE);
*/
                if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTPLAIN || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTHTML || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_UNKNOWN_CHAT_TYPE) {
                    //holder.sent_imagelayout.setVisibility(View.GONE);
                    holder.sendMessage.setText(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE)));

                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_LOCATION) {
                    //holder.sendMessage.setVisibility(View.GONE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumbstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


                    CSChatLocation location = CSChatObj.getLocationFromChatID(chatid);

                    String address = location.getAddress();
                    holder.sentimagetext.setText(address);


                    if (location!=null&&!location.equals("")) {
                        new LocationBitmap(holder.position, holder, holder.sent_imageview).execute("location", location.getLat().toString(), location.getLng().toString());
                    }


                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_IMAGE) {
                    //holder.sendMessage.setVisibility(View.GONE);

                    //String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    //String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    //String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    //int thumbstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));
                    String uploadfilepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_UPLOAD_FILE_PATH));
                    // Bitmap myBitmap = BitmapFactory.decodeFile(new File(uploadfilepath).getAbsolutePath());
                    //  Bitmap myBitmap = decodeFile(new File(uploadfilepath));

                    mImageLoader.loadImage("file://" + uploadfilepath, mOptions, new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            // cache is now warmed up
                            holder.sent_uploadimage.setVisibility(View.INVISIBLE);
                            holder.sent_imageview.setImageBitmap(loadedImage);

                        }
                    });

                    //if (thumbkey != null && !thumbkey.equals("")&&thumbstatus == 1) {
                    //new ImageDownloaderTask(holder.sent_imageview).execute("app",thumbkey,"");
                    //}

                    //holder.sentimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME)); holder.sentimagetext.setText("");


                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_VIDEO) {
                    //holder.sendMessage.setVisibility(View.GONE);
                    String filepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_UPLOAD_FILE_PATH));
                    Bitmap myBitmap = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Video.Thumbnails.MICRO_KIND);

                    holder.sent_imageview.setImageBitmap(myBitmap);

                    //holder.sentimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME));
                  //  holder.sentimagetext.setText(contenttype);


                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_CONTACT) {

                    //holder.sent_imagelayout.setVisibility(View.GONE);

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
                        ////LOG.info("MEssage number:" + array.get(i).toString());
                        ////LOG.info("labels:" + array.getJSONObject(i).getString("labels"));
                        if (finalmessage.equals("")) {
                            finalmessage = contact.getName() + "<br/>" + "<u>" + array.get(i).toString() + "</u>" + "<br/>" + array1.get(i).toString();

                        } else {
                            finalmessage = "<br/>" + finalmessage + contact.getName() + "<br/>" + "<u>" + array.get(i).toString() + "</u>" + "<br/>" + array1.get(i).toString();
                        }
                    }

                    holder.sendMessage.setText(Html.fromHtml(finalmessage));


                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_DOCUMENT) {
                    //holder.sendMessage.setVisibility(View.GONE);
                    holder.sent_imageview.setImageResource(R.drawable.defaultocicon);

                    //holder.sentimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME));
                    holder.sentimagetext.setText(contenttype);

                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_AUDIO) {
                    //holder.sendMessage.setVisibility(View.GONE);

                    holder.sent_imageview.setImageResource(R.drawable.defaultauioicon);

                    //holder.sentimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME));
                    holder.sentimagetext.setText(contenttype);
                }


                holder.sendTime.setText(shortTimeStr);
                holder.sentIcon.setVisibility(View.INVISIBLE);
                holder.deliveredIcon.setVisibility(View.INVISIBLE);
                holder.sentIcon1.setVisibility(View.INVISIBLE);
                holder.deliveredIcon1.setVisibility(View.INVISIBLE);
                holder.failedtext.setVisibility(View.INVISIBLE);

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
                    ////LOG.info("Chat status setting as read:"+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE)));
                    holder.sentIcon.setVisibility(View.INVISIBLE);
                    holder.deliveredIcon.setVisibility(View.INVISIBLE);
                    holder.sentIcon1.setVisibility(View.VISIBLE);
                    holder.deliveredIcon1.setVisibility(View.VISIBLE);

                }

            } else {
/*
                holder.recv_downloadimage.setVisibility(View.GONE);
				holder.progressBar.setVisibility(View.GONE);
				holder.sendLayout.setVisibility(View.GONE);
				holder.statusandtime_layout.setVisibility(View.GONE);

				holder.receiveLayout.setVisibility(View.VISIBLE);
				holder.receiveMessage.setVisibility(View.VISIBLE);


				holder.receive_image_layout.setVisibility(View.GONE);
				*/

                String filename = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME));
                //LOG.info("filename from app:"+filename);


                holder.receiveTime.setText(shortTimeStr);
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_GROUP_MESSAGE_SENDER));
                String name = "";
                Cursor ccr = CSDataProvider.getContactCursorByNumber(number);
                if (ccr.getCount() > 0) {
                    ccr.moveToNext();
                    name = ccr.getString(ccr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
                }
                ccr.close();
                if (name.equals("")) {
                    name = number;
                }
                holder.sender.setText(name);

                //LOG.info("TEST MESSAGE CHAT_ID:"+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID)));
                //LOG.info("TEST MESSAGE MSG_TYPE:"+cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)));
                //LOG.info("TEST MESSAGE MESSAGE:"+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE)));
                //LOG.info("TEST MESSAGE THUMBAINALKEY:"+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY)));


                if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTPLAIN || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTHTML || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_UNKNOWN_CHAT_TYPE) {

                    holder.receiveMessage.setText(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE)));

                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_LOCATION) {
                    //holder.receiveMessage.setVisibility(View.GONE);
                    //holder.receive_image_layout.setVisibility(View.VISIBLE);

                    holder.recv_downloadimage.setVisibility(View.INVISIBLE);
                    holder.progressBar.setVisibility(View.INVISIBLE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));


                    CSChatLocation location = CSChatObj.getLocationFromChatID(chatid);
                    String address = location.getAddress();
                    ////LOG.info("Location recv address:"+address);
                    //Double lattitude = location.getLat();
                    //Double longnitude = location.getLng();
                    holder.recvimagetext.setText(address);
//CSChatObj.downloadFile();

                    if (location!=null&&!location.equals("")) {
                        new LocationBitmap(holder.position, holder, holder.recv_imageview).execute("location", location.getLat().toString(), location.getLng().toString());
                    }

                    //holder.sent_imageview.

                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_IMAGE) {
                    //holder.receiveMessage.setVisibility(View.GONE);
                    //holder.receive_image_layout.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));
                    String downloadedfilepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DOWNLOAD_FILE_PATH));
                    //String downloadedfilepath = utils.getReceivedImagesDirectory() + "/" + mainkey;


                    if (new File(downloadedfilepath).exists()) {

                        mImageLoader.loadImage("file://" + downloadedfilepath, mOptions, new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view,
                                                          Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                // cache is now warmed up
                                holder.recv_imageview.setImageBitmap(loadedImage);
                                holder.recv_downloadimage.setVisibility(View.INVISIBLE);

                            }


                        });

                    } else {
                        holder.recv_downloadimage.setVisibility(View.VISIBLE);
                        //LOG.info(" chatid from adaptor image:" + chatid);
                    }

                    if (thumstatus == 1 && !new File(downloadedfilepath).exists()) {

                        if (thumbkey != null && !thumbkey.equals("")) {
                            new ImageDownloaderTask(holder.recv_imageview).execute("app", thumbkey, "");
                        }

                    }


                    ////LOG.info("Yes i am here yes");

                    //holder.recvimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME));
                    holder.recvimagetext.setText(contenttype);

                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_VIDEO) {
                    //holder.receiveMessage.setVisibility(View.GONE);
                    //holder.receive_image_layout.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));

                    String downloadedfilepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DOWNLOAD_FILE_PATH));
                    //String downloadedfilepath = utils.getReceivedImagesDirectory() + "/" + mainkey;
                    if (new File(downloadedfilepath).exists()) {
                        holder.recv_downloadimage.setVisibility(View.INVISIBLE);
                    } else {
                        holder.recv_downloadimage.setVisibility(View.VISIBLE);
                        ////LOG.info(" chatid from adaptor video:" + chatid);
                    }

                    if (thumstatus == 1) {

                        if (thumbkey != null && !thumbkey.equals("")) {
                            new ImageDownloaderTask(holder.recv_imageview).execute("app", thumbkey, "");
                        }

                    }


                    ////LOG.info("Yes i am here yes");

                    //holder.recvimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME));
                 //   holder.recvimagetext.setText(contenttype);


                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_CONTACT) {

                    //holder.receive_image_layout.setVisibility(View.GONE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    CSChatContact contact = CSChatObj.getContactFromChatID(chatid);

                    List array = contact.getNumbers();
                    List array1 = contact.getLabels();


                    String finalmessage = "";
                    for (int i = 0; i < array.size(); i++) {
                        ////LOG.info("MEssage number:" + array.get(i).toString());
                        ////LOG.info("labels:" + array.getJSONObject(i).getString("labels"));
                        if (finalmessage.equals("")) {
                            finalmessage = contact.getName() + "<br/>" + "<u>" + array.get(i).toString() + "</u>" + "<br/>" + array1.get(i).toString();

                        } else {
                            finalmessage = "<br/>" + finalmessage + contact.getName() + "<br/>" + "<u>" + array.get(i).toString() + "</u>" + "<br/>" + array1.get(i).toString();
                        }
                    }

                    holder.receiveMessage.setText(Html.fromHtml(finalmessage));


                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_DOCUMENT) {
                    //holder.receiveMessage.setVisibility(View.GONE);
                    //holder.receive_image_layout.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));

                    String downloadedfilepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DOWNLOAD_FILE_PATH));
                    //String downloadedfilepath = utils.getReceivedImagesDirectory() + "/" + mainkey;
                    if (new File(downloadedfilepath).exists()) {
                        holder.recv_downloadimage.setVisibility(View.INVISIBLE);
                        holder.recv_imageview.setImageResource(R.drawable.defaultocicon);
                    } else {
                        holder.recv_downloadimage.setVisibility(View.VISIBLE);
                        holder.recv_imageview.setImageResource(R.drawable.defaultocicon);
                        //LOG.info(" chatid from adaptor document:" + chatid);
                    }
/*
					if(thumstatus == 1) {

						if (thumbkey != null && !thumbkey.equals("")) {
							new ImageDownloaderTask(holder.recv_imageview).execute("app",thumbkey,"");
						}

					}
*/

                    ////LOG.info("Yes i am here yes");

                    //holder.recvimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME));
                    holder.recvimagetext.setText(contenttype);
                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_AUDIO) {
                    //holder.receiveMessage.setVisibility(View.GONE);
                    //holder.receive_image_layout.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.INVISIBLE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));

                    String downloadedfilepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DOWNLOAD_FILE_PATH));
                    //String downloadedfilepath = utils.getReceivedImagesDirectory() + "/" + mainkey;
                    if (new File(downloadedfilepath).exists()) {
                        holder.recv_downloadimage.setVisibility(View.INVISIBLE);
                        holder.recv_imageview.setImageResource(R.drawable.defaultauioicon);
                    } else {
                        holder.recv_downloadimage.setVisibility(View.VISIBLE);
                        holder.recv_imageview.setImageResource(R.drawable.defaultauioicon);
                        //LOG.info("chatid from adaptor audio:" + chatid);
                    }

/*
					if(thumstatus == 1) {

						if (thumbkey != null && !thumbkey.equals("")) {
							new ImageDownloaderTask(holder.recv_imageview).execute("app",thumbkey,"");
						}

					}
*/

                    ////LOG.info("Yes i am here yes");

                    //holder.recvimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_FILE_NAME));
                    holder.recvimagetext.setText(contenttype);
                }

                if (holder.recv_downloadimage != null) {
                    holder.recv_downloadimage.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            try {


                                //LOG.info(" holder.position:" + holder.position);
                                //LOG.info(" cursor.position:" + cursor.getPosition());

                                Cursor cur = CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_GROUPID, destination);
                                cur.moveToPosition(holder.position);
                                String mainkey = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                                String chatid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                                //LOG.info(" chatid from adaptor1:" + chatid);
                                cur.close();

                                //holder.progressBar.setVisibility(View.VISIBLE);
                                holder.recv_downloadimage.setVisibility(View.INVISIBLE);
                                //CSChatObj.downloadFile(chatid, utils.getReceivedImagesDirectory() + "/" + mainkey);
                                CSChatObj.downloadFile(chatid);
                            } catch (Exception ex) {
                                utils.logStacktrace(ex);
                            }
                        }
                    });
                }
                if (cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS)) == CSConstants.MESSAGE_RECEIVED || cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS)) == CSConstants.MESSAGE_DELIVERED_ACK) {

                    int isgroupmessage = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_IS_GROUP_MESSAGE));

                    CSChatObj.sendReadReceipt(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID)));

                    //CSDataProvider.updateChatMessageReadStatus(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID)), CSConstants.MESSAGE_READ);
                    this.changeCursor(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_GROUPID, destination));
                    notifyDataSetChanged();
                    return;
                }
            }


        } catch (Exception ex) {
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
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public int getViewTypeCount() {
        return 5;
    }

    private int getItemViewType(Cursor cursor) {
        int chattype = cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE));
        int issender = cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_IS_SENDER));
        int returnvalue = 0;
        if (chattype == CSConstants.E_TEXTPLAIN || chattype == CSConstants.E_TEXTHTML || chattype == CSConstants.E_CONTACT) {
            if (issender == 1) {
                returnvalue = 1;
            } else {
                returnvalue = 3;
            }
        } else if (chattype == CSConstants.E_LOCATION || chattype == CSConstants.E_IMAGE || chattype == CSConstants.E_VIDEO || chattype == CSConstants.E_DOCUMENT || chattype == CSConstants.E_AUDIO) {
            if (issender == 1) {
                returnvalue = 2;
            } else {
                returnvalue = 4;
            }
        } else {
            returnvalue = 0;
        }

        return returnvalue;

    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getItemViewType(cursor);
    }


    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.chat_oldgrp_item_empty, parent, false);
        try {
            final ViewHolder holder = new ViewHolder();

            int viewType = this.getItemViewType(cursor);

            switch (viewType) {
                case 0:
                    convertView = inflater.inflate(R.layout.chat_oldgrp_item_empty, parent, false);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.chat_oldgrp_item1, parent, false);
                    holder.dateView = (TextView) convertView.findViewById(R.id.dateView);

                    holder.sendLayout = (RelativeLayout) convertView.findViewById(R.id.send_layout);
                    holder.sendMessage = (TextView) convertView.findViewById(R.id.chat_send_text);


                    holder.statusandtime_layout = (RelativeLayout) convertView.findViewById(R.id.statusandtime);
                    holder.failedtext = (ImageView) convertView.findViewById(R.id.failedimage);
                    holder.sentIcon = (ImageView) convertView.findViewById(R.id.sent_icon);
                    holder.deliveredIcon = (ImageView) convertView.findViewById(R.id.delivered_icon);
                    holder.sentIcon1 = (ImageView) convertView.findViewById(R.id.sent_icon1);
                    holder.deliveredIcon1 = (ImageView) convertView.findViewById(R.id.delivered_icon1);
                    holder.sendTime = (TextView) convertView.findViewById(R.id.chat_send_time);

                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.chat_oldgrp_item2, parent, false);
                    holder.dateView = (TextView) convertView.findViewById(R.id.dateView);

                    holder.sendLayout = (RelativeLayout) convertView.findViewById(R.id.send_layout);
                    holder.sent_imagelayout = (RelativeLayout) convertView.findViewById(R.id.sent_imagelayout);
                    holder.sent_imageview = (ImageView) convertView.findViewById(R.id.sent_imageview);
                    holder.sentimagetext = (TextView) convertView.findViewById(R.id.sentimagetext);

                    holder.statusandtime_layout = (RelativeLayout) convertView.findViewById(R.id.statusandtime);
                    holder.failedtext = (ImageView) convertView.findViewById(R.id.failedimage);
                    holder.sentIcon = (ImageView) convertView.findViewById(R.id.sent_icon);
                    holder.deliveredIcon = (ImageView) convertView.findViewById(R.id.delivered_icon);
                    holder.sentIcon1 = (ImageView) convertView.findViewById(R.id.sent_icon1);
                    holder.deliveredIcon1 = (ImageView) convertView.findViewById(R.id.delivered_icon1);
                    holder.sendTime = (TextView) convertView.findViewById(R.id.chat_send_time);
                    holder.progressBar1 = (com.github.lzyzsd.circleprogress.DonutProgress) convertView.findViewById(R.id.progressBar);
                    holder.sent_uploadimage = (ImageView) convertView.findViewById(R.id.uploadimage);
                    break;
                case 3:
                    convertView = inflater.inflate(R.layout.chat_oldgrp_item5, parent, false);
                    holder.dateView = (TextView) convertView.findViewById(R.id.dateView);
                    holder.receiveLayout = (RelativeLayout) convertView.findViewById(R.id.receive_layout);
                    holder.receiveMessage = (TextView) convertView.findViewById(R.id.chat_receive_text);
                    holder.receiveTime = (TextView) convertView.findViewById(R.id.chat_receive_time);
                    holder.sender = (TextView) convertView.findViewById(R.id.sender);
                    break;
                case 4:
                    convertView = inflater.inflate(R.layout.chat_oldgrp_item6, parent, false);
                    holder.dateView = (TextView) convertView.findViewById(R.id.dateView);
                    holder.receiveLayout = (RelativeLayout) convertView.findViewById(R.id.receive_layout);
                    holder.receive_image_layout = (RelativeLayout) convertView.findViewById(R.id.receive_image_layout);
                    holder.recv_imageview = (ImageView) convertView.findViewById(R.id.recv_imageview);
                    holder.recvimagetext = (TextView) convertView.findViewById(R.id.recvimagetext);
                    holder.progressBar = (com.github.lzyzsd.circleprogress.DonutProgress) convertView.findViewById(R.id.progressBar);
                    holder.recv_downloadimage = (ImageView) convertView.findViewById(R.id.downloadimage);
                    holder.receiveTime = (TextView) convertView.findViewById(R.id.chat_receive_time);
                    holder.sender = (TextView) convertView.findViewById(R.id.sender);
                    break;

                default:
                    convertView = inflater.inflate(R.layout.chat_oldgrp_item_empty, parent, false);
                    break;
            }

            //convertView.setTag(convertView);

        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }

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
        com.github.lzyzsd.circleprogress.DonutProgress progressBar1;
        ImageView sent_uploadimage;
        TextView sender;
        int position;
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
        now.add(Calendar.DATE, -1);
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
                if (params[0].equals("app")) {
                    photo = CSDataProvider.getImageBitmap(params[1]);
                } else {
                    //photo = utils.loadContactPhoto(Long.parseLong(params[1]));
                }
				/*
				if(params[0].equals("app")&&photo == null) {
					photo = utils.loadContactPhoto(Long.parseLong(params[2]));
				}
*/
                if (photo == null) {
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
							////LOG.info("Yes scaleit is true");
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


    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (Exception e) {
        }
        return null;
    }

    private class LocationBitmap extends AsyncTask<String, Void, Bitmap> {
        private int mPosition;
        private ViewHolder mHolder;
//private String imgref;

        private final WeakReference<ImageView> imageViewReference;

        public LocationBitmap(int position, ViewHolder holder, ImageView sent_imageview) {
            mPosition = position;
            mHolder = holder;

            imageViewReference = new WeakReference<ImageView>(sent_imageview);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap photo = null;
            try {
                photo = utils.getGoogleMapThumbnail(params[1], params[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return photo;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    if (mHolder.position == mPosition) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
            super.onPostExecute(bitmap);
        }
    }

}
