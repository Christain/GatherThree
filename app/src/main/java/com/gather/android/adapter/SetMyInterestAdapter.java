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
public class SetMyInterestAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private DisplayMetrics metrics;
	private Context mContext;
	private ArrayList<UserInterestModel> list;

	public SetMyInterestAdapter(Context context) {
		this.mContext = context;
		this.metrics = context.getResources().getDisplayMetrics();
		this.list = new ArrayList<UserInterestModel>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
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
			convertView = mInflater.inflate(R.layout.item_set_my_interest_gridview, null);
			holder = new ViewHolder();
			holder.tvInterest = (TextView) convertView.findViewById(R.id.tvInterest);

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvInterest.getLayoutParams();
			params.width = (int) ((metrics.widthPixels - (metrics.density * 40 + 0.5f) - mContext.getResources().getDimensionPixelOffset(R.dimen.set_interest_padding) * 2) / 5);
			params.height = params.width;
			holder.tvInterest.setLayoutParams(params);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (position % 2 == 0) {
			holder.tvInterest.setBackgroundResource(R.drawable.bg_set_interest_green);
			holder.tvInterest.setTextColor(0xFF44D276);
		} else {
			holder.tvInterest.setBackgroundResource(R.drawable.bg_set_interest_orange);
			holder.tvInterest.setTextColor(0xFFFF9933);
		}
		holder.tvInterest.setText(list.get(position).getName());
		return convertView;
	}

	private static class ViewHolder {
		TextView tvInterest;
	}
	
	public void setNotifyChanged(ArrayList<UserInterestModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

}
