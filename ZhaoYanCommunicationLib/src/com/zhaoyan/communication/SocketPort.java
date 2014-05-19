package com.zhaoyan.communication;

public class SocketPort {
	/**
	 * To avoid socket port conflict, add different offset in our apps.
	 */
	private static final int PORT_OFFSET = 1000;
	
	public static final int MULTICAST_SEND_PORT = 40006 + PORT_OFFSET;
	public static final int MULTICAST_RECEIVE_PORT = 40007 + PORT_OFFSET;
	public static final int ANDROID_AP_SEND_PORT = 55556 + PORT_OFFSET;
	public static final int ANDROID_AP_RECEIVE_PORT = 55557 + PORT_OFFSET;
	public static final int SEARCH_SERVER_INFO_PORT = 55558 + PORT_OFFSET;
	/** Socket server port */
	public static final int COMMUNICATION_SERVER_PORT = 55555 + PORT_OFFSET;

	public static final int[] FILE_TRANSPORT_PROT = { 55560 + PORT_OFFSET,
			55561 + PORT_OFFSET, 55562 + PORT_OFFSET, 55563 + PORT_OFFSET,
			55564 + PORT_OFFSET, 55565 + PORT_OFFSET,
			55566 + PORT_OFFSET + PORT_OFFSET, 55567 + PORT_OFFSET,
			55568 + PORT_OFFSET, 55569 + PORT_OFFSET };

	public static final int HTTP_SHARE_SERVER_PORT = 60001 + PORT_OFFSET;
}
