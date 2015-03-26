package com.gather.android.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActModel;
import com.gather.android.model.ActModelList;
import com.gather.android.params.ActHotListParam;
import com.gather.android.params.ActListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.DataHelper;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class ActListAdapter extends SuperAdapter {

	private Activity context;
	private ResponseListener listener;
	private Response.ErrorListener errorListener;
	private int cityId, titleIndex, page, limit = 10, totalNum, maxPage, isOver;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayMetrics metrics;
	private OnActDetailClickListener actdetailListener;
	private GatherApplication application;
	private LatLng pt1;
	private boolean isHot = false;
	private DataHelper helper, hotHelper;
	private static String ACT_ALL = "ACT_ALL";
	private static String HOT_ALL = "HOT_ALL";

	public ActListAdapter(Activity context) {
		super(context);
		this.metrics = context.getResources().getDisplayMetrics();
		this.application = (GatherApplication) context.getApplication();
		this.context = context;
		this.helper = new DataHelper(context, ACT_ALL + AppPreference.getUserPersistentInt(context, AppPreference.USER_ID));
		this.hotHelper = new DataHelper(context, HOT_ALL + AppPreference.getUserPersistentInt(context, AppPreference.USER_ID));
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.initListener();
	}

	private void initListener() {
		listener = new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (page == 1) {
					if (!isHot) {
						if (titleIndex == 1) {
							if (helper != null) {
								helper.saveData(result);
							}
						}
					} else {
						if (hotHelper != null) {
							hotHelper.saveData(result);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_act_list, parent, false);
			holder = new ViewHolder();
			holder.flItemAll = (FrameLayout) convertView.findViewById(R.id.flItemAll);
			holder.viewBackground = (ImageView) convertView.findViewById(R.id.viewBackground);
			holder.ivActPic = (ImageView) convertView.findViewById(R.id.ivActPic);
			holder.tvActTitle = (TextView) convertView.findViewById(R.id.tvActTitle);
			holder.tvActTime = (TextView) convertView.findViewById(R.id.tvActTime);
			holder.tvActAddress = (TextView) convertView.findViewById(R.id.tvActAddress);
			holder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);

			LayoutParams params = (LayoutParams) holder.ivActPic.getLayoutParams();
			params.width = metrics.widthPixels;
			params.height = params.width * 11 / 18;
			holder.ivActPic.setLayoutParams(params);
			holder.viewBackground.setLayoutParams(params);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ActModel model = (ActModel) getItem(position);
		holder.viewBackground.setVisibility(View.INVISIBLE);
		holder.tvActTitle.setText(model.getTitle());
		holder.tvActAddress.setText(model.getAddr_road());
		if (model.getT_status() == 2) {
			holder.tvActTime.setText(TimeUtil.getActListTime(TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis())));
		} else {
			holder.tvActTime.setText(TimeUtil.getActListTime(model.getB_time()));
		}

		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getHead_img_url(), 500, 500, 50), holder.ivActPic, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				holder.progressBar.setProgress(0);
				holder.progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				holder.progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				holder.progressBar.setVisibility(View.GONE);
				holder.viewBackground.setVisibility(View.VISIBLE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				holder.progressBar.setProgress(Math.round(100.0f * current / total));
			}
		});
		if (application.mLocation != null && model.getLat() != 0 && model.getLon() != 0) {
			LatLng pt2 = new LatLng(model.getLat(), model.getLon());
			holder.tvDistance.setText(", " + (int) DistanceUtil.getDistance(pt1, pt2) / 1000 + "km");
			holder.tvDistance.setVisibility(View.VISIBLE);
		} else {
			holder.tvDistance.setVisibility(View.GONE);
		}
		holder.flItemAll.setOnClickListener(new OnDetailClickListener(model, position));
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivActPic, viewBackground;
		public TextView tvActTitle, tvActTime, tvActAddress, tvDistance;
		public FrameLayout flItemAll;
		public ProgressBar progressBar;
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
				if (!isHot) {
					ActListParam param = new ActListParam(context, cityId, page, limit);
					if (titleIndex != 0) {
						param.setTagId(titleIndex);
					}
					HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
					RequestManager.addRequest(task, context);
				} else {
					ActHotListParam param = new ActHotListParam(context, cityId, page, limit);
					HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
					RequestManager.addRequest(task, context);
				}
			}
		}
	}

	public void getActList(int cityId, int titleIndex) {
		if (!isRequest) {
			if (application.mLocation != null) {
				pt1 = new LatLng(application.mLocation.getLatitude(), application.mLocation.getLongitude());
			}
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.cityId = cityId;
			this.titleIndex = titleIndex;
			this.isHot = false;
			ActListParam param = new ActListParam(context, cityId, page, limit);
			if (titleIndex != 0) {
				param.setTagId(titleIndex);
			}
			HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}

	public void getActHotList(int cityId) {
		if (!isRequest) {
			if (application.mLocation != null) {
				pt1 = new LatLng(application.mLocation.getLatitude(), application.mLocation.getLongitude());
			}
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.cityId = cityId;
			this.isHot = true;
			ActHotListParam param = new ActHotListParam(context, cityId, page, limit);
			HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}

	/**
	 * 只有全部才有缓存
	 */
	public void getCacheList(boolean isWeek) {
		String cache = null;
		if (isWeek) {
			cache = helper.getData();
		} else {
			cache = hotHelper.getData();
		}
		if (null != cache) {
			setCacheMessage(cache);
			cache = null;
		}
	}

	private void setCacheMessage(String msg) {
		Gson gson = new Gson();
		ActModelList list = gson.fromJson(msg, ActModelList.class);
		if (list != null && list.getActs() != null) {
			isOver = 1;
			refreshOver(0, CACHE);
			refreshItems(list.getActs());
		}
	}

	private final double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public double DistanceOfTwoPoints(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

}
