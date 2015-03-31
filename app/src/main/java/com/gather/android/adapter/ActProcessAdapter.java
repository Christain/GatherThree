package com.gather.android.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
            holder.ivStatusSmall = (ImageView) convertView.findViewById(R.id.ivStatusSmall);
            holder.ivStatusBig = (ImageView) convertView.findViewById(R.id.ivStatusBIg);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ActProcessModel model = getItem(position);
        holder.tvTime.setText(model.getB_time()+"-"+ model.getE_time());
        holder.tvName.setText(model.getSubject());
        switch (model.getStatus()) {
            case 0://未设置
                holder.tvTime.setTextColor(0xFF6F7376);
                holder.tvName.setTextColor(0xFF6F7376);
                holder.tvTime.getPaint().setFlags(0);
                holder.tvName.getPaint().setFlags(0);
                holder.ivStatusSmall.setImageResource(R.drawable.icon_act_process_point);
                holder.ivStatusSmall.setVisibility(View.VISIBLE);
                holder.ivStatusBig.setVisibility(View.GONE);
                break;
            case 1://即将开始
                holder.tvTime.setTextColor(0xFF6E7379);
                holder.tvName.setTextColor(0xFF6E7379);
                holder.tvTime.getPaint().setFlags(0);
                holder.tvName.getPaint().setFlags(0);
                holder.ivStatusSmall.setImageResource(R.drawable.icon_act_process_point);
                holder.ivStatusSmall.setVisibility(View.VISIBLE);
                holder.ivStatusBig.setVisibility(View.GONE);
                break;
            case 2://正在进行
                holder.tvTime.setTextColor(0xFFFF9933);
                holder.tvName.setTextColor(0xFFFF9933);
                holder.tvTime.getPaint().setFlags(0);
                holder.tvName.getPaint().setFlags(0);
                holder.ivStatusBig.setImageResource(R.drawable.icon_act_process_point_big);
                holder.ivStatusSmall.setVisibility(View.GONE);
                holder.ivStatusBig.setVisibility(View.VISIBLE);
                break;
            case 3://已完成
                holder.tvTime.setTextColor(0xFFCCCCCC);
                holder.tvName.setTextColor(0xFFCCCCCC);
                holder.tvTime.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.ivStatusSmall.setImageResource(R.drawable.icon_act_process_point_gray);
                holder.ivStatusSmall.setVisibility(View.VISIBLE);
                holder.ivStatusBig.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvTime, tvName;
        public ImageView ivStatusSmall, ivStatusBig;
    }

    public void setActProcessList(ArrayList<ActProcessModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
