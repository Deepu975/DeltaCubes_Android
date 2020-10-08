package com.cavox.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.Utils.CSEvents;
import com.ca.Utils.CSExplicitEvents;
import com.ca.wrapper.CSChat;
import com.ca.wrapper.CSDataProvider;
import com.cavox.adapaters.FirstCallChatsAdapter;
import com.app.deltacubes.R;
import com.cavox.utils.utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Time;

import static com.cavox.utils.AppConstants.UIACTION_NEWCHAT;
import static com.cavox.utils.GlobalVariables.LOG;

public class FirstCallDialPad extends Fragment {

    //ListView mListView;

    public  static EditText editText;

    private ImageView mSearchCancelImg;
    RecyclerView rv;
    public static int contactstypetoload = 0;//0- normal contacts 1 for app contacts
    static FirstCallChatsAdapter appContactsAdapter;
    static FragmentActivity mActivity;
    static TextView mytextview;
    FloatingActionButton newchat;
   private String TAG="FirstCallDialPad";
    public FirstCallDialPad() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.firstcall_numpad, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LOG.info("onViewCreated:");




       
            TextView zero = (TextView) view.findViewById(R.id.imageButton_dialpad_0);
            TextView one = (TextView) view.findViewById(R.id.imageButton_dialpad_1);
            TextView two = (TextView) view.findViewById(R.id.imageButton_dialpad_2);
            TextView three = (TextView) view.findViewById(R.id.imageButton_dialpad_3);
            TextView four = (TextView) view.findViewById(R.id.imageButton_dialpad_4);
            TextView five = (TextView) view.findViewById(R.id.imageButton_dialpad_5);
            TextView six = (TextView) view.findViewById(R.id.imageButton_dialpad_6);
            TextView seven = (TextView) view.findViewById(R.id.imageButton_dialpad_7);
            TextView eight = (TextView) view.findViewById(R.id.imageButton_dialpad_8);
            TextView nine = (TextView) view.findViewById(R.id.imageButton_dialpad_9);

            TextView plus = (TextView) view.findViewById(R.id.imageButton_dialpad_plus);
            TextView hash = (TextView) view.findViewById(R.id.imageButton_dialpad_hash);


            final EditText edittext = (EditText) view.findViewById(R.id.editText1);
            ImageView backarrow = (ImageView) view.findViewById(R.id.imageView5);

            ImageView startcall = view.findViewById(R.id.button6);

        if (Build.VERSION.SDK_INT >= 21) {
            edittext.setShowSoftInputOnFocus(false);
        } else {
            edittext.setTextIsSelectable(true);
        }
        edittext.requestFocus();

        startcall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str;
                    if (!edittext.getText().toString().equals("")) {
                        com.cavox.utils.utils.donewPstncall(edittext.getText().toString(), mActivity);
                        edittext.setText("");
                        return;
                    }
                    Cursor c2 = CSDataProvider.getCallLogCursor();
                    if (c2.getCount() > 0) {
                        c2.moveToFirst();
                        str = c2.getString(c2.getColumnIndexOrThrow(CSDbFields.KEY_CALLLOG_NUMBER));
                    } else {
                        str = "";
                    }
                    c2.close();
                    if (str.equals("")) {
                        Toast.makeText(mActivity, "Enter a valid number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    edittext.setText(str);
                    edittext.setSelection(edittext.getText().toString().length());
                }
            });



            backarrow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try
                    {
                        edittext.setText("");

                    }catch (Exception ex) {
                        utils.logStacktrace(ex);
                    }

                    return true;
                }
            });
            backarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String obj = edittext.getText().toString();
                        int selectionStart = edittext.getSelectionStart() - 1;
                        edittext.setText(new StringBuffer(obj).deleteCharAt(selectionStart));
                        edittext.setSelection(selectionStart);
                    } catch (Exception ex) {}
                }
            });
        zero.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try
                {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "+"));
                    edittext.setSelection(selectionStart + 1);
                }catch (Exception ex) {
                    utils.logStacktrace(ex);
                }

                return true;
            }
        });
            zero.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "0"));
                    edittext.setSelection(selectionStart + 1);
                }
            });

            one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "1"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "2"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "3"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            four.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "4"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "5"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            six.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "6"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            seven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "7"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            eight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "8"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            nine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "9"));
                    edittext.setSelection(selectionStart + 1);
                }
            });
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "*"));
                    edittext.setSelection(selectionStart + 1);
                    //str = str+"*";
                }
            });
            hash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String obj = edittext.getText().toString();
                    int selectionStart = edittext.getSelectionStart();
                    edittext.setText(new StringBuffer(obj).insert(selectionStart, "#"));
                    edittext.setSelection(selectionStart + 1);
                    //str = str+"#";
                }
            });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Code here
        //LOG.info("On attach called9");

        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }


    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Code here
            //LOG.info("On attach called3");

            mActivity = (FragmentActivity) activity;

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
