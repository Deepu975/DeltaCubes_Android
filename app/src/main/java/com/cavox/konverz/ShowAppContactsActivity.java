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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.deltacubes.R;
import com.ca.Utils.CSDbFields;

import com.cavox.adapaters.FirstCallContactsAdapter;
import com.cavox.utils.AppConstants;
import com.cavox.utils.PreferenceProvider;
import com.cavox.utils.utils;
import com.ca.wrapper.CSDataProvider;

import java.util.ArrayList;

import static com.cavox.utils.GlobalVariables.LOG;

public class ShowAppContactsActivity extends AppCompatActivity {

    private ListView mListView;
    private FirstCallContactsAdapter addContactsAdapter;
    private EditText editText;
    Toolbar toolbar;
    //int selected_contact_count = 0;
    private ImageView mSearchCancelImg;
    public static ArrayList<String> rawnumbers = new ArrayList<String>();
    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<String> contactnames = new ArrayList<String>();
    private String TAG = "ShowAppContactsActivity";
    private String receivedFileType = "";
    private String receivedFilePath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcontacts);

        mListView = (ListView) findViewById(R.id.contact_list);

        editText = findViewById(R.id.editText);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSearchCancelImg = findViewById(R.id.chat_search_cancel_img);
        editText.setText("");

        toolbar.setTitle("DeltaCubes");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getIntExtra("uiaction", 0) == AppConstants.UIACTION_NEWCHAT) {
            toolbar.setSubtitle("Select a contact to chat");
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                receivedFileType = "text";
                receivedFilePath = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.i(TAG, "text received " + receivedFileType);
            } else if (type.startsWith("image/")) {
                receivedFileType = "image";
                receivedFilePath = intent.getParcelableExtra(Intent.EXTRA_STREAM).toString();
                Log.i(TAG, "iamge received");
            } else if (type.startsWith("audio/")) {
                Log.i(TAG, "audio received");
                receivedFileType = "audio";
                receivedFilePath = intent.getParcelableExtra(Intent.EXTRA_STREAM).toString();
            } else if (type.startsWith("video/")) {
                Log.i(TAG, "video received");
                receivedFileType = "video";
                receivedFilePath = intent.getParcelableExtra(Intent.EXTRA_STREAM).toString();
            }
        }
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rawnumbers.clear();
        labels.clear();
        contactnames.clear();


        addContactsAdapter = new FirstCallContactsAdapter(ShowAppContactsActivity.this, CSDataProvider.getAppContactsCursor(), 0);
       /* Log.i(TAG, "onCreate: cursor "+CSDataProvider.getAppContactsCursor()+" count "+CSDataProvider.getContactsCursor().getCount());
         Cursor cursor=CSDataProvider.getContactsCursor();
        while(cursor.moveToNext()){
            Log.i(TAG, "Coursor inside data: "+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_ID)));
            Log.i(TAG, "Coursor inside data: "+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME)));
            Log.i(TAG, "Coursor inside data: "+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER)));
            Log.i(TAG, "Coursor inside data: "+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_RAW_NUMBER)));
            Log.i(TAG, "Coursor inside data: "+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_IS_DIRECT_CONTACT)));
            Log.i(TAG, "Coursor inside data: "+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_TYPE)));
            Log.i(TAG, "Coursor inside data: "+cursor.getString(cursor.getColumnIndexOrThrow("isacinquired")));
            Log.i(TAG, "Coursor inside data: "+cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_IS_APP_CONTACT)));
            Log.i(TAG, "onCreate: \n");


        }*/
        mListView.setAdapter(addContactsAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {


                    Cursor cursor = null;
                    String searchstring = editText.getText().toString();
                    if (searchstring.equals("")) {
                        cursor = CSDataProvider.getAppContactsCursor();
                    } else {
                        cursor = CSDataProvider.getSearchAppContactsCursor(searchstring);
                    }

                    if (getIntent().getIntExtra("uiaction", 0) == AppConstants.UIACTION_NEWCHAT) {
                        cursor.moveToPosition(position);
                        String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER));
                        String contactName=cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
                        /*
                        Intent intent = new Intent(ShowAppContactsActivity.this, ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NUMBER, number);
                        intent.putExtra(ChatConstants.INTENT_CHAT_CONTACT_NAME, contactName);
                        if (!receivedFileType.equals("")) {
                            PreferenceProvider preferenceProvider = new PreferenceProvider(ShowAppContactsActivity.this);
                            preferenceProvider.setPrefboolean(PreferenceProvider.IS_FILE_SHARE_AVAILABLE, true);
                            intent.putExtra("isShareFileAvailable", true);
                            intent.putExtra("receivedFileType", receivedFileType);
                            intent.putExtra("receivedFilePath", receivedFilePath);
                        }
                        startActivity(intent);
                        */

                        Intent intent = new Intent(ShowAppContactsActivity.this, ChatAdvancedActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra("Sender", number);
                        intent.putExtra("IS_GROUP", false);
                        if (!receivedFileType.equals("")) {
                            PreferenceProvider preferenceProvider = new PreferenceProvider(ShowAppContactsActivity.this);
                            preferenceProvider.setPrefboolean(PreferenceProvider.IS_FILE_SHARE_AVAILABLE, true);
                            intent.putExtra("isShareFileAvailable", true);
                            intent.putExtra("receivedFileType", receivedFileType);
                            intent.putExtra("receivedFilePath", receivedFilePath);
                        }
                        startActivity(intent);
                        finish();
                    }


                    cursor.close();
                    hideKeyboard();
                } catch (Exception ex) {
                    utils.logStacktrace(ex);
                }


            }


        });

        mSearchCancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText != null && editText.getText().toString().length() > 0) {
                    editText.setText("");
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                LOG.info("Yes on touch up:" + editText.getText().toString());
                if (charSequence.length() > 0) {
                    mSearchCancelImg.setVisibility(View.VISIBLE);
                } else {
                    mSearchCancelImg.setVisibility(View.GONE);
                }
                refreshview();
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        rawnumbers.clear();
        labels.clear();
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
            if (!editText.getText().toString().equals("")) {

                addContactsAdapter.changeCursor(CSDataProvider.getSearchAppContactsCursor(editText.getText().toString()));
                addContactsAdapter.notifyDataSetChanged();

            } else {

                addContactsAdapter.changeCursor(CSDataProvider.getAppContactsCursor());
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
