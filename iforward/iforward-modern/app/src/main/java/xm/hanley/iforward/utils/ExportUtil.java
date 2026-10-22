package cn.xm.hanley.iforward.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.xm.hanley.iforward.domain.History;

import android.os.Environment;
import android.util.Log;

public class ExportUtil {

	
	private static String TAG = "ExportUtil";

	public  static boolean saveHistoryToSDCard(ArrayList<History> history){
		
		boolean flag = true;
		
		if(null == history || history.size() ==0){
			return false;
		}
		
		String fullPath = newFileName();
		try {
			File file = new File(fullPath);
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			for(History h:history){
				fw.write(h.getHname());
				fw.write("\t\t");
				fw.write(h.getHnumber());
				fw.write("\t\t");
				fw.write(h.getHdate()+" "+h.getHtime());
				fw.write("\t\t");
				fw.write(h.getHtransfer());
				fw.write("\n");
				fw.flush();
			}
			fw.close();
		} catch (Exception e) {
			flag = false;
			Log.e(TAG , e.toString());
		}
		return flag;
	}
	
	public static String newFileName(){
		
		StringBuffer sb = null;
		try {
			sb = new StringBuffer();
			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/iforward/";
			File tempFile = new File(path);
			if (!tempFile.exists()) {
				tempFile.mkdir();
			}
			sb.append(path);
			sb.append("转接历史[");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String result = sdf.format(date);
			sb.append(result);
			sb.append("]");
			sb.append("-");
			sb.append(date.getTime());
			sb.append(".txt");
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return sb.toString();
		
	}
}
