package cn.xm.hanley.iforward.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about);
		
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.anction_bar);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		TextView titleActionbar = (TextView) findViewById(R.id.title_action_bar);
		titleActionbar.setText(getResources().getString(R.string.about));
	}

}
