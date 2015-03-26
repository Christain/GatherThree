package com.gather.android.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.WebStrategy;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.NewsModel;
import com.gather.android.model.NewsModelList;
import com.gather.android.params.CancelCollectNewsParam;
import com.gather.android.params.CollectionNewsListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.DataHelper;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class CollectionNewsListAdapter extends SuperAdapter {

	private Activity context;
	private ResponseListener listener;
	private ErrorListener errorListener;
	private int myUserId, page, limit = 20, totalNum, maxPage, isOver, userId, type;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private LoadingDialog mLoadingDialog;
	private DataHelper helper;
	private static String COLLECTION = "COLLECTION";

	public CollectionNewsListAdapter(Activity context) {
		super(context);
		this.context = context;
		this.mLoadingDialog = LoadingDialog.createDialog(context, true);
		this.myUserId = AppPreference.getUserPersistentInt(context, AppPreference.USER_ID);
		this.helper = new DataHelper(context, COLLECTION + myUserId);
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.initListener();
	}

	private void initListener() {
		listener = new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (page == 1) {
					if (helper != null) {
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
				NewsModelList list = gson.fromJson(result, NewsModelList.class);
				if (list != null && list.getNews() != null) {
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
						refreshItems(list.getNews());
						break;
					case LOADMORE:
						if (page != maxPage) {
							page++;
							loadMoreOver(code, CLICK_MORE);
						} else {
							isOver = 1;
							loadMoreOver(code, ISOVER);
						}
						addItems(list.getNews());
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
		errorListener = new ErrorListener() {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_act_collection, parent, false);
			holder = new ViewHolder();
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItem);
			holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NewsModel model = (NewsModel) getItem(position);
		holder.tvTitle.setText(model.getTitle());
		holder.tvContent.setText(model.getIntro());
		holder.tvTime.setText(TimeUtil.getUserMessageTime(TimeUtil.getStringtoLong(model.getLov_time())));

		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getH_img_url(), 150, 150, 50), holder.ivPic, options);
		holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model));
		if (myUserId == userId) {
			holder.llItemAll.setOnLongClickListener(new OnItemAllLongClickListener(model, position));
		}
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivPic;
		public TextView tvTitle, tvContent, tvTime;
		public LinearLayout llItemAll;
	}

	@Override
	public void refresh() {

	}

	private class OnItemAllClickListener implements OnClickListener {

		private NewsModel model;

		public OnItemAllClickListener(NewsModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View v) {
			if (!ClickUtil.isFastClick()) {
				Intent intent = null;
				switch (model.getType_id()) {
				case 1:
					intent = new Intent(context, WebStrategy.class);
					intent.putExtra("MODEL", model);
					context.startActivity(intent);
					break;
				case 2:
					intent = new Intent(context, WebStrategy.class);
					intent.putExtra("MODEL", model);
					context.startActivity(intent);
					break;
				case 3:
					intent = new Intent(context, WebStrategy.class);
					intent.putExtra("MODEL", model);
					context.startActivity(intent);
					break;
				}
			}
		}
	}

	private class OnItemAllLongClickListener implements OnLongClickListener {

		private NewsModel model;
		private int position;

		public OnItemAllLongClickListener(NewsModel model, int position) {
			this.model = model;
			this.position = position;
		}

		@Override
		public boolean onLongClick(View arg0) {
			final DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(context);
			dialog.setMessage("您确定要取消收藏？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					CancelCollectNews(model.getId(), position);
				}
			}).show();
			return true;
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
				CollectionNewsListParam param = new CollectionNewsListParam(context, userId, type, page, limit);
				HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
				RequestManager.addRequest(task, context);
			}
		}
	}

	public void getCollectionNewsList(int userId, int type) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.userId = userId;
			this.type = type;
			CollectionNewsListParam param = new CollectionNewsListParam(context, userId, type, page, limit);
			HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}
	
	public void getCacheList() {
		page = 1;
		isOver = 0;
		loadType = REFRESH;
		String cache = helper.getData();
		if (null != cache) {
			setCacheMessage(cache);
			cache = null;
		} 
	}

	private void setCacheMessage(String msg) {
		try {
			JSONObject object = new JSONObject(msg);
			totalNum = object.getInt("total_num");
			if (totalNum % limit == 0) {
				maxPage = totalNum / limit;
			} else {
				maxPage = (totalNum / limit) + 1;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Gson gson = new Gson();
		NewsModelList list = gson.fromJson(msg, NewsModelList.class);
		if (list != null && list.getNews() != null) {
			switch (loadType) {
			case REFRESH:
				if (totalNum == 0) {
					refreshOver(0, ISNULL);
				} else if (page == maxPage) {
					isOver = 1;
					refreshOver(0, ISOVER);
				} else {
					page++;
					refreshOver(0, CLICK_MORE);
				}
				refreshItems(list.getNews());
				break;
			}
		}
	}

	/**
	 * 取消收藏
	 */
	private void CancelCollectNews(int id, final int position) {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("取消收藏中...");
			mLoadingDialog.show();
		}
		CancelCollectNewsParam param = new CancelCollectNewsParam(context, id);
		HttpStringPost task = new HttpStringPost(context, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				removeItem(position);
				toast("取消成功");
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("取消收藏失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("取消收藏失败，请重试");
			}
		}, param.getParameters());
		RequestManager.addRequest(task, context);
	}

}
