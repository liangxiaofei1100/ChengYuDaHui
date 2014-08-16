package com.zhaoyan.juyou.game.chengyudahui.download;

import com.google.zxing.client.android.CaptureActivity;
import com.zhaoyan.communication.qrcode.ServerInfoMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class ShareAppClientActivity extends ActionBarActivity {
	private Context mContext;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("接收应用");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mContext = this;

		initView();
		setContentView(mTextView);
		launchQRCodeScan();
	}

	private void initView() {
		mTextView = new TextView(mContext);

	}

	private void launchQRCodeScan() {
		Intent intent = new Intent(mContext, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String qrcode = data.getStringExtra(CaptureActivity.EXTRA_RESULT);
			mTextView.setText(qrcode);
		}
	}
}
