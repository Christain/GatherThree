package com.gather.android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.PublishTrendsPicGallery;
import com.gather.android.activity.TrendsPicGallery;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.database.PublishTrendsInfo;
import com.gather.android.database.PublishTrendsService;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.TrendsListModel;
import com.gather.android.model.TrendsModel;
import com.gather.android.model.TrendsPicModel;
import com.gather.android.model.TrendsPicObjectModel;
import com.gather.android.params.DelTrendsParam;
import com.gather.android.params.UserTrendsParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.NoScrollGridView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

@SuppressLint("InflateParams")
public class UserTrendsAdapter extends SuperAdapter {

	private int page, limit = 20, isOver = 0, totalNum, maxPage, userId, myUserId;
	private ResponseListener listener;
	private ErrorListener errorListener;
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions picOptions;
	private DisplayMetrics metrics;
	private TrendsPicAdapter picAdapter;
	private OnItemAllListener itemListener;
	private HashMap<String, Integer> timeMap;
	private PublishTrendsService service;

	public UserTrendsAdapter(Context context) {
		super(context);
		this.mContext = context;
		this.timeMap = new HashMap<String, Integer>();
		this.metrics = context.getResources().getDisplayMetrics();
		this.myUserId = AppPreference.getUserPersistentInt(mContext, AppPreference.USER_ID);
		this.service = new PublishTrendsService(mContext);
		this.picOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image).showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new FadeInBitmapDisplayer(0)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.initListener();
	}

	private void initListener() {
		listener = new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (page == 1) {
					timeMap.clear();
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
						if (userId == myUserId) {
							getList().clear();
							addItemsNoChanged(getDataBaseTrends());
							addItemsNoChanged(list.getDynamics());
							for (int i = 0; i < getMsgList().size(); i++) {
								String time = ((TrendsModel) getList().get(i)).getUserTrendsTime();
								if (!timeMap.containsKey(time)) {
									timeMap.put(time, i);
								}
								time = null;
							}
							notifyDataSetChanged();
						} else {
							for (int i = 0; i < list.getDynamics().size(); i++) {
								String time = list.getDynamics().get(i).getUserTrendsTime();
								if (!timeMap.containsKey(time)) {
									timeMap.put(time, i);
								}
								time = null;
							}
							refreshItems(list.getDynamics());
						}
						break;
					case LOADMORE:
						if (page != maxPage) {
							page++;
							loadMoreOver(code, CLICK_MORE);
						} else {
							isOver = 1;
							loadMoreOver(code, ISOVER);
						}
						for (int i = 0; i < list.getDynamics().size(); i++) {
							String time = list.getDynamics().get(i).getUserTrendsTime();
							if (!timeMap.containsKey(time)) {
								timeMap.put(time, getCount() + i);
							}
							time = null;
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			switch (getItemViewType(position)) {
			case 0:
				convertView = mInflater.inflate(R.layout.item_user_trends_no_pic, null);
				break;
			case 1:
				convertView = mInflater.inflate(R.layout.item_user_trends_one_pic, null);

				holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivPic.getLayoutParams();
				params.width = metrics.widthPixels / 3;
				params.height = params.width;
				holder.ivPic.setLayoutParams(params);
				break;
			case 2:
				convertView = mInflater.inflate(R.layout.item_user_trends_two_pic, null);
				
				holder.ivPicOne = (ImageView) convertView.findViewById(R.id.ivPicOne);
				holder.ivPicTwo = (ImageView) convertView.findViewById(R.id.ivPicTwo);
				LinearLayout.LayoutParams paramsOne = (LinearLayout.LayoutParams) holder.ivPicOne.getLayoutParams();
				paramsOne.width = metrics.widthPixels / 4;
				paramsOne.height = paramsOne.width;
				holder.ivPicOne.setLayoutParams(paramsOne);
				
				LinearLayout.LayoutParams paramsTwo = (LinearLayout.LayoutParams) holder.ivPicTwo.getLayoutParams();
				paramsTwo.width = paramsOne.width;
				paramsTwo.height = paramsOne.width;
				holder.ivPicTwo.setLayoutParams(paramsTwo);
				break;
			case 3:
				convertView = mInflater.inflate(R.layout.item_user_trends_more_pic, null);
				holder.gridView = (NoScrollGridView) convertView.findViewById(R.id.gridView);
				break;
			}
			holder.tvTimeBaseAll = (TextView) convertView.findViewById(R.id.tvTimeBaseAll);
			holder.tvTimeBaseDay = (TextView) convertView.findViewById(R.id.tvTimeBaseDay);
			holder.tvTimeBaseMonth = (TextView) convertView.findViewById(R.id.tvTimeBaseMonth);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.tvDel = (TextView) convertView.findViewById(R.id.tvDel);
			holder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TrendsModel model = (TrendsModel) getItem(position);
		if (timeMap.get(model.getUserTrendsTime()) == position) {
			if (model.getUserTrendsTime().contains("天")) {
				if (myUserId == model.getAuthor_id() && model.getUserTrendsTime().contains("今天")) {
					holder.tvTimeBaseAll.setVisibility(View.GONE);
					holder.tvTimeBaseMonth.setVisibility(View.GONE);
					holder.tvTimeBaseDay.setVisibility(View.GONE);
				} else {
					holder.tvTimeBaseAll.setVisibility(View.VISIBLE);
					holder.tvTimeBaseMonth.setVisibility(View.GONE);
					holder.tvTimeBaseDay.setVisibility(View.GONE);
					holder.tvTimeBaseAll.setText(model.getUserTrendsTime());
				}
			} else if (model.getUserTrendsTime().contains("未知")) {
				holder.tvTimeBaseAll.setVisibility(View.GONE);
				holder.tvTimeBaseMonth.setVisibility(View.GONE);
				holder.tvTimeBaseDay.setVisibility(View.GONE);
			} else if (model.getUserTrendsTime().contains("-")) {
				holder.tvTimeBaseAll.setVisibility(View.GONE);
				holder.tvTimeBaseMonth.setVisibility(View.VISIBLE);
				holder.tvTimeBaseDay.setVisibility(View.VISIBLE);
				holder.tvTimeBaseDay.setText(model.getUserTrendsTime().substring(model.getUserTrendsTime().indexOf("-", 1) + 1, model.getUserTrendsTime().length()));
				holder.tvTimeBaseMonth.setText(getMonth(model.getUserTrendsTime()));
			} else {
				holder.tvTimeBaseAll.setVisibility(View.VISIBLE);
				holder.tvTimeBaseMonth.setVisibility(View.GONE);
				holder.tvTimeBaseDay.setVisibility(View.GONE);
				holder.tvTimeBaseAll.setText("未知");
			}
		} else {
			holder.tvTimeBaseAll.setVisibility(View.GONE);
			holder.tvTimeBaseMonth.setVisibility(View.GONE);
			holder.tvTimeBaseDay.setVisibility(View.GONE);
		}
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
		switch (model.getUserTrendsType()) {
		case 1:
			if (model.isLocal()) {
				imageLoader.displayImage(Uri.decode(Uri.fromFile(new File(model.getImgs().getImgs().get(0).getImg_url())).toString()), holder.ivPic, picOptions);
			} else {
				imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImgs().getImgs().get(0).getImg_url(), 150, 150, 50), holder.ivPic, picOptions);
			}
			holder.ivPic.setOnClickListener(new OnOnePicClickListener(model, 0));
			break;
		case 2:
			if (model.isLocal()) {
				imageLoader.displayImage(Uri.decode(Uri.fromFile(new File(model.getImgs().getImgs().get(0).getImg_url())).toString()), holder.ivPicOne, picOptions);
				imageLoader.displayImage(Uri.decode(Uri.fromFile(new File(model.getImgs().getImgs().get(1).getImg_url())).toString()), holder.ivPicTwo, picOptions);
			} else {
				imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImgs().getImgs().get(0).getImg_url(), 150, 150, 50), holder.ivPicOne, picOptions);
				imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getImgs().getImgs().get(1).getImg_url(), 150, 150, 50), holder.ivPicTwo, picOptions);
			}
			holder.ivPicOne.setOnClickListener(new OnOnePicClickListener(model, 0));
			holder.ivPicTwo.setOnClickListener(new OnOnePicClickListener(model, 1));
			break;
		case 3:
			if (model.isLocal()) {
				picAdapter = new TrendsPicAdapter(mContext, model.getImgs().getImgs(), 2, model.isLocal());
			} else {
				picAdapter = new TrendsPicAdapter(mContext, model.getImgs().getImgs(), 2);
			}
			holder.gridView.setAdapter(picAdapter);
			break;
		}
		if (!model.isLocal()) {
			holder.llItemAll.setBackgroundResource(R.drawable.shape_white_bg_click_gray_style);
			holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model, position));
		} else {
			holder.llItemAll.setBackgroundColor(0xFFFFFFFF);
			holder.llItemAll.setOnClickListener(null);
		}
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivPic, ivPicOne, ivPicTwo;
		public TextView tvContent, tvTimeBaseAll, tvTimeBaseDay, tvTimeBaseMonth, tvDel, tvComment;
		public NoScrollGridView gridView;
		public LinearLayout llItemAll;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		TrendsModel model = (TrendsModel) getItem(position);// 0没有图片，1有一张图片，2有2张图片,3有多长图片
		return model.getUserTrendsType();
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
		DelTrendsParam param = new DelTrendsParam(mContext, dynamicId);
		HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				isRequest = false;
				removeItem(position);
			}

			@Override
			public void relogin(String msg) {
				isRequest = false;
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				isRequest = false;
				toast("删除失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				isRequest = false;
				toast("删除失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
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
	 * 单张图片点击
	 */
	private class OnOnePicClickListener implements OnClickListener {

		private TrendsModel model;
		private int position;

		public OnOnePicClickListener(TrendsModel model, int position) {
			this.model = model;
			this.position = position;
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
				intent.putExtra("POSITION", position);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Intent intent = new Intent(mContext, TrendsPicGallery.class);
				intent.putExtra("LIST", model.getImgs().getImgs());
				intent.putExtra("POSITION", position);
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
				model.setCreate_time(TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
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
	
	public HashMap<String, Integer> getTimeMap() {
		return timeMap;
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
				UserTrendsParam param = new UserTrendsParam(mContext, userId, page, limit);
				HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), listener, errorListener, param.getParameters());
				RequestManager.addRequest(task, context);
			}
		}
	}

	public void getUserTrendsList(int userId) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.userId = userId;
			isOver = 0;
			UserTrendsParam param = new UserTrendsParam(mContext, userId, page, limit);
			HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}
	
	private String getMonth(String time) {
		String monthTime = null;
		String month = time.substring(0, time.indexOf("-", 1));
		switch (Integer.parseInt(month)) {
		case 1:
			monthTime = "一月";
			break;
		case 2:
			monthTime = "二月";
			break;
		case 3:
			monthTime = "三月";
			break;
		case 4:
			monthTime = "四月";
			break;
		case 5:
			monthTime = "五月";
			break;
		case 6:
			monthTime = "六月";
			break;
		case 7:
			monthTime = "七月";
			break;
		case 8:
			monthTime = "八月";
			break;
		case 9:
			monthTime = "九月";
			break;
		case 10:
			monthTime = "十月";
			break;
		case 11:
			monthTime = "十一月";
			break;
		case 12:
			monthTime = "十二月";
			break;
		default:
			monthTime = "未知";
			break;
		}
		return monthTime;
	}

}
