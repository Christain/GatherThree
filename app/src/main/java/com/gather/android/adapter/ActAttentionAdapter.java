package com.gather.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.model.ActAttentionModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/3/28.
 */
public class ActAttentionAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<ActAttentionModel> list;
    private LayoutInflater mInflater;

    public ActAttentionAdapter(Context context, ArrayList<ActAttentionModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ActAttentionModel getItem(int position) {
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
            holder = new ViewHolder();
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_act_attention, null);
            holder.tvAttention = (TextView) convertView.findViewById(R.id.tvAttention);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ActAttentionModel model = getItem(position);
        holder.tvAttention.setText(model.getSubject());
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvAttention;
    }
}
