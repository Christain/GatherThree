package com.gather.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.activity.PublishTrendsPicGallery;
import com.gather.android.activity.TrendsPicGallery;
import com.gather.android.activity.UserCenter;
import com.gather.android.activity.UserTrends;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.database.PublishTrendsInfo;
import com.gather.android.database.PublishTrendsService;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.TrendsListModel;
import com.gather.android.model.TrendsModel;
import com.gather.android.model.TrendsPicModel;
import com.gather.android.model.TrendsPicObjectModel;
import com.gather.android.params.DelTrendsParam;
import com.gather.android.params.TrendsListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.DataHelper;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.NoScrollGridView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

@SuppressLint("InflateParams")
public class TrendsAdapter extends SuperAdapter {

	private int page, limit = 20, cityId, isOver = 0, totalNum, maxPage, myUserId;
	private ResponseHandler responseHandler;
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options, picOptions;
	private DisplayMetrics metrics;
	private TrendsPicAdapter picAdapter;
	private OnItemAllListener itemListener;
	private PublishTrendsService service;
	private DataHelper helper;
	private static String TAB_TRENDS = "TAB_TRENDS";
	private GatherApplication application;

	public TrendsAdapter(Context context) {
		super(context);
		this.mContext = context;
		this.metrics = context.getResources().getDisplayMetrics();
		this.application = (GatherApplication) context.getApplicationContext();
		this.myUserId = AppPreference.getUserPersistentInt(mContext, AppPreference.USER_ID);
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.picOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.initListener();
		this.service = new PublishTrendsService(mContext);
		helper = new DataHelper(mContext, TAB_TRENDS + myUserId);
	}

	private void initListener() {
        this.responseHandler = new ResponseHandler() {
            @Override
            public void onResponseSuccess(int code, Header[] headers, String result) {
                if (page == 1) {
                    if (AppPreference.hasLogin(mContext)) {
                        helper.saveData(result);
                    }
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
                TrendsListModel list = gson.fromJson(result, TrendsListModel.class);
                if (list != null && list.getDynamics() != null) {
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
                            getList().clear();
                            addItemsNoChanged(getDataBaseTrends());
                            addItems(list.getDynamics());
                            // refreshItems(list.getDynamics());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(code, CLICK_MORE);
                            } else {
                                isOver = 1;
                                loadMoreOver(code, ISOVER);
                            }
                            addItems(list.getDynamics());
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			switch (getItemViewType(position)) {
			case 0:
				convertView = mInflater.inflate(R.layout.item_trends_no_pic, null);
				break;
			case 1:
				convertView = mInflater.inflate(R.layout.item_trends_one_pic, null);

				holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivPic.getLayoutParams();
				params.width = metrics.widthPixels / 3;
				params.height = params.width;
				holder.ivPic.setLayoutParams(params);
				break;
			default:
				convertView = mInflater.inflate(R.layout.item_trends_more_pic, null);
				holder.gridView = (NoScrollGridView) convertView.findViewById(R.id.gridView);
				break;
			}
			holder.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.tvDel = (TextView) convertView.findViewById(R.id.tvDel);
			holder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TrendsModel model = (TrendsModel) getItem(position);
		holder.tvUserName.setText(model.getUser().getNick_name());
		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getUser().getHead_img_url(), 100, 100, 50), holder.ivUserIcon, options);
		holder.tvContent.setText(model.getContent());
		if (model.getComment_num() != 0) {
			holder.tvComment.setText("评论" + model.getComment_num());
		} else {
			holder.tvComment.setText("评论");
		}
		if (myUserId == model.getAuthor_id()) {
			holder.tvDel.setVisibility(View.VISIBLE);
			holder.tvDel.setOnClickListener(new OnDelClickListener(model, position));
		} else {
			holder.tvDel.setVisibility(View.GONE);
		}
		switch (model.getType()) {
		case 1:
			if (model.isLocal()) {
				imageLoader.displayImage(Uri.decode(Uri.fromFile(new File(model.getImgs().getImgs().get(0).getImg_url())).toString()), holder.ivPic, picOptions);
			} else {
				imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImgs().getImgs().get(0).getImg_url(), 150, 150, 50), holder.ivPic, picOptions);
			}
			holder.ivPic.setOnClickListener(new OnOnePicClickListener(model));
			break;
		case 2:
			if (model.isLocal()) {
				picAdapter = new TrendsPicAdapter(mContext, model.getImgs().getImgs(), 1, model.isLocal());
			} else {
				picAdapter = new TrendsPicAdapter(mContext, model.getImgs().getImgs(), 1);
			}
			holder.gridView.setAdapter(picAdapter);
			break;
		}
		if (!model.isLocal()) {
			holder.tvTime.setText(TimeUtil.getTrendsTime(TimeUtil.getStringtoLong(model.getCreate_time())));
			holder.ivUserIcon.setOnClickListener(new OnUserIconClickListener(model));
			holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model, position));
			holder.llItemAll.setBackgroundResource(R.drawable.shape_white_bg_click_gray_style);
		} else {
			holder.ivUserIcon.setOnClickListener(null);
			holder.llItemAll.setOnClickListener(null);
			holder.llItemAll.setBackgroundColor(0xFFFFFFFF);
			holder.tvTime.setText(TimeUtil.getTrendsTime(TimeUtil.getStringtoLong(model.getCreate_time())) + "     发布中...");
		}
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivUserIcon, ivPic;
		public TextView tvUserName, tvContent, tvTime, tvDel, tvComment;
		public NoScrollGridView gridView;
		public LinearLayout llItemAll;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		TrendsModel model = (TrendsModel) getItem(position);// 0没有图片，1有一张图片，2有多张图片
		return model.getType();
	}

	/**
	 * 删除监听
	 */
	private class OnDelClickListener implements OnClickListener {

		private int position;
		private TrendsModel model;

		public OnDelClickListener(TrendsModel model, int position) {
			this.model = model;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (!ClickUtil.isFastClick()) {
				if (!isRequest) {
					isRequest = true;
					if (model.isLocal()) {
						if (service != null) {
							service.delete(model.getId());
							removeItem(position);
						}
						isRequest = false;
					} else {
						DelTrends(model.getId(), position);
					}
				} else {
					toast("正在删除");
				}
			}
		}
	}

	/**
	 * 删除动态
	 */
	private void DelTrends(int dynamicId, final int position) {
		DelTrendsParam param = new DelTrendsParam(dynamicId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                isRequest = false;
                removeItem(position);
            }

            @Override
            public void onNeedLogin(String msg) {
                isRequest = false;
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                isRequest = false;
                toast("删除失败，请重试");
            }
        });
	}

	/**
	 * 点击Item
	 */
	private class OnItemAllClickListener implements OnClickListener {

		public TrendsModel model;
		private int position;

		public OnItemAllClickListener(TrendsModel model, int position) {
			this.model = model;
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				if (itemListener != null) {
					itemListener.OnItemListener(model, position);
				}
			}
		}
	}

	public interface OnItemAllListener {
		public void OnItemListener(TrendsModel model, int position);
	}

	public void setOnItemAllListener(OnItemAllListener listener) {
		this.itemListener = listener;
	}

	/**
	 * 点击头像
	 */
	private class OnUserIconClickListener implements OnClickListener {

		private TrendsModel model;

		public OnUserIconClickListener(TrendsModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				if (myUserId != model.getAuthor_id()) {
					Intent intent = new Intent(mContext, UserCenter.class);
					intent.putExtra("UID", model.getAuthor_id());
					intent.putExtra("MODEL", model.getUser());
					mContext.startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, UserTrends.class);
					intent.putExtra("MODEL", model.getUser());
					mContext.startActivity(intent);
				}
			}
		}
	}

	/**
	 * 单张图片点击
	 */
	private class OnOnePicClickListener implements OnClickListener {

		private TrendsModel model;

		public OnOnePicClickListener(TrendsModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View v) {
			if (model.isLocal()) {
				ArrayList<String> imgList = new ArrayList<String>();
				for (int i = 0; i < model.getImgs().getImgs().size(); i++) {
					imgList.add(model.getImgs().getImgs().get(i).getImg_url());
				}
				Intent intent = new Intent(mContext, PublishTrendsPicGallery.class);
				intent.putExtra("LIST", imgList);
				intent.putExtra("POSITION", 0);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Intent intent = new Intent(mContext, TrendsPicGallery.class);
				intent.putExtra("LIST", model.getImgs().getImgs());
				intent.putExtra("POSITION", 0);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}
	}

	/**
	 * 获取本地数据库未发布动态数据
	 */
	public ArrayList<TrendsModel> getDataBaseTrends() {
		ArrayList<TrendsModel> trendList = new ArrayList<TrendsModel>();
		ArrayList<PublishTrendsInfo> list = service.getTrendsList(myUserId);
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				TrendsModel model = new TrendsModel();
				model.setLocal(true);
				model.setId(list.get(i).id);
				model.setCreate_time(TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", list.get(i).time));
				model.setComment_num(0);
				model.setAuthor_id(myUserId);
				model.setUser(AppPreference.getUserInfo(mContext));
				model.setContent(list.get(i).content);
				TrendsPicObjectModel picModel = new TrendsPicObjectModel();
				ArrayList<TrendsPicModel> picList = new ArrayList<TrendsPicModel>();
				if (list.get(i).imgPaths.length() > 1) {
					String[] pic = list.get(i).imgPaths.split(",");
					picModel.setTotal_num(pic.length);
					for (int j = 0; j < pic.length; j++) {
						TrendsPicModel picurl = new TrendsPicModel();
						picurl.setImg_url(pic[j]);
						picList.add(picurl);
					}
				} else {
					picModel.setTotal_num(0);
				}
				picModel.setImgs(picList);
				model.setImgs(picModel);
				trendList.add(model);
			}
		}
		return trendList;
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
				TrendsListParam param = new TrendsListParam(myUserId, cityId, page, limit);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
			}
		}
	}

	public void getTrendsList(int cityId) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.cityId = cityId;
			isOver = 0;
            TrendsListParam param = new TrendsListParam(myUserId, cityId, page, limit);
            AsyncHttpTask.post(param.getUrl(), param, responseHandler);
		}
	}

	public void getCacheTrendsList(int cityId) {
		String cache = helper.getData();
		if (null != cache) {
			setCacheMessage(cache);
			cache = null;
		}
	}

	private void setCacheMessage(String msg) {
		Gson gson = new Gson();
		TrendsListModel list = gson.fromJson(msg, TrendsListModel.class);
		if (list != null && list.getDynamics() != null) {
			isOver = 1;
			refreshOver(0, CACHE);
			getList().clear();
			addItemsNoChanged(getDataBaseTrends());
			addItems(list.getDynamics());
		}
	}

}
