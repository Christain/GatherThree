package com.gather.android.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.model.TrendsCommentModel;

public class TrendsCommentAdapter extends BaseAdapter {
	
	private Context mContext;
	protected LayoutInflater mInflater;
	private ArrayList<TrendsCommentModel> list;
	private OnCommentClickListener listener;
	private OnCommentLongClickListener longListener;
	
	public TrendsCommentAdapter(Context context) {
		this.mContext = context;
		this.list = new ArrayList<TrendsCommentModel>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public TrendsCommentModel getItem(int position) {
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
			holder = new ViewHolder();
			this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_trends_comment, null);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.line = (View) convertView.findViewById(R.id.line);
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItemAll);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		TrendsCommentModel model = getItem(position);
		holder.tvContent.setText(model.getContent());
		if (model.getAt_id() == 0) {
			holder.tvContent.setText(NameProgress(model.getAuthor_user().getNick_name() + ": " + model.getContent()));
		} else {
			holder.tvContent.setText(NameReplayProgress(model.getAuthor_user().getNick_name() + " 回复" + model.getAt_user().getNick_name() + ": " + model.getContent()));
		}
		holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model, position));
		holder.llItemAll.setOnLongClickListener(new OnItemAllLongClickListener(model, position));
		return convertView;
	}

	private static class ViewHolder {
		public View line;
		public TextView tvContent;
		public LinearLayout llItemAll;
	}
	
	/**
	 * 关键字颜色区别
	 */
	private Spannable NameProgress(String name) {
		int start = 0;
		int end = name.indexOf(":", 1);
		Spannable word = new SpannableString(name);
		word.setSpan(new ForegroundColorSpan(0xff29ABE2), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return word;
	}
	
	/**
	 * 关键字颜色区别
	 */
	private Spannable NameReplayProgress(String name) {
		int start = 0;
		int end = name.indexOf(" 回复", 1);
		Spannable word = new SpannableString(name);
		word.setSpan(new ForegroundColorSpan(0xff29ABE2), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		int begin = name.indexOf(" 回复", 1) + 3;
		int over = name.indexOf(":", 1);
		word.setSpan(new ForegroundColorSpan(0xff29ABE2), begin, over, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return word;
	}
	
	/**
	 * 点击Item
	 */
	private class OnItemAllClickListener implements OnClickListener {
		
		private TrendsCommentModel model;
		private int position;
		
		public OnItemAllClickListener(TrendsCommentModel model, int position) {
			this.model = model;
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			if (listener != null) {
				listener.OnCommentClick(model, position);
			}
		}
	}
	
	/**
	 * 长按Item
	 */
	private class OnItemAllLongClickListener implements OnLongClickListener {
		
		private TrendsCommentModel model;
		private int position;
		
		public OnItemAllLongClickListener (TrendsCommentModel model, int position) {
			this.model = model;
			this.position = position;
		}
		@Override
		public boolean onLongClick(View arg0) {
			if (longListener != null) {
				longListener.OnCommentLongClick(model, position);
			}
			return true;
		}
		
	}
	
	public interface OnCommentClickListener {
		public void OnCommentClick(TrendsCommentModel model, int position);
	}
	
	public void setOnCommentClickListener(OnCommentClickListener listener) {
		this.listener = listener;
	}
	
	public interface OnCommentLongClickListener {
		public void OnCommentLongClick(TrendsCommentModel model, int position);
	}
	
	public void setOnCommentLongClickListener(OnCommentLongClickListener listener) {
		this.longListener = listener;
	}
	
	
	public void refreshItems(ArrayList<TrendsCommentModel> items) {
		list.clear();
		if (items != null) {
			list.addAll(items);
		}
		notifyDataSetChanged();
	}

	public void addItems(ArrayList<TrendsCommentModel> items) {
		if (items != null) {
			list.addAll(getCount(), items);
		}
		notifyDataSetChanged();
	}
	
	public void addItem(TrendsCommentModel item, int position) {
		if (item != null) {
			list.add(position, item);
		}
		notifyDataSetChanged();
	}
	
	public ArrayList<TrendsCommentModel> getList() {
		return list;
	}
	
	public void addItem(TrendsCommentModel item) {
		if (item != null) {
			list.add(item);
		}
		notifyDataSetChanged();
	}

}
