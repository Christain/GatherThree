package com.gather.android.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.preference.AppPreference;

/**
 * 切换卡消息
 */
public class MessageFragment extends BaseFragment implements OnClickListener {

	private View convertView;
	private TextView tvTitle;

	private int titleType = 0;
	private String[] titleArray = { "私信", "系统消息" };
	private TextView tvSystem, tvChat;
	private ImageView mCursor;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private int mCursorWidth, offset = 0, one;
	private int index, screenW;
	private ChatMessageFragment chat;

	@Override
	protected void OnCreate(Bundle savedInstanceState) {
		if (AppPreference.hasLogin(getActivity())) {
			DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
			screenW = metrics.widthPixels;
			mCursorWidth = BitmapFactory.decodeResource(getResources(), R.drawable.tab_friends_list).getWidth();
			offset = (screenW / 2 - mCursorWidth) / 2;
			LayoutInflater inflater = getActivity().getLayoutInflater();
			this.convertView = inflater.inflate(R.layout.fragment_message, (ViewGroup) getActivity().findViewById(R.id.tabhost), false);

			this.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			this.tvTitle.setText("消息");
			this.tvSystem = (TextView) convertView.findViewById(R.id.tvSystem);
			this.tvChat = (TextView) convertView.findViewById(R.id.tvChat);
			this.mCursor = (ImageView) convertView.findViewById(R.id.cursor);
			this.viewPager = (ViewPager) convertView.findViewById(R.id.viewpager);
			this.adapter = new ViewPagerAdapter(getChildFragmentManager());
			viewPager.setAdapter(adapter);
			viewPager.setOffscreenPageLimit(0);

			viewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {
					Animation animation = null;
					switch (position) {
					case 0:
						tvChat.setSelected(true);
						tvSystem.setSelected(false);
						tvChat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						tvSystem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
						if (titleType == 1) {
							if (index == 0) {
								animation = new TranslateAnimation(one, 0, 0, 0);
							} else {
								animation = new TranslateAnimation(0, -one, 0, 0);
							}
						}
						break;
					case 1:
						tvChat.setSelected(false);
						tvSystem.setSelected(true);
						tvChat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
						tvSystem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						if (titleType == 0) {
							if (index == 0) {
								animation = new TranslateAnimation(0, one, 0, 0);
							} else {
								animation = new TranslateAnimation(-one, 0, 0, 0);
							}
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

			this.tvSystem.setOnClickListener(this);
			this.tvChat.setOnClickListener(this);
		}
	}

	@Override
	protected View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (convertView != null) {
			ViewGroup p = (ViewGroup) convertView.getParent();
			if (p != null) {
				p.removeAllViews();
			}
			return convertView;
		} else {
			return null;
		}
	}

	@Override
	protected void OnSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void OnActivityCreated(Bundle savedInstanceState) {
		if (AppPreference.hasLogin(getActivity())) {
			InitImageView();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvSystem:
			viewPager.setCurrentItem(1);
			tvSystem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			tvChat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			break;
		case R.id.tvChat:
			viewPager.setCurrentItem(0);
			tvSystem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			tvChat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == 100) {
				if (data != null) {
					if (data.hasExtra("REFRESH") || data.hasExtra("STATUS")) {
						chat.getRefresh();
					}
				}
			}
		}
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		if (titleType == 0) {
			index = 0;
			tvChat.setSelected(true);
			tvSystem.setSelected(false);
			tvChat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			tvSystem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			Matrix matrix = new Matrix();
			matrix.postTranslate(offset, 0);
			mCursor.setImageMatrix(matrix);
			one = screenW / 2;
		} else {
			index = 1;
			tvChat.setSelected(false);
			tvSystem.setSelected(true);
			tvChat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			tvSystem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			Matrix matrix = new Matrix();
			matrix.postTranslate(offset + screenW / 2, 0);
			mCursor.setImageMatrix(matrix);
			one = screenW / 2;
		}
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				chat = new ChatMessageFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("TYPE", position);
				chat.setArguments(bundle);
				return chat;
			} else {
				SystemMessageFragment system = new SystemMessageFragment();
				Bundle bundle = new Bundle();
				bundle.putInt("TYPE", position);
				system.setArguments(bundle);
				return system;
			}

		}

		@Override
		public int getCount() {
			return titleArray.length;
		}
	}

}
