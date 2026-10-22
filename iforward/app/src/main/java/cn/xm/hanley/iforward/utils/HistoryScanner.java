package cn.xm.hanley.iforward.utils;

import java.util.ArrayList;

import cn.xm.hanley.iforward.constants.Constants;
import cn.xm.hanley.iforward.domain.History;
import cn.xm.hanley.iforward.sqlite.HistorySQLite;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 扫描历史记录
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-17下午3:51:22
 * @Google+ www.google.com/+hanleytang
 */
public class HistoryScanner extends Thread {

	private Handler handler;
	private Context context;

	public HistoryScanner(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void run() {
		loadHistory();
	}

	public void loadHistory() {
		HistorySQLite db = DataBaseFactoryUtil.createHistoryDB(context);
		ArrayList<History> hlist = db.queryHistory();
		if(null != hlist){
			Message msg  = handler.obtainMessage(Constants.RESPONSE_CODE_SHOW_HISTORY, hlist);
			msg.sendToTarget();
		}
	}

}
