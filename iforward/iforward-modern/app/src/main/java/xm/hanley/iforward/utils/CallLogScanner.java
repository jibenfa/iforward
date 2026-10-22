package cn.xm.hanley.iforward.utils;

import java.util.ArrayList;
import java.util.HashMap;

import cn.xm.hanley.iforward.constants.Constants;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;

/**
 * 扫描通话记录
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-23上午10:22:32
 * @Google+ www.google.com/+hanleytang
 */
public class CallLogScanner extends Thread{
	
	private Handler handler;

	private Context context;
	
	public CallLogScanner(Handler handler,Context context){
		this.handler = handler;
		this.context = context;
	}
	

	@Override
	public void run() {
		loadCallLogs();
	}



	public void loadCallLogs(){
		
		int flag = 1;
		ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
		
		ContentResolver cr = context.getContentResolver();
		final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE}, null, null,CallLog.Calls.DEFAULT_SORT_ORDER);
		while(cursor.moveToNext()){
			flag++;
            String contacNumber = cursor.getString(0);
            String contactName = cursor.getString(1);
            
            HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("contactName", contactName);
			map.put("contactNumber", contacNumber);
			map.put("lvcheck", false);
			if(!data.contains(map)){
				
				data.add(map);
			}
            
			Message msg = handler.obtainMessage(Constants.RESPONSE_CODE_PROGRESS_UPDATE);
			msg.arg1 = flag;
			msg.arg2 = cursor.getCount();
			msg.sendToTarget();
		}
		
		Message msg = handler.obtainMessage(Constants.RESPONSE_CODE_SHOW_CONTACT, data);
		msg.sendToTarget();
		cursor.close();
	}
}
