package com.cavox.adapaters;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.recyclerview.widget.RecyclerView;
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
import com.cavox.utils.GlobalVariables;
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

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    CSChat CSChatObj = new CSChat();
    static String sentimagedirectory = "";
    static String recvimagedirectory = "";
    static String destination = "";
    // Context mycontext;
    Cursor cursor;

    public MyRecyclerAdapter(Cursor c, String mydestination) {
        //this.mycontext = context;
        this.sentimagedirectory = utils.getSentImagesDirectory();
        this.recvimagedirectory = utils.getReceivedImagesDirectory();
        this.destination = mydestination;
        this.cursor = c;
    }
    public void changeCursor(Cursor cursor) {
        try {
           /* Cursor old = swapCursor(cursor);
            if (old != null) {
                old.close();
            }*/
        } catch (Exception ex) {utils.logStacktrace(ex);}
    }
    public void swapCursorAndNotifyDataSetChanged(Cursor newcursor) {
        Cursor oldCursor = cursor;
        try {
            if (cursor == newcursor) {
                return;// null;
            }
            this.cursor = newcursor;
            if (this.cursor != null) {
                this.notifyDataSetChanged();
            }
        } catch (Exception ex) {utils.logStacktrace(ex);}

        if (oldCursor != null) {
            oldCursor.close();
        }
        //return oldCursor;
    }
    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_chat_item, parent, false);
        return new MyViewHolder(itemView);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView sendMessage, receiveMessage, sendTime,receiveTime,dateView,recvimagetext,sentimagetext;
        public RelativeLayout sendLayout,receiveLayout,sent_imagelayout,receive_image_layout,statusandtime_layout;
        public ImageView sentIcon,deliveredIcon,sentIcon1,deliveredIcon1,failedtext,sent_imageview,recv_imageview,recv_downloadimage;
        public com.github.lzyzsd.circleprogress.DonutProgress progressBar;

        public MyViewHolder(View view) {
            super(view);
             sendLayout = (RelativeLayout) view.findViewById(R.id.send_layout);
             receiveLayout = (RelativeLayout) view.findViewById(R.id.receive_layout);
             sendMessage = (TextView) view.findViewById(R.id.chat_send_text);
             receiveMessage = (TextView) view.findViewById(R.id.chat_receive_text);
             sendTime = (TextView) view.findViewById(R.id.chat_send_time);
             receiveTime = (TextView) view.findViewById(R.id.chat_receive_time);
             dateView = (TextView) view.findViewById(R.id.dateView);
             sentIcon = (ImageView) view.findViewById(R.id.sent_icon);
             deliveredIcon = (ImageView) view.findViewById(R.id.delivered_icon);
             sentIcon1 = (ImageView) view.findViewById(R.id.sent_icon1);
             deliveredIcon1 = (ImageView) view.findViewById(R.id.delivered_icon1);
             failedtext = (ImageView) view.findViewById(R.id.failedimage);
             sent_imagelayout = (RelativeLayout) view.findViewById(R.id.sent_imagelayout);
             sent_imageview = (ImageView) view.findViewById(R.id.sent_imageview);
             receive_image_layout = (RelativeLayout) view.findViewById(R.id.receive_image_layout);
             recv_imageview = (ImageView) view.findViewById(R.id.recv_imageview);
             recvimagetext = (TextView) view.findViewById(R.id.recvimagetext);
             sentimagetext = (TextView) view.findViewById(R.id.sentimagetext);
             statusandtime_layout = (RelativeLayout) view.findViewById(R.id.statusandtime);
             progressBar = (com.github.lzyzsd.circleprogress.DonutProgress) view.findViewById(R.id.progressBar);
             recv_downloadimage = (ImageView) view.findViewById(R.id.downloadimage);
        }

        @Override
        public void onClick(View view) {

            GlobalVariables.NewChatActivityObj.onClick(view);
        }

        @Override
        public boolean onLongClick(View view) {
            GlobalVariables.NewChatActivityObj.onLongClick(view);
            return true;
        }
    }





    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        try {

cursor.moveToPosition(position);

            String prevDate = null;
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


                if (cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS)) == CSConstants.MESSAGE_RECEIVED || cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS)) == CSConstants.MESSAGE_DELIVERED_ACK) {
                    CSChatObj.sendReadReceipt(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID)));
                    //CSDataProvider.updateChatMessageReadStatus(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID)), CSConstants.MESSAGE_READ);
                    changeCursor(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID, destination));
                    //notifyDataSetChanged();
                    return;
                }

                if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTPLAIN || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_TEXTHTML || cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_UNKNOWN_CHAT_TYPE) {

                    holder.receiveMessage.setText(cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE)));

                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_LOCATION) {
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
                        new ImageDownloaderTask(holder.recv_imageview).execute("app", thumbkey, "");
                    }

                    //holder.sent_imageview.

                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_IMAGE) {
                    holder.receiveMessage.setVisibility(View.GONE);
                    holder.receive_image_layout.setVisibility(View.VISIBLE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


                    String downloadedfilepath = utils.getReceivedImagesDirectory() + "/" + mainkey;
                    if (new File(downloadedfilepath).exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(downloadedfilepath);
                        holder.recv_imageview.setImageBitmap(myBitmap);
                        holder.recv_downloadimage.setVisibility(View.GONE);
                    } else {
                        holder.recv_downloadimage.setVisibility(View.VISIBLE);
                    }

                    if (thumstatus == 1 && !new File(downloadedfilepath).exists()) {

                        if (thumbkey != null && !thumbkey.equals("")) {
                            new ImageDownloaderTask(holder.recv_imageview).execute("app", thumbkey, "");
                        }

                    }


                    LOG.info("Yes i am here yes");

                    holder.recvimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
                    holder.recvimagetext.setText(contenttype);

                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_VIDEO) {
                    holder.receiveMessage.setVisibility(View.GONE);
                    holder.receive_image_layout.setVisibility(View.VISIBLE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


                    String downloadedfilepath = utils.getReceivedImagesDirectory() + "/" + mainkey;
                    if (new File(downloadedfilepath).exists()) {
                        holder.recv_downloadimage.setVisibility(View.GONE);
                    } else {
                        holder.recv_downloadimage.setVisibility(View.VISIBLE);
                    }

                    if (thumstatus == 1) {

                        if (thumbkey != null && !thumbkey.equals("")) {
                            new ImageDownloaderTask(holder.recv_imageview).execute("app", thumbkey, "");
                        }

                    }


                    LOG.info("Yes i am here yes");

                    holder.recvimagetext.setVisibility(View.VISIBLE);
                    String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
                    holder.recvimagetext.setText(contenttype);


                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_CONTACT) {

                    //holder.receive_image_layout.setVisibility(View.GONE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    CSChatContact contact = CSChatObj.getContactFromChatID(chatid);

                    List array = contact.getNumbers();
                    List array1 = contact.getLabels();


                    String finalmessage = "";
                    for (int i = 0; i < array.size(); i++) {
                        //LOG.info("MEssage number:" + array.get(i).toString());
                        //LOG.info("labels:" + array.getJSONObject(i).getString("labels"));
                        if (finalmessage.equals("")) {
                            finalmessage = contact.getName() + "\n" + "<u>" + array.get(i).toString() + "</u>" + "\n" + array1.get(i).toString();

                        } else {
                            finalmessage = "\n\n" + finalmessage + contact.getName() + "\n" + "<u>" + array.get(i).toString() + "</u>" + "\n" + array1.get(i).toString();
                        }
                    }

                    holder.receiveMessage.setText(Html.fromHtml(finalmessage));


                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_DOCUMENT) {
                    holder.receiveMessage.setVisibility(View.GONE);
                    holder.receive_image_layout.setVisibility(View.VISIBLE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


                    String downloadedfilepath = utils.getReceivedImagesDirectory() + "/" + mainkey;
                    if (new File(downloadedfilepath).exists()) {
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
                } else if (cursor.getInt(cursor.getColumnIndex(CSDbFields.KEY_CHAT_MSG_TYPE)) == CSConstants.E_AUDIO) {
                    holder.receiveMessage.setVisibility(View.GONE);
                    holder.receive_image_layout.setVisibility(View.VISIBLE);

                    String chatid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
                    String mainkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
                    String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALKEY));
                    int thumstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_THUMBAINALSTATUS));


                    String downloadedfilepath = utils.getReceivedImagesDirectory() + "/" + mainkey;
                    if (new File(downloadedfilepath).exists()) {
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
                            LOG.info(" v.getId() from adaptor:" + v.getId());
                            LOG.info(" chatid from adaptor:" + chatid);

                            holder.progressBar.setVisibility(View.VISIBLE);
                            holder.recv_downloadimage.setVisibility(View.INVISIBLE);
                            //CSChatObj.downloadFile(chatid,  utils.getReceivedImagesDirectory() + "/" + mainkey);
                            CSChatObj.downloadFile(chatid);


                        } catch (Exception ex) {
                            utils.logStacktrace(ex);
                        }
                    }


                });


            }

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






}