package com.zhaoyan.communication.qrcode;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.zhaoyan.communication.util.Log;

/**
 * ServerInfoMessage user for connect a server.
 * 
 */
public class ServerInfoMessage extends QRCodeMessage<ServerInfoMessage> {
	private static final String TAG = ServerInfoMessage.class.getSimpleName();
	public String ssid;
	public String ip;
	public int port;
	public int networkType;

	public ServerInfoMessage() {
		this("", "", 0, 0);
	}

	public ServerInfoMessage(String ssid, String ip, int port, int serverType) {
		type = MessageType.SERVER_INFO;
		this.ssid = ssid;
		this.ip = ip;
		this.port = port;
		this.networkType = serverType;
	}

	@Override
	protected String messageToQRcode() {
		String qrcodeString = "";

		JSONObject qrcode = new JSONObject();
		try {
			qrcode.put("ssid", ssid);
			qrcode.put("type", type);
			qrcode.put("ip", ip);
			qrcode.put("port", port);
			qrcode.put("nt", networkType);
			qrcodeString = qrcode.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "messageToQRcode" + e);
		}

		Log.d(TAG, "qrcodeString: " + qrcodeString);
		return qrcodeString;
	}

	@Override
	public ServerInfoMessage QRCodeToMessage(String qrcode) {
		Log.d(TAG, "QRCodeToMessage qrcode " + qrcode);
		ServerInfoMessage serverInfoMessage = new ServerInfoMessage();

		JSONTokener jsonParser = new JSONTokener(qrcode);
		JSONObject userJsonObject = null;

		try {
			userJsonObject = (JSONObject) jsonParser.nextValue();
			serverInfoMessage.ssid = userJsonObject.getString("ssid");
			serverInfoMessage.type = userJsonObject.getInt("type");
			serverInfoMessage.ip = userJsonObject.getString("ip");
			serverInfoMessage.port = userJsonObject.getInt("port");
			serverInfoMessage.networkType = userJsonObject.getInt("nt");
			return serverInfoMessage;
		} catch (JSONException e) {
			Log.e(TAG, "QRCodeToMessage error. " + e);
		}

		return serverInfoMessage;
	}

}
