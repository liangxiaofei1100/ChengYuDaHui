package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.google.zxing.WriterException;
import com.zhaoyan.common.util.QRCodeEncoder;
import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class QRCodeDisplayActivity extends Activity {
	private static final String TAG = QRCodeDisplayActivity.class
			.getSimpleName();

	public static final String EXTRA_CONTENT = "content";

	private Context mContext;
	private ImageView mQRCodeImageView;

	private String mQRCodeContent;
	private Bitmap mQRCodeBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.qrcode_display);
		initView();

		Intent intent = getIntent();
		mQRCodeContent = intent.getStringExtra(EXTRA_CONTENT);
		if (TextUtils.isEmpty(mQRCodeContent)) {
			Log.d(TAG, "content extra is empty");
			mQRCodeContent = "";
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mQRCodeBitmap == null) {
			try {
				mQRCodeBitmap = QRCodeEncoder.createQRCode(mQRCodeContent, 350);
				mQRCodeImageView.setImageBitmap(mQRCodeBitmap);
			} catch (WriterException e) {
				Log.e(TAG, "create qrcode error " + e);
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseQRCodeBitmap();
	}

	private void initView() {
		mQRCodeImageView = (ImageView) findViewById(R.id.iv_qrcode);
	}

	private void releaseQRCodeBitmap() {
		if (mQRCodeBitmap != null) {
			mQRCodeImageView.setImageDrawable(null);
			mQRCodeBitmap.recycle();
			mQRCodeBitmap = null;
		}
	}

}
