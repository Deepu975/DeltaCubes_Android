package com.cavox.konverz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.ca.dao.CSContact;
import com.cavox.adapaters.CORGMultiSelectAdapter;
import com.ca.wrapper.CSClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Random;

public class AddContactActivity extends AppCompatActivity {

	private ListView mListView;
	private CORGMultiSelectAdapter addContactsAdapter;
	private Button submitContacts;
	private EditText editText;
	Toolbar toolbar;
	TextInputEditText urlobj;
	TextInputEditText urlobj1;
	EditText urlobj2;
	String username = "";
	String password = "";
	//int selected_contact_count = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcontact);


		toolbar = (Toolbar) findViewById(R.id.toolbar);


		toolbar.setTitle("Konverz");


		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setSubtitle("Add Contact");


		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});










		final TextView submitobj = (TextView) findViewById(R.id.button6);
		final TextView cancelobj = (TextView) findViewById(R.id.button7);
		final RelativeLayout mainLayoutt = (RelativeLayout) findViewById(R.id.mainlayout);

		urlobj = (TextInputEditText) findViewById(R.id.editText1);
		urlobj1 = (TextInputEditText) findViewById(R.id.editText2);

		//urlobj1.setText("123123");

		//urlobj2 = (EditText) findViewById(R.id.editText3);

		//urlobj.setText(IAmLiveDB.getsettingString(IAmLiveDbDataProvider.KEY_setings_username));
		//urlobj1.setText(IAmLiveDB.getsettingString(IAmLiveDbDataProvider.KEY_setings_password));
		//urlobj.setText("568188");
		//urlobj1.setText("715513");
		//urlobj.setText("74107410");
		//urlobj1.setText("789789");

		//initialize();



		final View kjhkj = mainLayoutt;

		mainLayoutt.setOnClickListener(new View.OnClickListener() {


			public void onClick(View v) {
				//System.out.println("SR_LOG:SUBMIT");

				try {
					if (kjhkj != null) {
						System.out.println("ok here1");
						InputMethodManager imm = (InputMethodManager) MainActivity.context.getSystemService(Context.INPUT_METHOD_SERVICE);
						System.out.println("ok here2");
						//imm.h
						imm.hideSoftInputFromWindow(kjhkj.getWindowToken(), 0);
					} else {
						System.out.println("but view is null hideKeyboard");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}


		});

		if (submitobj != null)
			submitobj.setOnClickListener(new View.OnClickListener() {


				public void onClick(View v) {
					//System.out.println("SR_LOG:SUBMIT");

					try {
						if (urlobj.getText().toString().equals("")) {
							Toast.makeText(MainActivity.context, "Contact shouldn't be empty", Toast.LENGTH_SHORT).show();
						} else {

							username = urlobj.getText().toString();
							password = urlobj1.getText().toString();
							CSClient CSClientObj = new CSClient();

							ArrayList<CSContact> CScontacts =  new ArrayList<CSContact>();

							CScontacts.add(new CSContact(password,username,2,password+username));

							boolean kjhkb = CSClientObj.addContacts(CScontacts);


							Log.i("AddContactActivity", "onClick: result of add conatct api "+kjhkb);
							if(kjhkb) {
								Toast.makeText(MainActivity.context, "Done", Toast.LENGTH_SHORT).show();
								finish();
							} else {
								Toast.makeText(MainActivity.context, "Failed", Toast.LENGTH_SHORT).show();
							}
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						Log.i("AddContactActivity", "onClick: exception occured");
						Toast.makeText(MainActivity.context, "Failed", Toast.LENGTH_SHORT).show();
					}

				}


			});

		cancelobj.setOnClickListener(new View.OnClickListener() {


			public void onClick(View v) {
				//System.out.println("SR_LOG:SUBMIT");

				try {

					onBackPressed();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}


		});




	}




	@Override
	public void onBackPressed() {
		super.onBackPressed();

		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

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
