package com.gather.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumDetailListAdapter extends SuperAdapter {

    private Context context;
    private ResponseListener listener;
    private Response.ErrorListener errorListener;
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
        listener = new ResponseListener() {
            @Override
            public void success(int code, String msg, String result) {
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
                                refreshOver(code, ISNULL);
                            } else if (page == maxPage) {
                                isOver = 1;
                                refreshOver(code, ISOVER);
                            } else {
                                page++;
                                refreshOver(code, CLICK_MORE);
                            }
                            refreshItems(list.getPhotoes());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(code, CLICK_MORE);
                            } else {
                                isOver = 1;
                                loadMoreOver(code, ISOVER);
                            }
                            addItems(list.getPhotoes());
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
            public void relogin(String msg) {
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
            public void error(int code, String msg) {
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
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                switch (loadType) {
                    case REFRESH:
                        refreshOver(-1, error.getMsg());
                        break;
                    case LOADMORE:
                        loadMoreOver(-1, error.getMsg());
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
                ActAlbumDetailListParam param = new ActAlbumDetailListParam(context, albumId, page, limit);
                HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
                RequestManager.addRequest(task, context);
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
            ActAlbumDetailListParam param = new ActAlbumDetailListParam(context, albumId, page, limit);
            HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
            RequestManager.addRequest(task, context);
        }
    }

}
