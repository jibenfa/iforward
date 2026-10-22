package cn.xm.hanley.iforward.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import cn.xm.hanley.iforward.sqlite.BlockSQLite;
import cn.xm.hanley.iforward.sqlite.HistorySQLite;

/**
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-16上午11:59:29
 * @Google+ www.google.com/+hanleytang
 */
public class DataBaseFactoryUtil {

	
	/**
	 * 转接对象DB
	 * @param context
	 * @return BlockSQLite
	 * @throws
	 */
	public static BlockSQLite createFordwardDB(Context context){
		BlockSQLite bs = new BlockSQLite(context);
		SQLiteDatabase sd = bs.getWritableDatabase();
		sd.close();
		return bs;
	}
	
	/**
	 * 转接历史DB
	 * @param context
	 * @return
	 * @return HistorySQLite
	 * @throws
	 */
	public static HistorySQLite createHistoryDB(Context context){
		HistorySQLite bs = new HistorySQLite(context);
		SQLiteDatabase sd = bs.getWritableDatabase();
		sd.close();
		return bs;
	}
}
