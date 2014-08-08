package com.zhaoyan.juyou.game.chengyudahui.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.MainActivity;

public class CopyDBFile {
	private static final String TAG = CopyDBFile.class.getSimpleName();

	public void copyDB(Context mContext, String name) {
		File file = new File(MainActivity.DB_DIR + "/" + name);
		doCopyDB(mContext, file, name);
	}

	public void copyGuoXueDB(Context mContext) {
		File file = new File(MainActivity.GUOXUE_DB_PATH);
		doCopyDB(mContext, file, "guoxue.db");
	}
	
	public void copyWordDB(Context context){
		File file = new File(MainActivity.WORD_PATH);
		doCopyDB(context, file, "word.db");
	}
	
	public void copyKnowledgeFile(Context context){
		File file = new File(MainActivity.KNOWLEDGE_FILES);
		doCopyFiles(context, file, "knowledge1.xml");
	}

	private void doCopyDB(Context context, File desFile, String srcFileName) {
		Log.e(TAG, desFile.getAbsolutePath());
		if (desFile.exists() && desFile.isFile()) {
			Log.d(TAG, desFile.getName() + " is exist");
			return;
		} else if (!desFile.exists()) {
			File file = new File(MainActivity.DB_DIR);
			if (!file.exists() || !file.isDirectory()) {
				file.mkdirs();
			}

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

	private void doCopyFiles(Context context, File desFile, String srcFileName) {
		if (desFile.exists() && desFile.isFile()) {
			Log.d(TAG, desFile.getName() + " is exist");
			return;
		} else if (!desFile.exists()) {
			File file = new File(MainActivity.FILES_DIR);
			if (!file.exists() || !file.isDirectory()) {
				file.mkdirs();
			}

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
