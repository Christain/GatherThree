package com.gather.android.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.UserCenter;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActCommentModel;
import com.gather.android.model.ActCommentModelList;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.ActCommentListParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class ActDetailCommentAdapter extends SuperAdapter {

	private Activity context;
	private ResponseListener listener;
	private Response.ErrorListener errorListener;
	private int actId, page, limit = 20, totalNum, maxPage, isOver;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayMetrics metrics;

	public ActDetailCommentAdapter(Activity context) {
		super(context);
		this.metrics = context.getResources().getDisplayMetrics();
		this.context = context;
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
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
				ActCommentModelList list = gson.fromJson(result, ActCommentModelList.class);
				if (list != null && list.getComments() != null) {
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
						refreshItems(list.getComments());
						break;
					case LOADMORE:
						if (page != maxPage) {
							page++;
							loadMoreOver(code, CLICK_MORE);
						} else {
							isOver = 1;
							loadMoreOver(code, ISOVER);
						}
						addItems(list.getComments());
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
			convertView = mInflater.inflate(R.layout.item_act_detail_comment, parent, false);
			holder = new ViewHolder();
			holder.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ActCommentModel model = (ActCommentModel) getItem(position);
		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getUser().getHead_img_url(), 150, 150, 50), holder.ivUserIcon, options);
		holder.tvUserName.setText(model.getUser().getNick_name());
		holder.tvContent.setText(model.getContent());
		holder.tvTime.setText(TimeUtil.getUserMessageTime(TimeUtil.getStringtoLong(model.getCreate_time())));
		holder.ivUserIcon.setOnClickListener(new OnUserIconClickListener(model.getUser(), model.getAuthor_id()));
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivUserIcon;
		public TextView tvUserName, tvContent, tvTime;
	}

	@Override
	public void refresh() {

	}

	private class OnUserIconClickListener implements OnClickListener {

		private UserInfoModel model;
		private int userId;

		public OnUserIconClickListener(UserInfoModel model, int userId) {
			this.model = model;
			this.userId = userId;
		}

		@Override
		public void onClick(View v) {
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(context, UserCenter.class);
				intent.putExtra("MODEL", model);
				intent.putExtra("UID", userId);
				context.startActivity(intent);
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
				ActCommentListParam param = new ActCommentListParam(context, actId, page, limit);
				HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
				RequestManager.addRequest(task, context);
			}
		}
	}

	public void getActCommentList(int actId) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.actId = actId;
			ActCommentListParam param = new ActCommentListParam(context, actId, page, limit);
			HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}

}
