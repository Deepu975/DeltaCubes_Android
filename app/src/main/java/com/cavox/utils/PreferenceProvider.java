package com.cavox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceProvider {


    SharedPreferences Pref;
    Editor edit;
    public static String NOTIFICATION_ID = "notificationID";
    public static String NOTIFICATION_MESSAGE = "notificationMessage";
    public static String IS_NEW_SERVER_INITILIZED="isNewServerInitilized";
    public static String IS_FILE_SHARE_AVAILABLE="isFileShareAvailable";
    //public static String IS_CALL_IN_CALLING_STATE="isCallInCallState";
    public static final String DONT_SHOW_LOCK_SCREEN_NOTIFICATION_MIUI = "dont_show_lock_screen_notification_miui";
    public PreferenceProvider(Context applicationContext) {
        // TODO Auto-generated constructor stub

        Pref = applicationContext.getSharedPreferences(GlobalVariables.MyPREFERENCES,
                Context.MODE_PRIVATE);

    }

    public void setPrefString(String key, String val) {

        edit = Pref.edit();
        edit.putString(key, val);
        edit.commit();
    }

    public void remove(String key) {
        edit = Pref.edit();
        edit.remove(key);
        edit.commit();
    }

    public void setPrefboolean(String key, boolean val) {
        edit = Pref.edit();
        edit.putBoolean(key, val);
        edit.commit();
    }

    public void setPrefint(String key, int val) {

        edit = Pref.edit();
        edit.putInt(key, val);
        edit.commit();
    }

    public void setPreffloat(String key, float val) {

        edit = Pref.edit();
        edit.putFloat(key, val);
        edit.commit();
    }

    public String getPrefString(String key) {

        return Pref.getString(key, "");
    }

    public boolean getPrefBoolean(String key) {

        return Pref.getBoolean(key, false);
    }

    public int getPrefInt(String key) {

        return Pref.getInt(key, 0);
    }

    public float getPrefFloat(String key) {

        return Pref.getFloat(key, 0);
    }

    public int getPrefInt(String key, int i) {
        // TODO Auto-generated method stub
        return Pref.getInt(key, -1);
    }

}
