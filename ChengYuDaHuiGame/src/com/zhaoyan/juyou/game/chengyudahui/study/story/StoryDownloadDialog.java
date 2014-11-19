package com.zhaoyan.juyou.game.chengyudahui.study.story;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.view.Effectstype;
import com.zhaoyan.juyou.game.chengyudahui.view.NiftyDialogBuilder;

public class StoryDownloadDialog {

	private NiftyDialogBuilder mBuilder;
	private TextView mTitleView, mPercentView;
	private ProgressBar mDownloadBar;
	
	private View mDownloadView, mResultView;
	private TextView mResultMsg;
	private CheckBox mCheckBox;
	
	private StoryInfo mStoryInfo;
	
	private OnDownloadOverListener mDownloadOverListener;
	public interface OnDownloadOverListener{
		void downloadOver(StoryInfo info);
	}
	
	public void setOnDwonloadOverListener(OnDownloadOverListener listener){
		mDownloadOverListener = listener;
	}
	
	public StoryDownloadDialog(Context context, StoryInfo info){
		mStoryInfo = info;
		View customView = LayoutInflater.from(context).inflate(R.layout.story_custom_dl_dialog, null);
		
		mDownloadView = customView.findViewById(R.id.scdd_rl_downloading);
		mTitleView = (TextView) customView.findViewById(R.id.scdd_tv_msg);
		mPercentView = (TextView) customView.findViewById(R.id.scdd_tv_percent);
		mDownloadBar = (ProgressBar) customView.findViewById(R.id.scdd_bar_download);
		mDownloadBar.setMax(100);
		mTitleView.setText(info.getTitle());
		mPercentView.setText("0%");
		
		mResultView = customView.findViewById(R.id.scdd_rl_result);
		mResultMsg = (TextView) customView.findViewById(R.id.scdd_tv_result);
		mCheckBox = (CheckBox) customView.findViewById(R.id.scdd_checkbox);
		
		mBuilder = new NiftyDialogBuilder(context, R.style.dialog_untran);
		mBuilder.withTitle("故事下载");
		mBuilder.withDuration(0);
		mBuilder.isCancelableOnTouchOutside(false);
		mBuilder.withEffect(Effectstype.FadeIn);
		mBuilder.withMessage(null);
		mBuilder.withTipMessage(null);
		mBuilder.setCustomView(customView, context);
		mBuilder.withButton1Text("取消");
		mBuilder.setButton1Click(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBuilder.dismiss();
			}
		});
	}
	
	public void updateProgress(int progress){
		mDownloadBar.setProgress(progress);
		mPercentView.setText(progress + "%");
	}
	
	public void downloadOver(String resultMsg, String localPath){
		mDownloadView.setVisibility(View.GONE);
		mResultView.setVisibility(View.VISIBLE);
		mResultMsg.setText(resultMsg);
		
		mStoryInfo.setLocalPath(localPath);
		
		mBuilder.withButton1Text(null);
		mBuilder.withButton2Text("播放");
		mBuilder.setButton2Click(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBuilder.cancel();
				mDownloadOverListener.downloadOver(mStoryInfo);
			}
		});
	}
	
	public void show(){
		if (mBuilder != null) {
			mBuilder.show();
		}
	}
	
	public void cancel(){
		if (mBuilder != null) {
			mBuilder.cancel();
			mBuilder = null;
		}
	}
}
