package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.view.ProgressWebView;

public class BaikeActivity extends ActionBarActivity {
	private static final String TAG = BaikeActivity.class.getSimpleName();
	
	public static final String KEYWORD = "keyword";
	
	private static final String BAIKE_PX = "http://wapbaike.baidu.com/search?word=";
	private static final String BAIKE_SX = "&type=0&pn=0&rn=10&submit=search";
	
	private ProgressWebView mWebView;
	private ProgressBar mLoadingBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baike_main);
		
		String keyword = "";
		Intent intent = getIntent();
		if (intent != null) {
			keyword = intent.getStringExtra(KEYWORD);
		}
		Log.d(TAG, "keyword:" + keyword);
		setTitle(keyword + "-百度百科");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mWebView = (ProgressWebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);  
		mWebView.getSettings().setSupportZoom(true);  
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);  
		mWebView.requestFocus();  
		
		mWebView.loadUrl(BAIKE_PX + keyword + BAIKE_SX);
		mWebView.setWebViewClient(new MyWebViewClient());  
		
		mLoadingBar = (ProgressBar) findViewById(R.id.bar_loading);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class MyWebViewClient extends WebViewClient{ 
        @Override
        public void onPageFinished(WebView view, String url) {
        	super.onPageFinished(view, url);
        	mLoadingBar.setVisibility(View.GONE);
        }
    }  
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			boolean back = mWebView.canGoBack();
			Log.d(TAG, "back:" + back);
			if (back) {
				mWebView.goBack();
				return true;
			}
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
