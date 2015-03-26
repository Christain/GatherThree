package com.gather.android.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.AlbumGridViewAdapter;
import com.gather.android.imgcache.ImageCache;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class Album extends SwipeBackActivity implements OnClickListener {
	private ArrayList<String> selectedDataList = new ArrayList<String>();
	private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
	public static HashMap<Integer, Integer> mChoosedMap = new HashMap<Integer, Integer>();
	private GridView gridView;
	private ArrayList<String> dataList = new ArrayList<String>();
	private ArrayList<String> cameraDir;// = "/DCIM/"
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	private LinearLayout selectedImageLayout;
	private Button okButton;
	private HorizontalScrollView scrollview;
	private int maxChose;
	private String Name;
	private boolean isCanClick = true;

	private TextView tvLeft, tvRight, tvTitle;
	private ImageView ivLeft, ivRight;

	private DisplayImageOptions options;

	@Override
	protected int layoutResId() {
		return R.layout.album;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		mChoosedMap.clear();
		Intent intent = getIntent();
		cameraDir = intent.getStringArrayListExtra("path");
		maxChose = intent.getExtras().getInt("maxChose");
		Name = intent.getStringExtra("name");
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		init();
		initListener();
	}

	private int calculateSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int widthPixels = metrics.widthPixels;
		float density = metrics.density;
		int width = (widthPixels - (int) (25 * density + 0.5f)) / 4;
		return width;
	}

	private void init() {
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.GONE);
		this.tvTitle.setText(Name);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.ivRight.setVisibility(View.GONE);
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, cameraDir, calculateSize());
		gridView.setAdapter(gridImageAdapter);
		refreshData();
		selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
		okButton = (Button) findViewById(R.id.ok_button);
		String ok = selectedDataList.size() + "/" + maxChose;
		okButton.setText("完成(" + ok + ")");
		scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);
		if (Build.VERSION.SDK_INT >= 9) {
			gridView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
			scrollview.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
		}
	}

	private void initListener() {
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (selectedDataList.size() <= maxChose && isCanClick) {
					isCanClick = false;
					String path = gridImageAdapter.getItem(position);
					if (selectedDataList.size() == maxChose) {
						if (removePath(path, position)) {
							View view = gridView.getChildAt(position - gridView.getFirstVisiblePosition());
							if (view != null) {
								CheckBox cBox = (CheckBox) view.findViewById(R.id.cbChoosed);
								cBox.setChecked(mChoosedMap.containsKey(position));
							}
						}
					} else {
						if (!removePath(path, position)) {
							ImageView imageView = (ImageView) LayoutInflater.from(Album.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
							imageView.setScaleType(ScaleType.CENTER_CROP);
							imageView.setTag(path);
							selectedImageLayout.addView(imageView);
							imageView.postDelayed(new Runnable() {
								@Override
								public void run() {
									int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
									if (off > 0) {
										scrollview.smoothScrollTo(off, 0);
									}
								}
							}, 100);

							mChoosedMap.put(position, position);
							hashMap.put(path, imageView);
							selectedDataList.add(path);
							ImageCache.from(Album.this).displayImageSD(imageView, path, R.drawable.default_image, 200, 200);
							// imageLoader.displayImage(Uri.decode(Uri.fromFile(new
							// File(path)).toString()), imageView, options);
							imageView.setOnClickListener(new OnPickedImageClickListener(path, position));
							String ok = selectedDataList.size() + "/" + maxChose;
							okButton.setText("完成(" + ok + ")");
						}
						View view = gridView.getChildAt(position - gridView.getFirstVisiblePosition());
						if (view != null) {
							CheckBox cBox = (CheckBox) view.findViewById(R.id.cbChoosed);
							cBox.setChecked(mChoosedMap.containsKey(position));
						}
					}
					isCanClick = true;
				}
			}
		});
		okButton.setOnClickListener(this);
	}

	private class OnPickedImageClickListener implements OnClickListener {
		private String imgPath;
		private int position;

		public OnPickedImageClickListener(String path, int position) {
			this.imgPath = path;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			removePath(imgPath, position);
			View view = gridView.getChildAt(position - gridView.getFirstVisiblePosition());
			if (view != null) {
				CheckBox cBox = (CheckBox) view.findViewById(R.id.cbChoosed);
				cBox.setChecked(mChoosedMap.containsKey(position));
			}
			// gridImageAdapter.notifyDataSetChanged();
		}

	}

	private boolean removePath(String path, int position) {
		if (hashMap.containsKey(path)) {
			mChoosedMap.remove(position);
			selectedImageLayout.removeView(hashMap.get(path));
			hashMap.remove(path);
			selectedDataList.remove(path);
			String ok = selectedDataList.size() + "/" + maxChose;
			// tv_title.setText(ok);
			okButton.setText("完成(" + ok + ")");
			return true;
		} else {
			return false;
		}
	}

	private void refreshData() {
		if (Album.this == null || Album.this.isFinishing()) {
			return;
		}
		progressBar.setVisibility(View.GONE);
		dataList.clear();
		dataList.addAll(cameraDir);
		gridImageAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.ok_button:
			if (!ClickUtil.isFastClick()) {
				if (selectedDataList.size() != 0) {
					Intent intent = new Intent();
					intent.putExtra("SELECT_LIST", selectedDataList);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					toast("请先选择图片");
				}
			}
			break;
		}
	}

}
