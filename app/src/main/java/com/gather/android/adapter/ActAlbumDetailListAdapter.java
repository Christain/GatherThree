package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gather.android.R;
import com.gather.android.activity.ActAlbumGallery;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActAlbumDetailModel;
import com.gather.android.model.ActAlbumDetailModelList;
import com.gather.android.params.ActAlbumDetailListParam;
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
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumDetailListAdapter extends SuperAdapter {

    private Context context;
    private ResponseHandler responseHandler;
    private DisplayMetrics metrics;
    private int albumId, page, limit = 20, totalNum, maxPage, isOver;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public ActAlbumDetailListAdapter(Context context) {
        super(context);
        this.context = context;
        this.metrics = context.getResources().getDisplayMetrics();
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
        this.initListener();
    }

    private void initListener() {
        responseHandler = new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
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
                ActAlbumDetailModelList list = gson.fromJson(result, ActAlbumDetailModelList.class);
                if (list != null && list.getPhotoes() != null) {
                    switch (loadType) {
                        case REFRESH:
                            if (totalNum == 0) {
                                refreshOver(returnCode, ISNULL);
                            } else if (page == maxPage) {
                                isOver = 1;
                                refreshOver(returnCode, ISOVER);
                            } else {
                                page++;
                                refreshOver(returnCode, CLICK_MORE);
                            }
                            refreshItems(list.getPhotoes());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(returnCode, CLICK_MORE);
                            } else {
                                isOver = 1;
                                loadMoreOver(returnCode, ISOVER);
                            }
                            addItems(list.getPhotoes());
                            break;
                    }
                } else {
                    switch (loadType) {
                        case REFRESH:
                            refreshOver(returnCode, ISNULL);
                            break;
                        case LOADMORE:
                            loadMoreOver(returnCode, ISOVER);
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
            public void onResponseFailed(int returnCode, String errorMsg) {
                switch (loadType) {
                    case REFRESH:
                        refreshOver(returnCode, errorMsg);
                        break;
                    case LOADMORE:
                        loadMoreOver(returnCode, errorMsg);
                        break;
                }
                isRequest = false;
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_act_album_detail_list, viewGroup, false);
            holder = new ViewHolder();
            holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivImage.getLayoutParams();
            params.width = (int) (metrics.widthPixels - (context.getResources().getDimensionPixelOffset(R.dimen.act_album_detail_gridview_padding) * 5))/4;
            params.height = params.width;
            holder.ivImage.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ActAlbumDetailModel model = (ActAlbumDetailModel) getItem(position);
        imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImg_url(), 150, 150, 50), holder.ivImage, options);
        holder.ivImage.setOnClickListener(new OnImageClickListener(model, position));

        return convertView;
    }

    private static class ViewHolder {
        public ImageView ivImage;
    }

    private class OnImageClickListener implements View.OnClickListener {

        private ActAlbumDetailModel model;
        private int position;

        public OnImageClickListener(ActAlbumDetailModel model, int position) {
            this.position = position;
            this.model = model;
        }

        @Override
        public void onClick(View view) {
            if (!ClickUtil.isFastClick()) {
                Intent intent = new Intent(context, ActAlbumGallery.class);
                ArrayList<String> resource = new ArrayList<String>();
                for (int i = 0; i < getCount(); i++) {
                    resource.add(((ActAlbumDetailModel) getList().get(i)).getImg_url());
                }
                intent.putExtra("LIST", resource);
                intent.putExtra("POSITION", position);
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public void loadMore() {
        if (isOver == 1) {
            loadMoreOver(0, ISOVER);
        } else {
            if (!isRequest) {
                this.isRequest = true;
                this.loadType = LOADMORE;
                ActAlbumDetailListParam param = new ActAlbumDetailListParam(albumId, page, limit);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
            }
        }
    }

    public void getALbumList(int albumId) {
        if (!isRequest) {
            this.isRequest = true;
            this.loadType = REFRESH;
            this.page = 1;
            this.isOver = 0;
            this.albumId = albumId;
            ActAlbumDetailListParam param = new ActAlbumDetailListParam(albumId, page, limit);
            AsyncHttpTask.post(param.getUrl(), param, responseHandler);
        }
    }

}
