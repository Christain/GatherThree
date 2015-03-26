package com.gather.android.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.TouchGalleyAdapter;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.model.TrendsPicModel;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.widget.touchgallery.GalleryViewPager;

/**
 * 动态图片浏览TrendsFragment
 */
public class TrendsPicGallery extends BaseActivity {

	private TextView tvNum;
	private GalleryViewPager gallery;
	private TouchGalleyAdapter adapter;
	private ArrayList<String> resources;
	private int position;

	@Override
	protected int layoutResId() {
		return R.layout.touch_gallery;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("POSITION")) {
			resources = new ArrayList<String>();
			if (intent.hasExtra("LIST")) {
				ArrayList<TrendsPicModel> list = new ArrayList<TrendsPicModel>();
				list = (ArrayList<TrendsPicModel>) intent.getSerializableExtra("LIST");
				for (int i = 0; i < list.size(); i++) {
					resources.add(list.get(i).getImg_url());
				}
			} else if (intent.hasExtra("USER_PHOTO_LIST")) {
				ArrayList<UserPhotoModel> list = new ArrayList<UserPhotoModel>();
				list = (ArrayList<UserPhotoModel>) intent.getSerializableExtra("USER_PHOTO_LIST");
				for (int i = 0; i < list.size(); i++) {
					resources.add(list.get(i).getImg_url());
				}
			}
			this.position = intent.getExtras().getInt("POSITION");
			this.tvNum = (TextView) findViewById(R.id.tvNum);
			this.gallery = (GalleryViewPager) findViewById(R.id.viewpager);
			this.gallery.setOnPageChangeListener(new myPageChangeListener());
			this.gallery.setOffscreenPageLimit(3);
			this.adapter = new TouchGalleyAdapter(TrendsPicGallery.this, resources);
			this.gallery.setAdapter(adapter);

			this.initView();
		} else {
			finish();
			toast("图片参数错误~~");
		}
	}

	private void initView() {
		if (resources.size() == 1) {
			tvNum.setVisibility(View.GONE);
		} else {
			tvNum.setVisibility(View.VISIBLE);
			tvNum.setText((position + 1) + "/" + resources.size());
		}
		gallery.setCurrentItem(position);
	}

	private class myPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			tvNum.setText((position + 1) + "/" + resources.size());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		}
		return true;
	}

}
