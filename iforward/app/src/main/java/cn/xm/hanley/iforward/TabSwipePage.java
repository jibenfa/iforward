package cn.xm.hanley.iforward;

import cn.xm.hanley.iforward.activity.AboutActivity;
import cn.xm.hanley.iforward.activity.GuidePageActivity;
import cn.xm.hanley.iforward.activity.R;
import cn.xm.hanley.iforward.fragment.CustomFragment;
import cn.xm.hanley.iforward.fragment.ForwardFragment;
import cn.xm.hanley.iforward.fragment.HistoryFragment;
import android.Manifest;
import android.app.role.RoleManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 主界面
 * @author HanleyTang
 * @email  tanghly@gmail.com
 * @date   2012-8-22下午3:19:18
 * @Google+ www.google.com/+hanleytang
 */
public class TabSwipePage extends FragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private String pendingCallUri;
    private TextView[] tabViews;


    private void requestScreeningSetup() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return;
        ArrayList<String> missing = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.READ_CONTACTS);
        }
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.CALL_PHONE);
        }
        if (!missing.isEmpty()) {
            requestPermissions(missing.toArray(new String[0]), 41);
            return;
        }
        requestCallScreeningRole();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 41) {
            requestCallScreeningRole();
        } else if (requestCode == 43) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (granted && pendingCallUri != null) {
                startCarrierCodeCall(pendingCallUri);
            } else {
                Toast.makeText(this, "需要电话权限才能设置运营商呼叫转移", Toast.LENGTH_LONG).show();
            }
            pendingCallUri = null;
        }
    }

    public void placeCarrierCode(String telUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            pendingCallUri = telUri;
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 43);
            return;
        }
        startCarrierCodeCall(telUri);
    }

    private void startCarrierCodeCall(String telUri) {
        try {
            Intent call = new Intent(Intent.ACTION_CALL, Uri.parse(telUri));
            startActivity(call);
        } catch (SecurityException e) {
            Toast.makeText(this, "没有电话权限，无法设置运营商呼叫转移", Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestScreeningSetup();
        setContentView(R.layout.tabswipe_main);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabViews = new TextView[] {
                (TextView) findViewById(R.id.tab_number),
                (TextView) findViewById(R.id.tab_objects),
                (TextView) findViewById(R.id.tab_history)
        };
        for (int i = 0; i < tabViews.length; i++) {
            final int page = i;
            tabViews[i].setOnClickListener(v -> mViewPager.setCurrentItem(page, true));
        }
        updateSelectedTab(0);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateSelectedTab(position);
            }
        });
        findViewById(R.id.menu_button).setOnClickListener(this::showMoreMenu);
        View root = findViewById(R.id.main_root);
        final int initialLeft = root.getPaddingLeft();
        final int initialTop = root.getPaddingTop();
        final int initialRight = root.getPaddingRight();
        final int initialBottom = root.getPaddingBottom();
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(initialLeft, initialTop + insets.getSystemWindowInsetTop(), initialRight,
                    initialBottom + insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        });
        root.requestApplyInsets();
    }

    private void updateSelectedTab(int selected) {
        if (tabViews == null) return;
        for (int i = 0; i < tabViews.length; i++) {
            TextView tab = tabViews[i];
            boolean active = i == selected;
            tab.setTextColor(getResources().getColor(active ? R.color.brand_teal : R.color.text_secondary));
            tab.setTypeface(Typeface.DEFAULT, active ? Typeface.BOLD : Typeface.NORMAL);
            tab.setBackgroundResource(active ? R.drawable.bg_tab_selected : R.drawable.bg_tab_default);
        }
    }

    private void showMoreMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_title, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
        popup.show();
    }

    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_title, menu);
        return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_about) {
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		} else if (itemId == R.id.menu_guide) {
			Intent intent = new Intent(this, GuidePageActivity.class);
			intent.putExtra("flag", true);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            
            Fragment newFragment = null;
            switch(i){
	    	case 0:
	    		newFragment = new CustomFragment();
	    		break;
			case 1:
				newFragment = new ForwardFragment();
	    		break;
			case 2:
				newFragment = new HistoryFragment();
	    		break;
	    	default:
	    		break;
	    	}
	    	
	    	return newFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            	case 0: return getString(R.string.forward_number).toUpperCase();
                case 1: return getString(R.string.forward_object).toUpperCase();
                case 2: return getString(R.string.forward_history).toUpperCase();
            }
            return null;
        }
    }

}
