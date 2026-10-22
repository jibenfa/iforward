package cn.xm.hanley.iforward.utils;

import java.util.ArrayList;

import cn.xm.hanley.iforward.constants.Constants;
import cn.xm.hanley.iforward.domain.Contact;
import cn.xm.hanley.iforward.sqlite.BlockSQLite;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 转接对象扫描
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-17下午5:51:38
 * @Google+ www.google.com/+hanleytang
 */
public class ForwardScanner extends Thread {

	private Handler handler;

	private Context context;

	public ForwardScanner(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void run() {
		loadForward();
	}

	public void loadForward() {
		BlockSQLite db = DataBaseFactoryUtil.createFordwardDB(context);
		ArrayList<Contact> contacts = db.queryAllForwardContact();
		if(null != contacts){
			Message msg  = handler.obtainMessage(Constants.RESPONSE_CODE_SHOW_FORWARD, contacts);
			msg.sendToTarget();
		}
	}

}
