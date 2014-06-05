package com.zhaoyan.juyou.game.chengyudahui.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

import com.zhaoyan.juyou.game.chengyudahui.MainActivity;

public class CopyDBFile {
	public void copyDB(Context mContext) {
		File file = new File(MainActivity.DB_PATH);
		if (file.exists() && file.isFile()) {
			Log.d("CopyDB", "the DB file is exist");
			return;
		} else if (!file.exists()) {
			file = new File(MainActivity.DB_DIR);
			if (!file.exists() || !file.isDirectory()) {
				file.mkdirs();
			}
			file = new File(MainActivity.DB_PATH);
			try {
				file.createNewFile();
				byte[] buffer = new byte[4096];
				InputStream inputStream = mContext.getAssets().open(
						"chengyu.db");
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				int count;
				count = inputStream.read(buffer);
				while (count > 0) {
					fileOutputStream.write(buffer);
					count = inputStream.read(buffer);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("CopyDB", "fail exception : " + e.toString());
			}
		}
	}

}
