package com.zhaoyan.juyou.game.chengyudahui;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.baidu.frontia.FrontiaApplication;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zhaoyan.communication.cache.BitmapLruCache;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.push.PushUtils;
import com.zhaoyan.juyou.game.chengyudahui.utils.ServiceUtil;

public class JuYouApplication extends FrontiaApplication {
	private static final String TAG = "JuYouApplication";
	private static Context mContext;
	private BitmapLruCache mCache = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		mContext = getApplicationContext();
		initApplication(getApplicationContext());

		initImageLoader(getApplicationContext());
		initBitmapCache();
	}

	public static Context getApplicContext() {
		return mContext;
	}

	/**
	 * Notice, call this not only in application's {@link #onCreate()}, but also
	 * in the first activity's onCreate(). Because application's
	 * {@link #onCreate()} will not be call every time when we launch first
	 * activity.
	 * 
	 * @param context
	 */
	public static synchronized void initApplication(Context context) {
		Log.d(TAG, "initApplication");

		// bind baidu push service
		// PushUtils.startBind(context);

		Intent intent = new Intent(JuYouService.ACTION_START_COMMUNICATION);
		intent.setClass(context, JuYouService.class);
		context.startService(intent);
	}

	public static synchronized void quitApplication(Context context) {
		Log.d(TAG, "quitApplication");
		Intent intent = new Intent(JuYouService.ACTION_STOP_COMMUNICATION);
		intent.setClass(context, JuYouService.class);
		context.startService(intent);
	}
	
	/** 初始化ImageLoader */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"zhaoyan/Cache");// 获取到缓存的目录地址
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	/**
	 * bitmap from network,download to ZhaoYanCache dir
	 */
	private void initBitmapCache() {
		// If we have external storage use it for the disk cache. Otherwise we
		// use
		// the cache dir
		File cacheLocation = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			cacheLocation = new File(Environment.getExternalStorageDirectory()
					+ "/ZhaoYanCache");
		} else {
			cacheLocation = new File(getFilesDir() + "/ZhaoYanCache");
		}

		if (!cacheLocation.exists()) {
			cacheLocation.mkdirs();
		}

		BitmapLruCache.Builder builder = new BitmapLruCache.Builder(this);
		builder.setMemoryCacheEnabled(true)
				.setMemoryCacheMaxSizeUsingHeapSize();
		builder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheLocation);
		mCache = builder.build();
	}

	public BitmapLruCache getBitmapCache() {
		return mCache;
	}

	public static JuYouApplication getApplication(Context context) {
		return (JuYouApplication) context.getApplicationContext();
	}

}
