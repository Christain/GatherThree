package com.gather.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.activity.ActAlbumDetailList;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActAlbumContentModel;
import com.gather.android.model.ActAlbumModel;
import com.gather.android.params.ActAlbumListParam;
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

/**
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumListAdapter extends SuperAdapter{

    private int page, limit = 20, actId, cityId, isOver = 0, totalNum, maxPage;
    private ResponseHandler responseHandler;
    private Context mContext;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private LoadingDialog mLoadingDialog;
    private boolean isRequest = false;
    private int myAlbumId = -1;

    public ActAlbumListAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.mLoadingDialog = LoadingDialog.createDialog(mContext, false);
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
        this.initListener();
    }

    private void initListener() {
        responseHandler = new ResponseHandler() {
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
                ActAlbumModel list = gson.fromJson(result, ActAlbumModel.class);
                if (list != null && list.getAlbums() != null) {
                    for (int i = 0; i < list.getAlbums().size(); i++) {
                        if (list.getAlbums().get(i).getSum() <= 0) {
                            list.getAlbums().remove(i);
                            i--;
                        }
                    }
                    switch (loadType) {
                        case REFRESH:
                            if (page == 1) {
                                myAlbumId = list.getMy_album_id();
//                                if (null != list.getBusi_video()) {
//                                    list.getBusi_video().setOwner(true);
//                                    list.getBusi_video().setType(2);
//                                    list.getAlbums().add(0, list.getBusi_video());
//                                }
                                if (null != list.getBusi_photo()) {
                                    list.getBusi_photo().setOwner(true);
                                    list.getBusi_photo().setType(1);
                                    list.getAlbums().add(0, list.getBusi_photo());
                                }
                            }
                            if (totalNum == 0) {
                                refreshOver(code, ISNULL);
                            } else if (page == maxPage) {
                                isOver = 1;
                                refreshOver(code, ISOVER);
                            } else {
                                page++;
                                refreshOver(code, CLICK_MORE);
                            }
                            refreshItems(list.getAlbums());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(code, CLICK_MORE);
                            } else {
                            isOver = 1;
                            loadMoreOver(code, ISOVER);
                        }
                            addItems(list.getAlbums());
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_act_album_list, null);
            holder = new ViewHolder();
            holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvNum = (TextView) convertView.findViewById(R.id.tvNum);
            holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItemAll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ActAlbumContentModel model = (ActAlbumContentModel) getItem(position);
        imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getCover_url(), 150, 150, 50), holder.ivImage, options);
        if (model.isOwner()) {
            if (model.getType() == 1){
                holder.tvName.setText("主办方的相册");
            } else {
                holder.tvName.setText("主办方的视频");
            }
        } else {
            holder.tvName.setText(model.getUser().getNick_name() + "的相册");
        }
        if (model.getSum() > 0) {
            holder.tvNum.setVisibility(View.VISIBLE);
            holder.tvNum.setText(model.getSum() + "");
        } else {
            holder.tvNum.setVisibility(View.INVISIBLE);
        }
        holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model));

        return convertView;
    }

    private static class ViewHolder {
        public ImageView ivImage;
        public TextView tvName, tvNum;
        public LinearLayout llItemAll;
    }

    private class OnItemAllClickListener implements View.OnClickListener {

        private ActAlbumContentModel model;

        public OnItemAllClickListener(ActAlbumContentModel model) {
            this.model = model;
        }

        @Override
        public void onClick(View arg0) {
            if (!ClickUtil.isFastClick()) {
                Intent intent = new Intent(context, ActAlbumDetailList.class);
                intent.putExtra("MODEL", model);
                intent.putExtra("ACT_ID", actId);
                if (myAlbumId() == model.getId()) {
                    intent.putExtra("IS_ME", "");
                }
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
                ActAlbumListParam param = new ActAlbumListParam(actId, cityId, page, limit);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
            }
        }
    }

    public void getActAlbumList(int actId, int cityId) {
        if (!isRequest) {
            this.isRequest = true;
            this.loadType = REFRESH;
            this.page = 1;
            isOver = 0;
            this.actId = actId;
            this.cityId = cityId;
            ActAlbumListParam param = new ActAlbumListParam(actId, cityId, page, limit);
            AsyncHttpTask.post(param.getUrl(), param, responseHandler);
        }
    }

    public int myAlbumId() {
        return myAlbumId;
    }

}
