package cn.xm.hanley.iforward.fragment;

import cn.xm.hanley.iforward.activity.R;
import cn.xm.hanley.iforward.activity.SelContactActivity;
import cn.xm.hanley.iforward.constants.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 设置转接号码
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-16下午3:53:05
 * @Google+ www.google.com/+hanleytang
 */
public class CustomFragment extends Fragment{

	private Button numberSettings;
	private TextView displayNumber;
	private TextView tipBegin;
	protected static final int CODE_REQUEST_CONTACT = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fm_custom, container, false);
	}
	
	@SuppressWarnings({"static-access" })
	private void init(){
		SharedPreferences share = getActivity().getSharedPreferences(Constants.SPS_FORWARD,getActivity().MODE_WORLD_WRITEABLE);
		String fnumber = share.getString(Constants.SPS_FORWARD_NUMBER, "");
		if(null != fnumber && fnumber.length() > 0){
			displayNumber.setText(fnumber);
			tipBegin.setVisibility(View.GONE);
		}else{
			tipBegin.setVisibility(View.VISIBLE);
			displayNumber.setText(getResources().getString(R.string.tip_no_number_settings));
		}
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findViewByIds();
		setListeners();
		init();
	}

	private void findViewByIds(){
		numberSettings = (Button)getActivity().findViewById(R.id.number_setings);
		displayNumber = (TextView)getActivity().findViewById(R.id.display_number);
		tipBegin = (TextView)getActivity().findViewById(R.id.tip_begin);
	}
	
	private void setListeners(){
		numberSettings.setOnClickListener(btnClickListener);
	}
	
	
	private OnClickListener btnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			clickOperation();
		}
		
	};

	private void clickOperation(){
		
		String contents[] = {
			getResources().getString(R.string.manually_add),
			getResources().getString(R.string.contacts_add_number),
			getResources().getString(R.string.disable_forward_number),
		};
		
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setTitle(getResources().getString(R.string.forward_number));
		b.setItems(contents, itemSelected);
		b.show();
	}
	
	public DialogInterface.OnClickListener itemSelected = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case 0:
				manuallyAdd();
				break;
			case 1:
				Intent intent = new Intent(getActivity(),SelContactActivity.class);
				startActivityForResult(intent, CODE_REQUEST_CONTACT);
				break;
			case 2:
				disableForward();
				break;
			}
		}
		
	};
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == CODE_REQUEST_CONTACT){
				String num = data.getStringExtra("number");
				beginForward(num);
				numberSaveSPS(num);
			}
		}
	}

	private void manuallyAdd(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.manually_add));
		final LayoutInflater factory = LayoutInflater.from(getActivity());
		final View newView = factory.inflate(R.layout.layout_manually_add, null);
		final EditText editText = (EditText)newView.findViewById(R.id.manually_number);
		builder.setView(newView);
		builder.setPositiveButton(getResources().getString(R.string.ok), new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String manuallyNumber = editText.getText().toString();
				beginForward(manuallyNumber);
				numberSaveSPS(manuallyNumber);
			}});
		builder.setNegativeButton(getResources().getString(R.string.cancle), null);
		builder.create();
		builder.show();
	}
	
	private void beginForward(String manuallyNumber){
		if(null != manuallyNumber && manuallyNumber.length() > 0){
			displayNumber.setText(manuallyNumber);
			Intent intent = new Intent(Intent.ACTION_CALL);
			String newNumber = bulidNumber(manuallyNumber);
			intent.setData(Uri.parse(newNumber));
			getActivity().startActivity(intent);
			tipBegin.setVisibility(View.GONE);
		}
	}
	
	private void disableForward(){
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:%23%2367%23"));
		getActivity().startActivity(intent);
		displayNumber.setText(getResources().getString(R.string.tip_no_number_settings));
		numberSaveSPS("");
		tipBegin.setVisibility(View.VISIBLE);
	}
	
	private String bulidNumber(String number){
		StringBuffer sb = new StringBuffer();
		sb.append("tel:**67*");
		sb.append(number);
		sb.append("%23");
		return sb.toString();
	}
	
	@SuppressWarnings("static-access")
	private void numberSaveSPS(String number){
		SharedPreferences share = getActivity().getSharedPreferences(Constants.SPS_FORWARD,getActivity().MODE_WORLD_WRITEABLE);
		share.edit().putString(Constants.SPS_FORWARD_NUMBER, number).commit();
	}
	
}
