package com.cavox.utils;

import android.os.Environment;

public class ChatConstants {

    // Below is the Folder structure for Chat
    public static final String extStorageDirectory = Environment
            .getExternalStorageDirectory().toString();
    public static final String CHAT_IMAGES_DIRECTORY = extStorageDirectory
            + "/DeltaCubes/Images/Received";
    public static final String CHAT_VIDEOS_DIRECTORY = extStorageDirectory
            + "/DeltaCubes/Videos/Received";
    public static final String CHAT_AUDIO_DIRECTORY = extStorageDirectory
            + "/DeltaCubes/Audios/Received";
    public static final String CHAT_AUDIO_DIRECTORY_SENT = extStorageDirectory
            + "/DeltaCubes/Audios/Sent";
    public static final String CHAT_DOCUMENTS_DIRECTORY = extStorageDirectory
            + "/DeltaCubes/Documents/Received";


    public static final String INTENT_DESTINATION_NUMBER = "destinationnumber";
    public static final String INTENT_CHAT_ID = "chatid";
    public static final String INTENT_LOCATION_LATITUDE = "latitude";
    public static final String INTENT_LOCATION_LONGITUDE = "longitude";
    public static final String INTENT_LOCATION_ADDRESS = "address";
    // Chat Constants
    public static final String INTENT_CHAT_CONTACT_NUMBER = "contactNumber";
    public static final String INTENT_CHAT_CONTACT_NAME = "contactName";

    public static final String TIME_FORMAT = "hh:mm a";
}
