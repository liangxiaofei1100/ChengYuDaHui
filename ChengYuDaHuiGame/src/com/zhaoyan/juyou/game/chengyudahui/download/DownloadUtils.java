package com.zhaoyan.juyou.game.chengyudahui.download;

import java.io.File;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.zhaoyan.common.util.PreferencesUtils;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.study.story.StoryInfo;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DownloadUtils {
	private static final String TAG = DownloadUtils.class.getSimpleName();
	public DownloadUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean isDownloading(int downloadManagerStatus) {
		return downloadManagerStatus == DownloadManager.STATUS_RUNNING
				|| downloadManagerStatus == DownloadManager.STATUS_PAUSED
				|| downloadManagerStatus == DownloadManager.STATUS_PENDING;
	}
	
	public static long downloadApp(Context context, DownloadManager dm, AppInfo appInfo){
		// Check storage remain size.
		boolean isStorageSizeAvailable = checkStorageRemainSize(context, appInfo.getAppSize());
		if (!isStorageSizeAvailable) {
			return -1;
		}
		
		String remotePath = appInfo.getAppUrl();
		String localPath = DownloadUtils.getLocalFilePath(remotePath);
		if (localPath == null) {
			return -1;
		}
		
		Log.d(TAG, "remotePath:" + (Conf.URL_EX + remotePath));
		Uri downloadUri = Uri.parse(Conf.URL_EX + remotePath);
		Uri localUri = Uri.parse("file://" + localPath);
		
		DownloadManager.Request request = new DownloadManager.Request(downloadUri);
		request.setDestinationUri(localUri);
        request.setTitle("应用下载:" + appInfo.getLabel());
        request.setDescription("zhaoyan desc");
        //下载完毕后，保留通知栏信息
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        request.setMimeType("application/com.chenyu.download.file");
        long downloadId = dm.enqueue(request);
        /** save download id to preferences **/
        PreferencesUtils.putLong(context, Conf.KEY_NAME_DOWNLOAD_ID, downloadId);
        
        return downloadId;
	}

	private static boolean checkStorageRemainSize(Context context, long fileSize) {
		String sdCardPathString = Environment.getExternalStorageDirectory()
				.getPath();
		long aviable = Utils.getAvailableBlockSize(sdCardPathString);
		if (aviable <= fileSize) {
			String fileSizeStr = Utils.getFormatSize(fileSize);
			String availableStr = Utils.getFormatSize(aviable);
			Toast.makeText(
					context,
					"可用空间不足" + "\n" + "文件大小:" + fileSizeStr + "\n" + "可用空间:"
							+ availableStr, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public static long downloadStorys(Context context, DownloadManager dm, StoryInfo info){
		// Check storage remain size.
		boolean isStorageSizeAvailable = checkStorageRemainSize(context,
				info.getSize());
		if (!isStorageSizeAvailable) {
			return -1;
		}
		
		String localPath = getStoryLocalPath(context, info.getFolder(), info.getFileName());
		
		String remotePath = Conf.CLOUD_STORY_DIR + info.getFolder() + "/" + info.getFileName();
		Log.d(TAG, "remoteUrl:" + remotePath);
		Uri downloadUri = Uri.parse(Conf.URL_EX + remotePath);
		Uri localUri = Uri.parse("file://" + localPath);
		
		DownloadManager.Request request = new DownloadManager.Request(downloadUri);
		request.setDestinationUri(localUri);
        //下载完毕后，保留通知栏信息
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setVisibleInDownloadsUi(false);
        request.setMimeType("application/com.chenyu.download.file");
        long downloadId = dm.enqueue(request);
        /** save download id to preferences **/
        return downloadId;
	}
	
	/**
	 * is sdcard space is full</br>
	 * @return local file path
	 */
	public static String getLocalFilePath(String remotePath){
		String sdCardPathString = Environment.getExternalStorageDirectory()
				.getPath();
		if (!new File(sdCardPathString).exists()) {
			new File(sdCardPathString).mkdirs();
		}
		int index = remotePath.lastIndexOf('/');
		String appName = remotePath.substring(index + 1);
		
		String localDir = sdCardPathString + "/" + Conf.ZHAOYAN_DIR + Conf.LOCAL_APP_DIR;
		if (!new File(localDir).exists()) {
			new File(localDir).mkdirs();
		}
		
		String nativePath = localDir +"/" + appName;
		return nativePath;
	}
	
	public static String getStoryLocalPath(Context context, String folder, String name){
		String sdCardPathString = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		String localDir = sdCardPathString + "/" + Conf.ZHAOYAN_DIR + Conf.LOCAL_STORY_DIR + "/" + folder;
		if (!new File(localDir).exists()) {
			new File(localDir).mkdirs();
		}
		
		String nativePath = localDir +"/" + name;
		return nativePath;
	}
	
	public static String getExistStoryLocalPath(Context context, String folder, String name){
		String localpath = getStoryLocalPath(context, folder, name);
		if (new File(localpath).exists()) {
			return localpath;
		} else {
			return null;
		}
	}

}
