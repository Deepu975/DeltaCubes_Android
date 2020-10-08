package com.cavox.konverz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.ca.Utils.CSDbFields;
import com.cavox.adapaters.CORGMultiSelectAdapter;
import com.cavox.utils.AppConstants;
import com.cavox.utils.utils;
import com.cavox.views.CustomEditText;
import com.ca.wrapper.CSDataProvider;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.cavox.utils.GlobalVariables.LOG;

public class ShowCORGMultiSelectActivity extends AppCompatActivity {

	private ListView mListView;
	private CORGMultiSelectAdapter addContactsAdapter;
	private Button submitContacts;
private EditText editText;
	Toolbar toolbar;
	//int selected_contact_count = 0;

	public static ArrayList<String> numbers = new ArrayList<String>();

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.corgmultiselect);

		mListView = (ListView) findViewById(R.id.contact_list);
		submitContacts = (Button) findViewById(R.id.button_addContacts);
		editText = (CustomEditText) findViewById(R.id.editText);

		toolbar = (Toolbar) findViewById(R.id.toolbar);

		editText.setText("");

		toolbar.setTitle("Konverz");

		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setSubtitle("Select Contacts");


		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		numbers.clear();




    addContactsAdapter = new CORGMultiSelectAdapter(ShowCORGMultiSelectActivity.this, CSDataProvider.getContactsAndGroupsCursor(), 0);
		mListView.setAdapter(addContactsAdapter);




		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {


					Cursor cursor = null;
					String searchstring = editText.getText().toString();
					if (searchstring.equals("")) {
						cursor = CSDataProvider.getContactsAndGroupsCursor();
					} else {
						cursor = CSDataProvider.getSearchContactsAndGroupsCursor(searchstring);
					}

					if (cursor != null && cursor.getCount() > 0) {


						CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
						checkBox.performClick();

						cursor.moveToPosition(position);
						String iscontactorgroup = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_IS_CONTACTORGROUP));

						if (checkBox.isChecked()) {
							String number = "";
							if (iscontactorgroup.equals(com.ca.Utils.CSConstants.GROUP)) {
								number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_ID));

								JSONObject json = new JSONObject();
								json.put("id", number);
								json.put("type", com.ca.Utils.CSConstants.GROUP);
								String message = json.toString();

								if(!numbers.contains(message)) {
									numbers.add(message);
								}
							} else {
								number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
								JSONObject json = new JSONObject();
								json.put("id", number);
								json.put("type", com.ca.Utils.CSConstants.CONTACT);
								String message = json.toString();

								if(!numbers.contains(message)) {
									numbers.add(message);
								}
							}



						} else if (!checkBox.isChecked()) {
							String number = "";
							if (iscontactorgroup.equals(com.ca.Utils.CSConstants.GROUP)) {
								number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_ID));

								JSONObject json = new JSONObject();
								json.put("id", number);
								json.put("type", com.ca.Utils.CSConstants.GROUP);
								String message = json.toString();



								numbers.remove(message);

							} else {
								number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));

								JSONObject json = new JSONObject();
								json.put("id", number);
								json.put("type", com.ca.Utils.CSConstants.CONTACT);
								String message = json.toString();


								numbers.remove(message);
							}


						}


						cursor.close();
					}


					toolbar.setSubtitle(String.valueOf(numbers.size()) + " Contacts Selected");
					hideKeyboard();
				} catch (Exception ex) {
					utils.logStacktrace(ex);
				}


			}
















		});


		submitContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(numbers.size()<=0) {
					Toast.makeText(getApplicationContext(), "Select at least one contact", Toast.LENGTH_SHORT).show();
				} else {
					sendAddContactsRequest();
				}
			}
		});


		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				LOG.info("Yes on touch up:"+editText.getText().toString());
				refreshview();
			}

			@Override
			public void afterTextChanged(Editable editable) {


			}
		});







	}

    public void sendAddContactsRequest()
	{



		if(numbers.size()<=0) {
			Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
		} else {


			submitContacts.setEnabled(false);

			//labels = getContactLabelDetails(numbers);



            Intent resultIntent = new Intent();
            resultIntent.putExtra("contactnumbers",numbers);
			if(getIntent().getIntExtra("uiaction",0) == AppConstants.UIACTION_FORWARDCHATMESSAGE) {
				resultIntent.putExtra("chatid",getIntent().getStringExtra("chatid"));
			}
            setResult(Activity.RESULT_OK, resultIntent);

            //numbers.clear();
            //labels.clear();
			//contactnames.clear();
            finish();

		}






	}




	@Override
	public void onBackPressed() {
		super.onBackPressed();
        numbers.clear();
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
	public void refreshview() {

		try {
			if(!editText.getText().toString().equals("")) {

				addContactsAdapter.changeCursor(CSDataProvider.getSearchContactsAndGroupsCursor(editText.getText().toString()));
				addContactsAdapter.notifyDataSetChanged();

			} else {

				addContactsAdapter.changeCursor(CSDataProvider.getContactsAndGroupsCursor());
				addContactsAdapter.notifyDataSetChanged();

			}

		} catch (Exception ex) {
			utils.logStacktrace(ex);
		}
	}
	private void hideKeyboard() {
		try {
			View view = this.getCurrentFocus();
			if (view != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		} catch (Exception ex) {

		}
	}

}
