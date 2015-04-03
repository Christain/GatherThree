package com.gather.android.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.model.ActEnrollCustomKeyModel;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/3/31.
 */
public class ActEnrollInfoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ActEnrollCustomKeyModel> list;

    public ActEnrollInfoAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<ActEnrollCustomKeyModel>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ActEnrollCustomKeyModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_act_enroll_info, null);
            holder.tvKey = (TextView) convertView.findViewById(R.id.tvKey);
            holder.etContent = (EditText) convertView.findViewById(R.id.etContent);
            holder.etContent.setTag(position);
            class MyTextWatcher implements TextWatcher {

                public MyTextWatcher(ViewHolder holder) {
                    mHolder = holder;
                }

                private ViewHolder mHolder;

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null && !"".equals(s.toString())) {
                        int position = (Integer) mHolder.etContent.getTag();
                        list.get(position).setContent(s.toString());
                    }
                }
            }
            holder.etContent.addTextChangedListener(new MyTextWatcher(holder));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ActEnrollCustomKeyModel model = getItem(position);
        holder.tvKey.setText(model.getSubject() + "：");
        return convertView;
    }

    private class ViewHolder {
        public TextView tvKey;
        public EditText etContent;
    }

    public void setCustomKey(ArrayList<ActEnrollCustomKeyModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<ActEnrollCustomKeyModel> getList() {
        return list;
    }
}
