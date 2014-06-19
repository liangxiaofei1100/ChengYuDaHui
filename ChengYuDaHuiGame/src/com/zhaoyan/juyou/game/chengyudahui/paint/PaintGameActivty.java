package com.zhaoyan.juyou.game.chengyudahui.paint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zhaoyan.juyou.game.chengyudahui.MainActivity;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PaintGameActivty extends Activity implements OnClickListener {
	private ImageView mPaintImage;
	private TextView mPaintChengyuName;
	private Button mChangeWordBtn, mPaintCleanBtn, mPaintRightBtn;
	private Bitmap mPaintBitmap;
	private Point firstPoint, secondPoint, thirdPoint;
	private Paint mPaint;
	private Canvas mCanvas;
	private int mWidth, mHeight;
	private boolean mPrepareFlag;
	private Random mRandom;
	private int mPaintScore = 0;
	private List<Operator> cancelOperator, resumeOperator;
	private String[] mPaintColor = new String[] { "红色", "绿色", "蓝色", "黑色", "黄色" };
	private AlertDialog selectColor;
	private List<Point> tempList;
	private boolean isMain = true, drawLine = true;
	private Path mPath;
	private final int BACKGROUND_COLOR=Color.GRAY;

	private class Operator {
		private List<Point> pointLists;

		public Operator() {
			pointLists = new ArrayList<PaintGameActivty.Point>();
		}
	}

	private class Point {
		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public Point() {
		};

		float x;
		float y;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paint_surface);
		mPaintChengyuName = (TextView) findViewById(R.id.paint_chengyu_name);
		mPaintImage = (ImageView) findViewById(R.id.paint_image_view);
		mPaintChengyuName.setText("");
		mPaintCleanBtn = (Button) findViewById(R.id.paint_clean_btn);
		mChangeWordBtn = (Button) findViewById(R.id.paint_change_word_btn);
		mPaintRightBtn = (Button) findViewById(R.id.paint_chengyu_right);
		mPaintCleanBtn.setOnClickListener(this);
		mChangeWordBtn.setOnClickListener(this);
		mPaintRightBtn.setOnClickListener(this);
		cancelOperator = new ArrayList<PaintGameActivty.Operator>();
		resumeOperator = new ArrayList<PaintGameActivty.Operator>();
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
		showWord();
		mPaintBitmap = Bitmap.createBitmap(mWidth, mHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mPaintBitmap);
		mCanvas.drawColor(BACKGROUND_COLOR);
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(5);
		mCanvas.drawBitmap(mPaintBitmap, new Matrix(), mPaint);
		mPaintImage.setImageBitmap(mPaintBitmap);
		if (isMain)
			mPaintImage.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub

					switch (arg1.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (tempList == null) {
							tempList = new ArrayList<PaintGameActivty.Point>();
						} else {
							tempList.clear();
						}
						if (firstPoint == null) {
							firstPoint = new Point();
						}
						firstPoint.x = arg1.getX();
						firstPoint.y = arg1.getY();
						tempList.add(new Point(firstPoint.x, firstPoint.y));
						if (mPath == null) {
							mPath = new Path();
						}
						mPath.reset();
						mPath.moveTo(firstPoint.x, firstPoint.y);
						break;
					case MotionEvent.ACTION_MOVE:
						if (drawLine) {
							drawLine(arg1);
						} else {
							drawQuad(arg1);
						}
						break;
					case MotionEvent.ACTION_UP:
						if (cancelOperator == null) {
							cancelOperator = new ArrayList<PaintGameActivty.Operator>();
						}
						Operator op = new Operator();
						op.pointLists.addAll(tempList);
						cancelOperator.add(op);
						if (cancelOperator.size() > 10) {
							cancelOperator.remove(0);
						}
						tempList.clear();
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
			mPaintScore++;
		case R.id.paint_change_word_btn:
			showWord();
		case R.id.paint_clean_btn:
			cleanPaint();
			break;

		default:
			break;
		}
	}

	private void cleanPaint() {
		if (mPrepareFlag) {

			mCanvas.drawColor(BACKGROUND_COLOR);
			mPaintImage.setImageBitmap(mPaintBitmap);
			if (cancelOperator != null)
				cancelOperator.clear();
			if (resumeOperator != null)
				resumeOperator.clear();
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
						ChengyuColums.EXAMPLE, ChengyuColums.ENGLISH,
						ChengyuColums.SIMILAR, ChengyuColums.OPPOSITE },
				"_id = " + id, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(ChengyuColums.NAME, cursor.getString(0));
				map.put(ChengyuColums.PINYIN, cursor.getString(1));
				map.put(ChengyuColums.COMMENT, cursor.getString(2));
				map.put(ChengyuColums.ORIGINAL, cursor.getString(3));
				map.put(ChengyuColums.EXAMPLE, cursor.getString(4));
				map.put(ChengyuColums.ENGLISH, cursor.getString(5));
				map.put(ChengyuColums.SIMILAR, cursor.getString(6));
				map.put(ChengyuColums.OPPOSITE, cursor.getString(7));
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
		menu.add("撤销");
		menu.add("恢复 ");
		return super.onCreateOptionsMenu(menu);
	}

	/** sync to paint picture */
	private void sendPaintPic() {

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
					builder.setSingleChoiceItems(mPaintColor, 0,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									switch (arg1) {
									case 0:
										mPaint.setColor(Color.RED);
										break;
									case 1:
										mPaint.setColor(Color.GREEN);
										break;
									case 2:
										mPaint.setColor(Color.BLUE);
										break;
									case 3:
										mPaint.setColor(Color.BLACK);
										break;
									case 4:
										mPaint.setColor(Color.YELLOW);
										break;
									default:
										break;
									}
									selectColor.dismiss();
								}
							});
					selectColor = builder.create();
				}
				selectColor.show();
			} else if ("撤销".equals(name)) {
				cancelOperator();
			} else {
				resumeOperator();
			}
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void cancelOperator() {
		if (cancelOperator != null && cancelOperator.size() > 0) {
			Operator op = cancelOperator.remove(cancelOperator.size() - 1);
			Point fp = null;
			Paint cancelPaint = new Paint();
			cancelPaint.setColor(Color.GRAY);
			cancelPaint.setStrokeWidth(5);
			for (Point p : op.pointLists) {
				if (fp != null) {
					mCanvas.drawLine(fp.x, fp.y, p.x, p.y, cancelPaint);
				}
				if (fp == null)
					fp = new Point();
				fp.x = p.x;
				fp.y = p.y;
			}
			if (resumeOperator == null) {
				resumeOperator = new ArrayList<PaintGameActivty.Operator>();
			}
			resumeOperator.add(op);
		}
	}

	private void resumeOperator() {
		if (resumeOperator != null && resumeOperator.size() > 0) {
			Operator op = resumeOperator.remove(resumeOperator.size() - 1);
			Point fp = null;
			for (Point p : op.pointLists) {
				if (fp != null) {
					mCanvas.drawLine(fp.x, fp.y, p.x, p.y, mPaint);
				}
				if (fp == null)
					fp = new Point();
				fp.x = p.x;
				fp.y = p.y;
			}
			cancelOperator.add(op);
		}

	}

	private void drawLine(MotionEvent event) {
		if (secondPoint == null)
			secondPoint = new Point();
		secondPoint.x = event.getX();
		secondPoint.y = event.getY();
		mCanvas.drawLine(firstPoint.x, firstPoint.y, secondPoint.x,
				secondPoint.y, mPaint);
		// mCanvas.drawPath(mPath, mPaint);
		firstPoint.x = secondPoint.x;
		firstPoint.y = secondPoint.y;
		tempList.add(new Point(secondPoint.x, secondPoint.y));
		mPaintImage.setImageBitmap(mPaintBitmap);
	}

	private void drawQuad(MotionEvent event) {
		if (secondPoint == null) {
			secondPoint = new Point();
			secondPoint.x = event.getX();
			secondPoint.y = event.getY();
		} else {
			if (thirdPoint == null) {
				thirdPoint = new Point();
			}
			thirdPoint.x = event.getX();
			thirdPoint.y = event.getY();
			mPath.quadTo(secondPoint.x, secondPoint.y, thirdPoint.x,
					thirdPoint.y);
			mCanvas.drawPath(mPath, mPaint);
			firstPoint.x = secondPoint.x;
			firstPoint.y = secondPoint.y;
			secondPoint.x = thirdPoint.x;
			secondPoint.y = thirdPoint.y;
//			mPath.reset();
			mPath.moveTo(firstPoint.x, firstPoint.y);
			mPaintImage.setImageBitmap(mPaintBitmap);
		}

	}
}
