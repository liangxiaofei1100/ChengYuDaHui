package com.zhaoyan.juyou.game.chengyudahui.study.story;

import android.os.Parcel;
import android.os.Parcelable;

public class StoryInfo implements Parcelable{

	private String title;
	private String fileName;
	private String folder;
	private String localPath;
	private long size;
	private int position;
	private int page;
	private boolean isSelect;
	private String sortLetter;
	private int duration;
	
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

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
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
	
	public String getSortLetter() {
		return sortLetter;
	}

	public void setSortLetter(String sortLetter) {
		this.sortLetter = sortLetter;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
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
		dest.writeString(folder);
		dest.writeString(localPath);
		dest.writeLong(size);
		dest.writeInt(position);
		dest.writeInt(page);
		dest.writeInt(isSelect ? 1 : 0);
		dest.writeString(sortLetter);
		dest.writeInt(duration);
	}

	public void readFromParcel(Parcel in) {
		title = in.readString();
		fileName = in.readString();
		folder = in.readString();
		localPath = in.readString();
		size = in.readLong();
		position = in.readInt();
		page = in.readInt();
		isSelect = in.readInt() == 1 ? true : false;
		sortLetter = in.readString();
		duration = in.readInt();
	}
	
	
}
