package com.gather.android.adapter;

import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.baseclass.SuperAdapter;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.ChatMessageList;
import com.gather.android.model.ChatMessageModel;
import com.gather.android.params.GetMessageContentParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ThumbnailUtil;
import com.gather.android.utils.TimeUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

@SuppressLint("InflateParams")
public class ChatAdapter extends SuperAdapter {

	private Context context;
	private int page, maxPage, limit = 20, userId, isOver, totalNum;
	private ResponseListener listener;
	private Response.ErrorListener errorListener;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private String myUserIcon, otherUserIcon;

	public ChatAdapter(Context context, String otherUserIcon) {
		super(context);
		this.context = context;
		this.otherUserIcon = otherUserIcon;
		this.myUserIcon = AppPreference.getUserPersistent(context, AppPreference.USER_PHOTO);
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
						refreshOver(0, "错误");
						isRequest = false;
						return;
					} finally {
						object = null;
					}
				}
				Gson gson = new Gson();
				ChatMessageList list = gson.fromJson(result, ChatMessageList.class);
				if (list != null && list.getMessages() != null) {
					Collections.reverse(list.getMessages());
					switch (loadType) {
					case REFRESH:
						if (totalNum == 0) {
							refreshOver(list.getMessages().size(), ISNULL);
						} else if (page == maxPage) {
							isOver = 1;
							refreshOver(list.getMessages().size(), ISOVER);
						} else {
							page++;
							refreshOver(list.getMessages().size(), CLICK_MORE);
						}
						refreshItems(list.getMessages());
						break;
					case LOADMORE:
						addItemsInFront(list.getMessages());
						if (page != maxPage) {
							page++;
							refreshOver(list.getMessages().size(), CLICK_MORE);
						} else {
							isOver = 1;
							refreshOver(list.getMessages().size(), ISOVER);
						}
						break;
					}
				} else {
					switch (loadType) {
					case REFRESH:
						refreshOver(list.getMessages().size(), ISNULL);
						break;
					case LOADMORE:
						loadMoreOver(list.getMessages().size(), ISOVER);
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
		int type = getItemViewType(position);
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch (type) {
			case 1:
				convertView = mInflater.inflate(R.layout.item_chat_in, null);
				break;
			case 0:
				convertView = mInflater.inflate(R.layout.item_chat_out, null);
				break;
			}
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
			holder.tvChatMessage = (TextView) convertView.findViewById(R.id.tvChatMessage);
			holder.view = (View) convertView.findViewById(R.id.view);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ChatMessageModel model = (ChatMessageModel) getItem(position);
		if (position == getMsgList().size() - 1) {
			holder.view.setVisibility(View.VISIBLE);
		} else {
			holder.view.setVisibility(View.GONE);
		}
		if (model.getRole() == 1) {
			imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(myUserIcon, 100, 100, 50), holder.ivUserIcon, options);
		} else {
			imageLoader.displayImage(ThumbnailUtil.ThumbnailMethod(otherUserIcon, 100, 100, 50), holder.ivUserIcon, options);
		}
		holder.tvChatMessage.setText(model.getContent());
		if (position == 0) {
			holder.tvTime.setVisibility(View.VISIBLE);
			holder.tvTime.setText(TimeUtil.getUserMessageTime(TimeUtil.getStringtoLong(model.getCreate_time())));
		} else {
			ChatMessageModel models = (ChatMessageModel) getItem(position - 1);
			if (TimeUtil.getStringtoLong(model.getCreate_time()) - TimeUtil.getStringtoLong(models.getCreate_time()) > 120000) {
				holder.tvTime.setVisibility(View.VISIBLE);
				holder.tvTime.setText(TimeUtil.getUserMessageTime(TimeUtil.getStringtoLong(model.getCreate_time())));
			} else {
				holder.tvTime.setVisibility(View.GONE);
			}
			models = null;
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		ChatMessageModel model = (ChatMessageModel) getItem(position);//1自己发送者，2自己接收者
		if (model.getRole() == 1) {
			return 0;
		} else {
			return 1;
		}
	}

	private static class ViewHolder {
		public TextView tvTime, tvChatMessage;
		public ImageView ivUserIcon;
		public View view;
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
	public void getMoreMessageList() {
		if (isOver == 1) {
			refreshOver(0, ISOVER);
		} else {
			if (!isRequest) {
				this.isRequest = true;
				this.loadType = LOADMORE;
				GetMessageContentParam param = new GetMessageContentParam(context, userId, page, limit);
				HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
				RequestManager.addRequest(task, context);
			}
		}
	}

	/**
	 * 获取私信内容
	 */
	public void getMessageList(int userId) {
		if (!isRequest) {
			this.isRequest = true;
			this.loadType = REFRESH;
			this.page = 1;
			this.isOver = 0;
			this.userId = userId;
			GetMessageContentParam param = new GetMessageContentParam(context, userId, page, limit);
			HttpStringPost task = new HttpStringPost(context, param.getUrl(), listener, errorListener, param.getParameters());
			RequestManager.addRequest(task, context);
		}
	}
	
	public void setUserIcon(String icon) {
		this.otherUserIcon = icon;
	}

}
