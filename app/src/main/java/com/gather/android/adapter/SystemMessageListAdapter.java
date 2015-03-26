package com.gather.android.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.SystemMessageModel;
import com.gather.android.model.SystemMessageModelList;
import com.gather.android.params.SystemMessageParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.DataHelper;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;

public class SystemMessageListAdapter extends SuperAdapter {

	private int page, limit = 20, isOver, totalNum, maxPage;
	private ResponseListener listener;
	private Response.ErrorListener errorListener;
	private Context mContext;
	private DataHelper helper;
	private static String SYSTEM_MSG = "SYSTEM_MSG";

	public SystemMessageListAdapter(Context context) {
		super(context);
		this.mContext = context;
		this.helper = new DataHelper(mContext, SYSTEM_MSG + AppPreference.getUserPersistentInt(mContext, AppPreference.USER_ID));
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
				SystemMessageModelList list = gson.fromJson(result, SystemMessageModelList.class);
				if (list != null && list.getSystem_msgs() != null) {
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
						refreshItems(list.getSystem_msgs());
						break;
					case LOADMORE:
						if (page != maxPage) {
							page++;
							loadMoreOver(code, CLICK_MORE);
						} else {
							isOver = 1;
							loadMoreOver(code, ISOVER);
						}
						addItems(list.getSystem_msgs());
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_system_message, null);
			holder = new ViewHolder();
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.ivTips = (ImageView) convertView.findViewById(R.id.ivTips);
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItem);

			holder.llItemAll.setBackgroundColor(0xFFFFFFFF);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SystemMessageModel model = (SystemMessageModel) getItem(position);
		holder.tvContent.setText(model.getContent());
		holder.tvTime.setText(TimeUtil.getUserMessageTime(TimeUtil.getStringtoLong(model.getCreate_time())));
		if (model.getStatus() == 0) {
			holder.ivTips.setVisibility(View.VISIBLE);
			holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model));
		} else {
			holder.ivTips.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	private static class ViewHolder {
		public TextView tvContent, tvTime;
		public ImageView ivTips;
		public LinearLayout llItemAll;
	}

	/**
	 * 点击Item
	 */
	private class OnItemAllClickListener implements OnClickListener {

		private SystemMessageModel model;

		public OnItemAllClickListener(SystemMessageModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick() && null != model && model.getStatus() == 0) {
				model.setStatus(1);
				notifyDataSetChanged();
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
				SystemMessageParam param = new SystemMessageParam(mContext, page, limit);
				HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), listener, errorListener, param.getParameters());
				RequestManager.addRequest(task, context);
			}
		}
	}

	public void getSystemMessageList() {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			isOver = 0;
			SystemMessageParam param = new SystemMessageParam(mContext, page, limit);
			HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), listener, errorListener, param.getParameters());
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
		} else {
			getSystemMessageList();
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
		SystemMessageModelList list = gson.fromJson(msg, SystemMessageModelList.class);
		if (list != null && list.getSystem_msgs() != null) {
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
				refreshItems(list.getSystem_msgs());
				break;
			}
		}
	}

}
