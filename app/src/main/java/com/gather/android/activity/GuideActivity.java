package com.gather.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;

import com.gather.android.R;
import com.gather.android.adapter.GuidePagerAdapter;
import com.gather.android.baseclass.BaseActivity;

@SuppressLint("InflateParams")
public class GuideActivity extends BaseActivity {

	private ViewPager mViewPager;
	private List<View> mListViews;

	@Override
	protected int layoutResId() {
		return R.layout.guide_activity;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		mViewPager = (ViewPager) findViewById(R.id.vPager);
		mListViews = new ArrayList<View>();
		ImageView imageViewOne = new ImageView(this);
		imageViewOne.setImageResource(R.drawable.guide_one);
		imageViewOne.setScaleType(ImageView.ScaleType.FIT_XY);
		ImageView imageViewTwo = new ImageView(this);
		imageViewTwo.setImageResource(R.drawable.guide_two);
		imageViewTwo.setScaleType(ImageView.ScaleType.FIT_XY);
		ImageView imageViewThree = new ImageView(this);
		imageViewThree.setImageResource(R.drawable.guide_three);
		imageViewThree.setScaleType(ImageView.ScaleType.FIT_XY);
		ImageView imageViewFour = new ImageView(this);
		imageViewFour.setImageResource(R.drawable.guide_four);
		imageViewFour.setScaleType(ImageView.ScaleType.FIT_XY);
		mListViews.add(imageViewOne);
		mListViews.add(imageViewTwo);
		mListViews.add(imageViewThree);
		mListViews.add(imageViewFour);
		mViewPager.setAdapter(new GuidePagerAdapter(mListViews));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new MainOnPageChangeListener());
		mViewPager.setCurrentItem(0);

	}

	class MainOnPageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			if (arg0 == 3) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(GuideActivity.this, IndexHome.class);
						GuideActivity.this.startActivity(intent);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}, 2000);
			}
		}
	}

}
