package com.gather.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gather.android.R;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.imgcache.ImageCache;
import com.gather.android.model.SelectPictureModel;
import com.gather.android.utils.PhotosSearch;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 选择相册图片
 */
public class SelectPicture extends SwipeBackActivity implements OnClickListener {

	private TextView tvLeft, tvRight, tvTitle;
	private ImageView ivLeft, ivRight;

	public static final String MAX_PICS_NUM = "MAX_PICS_NUM";
	public static final int MAX_NUM = 9;
	private int max_num;
	private List<SelectPictureModel> images;
	private Handler handler;
	private ListView listview;
	private LoadingDialog mLoadingDialog;
	private Myadapter adapter;
	
	private DisplayImageOptions options;

	@Override
	protected int layoutResId() {
		return R.layout.select_picture;
	}

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		mLoadingDialog = LoadingDialog.createDialog(SelectPicture.this, true);
		mLoadingDialog.setMessage("正在读取照片......");
		mLoadingDialog.show();
		max_num = getIntent().getIntExtra(MAX_PICS_NUM, MAX_NUM);
		
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.GONE);
		this.tvTitle.setText("选择图片");
		this.ivLeft.setVisibility(View.VISIBLE);
		this.ivRight.setVisibility(View.GONE);
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);

		this.listview = (ListView) this.findViewById(R.id.listview);
		if (Build.VERSION.SDK_INT >= 9) {
			listview.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		}

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing() && !SelectPicture.this.isFinishing()) {
					mLoadingDialog.dismiss();
				}
				if (images.size() > 0) {
					// 将所有的图片显示在listview中
					adapter = new Myadapter(SelectPicture.this);
					listview.setAdapter(adapter);
					listview.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
							SelectPictureModel model = (SelectPictureModel) adapter.getItem(position);
							if (null != model) {
								Intent intent = new Intent(SelectPicture.this, Album.class);
								intent.putExtra("path", model.getPicPath());
								intent.putExtra("maxChose", max_num);
								intent.putExtra("name", model.getPicName());
								startActivityForResult(intent, 100);
							}
						}
					});
				} else {
					Toast.makeText(SelectPicture.this, "没有图片", Toast.LENGTH_LONG).show();
				}
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				allScan();
				// 获得所有的图片
				PhotosSearch imageService = new PhotosSearch(SelectPicture.this);
				images = imageService.getImages();
				// Collections.sort(images, new PicCompare());
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	// 必须在查找前进行全盘的扫描，否则新加入的图片是无法得到显示的(加入对sd卡操作的权限)
	public void allScan() {
		MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()}, null, null);
	}

	class Myadapter extends BaseAdapter {
		private LayoutInflater inflater;

		public Myadapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public SelectPictureModel getItem(int position) {
			return images.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.item_select_picture, null);
				holder.imageName = (TextView) view.findViewById(R.id.name);
				holder.imageInfo = (TextView) view.findViewById(R.id.size);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			SelectPictureModel model = (SelectPictureModel) getItem(position);
			if (null != model) {
				holder.imageName.setText(model.getPicName());
				holder.imageInfo.setText("(" + model.getPicPath().size() + ")");
				String path = model.getPicPath().get(0);
				ImageCache.from(SelectPicture.this).displayImageSD(holder.image, path, R.drawable.default_image, 400, 400);
//				imageLoader.displayImage(Uri.decode(Uri.fromFile(new File(path)).toString()), holder.image, options);
			}
			return view;
		}

	}

	class ViewHolder {
		private TextView imageName;
		private TextView imageInfo;
		private ImageView image;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private String getChosedPic(ArrayList<String> list) {
		StringBuffer sb = new StringBuffer();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			if (i != size - 1) {
				sb.append(list.get(i));
				sb.append(",");
			} else {
				sb.append(list.get(i));
			}
		}
		list.clear();
		return sb.toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (data != null && data.hasExtra("SELECT_LIST")) {
				ArrayList<String> selectList = new ArrayList<String>();
				selectList = data.getStringArrayListExtra("SELECT_LIST");
				if (selectList.size() > 0) {
					String chosedPic = getChosedPic(selectList);
					Intent intent = new Intent();
					intent.putExtra("chosedPic", chosedPic);
					intent.setData(Uri.parse(chosedPic));
					setResult(RESULT_OK, intent);
				} else {
					setResult(RESULT_CANCELED);
				}
			}
		}
		finish();
	}

}