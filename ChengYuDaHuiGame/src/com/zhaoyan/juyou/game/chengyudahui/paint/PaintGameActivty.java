package com.zhaoyan.juyou.game.chengyudahui.paint;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zhaoyan.juyou.game.chengyudahui.MainActivity;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PaintGameActivty extends Activity implements OnClickListener {
	private ImageView mPaintImage;
	private TextView mPaintChengyuName;
	private View mChangeWordBtn, mPaintCleanBtn, mPaintRightBtn;
	public static Bitmap mPaintBitmap;
	private Paint mPaint;
	private Canvas mCanvas;
	private int mWidth, mHeight;
	private boolean mPrepareFlag;
	private Random mRandom;
	private String[] mPaintColor = new String[] { "红色", "绿色", "蓝色", "黑色", "黄色" },
			mPaintStyle = new String[] { "钢笔", "铅笔", "毛笔" };
	private AlertDialog selectColor, selectStyle;
	private Path mPath;
	private int index = -1;
	private Drawable mDrawable;
	private float mX, mY;
	private final float TOUCH_TOLERANCE = 4;
	public static int mPaintWidth = 16, mCurrentColor = Color.BLACK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paint_surface);
		index = getIntent().getIntExtra("index", -1);
		mPaintChengyuName = (TextView) findViewById(R.id.paint_chengyu_name);
		mPaintImage = (ImageView) findViewById(R.id.paint_image_view);
		mPaintChengyuName.setText("");
		mPaintCleanBtn = (View) findViewById(R.id.paint_clean_btn);
		mChangeWordBtn = (View) findViewById(R.id.paint_change_word_btn);
		mPaintRightBtn = (View) findViewById(R.id.paint_chengyu_right);
		mPaintCleanBtn.setOnClickListener(this);
		mChangeWordBtn.setOnClickListener(this);
		mPaintRightBtn.setOnClickListener(this);
		ViewTreeObserver vto = mPaintImage.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				mHeight = mPaintImage.getMeasuredHeight();
				mWidth = mPaintImage.getMeasuredWidth();
				if (!mPrepareFlag) {
					init();
					mPrepareFlag = true;
				}
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	private void init() {
		createBitmap();
		mPaintImage.setImageBitmap(mPaintBitmap);
		mPaintImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mPath == null) {
						mPath = new Path();
					}
					touch_start(arg1.getX(), arg1.getY());
					break;
				case MotionEvent.ACTION_MOVE:
					touch_move(arg1.getX(), arg1.getY());
					break;
				case MotionEvent.ACTION_UP:
					touch_up();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	private void showWord() {
		Map<String, String> map = getChengyuRandom();
		if (map != null)
			mPaintChengyuName.setText(map.get(ChengyuColums.NAME));
		else
			Log.e(PaintGameActivty.class.getName(), "paint get chengyue error");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.paint_chengyu_right:
			setResult();
			break;
		case R.id.paint_change_word_btn:
			showWord();
		case R.id.paint_clean_btn:
			cleanPaint();
			break;

		default:
			break;
		}
	}

	public void cleanPaint() {
		if (mPrepareFlag) {
			createBitmap();
		}
	}

	private Map<String, String> getChengyuRandom() {
		if (mRandom == null)
			mRandom = new Random();
		int id = Math.abs(mRandom.nextInt()) % MainActivity.DB_NUMBER;
		Cursor cursor = getContentResolver().query(
				ChengyuColums.CONTENT_URI,
				new String[] { ChengyuColums.NAME, ChengyuColums.PINYIN,
						ChengyuColums.COMMENT, ChengyuColums.ORIGINAL,
						ChengyuColums.EXAMPLE }, "_id = " + id, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(ChengyuColums.NAME, cursor.getString(0));
				map.put(ChengyuColums.PINYIN, cursor.getString(1));
				map.put(ChengyuColums.COMMENT, cursor.getString(2));
				map.put(ChengyuColums.ORIGINAL, cursor.getString(3));
				map.put(ChengyuColums.EXAMPLE, cursor.getString(4));
				cursor.close();
				return map;
			}
			cursor.close();
		}
		return null;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("设置画笔颜色");
		menu.add("设置画笔风格");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		String name = (String) item.getTitle();
		if (name != null) {
			if ("设置画笔颜色".equals(name)) {
				if (selectColor == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("设置画笔颜色");
					builder.setSingleChoiceItems(mPaintColor,
							getColorIndex(mCurrentColor),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									switch (arg1) {
									case 0:
										mCurrentColor = Color.RED;
										break;
									case 1:
										mCurrentColor = Color.GREEN;
										break;
									case 2:
										mCurrentColor = Color.BLUE;
										break;
									case 3:
										mCurrentColor = Color.BLACK;
										break;
									case 4:
										mCurrentColor = Color.YELLOW;
										break;
									default:
										break;
									}
									mPaint.setColor(mCurrentColor);
									selectColor.dismiss();
									selectColor = null;
								}
							});
					selectColor = builder.create();
				}
				selectColor.show();
			} else if ("设置画笔风格".equals(name)) {
				if (selectStyle == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("设置画笔颜色");
					builder.setSingleChoiceItems(mPaintStyle,
							getStyleIndex(mPaintWidth),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									switch (arg1) {
									case 0:
										mPaintWidth = 8;
										break;
									case 1:
										mPaintWidth = 12;
										break;
									case 2:
										mPaintWidth = 16;
										break;
									default:
										break;
									}
									mPaint.setStrokeWidth(DipToPixels(PaintGameActivty.this, mPaintWidth));
									selectStyle.dismiss();
									selectStyle = null;
								}
							});
					selectStyle = builder.create();
				}
				selectStyle.show();
			}
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
		mCanvas.drawPath(mPath, mPaint);
		mPaintImage.setImageBitmap(mPaintBitmap);
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
		mPaintImage.setImageBitmap(mPaintBitmap);
	}

	public void setResult() {
		Intent intent = new Intent();
		intent.putExtra("index", index);
		setResult(20, intent);
		// mPaintBitmap.recycle();
		finish();
	}

	private void createBitmap() {
		if (mPaintBitmap != null) {
			mPaintBitmap.recycle();
		}
		mPaintBitmap = Bitmap.createBitmap(mWidth, mHeight,
				Bitmap.Config.ARGB_8888);
		if (mDrawable == null) {
			mDrawable = getResources().getDrawable(R.drawable.mizige_new256);
			mDrawable.setBounds(0, 0, mWidth, mHeight);
		}
		mCanvas = new Canvas(mPaintBitmap);
		mPaint = new Paint();
		mPaint.setStrokeWidth(DipToPixels(this, mPaintWidth));
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(mCurrentColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mDrawable.draw(mCanvas);
		mPaintImage.setImageBitmap(mPaintBitmap);
	}

	private int getStyleIndex(int width) {
		switch (width) {
		case 12:
			return 1;
		case 16:
			return 2;

		default:
			break;
		}
		return 0;
	}

	// { "红色", "绿色", "蓝色", "黑色", "黄色" }
	private int getColorIndex(int color) {
		switch (color) {
		case Color.BLACK:
			return 3;
		case Color.RED:
			return 0;
		case Color.YELLOW:
			return 4;
		case Color.GREEN:
			return 1;
		case Color.BLUE:
			return 3;
		default:
			break;
		}
		return 0;
	}

	public int DipToPixels(Context context,final int dip) {
		final float SCALE = context.getResources().getDisplayMetrics().density;

		float valueDips = dip;
		int valuePixels = (int) (valueDips * SCALE + 0.5f);
		Log.e("ArbiterLiu", valuePixels + "@@@@@@@@@@@@@@@" + valueDips);
		return valuePixels;

	}
}
