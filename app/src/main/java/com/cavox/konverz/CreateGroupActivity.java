package com.cavox.konverz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.ca.Utils.CSEvents;
import com.ca.Utils.CSConstants;

import com.cavox.adapaters.SimpleImageTextAdapter;
import com.cavox.utils.AppConstants;
import com.cavox.utils.utils;
import com.cavox.views.RoundedImageView;
import com.ca.wrapper.CSGroups;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cavox.utils.GlobalVariables.LOG;

//import com.cavox.uiutils.UIActions;


public class CreateGroupActivity extends Activity {
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	Handler h = new Handler();
	int delay = 20000;
	Runnable RunnableObj;

	CSGroups CSGroupsObj = new CSGroups();
	private Button button_nex;
	private Button selectcontacts;
	private EditText group_name;
	private EditText status_text;
	private TextView contact_count;
	private RoundedImageView groupImage;
	String filepath = "";
	private static String mGroupId;
	String imageid = "";
	CSGroups IAmLiveCoreSendObj = new CSGroups();
	AlertDialog ad;
	public static List<String> groupnumbers = new ArrayList<String>();
	private String imageFilePath="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creategroup);
		try {
		groupImage = (RoundedImageView) findViewById(R.id.user_image);
		button_nex = (Button) findViewById(R.id.button_done);
		group_name = (EditText) findViewById(R.id.user_name);
		status_text = (EditText) findViewById(R.id.user_presenceText);
		contact_count = (TextView) findViewById(R.id.textView9);
		//mGroupId = getIntent().getStringExtra("grpid");
			selectcontacts = (Button) findViewById(R.id.selectcontacts);

			groupnumbers.clear();

		button_nex.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {

				if (group_name.getText().toString().isEmpty()) {
					group_name.setError(getResources().getString(R.string.error_empty_group));
					return;
				}
				String name = group_name.getText().toString();
				String presence = status_text.getText().toString();
				CSGroupsObj.createGroup(name, presence, filepath);
				showprogressbar();

			}
		});
			selectcontacts.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					Intent intentt = new Intent(MainActivity.context, ShowAppContactsMultiSelectActivity.class);
					intentt.putExtra("uiaction", AppConstants.UIACTION_ADDCONTACTSTOGROUP);
					startActivityForResult(intentt,891);
				}
			});
/*
		groupImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				i.setType("image/*");
				startActivityForResult(i, 999);
			}
		});
			*/
			groupImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//popupusertoselectimagesource();
					showoptions();
				}
			});
	} catch(Exception ex) {}
	}

	public boolean showoptions() {
		try {
			final ArrayList<String> grpoptions = new ArrayList<>();

			AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(CreateGroupActivity.this);
			successfullyLogin.setTitle("Options");
			successfullyLogin.setCancelable(true);
			grpoptions.clear();

			grpoptions.add("Camera");
			grpoptions.add("Gallery");




			SimpleImageTextAdapter simpleImageTextAdapter = new SimpleImageTextAdapter(MainActivity.context, grpoptions);
			successfullyLogin.setAdapter(simpleImageTextAdapter,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
											int which) {

							String finalaction = grpoptions.get(which);
							LOG.info("finalaction:"+finalaction);

							if(finalaction.equals("Camera")) {
								if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
									Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
									//intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());

									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
										intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", createImageFile()));
									} else {
										intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
									}
									intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
									startActivityForResult(intent, 221);
								} else {
									if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
										ArrayList<String> allpermissions = new ArrayList<String>();
										allpermissions.add(android.Manifest.permission.CAMERA);
										allpermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
										ArrayList<String> requestpermissions = new ArrayList<String>();

										for (String permission : allpermissions) {
											if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
												requestpermissions.add(permission);
											}
										}
										if (requestpermissions.size() > 0) {
											ActivityCompat.requestPermissions(CreateGroupActivity.this, requestpermissions.toArray(new String[requestpermissions.size()]), 101);
										}
									}

								}

								if(ad!=null) {
									ad.cancel();
								}
							} else if(finalaction.equals("Gallery")) {
								Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								i.setType("image/*");
								startActivityForResult(i, 999);
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

	private File createImageFile() {
		//LOG.info("test place2");
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		//File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File storageDir = new File(utils.getSentImagesDirectory());

		File image = null;
		try {
			image = File.createTempFile(
					imageFileName,  /* prefix */
					".jpg",         /* suffix */
					storageDir      /* directory */
			);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// Save a file: path for use with ACTION_VIEW intents
		//LOG.info("test place3");
		try {
			imageFilePath = image.getAbsolutePath();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//LOG.info("test place4");
		if (image == null) {
			LOG.info("image file is null");
		} else {
			LOG.info("image file is  not null path is:" + imageFilePath);
		}
		return image;
	}
	public boolean popupusertoselectimagesource() {
		try {

			final AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(CreateGroupActivity.this);
			final Button Camera = new Button(CreateGroupActivity.this);
			final Button Gallery = new Button(CreateGroupActivity.this);
			//Camera.setBackgroundColor(MainActivity.context.getResources().getColor(R.color.colorPrimary));
			//Camera.setTextColor(MainActivity.context.getResources().getColor(R.color.color_white));
			Camera.setText("Camera");
			//Gallery.setBackgroundColor(MainActivity.context.getResources().getColor(R.color.colorPrimary));
			//Gallery.setTextColor(MainActivity.context.getResources().getColor(R.color.color_white));
			Gallery.setText("Gallery");

			LinearLayout layout = new LinearLayout(CreateGroupActivity.this);
			layout.setOrientation(LinearLayout.VERTICAL);

			//final EditText titleBox = new EditText(UserProfileActivity.this);

			layout.addView(Camera);
			layout.addView(Gallery);

			successfullyLogin.setView(layout);


			successfullyLogin.setTitle("Pls select");





			Camera.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					//intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
					startActivityForResult(intent, 221);
					if(ad!=null) {
						ad.cancel();
					}
				}
			});

			Gallery.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					i.setType("image/*");
					startActivityForResult(i, 999);
					if(ad!=null) {
						ad.cancel();
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
			return false;
		}

	}




	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (requestCode == 999 || requestCode == 221) {

					if(requestCode == 221) {
						filepath = new String(imageFilePath);
						imageFilePath = "";
					} else {
						Uri selectedImageURI = data.getData();
						filepath = utils.getRealPathFromURI(getApplicationContext(),selectedImageURI);
					}
					LOG.info("File path:" + filepath);
					LOG.info("orifinal File length:" + new File(filepath).length());
					if(filepath.equals("")) {
						Toast.makeText(CreateGroupActivity.this, "No Image Set", Toast.LENGTH_SHORT).show();
					} else {
						if (new File(filepath).length() > 10000000) {
							filepath = "";
							Toast.makeText(CreateGroupActivity.this, "File Size limit excedded", Toast.LENGTH_SHORT).show();
						} else {
							Bitmap bitmap = BitmapFactory.decodeFile(filepath);
							try {

								bitmap = modifyOrientation(bitmap, filepath);
								bitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1280, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (bitmap != null) {
								groupImage.setImageBitmap(bitmap);
							}
						}
					}

			} else if (requestCode == 891 && resultCode == Activity.RESULT_OK && data != null) {

				groupnumbers = data.getStringArrayListExtra("contactnumbers");

				LOG.info("Yes in on activity:"+ groupnumbers.size());
				contact_count.setText(groupnumbers.size() + " Contacts Selected");
				}
		}catch (Exception ex) {
utils.logStacktrace(ex);
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
		try { 	Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	public void updateUI(String str) {
		
    	try {
		if(str.equals(CSEvents.CSCLIENT_NETWORKERROR)) {
			LOG.info("NetworkError receieved");
			dismissprogressbar();
			//Toast.makeText(this, "NetworkError", Toast.LENGTH_SHORT).show();
		}
		else if(str.equals("Failed")) {
			LOG.info("creategroupfailure");
			dismissprogressbar();
			Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
		}



	} catch(Exception ex){}
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		groupnumbers.clear();
		//Intent intent1 = new Intent(UIActions.CREATEGRPDONE.getKey());
		//LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(intent1);
		finish();
		return;
	}
	public class MainActivityReceiver extends BroadcastReceiver
	{

		@Override
	    public void onReceive(Context context, Intent intent)
	    {
	    	try {
	    		
	    		LOG.info("Yes Something receieved in RecentReceiver:"+intent.getAction().toString());
	    		if (intent.getAction().equals(CSEvents.CSCLIENT_NETWORKERROR)) {
	    			updateUI(CSEvents.CSCLIENT_NETWORKERROR);
	    		} else if(intent.getAction().equals(CSEvents.CSGROUPS_CREATEGROUP_RESPONSE)) {

	    			if(intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {

						//updateUI(CSEvents.creategroupsuccess");

						LOG.info("creategroupsuccess");
						mGroupId = intent.getStringExtra("groupid");
						LOG.info("mGroupId:" + mGroupId);
						if (!groupnumbers.isEmpty()) {

							CSGroupsObj.addMembersToGroup(mGroupId, groupnumbers);

						} else {
							LOG.info("NO ADD CONTACTS TO GROUP IS CALLED");
							onBackPressed();
						}
					} else {
						updateUI("Failed");
					}
				}
				 else if (intent.getAction().equals(CSEvents.CSGROUPS_ADDMEMBERS_TOGROUP_RESPONSE)) {
					if(intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
						dismissprogressbar();
						onBackPressed();
					} else {
						updateUI("Failed");
					}
				}
				 else if (intent.getAction().equals(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE)) {
					//updateUI(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE");
					if(intent.getStringExtra(CSConstants.RESULT).equals(CSConstants.RESULT_SUCCESS)) {
					} else {
						updateUI("Failed");
					}
				}
	    	
	    	} catch(Exception ex){}
	    }
	    }
	MainActivityReceiver MainActivityReceiverObj = new MainActivityReceiver();
	@Override
	public void onResume() {
		super.onResume();
		
		try{
			MainActivityReceiverObj =  new MainActivityReceiver();
			IntentFilter filter = new IntentFilter(CSEvents.CSCLIENT_NETWORKERROR);
			IntentFilter filter1 = new IntentFilter(CSEvents.CSGROUPS_CREATEGROUP_RESPONSE);
			IntentFilter filter3 = new IntentFilter(CSEvents.CSGROUPS_ADDMEMBERS_TOGROUP_RESPONSE);
			//IntentFilter filter4 = new IntentFilter(CSEvents.addcontactstogroupfailure);
			IntentFilter filter5 = new IntentFilter(CSEvents.CSGROUPS_PULLGROUPDETAILS_RESPONSE);
			//IntentFilter filter6 = new IntentFilter(CSEvents.pullGroupDetailsResfailure);

			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter1);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter3);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(MainActivityReceiverObj,filter5);




		} catch(Exception ex) {}
		
	}
	 @Override    
	 public void onPause() {
		 super.onPause();
		 
		 try {
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(MainActivityReceiverObj);
	 } catch(Exception ex) {}
			
		
	        }

	public void showprogressbar() {
		try {
			if(MainActivity.context!=null) {
				progressBarStatus = 0;
				progressBar = new ProgressDialog(CreateGroupActivity.this);
				progressBar.setCancelable(false);
				progressBar.setMessage("Please Wait..");
				progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressBar.setProgress(0);
				//progressBar.setMax(time);
				progressBar.show();

				h = new Handler();
				RunnableObj = new Runnable(){

					public void run(){
						h.postDelayed(this, delay);

						runOnUiThread(new Runnable() {
							public void run() {
								dismissprogressbar();
								//Toast.makeText(MainActivity.context, "NetworkError", Toast.LENGTH_SHORT).show();


							}
						});
					}
				};
				h.postDelayed(RunnableObj, delay);


			}
		} catch (Exception ex) {
			dismissprogressbar();
		}
	}

	public void dismissprogressbar() {
		try {
			LOG.info("dismissprogressbar2");
			if(progressBar!=null) {
				progressBar.dismiss();

			}
			if(h!=null) {
				h.removeCallbacks(RunnableObj);
			}
		} catch(Exception ex){}
	}

	/**
	 * This is used for change orientation of image
	 *
	 * @param bitmap              change orientation
	 * @param image_absolute_path path
	 * @return which returns bitmap image
	 * @throws IOException exception
	 */
	public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws
			IOException {
		ExifInterface ei = new ExifInterface(image_absolute_path);
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return rotate(bitmap, 90);

			case ExifInterface.ORIENTATION_ROTATE_180:
				return rotate(bitmap, 180);

			case ExifInterface.ORIENTATION_ROTATE_270:
				return rotate(bitmap, 270);

			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				return flip(bitmap, true, false);

			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				return flip(bitmap, false, true);

			default:
				return bitmap;
		}
	}

	/**
	 * This is used for rotate image whatever you want
	 *
	 * @param bitmap  this is used for rotate image
	 * @param degrees this is used for rotate image
	 * @return which returns bitmap iamge
	 */
	static Bitmap rotate(Bitmap bitmap, float degrees) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * This is used for flip image
	 *
	 * @param bitmap     flip image
	 * @param horizontal this is used for flip image
	 * @param vertical   this is used for flip image
	 * @return which returns bitmap
	 */
	static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
		Matrix matrix = new Matrix();
		matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

}
