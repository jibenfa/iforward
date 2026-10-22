package cn.xm.hanley.iforward.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

/**
 * @Description: 获取联系人数据[包含姓名和电话号码]
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-7下午11:26:52
 * @Google+ www.google.com/+hanleytang 
 */
public class ContactsUtil {

	private static String TAG = "ContactsUtil";

	/**
	 * 
	 * @Title:加载联系人
	 * @Description: 一次性获取所有联系人
	 * @return void
	 * @throws
	 */
	public static ArrayList<HashMap<String,Object>> loadContacts(Context context){
		
		ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		
		while (cursor.moveToNext()){
			
			int nameFieldColumnIndex  = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contactName = cursor.getString(nameFieldColumnIndex);
			if(null == contactName){
				continue;
			}
			
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		    int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

		    if(hasPhone > 0){
		    	
		    	Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);     
		    	while (phones.moveToNext()){      
		    		String contacNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		    		if(null == contacNumber){
						continue;
					}else{
						//只显示有姓名和电话的联系人
						Log.i(TAG , "contactName: "+contactName.toString());
						Log.i(TAG, "contacNumber is :"+contacNumber);
			    		HashMap<String,Object> map = new HashMap<String,Object>();
			    		map.put("contactName", contactName);
			    		map.put("contactNumber", contacNumber);
			    		map.put("lvcheck", false);
			    		data.add(map);
					}
		    		break;//目前不考虑有多个号码的联系人，故此break
		    	}     
		    	phones.close();    
		    }
		    
		}   
		cursor.close();
		
		return data;
	}
}
