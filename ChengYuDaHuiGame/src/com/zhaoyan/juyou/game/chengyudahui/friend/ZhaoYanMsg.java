package com.zhaoyan.juyou.game.chengyudahui.friend;

import android.content.Context;

public class ZhaoYanMsg {
	
	private int msgType;
	private String belongId;
	private String belongAvatar;//avator path in server
	private long msgTime;
	private int status;
	private String content;//1.如果是文本，那么content就是文本内容，2.如果是图片，那么content就是图片path
	private String converstationId;
	private String filePath;
	
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
	public Long getMsgTime() {
		return msgTime;
	}
	public void setMsgTime(Long msgTime) {
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
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public static ZhaoYanMsg createTextSendMsg(Context context, String targetId, String msg){
		ZhaoYanMsg zhaoYanMsg = new ZhaoYanMsg();
		zhaoYanMsg.setBelongId(targetId);
		zhaoYanMsg.setMsgType(MsgConfig.TYPE_TEXT);
		zhaoYanMsg.setContent(msg);
		zhaoYanMsg.setMsgTime(System.currentTimeMillis());
		//...
		return zhaoYanMsg;
	}
	
	public static ZhaoYanMsg createLocationSendMsg(Context context, String targetId, String address, 
			double latitude, double longtitude){
		ZhaoYanMsg zhaoYanMsg = new ZhaoYanMsg();
		zhaoYanMsg.setBelongId(targetId);
		zhaoYanMsg.setMsgType(MsgConfig.TYPE_LOCATION);
		zhaoYanMsg.setMsgTime(System.currentTimeMillis());
		//...
		return zhaoYanMsg;
	}
	
	public static ZhaoYanMsg createImageSendMsg(Context context, String targetId, String path){
		ZhaoYanMsg zhaoYanMsg = new ZhaoYanMsg();
		zhaoYanMsg.setBelongId(targetId);
		zhaoYanMsg.setMsgType(MsgConfig.TYPE_IMAGE);
		zhaoYanMsg.setMsgTime(System.currentTimeMillis());
		zhaoYanMsg.setContent("file://" + path);//for UIL to use,should add ex "file://"
		//...
		return zhaoYanMsg;
	}
}
