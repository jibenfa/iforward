package cn.xm.hanley.iforward.sqlite;

import java.util.ArrayList;

import cn.xm.hanley.iforward.domain.History;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 转接历史记录
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-17下午2:18:37
 * @Google+ www.google.com/+hanleytang
 */
public class HistorySQLite extends SQLiteOpenHelper {

	private String TAG = "HistorySQLite";
	private static final String DBNAME = "database.history";
	private static final int DB_VERSION = 1;

	public HistorySQLite(Context context){
		super(context, DBNAME, null, DB_VERSION);
	}
	
	public HistorySQLite(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DBNAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		  db.execSQL("create table history(_id Integer primary key autoincrement,hname varchar(20),hnumber varchar(20), hdate varchar(10),htime varchar(10),htransfer varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists history");
		onCreate(db);

	}
	
	// 添加一条记录
	public void insert(String historyName, String historyNumber,String historyDate,String historyTime,String htransfer) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("hname", historyName);
		cv.put("hnumber", historyNumber);
		cv.put("hdate", historyDate);
		cv.put("htime", historyTime);
		cv.put("htransfer", htransfer);
		db.insert("history", null, cv);
		db.close();
	}
	
	/**
	 * 根据电话删除历史记录
	 * @param historyNumber
	 * @return void
	 * @throws
	 */
	public void deleteHistoryByNumber(String historyNumber){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("history", "hnumber=?", new String[]{historyNumber});
		db.close();
	}
	
	
	
	/**
	 * 根据号码批量删除历史记录
	 * @param numberList
	 * @return void
	 * @throws
	 */
	public void deleteBatchByHistorytNumber(ArrayList<String> numberList){
		SQLiteDatabase db = this.getWritableDatabase();
		for(String nl:numberList){
			db.delete("history", "hnumber=?", new String[]{nl});
		}
		db.close();
		
	}
	
	/**
	 * 删除所有记录
	 * 
	 * @return void
	 * @throws
	 */
	public void deleteAll(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("history", null, null);
		db.close();
	}
	
	/**
	 * 查询历史记录
	 * @Title: queryHistory
	 * @return
	 * @return ArrayList<History>
	 * @throws
	 */
	public ArrayList<History> queryHistory(){
		ArrayList<History> historytList = new ArrayList<History>();
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String [] columns = new String[]{"hname","hnumber","hdate","htime","htransfer"};
			Cursor cursor = db.query("history", columns, "_id>?", new String[]{"0"}, null,null, null);
			while(cursor.moveToNext()){
				History history = new History();
				String hname = cursor.getString(0);
				String hnumber = cursor.getString(1);
				String hdate = cursor.getString(2);
				String htime = cursor.getString(3);
				String htransfer = cursor.getString(4);
				history.setHname(hname);
				history.setHnumber(hnumber);
				history.setHdate(hdate);
				history.setHtime(htime);
				history.setHtransfer(htransfer);
				historytList.add(history);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			Log.e(TAG , e.toString());
		}
		return historytList;
	}

}
