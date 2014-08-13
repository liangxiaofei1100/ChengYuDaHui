package com.zhaoyan.juyou.game.chengyudahui.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;

public class ClipboadUtil {

	@SuppressLint("NewApi")
	public static void setText(Context context, CharSequence text) {
		if (android.os.Build.VERSION.SDK_INT > 11) {
			android.content.ClipboardManager c = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			c.setPrimaryClip(ClipData.newPlainText(null, text));
		} else {
			android.text.ClipboardManager c = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			c.setText(text);
		}
	}

}
