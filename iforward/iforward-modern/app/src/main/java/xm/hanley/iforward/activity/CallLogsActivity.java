package cn.xm.hanley.iforward.activity;

import java.util.ArrayList;
import java.util.HashMap;

import cn.xm.hanley.iforward.constants.Constants;
import cn.xm.hanley.iforward.domain.Contact;
import cn.xm.hanley.iforward.utils.CallLogScanner;
import cn.xm.hanley.iforward.utils.ContactsUtil;


import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 显示通话记录
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-23上午10:25:06
 * @Google+ www.google.com/+hanleytang
 */
public class CallLogsActivity extends ListActivity {

	private static final String TAG = "CallLogsActivity";
	private Button btnOk;
	private Button btnCancle;
	ArrayList<Contact> selectedContacts;
	private ProgressBar progressBar;
	private TextView titleActionbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_contact);
		
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.anction_bar);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		findViewByids();
		setListeners();
		
		CallLogScanner cs = new CallLogScanner(handler,this);
		cs.start();
	}
	
	private void findViewByids(){
		btnOk = (Button)findViewById(R.id.btn_ok);
		btnCancle = (Button)findViewById(R.id.btn_cancle);
		progressBar = (ProgressBar)findViewById(R.id.scan_progress);
		titleActionbar = (TextView) findViewById(R.id.title_action_bar);
	}
	
	private void setListeners(){
		btnOk.setOnClickListener(onClickListener);
		btnCancle.setOnClickListener(onClickListener);
	}
	
	private void stepsProgress(int progress, int maxProgress) {
		progressBar.setMax(maxProgress);
		progressBar.setProgress(progress);
		progressBar.setVisibility(View.VISIBLE);
	}
	
	private OnClickListener onClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_ok:
				try {
					currentSelectedContants();
					int size = selectedContacts.size();
					if (0 == size) {
						Toast.makeText(getApplication(),getResources().getString(R.string.tip_no_item_selected),Toast.LENGTH_SHORT).show();
					}else{
						setResult(RESULT_OK);
						finish();
					}
					
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					Toast.makeText(getApplication(),getResources().getString(R.string.tip_no_item_selected),Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_cancle:
				CallLogsActivity.this.finish();
				break;
			default:
				Log.i(TAG, "onClickListener");
				break;
			}
		}
		
	};
	private SimpleAdapter simapleAdapter;

	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView lv, View v, int position, long id) {
		super.onListItemClick(lv, v, position, id);
		CheckBox ck = (CheckBox)v.findViewById(R.id.lv_checkbox);
		SimpleAdapter sa = (SimpleAdapter)this.getListAdapter();
		HashMap<String,Object> m = (HashMap<String,Object>)sa.getItem(position);
		m.put("lvcheck", !ck.isChecked());
		sa.notifyDataSetChanged();
		
		//ActionBar显示已经选择数量
		currentSelectedContants();
		int selectNum = selectedContacts.size();
		String actionBarTipName = getResources().getString(R.string.forward_object);
		StringBuffer sb = new StringBuffer();
		sb.append(actionBarTipName);
		sb.append("(");
		sb.append(selectNum);
		sb.append(")");
		titleActionbar.setText(sb.toString());
	}


	@SuppressWarnings("unused")
	private void showALLContacts(){
		ArrayList<HashMap<String,Object>> data = ContactsUtil.loadContacts(this);
		String[] itemName = new String[]{ "contactName","contactNumber","lvcheck"};
		int []   itemValue =  new int[]{ R.id.contactName,R.id.contactNumber,R.id.lv_checkbox};
		SimpleAdapter simapleAdapter = new SimpleAdapter(this, data,R.layout.item_contact, itemName, itemValue);
		this.setListAdapter(simapleAdapter);
		simapleAdapter.notifyDataSetChanged();
	}
	
	private void showContacts(ArrayList<HashMap<String,Object>> mydata){
		
		String[] itemName = new String[]{ "contactName","contactNumber","lvcheck"};
		int []   itemValue =  new int[]{ R.id.contactName,R.id.contactNumber,R.id.lv_checkbox};
		simapleAdapter = new SimpleAdapter(this, mydata,R.layout.item_contact, itemName, itemValue);
		this.setListAdapter(simapleAdapter);
	}
	
	
	@SuppressWarnings("unchecked")
	private void currentSelectedContants(){
		SimpleAdapter sa = (SimpleAdapter)this.getListAdapter();
		selectedContacts = new ArrayList<Contact>();
		int size = sa.getCount();
		for(int i=0; i<size; i++){
			HashMap<String,Object> contact = (HashMap<String,Object>)sa.getItem(i);
			boolean flag = (Boolean) contact.get("lvcheck");
			if(flag){
				String contactName = (String) contact.get("contactName");
				String contactNumber = (String)contact.get("contactNumber");
				contactNumber = contactNumber.replaceAll(" ", "");
				Contact ct = new Contact();
				ct.setContactName(contactName);
				ct.setContactNumber(contactNumber.replaceAll("-", ""));
				selectedContacts.add(ct);
			}
		}
		Constants.CURRENT_SELECTED_CONTACTS = selectedContacts;
	}
	
	
	
	
	private  Handler handler = new Handler(){

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
