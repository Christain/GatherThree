package com.gather.android.adapter;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gather.android.R;
import com.gather.android.activity.PublishTrendsPicGallery;
import com.gather.android.activity.TrendsPicGallery;
import com.gather.android.model.TrendsPicModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class TrendsPicAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private DisplayMetrics metrics;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ArrayList<TrendsPicModel> list;
	private int type;//1是切换卡动态列表，2是个人动态列表，3是评论动态页面
	private boolean local;//是否是本地数据库图片
	
	public TrendsPicAdapter(Context context, ArrayList<TrendsPicModel> list, int type) {
		this.mContext = context;
		this.list = list;
		this.type = type;
		this.local = false;
		this.metrics = context.getResources().getDisplayMetrics();
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	
	public TrendsPicAdapter(Context context, ArrayList<TrendsPicModel> list, int type, boolean local) {
		this.mContext = context;
		this.list = list;
		this.type = type;
		this.metrics = context.getResources().getDisplayMetrics();
		this.local = true;
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public TrendsPicModel getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_trends_pic, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);

			switch (type) {
			case 1:
				LinearLayout.LayoutParams paramsOne = (LinearLayout.LayoutParams) holder.imageView.getLayoutParams();
				paramsOne.width = (int) ((metrics.widthPixels - (metrics.density * 5 + 0.5f) * 2 - mContext.getResources().getDimensionPixelOffset(R.dimen.friends_list_item_padding_left_right) * 2 - mContext.getResources().getDimensionPixelOffset(R.dimen.trends_user_icon_size) - mContext.getResources().getDimensionPixelOffset(R.dimen.friends_list_item_padding_top_bottom))/3);
				paramsOne.height = paramsOne.width;
				holder.imageView.setLayoutParams(paramsOne);
				break;
			case 2:
				LinearLayout.LayoutParams paramsTwo= (LinearLayout.LayoutParams) holder.imageView.getLayoutParams();
				paramsTwo.width = (int) ((metrics.widthPixels - (metrics.density * 5 + 0.5f) * 2 - mContext.getResources().getDimensionPixelOffset(R.dimen.friends_list_item_padding_left_right) * 2 - mContext.getResources().getDimensionPixelOffset(R.dimen.user_trends_time_base_max_width) - mContext.getResources().getDimensionPixelOffset(R.dimen.friends_list_item_padding_top_bottom))/3);
				paramsTwo.height = paramsTwo.width;
				holder.imageView.setLayoutParams(paramsTwo);
				break;
			case 3:
				LinearLayout.LayoutParams paramsThree = (LinearLayout.LayoutParams) holder.imageView.getLayoutParams();
				paramsThree.width = (int) ((metrics.widthPixels - (metrics.density * 5 + 0.5f) * 2 - mContext.getResources().getDimensionPixelOffset(R.dimen.friends_list_item_padding_left_right) * 2 - mContext.getResources().getDimensionPixelOffset(R.dimen.trends_user_icon_size) - mContext.getResources().getDimensionPixelOffset(R.dimen.friends_list_item_padding_top_bottom))/3);
				paramsThree.height = paramsThree.width;
				holder.imageView.setLayoutParams(paramsThree);
				break;
			}

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TrendsPicModel model = getItem(position);
		if (local) {
			imageLoader.displayImage(Uri.decode(Uri.fromFile(new File(model.getImg_url())).toString()), holder.imageView, options);
		} else {
			imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImg_url(), 150, 150, 50), holder.imageView, options);
		}
		holder.imageView.setOnClickListener(new OnImageClickListener(position));
		return convertView;
	}

	private static class ViewHolder {
		public ImageView imageView;
	}
	
	/**
	 * 图片点击
	 */
	private class OnImageClickListener implements OnClickListener {
		
		private int position;
		
		public OnImageClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				if (local) {
					ArrayList<String> imgList = new ArrayList<String>();
					for (int i = 0; i < list.size(); i++) {
						imgList.add(list.get(i).getImg_url());
					}
					Intent intent = new Intent(mContext, PublishTrendsPicGallery.class);
					intent.putExtra("LIST", imgList);
					intent.putExtra("POSITION", position);
					mContext.startActivity(intent);
					((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				} else {
					Intent intent = new Intent(mContext, TrendsPicGallery.class);
					intent.putExtra("LIST", list);
					intent.putExtra("POSITION", position);
					mContext.startActivity(intent);
					((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}
		}
	}

}
