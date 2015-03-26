package com.gather.android.adapter;

import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.ActStrategyAndMemoryAndTicket;
import com.gather.android.activity.WebStrategy;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.NewsModel;
import com.gather.android.model.NewsModelList;
import com.gather.android.params.NewsListParam;
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

@SuppressLint("InflateParams")
public class ActStrategyListAdapter extends SuperAdapter {

	private Context context;
	private int page, maxPage, limit = 10, isOver, totalNum, cityId, tagId, type;
	private ResponseListener listener;
	private Response.ErrorListener errorListener;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayMetrics metrics;
	private int myUserId;
	private DataHelper strategyHelper, memoryHelper, ticketHelper;
	private static String STRATEGY = "STRATEGY";
	private static String MEMORY = "MEMORY";
	private static String TICKET = "TICKET";

	public ActStrategyListAdapter(Context context) {
		super(context);
		this.context = context;
		this.metrics = context.getResources().getDisplayMetrics();
		this.myUserId = AppPreference.getUserPersistentInt(context, AppPreference.USER_ID);
		this.strategyHelper = new DataHelper(context, STRATEGY + myUserId);
		this.memoryHelper = new DataHelper(context, MEMORY + myUserId);
		this.ticketHelper = new DataHelper(context, TICKET + myUserId);
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.initListener();
	}

	private void initListener() {
		listener = new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (page == 1) {
					if (tagId == 1) {
						switch (type) {
						case ActStrategyAndMemoryAndTicket.STRATEGY:
							if (strategyHelper != null) {
								strategyHelper.saveData(result);
							}
							break;
						case ActStrategyAndMemoryAndTicket.MEMORY:
							if (memoryHelper != null) {
								memoryHelper.saveData(result);
							}
							break;
						case ActStrategyAndMemoryAndTicket.TICKET:
							if (ticketHelper != null) {
								ticketHelper.saveData(result);
							}
							break;
						}
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
						refreshOver(0, "错误");
						isRequest = false;
						return;
					} finally {
						object = null;
					}
				}
				Gson gson = new Gson();
				NewsModelList list = gson.fromJson(result, NewsModelList.class);
				if (list != null && list.getNews() != null) {
					Collections.reverse(list.getNews());
					switch (loadType) {
					case REFRESH:
						if (totalNum == 0) {
							refreshOver(list.getNews().size(), ISNULL);
						} else if (page == maxPage) {
							isOver = 1;
							refreshOver(list.getNews().size(), ISOVER);
						} else {
							page++;
							refreshOver(list.getNews().size(), CLICK_MORE);
						}
						refreshItems(list.getNews());
						break;
					case LOADMORE:
						addItemsInFront(list.getNews());
						if (page != maxPage) {
							page++;
							refreshOver(list.getNews().size(), CLICK_MORE);
						} else {
							isOver = 1;
							refreshOver(list.getNews().size(), ISOVER);
						}
						break;
					}
				} else {
					switch (loadType) {
					case REFRESH:
						refreshOver(list.getNews().size(), ISNULL);
						break;
					case LOADMORE:
						loadMoreOver(list.getNews().size(), ISOVER);
						break;
					}
				}
				isRequest = false;
			}

			@Override
			public void relogin(String msg) {
				switch (loadType) {
				case REFRESH:
					refreshOver(5, "登录");
					break;
				case LOADMORE:
					refreshOver(5, "登录");
					break;
				}
				isRequest = false;
			}

			@Override
			public void error(int code, String msg) {
				switch (loadType) {
				case REFRESH:
					refreshOver(code, "错误");
					break;
				case LOADMORE:
					refreshOver(code, "错误");
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
					refreshOver(-1, "错误");
					break;
				case LOADMORE:
					refreshOver(-1, "错误");
					break;
				}
				isRequest = false;
			}
		};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_strategy_list, null);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.tvCost = (TextView) convertView.findViewById(R.id.tvCost);
			holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvIntro = (TextView) convertView.findViewById(R.id.tvIntro);
			holder.view = (View) convertView.findViewById(R.id.view);
			holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);

			LayoutParams params = (LayoutParams) holder.ivPic.getLayoutParams();
			params.width = metrics.widthPixels - context.getResources().getDimensionPixelOffset(R.dimen.strategy_item_margin_left_right) * 2 - context.getResources().getDimensionPixelOffset(R.dimen.strategy_item_padding_left_right) * 2;
			params.height = params.width * 35 / 64;
			holder.ivPic.setLayoutParams(params);

			if (type == ActStrategyAndMemoryAndTicket.TICKET) {
				holder.tvCost.setVisibility(View.VISIBLE);
			} else {
				holder.tvCost.setVisibility(View.GONE);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		NewsModel model = (NewsModel) getItem(position);
		if (position == getMsgList().size() - 1) {
			holder.view.setVisibility(View.VISIBLE);
		} else {
			holder.view.setVisibility(View.GONE);
		}
		holder.tvTitle.setText(model.getTitle());
		holder.tvTime.setText(TimeUtil.getActListTime(model.getPublish_time()));
		holder.tvIntro.setText(model.getIntro());
		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getH_img_url(), 400, 400, 50), holder.ivPic, options);
		if (type == ActStrategyAndMemoryAndTicket.TICKET) {
			holder.tvCost.setText("¥" + model.getPrice());
		}
		holder.llItem.setOnClickListener(new OnItemAllClickListener(model));
		return convertView;
	}

	private static class ViewHolder {
		public TextView tvTitle, tvTime, tvCost, tvIntro;
		public ImageView ivPic;
		public View view;
		public LinearLayout llItem;
	}

	/**
	 * 点击Item
	 */
	private class OnItemAllClickListener implements OnClickListener {

		private NewsModel model;

		public OnItemAllClickListener(NewsModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				Intent intent = null;
				switch (type) {
				case ActStrategyAndMemoryAndTicket.STRATEGY:
					intent = new Intent(context, WebStrategy.class);
					intent.putExtra("MODEL", model);
					context.startActivity(intent);
					break;
				case ActStrategyAndMemoryAndTicket.MEMORY:
					intent = new Intent(context, WebStrategy.class);
					intent.putExtra("MODEL", model);
					context.startActivity(intent);
					break;
				case ActStrategyAndMemoryAndTicket.TICKET:
					intent = new Intent(context, WebStrategy.class);
					intent.putExtra("MODEL", model);
					context.startActivity(intent);
					break;
				}
			}
		}
	}

	@Override
	public void refresh() {

	}

	@Override
	public void loadMore() {

	}

	/**
	 * 加载更多内容
	 */
	public void getMoreStrategyList() {
		if (isOver == 1) {
			refreshOver(0, ISOVER);
		} else {
			if (!isRequest) {
				this.isRequest = true;
				this.loadType = LOADMORE;
				NewsListParam param = new NewsListParam(context, cityId, tagId, type, page, limit);
				HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
				RequestManager.addRequest(task, context);
			}
		}
	}

	/**
	 * 获取内容
	 */
	public void getStrategyList(int cityId, int tagId, int type) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.cityId = cityId;
			this.tagId = tagId;
			this.type = type;
			NewsListParam param = new NewsListParam(context, cityId, tagId, type, page, limit);
			HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}
	
	/**
	 * 只有全部才有缓存
	 */
	public void getCacheList(int type) {
		page = 1;
		isOver = 0;
		loadType = REFRESH;
		String cache = null;
		switch (type) {
		case ActStrategyAndMemoryAndTicket.STRATEGY:
			cache = strategyHelper.getData();
			break;
		case ActStrategyAndMemoryAndTicket.MEMORY:
			cache = memoryHelper.getData();
			break;
		case ActStrategyAndMemoryAndTicket.TICKET:
			cache = ticketHelper.getData();
			break;
		}
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
			Collections.reverse(list.getNews());
			switch (loadType) {
			case REFRESH:
				if (totalNum == 0) {
					refreshOver(list.getNews().size(), ISNULL);
				} else if (page == maxPage) {
					isOver = 1;
					refreshOver(list.getNews().size(), ISOVER);
				} else {
					page++;
					refreshOver(list.getNews().size(), CLICK_MORE);
				}
				refreshItems(list.getNews());
				break;
			}
		}
	}

}
