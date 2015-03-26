package com.gather.android.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.model.UserInterestModel;

@SuppressLint("InflateParams")
public class VipClassifyGridviewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private DisplayMetrics metrics;
	private Context mContext;
	private ArrayList<UserInterestModel> list;

	public VipClassifyGridviewAdapter(Context context) {
		this.mContext = context;
		this.metrics = context.getResources().getDisplayMetrics();
		this.list = new ArrayList<UserInterestModel>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public UserInterestModel getItem(int position) {
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
			this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_vip_classify_gridview, null);
			holder = new ViewHolder();
			holder.tvInterest = (TextView) convertView.findViewById(R.id.tvInterest);

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvInterest.getLayoutParams();
			params.width = (int) ((metrics.widthPixels - mContext.getResources().getDimensionPixelOffset(R.dimen.vip_classify_gridview_padding) * 2 - mContext.getResources().getDimensionPixelOffset(R.dimen.vip_classify_margin_left_right) * 2) / 3);
			params.height = params.width * 2 / 6;
			holder.tvInterest.setLayoutParams(params);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserInterestModel model = getItem(position);
		holder.tvInterest.setText(model.getName());
		if (model.isSelect()) {
			holder.tvInterest.setSelected(true);
		} else {
			holder.tvInterest.setSelected(false);
		}
		return convertView;
	}

	private static class ViewHolder {
		public TextView tvInterest;
	}

	public void setNotifyChanged(ArrayList<UserInterestModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

}
