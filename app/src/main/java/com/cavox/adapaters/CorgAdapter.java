package com.cavox.adapaters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.ca.Utils.CSConstants;
import com.ca.Utils.CSDbFields;
import com.ca.wrapper.CSDataProvider;
import com.cavox.fragments.FirstCallContacts;
import com.app.deltacubes.R;
import com.cavox.utils.utils;

import java.io.File;

public class CorgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Cursor cursor;
String TAG = CorgAdapter.class.getSimpleName();
    public CorgAdapter(Context context, Cursor c) {
        this.context = context;
        this.cursor = c;
    }
    public void swapCursorAndNotifyDataSetChanged(Cursor newcursor) {
        try {

            Cursor oldCursor = cursor;
            try {
                if (cursor == newcursor) {
                    return;// null;
                }
                this.cursor = newcursor;
                if (this.cursor != null) {
                    this.notifyDataSetChanged();
                }
            } catch (Exception ex) {
                utils.logStacktrace(ex);
            }

            if (oldCursor != null) {
                oldCursor.close();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        //Log.i(TAG,"cursor Count:" + cursor.getCount());
        return (cursor == null) ? 0 : cursor.getCount();
    }
    @Override
    public int getItemViewType(int position) {

        //cursor.moveToPosition(position);
        int returnvalue = 0;
        return returnvalue;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener {

        TextView title;
        TextView secondary;
        ImageView image;


        public MyViewHolder(View view) {
            super(view);
             title = (TextView) view.findViewById(R.id.text1);
             secondary = (TextView) view.findViewById(R.id.text2);
             image = (ImageView) view.findViewById(R.id.imageView6);

             view.setOnClickListener(this);
             view.setOnLongClickListener(this);
            image.setOnClickListener(this);
            image.setOnLongClickListener(this);


            //Log.i(TAG,"MyViewHolder0 is called");
        }
        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();

            if (v.getId() == image.getId()){
                processClick(0,adapterPosition,0);
            } else {
                processClick(0,adapterPosition,1);
            }


        }

        @Override
        public boolean onLongClick(View v) {
 /*
            int adapterPosition = getAdapterPosition();
            if (v.getId() == address.getId()){
                processClick(0,adapterPosition,0);
            } else if (v.getId() == pincode.getId()) {
                processClick(0,adapterPosition,0);
            }
            */
            return true;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View defaultitemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_layout3_new, parent, false);

        switch (viewType) {
                case 0:
                    View itemView0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_layout3, parent, false);
                    return new MyViewHolder(itemView0);

            }


        return new MyViewHolder(defaultitemView);



    }




    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewholder, final int position) {
        try {
            viewholder.itemView.setTag(viewholder);
            cursor.moveToPosition(position);
            MyViewHolder myViewHolder = (MyViewHolder) viewholder;
            switch (viewholder.getItemViewType()) {
                case 0:
                    myViewHolder = (MyViewHolder) viewholder;
                    break;
            }

            {



                String id = "";
                String name = "";


                if (FirstCallContacts.contactstypetoload == 0) {
                    id = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACT_ID));
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NAME));
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACT_NUMBER));
                    myViewHolder.title.setText(name);
                    myViewHolder.secondary.setText(number);
                    // image.setEnabled(false);

                    Cursor cursor1=CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER,number);
                    if (cursor1.moveToNext()){
                        id=cursor1.getString(cursor1.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
                        //new ImageDownloaderTask(image).execute("app", id);
                        String filepath = CSDataProvider.getImageFilePath(id);
                        //if(new File(filepath).exists()) {
                            Glide.with(context)
                                    .load(Uri.fromFile(new File(filepath)))
                                    .apply(new RequestOptions().error(R.drawable.defaultcontact))
                                    .apply(RequestOptions.circleCropTransform())
                                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(filepath).length()+new File(filepath).lastModified()))))
                                    .into(myViewHolder.image);
                        //}
                        //image.setEnabled(false);
                    }else {
                        //image.setEnabled(true);
                        //new ImageDownloaderTask(image).execute("native", id);

                        Uri uri = null;// = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
try {
    uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
} catch (Exception ex) {
    ex.printStackTrace();
}
                        //LOG.info("corg filepath native uri:"+uri);
                        Glide.with(context)
                                .load(uri)
                                .apply(new RequestOptions().error(R.drawable.defaultcontact))
                                .apply(RequestOptions.circleCropTransform())
                                //.apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(filepath).length()+new File(filepath).lastModified()))))
                                .into(myViewHolder.image);


                    }

                    /*image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Konverz");
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Download Konverz from https://play.google.com/store/apps/details?id=com.app.deltacubes");
                            context.startActivity(sharingIntent);
                        }
                    });*/
                } else {
                    // image.setEnabled(true);
                    id = cursor.getString(cursor.getColumnIndex(CSDbFields.KEY_CONTACTORGROUP_ID));
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_NAME));
                    String iscontactorgroup = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_IS_CONTACTORGROUP));

                    if (iscontactorgroup.equals(CSConstants.GROUP)) {

                        String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
                        String picid = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_PICID));

                        myViewHolder.title.setText(name);
                        myViewHolder.secondary.setText(number);

                        String filepath = CSDataProvider.getImageFilePath(picid);
                        //if(new File(filepath).exists()) {
                            Glide.with(context)
                                    .load(Uri.fromFile(new File(filepath)))
                                    .apply(new RequestOptions().error(R.drawable.defaultgroup))
                                    .apply(RequestOptions.circleCropTransform())
                                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(new File(filepath).length()+new File(filepath).lastModified()))))
                                    .into(myViewHolder.image);
                        //}

                        //com.mikhaellopez.circularimageview.CircularImageView image = (com.mikhaellopez.circularimageview.CircularImageView) convertView.findViewById(R.id.imageView6);
                        //new ImageDownloaderTask(image).execute("group", picid, id);
                        final String mygrpid = id;
                        /*image.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context, ManageGroupActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("grpid", mygrpid);
                                context.startActivity(intent);

                            }
                        });
*/
                    } else {
                        final String number = cursor.getString(cursor.getColumnIndexOrThrow(CSDbFields.KEY_CONTACTORGROUP_DESC));
                        myViewHolder.title.setText(name);
                        //String description = "Hey there! I am using Konverz";
                        //Log.i("CorgAdapter"," conatt name "+name+" number "+number);
                        String description = "";
                        String picid = "";
                        //com.mikhaellopez.circularimageview.CircularImageView image = (com.mikhaellopez.circularimageview.CircularImageView) convertView.findViewById(R.id.imageView6);
                        Cursor cur = CSDataProvider.getProfileCursorByFilter(CSDbFields.KEY_PROFILE_MOBILENUMBER, number);
                        if (cur.getCount() > 0) {
                            cur.moveToNext();
                            picid = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_PROFILEPICID));
                            description = cur.getString(cur.getColumnIndexOrThrow(CSDbFields.KEY_PROFILE_DESCRIPTION));
                            String filepath = CSDataProvider.getImageFilePath(picid);
                            //LOG.info("corg filepath:"+filepath);
                            //if(new File(filepath).exists()) {
                                Glide.with(context)
                                        .load(Uri.fromFile(new File(filepath)))
                                        .apply(new RequestOptions().error(R.drawable.defaultcontact))
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(myViewHolder.image);
                            //}

                            //if (picid != null && !picid.equals("")) {
                            //LOG.info("TEST PIC number:"+number);
                            //LOG.info("TEST PIC name:"+name);
                            //LOG.info("TEST PIC ID:"+picid);
                            //new ImageDownloaderTask(image).execute("app", picid, id);
                            //}
                        }

                        cur.close();
                        if (description.equals("")) {
                            //description = "Hey there! I am using Konverz";
                            description = "";
                        }
                        myViewHolder.secondary.setText(description);

                        final String mydescription = description;
                        final String mypicid = picid;
                        final String myname = name;
                        final String myid = id;

                        /*image.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                {
                                    {
                                        Intent intent = new Intent(context, ManageUserActivity.class);
                                        Log.i("C", "Conatct number " + number);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("name", myname);
                                        intent.putExtra("number", number);
                                        intent.putExtra("description", mydescription);
                                        intent.putExtra("picid", mypicid);
                                        intent.putExtra("nativecontactid", myid);
                                        context.startActivity(intent);
                                    }
                                }

                            }
                        });
*/

                    }
                }


            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }



    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder){
        super.onViewRecycled(viewHolder);

        //Log.i(TAG,"ItemViewType in onViewRecycled:" + viewHolder.getItemViewType());


        switch (viewHolder.getItemViewType()) {

            case 0:
                MyViewHolder myviewHolder = (MyViewHolder) viewHolder;
                Glide.with(context).clear(myviewHolder.image);
                break;


        }
    }

    public void processClick(int clicktype,int cursorposition,int action) {
        try {
            //View view can be null
            Log.i(TAG, "clicktype:" + clicktype);
            Log.i(TAG, "cursorposition:" + cursorposition);
            Log.i(TAG, "action:" + action);

            FirstCallContacts.handleclick(cursorposition, action);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}