package com.gather.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActModel;
import com.gather.android.model.ActModelList;
import com.gather.android.params.ActListParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class ActSearchAdapter extends SuperAdapter {

	private Activity context;
	private ResponseHandler responseHandler;
	private int cityId, page, limit = 10, totalNum, maxPage, isOver;
	private String keyWords;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private OnActDetailClickListener actdetailListener;

	public ActSearchAdapter(Activity context) {
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
                ActModelList list = gson.fromJson(result, ActModelList.class);
                if (list != null && list.getActs() != null) {
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
                            refreshItems(list.getActs());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(code, CLICK_MORE);
                            } else {
                                isOver = 1;
                                loadMoreOver(code, ISOVER);
                            }
                            addItems(list.getActs());
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
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_act_collection, parent, false);
			holder = new ViewHolder();
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItem);
			holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

			holder.tvTime.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ActModel model = (ActModel) getItem(position);
		holder.tvTitle.setText(model.getTitle());
		holder.tvContent.setText(model.getIntro());
		holder.tvTime.setText(TimeUtil.getActListTime(model.getB_time()));

		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getHead_img_url(), 200, 200, 50), holder.ivPic, options);
		holder.llItemAll.setOnClickListener(new OnDetailClickListener(model, position));
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivPic;
		public TextView tvTitle, tvContent, tvTime;
		public LinearLayout llItemAll;
	}

	/**
	 * 查看详情
	 */
	private class OnDetailClickListener implements OnClickListener {

		private ActModel model;
		private int position;

		public OnDetailClickListener(ActModel model, int position) {
			this.model = model;
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick() && null != actdetailListener) {
				actdetailListener.OnDetailListener(model, position);
			}
		}
	}

	public interface OnActDetailClickListener {
		public void OnDetailListener(ActModel model, int position);
	}

	public void setOnActDetailClickListener(OnActDetailClickListener listener) {
		this.actdetailListener = listener;
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
				ActListParam param = new ActListParam(cityId, page, limit);
				param.setKeyWords(keyWords);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
			}
		}
	}

	public void getSearchActList(int cityId, String keyWords) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.cityId = cityId;
			this.keyWords = keyWords;
			ActListParam param = new ActListParam(cityId, page, limit);
			param.setKeyWords(keyWords);
            AsyncHttpTask.post(param.getUrl(), param, responseHandler);
		}
	}

}
