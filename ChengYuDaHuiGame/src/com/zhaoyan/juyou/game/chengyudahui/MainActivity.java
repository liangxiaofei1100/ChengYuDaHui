package com.zhaoyan.juyou.game.chengyudahui;

import com.zhaoyan.juyou.game.chengyudahui.activity.GetGoldActivity;

import java.text.SimpleDateFormat;

import com.zhaoyan.juyou.game.chengyudahui.db.CopyDBFile;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;
import com.zhaoyan.juyou.game.chengyudahui.paint.PaintGameActivty;
import com.zhaoyan.juyou.game.chengyudahui.speakgame.SpeakGameActivity;
import com.zhaoyan.juyou.game.chengyudahui.study.StudyActivity;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	public static String DB_PATH = "", DB_DIR, FILES_DIR;
	public static String GUOXUE_DB_PATH = "";
	public static String KNOWLEDGE_FILES = "";
	public static Button mStudyBtn, mSpeakBtn, mPaintBtn, mScoreBtn;
	public static int DB_NUMBER = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JuYouApplication.initApplication(getApplicationContext());
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		DB_DIR = this.getFilesDir().getAbsolutePath() + "/database";
		DB_PATH = DB_DIR + "/chengyu.db";
		new CopyDBFile().copyDB(this);
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
		if (mStudyBtn != null) {
			mStudyBtn.setOnClickListener(this);
			mSpeakBtn.setOnClickListener(this);
			mPaintBtn.setOnClickListener(this);
			mScoreBtn.setOnClickListener(this);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (DB_NUMBER == -1)
			getChengyuNumber();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("SimpleDateFormat") public static String getDate() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return sDateFormat.format(new java.util.Date());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			mStudyBtn = (Button) rootView.findViewById(R.id.study_btn);
			mSpeakBtn = (Button) rootView.findViewById(R.id.speak_btn);
			mPaintBtn = (Button) rootView.findViewById(R.id.paint_btn);
			mScoreBtn = (Button) rootView.findViewById(R.id.score_btn);
			return rootView;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (arg0.getId()) {
		case R.id.study_btn:
			intent.setClass(this, StudyActivity.class);
			break;
		case R.id.speak_btn:
			intent.setClass(this, SpeakGameActivity.class);
			break;
		case R.id.paint_btn:
			intent.setClass(this, PaintGameActivty.class);
			break;
		case R.id.score_btn:
			intent.setClass(this, GetGoldActivity.class);
			break;

		default:
			intent = null;
			break;
		}
		if (intent != null)
			this.startActivity(intent);
	}

	private void getChengyuNumber() {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				Cursor cursor = getContentResolver().query(
						ChengyuColums.CONTENT_URI, new String[] { "_id" },
						null, null, null);
				if (cursor != null) {
					DB_NUMBER = cursor.getCount();
					cursor.close();
				} else {
					DB_NUMBER = 30000;
				}
				Log.d(MainActivity.class.getSimpleName(),
						"the Chengyu number is : " + DB_NUMBER);
			}
		}.start();

	}
}
