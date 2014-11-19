package com.zhaoyan.juyou.game.chengyudahui.study.write;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaseActivity;
import com.zhaoyan.juyou.game.chengyudahui.view.LetterImageView;

public class WriteMainActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_main);
		
		Intent intent = getIntent();
		if (intent != null) {
			setTitle(intent.getStringExtra("title"));
		}
		
		initItemActionBar();
		ListView listView = (ListView) findViewById(R.id.lv_write);
		String[] items = getResources().getStringArray(R.array.write_items);
		SampleListAdapter adapter = new SampleListAdapter(this, Arrays.asList(items));
		listView.setAdapter(adapter);
	}
	
	private static class SampleListAdapter extends ArrayAdapter<String> {

        public SampleListAdapter(Context context, List<String> objects) {
            super(context, R.layout.activity_write_item, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String name = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_write_item, parent, false);
            }

            LetterImageView letterImageView = (LetterImageView) convertView.findViewById(R.id.liv_write_num);
            letterImageView.setOval(true);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_item_title);
            letterImageView.setLetter(((position + 1) + "").charAt(0));
            textView.setText(name);

            return convertView;
        }
    }
}
