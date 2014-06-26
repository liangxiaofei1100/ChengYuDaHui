package com.zhaoyan.juyou.game.chengyudahui.push;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class PushUtils {
	
	public static void startBind(Context context){
		if (!hasBind(context)) {
			PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY,
					Conf.APIKEY);
			// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
			// PushManager.enableLbs(getApplicationContext());
			
			// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
	        // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
	        // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
			Resources resources = context.getResources();
			String pkgName = context.getPackageName();
	        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
	                context, resources.getIdentifier(
	                        "notification_custom_builder", "layout", pkgName),
	                        resources.getIdentifier("notification_icon", "id", pkgName),
	                        resources.getIdentifier("notification_title", "id", pkgName),
	                resources.getIdentifier("notification_text", "id", pkgName));
	        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
	        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
	                | Notification.DEFAULT_VIBRATE);
	        cBuilder.setStatusbarIcon(context.getApplicationInfo().icon);
	        cBuilder.setLayoutDrawable(resources.getIdentifier(
	                "simple_notification_icon", "drawable", pkgName));
	        PushManager.setNotificationBuilder(context, 1, cBuilder);
		}
	}
	
	// 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
    public static boolean hasBind(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String flag = sp.getString("bind_flag", "");
        if ("ok".equalsIgnoreCase(flag)) {
            return true;
        }
        return false;
    }

    public static void setBind(Context context, boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }
    
}
