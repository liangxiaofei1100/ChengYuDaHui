package com.zhaoyan.communication.qrcode;

/**
 * This class is used to translate between QRCode string and QRCode
 * message.</br>
 * 
 * QRCode message is a parsed data, used for other APP code; QRCode String is a
 * unparsed data, used for QRCode image.</br>
 * 
 * Relationship as below:</br>
 * 
 * APP Code <--> QRCode message <--> QRCode string <--> QRCode image <--> QRCode
 * string <--> QRCode message <--> APP Code </br>
 * 
 * @param <T>
 */
public abstract class QRCodeMessage<T extends QRCodeMessage> {
	public int type;
	public static final String SCHEME = "zhaoyan://";

	/**
	 * Translate this QRCode message to a QRCode string. Include scheme string.
	 * 
	 * @return
	 */
	public final String getQRCodeString() {
		return SCHEME + messageToQRcode();
	}

	/**
	 * Translate QRCode message to QRCode string.
	 * 
	 * @return
	 */
	protected abstract String messageToQRcode();

	/**
	 * Translate a QRCode string to QRCodeMessage. Handler scheme string.
	 * 
	 * @param qrcode
	 * @return
	 */
	public final T getQRCodeMessage(String qrcode) {
		if (!qrcode.startsWith(SCHEME)) {
			return null;
		}
		return QRCodeToMessage(qrcode.substring(SCHEME.length()));
	}

	/**
	 * Translate a QRCode string to QRCodeMessage
	 * 
	 * @param qrcode
	 * @return
	 */
	public abstract T QRCodeToMessage(String qrcode);
}
