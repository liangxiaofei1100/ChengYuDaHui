package com.zhaoyan.juyou.game.chengyudahui.db;

import com.zhaoyan.juyou.game.chengyudahui.db.HistoryData.HistoryColums;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDB extends SQLiteOpenHelper {

	public HistoryDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String sql = "create table " + HistoryColums.TableName
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ HistoryColums.KIND + " INTEGER, " + HistoryColums.TIME
				+ " TEXT, " + HistoryColums.NAME + " TEXT);";
		arg0.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		String dropTable = "DROP TABLE IF EXISTS " + HistoryColums.TableName;
		arg0.execSQL(dropTable);
		onCreate(arg0);
	}

}
