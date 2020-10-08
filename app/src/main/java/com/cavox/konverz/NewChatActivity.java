package com.cavox.konverz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;
import com.ca.Utils.CSExplicitEvents;
import com.cavox.adapaters.SimpleImageTextAdapter1;
import com.cavox.adapaters.SimpleTextAdapter;
import com.ca.dao.CSChatContact;
import com.ca.dao.CSChatLocation;
import com.cavox.utils.AppConstants;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.utils;
import com.ca.wrapper.CSChat;
import com.ca.wrapper.CSDataProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.cavox.utils.AppConstants.UIACTION_NEWCHAT;
import static com.cavox.utils.GlobalVariables.LOG;

//import com.ca.wrapper.CSFileTransferUtils;
//import com.google.android.gms.maps.model.LatLng;

//import com.cavox.uiutils.UIActions;

public class NewChatActivity extends AppCompatActivity
	 {
		 androidx.appcompat.app.AlertDialog.Builder successfullyLogin;
		 androidx.appcompat.app.AlertDialog dismisssuccessfullyLogin;
	private RecyclerView mChatList;
	private EditText mDataText;
	private ImageView mEmoticons;
	private ImageView mSend;
	private String destination;
	private boolean isGroupChat = false;
	long trxn;
	private com.cavox.adapaters.MyRecyclerAdapter chatAdapter;
	CSChat CSChatObj = new CSChat();
	private Toolbar toolbar;
	int focus = 0;
		 AlertDialog ad;
		 //CSFileTransferUtils CSFileTransferUtilsObj = new CSFileTransferUtils();
		 //CoreApis CoreApisObj = new CoreApis();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_chat_layout);
		try {
			mChatList = (RecyclerView) findViewById(R.id.chat_data);
			mDataText = (EditText) findViewById(R.id.message);
			mEmoticons = (ImageView) findViewById(R.id.emoticons);
			mSend = (ImageView) findViewById(R.id.send);
			destination = getIntent().getStringExtra("Sender");
			isGroupChat = getIntent().getBooleanExtra("IS_GROUP", false);
			toolbar = (Toolbar) findViewById(R.id.toolbar);
			String name = "";
			Cursor ccfr = CSDataProvider.getContactCursorByNumber(destination);
			//LOG.info("ccfr.getCount():"+ccfr.getCount());
			if (ccfr.getCount() > 0) {
				ccfr.moveToNext();
				name = ccfr.getString(ccfr.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
			}
			ccfr.close();
			if (name.equals("")) {
				toolbar.setTitle(destination);
			} else {
				toolbar.setTitle(name);
			}
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			toolbar.setNavigationOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});

			//getSupportActionBar().setShowHideAnimationEnabled(true);
			//getSupportActionBar().getCustomView().findViewWithTag("SubTitle").setSelected(true);
			//getSupportActionBar().getCustomView().animate().start();







/*
			 final RelativeLayout mainlayout = (RelativeLayout) findViewById(R.id.mainLL);
			 mainlayout.setOnClickListener(new OnClickListener() {


				 public void onClick(View v) {
					 try {
						 InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						 imm.hideSoftInputFromWindow(mainlayout.getWindowToken(), 0);
					 } catch (Exception ex) {
						 utils.logStacktrace(ex);
					 }
				 }


			 });
*/


/*
			 chatAdapter = new ConversationAdapter(ChatActivity.this, CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID,destination), 0);
				mChatList.setFastScrollEnabled(true);
				mChatList.setAdapter(chatAdapter);
				if(chatAdapter.getCount() > 0){
					mChatList.setSelection(chatAdapter.getCount() - 1);
				}*/
			mSend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!mDataText.getText().toString().equals("")) {
						sendMessage(mDataText.getText().toString());
					}


				}
			});
			mDataText.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					try {
						final int DRAWABLE_LEFT = 0;
						final int DRAWABLE_TOP = 1;
						final int DRAWABLE_RIGHT = 2;
						final int DRAWABLE_BOTTOM = 3;
						if (event.getAction() == MotionEvent.ACTION_UP) {
							if (event.getRawX() >= (mDataText.getRight() - mDataText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
								//popupusertoselectimagesource();
								LOG.info("Yes here");
								showfileoptions();
								LOG.info("Yes here1");
								return true;
							}
						}
						return false;
					} catch (Exception ex) {
						utils.logStacktrace(ex);
						return false;
					}
				}
			});
			mDataText.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				}

				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					//LOG.info("Yes on touch up1:"+mDataText.getText().toString());
					if (mDataText.getText().toString().equals("")) {
						mDataText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.fileattachemnt, 0);
					} else {
						mDataText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				}

				@Override
				public void afterTextChanged(Editable editable) {
				}
			});
		} catch (Exception ex) {
			utils.logStacktrace(ex);
		}
	}




		 public void onClick(final View view) {

					 try {
						 int position = mChatList.getChildLayoutPosition(view);
						 LOG.info("position:" + position);

						 Cursor cur = CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID, destination);
						 cur.moveToPosition(position);
						 String chatid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));
						 int chattype = cur.getInt(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MSG_TYPE));
						 int issender = cur.getInt(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_IS_SENDER));

						 if (chattype == 0) {
							 //E_UNKNOWN_CHAT_TYPE;
						 } else if (chattype == 1) {
							 //E_TEXTPLAIN;
						 } else if (chattype == 2) {
							 //E_TEXTHTML;
						 } else if (chattype == 3) {
							 CSChatLocation location = CSChatObj.getLocationFromChatID(chatid);
							 Double lat = location.getLat();
							 Double lng = location.getLng();

							 String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + "&zoom=14&markers=color:blue%7C" + lat + "," + lng;
							 LOG.info("geoUri to show on map:" + geoUri);
							 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
							 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							 getApplicationContext().startActivity(intent);

							 //E_LOCATION;
						 } else if (chattype == 4 || chattype == 5 || chattype == 7 || chattype == 8) {
							 String imagekey = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
							 String contenttype = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
							 String filepath = "";
							 if (issender == 1) {
							 filepath = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_UPLOAD_FILE_PATH));
							 } else {
							 filepath = utils.getReceivedImagesDirectory() + "/" + imagekey;
							 }
							 LOG.info("contenttype contenttype:" + contenttype);
							 Intent intent = new Intent();
							 intent.setAction(Intent.ACTION_VIEW);


							 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
								 Uri apkURI = FileProvider.getUriForFile(
										 getApplicationContext(),
										 getApplicationContext().getApplicationContext()
												 .getPackageName() + ".provider", new File(filepath));
								 intent.setDataAndType(apkURI, contenttype);
								 intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							 } else {
								 intent.setDataAndType(Uri.fromFile(new File(filepath)), contenttype);
							 }

							 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							 getApplicationContext().startActivity(intent);
							 //E_IMAGE;
						 }

					/*else if(chattype == 5) {
						//E_VIDEO;
					}*/
						 else if (chattype == 6) {

							 CSChatContact contact = CSChatObj.getContactFromChatID(chatid);
							 String name = contact.getName();
							 List<String> numbers = contact.getNumbers();
							 List<String> labels = contact.getLabels();
							 String number = "";
							 String label = "";
							 if (numbers.size() > 0) {
								 number = numbers.get(0);

							 }
							 if (labels.size() > 0) {
								 label = labels.get(0);

							 }

							 Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
							 intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
							 intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
							 intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
							 intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, label);
							 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							 getApplicationContext().startActivity(intent);


							 //E_CONTACT;
						 } /*else if (chattype == 7) {
						//E_DOCUMENT;
					} else if (chattype == 8) {
						//E_AUDIO;fde
					}*/
						 cur.close();
					 } catch (Exception ex) {
						 Toast.makeText(getApplicationContext(), "Can't find an application to open!", Toast.LENGTH_SHORT).show();
						 utils.logStacktrace(ex);
					 }
				 }








	public  void onLongClick(final View view) {

		try {
			int pos = mChatList.getChildLayoutPosition(view);
			Cursor cur = CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID, destination);
			cur.moveToPosition(pos);
			String chatid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_ID));

			cur.close();

			showchatoptions(chatid);
		} catch (Exception ex) {
			utils.logStacktrace(ex);
		}



	}




		 private void sendMessage(String message) {
		mDataText.setText("");
		//hideKeyboard();
		try {

				CSChatObj.sendMessage(destination, message,false);

		} catch (Exception e) {
			utils.logStacktrace(e);
		}
	}

		 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			 super.onActivityResult(requestCode, resultCode, data);
			try {
				if(requestCode == 994 && resultCode == Activity.RESULT_OK && data != null) {
					LOG.info("Got Location data");
						CSChatLocation CSChatLocationObj = new CSChatLocation(data.getDoubleExtra("locationlat",0.0),data.getDoubleExtra("locationlng",0.0),data.getStringExtra("address"));
						CSChatObj.sendLocation(destination,CSChatLocationObj,false);

				} else if(requestCode == 995 && resultCode == Activity.RESULT_OK && data != null) {
					LOG.info("Got Contact data");


					ArrayList<String> contacts = data.getStringArrayListExtra("contactnumbers");
					ArrayList<String> labels = data.getStringArrayListExtra("contactslabels");
					ArrayList<String> contactnames = data.getStringArrayListExtra("contactsnames");

					LOG.info("Got Contact data size"+contacts.size());


					for (int i=0;i<contacts.size();i++) {

						List<String> list1 = new ArrayList<String>();
						List<String> list2 = new ArrayList<String>();

					String name = contactnames.get(i);
					list1.add(contacts.get(i));
					list2.add(labels.get(i));



					CSChatContact CSChatContactObj = new CSChatContact(name,list1,list2);
					boolean result = CSChatObj.sendContact(destination,CSChatContactObj,false);
					LOG.info("MEssage Result:"+result);
					}



				}

                else if(requestCode == 890 && resultCode == Activity.RESULT_OK && data != null) {
                    LOG.info("Got sound record data");

                        boolean result = CSChatObj.sendAudio(destination,data.getStringExtra("filepath"),false);
                        LOG.info("MEssage Result:"+result);
                    }

				else if(requestCode == 891 && resultCode == Activity.RESULT_OK && data != null) {
					LOG.info("Got to be forwarded data");
					String chatid = data.getStringExtra("chatid");
					List<String> numbers = data.getStringArrayListExtra("contactnumbers");
if(chatid == null || chatid.equals("")||numbers.isEmpty()) {
	Toast.makeText(NewChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
	return;
} else {
	Toast.makeText(NewChatActivity.this, "Forwarding..", Toast.LENGTH_SHORT).show();
}
					Cursor cursor = CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_ID,chatid);

					cursor.moveToNext();

					String message = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
					//long time = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_TIME));
					//int chatststus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_STATUS));
					int issender = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_IS_SENDER));
					//int isgrpmessage = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_IS_GROUP_MESSAGE));
					//String destinationnumber = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DESTINATION_LOGINID));
					//String destinationgroupid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DESTINATION_GROUPID));
					int msgtype = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MSG_TYPE));
					//String destinationname = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_DESTINATION_NAME));
					//long deliveredtime = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_DELIVERED_TIME));
					//long readtime = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_READ_TIME));
					String uploadfilepath = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_UPLOAD_FILE_PATH));
					//String thumbkey = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_THUMBAINALKEY));
					//String contenttype = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_CONTENT_TYPE));
					//int thumbainalstatus = cursor.getInt(cursor.getColumnIndexOrThrow(CSDbFields.KEY_THUMBAINALSTATUS));


cursor.close();



					for(String number:numbers) {
LOG.info("Forwarding to:"+number);
						switch (msgtype) {
							case CSConstants.E_TEXTPLAIN:
								CSChatObj.sendMessage(number,message,false);
								break;
							case CSConstants.E_TEXTHTML:
								CSChatObj.sendMessage(number,message,false);
								break;
							case CSConstants.E_LOCATION:
								CSChatLocation cschatlocation = CSChatObj.getLocationFromChatID(chatid);
								CSChatObj.sendLocation(number,cschatlocation,false);
								break;
							case CSConstants.E_IMAGE:
								if(issender == 1){
									CSChatObj.sendPhoto(number, uploadfilepath,false);
								} else {
									CSChatObj.sendPhoto(number, utils.getReceivedImagesDirectory() + "/" + message,false);
								}
									break;
							case CSConstants.E_VIDEO:
								if(issender == 1){
									CSChatObj.sendVideo(number, uploadfilepath,false);
								} else {
									CSChatObj.sendVideo(number, utils.getReceivedImagesDirectory() + "/" + message,false);
								}
									break;
							case CSConstants.E_CONTACT:
									CSChatContact cschatContact = CSChatObj.getContactFromChatID(chatid);
									CSChatObj.sendContact(number, cschatContact,false);
									break;
							case CSConstants.E_DOCUMENT:
								if(issender == 1){
									CSChatObj.sendDocument(number, uploadfilepath,false);
								} else {
									CSChatObj.sendDocument(number, utils.getReceivedImagesDirectory() + "/" + message,false);
								}
									break;
							case CSConstants.E_AUDIO:
								if(issender == 1){
									CSChatObj.sendAudio(number, uploadfilepath,false);
								} else {
									CSChatObj.sendAudio(number, utils.getReceivedImagesDirectory() + "/" + message,false);
								}
									break;
						}


					}



				}


				else if (requestCode == 999 || requestCode == 221) {
					String filepath = "";
					if (data != null) {
						if (requestCode == 221) {
							Bitmap photo = (Bitmap) data.getExtras().get("data");
							Uri tempUri = getImageUri(getApplicationContext(), photo);
							filepath = utils.getRealPathFromURI(getApplicationContext(),tempUri);
						} else {
							Uri selectedImageURI = data.getData();
							filepath = utils.getRealPathFromURI(getApplicationContext(),selectedImageURI);
						}
						LOG.info("File path:" + filepath);
						LOG.info("orifinal File length:" + new File(filepath).length());
						if (filepath.equals("")) {
							Toast.makeText(NewChatActivity.this, "No Image Seected", Toast.LENGTH_SHORT).show();
						} else {

							CSChatObj.sendPhoto(destination,filepath,false);

											}
					}
				} else if (requestCode == 997) {
					String filepath = "";
					if (data != null) {

							Uri selectedImageURI = data.getData();
							filepath = utils.getRealPathFromURI(getApplicationContext(),selectedImageURI);

						LOG.info("File path:" + filepath);
						LOG.info("orifinal File length:" + new File(filepath).length());
						if (filepath.equals("")) {
							Toast.makeText(NewChatActivity.this, "No Video Selected", Toast.LENGTH_SHORT).show();
						} else {

							CSChatObj.sendVideo(destination,filepath,false);

						}
					}
				}

				else if (requestCode == 889) {
					String filepath = "";
					if (data != null) {


						//to do	CSChatObj.sendDocument(destination,data.getData(),false);


					}
				}
			} catch (Exception ex) {
				utils.logStacktrace(ex);
			}
			 }
	public void updateUI(String str) {
    	try {
			if (str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
				LOG.info("NetworkError receieved");
				//Toast.makeText(MainActivity.context, "NetworkError", Toast.LENGTH_SHORT).show();
			}
    		else if(str.equals(CSEvents.CSCHAT_CHATUPDATED)) {
    			/*chatAdapter = new ConversationAdapter(ChatActivity.this, CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID,destination), 0);
				mChatList.setFastScrollEnabled(true);
				mChatList.setAdapter(chatAdapter);*/
				LOG.info("Focus count chat updated called changing the focus");
				chatAdapter.changeCursor(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID,destination));
				////chatAdapter.notifyDataSetChanged();
/*
				if(chatAdapter.getCount() > 0){
					mChatList.setSelection(chatAdapter.getCount() - (1));
				}
*/
    		}
			else if(str.equals(CSExplicitEvents.CSChatReceiver)) {
				chatAdapter.changeCursor(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID,destination));
				//chatAdapter.notifyDataSetChanged();
				if(chatAdapter.getItemCount() > 0){
					mChatList.scrollToPosition(chatAdapter.getItemCount() - (1));
					//mChatList.setSelection(chatAdapter.getItemCount() - (1));
				}
			}
			/*
			else if(str.equals(CSEvents.getPresenceRessuccess)) {
				LOG.info("Received presence status here");
			}
			*/
	} catch(Exception ex){}
    }
	private void hideKeyboard() {
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	public class MainActivityReceiver extends BroadcastReceiver
	{
		@Override
	    public void onReceive(Context context, Intent intent)
	    {
	    	try {
	    		LOG.info("Yes Something receieved in RecentReceiver:"+intent.getAction().toString());
	    		if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
					toolbar.setSubtitle("");
	    			updateUI(CSEvents.CSCLIENT_NETWORKERROR);
	    		} else if (intent.getAction().equals(CSEvents.CSCLIENT_LOGIN_RESPONSE)) {
					if(intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
						List<String> numbers = new ArrayList<String>();
						numbers.add(destination);
						CSChatObj.getPresence(numbers);
					}
				} else if(intent.getAction().equals(CSEvents.CSCHAT_CHATUPDATED)) {
					if(destination.equals(intent.getStringExtra("destinationnumber"))) {
						updateUI(CSEvents.CSCHAT_CHATUPDATED);
					}
	    		}
				else if(intent.getAction().equals(CSExplicitEvents.CSChatReceiver)) {
					if(destination.equals(intent.getStringExtra("destinationnumber"))) {
						updateUI(CSExplicitEvents.CSChatReceiver);
					}
				}
				else if(intent.getAction().equals(CSEvents.CSCHAT_GETPRESENCE_RESPONSE)) {
					try {

						if(intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
						if (destination.equals(intent.getStringExtra("presencenumber"))) {
							String presencestatus = intent.getStringExtra("presencestatus");
							long lastseentime = intent.getLongExtra("lastseentime", 0);
							if (presencestatus.equals("ONLINE")) {
								toolbar.setSubtitle("online");
							} else {
								if (DateUtils.isToday(lastseentime)) {
									toolbar.setSubtitle("today at " + new SimpleDateFormat("hh:mm a").format(lastseentime));

									//reSetSubTitle("today at "+new SimpleDateFormat("hh:mm a").format(lastseentime));
									//toolbar.setSubtitle("today at "+new SimpleDateFormat("hh:mm a").format(lastseentime));

								} else if (utils.isYesterday(lastseentime)) {
									toolbar.setSubtitle("last seen yesterday at " + new SimpleDateFormat("hh:mm a").format(lastseentime));
								} else {
									toolbar.setSubtitle(" " + new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(lastseentime));
								}

							}
						}
					}
					} catch(Exception ex){}
				}
				else if (intent.getAction().equals(CSEvents.CSCHAT_UPLOADFILEDONE)) {
					if(isGroupChat) {
						//scale down image here and store locally with filekey as name
						//storeImage(getbitmap(intent.getStringExtra("filepath")),utils.getSentImagesDirectory()+intent.getStringExtra("filekey"));
						//CSChatObj.sendGroupChatReq(destination, intent.getStringExtra("filekey"), AppConstants.E_IMAGE);
					}
					else {
						//scale down image here and store locally with filekey as name
						//storeImage(getbitmap(intent.getStringExtra("filepath")),utils.getSentImagesDirectory()+intent.getStringExtra("filekey"));
						//CSChatObj.sendChatReq(destination, intent.getStringExtra("filekey"), AppConstants.E_IMAGE);
					}
				}
				else if (intent.getAction().equals(CSEvents.CSCHAT_UPLOADFILEFAILED)) {
				}



				else if (intent.getAction().equals(CSEvents.CSCHAT_DOWNLOADPROGRESS)) {
					long percentage = intent.getLongExtra("transferpercentage",0);
					LOG.info("Download percentage:"+percentage);
					LOG.info(" chatid from activity:" +  intent.getStringExtra("chatid"));

					//updateView(intent.getStringExtra("chatid"),"downloadprogress",(int)percentage);
				}
				else if (intent.getAction().equals(CSEvents.CSCHAT_DOWNLOADFILEDONE)) {
					chatAdapter.changeCursor(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID,destination));
					//chatAdapter.notifyDataSetChanged();

					//updateView(intent.getStringExtra("chatid"),"downloadfiledone",0);
				}

				else if (intent.getAction().equals(CSEvents.CSCHAT_DOWNLOADFILEFAILED)) {
					chatAdapter.changeCursor(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID,destination));
					//chatAdapter.notifyDataSetChanged();

//					updateView(intent.getStringExtra("chatid"),"downloadfilefailed",0);
				}
	    	} catch(Exception ex){}
	    }
	    }













	MainActivityReceiver MainActivityReceiverObj = new MainActivityReceiver();
	@Override
	public void onResume() {
		super.onResume();
		try {
			GlobalVariables.inchatactivitydestination = destination;
			MainActivityReceiverObj =  new MainActivityReceiver();
			IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
			IntentFilter filter1 = new IntentFilter(CSEvents.CSCHAT_CHATUPDATED);
			IntentFilter filter2 = new IntentFilter(CSExplicitEvents.CSChatReceiver);
			IntentFilter filter3 = new IntentFilter(CSEvents.CSCHAT_GETPRESENCE_RESPONSE);
			IntentFilter filter4 = new IntentFilter(CSEvents.CSCHAT_UPLOADFILEDONE);
			IntentFilter filter5 = new IntentFilter(CSEvents.CSCHAT_UPLOADFILEFAILED);
			IntentFilter filter6 = new IntentFilter(CSEvents.CSCHAT_DOWNLOADFILEDONE);
			IntentFilter filter7 = new IntentFilter(CSEvents.CSCHAT_DOWNLOADFILEFAILED);
			IntentFilter filter8 = new IntentFilter(CSEvents.CSCLIENT_LOGIN_RESPONSE);
			IntentFilter filter9 = new IntentFilter(CSEvents.CSCHAT_DOWNLOADPROGRESS);

			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter1);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter2);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter3);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter4);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter5);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter6);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter7);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter8);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter9);

			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter2);
			//getApplicationContext().registerReceiver(MainActivityReceiverObj,filter2);

			Cursor ccr = CSDataProvider.getChatCursorFilteredByNumberAndUnreadMessages(destination);
			focus = ccr.getCount();
			ccr.close();
			LOG.info("Focus count:"+focus);
			chatAdapter = new com.cavox.adapaters.MyRecyclerAdapter(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID,destination),getIntent().getStringExtra("Sender"));


			//mChatList.setFastScrollEnabled(true);
			mChatList.setAdapter(chatAdapter);
			mChatList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
			if(chatAdapter.getItemCount() > 0){
				mChatList.scrollToPosition(chatAdapter.getItemCount() - (1+focus));
				//mChatList.setSelection(chatAdapter.getItemCount() - (1+focus));
			}
			//String strr = CSDataProvider.getSetting(CSDbFields.KEY_SETINGS_LOGINSTATUS);
			if(CSDataProvider.getLoginstatus()) {
				List<String> numbers = new ArrayList<String>();
				numbers.add(destination);
				CSChatObj.getPresence(numbers);
			}
			//CSFileTransferUtilsObj.uploadFile("test.png", "/storage/sdcard0/VoxVallyCa/Images/Sent/test.png");
			//CSFileTransferUtilsObj.downloadFile("test.png", "/storage/sdcard0/VoxVallyCa/Images/Received/test_downloaded.png");
		} catch(Exception ex) {
			utils.logStacktrace(ex);
		}
	}
	 @Override
	 public void onPause() {
		 super.onPause();
		 try {
			 GlobalVariables.inchatactivitydestination = "";
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
			 MainActivity.context.unregisterReceiver(MainActivityReceiverObj);

	 } catch(Exception ex) {}
	        }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.exitmenu, menu);
		getMenuInflater().inflate(R.menu.chatmenu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.audiocall:
				utils.donewvoicecall(destination,NewChatActivity.this);


				return true;

			case R.id.videocall:
				utils.donewVideocall(destination,NewChatActivity.this);
				return true;

			case R.id.newchat:

				Intent intent = new Intent(MainActivity.context, ShowAppContactsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("uiaction", UIACTION_NEWCHAT);
				MainActivity.context.startActivity(intent);

onBackPressed();

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}


	    }

		 public Uri getImageUri(Context inContext, Bitmap inImage) {
			 try { ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				 inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				 String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
				 return Uri.parse(path);
			 }catch (Exception ex) {
				 return null;
			 }
		 }
		 /*
		 private String getRealPathFromURI(Uri contentURI) {
			 String result = "";
			 try {

				 Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
				 if (cursor == null) { // Source is Dropbox or other similar local file path
					 result = contentURI.getPath();
				 } else {
					 cursor.moveToFirst();
					 int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
					 result = cursor.getString(idx);
					 cursor.close();
				 } }catch (Exception ex) {
			 }
			 return result;
		 }
*/


		 /*public Bitmap getbitmap1(String path){
			 Bitmap imgthumBitmap=null;
			 try
			 {
				 final int THUMBNAIL_SIZE = 64;
				 FileInputStream fis = new FileInputStream(path);
				 imgthumBitmap = BitmapFactory.decodeStream(fis);
				 imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
						 THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
				 //ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
				 //imgthumBitmap.compress(Bitmap.CompressFormat.PNG, 100,bytearroutstream);
			 }
			 catch(Exception ex) {
				 utils.logStacktrace(ex);
			 }
			 return imgthumBitmap;
		 }
		 private void storeImage(Bitmap image,String targetpath) {
			 //String filepath = "/storage/sdcard0/VoxVallyCa/testimagecompressed.png";
			 File pictureFile = new File(targetpath);
			 if (pictureFile == null) {
				LOG.info("Error creating media file, check storage permissions: ");// e.getMessage());
				 return;
			 }
			 try {
				 FileOutputStream fos = new FileOutputStream(pictureFile);
				 image.compress(Bitmap.CompressFormat.PNG, 100, fos);
				 fos.close();
			 } catch (Exception e) {
				utils.logStacktrace(e);
			 }
		 }
		 public Bitmap getbitmap(String filepath){
			 Bitmap bitmap=null;
			 try
			 {
				 if(new File(filepath).length()<=200000) {
					 bitmap = BitmapFactory.decodeFile(filepath);
				 }
				 else if(new File(filepath).length()<=500000) {
					 bitmap = getscaleddownBitmap(filepath, 2);//giving 90kb on 306kb file
				 } else if(new File(filepath).length()<=1000000) {
					 bitmap = getscaleddownBitmap(filepath, 4);
				 } else if(new File(filepath).length()<=3000000) {
					 bitmap = getscaleddownBitmap(filepath, 6);
				 } else if(new File(filepath).length()<=6000000) {
					 bitmap = getscaleddownBitmap(filepath, 8);//giving 101kb on 5.5mb file
				 } else if(new File(filepath).length()<=10000000) {
					 bitmap = getscaleddownBitmap(filepath, 10);
				 } else if(new File(filepath).length()<=14000000) {
					 bitmap = getscaleddownBitmap(filepath, 12);
				 } else if(new File(filepath).length()<=20000000) {
					 bitmap = getscaleddownBitmap(filepath, 14);
				 }
			 }
			 catch(Exception ex) {
				 utils.logStacktrace(ex);
			 }
			 return bitmap;
		 }
		 public Bitmap getscaleddownBitmap(String filename,int SampleSize) {
			 Bitmap bitmap = null;
			 try {
				 BitmapFactory.Options options = new BitmapFactory.Options();
				 options.inJustDecodeBounds = true;
				 BitmapFactory.decodeFile(filename, options);
				 options.inJustDecodeBounds = false;
				 options.inSampleSize = SampleSize;

				 bitmap = BitmapFactory.decodeFile(filename, options);
				 LOG.info("bitmap.getByteCount():" + bitmap.getByteCount());
				 if (bitmap != null) {
					 bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
				 }
				 LOG.info("bitmap.getByteCount1():" + bitmap.getByteCount());
				 return bitmap;
			 } catch (Exception ex) {
				 return bitmap;
			 }
		 }*/
		 @Override
		 public void onBackPressed() {
			 //super.onBackPressed();
			 try {
				 //Intent intent = new Intent(UIActions.USERCHATDONE.getKey());
				 //LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent);
				 finish();
			 } catch (Exception ex) {
			 }
		 }

/*
public void donewVideocall(String numbertodial) {


			 try {

				 if (!numbertodial.equals("")&&!numbertodial.equals(GlobalVariables.phoneNumber)) {

					 Intent intent = new Intent(getApplicationContext(), PlayNewVideoCallActivity.class);
					 intent.putExtra("dstnumber", numbertodial);
					 intent.putExtra("isinitiatior", true);
					 startActivityForResult(intent, 954);
				 } else {
					 Toast.makeText(getApplicationContext(), "No valid Number", Toast.LENGTH_SHORT).show();
				 }

			 } catch (Exception ex) {
				 utils.logStacktrace(ex);
			 }
		 }



		 public void donewvoicecall(String numbertodial) {

			 try {

				 LOG.info("Doing new outgoing call");


				 if (!numbertodial.equals("")&&!numbertodial.equals(GlobalVariables.phoneNumber)) {


					 Intent intent = new Intent(getApplicationContext(), PlayNewAudioCallActivity.class);
					 intent.putExtra("dstnumber", numbertodial);
					 intent.putExtra("isinitiatior", true);
					 startActivityForResult(intent, 954);
				 } else {
					 Toast.makeText(getApplicationContext(), "No valid Number", Toast.LENGTH_SHORT).show();
				 }


			 } catch (Exception ex) {
				 utils.logStacktrace(ex);
			 }
		 }*/
		 public boolean shownwalert(String result) {
			 try {
				 AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(getApplicationContext());
				 successfullyLogin.setMessage(result);
				 successfullyLogin.setPositiveButton("Settings",
						 new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog,
												 int which) {
								 startActivity(new Intent(Settings.ACTION_SETTINGS));
							 }
						 });
				 successfullyLogin.setNegativeButton("Ok",
						 new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog,
												 int which) {

							 }
						 });

				 successfullyLogin.show();

				 return true;
			 } catch(Exception ex) {
				 return false;
			 }

		 }

		 public boolean showactivatelocationalert(String message) {
			 try {
				 AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(NewChatActivity.this);
				 successfullyLogin.setMessage(message);
				 successfullyLogin.setPositiveButton("Settings",
						 new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog,
												 int which) {
								 Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								 startActivity(intent);
							 }
						 });
				 successfullyLogin.setNegativeButton("Cancel",
						 new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog,
												 int which) {

							 }
						 });

				 successfullyLogin.show();

				 return true;
			 } catch(Exception ex) {
				 return false;
			 }

		 }


		 public boolean showfileoptions() {
			 try {


				 runOnUiThread(new Runnable() {

					 @Override
					 public void run() {

						 successfullyLogin = new androidx.appcompat.app.AlertDialog.Builder(NewChatActivity.this);

						 LayoutInflater inflater = NewChatActivity.this.getLayoutInflater();
						 View dialogView = inflater.inflate(R.layout.filetransferoptions, null);
						 successfullyLogin.setView(dialogView);
						 RelativeLayout docselect = (RelativeLayout) dialogView.findViewById(R.id.row11);
						 RelativeLayout camselect = (RelativeLayout) dialogView.findViewById(R.id.row12);
						 RelativeLayout galleryselect = (RelativeLayout) dialogView.findViewById(R.id.row13);
						 RelativeLayout audioselect = (RelativeLayout) dialogView.findViewById(R.id.row21);
						 RelativeLayout locationselect = (RelativeLayout) dialogView.findViewById(R.id.row22);
						 RelativeLayout contactselect = (RelativeLayout) dialogView.findViewById(R.id.row23);




						 docselect.setOnClickListener(new OnClickListener() {
							 @Override
							 public void onClick(View v) {
								 LOG.info("docselect");
								 showFileChooser();
								 dismisssuccessfullyLogin.cancel();
										 }
						 });
						 camselect.setOnClickListener(new OnClickListener() {
							 @Override
							 public void onClick(View v) {
								 LOG.info("camselect");
								 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								 startActivityForResult(intent, 221);
								 dismisssuccessfullyLogin.cancel();
							 }
						 });
						 galleryselect.setOnClickListener(new OnClickListener() {
							 @Override
							 public void onClick(View v) {
								 LOG.info("galleryselect");
								 showoptions();
								 dismisssuccessfullyLogin.cancel();
							 }
						 });
						 audioselect.setOnClickListener(new OnClickListener() {
							 @Override
							 public void onClick(View v) {
								 LOG.info("audioselect");
								 Intent intent = new Intent(getApplicationContext(), SoundRecorderActivity.class);
								 startActivityForResult(intent, 890);
								 dismisssuccessfullyLogin.cancel();
							 }
						 });


						 locationselect.setOnClickListener(new OnClickListener() {
							 @Override
							 public void onClick(View v) {
								 LOG.info("locationselect");
								 LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
								 boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
								 if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
									 ActivityCompat.requestPermissions(NewChatActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 102);
								 }
								 if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

									 if (!enabled) {
										 showactivatelocationalert("Please enable GPS");
									 } else {
										 Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
										 startActivityForResult(intent, 994);
									 }
								 }
								 dismisssuccessfullyLogin.cancel();
							 }
						 });
						 contactselect.setOnClickListener(new OnClickListener() {
							 @Override
							 public void onClick(View v) {
								 LOG.info("contactselect");
								 Intent contactPickerIntent = new Intent(getApplicationContext(),ShareContactsInChatActivity.class);
								 startActivityForResult(contactPickerIntent, 995);
								 dismisssuccessfullyLogin.cancel();
							 }
						 });













						 LOG.info("Yes here2");

						 dismisssuccessfullyLogin = successfullyLogin.show();
						 LOG.info("Yes here3");
					 }
				 });
				 return true;
			 } catch(Exception ex) {
				 utils.logStacktrace(ex);
				 return false;
			 }

		 }




		 private void showFileChooser() {

			 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			 intent.setType("*/*");      //all files
			 //intent.setType("text/xml");   //XML file only
			 intent.addCategory(Intent.CATEGORY_OPENABLE);

			 try {
				 startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 889);
			 } catch (android.content.ActivityNotFoundException ex) {
				 // Potentially direct the user to the Market with a Dialog
				 Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
			 }
		 }


		 public boolean showoptions() {
			 try {
				 final ArrayList<String> grpoptions = new ArrayList<>();

				 AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(NewChatActivity.this);
				 successfullyLogin.setTitle("Select");
				 successfullyLogin.setCancelable(true);
				 grpoptions.clear();

				 grpoptions.add("Images");
				 grpoptions.add("Videos");

				 SimpleImageTextAdapter1 SimpleImageTextAdapter1 = new SimpleImageTextAdapter1(getApplicationContext(), grpoptions);
				 successfullyLogin.setAdapter(SimpleImageTextAdapter1,
						 new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog,
												 int which) {

								 String finalaction = grpoptions.get(which);
								 LOG.info("finalaction:"+finalaction);

								 if(finalaction.equals("Images")) {
									 Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									 i.setType("image/*");
									 startActivityForResult(i, 999);
									 if(ad!=null) {
										 ad.cancel();
									 }
								 } else if(finalaction.equals("Videos")) {
									 Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									 i.setType("video/*");
									 startActivityForResult(i, 997);
									 if(ad!=null) {
										 ad.cancel();
									 }
								 }
							 }
						 });


				 successfullyLogin.setNegativeButton("Cancel",
						 new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog,
												 int which) {


							 }
						 });

				 ad = successfullyLogin.show();

				 return true;
			 } catch(Exception ex) {
				 utils.logStacktrace(ex);
				 return false;
			 }

		 }



		 public boolean showchatoptions(final String chatid) {
			 try {
				 final ArrayList<String> grpoptions = new ArrayList<>();

				 AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(NewChatActivity.this);
				 successfullyLogin.setTitle("Select");
				 successfullyLogin.setCancelable(true);
				 grpoptions.clear();

				 grpoptions.add("Delete");
				 grpoptions.add("forward");
				 grpoptions.add("copy");

				 String message = "";
				 int chattype = 0;


				 Cursor cur = CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_ID, chatid);
				 if (cur.getCount() > 0) {
					 cur.moveToNext();
					 message = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MESSAGE));
					 chattype = cur.getInt(cur.getColumnIndexOrThrow(CSDbFields.KEY_CHAT_MSG_TYPE));


				 }
				 cur.close();

				 final String final_message = message;
				 final int final_chattype = chattype;

				 SimpleTextAdapter simpleTextAdapter = new SimpleTextAdapter(getApplicationContext(), grpoptions);
				 successfullyLogin.setAdapter(simpleTextAdapter,
						 new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog,
												 int which) {

								 String finalaction = grpoptions.get(which);
								 LOG.info("finalaction:" + finalaction);

								 if (finalaction.equals("Delete")) {
									 CSChatObj.deleteChatMessagebyfilter(CSDbFields.KEY_CHAT_ID, chatid);
									 chatAdapter.changeCursor(CSDataProvider.getChatCursorByFilter(CSDbFields.KEY_CHAT_DESTINATION_LOGINID, destination));
									 //chatAdapter.notifyDataSetChanged();


									 String filepath = utils.getReceivedImagesDirectory() + "/" + final_message;
									 if (new File(filepath).exists()) {
										 new File(filepath).delete();
									 }

									 if (ad != null) {
										 ad.cancel();
									 }
								 } else if (finalaction.equals("forward")) {

									 Intent contactPickerIntent = new Intent(getApplicationContext(),ShowAppContactsMultiSelectActivity.class);
									 contactPickerIntent.putExtra("uiaction", AppConstants.UIACTION_FORWARDCHATMESSAGE);
									 contactPickerIntent.putExtra("chatid", chatid);

									 startActivityForResult(contactPickerIntent, 891);






									 if (ad != null) {
										 ad.cancel();
									 }
								 } else if (finalaction.equals("copy")) {

									 if(final_chattype == 0) {
										 //E_UNKNOWN_CHAT_TYPE;
									 } else if(final_chattype == 1 || final_chattype == 2) {
										 String messagee = final_message.trim();
										 ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
										 ClipData clip = ClipData.newPlainText("label", messagee);
										 clipboard.setPrimaryClip(clip);
										 //E_TEXTPLAIN;
									 } /*else if(final_chattype == 2) {
										 //E_TEXTHTML;
									 }*/ else if(final_chattype == 3) {
										 CSChatLocation location = CSChatObj.getLocationFromChatID(chatid);
										 ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
										 ClipData clip = ClipData.newPlainText("label", location.getAddress());
										 clipboard.setPrimaryClip(clip);

										 //E_LOCATION;
									 } else if(final_chattype == 4 || final_chattype == 5 || final_chattype == 7 || final_chattype == 8) {
										 String filepath = utils.getReceivedImagesDirectory()+"/"+final_message;
										 ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
										 ClipData clip = ClipData.newPlainText("label",filepath);
										 clipboard.setPrimaryClip(clip);
										 //E_IMAGE;
									 }

					/*else if(final_chattype == 5) {
						//E_VIDEO;
					}*/ else if(final_chattype == 6) {

										 CSChatContact contact = CSChatObj.getContactFromChatID(chatid);
										 String name = contact.getName();
										 List<String> numbers = contact.getNumbers();
										 List<String> labels = contact.getLabels();
										 String number = "";
										 String label = "";
										 if(numbers.size()>0) {
											 number = numbers.get(0);

										 } if(labels.size()>0) {
											 label = labels.get(0);

										 }


										 ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
										 ClipData clip = ClipData.newPlainText("label",name+" "+number+" "+label);
										 clipboard.setPrimaryClip(clip);



										 //E_CONTACT;
									 } /*else if (final_chattype == 7) {
						//E_DOCUMENT;
					} else if (final_chattype == 8) {
						//E_AUDIO;
					}*/

									 if (ad != null) {
										 ad.cancel();
									 }
								 }
							 }
						 });


				 successfullyLogin.setNegativeButton("Cancel",
						 new DialogInterface.OnClickListener() {
							 @Override
							 public void onClick(DialogInterface dialog,
												 int which) {


							 }
						 });

				 ad = successfullyLogin.show();

				 return true;
			 } catch (Exception ex) {
				 utils.logStacktrace(ex);
				 return false;
			 }


	 }

		 public void reSetSubTitle(final String message) {


				 try {
					 final Handler handler = new Handler();
					 handler.postDelayed(new Runnable() {
						 @Override
						 public void run() {
							toolbar.setSubtitle(message);
						 }
					 }, 100);

				 } catch (Exception ex) {
					 utils.logStacktrace(ex);
				 }


		 }


	 }
