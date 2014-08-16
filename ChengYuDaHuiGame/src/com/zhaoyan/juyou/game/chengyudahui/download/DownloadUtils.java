package com.zhaoyan.juyou.game.chengyudahui.download;

import java.io.File;

import com.baidu.frontia.FrontiaFile;
import com.zhaoyan.common.util.PreferencesUtils;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DownloadUtils {
	private static final String TAG = DownloadUtils.class.getSimpleName();
	public DownloadUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static long downloadApp(Context context, DownloadManager dm, AppInfo appInfo){
		String remotePath = appInfo.getAppUrl();
		String localPath = DownloadUtils.getLocalFilePath(context, appInfo.getAppSize(), remotePath);
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
	
	public static long downloadAudio(Context context, DownloadManager dm, long size, String name){
		String remoteUrl = Conf.STORY_URL_EX + name;
		
		//需要对sdcard剩余空间作判断
		String localPath = getAUdioLocalPath(context, size, name);
		
		Log.d(TAG, "remoteUrl:" + remoteUrl);
		Uri downloadUri = Uri.parse(remoteUrl);
		Uri localUri = Uri.parse("file://" + localPath);
		
		DownloadManager.Request request = new DownloadManager.Request(downloadUri);
		request.setDestinationUri(localUri);
        request.setTitle("应用下载:" + name);
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
	 * @param context
	 * @param filesize
	 * @return local file path
	 */
	public static String getLocalFilePath(Context context, long filesize, String remotePath){
		String sdCardPathString = Environment.getExternalStorageDirectory()
				.getPath();
		if (!new File(sdCardPathString).exists()) {
			new File(sdCardPathString).mkdirs();
		}
		long aviable = Utils.getAvailableBlockSize(sdCardPathString);
		if (aviable <= filesize) {
			String fileSizeStr = Utils.getFormatSize(filesize);
			String availableStr = Utils.getFormatSize(aviable);
			Toast.makeText(
					context,
					"可用空间不足" + "\n" + "文件大小:" + fileSizeStr + "\n" + "可用空间:"
							+ availableStr, Toast.LENGTH_SHORT).show();
			return null;
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
	
	public static String getAUdioLocalPath(Context context, long size, String name){
		String sdCardPathString = Environment.getExternalStorageDirectory().getAbsolutePath();
		long aviable = Utils.getAvailableBlockSize(sdCardPathString);
		if (aviable <= size) {
			String fileSizeStr = Utils.getFormatSize(size);
			String availableStr = Utils.getFormatSize(aviable);
			Toast.makeText(
					context,
					"可用空间不足" + "\n" + "文件大小:" + fileSizeStr + "\n" + "可用空间:"
							+ availableStr, Toast.LENGTH_SHORT).show();
			return null;
		}
		
		String localDir = sdCardPathString + "/" + Conf.ZHAOYAN_DIR + Conf.LOCAL_STORY_DIR;
		if (!new File(localDir).exists()) {
			new File(localDir).mkdirs();
		}
		
		String nativePath = localDir +"/" + name;
		return nativePath;
	}

}
