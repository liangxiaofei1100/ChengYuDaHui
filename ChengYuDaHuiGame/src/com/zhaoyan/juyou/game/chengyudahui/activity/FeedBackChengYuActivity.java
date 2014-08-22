package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.utils.FeedbackUtil;
import com.zhaoyan.juyou.game.chengyudahui.utils.FeedbackUtil.FeedbackResultListener;

public class FeedBackChengYuActivity extends BackgroundMusicBaseActivity {
	private static final String TAG = FeedBackChengYuActivity.class
			.getSimpleName();
	public static final String EXTRA_CHENGYU = "chengyu";

	private Context mContext;

	private EditText mMessageEditText;
	private TextView mTitleTextView;
	private String mChengYu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("成语反馈");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_feedback_chengyu);
		mContext = this;

		initView();

		Intent intent = getIntent();
		if (intent != null) {
			mChengYu = intent.getStringExtra(EXTRA_CHENGYU);
			mTitleTextView.setText("反馈成语：" + mChengYu);
		}
	}

	private void initView() {
		mTitleTextView = (TextView) findViewById(R.id.tv_feedback_title);
		mMessageEditText = (EditText) findViewById(R.id.et_feedback_message);
	}

	public void feedback(View view) {
		if (!NetWorkUtil.isNetworkConnected(mContext)) {
			Toast.makeText(mContext, "请连接网络", Toast.LENGTH_SHORT).show();
			return;
		}

		String message = mMessageEditText.getText().toString();
		ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(mContext);
		String username = "unknown user";
		if (account != null) {
			username = account.userName;
		}

		Log.d(TAG, "feedback chengyu = " + mChengYu + ", message = " + message
				+ ", username = " + username);

		FeedbackUtil.feedbackChengYuError(mChengYu, message, username,
				new FeedbackResultListener() {

					@Override
					public void onFeedbackResult(boolean isSuccess) {
						String message = isSuccess ? "您的反馈已成功发送" : "你的发送反馈失败";
						Toast.makeText(getApplicationContext(), message,
								Toast.LENGTH_SHORT).show();
					}
				});
		Toast.makeText(mContext, "感谢您的反馈，正在发送中。。。", Toast.LENGTH_SHORT).show();
		finish();
	}
}
