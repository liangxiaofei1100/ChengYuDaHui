package com.zhaoyan.communication;

import java.text.NumberFormat;
import java.util.Comparator;

import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;


public class HistoryManager {
		//status
		public static final int STATUS_DEFAULT = 0;
		public static final int STATUS_PRE_SEND = 10;
		public static final int STATUS_SENDING = 11;
		public static final int STATUS_SEND_SUCCESS = 12;
		public static final int STATUS_SEND_FAIL = 13;
		
		public static final int STATUS_PRE_RECEIVE = 21;
		public static final int STATUS_RECEIVING = 22;
		public static final int STATUS_RECEIVE_SUCCESS = 23;
		public static final int STATUS_RECEIVE_FAIL = 24;
		
		public static final String HISTORY_URI = "history_uri";
		
		public static final NumberFormat nf = NumberFormat.getPercentInstance();
		
		public static final int TYPE_SEND = 0;
		public static final int TYPE_RECEIVE = 1;
		
		public static final String ME = "ME";
		
		private Context mContext;
		
		public Comparator<HistoryInfo> getDateComparator() {
			return new DateComparator();
		}
		/**
	     * Perform alphabetical comparison of application entry objects.
	     */
	    public static class DateComparator implements Comparator<HistoryInfo> {
	        @Override
	        public int compare(HistoryInfo object1, HistoryInfo object2) {
	        	long date1 = object1.getDate();
				long date2 = object2.getDate();
				if (date1 > date2) {
					return -1;
				} else if (date1 == date2) {
					return 0;
				} else {
					return 1;
				}
	        }
	    };
	    
	    public HistoryManager(Context context){
	    	mContext = context;
	    }
	    
	    public  synchronized void insertToDb(HistoryInfo historyInfo){
	    	InsertThread insertThread = new InsertThread(historyInfo);
	    	insertThread.start();
	    }
	    
	    public void deleteItemFromDb(int id){
	    	Uri uri = Uri.parse(ZhaoYanCommunicationData.History.CONTENT_URI + "/" + id);
			mContext.getContentResolver().delete(uri, null, null);
	    }
	    
	    class InsertThread extends Thread{
	    	HistoryInfo historyInfo = null;
	    	
	    	InsertThread(HistoryInfo historyInfo){
	    		this.historyInfo = historyInfo;
	    	}
	    	
	    	@Override
	    	public void run() {
	    		ContentValues values = new ContentValues();
	    		values.put(ZhaoYanCommunicationData.History.FILE_PATH, historyInfo.getFile().getAbsolutePath());
	    		values.put(ZhaoYanCommunicationData.History.FILE_NAME, historyInfo.getFile().getName());
	    		values.put(ZhaoYanCommunicationData.History.FILE_SIZE, historyInfo.getFile().length());
	    		values.put(ZhaoYanCommunicationData.History.SEND_USERNAME, historyInfo.getSendUserName());
	    		values.put(ZhaoYanCommunicationData.History.RECEIVE_USERNAME, historyInfo.getReceiveUser().getUserName());
	    		values.put(ZhaoYanCommunicationData.History.PROGRESS, historyInfo.getProgress());
	    		values.put(ZhaoYanCommunicationData.History.DATE, historyInfo.getDate());
	    		values.put(ZhaoYanCommunicationData.History.STATUS, historyInfo.getStatus());
	    		values.put(ZhaoYanCommunicationData.History.MSG_TYPE, historyInfo.getMsgType());
	    		
	    		mContext.getContentResolver().insert(ZhaoYanCommunicationData.History.CONTENT_URI, values);
	    	}
	    }
	    
	    public ContentValues getInsertValues(HistoryInfo historyInfo){
	    	ContentValues values = new ContentValues();
	    	values.put(ZhaoYanCommunicationData.History.FILE_PATH, historyInfo.getFile().getAbsolutePath());
    		values.put(ZhaoYanCommunicationData.History.FILE_NAME, historyInfo.getFile().getName());
    		values.put(ZhaoYanCommunicationData.History.FILE_SIZE, historyInfo.getFileSize());
    		values.put(ZhaoYanCommunicationData.History.SEND_USERNAME, historyInfo.getSendUserName());
    		values.put(ZhaoYanCommunicationData.History.RECEIVE_USERNAME, historyInfo.getReceiveUser().getUserName());
    		values.put(ZhaoYanCommunicationData.History.PROGRESS, historyInfo.getProgress());
    		values.put(ZhaoYanCommunicationData.History.DATE, historyInfo.getDate());
    		values.put(ZhaoYanCommunicationData.History.STATUS, historyInfo.getStatus());
    		values.put(ZhaoYanCommunicationData.History.MSG_TYPE, historyInfo.getMsgType());
    		values.put(ZhaoYanCommunicationData.History.FILE_TYPE, historyInfo.getFileType());
    		values.put(ZhaoYanCommunicationData.History.FILE_ICON, historyInfo.getIcon());
    		values.put(ZhaoYanCommunicationData.History.SEND_USER_HEADID, historyInfo.getSendUserHeadId());
    		values.put(ZhaoYanCommunicationData.History.SEND_USER_ICON, historyInfo.getSendUserIcon());
    		
    		return values;
	    }
	    
	    /**
		 * when app finish,modfiy all pre(pre_send/pre_receive) status to fail
		 * status that file transfer history in history stable
		 */
		public static void modifyHistoryDb(Context context) {
			try {
				ContentValues values = new ContentValues();
				// 更新两次，第一次将pre_send,改为send_fail
				values.put(ZhaoYanCommunicationData.History.STATUS, HistoryManager.STATUS_SEND_FAIL);
				String where = ZhaoYanCommunicationData.History.STATUS + "="
						+ HistoryManager.STATUS_PRE_SEND + " or " + 
						ZhaoYanCommunicationData.History.STATUS + "=" + HistoryManager.STATUS_SENDING;
				context.getContentResolver().update(
						ZhaoYanCommunicationData.History.CONTENT_URI,
						values, where, null);

				// 第二次，将pre_receive,改为receive_fail
				values.put(ZhaoYanCommunicationData.History.STATUS,
						HistoryManager.STATUS_RECEIVE_FAIL);
				where = ZhaoYanCommunicationData.History.STATUS + "="
						+ HistoryManager.STATUS_PRE_RECEIVE + " or "
						+ ZhaoYanCommunicationData.History.STATUS + "=" + HistoryManager.STATUS_RECEIVING;
				context.getContentResolver().update(
						ZhaoYanCommunicationData.History.CONTENT_URI,
						values, where , null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
