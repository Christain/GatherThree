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
import android.widget.LinearLayout;

import com.gather.android.R;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.utils.ThumbnailUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

@SuppressLint("InflateParams")
public class UserCenterPicAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private Context context;
	private DisplayMetrics metrics;
	private ArrayList<UserPhotoModel> list;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options; 
	
	public UserCenterPicAdapter(Context context) {
		this.context = context;
		this.metrics = context.getResources().getDisplayMetrics();
		this.list = new ArrayList<UserPhotoModel>();
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_user_center_pic, null);
			holder = new ViewHolder();
			holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivPic.getLayoutParams();
			params.width = (int) ((metrics.widthPixels - 40) / 3);
			params.height = params.width * 35 / 23;
			holder.ivPic.setLayoutParams(params);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserPhotoModel model = getItem(position);
		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImg_url(), 250, 250, 50), holder.ivPic, options);
		return convertView;
	}
	
	private static class ViewHolder {
		public ImageView ivPic;
	}
	
	public void setNotifyChanged(ArrayList<UserPhotoModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
}
