package com.zhaoyan.communication;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.zhaoyan.communication.ipc.aidl.User;


public class HistoryInfo implements Parcelable{
	/**
	 * 0, send message </br>
	 * 1, receive message
	 * */
	private int msgType;
	
	/**send or receive time*/
	private long date;
	
	/**receive message user*/
	private User receiveUser;
	
	/**send user name*/
	private String sendUserName;
	
	/**send user head icon id*/
	private int sendUserHeadId;
	/**send user head icon*/
	private byte[] sendUserIcon;
	
	/**send or receive file info*/
	private File file;
	/**send or recive file name*/
	private String fileName;
	private long fileSize;
	
	/**current transfer file size(bytes)*/
	private double progress = 0;
	private double max = 0;
	
	/**this file's status:sending,receiving,send ok,send fail and so on*/
	private int status;
	
	/**transfer file icon*/
	private byte[] fileIcon;
	/**file type,image,apk,video and so on*/
	private int fileType;
	
	/**for speed*/
	private long startTime;
	private long nowTime;
	
	private Uri uri;
	
	public HistoryInfo(){
	}
	
	public HistoryInfo(int  msgType, long date, User user, File file){
		this.msgType = msgType;
		this.date = date;
		this.receiveUser = user;
		this.file = file;
	}
	
	public int getMsgType(){
		return msgType;
	}
	
	public void setMsgType(int type){
		msgType = type;
	}
	
	public long getDate(){
		return date;
	}
	
	public String getFormatDate(){
		return getFormatDate(date);
	}
	
	public void setDate(long date){
		this.date = date;
	}
	
	public User getReceiveUser(){
		return receiveUser;
	}
	
	public void setReceiveUser(User user){
		this.receiveUser = user;
	}
	
	public String getSendUserName(){
		return sendUserName;
	}
	
	public void setSendUserName(String name){
		this.sendUserName = name;
	}
	
	public int getSendUserHeadId(){
		return sendUserHeadId;
	}
	
	public void setSendUserHeadId(int headId){
		this.sendUserHeadId = headId;
	}
	
	public byte[] getSendUserIcon(){
		return sendUserIcon;
	}
	
	public void setSendUserIcon(byte[] headIcon){
		this.sendUserIcon = headIcon;
	}
	
	public File getFile(){
		return file;
	}
	
	public void setFile(File file){
		this.file = file;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	public long getFileSize(){
		return fileSize;
	}
	
	public void setFileSize(long size){
		this.fileSize = size;
	}
	
	public double getMax(){
		return fileSize;
	}
	
	public double getProgress(){
		return progress;
	}
	
	public void setProgress(double progress){
		this.progress = progress;
	}
	
	public int getStatus(){
		return status;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public byte[] getIcon(){
		return fileIcon;
	}
	
	public void setIcon(byte[] icon){
		this.fileIcon = icon;
	}
	
	public int getFileType(){
		return fileType;
	}
	
	public void setFileType(int type){
		this.fileType = type;
	}
	
	public void setStartTime(long time){
		this.startTime = time;
	}
	
	public void setNowTime(long time){
		this.nowTime = time;
	}
	
	public String getSpeed(){
		long duration = nowTime  - startTime;
		return getFormatSize((progress / (duration / 1000)));
	}
	
	public Uri getUri(){
		return uri;
	}
	
	public void setUri(Uri uri){
		this.uri = uri;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
	}
	
	public static String getFormatSize(double size){
		if (size >= 1024 * 1024 * 1024){
			Double dsize = size / (1024 * 1024 * 1024);
			return new DecimalFormat("#.00").format(dsize) + "G";
		}else if (size >= 1024 * 1024) {
			Double dsize = size / (1024 * 1024);
			return new DecimalFormat("#.00").format(dsize) + "M";
		}else if (size >= 1024) {
			Double dsize = size / 1024;
			return new DecimalFormat("#.00").format(dsize) + "K";
		}else {
			return String.valueOf((int)size) + "B";
		}
	}
	
	/**get date format*/
	public static String getFormatDate(long date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = format.format(new Date(date));
		return dateString;
	}
}
