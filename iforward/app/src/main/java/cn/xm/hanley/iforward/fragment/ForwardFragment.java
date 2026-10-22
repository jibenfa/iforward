package cn.xm.hanley.iforward.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import cn.xm.hanley.iforward.activity.CallLogsActivity;
import cn.xm.hanley.iforward.activity.ContactsActivity;
import cn.xm.hanley.iforward.activity.R;
import cn.xm.hanley.iforward.adapter.ForwardAdapter;
import cn.xm.hanley.iforward.constants.Constants;
import cn.xm.hanley.iforward.domain.Contact;
import cn.xm.hanley.iforward.domain.History;
import cn.xm.hanley.iforward.sqlite.BlockSQLite;
import cn.xm.hanley.iforward.utils.DataBaseFactoryUtil;
import cn.xm.hanley.iforward.utils.ForwardScanner;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


/**
 * 转接对象
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-17下午3:43:19
 * @Google+ www.google.com/+hanleytang
 */
public class ForwardFragment extends ListFragment {
	
	protected static final int CODE_REQUEST_CONTACT = 1;
	ArrayList<History> selectedContacts;
	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	private Button btnAddForward;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fm_forward, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findViewByIds();
		setListeners();
	}

	@Override
	public void onResume() {
		super.onResume();
		reloadForwardList();
	}

	private void reloadForwardList() {
		if (getActivity() != null) {
			new ForwardScanner(handler, getActivity()).start();
		}
	}
	
	private void findViewByIds(){
		btnAddForward = (Button)getActivity().findViewById(R.id.btn_add_forward_num);
	}
	
	private void setListeners(){
		btnAddForward.setOnClickListener(btnListener);
	}
	
	private OnClickListener btnListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_add_forward_num) {
				clickOperation();
			}
		}
	};
	
	
	private void clickOperation(){
		
		String contents[] = {
			getResources().getString(R.string.manually_add),
			getResources().getString(R.string.contacts_add_number),
			getResources().getString(R.string.calllogs_add_number),
		};
		
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setTitle(getResources().getString(R.string.forward_object));
		b.setItems(contents, itemSelected);
		b.show();
	}
	
	public DialogInterface.OnClickListener itemSelected = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = null;
			
			switch(which){
			case 0:
				manuallyAdd();
				break;
			case 1:
				intent = new Intent(getActivity(),ContactsActivity.class);
				startActivityForResult(intent, CODE_REQUEST_CONTACT);
				break;
			case 2:
				intent = new Intent(getActivity(),CallLogsActivity.class);
				startActivityForResult(intent, CODE_REQUEST_CONTACT);
				break;
			}
		}
	};
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == CODE_REQUEST_CONTACT){
				ArrayList<Contact> cttList = Constants.CURRENT_SELECTED_CONTACTS;
				saveToDatabase(cttList);
				Constants.CURRENT_SELECTED_CONTACTS = null;
			}
		}
	}

	private void manuallyAdd(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.manually_add));
		final LayoutInflater factory = LayoutInflater.from(getActivity());
		final View newView = factory.inflate(R.layout.layout_manually_add2, null);
		final EditText editTextNum = (EditText)newView.findViewById(R.id.manually_number);
		final EditText editTextMemo = (EditText)newView.findViewById(R.id.memo);;
		builder.setView(newView);
		builder.setPositiveButton(getResources().getString(R.string.ok), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String manuallyNumber = editTextNum.getText().toString();
				String manuallyMemo = editTextMemo.getText().toString();
				saveToDataBase(manuallyMemo,manuallyNumber);
			}});
		builder.setNegativeButton(getResources().getString(R.string.cancle), null);
		builder.create();
		builder.show();
	}
	
	
	private void saveToDataBase(String contactName,String contactNumber){
		
		if(null != contactNumber && contactNumber.length() > 0){
			if(null == contactName || contactName.length() ==0){
				contactName = "未知";
			}
			BlockSQLite db = DataBaseFactoryUtil.createFordwardDB(getActivity());
			boolean flag = db.isContainNumber(contactNumber);//如果DB中已经存在则不添加
			if(!flag){
				db.insert(contactName, contactNumber);
			}
		}
		ForwardScanner cs = new ForwardScanner(handler,getActivity());
		cs.start();
	}
	
	private void saveToDatabase(ArrayList<Contact> clist){
		BlockSQLite db = DataBaseFactoryUtil.createFordwardDB(getActivity());
		for(Contact c:clist){
			//如果DB中已经存在则不添加
			boolean flag = db.isContainNumber(c.getContactNumber());
			if(!flag){
				db.insert(c.getContactName(), c.getContactNumber());
			}
		}
		ForwardScanner cs = new ForwardScanner(handler,getActivity());
		cs.start();
	}
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void showData(Object data){
		ArrayList<Contact> contacts = (ArrayList<Contact>) data;
		ForwardAdapter forwardAdapter = new ForwardAdapter(getActivity(),contacts,handler);
		setListAdapter(forwardAdapter);
		forwardAdapter.notifyDataSetChanged();
	}
	
	
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case Constants.RESPONSE_CODE_SHOW_FORWARD:
				showData(msg.obj);
				break;
			case Constants.RESPONSE_CODE_SHOW_REFRESH:
				showData(msg.obj);
				break;
			}
		}
	};
}
