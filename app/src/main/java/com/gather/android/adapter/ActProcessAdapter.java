package com.gather.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.model.ActProcessModel;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/3/18.
 */
public class ActProcessAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ActProcessModel> list;
    private LayoutInflater mInflater;

    public ActProcessAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = new ArrayList<ActProcessModel>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ActProcessModel getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_act_process_list, null);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ActProcessModel model = getItem(position);
        holder.tvTime.setText(model.getB_time()+"-"+ model.getE_time());
        holder.tvName.setText(model.getSubject());
        switch (model.getStatus()) {
            case 0:
                holder.tvStatus.setText("未设置");
                holder.tvStatus.setTextColor(0xFFD3D3D3);
                break;
            case 1:
                holder.tvStatus.setText("即将开始");
                holder.tvStatus.setTextColor(0xFFD3D3D3);
                break;
            case 2:
                holder.tvStatus.setText("正在进行");
                holder.tvStatus.setTextColor(0xFFFF9933);
                break;
            case 3:
                holder.tvStatus.setText("已完成 ");
                holder.tvStatus.setTextColor(0xFF67BE5F);
                break;
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvTime, tvName, tvStatus;
    }

    public void setActProcessList(ArrayList<ActProcessModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
