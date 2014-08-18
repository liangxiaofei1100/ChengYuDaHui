package com.zhaoyan.juyou.game.chengyudahui.dictate;

import android.os.Parcel;
import android.os.Parcelable;

public class StoryInfo implements Parcelable{

	private String title;
	private String fileName;
	private String remotePath;
	private String localPath;
	private long size;
	private int position;
	private int page;
	private boolean isSelect;
	
	public StoryInfo(){};

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private StoryInfo(Parcel in) {
		readFromParcel(in);
	}

	public static final Parcelable.Creator<StoryInfo> CREATOR = new Parcelable.Creator<StoryInfo>() {

		@Override
		public StoryInfo createFromParcel(Parcel source) {
			return new StoryInfo(source);
		}

		@Override
		public StoryInfo[] newArray(int size) {
			return new StoryInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(fileName);
		dest.writeString(remotePath);
		dest.writeString(localPath);
		dest.writeLong(size);
		dest.writeInt(position);
		dest.writeInt(page);
		dest.writeInt(isSelect ? 1 : 0);
	}

	public void readFromParcel(Parcel in) {
		title = in.readString();
		fileName = in.readString();
		remotePath = in.readString();
		localPath = in.readString();
		size = in.readLong();
		position = in.readInt();
		page = in.readInt();
		isSelect = in.readInt() == 1 ? true : false;
	}
	
	
}
