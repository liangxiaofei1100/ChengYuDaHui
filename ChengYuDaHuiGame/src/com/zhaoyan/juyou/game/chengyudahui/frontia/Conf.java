package com.zhaoyan.juyou.game.chengyudahui.frontia;


public class Conf {
	
	public final static String APIKEY = "cd1EyW7zKA7ZmYuDunUnkBfG";
    public final static String CLOUD_APP_LOCATION = "apps/";
    public final static String CLOUD_ICON_LOCATION = "icons/";
    public final static String CLOUD_INFO_LOCATION = "infos/";
    public final static String LOCAL_APP_DOWNLOAD_PATH = "/baiduApp";
    
    public final static String SHARED_PREFS_NAME = "dlapp";
    public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";
    public static final String KEY_NAME_ITEM_POSITION = "item_position";
    public static final String KEY_NAME_LAST_STATUS = "last_status";
    
    public static final String URL_EX = "http://bcs.duapp.com/bccd1eyw7zka7zmyudununkbfgmbaas/";
    
    public static final String ACTION_CANCEL_DOWNLOAD = "com.zhaoyan.cancel.download";
    public static final String ACTION_START_DOWNLOAD = "com.zhaoyan.start.download";
    
    //request_code
    public final static int REQUEST_CODE1 = 2;
    public final static int REQUEST_CODE2 = 3;
    public final static int REQUEST_CODE3 = 4;
    public final static int REQUEST_CODE4 = 5;
    //result_code
    public final static int RESULT_CODE1 = 10;
    public final static int RESULT_CODE2 = 11;
    public final static int RESULT_CODE3 = 12;
    public final static int RESULT_CODE4 = 13;
    
    //app status
    public static final int NOT_DOWNLOAD = 1;
    public static final int DOWNLOADING = 2;
    public static final int DOWNLOADED = 3;
    public static final int INSTALLED = 4;
    public static final int NEED_UDPATE = 5;
    
}
