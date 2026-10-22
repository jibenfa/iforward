package cn.xm.hanley.iforward.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class GuidePageAdapter extends PagerAdapter {

	List<View> viewList;

	public GuidePageAdapter(List<View> viewList) {
		this.viewList = viewList;
	}

	@Override
	public int getCount() {
		if (viewList != null) {
			return viewList.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(View view, int position) {
		ViewPager vp = (ViewPager)view;
		vp.addView(viewList.get(position), 0);
		return viewList.get(position);
	}

	@Override
	public void destroyItem(View view, int position, Object object) {
		ViewPager vp = (ViewPager)view;
		vp.removeView(viewList.get(position));
	}

	@Override
	public void finishUpdate(View container) {

	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View container) {

	}

}
