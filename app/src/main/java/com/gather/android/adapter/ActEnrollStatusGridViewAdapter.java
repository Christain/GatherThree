package com.gather.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.model.UserInfoModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/3/17.
 */
public class ActEnrollStatusGridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<UserInfoModel> list;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public ActEnrollStatusGridViewAdapter(Context context, ArrayList<UserInfoModel> list) {
        this.context = context;
        this.list = list;
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        if (list.size() > 4) {
            return 4;
        } else {
            return list.size();
        }
    }

    @Override
    public UserInfoModel getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_act_detail_vip, null);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            holder.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
            holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivUserIcon.getLayoutParams();
            params.width = (int) (metrics.widthPixels - (context.getResources().getDimensionPixelOffset(R.dimen.act_detail_item_gridview_padding) * 2) - (context.getResources().getDimensionPixelOffset(R.dimen.act_detail_item_gridview_padding) * 3)) / 4;
            params.height = params.width;
            holder.ivUserIcon.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserInfoModel model = getItem(position);
        imageLoader.displayImage(model.getHead_img_url(), holder.ivUserIcon, options);
        holder.tvUserName.setText(model.getNick_name());

        // holder.llItem.setOnClickListener(new OnItemAllClickListener(model));
        return convertView;
    }

    private static class ViewHolder {
        public LinearLayout llItem;
        public ImageView ivUserIcon;
        public TextView tvUserName;
    }

}
