package com.gather.android.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gather.android.R;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.utils.ThumbnailUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

@SuppressLint("InflateParams")
public class UserPhotoAdapter extends BaseAdapter {
	
	private Context context;
	private DisplayMetrics metrics;
	private ArrayList<UserPhotoModel> list;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public UserPhotoAdapter(Context context) {
		this.context = context;
		this.metrics = context.getResources().getDisplayMetrics();
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.list = new ArrayList<UserPhotoModel>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public UserPhotoModel getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_user_center_pic, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.ivPic);

			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) holder.imageView.getLayoutParams();
			params.width = (int) ((metrics.widthPixels - (metrics.density * 32 + 0.5f)) / 3);
			params.height = params.width * 35 / 23;
			holder.imageView.setLayoutParams(params);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserPhotoModel model = getItem(position);
		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImg_url(), 250, 250, 50), holder.imageView, options);
		return convertView;
	}
	
	private static class ViewHolder{
		public ImageView imageView;
	}
	
	public void setPicLis(ArrayList<UserPhotoModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

}
