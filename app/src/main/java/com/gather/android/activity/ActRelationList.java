package com.gather.android.activity;

import android.content.Intent;
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
import com.gather.android.fragment.ActHasCommentListFragment;
import com.gather.android.fragment.ActHasEnrollListFragment;
import com.gather.android.fragment.ActHasSignListFragment;
import com.gather.android.fragment.CollectionActListFragment;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

/**
 * 活动相关列表（已关注，已评论，已报名，已签到）
 */
public class ActRelationList extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, mCursor;
	private TextView tvTitle;
	private TextView mTabs[] = new TextView[4];
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private int titleIndex, mCursorWidth = 0, offset = 0;

	private final static int FOCUS = 0;
	private final static int ENROLL = 1;
	private final static int SIGN = 2;
	private final static int COMMENT = 3;

	private CollectionActListFragment actListFragment; // 已关注列表
	private ActHasEnrollListFragment actHasEnrollListFragment;// 已报名列表
	private ActHasSignListFragment actHasSignListFragment;// 已签到列表
	private ActHasCommentListFragment actHasCommentListFragment;// 已评论列表

	private int userId;

	@Override
	protected int layoutResId() {
		return R.layout.act_relation_list;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("UID")) {
			this.userId = intent.getExtras().getInt("UID");
			this.ivLeft = (ImageView) findViewById(R.id.ivBack);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvTitle.setText("活动");

			this.mTabs[FOCUS] = (TextView) findViewById(R.id.tvFocus);
			this.mTabs[COMMENT] = (TextView) findViewById(R.id.tvComment);
			this.mTabs[ENROLL] = (TextView) findViewById(R.id.tvEnroll);
			this.mTabs[SIGN] = (TextView) findViewById(R.id.tvSign);
			this.mCursor = (ImageView) findViewById(R.id.cursor);
			this.InitImageView();
			this.viewPager = (ViewPager) findViewById(R.id.viewpager);
			this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
			this.viewPager.setAdapter(adapter);
			this.viewPager.setOffscreenPageLimit(0);
			this.viewPager.setCurrentItem(FOCUS);
			this.viewPager.setOnPageChangeListener(new OnViewPagerChangedListener());

			this.ivLeft.setOnClickListener(this);
			this.mTabs[FOCUS].setOnClickListener(new TabOnClickListener(FOCUS));
			this.mTabs[COMMENT].setOnClickListener(new TabOnClickListener(COMMENT));
			this.mTabs[ENROLL].setOnClickListener(new TabOnClickListener(ENROLL));
			this.mTabs[SIGN].setOnClickListener(new TabOnClickListener(SIGN));
		} else {
			toast("活动相关信息错误");
			finish();
		}
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
		params.width = (metrics.widthPixels - (getResources().getDimensionPixelOffset(R.dimen.collection_tabs_padding_left_right) * 2)) / 4;
		mCursor.setLayoutParams(params);
		mCursor.setPadding(32, 0, 32, 0);
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
			case FOCUS:
				switch (titleIndex) {
				case ENROLL:
					animation = new TranslateAnimation(one, 0, 0, 0);
					break;
				case SIGN:
					animation = new TranslateAnimation(two, 0, 0, 0);
					break;
				case COMMENT:
					animation = new TranslateAnimation(three, 0, 0, 0);
					break;
				}
				break;
			case ENROLL:
				switch (titleIndex) {
				case FOCUS:
					animation = new TranslateAnimation(offset, one, 0, 0);
					break;
				case SIGN:
					animation = new TranslateAnimation(two, one, 0, 0);
					break;
				case COMMENT:
					animation = new TranslateAnimation(three, one, 0, 0);
					break;
				}
				break;
			case SIGN:
				switch (titleIndex) {
				case FOCUS:
					animation = new TranslateAnimation(offset, two, 0, 0);
					break;
				case ENROLL:
					animation = new TranslateAnimation(one, two, 0, 0);
					break;
				case COMMENT:
					animation = new TranslateAnimation(three, two, 0, 0);
					break;
				}
				break;
			case COMMENT:
				switch (titleIndex) {
				case FOCUS:
					animation = new TranslateAnimation(offset, three, 0, 0);
					break;
				case ENROLL:
					animation = new TranslateAnimation(one, three, 0, 0);
					break;
				case SIGN:
					animation = new TranslateAnimation(two, three, 0, 0);
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
				actListFragment = new CollectionActListFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("UID", userId);
				actListFragment.setArguments(bundle);
				return actListFragment;
			} else if (arg0 == 1) {
				actHasEnrollListFragment = new ActHasEnrollListFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("UID", userId);
				actHasEnrollListFragment.setArguments(bundle);
				return actHasEnrollListFragment;
			} else if (arg0 == 2) {
				actHasSignListFragment = new ActHasSignListFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("UID", userId);
				actHasSignListFragment.setArguments(bundle);
				return actHasSignListFragment;
			} else {
				actHasCommentListFragment = new ActHasCommentListFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("UID", userId);
				actHasCommentListFragment.setArguments(bundle);
				return actHasCommentListFragment;
			}
		}

		@Override
		public int getCount() {
			return 4;
		}
	}

}
