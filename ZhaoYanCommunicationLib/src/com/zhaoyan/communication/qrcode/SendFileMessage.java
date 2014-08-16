package com.zhaoyan.communication.qrcode;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.zhaoyan.communication.util.Log;

public class SendFileMessage extends QRCodeMessage<SendFileMessage> {
	private static final String TAG = SendFileMessage.class.getSimpleName();

	public ServerInfoMessage serverInfoMessage;

	public SendFileMessage() {
		this(null);
	}

	public SendFileMessage(ServerInfoMessage serverInfo) {
		type = MessageType.SEND_FILE;
		this.serverInfoMessage = serverInfo;

	}

	@Override
	protected String messageToQRcode() {
		String qrcodeString = "";

		JSONObject qrcode = new JSONObject();
		try {
			qrcode.put("type", type);
			qrcode.put("server", serverInfoMessage.messageToQRcode());
			qrcodeString = qrcode.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "messageToQRcode" + e);
		}

		Log.d(TAG, "qrcodeString: " + qrcodeString);
		return qrcodeString;
	}

	@Override
	public SendFileMessage QRCodeToMessage(String qrcode) {
		Log.d(TAG, "QRCodeToMessage qrcode " + qrcode);
		ServerInfoMessage serverInfoMessage = new ServerInfoMessage();

		JSONTokener jsonParser = new JSONTokener(qrcode);
		JSONObject userJsonObject = null;

		try {
			userJsonObject = (JSONObject) jsonParser.nextValue();
			this.type = userJsonObject.getInt("type");
			
			String server = userJsonObject.getString("server");
			serverInfoMessage = serverInfoMessage.QRCodeToMessage(server);
			this.serverInfoMessage = serverInfoMessage;

			return this;
		} catch (JSONException e) {
			Log.e(TAG, "QRCodeToMessage error. " + e);
		}

		return null;
	}

}
