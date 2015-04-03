package com.gather.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gather.android.R;
import com.gather.android.model.ActAlbumDetailModel;
import com.gather.android.utils.ThumbnailUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christain on 2015/4/2.
 */
public class ActAlbumAddPhotoAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private DisplayMetrics metrics;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private ArrayList<ActAlbumDetailModel> list;

    public ActAlbumAddPhotoAdapter(Context context) {
        this.context = context;
        this.metrics = context.getResources().getDisplayMetrics();
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
        this.list = new ArrayList<ActAlbumDetailModel>();
    }

    public boolean isFull() {
        return list.size() == 100;
    }

    public void setUserPhotoList(ArrayList<ActAlbumDetailModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<ActAlbumDetailModel> getPicList() {
        return list;
    }

    public void add(ActAlbumDetailModel model) {
        this.list.add(model);
        notifyDataSetChanged();
    }

    public void add(List<ActAlbumDetailModel> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void del(int position) {
        if (position >= 0 && position < list.size()) {
            list.get(position).destroy();
            list.remove(position);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (isFull()) {
            return list.size();
        } else {
            return list.size() + 1;
        }
    }

    @Override
    public ActAlbumDetailModel getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_user_photo_set, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.rlAdd = (RelativeLayout) convertView.findViewById(R.id.rlAdd);

            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) holder.imageView.getLayoutParams();
            params.width = (int) ((metrics.widthPixels - (metrics.density * 40 + 0.5f)) / 4);
            params.height = params.width;
            holder.imageView.setLayoutParams(params);
            holder.rlAdd.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!isFull() && position == getCount() - 1) {
            holder.rlAdd.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.rlAdd.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            ActAlbumDetailModel model = getItem(position);
            if (model.getId() == 0) {
                imageLoader.displayImage(Uri.decode(Uri.fromFile(new File(model.getPath())).toString()), holder.imageView, options);
            } else {
                imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImg_url(), 250, 250, 50), holder.imageView, options);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        public ImageView imageView;
        public RelativeLayout rlAdd;
    }
}
