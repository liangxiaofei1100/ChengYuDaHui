package com.zhaoyan.juyou.game.chengyudahui.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.angel.devil.view.AsyncImageView;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.frontia.AppInfo;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;
import com.zhaoyan.juyou.game.chengyudahui.frontia.GetAppListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class GetAppAdapter extends BaseAdapter {
	private static final String TAG = "GetAppAdapter";
	private List<AppInfo> mDataList = new ArrayList<AppInfo>();
	private LayoutInflater mInflater;
	private ButtonClickListener mClickListener = new ButtonClickListener();
	
	private Context mContext;
	
	public GetAppAdapter(List<AppInfo> list, Context context){
		mContext = context;
		mDataList = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder = null;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.get_app_list_item, null);
			holder = new ViewHolder();
			holder.imageView = (AsyncImageView) view.findViewById(R.id.iv_app_icon);
			holder.appLabelView = (TextView) view.findViewById(R.id.tv_app_label);
			holder.appInfoView = (TextView) view.findViewById(R.id.tv_app_info);
			holder.infoView = (TextView) view.findViewById(R.id.tv_info);
			holder.downloadBtn = (Button) view.findViewById(R.id.btn_download);
			holder.downloadBtn.setOnClickListener(mClickListener);
			
			holder.downloadView = view.findViewById(R.id.rl_downloading);
			holder.progressView = (TextView) view.findViewById(R.id.tv_dl_progress);
//			holder.percentView = (TextView) view.findViewById(R.id.tv_dl_percent);
			holder.barView = (ProgressBar) view.findViewById(R.id.bar_downloading);
			holder.barView.setMax(100);
			
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		Log.d(TAG, "getView:mDataList.size():" + mDataList.size());
		AppInfo appInfo = mDataList.get(position);
		
		int status = appInfo.getStatus();
		
		MsgData msgData = new MsgData(position, status);
		holder.downloadBtn.setTag(msgData);
		
		holder.appLabelView.setText(appInfo.getLabel());
		holder.infoView.setText(appInfo.getTitle());
		holder.imageView.setDefaultImageResource(R.drawable.ic_launcher);
		holder.imageView.setPath(appInfo.getIconUrl());
		
		String size = Utils.getFormatSize(appInfo.getAppSize());
		holder.appInfoView.setText(size);
		
		switch (status) {
		case Conf.NOT_DOWNLOAD:
			holder.downloadView.setVisibility(View.GONE);
			holder.appInfoView.setVisibility(View.VISIBLE);
			holder.downloadBtn.setText("下载");
			holder.appInfoView.setText(size);
			break;
		case  Conf.DOWNLOADING:
			holder.downloadView.setVisibility(View.VISIBLE);
			holder.appInfoView.setVisibility(View.GONE);
			holder.downloadBtn.setText("取消");
			int percent = appInfo.getPercent();
			Log.d(TAG, "percent:" + percent);
//			holder.downloadBtn.setProgress(percent);
//			holder.downloadBtn.setLoadingText(percent + "%");
			
			String progress = Utils.getFormatSize(appInfo.getProgressBytes());
			holder.progressView.setText(progress);
//			holder.percentView.setText(percent + "%");
			holder.barView.setProgress(percent);
			break;
		case Conf.DOWNLOADED:
			holder.downloadView.setVisibility(View.GONE);
			holder.appInfoView.setVisibility(View.VISIBLE);
//			holder.downloadBtn.setProgress(appInfo.getPercent());
//			holder.downloadBtn.setCompleteText("安装");
			holder.downloadBtn.setText("安装");
			holder.appInfoView.setText(size);
			break;
		case Conf.INSTALLED:
			holder.downloadView.setVisibility(View.GONE);
			holder.appInfoView.setVisibility(View.VISIBLE);
			holder.downloadBtn.setText("打开");
			holder.appInfoView.setText(size);
			break;
		case Conf.NEED_UDPATE:
			holder.downloadView.setVisibility(View.GONE);
			holder.appInfoView.setVisibility(View.VISIBLE);
			holder.downloadBtn.setText("更新");
			holder.appInfoView.setText(size);
			break;
		default:
			break;
		}
		
		return view;
	}
	
	private class ViewHolder{
		AsyncImageView imageView;
		TextView appLabelView;
		TextView appInfoView;
		TextView infoView;
		Button downloadBtn;
		
		View downloadView;
		TextView progressView;
		ProgressBar barView;
	}
	
	class MsgData {
		int position;
		int status;

		public MsgData(int position, int status) {
			this.position = position;
			this.status = status;
		}
	}
	
	
	private class ButtonClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			MsgData msgData = (MsgData) v.getTag();
			int position = msgData.position;
			int status = msgData.status;
			Bundle bundle = new Bundle(2);
			switch (status) {
			case Conf.NOT_DOWNLOAD:
				bundle.putInt(GetAppListener.CALLBACK_FLAG, GetAppListener.MSG_START_DOWNLOAD);
				Toast.makeText(mContext, "Start download", Toast.LENGTH_SHORT).show();
				break;
			case Conf.DOWNLOADING:
				bundle.putInt(GetAppListener.CALLBACK_FLAG, GetAppListener.MSG_STOP_DOWNLOAD);
				break;
			case Conf.DOWNLOADED:
				bundle.putInt(GetAppListener.CALLBACK_FLAG, GetAppListener.MSG_INSTALL_APP);
				break;
			case Conf.INSTALLED:
				bundle.putInt(GetAppListener.CALLBACK_FLAG, GetAppListener.MSG_OPEN_APP);
				break;
			case Conf.NEED_UDPATE:
				bundle.putInt(GetAppListener.CALLBACK_FLAG, GetAppListener.MSG_UPDATE_APP);
				break;
			default:
				break;
			}
			bundle.putInt(GetAppListener.KEY_ITEM_POSITION, position);
			notifyActivityStateChanged(bundle);
		}
		
	}
	
	private static class Record{
		int mHashCode;
		GetAppListener mCallBack;
	}
	
	private ArrayList<Record> mRecords = new ArrayList<Record>();
	public void registerKeyListener(GetAppListener callBack){
		synchronized (mRecords) {
			//register callback in adapter,if the callback is exist,just replace the event
			Record record = null;
			int hashCode = callBack.hashCode();
			final int n = mRecords.size();
			for(int i = 0; i < n ; i++){
				record = mRecords.get(i);
				if (hashCode == record.mHashCode) {
					return;
				}
			}
			
			record = new Record();
			record.mHashCode = hashCode;
			record.mCallBack = callBack;
			mRecords.add(record);
		}
	}
	
	private void notifyActivityStateChanged(Bundle bundle){
		if (!mRecords.isEmpty()) {
			Log.d(TAG, "notifyActivityStateChanged.clients = " + mRecords.size());
			synchronized (mRecords) {
				Iterator<Record> iterator = mRecords.iterator();
				while (iterator.hasNext()) {
					Record record = iterator.next();
					
					GetAppListener listener = record.mCallBack;
					if (listener == null) {
						iterator.remove();
						return;
					}
					
					listener.onCallBack(bundle);
				}
			}
		}
	}
	
	public void unregisterMyKeyListener(GetAppListener callBack){
		remove(callBack.hashCode());
	}
	
	private void remove(int hashCode){
		synchronized (mRecords) {
			Iterator<Record> iterator =mRecords.iterator();
			while(iterator.hasNext()){
				Record record = iterator.next();
				if (record.mHashCode == hashCode) {
					iterator.remove();
				}
			}
		}
	}

}
