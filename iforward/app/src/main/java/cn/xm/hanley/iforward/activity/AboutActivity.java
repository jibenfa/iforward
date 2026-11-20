package cn.xm.hanley.iforward.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about);
		
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			android.view.View customView = getLayoutInflater().inflate(R.layout.anction_bar, null);
			ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
					ActionBar.LayoutParams.MATCH_PARENT,
					ActionBar.LayoutParams.MATCH_PARENT,
					Gravity.CENTER);
			actionBar.setCustomView(customView, layoutParams);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(true);
			TextView titleActionbar = customView.findViewById(R.id.title_action_bar);
			if (titleActionbar != null) {
				titleActionbar.setText(getResources().getString(R.string.about));
				int paddingEnd = (int) (48 * getResources().getDisplayMetrics().density);
				titleActionbar.setPadding(0, 0, paddingEnd, 0);
			}
		}

		try {
			PackageInfo packageInfo;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.PackageInfoFlags.of(0));
			} else {
				packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			}
			TextView tvVersion = findViewById(R.id.about_version);
			if (tvVersion != null && packageInfo != null && packageInfo.versionName != null) {
				tvVersion.setText(getString(R.string.about_version, packageInfo.versionName));
			}
		} catch (Exception ignored) {
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
