package com.gather.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.activity.TrendsPicGallery;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActPlacePlanModel;
import com.gather.android.model.ActPlacePlanModelList;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.params.ActPlacePlanParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/3/30.
 */
public class ActPlacePlanAdapter extends SuperAdapter{
    private Activity context;
    private ResponseHandler responseHandler;
    private int page, limit = 50, totalNum, maxPage, isOver, actId;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public ActPlacePlanAdapter(Activity context) {
        super(context);
        this.context = context;
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
        this.initListener();
    }

    private void initListener() {
        this.responseHandler = new ResponseHandler() {
            @Override
            public void onResponseSuccess(int code, Header[] headers, String result) {
                if (page == 1) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(result);
                        totalNum = object.getInt("total_num");
                        if (totalNum % limit == 0) {
                            maxPage = totalNum / limit;
                        } else {
                            maxPage = (totalNum / limit) + 1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        refreshOver(-1, "数据解析出错");
                        isRequest = false;
                        return;
                    } finally {
                        object = null;
                    }
                }
                Gson gson = new Gson();
                ActPlacePlanModelList list = gson.fromJson(result, ActPlacePlanModelList.class);
                if (list != null && list.getPlace_imgs() != null) {
                    switch (loadType) {
                        case REFRESH:
                            if (totalNum == 0) {
                                refreshOver(code, ISNULL);
                            } else if (page == maxPage) {
                                isOver = 1;
                                refreshOver(code, ISOVER);
                            } else {
                                page++;
                                refreshOver(code, CLICK_MORE);
                            }
                            refreshItems(list.getPlace_imgs());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(code, CLICK_MORE);
                            } else {
                                isOver = 1;
                                loadMoreOver(code, ISOVER);
                            }
                            addItems(list.getPlace_imgs());
                            break;
                    }
                } else {
                    switch (loadType) {
                        case REFRESH:
                            refreshOver(code, ISNULL);
                            break;
                        case LOADMORE:
                            loadMoreOver(code, ISOVER);
                            break;
                    }
                }
                isRequest = false;
            }

            @Override
            public void onNeedLogin(String msg) {
                switch (loadType) {
                    case REFRESH:
                        refreshOver(5, msg);
                        break;
                    case LOADMORE:
                        loadMoreOver(5, msg);
                        break;
                }
                isRequest = false;
            }

            @Override
            public void onResponseFailed(int code, String msg) {
                switch (loadType) {
                    case REFRESH:
                        refreshOver(code, msg);
                        break;
                    case LOADMORE:
                        loadMoreOver(code, msg);
                        break;
                }
                isRequest = false;
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_act_place_plan, parent, false);
            holder = new ViewHolder();
            holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);
            holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivPic.getLayoutParams();
            params.width = metrics.widthPixels;
            params.height = params.width * 8 / 18;
            holder.ivPic.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ActPlacePlanModel model = (ActPlacePlanModel) getItem(position);
        holder.tvName.setText(model.getSubject());

        imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImg_url(), 300, 300, 50), holder.ivPic, options);
        holder.llItem.setOnClickListener(new OnItemAllClickListener(position));
        return convertView;
    }

    private static class ViewHolder {
        public ImageView ivPic;
        public TextView tvName;
        public LinearLayout llItem;
    }

    @Override
    public void refresh() {

    }

    private class OnItemAllClickListener implements View.OnClickListener {

        private int position;

        public OnItemAllClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (!ClickUtil.isFastClick()) {
                Intent intent = new Intent(context, TrendsPicGallery.class);
                ArrayList<UserPhotoModel> list = new ArrayList<UserPhotoModel>();
                for (int i = 0; i < getCount(); i++) {
                    UserPhotoModel model = new UserPhotoModel();
                    ActPlacePlanModel pic = (ActPlacePlanModel) getItem(i);
                    model.setImg_url(pic.getImg_url());
                    list.add(model);
                }
                intent.putExtra("USER_PHOTO_LIST", list);
                intent.putExtra("POSITION", position);
                context.startActivity(intent);
                context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }

    @Override
    public void loadMore() {
        if (isOver == 1) {
            loadMoreOver(0, ISOVER);
        } else {
            if (!isRequest) {
                this.isRequest = true;
                this.loadType = LOADMORE;
                ActPlacePlanParam param = new ActPlacePlanParam(actId);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
            }
        }
    }

    public void getPlaceList(int actId) {
        if (!isRequest) {
            this.isRequest = true;
            this.loadType = REFRESH;
            this.page = 1;
            this.isOver = 0;
            this.actId = actId;
            ActPlacePlanParam param = new ActPlacePlanParam(actId);
            AsyncHttpTask.post(param.getUrl(), param, responseHandler);
        }
    }


}
