package com.gather.android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gather.android.R;
import com.gather.android.model.PickedImageModel;
import com.gather.android.utils.ClickUtil;

public class PickedImagesAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<PickedImageModel> list = new ArrayList<PickedImageModel>();;
	private OnPickedItemClickListener mListener;
	private int max_num;

	public PickedImagesAdapter(Context content, int max_num) {
		this.context = content;
		this.max_num = max_num;
	}

	public void setListener(OnPickedItemClickListener l) {
		this.mListener = l;
	}

	public boolean isFull() {
		return list.size() == max_num;
	}

	public int getImagesNum() {
		return list.size();
	}

	public List<File> getImagesList() {
		List<File> imagesList = new ArrayList<File>();
		for (int i = 0; i < list.size(); i++) {
			imagesList.add(list.get(i).getImageFile());
		}
		return imagesList;
	}
	
	public ArrayList<PickedImageModel> getList() {
		return list;
	}

	public void add(PickedImageModel model) {
		this.list.add(model);
		notifyDataSetChanged();
	}

	public void add(List<PickedImageModel> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	public void reAdd(int location, PickedImageModel model) {
		if (location >= 0 && location < list.size()) {
			list.get(location).destroy();
			list.remove(location);
			this.list.add(location, model);
		}
		notifyDataSetChanged();
	}

	public void del(int position) {
		if (position >= 0 && position < list.size()) {
			list.get(position).destroy();
			list.remove(position);
		}
		notifyDataSetChanged();
	}

	public void destroy() {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).destroy();
		}
		list.clear();
	}

	public void destroyAll() {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).destroyAll();
		}
		list.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (isFull()) {
			return list.size();
		} else {
			return list.size() + 1;
		}
	}

	@Override
	public PickedImageModel getItem(int position) {
		if (position >= 0 && position < list.size()) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_publish_trends_pic, null);
			holder = new ViewHolder();
			holder.ivImage = (ImageView) convertView.findViewById(R.id.imageView);
			holder.ibtImgClick = (ImageButton) convertView.findViewById(R.id.ibtClick);
			holder.ibtImgClick.setVisibility(View.VISIBLE);
			
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			LayoutParams params = holder.ivImage.getLayoutParams();
			if (max_num == 3) {
				params.width = (metrics.widthPixels - context.getResources().getDimensionPixelOffset(R.dimen.publish_trends_gridview_margin) * 2 - context.getResources().getDimensionPixelOffset(R.dimen.publish_trends_gridview_padding) * 2) / 3;
			} else {
				params.width = (metrics.widthPixels - context.getResources().getDimensionPixelOffset(R.dimen.publish_trends_gridview_margin) * 2 - context.getResources().getDimensionPixelOffset(R.dimen.publish_trends_gridview_padding) * 3) / 4;
			}
			params.height = params.width;
			holder.ivImage.setLayoutParams(params);
			holder.ibtImgClick.setLayoutParams(params);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!isFull() && position == getCount() - 1) {
			holder.ibtImgClick.setImageResource(R.drawable.shape_add_pic_click_style);
			holder.ibtImgClick.setOnClickListener(new OnItemClick(-10));
			holder.ivImage.setImageBitmap(null);
		} else {
			holder.ibtImgClick.setImageBitmap(null);
			holder.ivImage.setImageBitmap(getItem(position).getImageBmp());
			holder.ibtImgClick.setOnClickListener(new OnItemClick(position));
		}
		return convertView;
	}

	private class ViewHolder {
		public ImageView ivImage;
		public ImageButton ibtImgClick;
	}

	private class OnItemClick implements OnClickListener {
		private int postion;

		public OnItemClick(int p) {
			this.postion = p;
		}

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				if (!ClickUtil.isFastClick()) {
					if (this.postion == -10) {
						mListener.addImage();
					} else {
						mListener.onImageItemClicked(this.postion);
					}
				}
			}
		}

	}

	public interface OnPickedItemClickListener {
		public void onImageItemClicked(int position);

		public void addImage();
	}

}
