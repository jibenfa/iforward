package cn.xm.hanley.iforward.utils;

import cn.xm.hanley.iforward.activity.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;


/**
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-21上午10:14:43
 * @Google+ www.google.com/+hanleytang
 */
public class TipsUtil {

	
    /**
     * 
    * @Description: 关于
    * @param @param context  
    * @return void 
    * @throws
     */
    public static void shwoAbout(Context context) {   
        AlertDialog.Builder builder = new AlertDialog.Builder(context);   
        builder.setTitle(context.getResources().getString(R.string.about));   
        builder.setMessage(context.getResources().getString(R.string.app_intrudoction));   
        builder.setPositiveButton(context.getResources().getString(R.string.ok),   
                new DialogInterface.OnClickListener() {   
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }   
                });   
        builder.show();   
    } 
    
    public static void tipToast(Context context,int source){
    	Toast.makeText(context, context.getResources().getString(source), Toast.LENGTH_SHORT).show();
    }
	
}
