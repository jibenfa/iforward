package cn.xm.hanley.iforward.activity;

import java.util.ArrayList;
import java.util.HashMap;

import cn.xm.hanley.iforward.constants.Constants;
import cn.xm.hanley.iforward.utils.ContactScanner;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;


/**
 * 转接号码选择联系人
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-20上午11:19:08
 * @Google+ www.google.com/+hanleytang
 */
public class SelContactActivity extends ListActivity {

	private ProgressBar progressBar;
	private TextView titleActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list_sel_contact);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.anction_bar);
		
		findViewByIds();
		
		ContactScanner cs = new ContactScanner(handler,this);
		cs.start();
		
	}
	
	private void findViewByIds(){
		progressBar = (ProgressBar)findViewById(R.id.scan_progress);
		titleActionBar = (TextView)findViewById(R.id.title_action_bar);
		titleActionBar.setText(getResources().getString(R.string.forward_number));
	}
	
	
	private SimpleAdapter simapleAdapter;

	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView lv, View v, int position, long id) {
		super.onListItemClick(lv, v, position, id);
		
		SimpleAdapter sa = (SimpleAdapter)this.getListAdapter();
		HashMap<String,Object> m = (HashMap<String,Object>)sa.getItem(position);
		Intent data = new Intent();
		data.putExtra("number", m.get("contactNumber").toString());
		setResult(RESULT_OK, data);
		finish();
	}

	
	private void showContacts(ArrayList<HashMap<String,Object>> mydata){

		String[] itemName = new String[]{ "contactName","contactNumber"};
		int []   itemValue =  new int[]{ R.id.contactName,R.id.contactNumber};
		simapleAdapter = new SimpleAdapter(this, mydata,R.layout.item_sel_contact, itemName, itemValue);
		this.setListAdapter(simapleAdapter);
	}
	
	private void stepsProgress(int progress,int maxProgress){
		progressBar.setProgress(progress);
		progressBar.setMax(maxProgress);
		progressBar.setVisibility(View.VISIBLE);
	}
	
	private Handler handler = new Handler(){

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case Constants.RESPONSE_CODE_SHOW_CONTACT:
				progressBar.setVisibility(View.GONE);
				ArrayList<HashMap<String,Object>> mydata = (ArrayList<HashMap<String,Object>>)msg.obj;
				showContacts(mydata);
				break;
			case Constants.RESPONSE_CODE_PROGRESS_UPDATE:
				stepsProgress(msg.arg1, msg.arg2);
				break;
			}
		}
		
	};
	
}
