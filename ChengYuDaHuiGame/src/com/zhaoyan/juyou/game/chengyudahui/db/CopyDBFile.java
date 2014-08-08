package com.zhaoyan.juyou.game.chengyudahui.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.DBConfig;

public class CopyDBFile {
	private static final String TAG = CopyDBFile.class.getSimpleName();

	public static void copyDB(Context mContext, String name) {
		File file = new File(DBConfig.DATABASE_DIR + "/" + name);
		doCopyDB(mContext, file, name);
	}
	
	public static void copyFile(Context context, String name){
		File file = new File(DBConfig.FILES_DIR + "/" + name);
		doCopyFiles(context, file, name);
	}

	private static void doCopyDB(Context context, File desFile, String srcFileName) {
		Log.d(TAG, "doCopyDB:" + desFile.getAbsolutePath());
		if (desFile.exists() && desFile.isFile()) {
			Log.d(TAG, desFile.getName() + " is exist");
			return;
		} else if (!desFile.exists()) {

			FileOutputStream fos = null;
			try {
				desFile.createNewFile();
				byte[] buffer = new byte[4096];
				InputStream inputStream = context.getAssets().open(srcFileName);
				fos = new FileOutputStream(desFile);
				int count;
				count = inputStream.read(buffer);
				while (count > 0) {
					fos.write(buffer);
					count = inputStream.read(buffer);
				}

				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "doCopyDB.fail exception : " + e.toString());
			}
		}
	}

	private static void doCopyFiles(Context context, File desFile, String srcFileName) {
		if (desFile.exists() && desFile.isFile()) {
			Log.d(TAG, desFile.getName() + " is exist");
			return;
		} else if (!desFile.exists()) {

			FileOutputStream fos = null;
			try {
				desFile.createNewFile();
				byte[] buffer = new byte[4096];
				InputStream inputStream = context.getAssets().open(srcFileName);
				fos = new FileOutputStream(desFile);
				int count;
				count = inputStream.read(buffer);
				while (count > 0) {
					fos.write(buffer);
					count = inputStream.read(buffer);
				}

				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "doCopyFiles.fail exception : " + e.toString());
			}
		}
	}

}
