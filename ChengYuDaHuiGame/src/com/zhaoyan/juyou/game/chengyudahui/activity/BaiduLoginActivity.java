package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaData;
import com.baidu.frontia.FrontiaQuery;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener;
import com.baidu.frontia.api.FrontiaStorageListener.DataInfoListener;
import com.baidu.frontia.api.FrontiaStorageListener.DataOperationListener;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.frontia.BaiduFrontiaUser;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;

public class BaiduLoginActivity extends Activity {
	private static final String TAG = "MainActivity";
	private FrontiaStorage mCloudStorage;
	private FrontiaData frontiaData;
	private Button register, query, logIn, delete;
	private TextView baiduInfo;
	private final String Tag = "baiduTest";
	private ProgressDialog progressDialog;
	private BaiduFrontiaUser user;
	private EditText accountEt, passwordEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.baidu_login);
		
		// 使用百度分配给应用的APIKEY（与APP一一对应），初始化百度云盘
		Frontia.init(this.getApplicationContext(), Conf.APIKEY);
		mCloudStorage = Frontia.getStorage();
		frontiaData = new FrontiaData();
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		user = new BaiduFrontiaUser();
		user.imei = telephonyManager.getDeviceId();
		setupViews();
		creatDownloadDirection();
		// checkIfnewUser();
		
	}

	private void setupViews() {
	//	delete = (Button) findViewById(R.id.deleteBtn);
	//	query = (Button) findViewById(R.id.checkBtn);
		progressDialog = new ProgressDialog(BaiduLoginActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在连接网络，请稍等……");
	//	baiduInfo = (TextView) findViewById(R.id.baiduInfoTV);
		register = (Button) findViewById(R.id.register);
		logIn = (Button) findViewById(R.id.logoIn);
		accountEt = (EditText) findViewById(R.id.accountEt);
		passwordEt = (EditText) findViewById(R.id.passwordEt);
		// 读取并显示终端信息
		accountEt.setText("IMEI_" + user.imei);

		// 注册按钮，向baidu提交账号信息
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkIfnewUserBeforeRegister();
			}
		});
		// 登录按钮，登录账户
		logIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				checkUserInfoBeforeLogin();
				// 注册成功后直接进入登陆界面
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("USER", user);
				intent.putExtras(bundle);
				intent.setClass(BaiduLoginActivity.this, JiFenMainActivity.class);
				startActivity(intent);
			}
		});
		
/*
		// 从baidu云盘删除所有账号信息
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteData();
			}
		});

		// 查询所有账户信息
		query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				queryUserInfo();
			}
		});
		*/
	}

	private void creatDownloadDirection() {
		// 创建下载目录
		try {
			File downloadDir = new File(Environment.getExternalStorageDirectory().getPath() + Conf.LOCAL_APP_DOWNLOAD_PATH);
			String remotePathString = Environment.getExternalStorageDirectory().getPath() + Conf.LOCAL_APP_DOWNLOAD_PATH;
			if (!downloadDir.exists())
				downloadDir.mkdirs();
		} catch (Exception e) {
			Toast.makeText(this, "创建下载目录失败，请检查sdcard是否安装正常！", Toast.LENGTH_LONG).show();
		}
	}

	public void checkUserInfoBeforeLogin() {
		// 首先检验该用户是否已经注册过，没有注册则提示注册；已经注册则验证密码；
		FrontiaQuery query = new FrontiaQuery();
		query.equals("IMEI", user.imei);
		progressDialog.show();
		mCloudStorage.findData(query, new DataInfoListener() {

			@Override
			public void onSuccess(List<FrontiaData> dataList) {
				Log.i(Tag, "saveUserInfo---->query账户信息成功！" + "共查询到" + dataList.size() + "个结果");
				if (dataList.size() == 1) {
					frontiaData = dataList.get(0);
					progressDialog.dismiss();
					if (frontiaData.get("PASSWORD").toString().equals(passwordEt.getText().toString())) {
						Toast toast = Toast.makeText(BaiduLoginActivity.this, "登录成功", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						frontiaData = dataList.get(0);
						user.device = frontiaData.get("DEVICE").toString();
						user.os = frontiaData.get("OS").toString();
						user.phone = frontiaData.get("PHONE").toString();
						user.mail = frontiaData.get("MAIL").toString();
						user.name = frontiaData.get("NAME").toString();
						user.password = frontiaData.get("PASSWORD").toString();
						user.downloadAppCount = frontiaData.get("DOWNLOAD_COUNT").toString();
						user.uploadAppCount = frontiaData.get("UPLOAD_COUNT").toString();
						user.startAppCount = frontiaData.get("START_COUNT").toString();
						// 进入登陆界面
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("USER", user);
						intent.putExtras(bundle);
						intent.setClass(BaiduLoginActivity.this, JiFenMainActivity.class);
						startActivity(intent);
					} else {
						Toast toast = Toast.makeText(BaiduLoginActivity.this, "密码错误，登陆失败！", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				} else if (dataList.size() > 1) {
					Toast toast = Toast.makeText(BaiduLoginActivity.this, "服务器数据出错！！！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					progressDialog.dismiss();
					Toast toast = Toast.makeText(BaiduLoginActivity.this, "用户未注册，请先注册！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				progressDialog.dismiss();
				Log.e(TAG, "errCode:" + errCode + ",errMsg:" + errMsg);
				Toast toast = Toast.makeText(BaiduLoginActivity.this, "网络出错，请确保网络正常连接后再使用软件！"
						+ "\n" + "errCode:" + errCode + ",errMsg:" + errMsg, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				Log.i(Tag, "saveUserInfo---->query账户信息失败！");
			}
		});

	}

	public void checkIfnewUserBeforeRegister() {
		// 首先查询该设备是否已注册过账号，是则提示已注册，否则向百度云盘写入账号信息，并提示注册成功
		FrontiaQuery query = new FrontiaQuery();
		query.equals("IMEI", user.imei);
		progressDialog.show();
		mCloudStorage.findData(query, new DataInfoListener() {

			@Override
			public void onSuccess(List<FrontiaData> dataList) {
				Log.i(Tag, "saveUserInfo---->query账户信息成功！" + "共查询到" + dataList.size() + "个结果");
				if (dataList.size() >= 1) {
					progressDialog.dismiss();
					Toast toast = Toast.makeText(BaiduLoginActivity.this, "您已经注册过账号，请直接登录", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					logIn.setEnabled(true);
					register.setEnabled(false);
				} else {
					registerUserInfo();
				}

			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				progressDialog.dismiss();
				Toast toast = Toast.makeText(BaiduLoginActivity.this, "网络出错，请确保网络正常连接后再使用软件！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				Log.i(Tag, "saveUserInfo---->query账户信息失败！");
			}
		});

	}

	public void registerUserInfo() {
		user.device = android.os.Build.MODEL;
		user.os = android.os.Build.VERSION.RELEASE;
		user.phone = "";
		user.mail = "";
		user.name = "";
		user.password = "";
		user.downloadAppCount = "0";
		user.uploadAppCount = "0";
		user.startAppCount = "0";

		frontiaData.put("IMEI", user.imei);
		frontiaData.put("DEVICE", user.device);// 手机型号
		frontiaData.put("OS", user.os);// 系统版本
		frontiaData.put("PHONE", user.phone);// 手机号
		frontiaData.put("MAIL", user.mail); // 邮箱
		frontiaData.put("NAME", user.name); // 昵称
		frontiaData.put("PASSWORD", user.password); // 密码，默认为空
		frontiaData.put("DOWNLOAD_COUNT", user.downloadAppCount); //
		frontiaData.put("UPLOAD_COUNT", user.uploadAppCount); //
		frontiaData.put("START_COUNT", user.startAppCount); //
		mCloudStorage.insertData(frontiaData, new FrontiaStorageListener.DataInsertListener() {

			@Override
			public void onSuccess() {
				progressDialog.dismiss();
				Log.i(Tag, "saveUserInfo---->保存了一个用户IMEI信息！");
				Toast toast = Toast.makeText(BaiduLoginActivity.this, "恭喜你，注册成功！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				// 注册成功后直接进入登陆界面
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("USER", user);
				intent.putExtras(bundle);
				intent.setClass(BaiduLoginActivity.this, JiFenMainActivity.class);
				startActivity(intent);
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				progressDialog.dismiss();
				Toast toast = Toast.makeText(BaiduLoginActivity.this, "网络出错，注册失败！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}

		});
	}

	public void queryUserInfo() {
		progressDialog.show();
		// 从百度云盘读取账户信息
		// 空的FrontiaQuery表示query所有的数据(具有可读权限数据才能被查到)
		FrontiaQuery query = new FrontiaQuery();

		mCloudStorage.findData(query, new DataInfoListener() {

			@Override
			public void onSuccess(List<FrontiaData> dataList) {
				progressDialog.dismiss();

				StringBuilder sb = new StringBuilder();
				int i = 0;
				for (FrontiaData d : dataList) {
					sb.append(i).append(":").append(d.toJSON().toString()).append("\n");
					i++;
				}
				if (dataList.size() > 0) {
					baiduInfo.setText(sb.toString());
				} else {
					baiduInfo.setText("百度云盘现在没有存储数据！");
				}
				Toast toast = Toast.makeText(BaiduLoginActivity.this, "查询数据成功！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				progressDialog.dismiss();
				Toast toast = Toast.makeText(BaiduLoginActivity.this, "网络出错，查询数据失败！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

			}
		});

	}

	protected void deleteData() {
		progressDialog.show();
		FrontiaQuery query = new FrontiaQuery();
		mCloudStorage.deleteData(query, new DataOperationListener() {

			@Override
			public void onSuccess(long count) {
				progressDialog.dismiss();
				Toast toast = Toast.makeText(BaiduLoginActivity.this, "删除数据成功！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				progressDialog.dismiss();
				Toast toast = Toast.makeText(BaiduLoginActivity.this, "删除数据失败！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
