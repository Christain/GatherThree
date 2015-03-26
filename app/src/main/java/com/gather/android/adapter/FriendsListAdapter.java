package com.gather.android.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.activity.LoginIndex;
import com.gather.android.activity.UserCenter;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.UserInfoModel;
import com.gather.android.model.UserList;
import com.gather.android.params.AddFocusParam;
import com.gather.android.params.CancelFocusParam;
import com.gather.android.params.FansAndFocusListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.ThumbnailUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tendcloud.tenddata.TCAgent;

public class FriendsListAdapter extends SuperAdapter {

	private int page, limit = 20, uid, titleType, cityId, isOverOne = 0, isOverTwo = 0, totalNum, maxPage;
	private ResponseListener listener;
	private ErrorListener errorListener;
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DialogChoiceBuilder choiceBuilder;
	private DisplayImageOptions options;
	private int myUserId;
	private LoadingDialog mLoadingDialog;
	private boolean isRequest = false;

	public FriendsListAdapter(Context context) {
		super(context);
		this.mContext = context;
		this.choiceBuilder = DialogChoiceBuilder.getInstance(mContext);
		this.mLoadingDialog = LoadingDialog.createDialog(mContext, false);
		this.myUserId = AppPreference.getUserPersistentInt(mContext, AppPreference.USER_ID);
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
				UserList list = gson.fromJson(result, UserList.class);
				if (list != null && list.getUsers() != null) {
					switch (loadType) {
					case REFRESH:
						if (totalNum == 0) {
							refreshOver(code, ISNULL);
						} else if (page == maxPage) {
							if (titleType == 1) {
								isOverOne = 1;
							} else {
								isOverTwo = 1;
							}
							refreshOver(code, ISOVER);
						} else {
							page++;
							refreshOver(code, CLICK_MORE);
						}
						refreshItems(list.getUsers());
						break;
					case LOADMORE:
						if (page != maxPage) {
							page++;
							loadMoreOver(code, CLICK_MORE);
						} else {
							if (titleType == 1) {
								isOverOne = 1;
							} else {
								isOverTwo = 1;
							}
							loadMoreOver(code, ISOVER);
						}
						addItems(list.getUsers());
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_friends_list, null);
			holder = new ViewHolder();
			holder.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
			holder.ivUserSex = (ImageView) convertView.findViewById(R.id.ivUserSex);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			holder.tvUserBrief = (TextView) convertView.findViewById(R.id.tvUserBrief);
			holder.tvFocus = (TextView) convertView.findViewById(R.id.tvFocus);
			holder.ivVip = (ImageView) convertView.findViewById(R.id.ivVip);
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserInfoModel model = (UserInfoModel) getItem(position);
		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getHead_img_url(), 100, 100, 50), holder.ivUserIcon, options);
		holder.tvUserName.setText(model.getNick_name());
		holder.tvUserBrief.setText(model.getIntro());
		holder.tvFocus.setVisibility(View.VISIBLE);
		if (myUserId == model.getUid()) {
			holder.tvFocus.setVisibility(View.GONE);
		} else {
			holder.tvFocus.setVisibility(View.VISIBLE);
			if (model.getIs_focus() == 0) {
				holder.tvFocus.setText("关注");
			} else {
				holder.tvFocus.setText("取消关注");
			}
		}
		if (model.getIs_vip() == 0) {
			holder.ivVip.setVisibility(View.GONE);
		} else {
			holder.ivVip.setVisibility(View.VISIBLE);
		}
		if (model.getSex() == 1) {
			holder.ivUserSex.setImageResource(R.drawable.icon_user_list_sex_male);
		} else {
			holder.ivUserSex.setImageResource(R.drawable.icon_user_list_sex_female);
		}

		holder.tvFocus.setOnClickListener(new OnFocusClickListener(model, position));
		holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model));
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivUserIcon, ivUserSex, ivVip;
		public TextView tvUserName, tvUserBrief, tvFocus;
		public LinearLayout llItemAll;
	}

	private class OnItemAllClickListener implements OnClickListener {

		private UserInfoModel model;

		public OnItemAllClickListener(UserInfoModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			if (myUserId == model.getUid()) {
				toast("这是您自己");
			} else {
				Intent intent = new Intent(mContext, UserCenter.class);
				intent.putExtra("UID", model.getUid());
				intent.putExtra("MODEL", model);
				mContext.startActivity(intent);
			}
		}
	}

	private class OnFocusClickListener implements OnClickListener {

		private UserInfoModel model;
		private int position;

		public OnFocusClickListener(UserInfoModel model, int position) {
			this.model = model;
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				if (AppPreference.hasLogin(mContext)) {
					if (model.getIs_focus() == 1 && !isRequest) {
						isRequest = true;
						CancelFocus(model, position);
					} else if (model.getIs_focus() == 0 && !isRequest) {
						isRequest = true;
						AddFocus(model, position);
					}
				} else {
					DialogLogin();
				}
			}
		}
	}

	/**
	 * 取消关注
	 */
	private void CancelFocus(final UserInfoModel model, final int position) {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("取消关注中...");
			mLoadingDialog.show();
		}
		CancelFocusParam param = new CancelFocusParam(mContext, model.getUid());
		HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				if (titleType == 2) {
					model.setIs_focus(0);
				} else {
					getMsgList().remove(position);
				}
				notifyDataSetChanged();
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				toast("取消关注失败");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				toast("取消关注失败");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 加关注
	 */
	private void AddFocus(final UserInfoModel model, final int position) {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("关注中...");
			mLoadingDialog.show();
		}
		AddFocusParam param = new AddFocusParam(mContext, model.getUid());
		HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				TCAgent.onEvent(mContext, "关注人");
				isRequest = false;
				model.setIs_focus(1);
				notifyDataSetChanged();
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				toast("加关注失败");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				toast("加关注失败");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	@Override
	public void refresh() {

	}

	@Override
	public void loadMore() {
		if (titleType == 1 && isOverOne == 1) {
			loadMoreOver(0, ISOVER);
		} else if (titleType == 2 && isOverTwo == 1) {
			loadMoreOver(0, ISOVER);
		} else {
			if (!isRequest) {
				this.isRequest = true;
				this.loadType = LOADMORE;
				FansAndFocusListParam param = new FansAndFocusListParam(mContext, uid, cityId, titleType, page, limit);
				HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), listener, errorListener, param.getParameters());
				RequestManager.addRequest(task, context);
			}
		}
	}

	public void getFansAndFocusList(int uid, int cityId, int titleType) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			if (titleType == 1) {
				isOverOne = 0;
			} else {
				isOverTwo = 0;
			}
			this.uid = uid;
			this.cityId = cityId;
			this.titleType = titleType;
			FansAndFocusListParam param = new FansAndFocusListParam(mContext, uid, cityId, titleType, page, limit);
			HttpStringPost task = new HttpStringPost(mContext, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}

	/**
	 * 需要去登录
	 */
	private void DialogLogin() {
		if (choiceBuilder != null && !choiceBuilder.isShowing()) {
			choiceBuilder.setMessage("想看更多内容，现在就去登录吧？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (PushManager.isPushEnabled(mContext)) {
						PushManager.stopWork(mContext);
					}
					GatherApplication application = (GatherApplication) mContext.getApplicationContext();
					application.setUserInfoModel(null);
					AppPreference.clearInfo(mContext);
					Intent intent = new Intent(mContext, LoginIndex.class);
					mContext.startActivity(intent);
					choiceBuilder.dismiss();
				}
			}).show();
		}
	}

}
