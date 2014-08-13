package com.zhaoyan.juyou.game.chengyudahui.utils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zhaoyan.juyou.game.chengyudahui.activity.BaikeActivity;
import com.zhaoyan.juyou.game.chengyudahui.knowledge.GuessGameOfPictureActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StatFs;
import android.view.Window;
import android.view.WindowManager;

public class Utils {

	/**
	 * byte convert
	 * @param size like 3232332
	 * @return like 3.23M
	 */
	public static String getFormatSize(long size){
		DecimalFormat df = new DecimalFormat("###.##");
		float f;
		if (size >= 1024 * 1024 * 1024){
			f = (float) ((float) size / (float) (1024 * 1024 * 1024));
		    return (df.format(new Float(f).doubleValue())+"GB");
		}else if (size >= 1024 * 1024) {
			f = (float) ((float) size / (float) (1024 * 1024));
		    return (df.format(new Float(f).doubleValue())+"MB");
		}else if (size >= 1024) {
			f = (float) ((float) size / (float) 1024);
			return (df.format(Float.valueOf(f).doubleValue())+"KB");
		}else {
			return String.valueOf((int)size) + "B";
		}
	}
	
	/**get app install date*/
	public static String getFormatDate(long date){
		return getFormatDate(new Date(date));
	}
	
	public static String getFormatDate(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = format.format(date);
		return dateString;
	}
	
	/**set dialog dismiss or not*/
	public static void setDialogDismiss(DialogInterface dialog, boolean dismiss){
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, dismiss);
			dialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * force show virtual menu key </br>
	 * must call after setContentView() 
	 * @param window you can use getWindow()
	 */
	public static void forceShowMenuKey(Window window){
		try {
			window.addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static long getAvailableBlockSize(String path) {
		StatFs stat = new StatFs(path);
		long blocksize = stat.getBlockSize();
		long availableblocks = stat.getAvailableBlocks();
		return availableblocks * blocksize;
	}
	
	/**
	 * bytes tp chars
	 * 
	 * @param bytes
	 * @return
	 */
	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}
	
	/**
	 * get n int random nums from a number
	 * (从指定范围内生成n个不重复的随机数)
	 * @param n random count
	 * @param totalNum  指定范围的最大数
	 * @return 不重复的HashSet集合
	 */
	public static Set<Integer> getRandomNums(int n, int totalNum){
		Set<Integer> set = new HashSet<Integer>();
		Random random  = new Random();
		for (int i = 0; i < n; i++) {
			int num = random.nextInt(totalNum);
			while (!set.add(num)) {
				num = random.nextInt(totalNum);
			}
		}
		return set;
	}
	
	/**
	 * is the date in today
	 * @param date
	 * @return
	 */
	public static boolean isToday(long date){
		long todayStartTime = getStartTime();
		long todayEndTime = getEndTime();
		return date >= todayStartTime && date < todayEndTime;
	}
	
	/**
	 * get today start time,long
	 * @return
	 */
	public static Long getStartTime(){  
        Calendar todayStart = Calendar.getInstance();  
        todayStart.set(Calendar.HOUR, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0);  
        return todayStart.getTime().getTime();  
    }  
      
	/**
	 * get today start time,long
	 * @return
	 */
	public static Long getEndTime(){  
        Calendar todayEnd = Calendar.getInstance();  
        todayEnd.set(Calendar.HOUR, 23);  
        todayEnd.set(Calendar.MINUTE, 59);  
        todayEnd.set(Calendar.SECOND, 59);  
        todayEnd.set(Calendar.MILLISECOND, 999);  
        return todayEnd.getTime().getTime();  
    }  
	
	/**
	 * start baidu baike activity
	 * @param context context for start activity
	 * @param keyword 
	 */
	public static void startBaikeActivity(Context context, String keyword){
		Intent intent = new Intent();
		intent.setClass(context, BaikeActivity.class);
		intent.putExtra(BaikeActivity.KEYWORD, keyword);
		context.startActivity(intent);
	}
	
}
