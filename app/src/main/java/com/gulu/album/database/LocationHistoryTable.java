package com.gulu.album.database;

import android.database.sqlite.SQLiteDatabase;

public class LocationHistoryTable implements BaseTable {
	
	public static final String TABLE_NAME = "location_history";
	
	// table colums
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_ALTITUDE = "altitude";
	public static final String COLUMN_ACCURACY = "accuracy";
	public static final String COLUMN_BEAR = "bear";
	public static final String COLUMN_SPEED = "speed";
	public static final String COLUMN_FK = "foreign"; // MODIFY AS NEEDED
	
	public static final String[] PROJECTION = new String[] { COLUMN_TIME,
			COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_ALTITUDE,
			COLUMN_ACCURACY, COLUMN_BEAR, COLUMN_SPEED };
	
	static final String TABLE_CREATE_STATEMENT = "create table " + TABLE_NAME
			+ "(" + COLUMN_ID + "INTEGER autoincrement," + COLUMN_TIME
			+ "TEXT NOT NULL," + COLUMN_LATITUDE + "REAL," + COLUMN_LONGITUDE
			+ "REAL," + COLUMN_ALTITUDE + " REAL," + COLUMN_BEAR + " TEXT,"
			+ COLUMN_SPEED + " TEXT)";
	
	static final String TABLE_DELETE_STATEMENT = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;
	
	@Override
	public void createTable(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_STATEMENT);
	}
	
	@Override
	public void deleteTable(SQLiteDatabase db) {
		db.execSQL(TABLE_DELETE_STATEMENT);
	}
}
