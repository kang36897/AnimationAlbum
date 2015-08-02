package com.gulu.album.database;

import android.database.sqlite.SQLiteDatabase;

public interface BaseTable<T> {
	
	public void createTable(SQLiteDatabase db);
	
	public void deleteTable(SQLiteDatabase db);
}
