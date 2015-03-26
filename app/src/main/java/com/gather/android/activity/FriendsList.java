package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.fragment.FriendsFragment;
import com.gather.android.preference.AppPreference;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.gather.android.widget.swipeback.SwipeBackLayout;

/**
 * 好友列表
 */
@SuppressLint("InflateParams")
public class FriendsList extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft;
	private TextView tvTitle;

	private int titleType = 0;
	private String[] titleArray = { "我的关注", "我的粉丝" };
	private TextView tvFocus, tvFans;
	private ImageView mCursor;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private int uid;
	private boolean isMe;

	private int mCursorWidth, offset = 0;

	@Override
	protected int layoutResId() {
		return R.layout.friends_list;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("UID")) {
			uid = intent.getExtras().getInt("UID");
			if (uid == AppPreference.getUserPersistentInt(FriendsList.this, AppPreference.USER_ID)) {
				isMe = true;
			} else {
				isMe = false;
			}
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);

			this.ivLeft.setVisibility(View.VISIBLE);
			this.tvTitle.setText("好友列表");
			this.ivLeft.setImageResource(R.drawable.icon_title_arrow_yellow);
			this.ivLeft.setOnClickListener(this);
			this.tvFocus = (TextView) findViewById(R.id.tvFocus);
			this.tvFans = (TextView) findViewById(R.id.tvFans);
			this.mCursor = (ImageView) findViewById(R.id.cursor);
			this.viewPager = (ViewPager) findViewById(R.id.viewpager);
			this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
			viewPager.setAdapter(adapter);
			viewPager.setOffscreenPageLimit(0);

			this.InitImageView();
			viewPager.setOnPageChangeListener(new OnPageChangeListener() {
				int one = offset * 2 + mCursorWidth;

				@Override
				public void onPageSelected(int position) {
					Animation animation = null;
					switch (position) {
					case 0:
						tvFocus.setSelected(true);
						tvFans.setSelected(false);
						tvFocus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						tvFans.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
						if (titleType == 1) {
							animation = new TranslateAnimation(one, 0, 0, 0);
						}
						break;
					case 1:
						tvFocus.setSelected(false);
						tvFans.setSelected(true);
						tvFocus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
						tvFans.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						if (titleType == 0) {
							animation = new TranslateAnimation(offset, one, 0, 0);
						}
						break;
					}
					titleType = position;
					animation.setFillAfter(true);
					animation.setDuration(200);
					mCursor.startAnimation(animation);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});
			
			if (!isMe) {
				tvFocus.setText("TA的关注");
				tvFans.setText("TA的粉丝");
			} else {
				tvFocus.setText("我的关注");
				tvFans.setText("我的粉丝");
			}
			this.tvFocus.setOnClickListener(this);
			this.tvFans.setOnClickListener(this);
		} else {
			toast("页面信息错误");
			finish();
		}
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		titleType = 0;
		tvFocus.setSelected(true);
		tvFans.setSelected(false);
		tvFocus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		tvFans.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		mCursor = (ImageView) findViewById(R.id.cursor);
		mCursorWidth = BitmapFactory.decodeResource(getResources(), R.drawable.tab_friends_list).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 2 - mCursorWidth) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		mCursor.setImageMatrix(matrix);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.tvFocus:
			viewPager.setCurrentItem(0);
			tvFocus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			tvFans.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			break;
		case R.id.tvFans:
			viewPager.setCurrentItem(1);
			tvFocus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			tvFans.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			break;
		}
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			FriendsFragment fragment = new FriendsFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("TYPE", arg0);
			bundle.putInt("UID", uid);
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return titleArray.length;
		}
	}

}
