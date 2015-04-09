package com.gather.android.adapter;

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

import com.gather.android.R;
import com.gather.android.activity.UserCenter;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.MessageUserList;
import com.gather.android.model.MessageUserListModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.CancelMessageUserParam;
import com.gather.android.params.GetMessageUserListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.DataHelper;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatUserListAdapter extends SuperAdapter {

	private int page, limit = 20, cityId, isOver, totalNum, maxPage;
	private ResponseHandler responseHandler;
	private Activity mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private LoadingDialog mLoadingDialog;
	private GatherApplication application;
	private OnItemAllClickListener clickListener;
	private DataHelper helper;
	private static String CHAT_USER = "CHAT_USER";

	public ChatUserListAdapter(Activity context) {
		super(context);
		this.mContext = context;
		this.mLoadingDialog = LoadingDialog.createDialog(mContext, true);
		this.application = (GatherApplication) context.getApplication();
		this.helper = new DataHelper(mContext, CHAT_USER + AppPreference.getUserPersistentInt(mContext, AppPreference.USER_ID));
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
		this.initListener();
	}

	private void initListener() {
        this.responseHandler = new ResponseHandler() {
            @Override
            public void onResponseSuccess(int code, Header[] headers, String result) {
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
                MessageUserList list = gson.fromJson(result, MessageUserList.class);
                if (list != null && list.getUsers() != null) {
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
                            refreshItems(list.getUsers());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(code, CLICK_MORE);
                            } else {
                                isOver = 1;
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_chat_user_list, null);
			holder = new ViewHolder();
			holder.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
			holder.ivUserSex = (ImageView) convertView.findViewById(R.id.ivUserSex);
			holder.ivRedTips = (ImageView) convertView.findViewById(R.id.ivTips);
			holder.ivShield = (ImageView) convertView.findViewById(R.id.ivShield);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MessageUserListModel model = (MessageUserListModel) getItem(position);
		imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(model.getHead_img_url(), 100, 100, 50), holder.ivUserIcon, options);
		holder.tvUserName.setText(model.getNick_name());
		if (model.getSex() == 1) {
			holder.ivUserSex.setImageResource(R.drawable.icon_user_list_sex_male);
		} else {
			holder.ivUserSex.setImageResource(R.drawable.icon_user_list_sex_female);
		}
		holder.tvContent.setText(model.getContent());
		holder.tvTime.setText(TimeUtil.getUserMessageTime(TimeUtil.getStringtoLong(model.getLast_contact_time())));
		if (model.getStatus() == 0) {
			if (model.getNew_msg_num() != 0) {
				holder.ivRedTips.setVisibility(View.VISIBLE);
			} else {
				holder.ivRedTips.setVisibility(View.GONE);
			}
			holder.ivShield.setVisibility(View.INVISIBLE);
		} else {
			holder.ivRedTips.setVisibility(View.GONE);
			holder.ivShield.setVisibility(View.VISIBLE);
		}

		holder.ivUserIcon.setOnClickListener(new OnUserIconClickListener(model));
		holder.llItemAll.setOnClickListener(new OnAllClickListener(model, position));
		holder.llItemAll.setOnLongClickListener(new OnItemAllLongClickListener(model));
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivUserIcon, ivUserSex, ivRedTips, ivShield;
		public TextView tvUserName, tvContent, tvTime;
		public LinearLayout llItemAll;
	}

	private class OnAllClickListener implements OnClickListener {

		private MessageUserListModel model;
		private int position;

		public OnAllClickListener(MessageUserListModel model, int position) {
			this.model = model;
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				if (clickListener != null) {
					clickListener.clickListener(model, position);
				}
			}
		}
	}

	public interface OnItemAllClickListener {
		public void clickListener(MessageUserListModel model, int position);
	}

	public void setOnItemAllClickListener(OnItemAllClickListener listener) {
		this.clickListener = listener;
	}

	private class OnItemAllLongClickListener implements OnLongClickListener {

		private MessageUserListModel model;

		public OnItemAllLongClickListener(MessageUserListModel model) {
			this.model = model;
		}

		@Override
		public boolean onLongClick(View arg0) {
			CancelUser(model);
			return true;
		}
	}

	/**
	 * 点击头像
	 */
	private class OnUserIconClickListener implements OnClickListener {

		private MessageUserListModel model;

		public OnUserIconClickListener(MessageUserListModel model) {
			this.model = model;
		}

		@Override
		public void onClick(View arg0) {
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent(context, UserCenter.class);
				intent.putExtra("UID", model.getId());
				UserInfoModel userInfoModel = new UserInfoModel();
				userInfoModel.setNick_name(model.getNick_name());
				userInfoModel.setSex(model.getSex());
				userInfoModel.setBirth(model.getBirth());
				userInfoModel.setHobby(model.getHobby());
				userInfoModel.setHead_img_url(model.getHead_img_url());
				userInfoModel.setIntro(model.getIntro());
				intent.putExtra("MODEL", userInfoModel);
				context.startActivity(intent);
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
				GetMessageUserListParam param = new GetMessageUserListParam(cityId, page, limit);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
			}
		}
	}

	public void getChatUserList(int cityId) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			isOver = 0;
			this.cityId = cityId;
            GetMessageUserListParam param = new GetMessageUserListParam(cityId, page, limit);
            AsyncHttpTask.post(param.getUrl(), param, responseHandler);
		}
	}

	public void getCacheList(int cityId) {
		page = 1;
		isOver = 0;
		loadType = REFRESH;
		String cache = helper.getData();
		if (null != cache) {
			setCacheMessage(cache);
			cache = null;
		} else {
			getChatUserList(cityId);
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
		MessageUserList list = gson.fromJson(msg, MessageUserList.class);
		if (list != null && list.getUsers() != null) {
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
				refreshItems(list.getUsers());
				break;
			}
		}
	}

	/**
	 * 删除私信联系人
	 */
	private void CancelUser(final MessageUserListModel model) {
		final DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(mContext);
		dialog.setMessage("您确定要删除？").withDuration(400).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mLoadingDialog.setMessage("正在删除...");
				mLoadingDialog.show();
				CancelMessageUserParam param = new CancelMessageUserParam(model.getId());
                AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                    @Override
                    public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        getMsgList().remove(model);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeedLogin(String msg) {
                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        dialog.dismiss();
                        needLogin(msg);
                    }

                    @Override
                    public void onResponseFailed(int returnCode, String errorMsg) {
                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        dialog.dismiss();
                        toast(errorMsg);
                    }
                });
			}
		}).show();
	}

}
