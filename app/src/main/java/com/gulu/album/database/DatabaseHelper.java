package com.gulu.album.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DEFALUT_DATABASE_NAME = "honeycomb.db";
	public static final int DEFAULT_DATABASE_VERSION = 1;
	private static DatabaseHelper sInstance;
	
	static Class<BaseTable>[] REGISTED_TABLES = new Class[] { LocationHistoryTable.class, };
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		createTableInDB(db);
	}
	
	void createTableInDB(SQLiteDatabase db) {
		for (Class<BaseTable> clazz : REGISTED_TABLES) {
			try {
				clazz.newInstance().createTable(db);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		deleteTableInDB(db);
		
		createTableInDB(db);
	}
	
	void deleteTableInDB(SQLiteDatabase db) {
		for (Class<BaseTable> clazz : REGISTED_TABLES) {
			try {
				clazz.newInstance().deleteTable(db);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized static DatabaseHelper getInstance(Context context) {
		
		if (sInstance == null) {
			sInstance = new DatabaseHelper(context.getApplicationContext(),
					DEFALUT_DATABASE_NAME, null, DEFAULT_DATABASE_VERSION);
		}
		
		return sInstance;
		
	}
	
}
