package com.gather.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.activity.ActDetail;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActModel;
import com.gather.android.model.ActModelList;
import com.gather.android.params.ActHasCommentListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class ActHasCommentListAdapter extends SuperAdapter {

	private Activity context;
	private ResponseHandler responseHandler;
	private int myUserId, page, limit = 20, totalNum, maxPage, isOver, userId;

	public ActHasCommentListAdapter(Activity context) {
		super(context);
		this.context = context;
		this.myUserId = AppPreference.getUserPersistentInt(context, AppPreference.USER_ID);
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
			convertView = mInflater.inflate(R.layout.item_act_has_comment_list, parent, false);
			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ActModel model = (ActModel) getItem(position);
		holder.tvTitle.setText(model.getTitle());
		switch (model.getT_status()) {
		case 1:// 即将开始
			holder.tvTime.setText("将开始");
			break;
		case 2:// 进行中
			holder.tvTime.setText("进行中");
			break;
		case 3:// 筹备中
			holder.tvTime.setText("筹备中");
			break;
		case 4:// 已结束
			holder.tvTime.setText("已结束");
			break;
		}
		holder.llItem.setOnClickListener(new OnItemAllClickListener(model));
		return convertView;
	}

	private static class ViewHolder {
		public TextView tvTitle, tvTime;
		public LinearLayout llItem;
	}

	@Override
	public void refresh() {

	}

	private class OnItemAllClickListener implements OnClickListener {

		private ActModel model;

		public OnItemAllClickListener(ActModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View v) {
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(context, ActDetail.class);
				intent.putExtra("ID", model.getId());
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
				ActHasCommentListParam param = new ActHasCommentListParam(userId, page, limit);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
			}
		}
	}

	public void getActHasCommentList(int userId) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.userId = userId;
			ActHasCommentListParam param = new ActHasCommentListParam(userId, page, limit);
            AsyncHttpTask.post(param.getUrl(), param, responseHandler);
		}
	}

}
