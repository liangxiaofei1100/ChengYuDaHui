package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.bae.GetUserInfo;
import com.zhaoyan.juyou.game.chengyudahui.bae.ZhaoYanUser;
import com.zhaoyan.juyou.game.chengyudahui.frontia.BaiduFrontiaUser;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class JiFenMainActivity extends Activity {
	private TextView tv_imei, tv_device, tv_os, tv_phone, tv_mail, tv_name, tv_downloadCount, tv_uploadCount,
			tv_startCount;
	private Button b_edit, downloadBtn, uploadBtn,myAppBtn;
	private BaiduFrontiaUser user;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.jifen_main);
		setupViews();
		Intent intent = getIntent();
		String userName = intent.getStringExtra("user_name");
		
		getUserInfo(userName);
	}

	private void getUserInfo(String userName) {
		GetUserInfo getUserInfo = new GetUserInfo();
		getUserInfo.getUserInfo(userName);
		getUserInfo.setGetUserInfoResultListener(new GetUserInfo.GetUserInfoResultListener() {
			
			@Override
			public void onGetUserInfoSuccess(ZhaoYanUser user) {
				tv_name.setText("账号名：" + user.userName);
				tv_phone.setText("手机号码：" + user.phone);
				tv_mail.setText("邮箱地址：" + user.email);
				tv_downloadCount.setText("金币：" + user.gold);
			}
			
			@Override
			public void onGetUserInfoFail(String message) {
				Toast.makeText(mContext, "获得用户信息失败："+message, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void setupViews() {
		tv_imei = (TextView) findViewById(R.id.imeiTV);
		tv_device = (TextView) findViewById(R.id.deviceTV);
		tv_os = (TextView) findViewById(R.id.osTV);
		tv_phone = (TextView) findViewById(R.id.phoneTV);
		tv_mail = (TextView) findViewById(R.id.mailTV);
		tv_name = (TextView) findViewById(R.id.nameTV);
		tv_downloadCount = (TextView) findViewById(R.id.downloadAppCountTV);
		tv_uploadCount = (TextView) findViewById(R.id.uploadAppCountTV);
		tv_startCount = (TextView) findViewById(R.id.startAppCountTV);
		// tv_password = (TextView)findViewById(R.id.passwordTV);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		user = (BaiduFrontiaUser) bundle.getSerializable("USER");

		tv_imei.setText("设备IMEI：" + user.imei);
		tv_device.setText("设备型号：" + user.device);
		tv_os.setText("系统版本：" + user.os);
		tv_phone.setText("手机号码：" + user.phone);
		tv_mail.setText("邮箱地址：" + user.mail);
		tv_name.setText("账户昵称：" + user.name);
		tv_downloadCount.setText("下载应用：" + user.downloadAppCount + "次");
		tv_uploadCount.setText("上传应用：" + user.uploadAppCount + "次");
		tv_startCount.setText("打开应用：" + user.startAppCount + "次");
		// tv_password.setText("账户密码："+user.password);
		b_edit = (Button) findViewById(R.id.editBTN);
		b_edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//need relisze? please tell me
//				Intent intent = new Intent(JiFenMainActivity.this, EditUserActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("USER", user);
//				intent.putExtras(bundle);
//				startActivityForResult(intent, Conf.REQUEST_CODE1);
			}
		});
		// 下载app按钮
		downloadBtn = (Button) findViewById(R.id.downloadBTN);
		downloadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(JiFenMainActivity.this, GetAppActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("USER", user);
				intent.putExtras(bundle);
				startActivityForResult(intent, Conf.REQUEST_CODE3);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (Conf.REQUEST_CODE1 == requestCode) {
			if (Conf.RESULT_CODE1==resultCode) {

				Bundle bundle = data.getExtras();
				user = (BaiduFrontiaUser) bundle.getSerializable("USER");
				tv_phone.setText("手机号码：" + user.phone);
				tv_mail.setText("邮箱地址：" + user.mail);
				tv_name.setText("账户昵称：" + user.name);
				// tv_mail.setText("账户密码："+user.password);
			}
		}
		else if (Conf.REQUEST_CODE2 == requestCode) {
			if (Conf.RESULT_CODE2==resultCode) {

				Bundle bundle = data.getExtras();
				user = (BaiduFrontiaUser) bundle.getSerializable("USER");
				tv_uploadCount.setText("上传应用：" + user.uploadAppCount+"次");
			}
		}
		else if (Conf.REQUEST_CODE3 == requestCode) {
			if (Conf.RESULT_CODE3==resultCode) {

				Bundle bundle = data.getExtras();
				user = (BaiduFrontiaUser) bundle.getSerializable("USER");
				if(user!=null)
				tv_downloadCount.setText("下载应用：" + user.downloadAppCount+"次");
			}
		}
		else if (Conf.REQUEST_CODE4 == requestCode) {
			if (Conf.RESULT_CODE4==resultCode) {

				Bundle bundle = data.getExtras();
				user = (BaiduFrontiaUser) bundle.getSerializable("USER");
				if(user!=null)
				tv_startCount.setText("打开应用：" + user.startAppCount+"次");
			}
		}
	}

}
