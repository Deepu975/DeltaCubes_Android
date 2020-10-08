package com.cavox.utils;

import com.ca.Utils.CSConstants;
import com.cavox.konverz.NewChatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables {



	public static final String MyPREFERENCES = "IamLivePrefs";


	public static boolean hold = true;


	public static CSConstants.CALLRECORD callrecord = CSConstants.CALLRECORD.DONTRECORD;


/*
	public static String server = "ppclb-824584617.us-west-2.elb.amazonaws.com";//default
	public static int port = 80;//default
	public static String appid = "aid_8656e0f6_c982_4485_8ca7_656780b53d34";//konverz
	public static String appname = "iamlive";
	//public static String appid = "pid_39684a6d_5103_4254_9775_1b923b9b98d5";//sample apps
*/


	public static String server = "ppclb-824584617.us-west-2.elb.amazonaws.com";//default
	public static int port = 80;//default
	public static String appid = "aid_8656e0f6_c982_4485_8ca7_656780b53d34";//konverz
	public static String appname = "iamlive";
	//public static String appid = "pid_39684a6d_5103_4254_9775_1b923b9b98d5";//sample apps


/*
	//public static String server = "iml.vox-cpaas.in";//india portal
	//public static int port = 443;//india portla LB port
	public static String server = "13.234.229.198";//india portal
	public static int port = 8050;//defaultset
	public static String appid = "pid_88ae4e2a_e094_4b21_8cfe_f3e96693eb7b";//india portal
	public static String appname = "iamlive";
*/


/*
	public static String server = "ppclb-824584617.us-west-2.elb.amazonaws.com";//default
	public static int port = 80;//default
	public static String appid = "pid_4df0c551_11b2_4530_8a78_10b83c1986f1";//konverz
	public static String appname = "iamlive";
	//public static String appid = "pid_39684a6d_5103_4254_9775_1b923b9b98d5";//sample apps
*/


/*
	public static String server = "ppclb-824584617.us-west-2.elb.amazonaws.com";//default
	public static int port = 80;//default
	public static String appid = "pid_cf75fefc_ebdf_48f9_89b9_b9ad373dd3d5";//deltacubes
	public static String appname = "deltacubes";
	//public static String appid = "pid_39684a6d_5103_4254_9775_1b923b9b98d5";//sample apps
*/

/*
	public static String server = "Android.talkr.ca";//talkr
	public static int port = 80;//default
	public static String appid = "iamlivedbnew";//talkr
	public static String appname = "talkr";
	//public static String appid = "pid_39684a6d_5103_4254_9775_1b923b9b98d5";//sample apps
*/

/*
	public static String server = "192.96.206.176";//default
	public static int port = 7061;//default
	public static String appid = "iamlivedbnew";//tringy
	public static String appname = "tringy";
*/

/*
	public static String server = "ppclb-824584617.us-west-2.elb.amazonaws.com";//default
	public static int port = 80;//default
	//public static String appid = "iamlivedbnew";//jetbyte
	public static String appid = "pid_904a93f4_5889_4ee5_a129_9d43737d149a";//jetbyte appid
	public static String appname = "jetbyte";
	//public static String appid = "pid_39684a6d_5103_4254_9775_1b923b9b98d5";//sample apps
*/

/*
	public static String server = "208.96.164.20";//ericall
	public static int port = 7061;
	public static String appid = "";//ericall
	public static String appname = "";
*/

/*
	public static String server = "13.234.229.198";//india portal
	public static int port = 8050;//default
	public static String appid = "pid_2b5d90bc_82cb_4cbd_ba64_245c2d8fb38d";//konverz
	public static String appname = "iamlive";
	//public static String appid = "pid_39684a6d_5103_4254_9775_1b923b9b98d5";//sample apps
*/


/*
    public static String server = "104.245.48.14";
    public static int port = 8050;//default
    public static String appid = "iamlivedbnew";
    public static String appname = "SpiceComm";
    //public static String appid = "pid_39684a6d_5103_4254_9775_1b923b9b98d5";//sample apps
*/


    public static NewChatActivity NewChatActivityObj =  new NewChatActivity();

	public static final Logger LOG = LoggerFactory.getLogger("ConnectSdk");





	public static int loginretries = 0;
public static boolean isalreadysignedup = false;
public static String phoneNumber = "";
public static String pass = "pass";
	public static String inchatactivitydestination = "";
	public static int tab_selected = 1;

	public static boolean toolbarcollapsed = false;

public static int incallcount = 0;
	public static int answeredcallcount = 0;

	public static String lastcallid = "";

	public static final String ONLY_DATE_FORMAT = "dd MMM yy";
	public static final String TIME_FORMAT = "hh:mm a";
	public static final String TIME_FORMAT_24HR = "HH:mm";






	public static String chatappname = "";
	//image
	public static String imagedirectory = chatappname+"/Images";
	public static String imagedirectorysent = chatappname+"/Images/Sent";//used for location and doc
	public static String imagedirectoryreceived = chatappname+"/Images/Received"; //used for location and thumbainal internally

	//video
	public static String videodirectory = chatappname+"/Videos";
	public static String videodirectorysent = chatappname+"/Videos/Sent";//used for location and doc
	public static String videodirectoryreceived = chatappname+"/Videos/Received"; //used for location and thumbainal internally

	//audio
	public static String audiodirectory = chatappname+"/Audios";
	public static String audiodirectorysent = chatappname+"/Audios/Sent";//used for location and doc
	public static String audiodirectoryreceived = chatappname+"/Audios/Received"; //used for location and thumbainal internally

	//Documents
	public static String docsdirectory = chatappname+"/Documents";
	public static String docsdirectorysent = chatappname+"/Documents/Sent";//used for location and doc
	public static String docsdirectoryreceived = chatappname+"/Documents/Received"; //used for location and thumbainal internally

	//profiles
	public static String profilesdirectory = chatappname+"/Profile Photos";
	//public static String profilesdirectorysent = chatappname+"/Profile Photos/Sent";//used for location and doc
	//public static String profilesdirectoryreceived = chatappname+"/Profile Photos/Received"; //used for location and thumbainal internally

	public static String thumbnailsdirectory = chatappname+"/Thumbnails";
	public static String recordingsdirectory = chatappname+"/Recordings";

}
