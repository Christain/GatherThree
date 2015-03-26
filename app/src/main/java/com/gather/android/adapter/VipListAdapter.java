package com.gather.android.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.activity.UserCenter;
import com.gather.android.model.UserInfoModel;
import com.gather.android.utils.ClickUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class VipListAdapter extends BaseAdapter {
	
	private Context context;
	private LayoutInflater mInflater;
	private DisplayMetrics metrics;
	private ArrayList<UserInfoModel> list;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public VipListAdapter(Context context) {
		this.context = context;
		this.metrics = context.getResources().getDisplayMetrics();
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.list = new ArrayList<UserInfoModel>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public UserInfoModel getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_vip_list, null);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			holder.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
			holder.llItem = (FrameLayout) convertView.findViewById(R.id.llItem);
			
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.llItem.getLayoutParams();
			params.width = (int) (metrics.widthPixels - (context.getResources().getDimensionPixelOffset(R.dimen.vip_list_gridview_margin) * 2) - (context.getResources().getDimensionPixelOffset(R.dimen.vip_list_gridview_horizontalSpacing) * 2))/3;
			params.height = params.width * 35 / 23;
			holder.llItem.setLayoutParams(params);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserInfoModel model = getItem(position);
		imageLoader.displayImage(model.getHead_img_url(), holder.ivUserIcon, options);
		holder.tvUserName.setText(model.getNick_name());
		
		holder.llItem.setOnClickListener(new OnItemAllClickListener(model));
		return convertView;
	}
	
	private static class ViewHolder {
		public FrameLayout llItem;
		public ImageView ivUserIcon;
		public TextView tvUserName;
	}
	
	private class OnItemAllClickListener implements OnClickListener {
		
		private UserInfoModel model;
		
		public OnItemAllClickListener(UserInfoModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(context, UserCenter.class);
				intent.putExtra("UID", model.getUid());
				intent.putExtra("MODEL", model);
				context.startActivity(intent);
			}
		}
	}
	
	public void refreshItems(ArrayList<UserInfoModel> items) {
		list.clear();
		if (items != null) {
			list.addAll(items);
		}
		notifyDataSetChanged();
	}

	public void addItems(ArrayList<UserInfoModel> items) {
		if (items != null) {
			list.addAll(getCount(), items);
		}
		notifyDataSetChanged();
	}
	
	public void addItem(UserInfoModel item, int position) {
		if (item != null) {
			list.add(position, item);
		}
		notifyDataSetChanged();
	}
	
	public ArrayList<UserInfoModel> getList() {
		return list;
	}
	
	public void addItem(UserInfoModel item) {
		if (item != null) {
			list.add(item);
		}
		notifyDataSetChanged();
	}

}
