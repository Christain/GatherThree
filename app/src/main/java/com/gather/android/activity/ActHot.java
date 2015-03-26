package com.gather.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.fragment.ActHotListFragment;
import com.gather.android.fragment.ActStrategyListFragment;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 热门活动
 */
public class ActHot extends SwipeBackActivity implements OnClickListener {

	private ImageView ivBack, mCursor;
	private TextView tvTitle;
	private TextView mTabs[] = new TextView[2];
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private int titleIndex, mCursorWidth = 0, offset = 0;

	private final static int ACT = 0;
	private final static int STRATEGY = 1;

	private ActHotListFragment actHotListFragment;
	private ActStrategyListFragment actStrategyListFragment;

	@Override
	protected int layoutResId() {
		return R.layout.act_hot;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.ivBack = (ImageView) findViewById(R.id.ivBack);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);

		this.tvTitle.setText("热门");
		this.ivBack.setImageResource(R.drawable.title_back_click_style);
		this.ivBack.setOnClickListener(this);

		this.mTabs[ACT] = (TextView) findViewById(R.id.tvAct);
		this.mTabs[STRATEGY] = (TextView) findViewById(R.id.tvInformation);
		this.mCursor = (ImageView) findViewById(R.id.cursor);
		this.InitImageView();
		this.viewPager = (ViewPager) findViewById(R.id.viewpager);
		this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
		this.viewPager.setAdapter(adapter);
        this.viewPager.setOffscreenPageLimit(0);
        this.viewPager.setCurrentItem(ACT);
        this.viewPager.setOnPageChangeListener(new OnViewPagerChangedListener());

		this.mTabs[ACT].setOnClickListener(new TabOnClickListener(ACT));
		this.mTabs[STRATEGY].setOnClickListener(new TabOnClickListener(STRATEGY));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		}
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		titleIndex = 0;
		for (int i = 0; i < mTabs.length; i++) {
			if (i == 0) {
				mTabs[i].setSelected(true);
				mTabs[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			} else {
				mTabs[i].setSelected(false);
				mTabs[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			}
		}
		mCursor = (ImageView) findViewById(R.id.cursor);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		LayoutParams params = (LayoutParams) mCursor.getLayoutParams();
		params.width = (metrics.widthPixels - (getResources().getDimensionPixelOffset(R.dimen.collection_tabs_padding_left_right) * 2)) / 2;
		mCursor.setLayoutParams(params);
		mCursor.setPadding(getResources().getDimensionPixelOffset(R.dimen.collection_tabs_padding_left_right), 0, getResources().getDimensionPixelOffset(R.dimen.collection_tabs_padding_left_right), 0);
		mCursorWidth = params.width;
		offset = 0;
	}

	/**
	 * 头标点击监听
	 */
	private class TabOnClickListener implements OnClickListener {
		private int index = 0;

		public TabOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	};

	/**
	 * 切换页卡监听
	 */
	private class OnViewPagerChangedListener implements OnPageChangeListener {
		int one = offset * 2 + mCursorWidth;
		int two = one * 2;
		int three = one * 3;

		@Override
		public void onPageSelected(int position) {
			Animation animation = null;
			switch (position) {
			case ACT:
				switch (titleIndex) {
				case STRATEGY:
					animation = new TranslateAnimation(one, 0, 0, 0);
					break;
				}
				break;
			case STRATEGY:
				switch (titleIndex) {
				case ACT:
					animation = new TranslateAnimation(offset, one, 0, 0);
					break;
				}
				break;
			}
			titleIndex = position;
			animation.setFillAfter(true);
			animation.setDuration(200);
			mCursor.startAnimation(animation);
			for (int i = 0; i < mTabs.length; i++) {
				if (i == position) {
					mTabs[i].setSelected(true);
					mTabs[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				} else {
					mTabs[i].setSelected(false);
					mTabs[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			if (arg0 == 0) {
				actHotListFragment = new ActHotListFragment();
				return actHotListFragment;
			} else {
				actStrategyListFragment = new ActStrategyListFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("TAG_ID", 0);
				bundle.putInt("TYPE", ActStrategyAndMemoryAndTicket.STRATEGY);
				actStrategyListFragment.setArguments(bundle);
				return actStrategyListFragment;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

}
