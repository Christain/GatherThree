package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.gather.android.activity.ActLocationAndCarPlaceMap;
import com.gather.android.model.ActAddressModel;
import com.gather.android.utils.ClickUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/3/30.
 */
public class ActLocationAndCarPlaceAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ActAddressModel> list;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public ActLocationAndCarPlaceAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<ActAddressModel>();
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ActAddressModel getItem(int position) {
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
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_act_location_and_car_place, null);
            holder.tvActLocation = (TextView) convertView.findViewById(R.id.tvActLcation);
            holder.tvLocationCar = (TextView) convertView.findViewById(R.id.tvLcationCar);
            holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
            holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItemAll);

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivPic.getLayoutParams();
            params.width = metrics.widthPixels - context.getResources().getDimensionPixelOffset(R.dimen.act_location_and_car_place_item_padding)* 2;
            params.height = params.width * 8/18;
            holder.ivPic.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ActAddressModel model = getItem(position);
        holder.tvLocationCar.setText("活动地点" + (position + 1) + "：" + model.getAddr_name());
        holder.tvActLocation.setText(model.getAddr_city() + model.getAddr_area() + model.getAddr_road() + model.getAddr_name());
        imageLoader.displayImage(staticMapUrl(model), holder.ivPic, options);

        holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model));
        return convertView;
    }

    private class ViewHolder {
        public LinearLayout llItemAll;
        public ImageView ivPic;
        public TextView tvActLocation, tvLocationCar;
    }

    private class OnItemAllClickListener implements View.OnClickListener {

        private ActAddressModel model;

        public OnItemAllClickListener(ActAddressModel model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            if (!ClickUtil.isFastClick()) {
                Intent intent = new Intent(context, ActLocationAndCarPlaceMap.class);
                intent.putExtra("MODEL", model);
                context.startActivity(intent);
            }
        }
    }

    private String staticMapUrl(ActAddressModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://api.map.baidu.com/staticimage?");
        sb.append("copyright=1");
        sb.append("&dpiType=ph");
        sb.append("&zoom=17");
        sb.append("&scale=2");
        sb.append("&width=");
        sb.append("800");
        sb.append("&height=");
        sb.append("400");
        sb.append("&center=");
        sb.append(model.getLon()+ "," + model.getLat());
        sb.append("&markers=");
        sb.append(model.getLon()+ "," + model.getLat());
        for (int i = 0; i < model.getParking_spaces().size(); i++) {
            sb.append("|");
            sb.append(model.getParking_spaces().get(i).getLon() + "," + model.getParking_spaces().get(i).getLat());
        }
        sb.append("&markerStyles=-1,http://jhla-app-icons.oss-cn-qingdao.aliyuncs.com/ic_location.png");
        for (int i = 0; i < model.getParking_spaces().size(); i++) {
            sb.append("|");
            sb.append("-1,http://jhla-app-icons.oss-cn-qingdao.aliyuncs.com/ic_location_parking.png");
        }
        return sb.toString();
    }

    public void setPlaceList(ArrayList<ActAddressModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

}
