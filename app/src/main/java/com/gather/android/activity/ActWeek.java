package com.gather.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.fragment.ActListFragment;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.UserInterestList;
import com.gather.android.model.UserInterestModel;
import com.gather.android.params.GetActTagsParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * 本周活动列表
 */
public class ActWeek extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, mCursor;
	private TextView tvTitle, tvNearby, tvSearch;
	private ArrayList<TextView> mTextList;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private int titleIndex, mCursorWidth = 0, offset = 0;

	private ArrayList<UserInterestModel> tagList = new ArrayList<UserInterestModel>();
	private LinearLayout linearLayout;
	private RelativeLayout rlBar;

	private LoadingDialog mLoadingDialog;
	private GatherApplication application;

	@Override
	protected int layoutResId() {
		return R.layout.act_week;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.application = (GatherApplication) getApplication();
		this.mLoadingDialog = LoadingDialog.createDialog(ActWeek.this, true);
		this.ivLeft = (ImageView) findViewById(R.id.ivBack);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvTitle.setText("本周活动");
		this.tvNearby = (TextView) findViewById(R.id.tvNearby);
		this.tvSearch = (TextView) findViewById(R.id.tvSearch);
		this.rlBar = (RelativeLayout) findViewById(R.id.rlBar);
		this.linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);

		initView();
	}

	private void initView() {
		SharedPreferences actMarkPreferences = getSharedPreferences("ACT_MARK_LIST_" + application.getCityId(), Context.MODE_PRIVATE);
		String actMark = actMarkPreferences.getString("MARK", "");
		if (!actMark.equals("")) {
			Gson gson = new Gson();
			UserInterestList list = gson.fromJson(actMark, UserInterestList.class);
			if (list != null) {
				tagList = list.getTags();
				if (tagList.size() == 0 || tagList.size() == 1) {
					rlBar.setVisibility(View.GONE);
				} else {
					rlBar.setVisibility(View.VISIBLE);
//					UserInterestModel model = new UserInterestModel();
//					model.setName("全部");
//					model.setId(0);
//					tagList.add(0, model);
					mTextList = new ArrayList<TextView>();
					ColorStateList csl = (ColorStateList) getBaseContext().getResources().getColorStateList(R.drawable.friends_list_tab_text_color);
					for (int i = 0; i < tagList.size(); i++) {
						TextView textView = new TextView(ActWeek.this);
						textView.setText(tagList.get(i).getName());
						textView.setGravity(Gravity.CENTER);
						textView.setTextColor(csl);
						textView.setOnClickListener(new TabOnClickListener(i));
						textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
						mTextList.add(textView);
						linearLayout.addView(textView);
					}
					this.mCursor = (ImageView) findViewById(R.id.cursor);
					this.InitImageView();
				}
			} else {
				rlBar.setVisibility(View.GONE);
			}

			this.viewPager = (ViewPager) findViewById(R.id.viewpager);
			this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
			this.viewPager.setAdapter(adapter);
			this.viewPager.setOffscreenPageLimit(0);
			this.viewPager.setPageMargin(10);
			this.viewPager.setCurrentItem(0);
			if (tagList.size() > 0) {
				this.viewPager.setOnPageChangeListener(new OnViewPagerChangedListener());
			}

			this.ivLeft.setOnClickListener(this);
			this.tvNearby.setOnClickListener(this);
			this.tvSearch.setOnClickListener(this);
		} else {
			getActMarkList();
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
		case R.id.tvNearby:
			if (!ClickUtil.isFastClick() && tagList != null) {
				Intent intent = new Intent(ActWeek.this, ActNearby.class);
				startActivity(intent);
			}
			break;
		case R.id.tvSearch:
			if (!ClickUtil.isFastClick() && tagList != null) {
				Intent intent = new Intent(ActWeek.this, ActSearch.class);
				startActivity(intent);
			}
			break;
		}
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		titleIndex = 0;
		for (int i = 0; i < mTextList.size(); i++) {
			if (i == 0) {
				mTextList.get(i).setSelected(true);
				mTextList.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			} else {
				mTextList.get(i).setSelected(false);
				mTextList.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			}
		}
		mCursor = (ImageView) findViewById(R.id.cursor);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		LayoutParams params = (LayoutParams) mCursor.getLayoutParams();
		params.width = (metrics.widthPixels - (getResources().getDimensionPixelOffset(R.dimen.collection_tabs_padding_left_right) * 2)) / tagList.size();
		mCursor.setLayoutParams(params);
		mCursor.setPadding(40, 0, 40, 0);
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
		int four = one * 4;
		int five = one * 5;
		int six = one * 6;

		@Override
		public void onPageSelected(int position) {
			Animation animation = null;
			switch (position) {
			case 0:
				switch (titleIndex) {
				case 1:
					animation = new TranslateAnimation(one, 0, 0, 0);
					break;
				case 2:
					animation = new TranslateAnimation(two, 0, 0, 0);
					break;
				case 3:
					animation = new TranslateAnimation(three, 0, 0, 0);
					break;
				case 4:
					animation = new TranslateAnimation(four, 0, 0, 0);
					break;
				case 5:
					animation = new TranslateAnimation(five, 0, 0, 0);
					break;
				case 6:
					animation = new TranslateAnimation(six, 0, 0, 0);
					break;
				}
				break;
			case 1:
				switch (titleIndex) {
				case 0:
					animation = new TranslateAnimation(offset, one, 0, 0);
					break;
				case 2:
					animation = new TranslateAnimation(two, one, 0, 0);
					break;
				case 3:
					animation = new TranslateAnimation(three, one, 0, 0);
					break;
				case 4:
					animation = new TranslateAnimation(four, one, 0, 0);
					break;
				case 5:
					animation = new TranslateAnimation(five, one, 0, 0);
					break;
				case 6:
					animation = new TranslateAnimation(six, one, 0, 0);
					break;
				}
				break;
			case 2:
				switch (titleIndex) {
				case 0:
					animation = new TranslateAnimation(offset, two, 0, 0);
					break;
				case 1:
					animation = new TranslateAnimation(one, two, 0, 0);
					break;
				case 3:
					animation = new TranslateAnimation(three, two, 0, 0);
					break;
				case 4:
					animation = new TranslateAnimation(four, two, 0, 0);
					break;
				case 5:
					animation = new TranslateAnimation(five, two, 0, 0);
					break;
				case 6:
					animation = new TranslateAnimation(six, two, 0, 0);
					break;
				}
				break;
			case 3:
				switch (titleIndex) {
				case 0:
					animation = new TranslateAnimation(offset, three, 0, 0);
					break;
				case 1:
					animation = new TranslateAnimation(one, three, 0, 0);
					break;
				case 2:
					animation = new TranslateAnimation(two, three, 0, 0);
					break;
				case 4:
					animation = new TranslateAnimation(four, three, 0, 0);
					break;
				case 5:
					animation = new TranslateAnimation(five, three, 0, 0);
					break;
				case 6:
					animation = new TranslateAnimation(six, three, 0, 0);
					break;
				}
				break;
			case 4:
				switch (titleIndex) {
				case 0:
					animation = new TranslateAnimation(offset, four, 0, 0);
					break;
				case 1:
					animation = new TranslateAnimation(one, four, 0, 0);
					break;
				case 2:
					animation = new TranslateAnimation(two, four, 0, 0);
					break;
				case 3:
					animation = new TranslateAnimation(three, four, 0, 0);
					break;
				case 5:
					animation = new TranslateAnimation(five, four, 0, 0);
					break;
				case 6:
					animation = new TranslateAnimation(six, four, 0, 0);
					break;
				}
				break;
			case 5:
				switch (titleIndex) {
				case 0:
					animation = new TranslateAnimation(offset, five, 0, 0);
					break;
				case 1:
					animation = new TranslateAnimation(one, five, 0, 0);
					break;
				case 2:
					animation = new TranslateAnimation(two, five, 0, 0);
					break;
				case 3:
					animation = new TranslateAnimation(three, five, 0, 0);
					break;
				case 4:
					animation = new TranslateAnimation(four, five, 0, 0);
					break;
				case 6:
					animation = new TranslateAnimation(six, five, 0, 0);
					break;
				}
				break;
			case 6:
				switch (titleIndex) {
				case 0:
					animation = new TranslateAnimation(offset, six, 0, 0);
					break;
				case 1:
					animation = new TranslateAnimation(one, six, 0, 0);
					break;
				case 2:
					animation = new TranslateAnimation(two, six, 0, 0);
					break;
				case 3:
					animation = new TranslateAnimation(three, six, 0, 0);
					break;
				case 4:
					animation = new TranslateAnimation(four, six, 0, 0);
					break;
				case 5:
					animation = new TranslateAnimation(five, six, 0, 0);
					break;
				}
				break;
			}
			animation.setFillAfter(true);
			animation.setDuration(200);
			mCursor.startAnimation(animation);
			titleIndex = position;
			for (int i = 0; i < mTextList.size(); i++) {
				if (i == position) {
					mTextList.get(i).setSelected(true);
					mTextList.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				} else {
					mTextList.get(i).setSelected(false);
					mTextList.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
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
			ActListFragment fragment = new ActListFragment();
			Bundle bundle = new Bundle();
			if (tagList.size() != 0) {
				bundle.putInt("TYPE", tagList.get(arg0).getId());
			} else {
				bundle.putInt("TYPE", 0);
			}
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			if (tagList.size() != 0) {
				return tagList.size();
			} else {
				return 1;
			}
		}
	}

	/**
	 * 获取活动标签列表
	 */
	private void getActMarkList() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("加载中...");
			mLoadingDialog.show();
		}
		GetActTagsParam param = new GetActTagsParam(application.getCityId());
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                SharedPreferences cityPreferences = ActWeek.this.getSharedPreferences("ACT_MARK_LIST_" + application.getCityId(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = cityPreferences.edit();
                editor.putString("MARK", result);
                editor.commit();
                initView();
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("获取活动标签出错");
                finish();
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("获取活动标签出错");
                finish();
            }
        });
	}

}
