package com.zhaoyan.juyou.game.chengyudahui.frontia;

import org.json.JSONObject;

public class AppInfo {
    private int app_id;//标记该应用的唯一标识符
    private String app_label;//应用名称
    private String author_id;//作者id，暂时不知道怎么用
    private String author;//应用作者
    private String update_time;//应用上传或者更新时间
    private String app_language;//应用语言
    private String app_version;//应用版本号
    private int download_count;//被下载次数
    private String introduce ;//详细介绍
    private String title;//简介
    private String size;//应用大小
    
    private String app_url; //app下载地址
    private String icon_url;// app icon 地址
    //暂定显示两张app界面截图介绍
    private String jiemian_url1;//app 界面截图介绍地址1
    private String jiemian_url2;//app 界面截图介绍地址2
    
    private String app_type;//应用分类：如社交，游戏（卡牌，回合等等），购物等等
    
    private String packageName;//应用包名
    
    public AppInfo(){
    	
    }
    
    public AppInfo(int app_id,String app_label,
    		String author_id,String update_time,
    		String app_language,String app_version,
    		int download_count,String introduce,String title,
    		String size,String author,
    		String app_url, String icon_url,
    		String jiemian_url1, String jiemian_url2,
    		String app_type, String packageName
    		){
    	this.app_id = app_id ;
    	this.app_label = app_label;
    	this.author_id = author_id;
    	this.author = author;
    	this.download_count = download_count ;
    	this.introduce = introduce;
    	this.title = title;
    	this.size = size;
    	this.app_version = app_version;
    	this.app_language = app_language;
    	this.update_time = update_time;
    	
    	this.app_url = app_url;
    	this.icon_url = icon_url;
    	this.jiemian_url1 = jiemian_url1;
    	this.jiemian_url2 = jiemian_url2;
    	
    	this.app_type = app_type;
    	this.packageName = packageName;
    }
    
    public void setAppId(int appId){
    	this.app_id = appId;
    }
    
    public int getAppId(){
    	return app_id;
    }
    
    public void setDownloadCount(int downloadCount){
    	this.download_count = downloadCount;
    }
    
    public int getDownloadCount(){
    	return download_count;
    }
    
    public void setSize(String size){
    	this.size = size;
    }
    
    public String getSize(){
    	return size;
    }
    
    public void setAppLabel(String label){
    	this.app_label = label;
    }
    
    public String getAppLabel(){
    	return app_label;
    }
    
    public void setAuthorId(String authorId){
    	this.author_id = authorId;
    }
    
    public String getAuthorId(){
    	return author_id;
    }
    
    public void setUpdateTime(String updateTime){
    	this.update_time = updateTime;
    }
    
    public String getUpdateTime(){
    	return update_time;
    }
    
    public void setAppLanguage(String language){
    	this.app_language = language;
    }
    
    public String getAppLanguage(){
    	return app_language;
    }
    
    public void setAppVersion(String version){
    	this.app_version = version;
    }
    
    public String getAppVersion(){
    	return app_version;
    }
    
    public void setIntroduce(String introduce){
    	this.introduce = introduce;
    }
    
    public String getIntroduce(){
    	return introduce;
    }
    
    public void setAuthor(String author){
    	this.author = author;
    }
    
    public String getAuthor(){
    	return author;
    }
    
    public String get_who(){
    	return "Software";
    }
    
    public void setTitle(String title){
    	this.title = title;
    }
    
	public String getTitle() {
		return title;
	}
	
	public void setAppUrl(String appUrl){
		this.app_url = appUrl;
	}
	
	public String getAppUrl() {
		return app_url;
	}
	
	public void setIconUrl(String iconUrl){
		this.icon_url = iconUrl;
	}
	
	public String getIconUrl() {
		return icon_url;
	}
	
	public void setJiemianUrl1(String jiemianUrl1){
		this.jiemian_url1 = jiemianUrl1;
	}
	
	public String getJiemianUrl1() {
		return jiemian_url1;
	}
	
	public void setJiemianUrl2(String jiemianUrl2){
		this.jiemian_url2 = jiemianUrl2;
	}
	
	public String getJiemianUrl2() {
		return jiemian_url2;
	}
	
	public void setAppType(String appType){
		this.app_type = appType;
	}
	
	public String getAppType() {
		return app_type;
	}
	
	public void setPackageName(String packageName){
		this.packageName = packageName;
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public static AppInfo parseJson(JSONObject jsonObject){
		AppInfo appInfo = null;
		int app_id = 0;
		String app_label = "";
		String author_id = "";
		String author = "";
		String update_time = "";
		String app_language = "";
		String app_version = "";
		int download_count = 0;
		String introduce = "";
		String title = "";
		String size = "";

		String app_url = "";
		String icon_url = "";

		String jiemian_url1 = "";
		String jiemian_url2 = "";

		String app_type = "";

		String packageName = "";
		try {
			app_id = jsonObject.getInt(AppJSON.APP_ID);
			app_label = jsonObject.getString(AppJSON.APP_LABEL);
			author_id = jsonObject.getString(AppJSON.AUTHOR_ID);
			author = jsonObject.getString(AppJSON.AUTHOR);
			update_time = jsonObject.getString(AppJSON.UPDATE_TIME);
			app_language = jsonObject.getString(AppJSON.APP_LANGUAGE);
			app_version = jsonObject.getString(AppJSON.APP_VERSION);
			download_count = jsonObject.getInt(AppJSON.DOWNLOAD_COUNT);
			introduce = jsonObject.getString(AppJSON.INTRODUCE);
			title = jsonObject.getString(AppJSON.TITLE);
			size = jsonObject.getString(AppJSON.SIZE);
			app_url = jsonObject.getString(AppJSON.APP_URL);
			icon_url = jsonObject.getString(AppJSON.ICON_URL);
			jiemian_url1 = jsonObject.getString(AppJSON.JIEMIAN_URL1);
			jiemian_url2 = jsonObject.getString(AppJSON.JIEMIAN_URL2);
			app_type = jsonObject.getString(AppJSON.APP_TYPE);
			packageName = jsonObject.getString(AppJSON.PACKAGE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		appInfo = new AppInfo(app_id, app_label, author_id, update_time, app_language, 
				app_version, download_count, 
				introduce, title, size, author, app_url, icon_url, jiemian_url1, 
				jiemian_url2, app_type, packageName);
		
		return appInfo;
	}
    
	public String toString(){
		String info = "appid=" + app_id + ",app_label=" + app_label + ",author_id=" + author_id
				+ ",author=" + author+ ",update_time=" + update_time + ",app_language=" + app_language
				+ ",app_version=" + app_version + ",download_count=" + download_count
				+ ",introduce=" + introduce + ",title=" + title + ",size=" + size
				+ ",app_url=" + app_url+ ",icon_url=" + icon_url + ",jiemian_url1" + jiemian_url1
				+",jiemian_url2=" + jiemian_url2 + ",app_type=" + app_type + ",packageName=" + packageName;
		
		return info;
	}
}
