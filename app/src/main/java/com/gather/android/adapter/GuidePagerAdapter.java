package com.gather.android.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class GuidePagerAdapter extends PagerAdapter {

	private List<View> mListViews;
	
	public GuidePagerAdapter(List<View> listViews) {
		// TODO Auto-generated constructor stub
		mListViews = listViews;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
//		super.destroyItem(container, position, object);
		container.removeView(mListViews.get(position));
	}
	
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
//		return super.instantiateItem(container, position);
		container.addView(mListViews.get(position), 0);
		return mListViews.get(position);
	}
	
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		// TODO Auto-generated method stub
		return view == obj;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListViews.size();
	}

}
