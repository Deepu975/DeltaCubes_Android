package com.cavox.konverz;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.app.deltacubes.R;
import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.wrapper.CSChat;
import com.cavox.adapaters.MessageinfoAdapter;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;

import static com.cavox.utils.GlobalVariables.LOG;

public class ChatFtSettingsActivity extends AppCompatActivity
{

	CSChat CSChatObj = new CSChat();

	CheckBox checkboxwifi;
	CheckBox checkboxdata;
	CheckBox checkboxall;
	CheckBox checkboxnone;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatftdownloadsettings);

		try {

			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			toolbar.setTitle("Chat Files Download Settings");
			Switch enableautodownload = (Switch) findViewById(R.id.switch1);
			RelativeLayout relwifi = (RelativeLayout) findViewById(R.id.prefwifi);
			RelativeLayout reldata = (RelativeLayout) findViewById(R.id.prefdata);
			RelativeLayout relall = (RelativeLayout) findViewById(R.id.prefall);
			RelativeLayout relnone = (RelativeLayout) findViewById(R.id.prefnone);

			 checkboxwifi = (CheckBox) findViewById(R.id.checkBox2);
			 checkboxdata = (CheckBox) findViewById(R.id.checkBox3);
			 checkboxall = (CheckBox) findViewById(R.id.checkBox4);
			 checkboxnone = (CheckBox) findViewById(R.id.checkBox5);

			CompoundButtonCompat.setButtonTintList(checkboxwifi, ColorStateList.valueOf(getResources().getColor(R.color.theme_color)));
			CompoundButtonCompat.setButtonTintList(checkboxdata, ColorStateList.valueOf(getResources().getColor(R.color.theme_color)));
			CompoundButtonCompat.setButtonTintList(checkboxall, ColorStateList.valueOf(getResources().getColor(R.color.theme_color)));
			CompoundButtonCompat.setButtonTintList(checkboxnone, ColorStateList.valueOf(getResources().getColor(R.color.theme_color)));


			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});


			checkboxwifi.setEnabled(false);
			checkboxdata.setEnabled(false);
			checkboxall.setEnabled(false);
			checkboxnone.setEnabled(false);

					if(CSChatObj.isAutoDownloadOfFilesInChatEnabled()) {
						enableautodownload.setChecked(true);
						relwifi.setEnabled(true);
						reldata.setEnabled(true);
						relall.setEnabled(true);
						relnone.setEnabled(true);
					} else {
						enableautodownload.setChecked(false);
						relwifi.setEnabled(false);
						reldata.setEnabled(false);
						relall.setEnabled(false);
						relnone.setEnabled(false);
					}
			setCheckboxes();

			enableautodownload.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						if(CSChatObj.isAutoDownloadOfFilesInChatEnabled()) {
							if(CSChatObj.enableAutoDownloadOfFilesInChat(false)) {
								enableautodownload.setChecked(false);
								relwifi.setEnabled(false);
								reldata.setEnabled(false);
								relall.setEnabled(false);
								relnone.setEnabled(false);
							}
						} else {
							if(CSChatObj.enableAutoDownloadOfFilesInChat(true)) {
								enableautodownload.setChecked(true);
								relwifi.setEnabled(true);
								reldata.setEnabled(true);
								relall.setEnabled(true);
								relnone.setEnabled(true);
							}
						}


					} catch (Exception ex) {
						utils.logStacktrace(ex);
					}
				}
			});




			relwifi.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
CSChatObj.setPreferredNWToDownloadFilesInChat(CSConstants.CHATFTDOWNLOADPREFNW.WIFI);
						setCheckboxes();
					} catch (Exception ex) {
						utils.logStacktrace(ex);
					}
				}
			});

			reldata.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						CSChatObj.setPreferredNWToDownloadFilesInChat(CSConstants.CHATFTDOWNLOADPREFNW.DATA);
						setCheckboxes();
					} catch (Exception ex) {
						utils.logStacktrace(ex);
					}
				}
			});
			relall.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						CSChatObj.setPreferredNWToDownloadFilesInChat(CSConstants.CHATFTDOWNLOADPREFNW.ALL);
						setCheckboxes();
					} catch (Exception ex) {
						utils.logStacktrace(ex);
					}
				}
			});
			relnone.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						CSChatObj.setPreferredNWToDownloadFilesInChat(CSConstants.CHATFTDOWNLOADPREFNW.NONE);
						setCheckboxes();
					} catch (Exception ex) {
						utils.logStacktrace(ex);
					}
				}
			});















		} catch(Exception ex){
			utils.logStacktrace(ex);
		}

	}




public void  setCheckboxes() {
		try {
			CSConstants.CHATFTDOWNLOADPREFNW prefnw = CSChatObj.getPreferredNWToDownloadFilesInChat();

			if(prefnw == CSConstants.CHATFTDOWNLOADPREFNW.WIFI) {
				checkboxwifi.setChecked(true);
				checkboxdata.setChecked(false);
				checkboxall.setChecked(false);
				checkboxnone.setChecked(false);
			} else if(prefnw == CSConstants.CHATFTDOWNLOADPREFNW.DATA) {
				checkboxwifi.setChecked(false);
				checkboxdata.setChecked(true);
				checkboxall.setChecked(false);
				checkboxnone.setChecked(false);
			} else if(prefnw == CSConstants.CHATFTDOWNLOADPREFNW.ALL) {
				checkboxwifi.setChecked(false);
				checkboxdata.setChecked(false);
				checkboxall.setChecked(true);
				checkboxnone.setChecked(false);
			} else if(prefnw == CSConstants.CHATFTDOWNLOADPREFNW.NONE) {
				checkboxwifi.setChecked(false);
				checkboxdata.setChecked(false);
				checkboxall.setChecked(false);
				checkboxnone.setChecked(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
}






	@Override
	public void onResume() {
		super.onResume();


	}
	@Override
	public void onPause() {
		super.onPause();

	}





}
