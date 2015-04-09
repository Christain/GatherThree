package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.activity.UserCenter;
import com.gather.android.model.ActEnrollStatusCommentModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/3/17.
 */
public class ActEnrollStatusCommentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ActEnrollStatusCommentModel> list;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public ActEnrollStatusCommentAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<ActEnrollStatusCommentModel>();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ActEnrollStatusCommentModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_act_enroll_status_comment, parent, false);
            holder = new ViewHolder();
            holder.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ActEnrollStatusCommentModel model = (ActEnrollStatusCommentModel) getItem(position);
        if (model.getIs_admin() == 1) {
            holder.ivUserIcon.setImageResource(R.drawable.icon_act_admin);
            holder.tvContent.setText("管理员：" + model.getContent());
            holder.tvContent.setTextColor(0xFFFF9933);
        } else {
            imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getUser().getHead_img_url(), 150, 150, 50), holder.ivUserIcon, options);
            holder.tvContent.setTextColor(0xFF6F7376);
            holder.tvContent.setText(NameProgress(model.getUser().getNick_name() + "：" + model.getContent()));
        }
        holder.tvTime.setText(TimeUtil.getUserMessageTime(TimeUtil.getStringtoLong(model.getCreate_time())));
        holder.ivUserIcon.setOnClickListener(new OnUserIconClickListener(model.getUser(), model.getAuthor_id()));
        return convertView;
    }

    private static class ViewHolder {
        public ImageView ivUserIcon;
        public TextView tvContent, tvTime;
    }

    private class OnUserIconClickListener implements View.OnClickListener {

        private UserInfoModel model;
        private int userId;

        public OnUserIconClickListener(UserInfoModel model, int userId) {
            this.model = model;
            this.userId = userId;
        }

        @Override
        public void onClick(View v) {
            if (!ClickUtil.isFastClick()) {
                Intent intent = new Intent(context, UserCenter.class);
                intent.putExtra("MODEL", model);
                intent.putExtra("UID", userId);
                context.startActivity(intent);
            }
        }

    }

    /**
     * 关键字颜色区别
     */
    private Spannable NameProgress(String name) {
        int start = 0;
        int end = name.indexOf("：", 1);
        Spannable word = new SpannableString(name);
        word.setSpan(new ForegroundColorSpan(0xff000000), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return word;
    }

    public void refreshItems(ArrayList<ActEnrollStatusCommentModel> items) {
        list.clear();
        if (items != null) {
            list.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<ActEnrollStatusCommentModel> items) {
        if (items != null) {
            list.addAll(getCount(), items);
        }
        notifyDataSetChanged();
    }

    public void addItem(ActEnrollStatusCommentModel item, int position) {
        if (item != null) {
            list.add(position, item);
        }
        notifyDataSetChanged();
    }

    public ArrayList<ActEnrollStatusCommentModel> getList() {
        return list;
    }

    public void addItem(ActEnrollStatusCommentModel item) {
        if (item != null) {
            list.add(item);
        }
        notifyDataSetChanged();
    }

}
