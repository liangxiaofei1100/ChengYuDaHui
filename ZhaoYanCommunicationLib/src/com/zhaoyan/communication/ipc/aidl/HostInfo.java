package com.zhaoyan.communication.ipc.aidl;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class HostInfo implements Serializable, Parcelable {

	private static final long serialVersionUID = 57468L;
	public int hostId = -1;// ����server��������ڱ����ң����Կ��ٶ�λ
	public int ownerID;
	public String ownerName;
	public int personLimit;
	public String packageName;
	public String appName;
	public int personNumber;
	public byte isAlive = 0;// just a flag like boolean
	public int app_id;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(hostId);
		dest.writeInt(ownerID);
		dest.writeString(ownerName);
		dest.writeInt(personLimit);
		dest.writeString(packageName);
		dest.writeString(appName);
		dest.writeInt(personNumber);
		dest.writeByte(isAlive);
		dest.writeInt(app_id);
	}

	public void readFromParcel(Parcel source) {
		hostId = source.readInt();
		ownerID = source.readInt();
		ownerName = source.readString();
		personLimit = source.readInt();
		packageName = source.readString();
		appName = source.readString();
		personNumber = source.readInt();
		isAlive = source.readByte();
		app_id = source.readInt();
	}

	public HostInfo() {
	}

	public HostInfo(Parcel source) {
		readFromParcel(source);
	}

	public static Creator<HostInfo> CREATOR = new Parcelable.Creator<HostInfo>() {

		@Override
		public HostInfo createFromParcel(Parcel source) {
			return new HostInfo(source);
		}

		@Override
		public HostInfo[] newArray(int size) {
			return new HostInfo[size];
		}

	};
}
