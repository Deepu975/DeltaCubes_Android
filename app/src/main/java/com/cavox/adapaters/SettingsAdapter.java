package com.cavox.adapaters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.dao.CSAppDetails;
import com.ca.wrapper.CSClient;
import com.app.deltacubes.R;
import com.cavox.utils.GlobalVariables;
import com.cavox.utils.PreferenceProvider;
import com.cavox.utils.utils;
import com.cavox.views.CustomTextView;
import com.ca.wrapper.CSDataProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.cavox.utils.GlobalVariables.LOG;


public class SettingsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    ArrayList<String> settings = new ArrayList<>();
    PreferenceProvider pf;

    public SettingsAdapter(Context context, ArrayList<String> c, int flags) {

        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.settings = c;
         pf = new PreferenceProvider(context);

    }


    public int getCount() {
        return settings.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final ViewHolder holder;

            convertView = mInflater.inflate(R.layout.settings_row_layout, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.imageview);
            holder.text = (CustomTextView) convertView.findViewById(R.id.text1);
            holder.vertionTv = convertView.findViewById(R.id.version_tv);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox2);

            if (position == 0) {
                holder.image.setImageResource(R.drawable.ic_change_password);
            } else if (position == 1) {
                holder.image.setImageResource(R.drawable.ic_record_all_calls);
                holder.checkbox.setVisibility(View.VISIBLE);
                boolean record =  pf.getPrefBoolean("recordall");
                if(record) {
                    holder.checkbox.setChecked(true);
                    GlobalVariables.callrecord = CSConstants.CALLRECORD.RECORD;
                } else {
                    holder.checkbox.setChecked(false);
                    GlobalVariables.callrecord = CSConstants.CALLRECORD.DONTRECORD;
                }

            } else if (position == 2) {
                holder.image.setImageResource(R.drawable.ic_chat_files_download_settings);
            }
            else if (position == 3) {
                holder.image.setImageResource(R.drawable.ic_about);
            }
            else if (position == 4) {
                holder.image.setImageResource(R.drawable.ic_upload_icon);
            } else if (position == 5) {
                holder.image.setImageResource(R.drawable.ic_send_recent_logs);
            } else if (position == 6) {
                holder.image.setImageResource(R.drawable.ic_start_logging);
                holder.checkbox.setVisibility(View.VISIBLE);
                boolean read =  new CSClient().getPublicReadOfProfileandChatFilesStatus();

                LOG.info("read permission:"+read);
                if(read) {
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }

            } else if (position == 7) {
                holder.image.setImageResource(R.drawable.ic_make_all_files_public);
                holder.checkbox.setVisibility(View.VISIBLE);
                boolean read =  new CSClient().isGroupActivityinChatEnabled();

                LOG.info("isGroupActivityinChatEnabled:"+read);
                if(read) {
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }

            } else if (position == 8) {
                holder.image.setImageResource(R.drawable.ic_make_all_files_public);
                holder.checkbox.setVisibility(View.VISIBLE);
                boolean read =  new CSClient().iscallLogActivityinChatEnabled();

                LOG.info("iscallLogActivityinChatEnabled:"+read);
                if(read) {
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }

            } else if (position == 9) {
                holder.image.setImageResource(R.drawable.ic_make_all_files_public);
                holder.checkbox.setVisibility(View.VISIBLE);
                boolean read =  new CSClient().isUserActivityinChatEnabled();

                LOG.info("isUserActivityinChatEnabled:"+read);
                if(read) {
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }

            } else if (position == 10) {
                holder.image.setImageResource(R.drawable.ic_log_out);
            }

            holder.text.setText(settings.get(position));


            if (position == 3) {
                String version = "Konverz " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                holder.vertionTv.setVisibility(View.VISIBLE);
                holder.vertionTv.setText(version);
            }



            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
if(position == 1) {
    LOG.info("read permission call recording");
    if (holder.checkbox.isChecked()) {
        GlobalVariables.callrecord = CSConstants.CALLRECORD.RECORD;
        pf.setPrefboolean("recordall", true);
    } else {
        GlobalVariables.callrecord = CSConstants.CALLRECORD.DONTRECORD;
        pf.setPrefboolean("recordall", false);
    }
} else if(position == 6) {

    if (holder.checkbox.isChecked()) {
        LOG.info("read permission setting to true");
        new CSClient().enablePublicReadOfProfileandChatFiles(true);
    } else {
        LOG.info("read permission setting to false");
        new CSClient().enablePublicReadOfProfileandChatFiles(false);
    }
} else if(position == 7) {

    if (holder.checkbox.isChecked()) {
        LOG.info("enableGroupActivityinChat setting to true");
        new CSClient().enableGroupActivityinChat(true);
    } else {
        LOG.info("enableGroupActivityinChat setting to false");
        new CSClient().enableGroupActivityinChat(false);
    }
} else if(position == 8) {

    if (holder.checkbox.isChecked()) {
        LOG.info("enablecallLogActivityinChat setting to true");
        new CSClient().enablecallLogActivityinChat(true);
    } else {
        LOG.info("enablecallLogActivityinChat setting to false");
        new CSClient().enablecallLogActivityinChat(false);
    }
} else if(position == 9) {

    if (holder.checkbox.isChecked()) {
        LOG.info("enableUserActivityinChat setting to true");
        new CSClient().enableUserActivityinChat(true);
    } else {
        LOG.info("enableUserActivityinChat setting to false");
        new CSClient().enableUserActivityinChat(false);
    }
}

                         } catch (Exception ex) {
                        utils.logStacktrace(ex);
                    }
                }


            });



            convertView.setTag(holder);

        } catch (Exception ex) {

        }

        return convertView;
    }


    static class ViewHolder {
        ImageView image;
        CustomTextView text, vertionTv;
        CheckBox checkbox;
    }



}