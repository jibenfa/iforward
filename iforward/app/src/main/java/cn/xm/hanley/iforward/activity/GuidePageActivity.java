package cn.xm.hanley.iforward.activity;

import java.util.ArrayList;
import java.util.List;

import cn.xm.hanley.iforward.TabSwipePage;
import cn.xm.hanley.iforward.adapter.GuidePageAdapter;
import cn.xm.hanley.iforward.constants.Constants;

import android.app.Activity;
import android.app.role.RoleManager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 操作指南界面
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-25下午2:32:33
 * @Google+ www.google.com/+hanleytang
 */
public class GuidePageActivity extends Activity {

	private ViewPager viewPager;
	private GuidePageAdapter guidePageAdapter;
	private List<View> listViews;
	private int viewSize;
	private int currentSelected;
	
	//可点击的圆点图片
	private ImageView[] imageRoundPoint;
	
	//切换的背景图片
	private final static int viewBackground[] = { 
												R.drawable.guide01,
												R.drawable.guide02, 
												R.drawable.guide03,};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestScreeningSetup();
		
		SharedPreferences sp = getSharedPreferences(Constants.SPS_FORWARD, Context.MODE_PRIVATE);
		boolean guideFlag = sp.getBoolean(Constants.SPS_GUIDE_FLAG, true);
		if(guideFlag){
			setContentView(R.layout.layout_guide_page);
			initGuidePage();
			initImgRoundPoint();
			sp.edit().putBoolean(Constants.SPS_GUIDE_FLAG, false).commit();
		}else{
			boolean flag = this.getIntent().getBooleanExtra("flag", false);
			if(flag){
				setContentView(R.layout.layout_guide_page);
				initGuidePage();
				initImgRoundPoint();
			}else{
				Intent intent = new Intent(this,TabSwipePage.class);
				startActivity(intent);
				finish();
			}
		}
	}

	/** Request contacts access and the user-selected call-screening role. */
	private void requestScreeningSetup() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return;
		if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 41);
			return;
		}
		requestCallScreeningRole();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 41) requestCallScreeningRole();
	}

	private void requestCallScreeningRole() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			RoleManager roleManager = getSystemService(RoleManager.class);
			if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)
					&& !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
				startActivityForResult(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING), 42);
			}
		}
	}
	
	private void initGuidePage() {
		listViews = new ArrayList<View>();
		LayoutInflater mLayoutInflater = getLayoutInflater();

		viewSize = viewBackground.length;
		for (int i = 0; i < viewSize; i++) {
			View view = mLayoutInflater.inflate(R.layout.layout_guide_container, null);
			view.setBackgroundResource(viewBackground[i]);
			listViews.add(view);
		}
		
		LayoutInflater viewInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View appStart = viewInflater.inflate(R.layout.layout_app_start, null);
		Button btnStart = (Button) appStart.findViewById(R.id.start);
		btnStart.setOnClickListener(btnOnClickListener);
		listViews.add(appStart);
		viewSize++;
		
		guidePageAdapter = new GuidePageAdapter(listViews);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(guidePageAdapter);
		viewPager.setOnPageChangeListener(onPageChangeListener);
	}
	
	
	private void initImgRoundPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
		imageRoundPoint = new ImageView[viewSize];
		for (int i = 0; i < viewSize; i++) {
			imageRoundPoint[i] = (ImageView) linearLayout.getChildAt(i);
			imageRoundPoint[i].setEnabled(true);
			imageRoundPoint[i].setOnClickListener(imageRoundPointOnClickListener);
			imageRoundPoint[i].setTag(i);
		}
		currentSelected = 0;
		imageRoundPoint[currentSelected].setEnabled(false);
	}

	private void setCurrentView(int position) {
		if (position < 0 || position >= viewSize) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	private void setCurrentPoint(int position) {
		if (position < 0 || position > viewSize - 1 || currentSelected == position) {
			return;
		}
		imageRoundPoint[currentSelected].setEnabled(true);
		imageRoundPoint[position].setEnabled(false);
		currentSelected = position;
	}

	
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int state) {

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			setCurrentPoint(position);
		}
		
	};

	//圆点图片点击事件
	private OnClickListener imageRoundPointOnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			setCurrentView(position);
			setCurrentPoint(position);
		}
	};

	//按钮点击事件
	private OnClickListener btnOnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(GuidePageActivity.this,TabSwipePage.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);//切换Activity的过渡动
			finish();
		}
	};
}
