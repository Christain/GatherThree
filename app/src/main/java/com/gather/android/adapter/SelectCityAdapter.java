package com.gather.android.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.model.CityListModel;

public class SelectCityAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private ArrayList<CityListModel> list;

	public SelectCityAdapter(Context context) {
		this.context = context;
		this.list = new ArrayList<CityListModel>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public CityListModel getItem(int position) {
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
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_select_city, null);
			holder = new ViewHolder();
			holder.llCity = (LinearLayout) convertView.findViewById(R.id.llCity);
			holder.tvCity = (TextView) convertView.findViewById(R.id.tvCity);
			holder.ivSelect = (ImageView) convertView.findViewById(R.id.ivSelect);
			holder.topline = (View) convertView.findViewById(R.id.topline);
			holder.bottomline = (View) convertView.findViewById(R.id.bottomline);
			holder.midline = (View) convertView.findViewById(R.id.midline);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CityListModel model = (CityListModel) getItem(position);
		holder.tvCity.setText(model.getName());
		if (position == 0) {
			holder.topline.setVisibility(View.VISIBLE);
			holder.midline.setVisibility(View.GONE);
		} else {
			holder.midline.setVisibility(View.VISIBLE);
			holder.topline.setVisibility(View.GONE);
		}
		if (position == list.size() - 1) {
			holder.midline.setVisibility(View.GONE);
			holder.bottomline.setVisibility(View.VISIBLE);
		} else {
			holder.midline.setVisibility(View.VISIBLE);
			holder.bottomline.setVisibility(View.GONE);
		}
		if (model.isSelect()) {
			holder.ivSelect.setVisibility(View.VISIBLE);
		} else {
			holder.ivSelect.setVisibility(View.GONE);
		}
		return convertView;
	}

	private static class ViewHolder {
		public LinearLayout llCity;
		public TextView tvCity;
		public ImageView ivSelect;
		public View topline, bottomline, midline;
	}
	
	public void setNotifyData(ArrayList<CityListModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

}
