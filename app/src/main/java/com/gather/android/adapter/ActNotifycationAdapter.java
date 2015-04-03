package com.gather.android.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.model.ActNotifyModel;
import com.gather.android.utils.TimeUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christain on 2015/3/31.
 */
public class ActNotifycationAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ActNotifyModel> list;
    private LayoutInflater mInflater;
    private String compare = "";

    public ActNotifycationAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<ActNotifyModel>();
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i2) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_act_notify_group_view, null);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.ivArrow = (ImageView) convertView.findViewById(R.id.ivArrow);
            holder.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ActNotifyModel model = list.get(groupPosition);

        if(!isExpanded){
            holder.ivArrow.setImageResource(R.drawable.icon_act_notify_arrow_right);
        } else {
            holder.ivArrow.setImageResource(R.drawable.icon_act_notify_arrow_buttom);
        }
        if (groupPosition == 0) {
            holder.tvTitle.setTextColor(0xFFFF9933);
            holder.ivStatus.setImageResource(R.drawable.icon_act_process_point_big);
        } else {
            holder.tvTitle.setTextColor(0xFF6E7377);
            holder.ivStatus.setImageResource(R.drawable.icon_act_process_point_gray);
        }
        holder.tvTitle.setText(model.getSubject());
        if (!compare.equals("")) {
            SpannableString s = new SpannableString(model.getSubject());
            Pattern p = Pattern.compile(compare);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(0xFFFF9933), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            holder.tvTitle.setText(s);
        } else {
            holder.tvTitle.setText(model.getSubject());
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewChildHolder holder = null;
        if (convertView == null) {
            holder = new ViewChildHolder();
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_act_notify_child_view, null);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

            convertView.setTag(holder);
        } else {
            holder = (ViewChildHolder) convertView.getTag();
        }
        ActNotifyModel model = list.get(groupPosition);
        if (!compare.equals("")) {
            SpannableString s = new SpannableString(model.getDescri());
            Pattern p = Pattern.compile(compare);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(0xFFFF9933), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            holder.tvContent.setText(s);
        } else {
            holder.tvContent.setText(model.getDescri());
        }
        holder.tvTime.setText(TimeUtil.getTrendsTime(TimeUtil.getStringtoLong(model.getCreate_time())));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    private class ViewHolder {
        public TextView tvTitle;
        public ImageView ivArrow, ivStatus;
    }

    private class ViewChildHolder {
        public TextView tvContent, tvTime;
    }


    public void setNotifyMessage(ArrayList<ActNotifyModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setSearchNotifyMessage(String compare, ArrayList<ActNotifyModel> list) {
        this.list = list;
        this.compare = compare;
        notifyDataSetChanged();
    }
}
