package com.gather.android.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.activity.ActDetail;
import com.gather.android.activity.QRCodeScan;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ActHasEnrollModel;
import com.gather.android.model.ActHasEnrollModelList;
import com.gather.android.params.ActHasEnrollListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class ActHasEnrollListAdapter extends SuperAdapter {

	private Activity context;
	private ResponseListener listener;
	private Response.ErrorListener errorListener;
	private int myUserId, page, limit = 20, totalNum, maxPage, isOver, userId;

	public ActHasEnrollListAdapter(Activity context) {
		super(context);
		this.context = context;
		this.myUserId = AppPreference.getUserPersistentInt(context, AppPreference.USER_ID);
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
				ActHasEnrollModelList list = gson.fromJson(result, ActHasEnrollModelList.class);
				if (list != null && list.getEnrolls() != null) {
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
						refreshItems(list.getEnrolls());
						break;
					case LOADMORE:
						if (page != maxPage) {
							page++;
							loadMoreOver(code, CLICK_MORE);
						} else {
							isOver = 1;
							loadMoreOver(code, ISOVER);
						}
						addItems(list.getEnrolls());
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
			convertView = mInflater.inflate(R.layout.item_act_has_enroll_list, parent, false);
			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.tvSign = (TextView) convertView.findViewById(R.id.tvSign);
			holder.tvActStatus = (TextView) convertView.findViewById(R.id.tvActStatus);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ActHasEnrollModel model = (ActHasEnrollModel) getItem(position);
		holder.tvTitle.setText(model.getAct().getTitle());
		holder.tvTime.setText(TimeUtil.getUserMessageTime(TimeUtil.getStringtoLong(model.getCreate_time())));
		switch (model.getAct().getT_status()) {
		case 1:// 即将开始
			holder.tvActStatus.setText("将开始");
			holder.tvSign.setVisibility(View.GONE);
			break;
		case 2:// 进行中
			holder.tvActStatus.setText("进行中");
			holder.tvSign.setVisibility(View.VISIBLE);
			holder.tvSign.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!ClickUtil.isFastClick()) {
						Intent intent = new Intent(context, QRCodeScan.class);
						context.startActivity(intent);
					}
				}
			});
			break;
		case 3:// 筹备中
			holder.tvActStatus.setText("筹备中");
			holder.tvSign.setVisibility(View.GONE);
			break;
		case 4:// 已结束
			holder.tvActStatus.setText("已结束");
			holder.tvSign.setVisibility(View.GONE);
			break;
		}
		if (myUserId != userId) {
			holder.tvSign.setVisibility(View.GONE);
			holder.tvContent.setVisibility(View.GONE);
		} else {
			holder.tvContent.setVisibility(View.VISIBLE);
			holder.tvContent.setText("姓名：" + model.getName() + "\n" + "联系电话：" + model.getPhone() + "\n" + "报名人数：" + model.getPeople_num());
		}
		
		holder.llItem.setOnClickListener(new OnItemAllClickListener(model));
		return convertView;
	}

	private static class ViewHolder {
		public TextView tvTitle, tvTime, tvSign, tvActStatus, tvContent;
		public LinearLayout llItem;
	}

	@Override
	public void refresh() {

	}

	private class OnItemAllClickListener implements OnClickListener {

		private ActHasEnrollModel model;

		public OnItemAllClickListener(ActHasEnrollModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View v) {
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(context, ActDetail.class);
				intent.putExtra("ID", model.getAct_id());
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
				ActHasEnrollListParam param = new ActHasEnrollListParam(context, userId, page, limit);
				HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
				RequestManager.addRequest(task, context);
			}
		}
	}

	public void getActHasEnrollList(int userId) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.userId = userId;
			ActHasEnrollListParam param = new ActHasEnrollListParam(context, userId, page, limit);
			HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}

}
