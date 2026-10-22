package cn.xm.hanley.iforward.sqlite;

import java.util.ArrayList;

import cn.xm.hanley.iforward.domain.Contact;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @Description: 转接列表
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-16上午11:05:33
 * @Google+ www.google.com/+hanleytang
 */
public class BlockSQLite extends SQLiteOpenHelper {

	private String TAG = "BlockSQLite";
	private static final String DBNAME = "database.block_contacts";
	private static final int DB_VERSION = 1;

	public BlockSQLite(Context context){
		super(context, DBNAME, null, DB_VERSION);
	}
	
	public BlockSQLite(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DBNAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		  db.execSQL("create table block_contacts(_id Integer primary key autoincrement,cname varchar(20),cnumber varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists block_contacts");
		onCreate(db);

	}
	
	// 添加一条记录
	public void insert(String contactName, String contactNumber) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("cname", contactName);
		cv.put("cnumber", contactNumber);
		db.insert("block_contacts", null, cv);
		db.close();
	}
	
	/**
	 * 根据联系人电话删除拦截对象
	 * @param contactNumber
	 * @return void
	 * @throws
	 */
	public void deleteContactByNumber(String contactNumber){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("block_contacts", "cnumber=?", new String[]{contactNumber});
		db.close();
	}
	
	
	
	/**
	 * 根据联系人号码批量删除拦截对象
	 * @param numberList
	 * @return void
	 * @throws
	 */
	public void deleteBatchByContactNumber(ArrayList<String> numberList){
		SQLiteDatabase db = this.getWritableDatabase();
		for(String nl:numberList){
			db.delete("block_contacts", "cnumber=?", new String[]{nl});
		}
		db.close();
	}
	
	
	/**
	 * 查询被转接的联系人列表
	 * @Title: queryBlockContact
	 * @return
	 * @return ArrayList<Contact>
	 * @throws
	 */
	public ArrayList<Contact> queryAllForwardContact(){
		ArrayList<Contact> contactList = new ArrayList<Contact>();
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String [] columns = new String[]{"cname","cnumber"};
			Cursor cursor = db.query("block_contacts", columns, "_id>?", new String[]{"0"}, null,null, null);
			while(cursor.moveToNext()){
				Contact contact = new Contact();
				String cname = cursor.getString(0);
				String cnumber = cursor.getString(1);
				contact.setContactName(cname);
				contact.setContactNumber(cnumber);
				contactList.add(contact);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			Log.e(TAG , e.toString());
		}
		return contactList;
	}
	
	
	/**
	 * 根据号码查询拦截对象
	 * @param number
	 * @return
	 * @return Contact
	 * @throws
	 */
	public Contact queryForwardContactByNumber(String number){
		Contact contact = null;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String [] columns = new String[]{"cname","cnumber"};
			Cursor cursor = db.query("block_contacts", columns, "cnumber=?", new String[]{number}, null,null, null);
			while(cursor.moveToLast()){
				contact = new Contact();
				String cname = cursor.getString(0);
				String cnumber = cursor.getString(1);
				contact.setContactName(cname);
				contact.setContactNumber(cnumber);
				break;
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			Log.e(TAG , e.toString());
		}
		return contact;
	}

	
	
	/**
	 * 是否包含此号码
	 * @param number
	 * @return
	 * @return boolean
	 * @throws
	 */
	public boolean isContainNumber(String number){
		boolean flag = false;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String [] columns = new String[]{"cname","cnumber"};
			Cursor cursor = db.query("block_contacts", columns, "cnumber=?", new String[]{number}, null,null, null);
			while(cursor.moveToLast()){
				flag = true;
				break;
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			flag = false;
			Log.e(TAG , e.toString());
		}
		return flag;
	}
	
}
