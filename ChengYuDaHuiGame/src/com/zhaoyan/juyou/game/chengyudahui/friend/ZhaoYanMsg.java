package com.zhaoyan.juyou.game.chengyudahui.friend;

import android.content.Context;

public class ZhaoYanMsg {
	
	private int msgType;
	private String belongId;
	private String belongAvatar;//avator path in server
	private String msgTime;
	private int status;
	private String content;
	private String converstationId;
	
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public String getBelongId() {
		return belongId;
	}
	public void setBelongId(String belongId) {
		this.belongId = belongId;
	}
	public String getBelongAvatar() {
		return belongAvatar;
	}
	public void setBelongAvatar(String belongAvatar) {
		this.belongAvatar = belongAvatar;
	}
	public String getMsgTime() {
		return msgTime;
	}
	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getConverstationId() {
		return converstationId;
	}
	public void setConverstationId(String converstationId) {
		this.converstationId = converstationId;
	}
	public static ZhaoYanMsg createTextSendMsg(Context context, String targetId, String msg){
		ZhaoYanMsg zhaoYanMsg = new ZhaoYanMsg();
		zhaoYanMsg.setBelongId(targetId);
		//...
		return zhaoYanMsg;
	}
	
	public static ZhaoYanMsg createLocationSendMsg(Context context, String targetId, String address, 
			double latitude, double longtitude){
		ZhaoYanMsg zhaoYanMsg = new ZhaoYanMsg();
		zhaoYanMsg.setBelongId(targetId);
		//...
		return zhaoYanMsg;
	}
}
