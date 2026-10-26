package cn.xm.hanley.iforward.fragment;

import cn.xm.hanley.iforward.activity.R;
import cn.xm.hanley.iforward.activity.SelContactActivity;
import cn.xm.hanley.iforward.TabSwipePage;
import cn.xm.hanley.iforward.constants.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
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
import android.widget.Switch;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import java.util.Locale;

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
	private Switch timeWindowSwitch;
	private Button startTimeButton;
	private Button endTimeButton;
	protected static final int CODE_REQUEST_CONTACT = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fm_custom, container, false);
	}
	
	@SuppressWarnings({"static-access" })
	private void init(){
		SharedPreferences share = getActivity().getSharedPreferences(Constants.SPS_FORWARD,Context.MODE_PRIVATE);
		String fnumber = share.getString(Constants.SPS_FORWARD_NUMBER, "");
		initTimeWindow(share);
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
		timeWindowSwitch = (Switch)getActivity().findViewById(R.id.time_window_switch);
		startTimeButton = (Button)getActivity().findViewById(R.id.time_window_start);
		endTimeButton = (Button)getActivity().findViewById(R.id.time_window_end);
	}
	
	private void setListeners(){
		numberSettings.setOnClickListener(btnClickListener);
		startTimeButton.setOnClickListener(v -> showTimePicker(true));
		endTimeButton.setOnClickListener(v -> showTimePicker(false));
		timeWindowSwitch.setOnCheckedChangeListener((button, checked) -> {
			getActivity().getSharedPreferences(Constants.SPS_FORWARD, Context.MODE_PRIVATE)
					.edit().putBoolean(Constants.SPS_TIME_WINDOW_ENABLED, checked).apply();
			updateTimeWindowControls();
		});
	}

	private void initTimeWindow(SharedPreferences preferences) {
		int start = preferences.getInt(Constants.SPS_TIME_WINDOW_START, 9 * 60);
		int end = preferences.getInt(Constants.SPS_TIME_WINDOW_END, 18 * 60);
		timeWindowSwitch.setChecked(preferences.getBoolean(Constants.SPS_TIME_WINDOW_ENABLED, false));
		updateTimeButton(startTimeButton, R.string.time_window_start, start);
		updateTimeButton(endTimeButton, R.string.time_window_end, end);
		updateTimeWindowControls();
	}

	private void updateTimeWindowControls() {
		boolean enabled = timeWindowSwitch.isChecked();
		startTimeButton.setEnabled(enabled);
		endTimeButton.setEnabled(enabled);
		startTimeButton.setAlpha(enabled ? 1.0f : 0.55f);
		endTimeButton.setAlpha(enabled ? 1.0f : 0.55f);
	}

	private void showTimePicker(boolean isStart) {
		SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SPS_FORWARD, Context.MODE_PRIVATE);
		String key = isStart ? Constants.SPS_TIME_WINDOW_START : Constants.SPS_TIME_WINDOW_END;
		int defaultValue = isStart ? 9 * 60 : 18 * 60;
		int current = preferences.getInt(key, defaultValue);
		new TimePickerDialog(getActivity(), (TimePicker view, int hour, int minute) -> {
			int value = hour * 60 + minute;
			preferences.edit().putInt(key, value).apply();
			updateTimeButton(isStart ? startTimeButton : endTimeButton,
					isStart ? R.string.time_window_start : R.string.time_window_end, value);
		}, current / 60, current % 60, true).show();
	}

	private void updateTimeButton(Button button, int labelResource, int minuteOfDay) {
		String time = String.format(Locale.getDefault(), "%02d:%02d", minuteOfDay / 60, minuteOfDay % 60);
		button.setText(getString(labelResource, time));
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
			String newNumber = bulidNumber(manuallyNumber);
			((TabSwipePage) getActivity()).placeCarrierCode(newNumber);
			tipBegin.setVisibility(View.GONE);
		}
	}
	
	private void disableForward(){
		((TabSwipePage) getActivity()).placeCarrierCode("tel:%23%2367%23");
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
		SharedPreferences share = getActivity().getSharedPreferences(Constants.SPS_FORWARD,Context.MODE_PRIVATE);
		share.edit().putString(Constants.SPS_FORWARD_NUMBER, number).commit();
	}
	
}
