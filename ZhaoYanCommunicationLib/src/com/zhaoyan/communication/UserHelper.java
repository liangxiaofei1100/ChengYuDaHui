package com.zhaoyan.communication;

import java.util.Arrays;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;
import com.zhaoyan.communication.util.ArrayUtil;

public class UserHelper {
	private static final String TAG = "UserHelper";

	public static final int[] HEAD_IMAGES = { R.drawable.head1,
			R.drawable.head2, R.drawable.head3, R.drawable.head4,
			R.drawable.head5, R.drawable.head6, R.drawable.head7,
			R.drawable.head8, R.drawable.head9 };

	private static final String[] PROJECTION = { ZhaoYanCommunicationData.User._ID,
			ZhaoYanCommunicationData.User.USER_NAME, ZhaoYanCommunicationData.User.USER_ID,
			ZhaoYanCommunicationData.User.HEAD_ID, ZhaoYanCommunicationData.User.THIRD_LOGIN,
			ZhaoYanCommunicationData.User.HEAD_DATA, ZhaoYanCommunicationData.User.IP_ADDR,
			ZhaoYanCommunicationData.User.STATUS, ZhaoYanCommunicationData.User.TYPE, ZhaoYanCommunicationData.User.SSID,
			ZhaoYanCommunicationData.User.NETWORK, ZhaoYanCommunicationData.User.SIGNATURE };

	public static final int getHeadImageResource(int headId) {
		return HEAD_IMAGES[headId];
	}

	/**
	 * Get the set name, if name is not set, return null
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserName(Context context) {
		String name = null;
		UserInfo userInfo = loadLocalUser(context);
		if (userInfo != null) {
			name = userInfo.getUser().getUserName();
		}
		return name;
	}

	private static UserInfo getUserFromCursor(Cursor cursor) {
		// get user.
		User user = new User();
		int id = cursor.getInt(cursor.getColumnIndex(ZhaoYanCommunicationData.User.USER_ID));
		String name = cursor.getString(cursor
				.getColumnIndex(ZhaoYanCommunicationData.User.USER_NAME));
		user.setUserID(id);
		user.setUserName(name);

		// get user info
		UserInfo userInfo = new UserInfo();
		userInfo.setUser(user);

		int headID = cursor.getInt(cursor
				.getColumnIndex(ZhaoYanCommunicationData.User.HEAD_ID));
		int thirdLogin = cursor.getInt(cursor
				.getColumnIndex(ZhaoYanCommunicationData.User.THIRD_LOGIN));
		byte[] headData = cursor.getBlob(cursor
				.getColumnIndex(ZhaoYanCommunicationData.User.HEAD_DATA));
		int type = cursor.getInt(cursor.getColumnIndex(ZhaoYanCommunicationData.User.TYPE));
		String ipAddress = cursor.getString(cursor
				.getColumnIndex(ZhaoYanCommunicationData.User.IP_ADDR));
		String ssid = cursor.getString(cursor
				.getColumnIndex(ZhaoYanCommunicationData.User.SSID));
		int status = cursor
				.getInt(cursor.getColumnIndex(ZhaoYanCommunicationData.User.STATUS));
		int networkType = cursor.getInt(cursor
				.getColumnIndex(ZhaoYanCommunicationData.User.NETWORK));
		String signature = cursor.getString(cursor
				.getColumnIndex(ZhaoYanCommunicationData.User.SIGNATURE));

		userInfo.setHeadId(headID);
		userInfo.setThirdLogin(thirdLogin);
		userInfo.setHeadBitmapData(headData);
		userInfo.setType(type);
		userInfo.setIpAddress(ipAddress);
		userInfo.setSsid(ssid);
		userInfo.setStatus(status);
		userInfo.setNetworkType(networkType);
		userInfo.setSignature(signature);
		return userInfo;
	}

	/**
	 * Load local user from database. If there is no local user, return null.
	 * 
	 * @param context
	 * @return
	 */
	public static UserInfo loadLocalUser(Context context) {
		UserInfo userInfo = null;

		ContentResolver contentResolver = context.getContentResolver();
		String selection = ZhaoYanCommunicationData.User.TYPE + "="
				+ ZhaoYanCommunicationData.User.TYPE_LOCAL;
		Cursor cursor = contentResolver.query(ZhaoYanCommunicationData.User.CONTENT_URI,
				PROJECTION, selection, null, ZhaoYanCommunicationData.User.SORT_ORDER_DEFAULT);
		if (cursor != null) {
			try {
				int count = cursor.getCount();
				if (count == 0) {
					Log.d(TAG, "No Local user");
				} else if (count == 1) {
					if (cursor.moveToFirst()) {
						userInfo = getUserFromCursor(cursor);
					}
				} else {
					Log.e(TAG,
							"loadLocalUser error. There must be one local user at most!");
				}
			} catch (Exception e) {
			} finally {
				cursor.close();
			}
		}
		return userInfo;
	}

	/**
	 * Save the user as local user.
	 * 
	 * @param context
	 * @param userInfo
	 */
	public static synchronized void saveLocalUser(Context context,
			UserInfo userInfo) {
		Log.d(TAG, "saveLocalUser");
		if (!userInfo.isLocal()) {
			throw new IllegalArgumentException(
					"saveLocalUser, this user is not local user.");
		}
		ContentResolver contentResolver = context.getContentResolver();
		String selection = ZhaoYanCommunicationData.User.TYPE + "="
				+ ZhaoYanCommunicationData.User.TYPE_LOCAL;
		Cursor cursor = contentResolver.query(ZhaoYanCommunicationData.User.CONTENT_URI,
				PROJECTION, selection, null, ZhaoYanCommunicationData.User.SORT_ORDER_DEFAULT);
		if (cursor != null) {
			try {
				int count = cursor.getCount();
				if (count == 0) {
					Log.d(TAG, "No Local user. Add local user.");
					contentResolver.insert(ZhaoYanCommunicationData.User.CONTENT_URI,
							getContentValuesFromUserInfo(userInfo));
				} else if (count == 1) {
					Log.d(TAG, "Local user exist. Update local user.");
					if (cursor.moveToFirst()) {
						int id = cursor.getInt(cursor
								.getColumnIndex(ZhaoYanCommunicationData.User._ID));
						updateUserToDatabase(context, userInfo, id);
					} else {
						Log.e(TAG, "saveUser moveToFirst() error.");
					}
				} else {
					Log.e(TAG, "saveUser There must be one local user at most!");
					contentResolver.delete(ZhaoYanCommunicationData.User.CONTENT_URI,
							selection, null);
					contentResolver.insert(ZhaoYanCommunicationData.User.CONTENT_URI,
							getContentValuesFromUserInfo(userInfo));
				}
			} catch (Exception e) {
			} finally {
				cursor.close();
			}
		}
	}

	private static void updateUserToDatabase(Context context,
			UserInfo userInfo, int id) {
		Log.d(TAG, "updateUserToDatabase  userInfo = " + userInfo);
		ContentResolver contentResolver = context.getContentResolver();
		String selection = ZhaoYanCommunicationData.User._ID + "=" + id;
		contentResolver.update(ZhaoYanCommunicationData.User.CONTENT_URI,
				getContentValuesFromUserInfo(userInfo), selection, null);
	}

	/**
	 * Add a remote user to database.
	 * 
	 * @param context
	 * @param userInfo
	 */
	public static void addRemoteUserToDatabase(Context context,
			UserInfo userInfo) {
		if (userInfo.getType() == ZhaoYanCommunicationData.User.TYPE_LOCAL) {
			throw new IllegalArgumentException(
					TAG
							+ "addUserToDatabase, userInfo type must not be TYPE_LOCAL.");
		}
		ContentResolver contentResolver = context.getContentResolver();
		contentResolver.insert(ZhaoYanCommunicationData.User.CONTENT_URI,
				getContentValuesFromUserInfo(userInfo));
	}

	/**
	 * Update user info in database.
	 * 
	 * @param context
	 * @param userInfo
	 */
	public static void updateUserToDataBase(Context context, UserInfo userInfo) {
		ContentResolver contentResolver = context.getContentResolver();
		String selection = ZhaoYanCommunicationData.User.USER_ID + "="
				+ userInfo.getUser().getUserID() + " and "
				+ ZhaoYanCommunicationData.User.USER_NAME + "='"
				+ userInfo.getUser().getUserName() + "'";
		contentResolver.update(ZhaoYanCommunicationData.User.CONTENT_URI,
				getContentValuesFromUserInfo(userInfo), selection, null);
	}

	private static ContentValues getContentValuesFromUserInfo(UserInfo userInfo) {
		ContentValues values = new ContentValues();
		values.put(ZhaoYanCommunicationData.User.USER_ID, userInfo.getUser().getUserID());
		values.put(ZhaoYanCommunicationData.User.USER_NAME, userInfo.getUser().getUserName());
		values.put(ZhaoYanCommunicationData.User.HEAD_ID, userInfo.getHeadId());
		values.put(ZhaoYanCommunicationData.User.THIRD_LOGIN, userInfo.getThirdLogin());

		byte[] headBitmapData = userInfo.getHeadBitmapData();
		if (headBitmapData == null) {
			values.put(ZhaoYanCommunicationData.User.HEAD_DATA, new byte[] {});
		} else {
			values.put(ZhaoYanCommunicationData.User.HEAD_DATA, headBitmapData);
		}

		values.put(ZhaoYanCommunicationData.User.TYPE, userInfo.getType());
		values.put(ZhaoYanCommunicationData.User.IP_ADDR, userInfo.getIpAddress());
		values.put(ZhaoYanCommunicationData.User.SSID, userInfo.getSsid());
		values.put(ZhaoYanCommunicationData.User.STATUS, userInfo.getStatus());
		values.put(ZhaoYanCommunicationData.User.NETWORK, userInfo.getNetworkType());
		values.put(ZhaoYanCommunicationData.User.SIGNATURE, userInfo.getSignature());
		return values;
	}

	/**
	 * Get the user info of the user.
	 * 
	 * @param context
	 * @param user
	 * @return
	 */
	public static UserInfo getUserInfo(Context context, User user) {
		UserInfo userInfo = null;
		ContentResolver contentResolver = context.getContentResolver();
		String selection = ZhaoYanCommunicationData.User.USER_ID + "=" + user.getUserID();
		Cursor cursor = contentResolver.query(ZhaoYanCommunicationData.User.CONTENT_URI,
				PROJECTION, selection, null, ZhaoYanCommunicationData.User.SORT_ORDER_DEFAULT);
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0 && cursor.moveToFirst()) {
					userInfo = getUserFromCursor(cursor);
				}
			} catch (Exception e) {
			} finally {
				cursor.close();
			}
		}
		return userInfo;
	}

	public static void removeAllRemoteConnectedUser(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		String selection = ZhaoYanCommunicationData.User.TYPE + " != "
				+ ZhaoYanCommunicationData.User.TYPE_LOCAL;
		contentResolver.delete(ZhaoYanCommunicationData.User.CONTENT_URI, selection, null);
	}

	/**
	 * Remove a connected remote user.
	 * 
	 * @param context
	 * @param userId
	 */
	public static void removeRemoteConnectedUser(Context context, int userId) {
		ContentResolver contentResolver = context.getContentResolver();
		String selection = ZhaoYanCommunicationData.User.TYPE + "!="
				+ ZhaoYanCommunicationData.User.TYPE_LOCAL + " and " + ZhaoYanCommunicationData.User.USER_ID
				+ "=" + userId;
		contentResolver.delete(ZhaoYanCommunicationData.User.CONTENT_URI, selection, null);
	}

	public static User[] sortUsersById(Map<Integer, User> users) {
		if (users.isEmpty()) {
			throw new IllegalArgumentException(
					"sortUsersById(), users is empty.");
		}
		int i = 0;
		int[] userIdSorted = new int[users.size()];
		for (Map.Entry<Integer, User> entry : users.entrySet()) {
			userIdSorted[i] = entry.getKey();
			i++;
		}
		Arrays.sort(userIdSorted);

		User[] userSorted = new User[users.size()];
		for (int j = 0; j < userIdSorted.length; j++) {
			userSorted[j] = users.get(userIdSorted[j]);
		}
		return userSorted;
	}

	public static byte[] encodeUser(User user) {
		byte[] data = ArrayUtil.objectToByteArray(user);
		return data;
	}

	public static User decodeUser(byte[] data) {
		User user = (User) ArrayUtil.byteArrayToObject(data);
		return user;
	}

	public static UserInfo getUserInfo(Context context, String selection) {
		UserInfo userInfo = null;
		Cursor cursor = context.getContentResolver().query(
				ZhaoYanCommunicationData.User.CONTENT_URI, PROJECTION, selection, null,
				ZhaoYanCommunicationData.User.SORT_ORDER_DEFAULT);
		if (cursor != null) {
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				userInfo = getUserFromCursor(cursor);
			}
			cursor.close();
		}
		return userInfo;
	}
}
