package com.gather.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.gather.android.R;
import com.gather.android.activity.Album;
import com.gather.android.imgcache.ImageCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class AlbumGridViewAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> dataList;
	private int width = 80;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public AlbumGridViewAdapter(Context c, ArrayList<String> dataList, int width) {
		mContext = c;
		this.dataList = dataList;
		this.width = width;
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public String getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * 存放列表项控件句柄
	 */
	private static class ViewHolder {
		public ImageView imageView;
		public CheckBox cbChoosed;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.select_imageview, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
			viewHolder.cbChoosed = (CheckBox) convertView.findViewById(R.id.cbChoosed);
			LayoutParams params = viewHolder.imageView.getLayoutParams();
			params.width = width;
			params.height = width;
			viewHolder.imageView.setLayoutParams(params);
			viewHolder.cbChoosed.setLayoutParams(params);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ImageCache.from(mContext).displayImageSD(viewHolder.imageView, dataList.get(position), R.drawable.default_image, 200, 200);
//		imageLoader.displayImage(Uri.decode(Uri.fromFile(new File(dataList.get(position))).toString()), viewHolder.imageView, options);
		viewHolder.cbChoosed.setChecked(Album.mChoosedMap.containsKey(position));

		return convertView;
	}

	// @Override
	// public void onClick(View view) {
	// if (view instanceof ToggleButton) {
	// ToggleButton toggleButton = (ToggleButton) view;
	// int position = (Integer) toggleButton.getTag();
	// if (dataList != null && mOnItemClickListener != null && position <
	// dataList.size()) {
	// mOnItemClickListener.onItemClick(toggleButton, position,
	// dataList.get(position),
	// toggleButton.isChecked());
	// }
	// }
	// }

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(ToggleButton view, int position, String path, boolean isChecked);
	}

}
