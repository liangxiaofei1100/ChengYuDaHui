package com.zhaoyan.juyou.game.chengyudahui.dictate;

import android.os.Parcel;
import android.os.Parcelable;

public class StoryItem implements Parcelable{
	private int typeId;
	private String typeName;
	private String folder;
	
	public StoryItem(int id, String name, String foler){
		this.typeId = id;
		this.typeName = name;
		this.folder = foler;
	}
	
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private StoryItem(Parcel in) {
		readFromParcel(in);
	}

	public static final Parcelable.Creator<StoryItem> CREATOR = new Parcelable.Creator<StoryItem>() {

		@Override
		public StoryItem createFromParcel(Parcel source) {
			return new StoryItem(source);
		}

		@Override
		public StoryItem[] newArray(int size) {
			return new StoryItem[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(typeId);
		dest.writeString(typeName);
		dest.writeString(folder);
	}

	public void readFromParcel(Parcel in) {
		typeId = in.readInt();
		typeName = in.readString();
		folder = in.readString();
	}
	
}
