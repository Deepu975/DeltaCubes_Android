package com.cavox.konverz;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.multidex.MultiDex;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.ca.dao.CSAppDetails;
import com.ca.wrapper.CSClient;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.utils;

import java.util.ArrayList;

import static com.cavox.utils.GlobalVariables.LOG;

public class GetReadPhoneStatePermission extends AppCompatActivity
{
	String managecontactnumber = "";
	String managedirection = "";

	ListView mListView;
	Toolbar toolbar;
	//FloatingActionButton fab;
	//TextView subtitle;
	CSClient CSClientObj = new CSClient();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getpermission);
		try {

			showalert();

		} catch(Exception ex){utils.logStacktrace(ex);}
	}



	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	private void checkpermissions() {

		LOG.info("checkpermissions is called");
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
			ArrayList<String> allpermissions = new ArrayList<String>();

			allpermissions.add(Manifest.permission.READ_PHONE_STATE);
			ArrayList<String> requestpermissions = new ArrayList<String>();

			for (String permission : allpermissions) {
				if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
					requestpermissions.add(permission);
				}
			}
			if (requestpermissions.size() > 0) {
				ActivityCompat.requestPermissions(GetReadPhoneStatePermission.this, requestpermissions.toArray(new String[requestpermissions.size()]), 101);
			}
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		LOG.info("onRequestPermissionsResult:"+requestCode);

		switch (requestCode) {
			case 101:
				if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
					Toast.makeText(GetReadPhoneStatePermission.this, "Read phone state permission needed", Toast.LENGTH_LONG).show();

				}
				finish();

				break;

			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}


	public boolean showalert() {
		try {


			//Testit TestitObj = new Testit();
			//TestitObj.testprint();


			AlertDialog.Builder successfullyLogin = new AlertDialog.Builder(GetReadPhoneStatePermission.this);
			successfullyLogin.setTitle("Read phone state Permission needed");
			successfullyLogin.setCancelable(false);
			successfullyLogin.setMessage("Read phone state Permission is needed in order to receive calls");

			successfullyLogin.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
											int which) {



checkpermissions();
							//CSAppDetails csAppDetails = new CSAppDetails("iamLive","did_19e01bd3_b7d6_44fd_81ac_034ef7fcf6fb","aid_8656e0f6_c982_4485_8ca7_656780b53d34");
							//CSAppDetails csAppDetails = new CSAppDetails("iamLive", "aid_8656e0f6_c982_4485_8ca7_656780b53d34");                          //for go4sip
							//CSAppDetails csAppDetails = new CSAppDetails("iamlivedbnew","iamlivedbnew","iamlivedbnew");
							CSAppDetails csAppDetails = new CSAppDetails(GlobalVariables.appname,GlobalVariables.appid);

							CSClientObj.initialize(GlobalVariables.server, GlobalVariables.port, csAppDetails);
						}
					});


			successfullyLogin.show();

			return true;
		} catch (Exception ex) {
			return false;
		}

	}
}
