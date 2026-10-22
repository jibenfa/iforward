package cn.xm.hanley.iforward.utils;

import java.util.ArrayList;
import java.util.HashMap;

import cn.xm.hanley.iforward.constants.Constants;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

/**
 * @Description: 扫描联系人
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-14下午5:32:35
 * @Google+ www.google.com/+hanleytang
 */
public class ContactScanner extends Thread {

	private Handler handler;

	private Context context;

	public ContactScanner(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void run() {
		loadContact();
	}

	public void loadContact() {
		
		int flag = 1;
		ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (cursor.moveToNext()) {
			
			flag++;
			
			int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contactName = cursor.getString(nameFieldColumnIndex);
			if (null == contactName) {
				continue;
			}

			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (hasPhone > 0) {

				Cursor phones = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phones.moveToNext()) {
					String contacNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if (null == contacNumber) {
						continue;
					} else {
						// 只显示有姓名和电话的联系人
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("contactName", contactName);
						map.put("contactNumber", contacNumber);
						map.put("lvcheck", false);
						data.add(map);
						Message msg = handler.obtainMessage(Constants.RESPONSE_CODE_PROGRESS_UPDATE);
						msg.arg1 = flag;
						msg.arg2 = cursor.getCount();
						msg.sendToTarget();
					}
				}
				phones.close();
			}

		}
		Message msg = handler.obtainMessage(Constants.RESPONSE_CODE_SHOW_CONTACT, data);
		msg.sendToTarget();
		cursor.close();
	}

}
