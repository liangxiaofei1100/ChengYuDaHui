package com.zhaoyan.juyou.game.chengyudahui.download;


public class Conf {
	
	public final static String APIKEY = "cd1EyW7zKA7ZmYuDunUnkBfG";
	
	//local path  begin
	public final static String ZHAOYAN_DIR = "zhaoyan";
	
    public final static String LOCAL_APP_DIR = "/apps";
    public final static String LOCAL_STORY_DIR = "/storys";
    //local path end
    
    public final static String SHARED_PREFS_NAME = "dlapp";
    public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";
    public static final String KEY_NAME_ITEM_POSITION = "item_position";
    public static final String KEY_NAME_LAST_STATUS = "last_status";
    
    //cloud path begin
    public static final String URL_EX = "http://bcs.duapp.com/bccd1eyw7zka7zmyudununkbfgmbaas/";
    public final static String CLOUD_APP_DIR = "apps/";
    public final static String CLOUD_LOGO_DIR = "app_logos/";
    public static final String CLOUD_APP_CAPTURES = "app_captures/";
//    public final static String CLOUD_INFO_DIR = "infos/";
    public static final String CLOUD_LISTEN_DIR = "listen_pic/";
    public static final String CLOUD_STORY_DIR = "storys/";
    
    public static final String APP_URL_EX = URL_EX + CLOUD_APP_DIR;
    public static final String APP_LOGO_URL_EX = URL_EX + CLOUD_LOGO_DIR;
    public static final String APP_CAPTURE_URL_EX = URL_EX + CLOUD_APP_CAPTURES;
    public static final String LISTEN_URL_EX = URL_EX + CLOUD_LISTEN_DIR;
    public static final String STORY_URL_EX = URL_EX + CLOUD_STORY_DIR;
    //cloud path end
    
    public static final String ACTION_CANCEL_DOWNLOAD = "com.zhaoyan.cancel.download";
    public static final String ACTION_START_DOWNLOAD = "com.zhaoyan.start.download";
    
    //app status
    public static final int NOT_DOWNLOAD = 1;
    public static final int DOWNLOADING = 2;
    public static final int DOWNLOADED = 3;
    public static final int INSTALLED = 4;
    public static final int NEED_UDPATE = 5;
    
}
