package com.zhaoyan.juyou.game.chengyudahui.frontia;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable {
	private int appId;// 标记该应用的唯一标识符
	private String label;// 应用名称
	private String authorId;// 作者id，暂时不知道怎么用
	private String author;// 应用作者
	private String date;// 应用上传或者更新时间
	private String appLanguage;// 应用语言
	private String version;// 应用版本号
	private String introduce;// 详细介绍
	private String title;// 简介
	private String notes;//获取金币等相关介绍
	private long size;

	private String appUrl; // app云端下载地址
	private String iconUrl;// app icon 云端地址
	// 暂定显示两张app界面截图介绍
	private String jiemian_url1;// app 界面截图介绍地址1
	private String jiemian_url2;// app 界面截图介绍地址2

	private String appType;// 应用分类：如社交，游戏（卡牌，回合等等），购物等等

	private String packageName;// 应用包名

	private int status;

	private long progressBytes;
	private int percent;

	private String localPath;

	public AppInfo() {
	}

	public AppInfo(int app_id, String label, String authorId, String date,
			String appLanguage, String version, String introduce, String title,
			String goldInfos,
			long size, String author, String app_url, String icon_url,
			String jiemian_url1, String jiemian_url2, String appType,
			String packageName) {
		super();
		this.appId = app_id;
		this.label = label;
		this.authorId = authorId;
		this.author = author;
		this.introduce = introduce;
		this.title = title;
		this.notes = goldInfos;
		this.size = size;
		this.version = version;
		this.appLanguage = appLanguage;
		this.date = date;

		this.appUrl = app_url;
		this.iconUrl = icon_url;
		this.jiemian_url1 = jiemian_url1;
		this.jiemian_url2 = jiemian_url2;

		this.appType = appType;
		this.packageName = packageName;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getAppId() {
		return appId;
	}

	// public void setSize(String size){
	// this.sizeStr = size;
	// }
	//
	// public String getSize(){
	// return sizeStr;
	// }

	public void setAppSize(long size) {
		this.size = size;
	}

	public long getAppSize() {
		return size;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setDate(String updateTime) {
		this.date = updateTime;
	}

	public String getDate() {
		return date;
	}

	public void setAppLanguage(String language) {
		this.appLanguage = language;
	}

	public String getAppLanguage() {
		return appLanguage;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	// public String get_who(){
	// return "Software";
	// }

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public void setGoldInfos(String goldInfos){
		this.notes = goldInfos;
	}
	
	public String getGoldInfos(){
		return notes;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setJiemianUrl1(String jiemianUrl1) {
		this.jiemian_url1 = jiemianUrl1;
	}

	public String getJiemianUrl1() {
		return jiemian_url1;
	}

	public void setJiemianUrl2(String jiemianUrl2) {
		this.jiemian_url2 = jiemianUrl2;
	}

	public String getJiemianUrl2() {
		return jiemian_url2;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getAppType() {
		return appType;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setProgressBytes(long bytes) {
		this.progressBytes = bytes;
	}

	public long getProgressBytes() {
		return progressBytes;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public int getPercent() {
		return percent;
	}

	public void setAppLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getAppLocalPath() {
		return localPath;
	}

	public static AppInfo parseJson(JSONObject jsonObject) {
		AppInfo appInfo = null;
		int app_id = 0;
		String app_label = "";
		String author_id = "";
		String author = "";
		String date = "";
		String app_language = "";
		String app_version = "";
		String introduce = "";
		String title = "";
		String goldInfos = "";
		long size = 0;

		String app_url = "";
		String icon_url = "";

		String jiemian_url1 = "";
		String jiemian_url2 = "";

		String app_type = "";

		String packageName = "";
		
		try {
			app_id = jsonObject.getInt(AppInfoTable.APP_ID);
			app_label = jsonObject.getString(AppInfoTable.APP_LABEL);
			author_id = jsonObject.getString(AppInfoTable.AUTHOR_ID);
			author = jsonObject.getString(AppInfoTable.AUTHOR);
			date = jsonObject.getString(AppInfoTable.DATE);
			app_language = jsonObject.getString(AppInfoTable.APP_LANGUAGE);
			app_version = jsonObject.getString(AppInfoTable.APP_VERSION);
			introduce = jsonObject.getString(AppInfoTable.INTRODUCE);
			title = jsonObject.getString(AppInfoTable.TITLE);
			goldInfos = jsonObject.getString(AppInfoTable.NOTES);
			size = jsonObject.getLong(AppInfoTable.SIZE);
			
			app_url = jsonObject.getString(AppInfoTable.APP_URL);
			icon_url = jsonObject.getString(AppInfoTable.ICON_URL);
			jiemian_url1 = jsonObject.getString(AppInfoTable.JIEMIAN_URL1);
			jiemian_url2 = jsonObject.getString(AppInfoTable.JIEMIAN_URL2);
			app_type = jsonObject.getString(AppInfoTable.APP_TYPE);
			packageName = jsonObject.getString(AppInfoTable.PACKAGE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		appInfo = new AppInfo(app_id, app_label, author_id, date,
				app_language, app_version, introduce, title, goldInfos, size, author,
				app_url, icon_url, jiemian_url1, jiemian_url2, app_type,
				packageName);

		return appInfo;
	}

	public String toString() {
		String info = "appid=" + appId + ",app_label=" + label + ",author_id="
				+ authorId + ",author=" + author + ",update_time=" + date
				+ ",app_language=" + appLanguage + ",app_version=" + version
				+ ",introduce=" + introduce + ",title=" + title
				+ ",goldInfos:" + notes + ",size="
				+ size + ",app_url=" + appUrl + ",icon_url=" + iconUrl
				+ ",jiemian_url1" + jiemian_url1 + ",jiemian_url2="
				+ jiemian_url2 + ",app_type=" + appType + ",packageName="
				+ packageName;

		return info;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private AppInfo(Parcel in) {
		readFromParcel(in);
	}

	public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {

		@Override
		public AppInfo createFromParcel(Parcel source) {
			return new AppInfo(source);
		}

		@Override
		public AppInfo[] newArray(int size) {
			return new AppInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(appId);
		dest.writeString(label);
		dest.writeString(authorId);
		dest.writeString(author);
		dest.writeString(date);
		dest.writeString(appLanguage);
		dest.writeString(version);
		dest.writeString(introduce);
		dest.writeString(title);
		dest.writeString(notes);
		dest.writeLong(size);
		dest.writeString(appUrl);
		dest.writeString(iconUrl);
		dest.writeString(jiemian_url1);
		dest.writeString(jiemian_url2);
		dest.writeString(appType);
		dest.writeString(packageName);
		dest.writeInt(status);
		dest.writeLong(progressBytes);
		dest.writeInt(percent);
		dest.writeString(localPath);
	}

	public void readFromParcel(Parcel in) {
		appId = in.readInt();
		label = in.readString();
		authorId = in.readString();
		author = in.readString();
		date = in.readString();
		appLanguage = in.readString();
		version = in.readString();
		introduce = in.readString();
		title = in.readString();
		notes = in.readString();
		size = in.readLong();
		appUrl = in.readString();
		iconUrl = in.readString();
		jiemian_url1 = in.readString();
		jiemian_url2 = in.readString();
		appType = in.readString();
		packageName = in.readString();
		status = in.readInt();
		progressBytes = in.readLong();
		percent = in.readInt();
		localPath = in.readString();
	}

}
