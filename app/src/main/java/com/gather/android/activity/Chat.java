package com.gather.android.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;
import com.gather.android.R;
import com.gather.android.adapter.ChatAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.listener.OnAdapterRefreshOverListener;
import com.gather.android.model.ChatMessageModel;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.CancelShieldContactUserParam;
import com.gather.android.params.DelMessageParam;
import com.gather.android.params.GetUserCenterParam;
import com.gather.android.params.SendChatMessageParam;
import com.gather.android.params.ShieldContactUserParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.ChatListView;
import com.gather.android.widget.ChatListView.IXListViewListener;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

/**
 * 私信
 */
public class Chat extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private int userId;
	private EditText etContent;
	private ImageView btSend;
	private ChatListView listView;
	private ChatAdapter adapter;
	private String otherUserName = "", otherUserIcon = "";
	private UserInfoModel userInfoModel;
	private boolean isCallBack = false;// 判断内容是否发生改变，好更新联系人列表
	private boolean isRequest = false;
	private boolean isStatus = false;
	private int status;// 0正常，1已屏蔽提醒
	private String myUserName, baidu_api_key = "";
	private int myUserId;

	private LoadingDialog mLoadingDialog;

	@Override
	protected int layoutResId() {
		return R.layout.chat;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("UID")) {
			this.userId = intent.getExtras().getInt("UID");
			if (intent.hasExtra("MODEL")) {
				this.userInfoModel = (UserInfoModel) intent.getSerializableExtra("MODEL");
				this.otherUserName = userInfoModel.getNick_name();
				this.otherUserIcon = userInfoModel.getHead_img_url();
				this.status = userInfoModel.getIs_shield();
			}
			this.myUserName = AppPreference.getUserPersistent(Chat.this, AppPreference.NICK_NAME);
			this.myUserId = AppPreference.getUserPersistentInt(Chat.this, AppPreference.USER_ID);
			this.mLoadingDialog = LoadingDialog.createDialog(Chat.this, true);
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvTitle.setText(otherUserName);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.VISIBLE);
			this.ivLeft.setVisibility(View.VISIBLE);
			if (userInfoModel != null) {
				if (status == 0) {
					this.tvRight.setText("屏蔽");
				} else {
					this.tvRight.setText("取消屏蔽");
				}
			} else {
				tvRight.setVisibility(View.INVISIBLE);
			}
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);
			this.tvRight.setOnClickListener(this);

			this.etContent = (EditText) findViewById(R.id.etContent);
			this.btSend = (ImageView) findViewById(R.id.btSend);
			this.listView = (ChatListView) findViewById(R.id.chatListView);
			this.listView.setPullRefreshEnable(true);
			this.adapter = new ChatAdapter(Chat.this, otherUserIcon);

			this.listView.setAdapter(adapter);
			this.listView.setXListViewListener(new IXListViewListener() {
				@Override
				public void onRefresh() {
					adapter.getMoreMessageList();
				}
			});
			this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
				@Override
				public void refreshOver(int code, String msg) {
					listView.stopRefresh();
					if (msg.contains("登录")) {
						needLogin(msg);
					} else if (msg.contains("错误")) {
						toast("获取失败");
					} else {
						listView.setSelection(code);
					}
				}
			});
			this.etContent.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View arg0, boolean focus) {
					if (focus) {
						if (adapter.getCount() > 0) {
							listView.setSelection(adapter.getCount() - 1);
						}
					} else {
						closeKeyboard();
					}
				}
			});
			this.listView.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					etContent.clearFocus();
				}

				@Override
				public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

				}
			});
			this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					ChatMessageModel model = (ChatMessageModel) adapter.getItem(position - 1);
					if (null != model) {
						String name = "";
						if (model.getRole() == 1) {
							name = myUserName;
						} else {
							name = otherUserName;
						}
						DelMessageDialog dialog = new DelMessageDialog(Chat.this, R.style.dialog_del_message, name, position - 1, model.getContent(), model.getId());
						dialog.show();
					}
					return true;
				}
			});
			if (otherUserName.length() > 0 && otherUserIcon.length() > 0) {
				this.listView.onClickRefush();
				this.adapter.getMessageList(userId);
			} else {
				GatherApplication application = (GatherApplication) getApplication();
				getUserInfo(application.getCityId());
			}

			this.btSend.setOnClickListener(this);
		} else {
			toast("私信信息有误");
			finish();
		}
	}

	/**
	 * 获取个人信息
	 */
	private void getUserInfo(int cityId) {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("获取信息中...");
			mLoadingDialog.show();
		}
		GetUserCenterParam param = new GetUserCenterParam(Chat.this, cityId);
		param.addUserId(userId);
		HttpStringPost task = new HttpStringPost(Chat.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					Gson gson = new Gson();
					userInfoModel = gson.fromJson(object.getString("user"), UserInfoModel.class);
					if (userInfoModel != null) {
						otherUserName = userInfoModel.getNick_name();
						otherUserIcon = userInfoModel.getHead_img_url();
						status = userInfoModel.getIs_shield();
						tvTitle.setText(otherUserName);
						if (!tvRight.isShown()) {
							tvRight.setVisibility(View.VISIBLE);
						}
						if (status == 0) {
							tvRight.setText("屏蔽");
						} else {
							tvRight.setText("取消屏蔽");
						}
						adapter.setUserIcon(otherUserIcon);
						listView.onClickRefush();
						adapter.getMessageList(userId);
					} else {
						Toast.makeText(Chat.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(Chat.this, "个人信息解析失败", Toast.LENGTH_SHORT).show();
					finish();
				}
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
				finish();
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Toast.makeText(Chat.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
				finish();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Toast.makeText(Chat.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
				finish();
			}
		}, param.getParameters());
		RequestManager.addRequest(task, Chat.this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			if (adapter.getCount() > 0 && isCallBack) {
				intent.putExtra("REFRESH", "");
			}
			if (isStatus) {
				intent.putExtra("STATUS", status);
			}
			setResult(RESULT_OK, intent);
			finish();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				Intent intent = new Intent();
				if (adapter.getCount() > 0 && isCallBack) {
					intent.putExtra("REFRESH", "");
				}
				if (isStatus) {
					intent.putExtra("STATUS", status);
				}
				setResult(RESULT_OK, intent);
				finish();
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (status == 0 && tvRight.getText().toString().equals("屏蔽")) {
					if (!isRequest) {
						isRequest = true;
						ShieldContactUser(userId);
					}
				} else {
					if (!isRequest) {
						isRequest = true;
						CancelShieldContactUser(userId);
					}
				}
			}
			break;
		case R.id.btSend:
			if (!ClickUtil.isFastClick()) {
				if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
					toast("请输入私信内容");
					return;
				}
				if (!isRequest) {
					isRequest = true;
					closeKeyboard();
					sendMessage(etContent.getText().toString().trim());
				}
			}
			break;
		}
	}

	/**
	 * 发送私信
	 */
	private void sendMessage(final String content) {
		SendChatMessageParam param = new SendChatMessageParam(Chat.this, userId, content);
		HttpStringPost task = new HttpStringPost(Chat.this, param.getUrl(), new ResponseListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void success(int code, String msg, String result) {
				isCallBack = true;
				etContent.setText("");
				if (userInfoModel != null && !userInfoModel.getBaidu_user_id().equals("")) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							PushMessage(content, userInfoModel.getLast_login_platform());
						}
					}).start();
				}
				ChatMessageModel model = new ChatMessageModel();
				model.setContent(content);
				model.setRole(1);
				model.setContact_id(userId);
				model.setCreate_time(TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
				adapter.getMsgList().add(model);
				adapter.notifyDataSetChanged();
				listView.setSelection(adapter.getCount() - 1);
				isRequest = false;
			}

			@Override
			public void relogin(String msg) {
				needLogin(msg);
				isRequest = false;
			}

			@Override
			public void error(int code, String msg) {
				isRequest = false;
				toast("发送失败");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				isRequest = false;
				toast("发送失败");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 推送
	 */
	private void PushMessage(String content, int platform) {
		if (baidu_api_key.equals("")) {
			baidu_api_key = getMetaValue(Chat.this, "api_key");
		}
		ChannelKeyPair pair = new ChannelKeyPair(baidu_api_key, Constant.BAIDU_SECRET_KEY);
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		try {
			PushUnicastMessageRequest request = new PushUnicastMessageRequest();
			if (platform == 3) {
				request.setDeviceType(3);
				request.setMessage(contentAndroidJson(content));
			} else if (platform == 4) {
				if (Constant.SHOW_LOG) {
					request.setDeployStatus(1);
				} else {
					request.setDeployStatus(2);
				}
				request.setDeviceType(4);
				request.setMessage(contentIosJson(content));
			}
			request.setChannelId(userInfoModel.getBaidu_channel_id());
			request.setUserId(userInfoModel.getBaidu_user_id());
			request.setMessageType(1);

			PushUnicastMessageResponse response = channelClient.pushUnicastMessage(request);
			System.out.println("push amount : " + response.getSuccessAmount());
		} catch (ChannelClientException e) {
			e.printStackTrace();
		} catch (ChannelServerException e) {
			System.out.println(String.format("request_id: %d, error_code: %d, error_message: %s", e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
		}
	}

	private String contentAndroidJson(String content) {
		try {
			JSONObject object = new JSONObject();
			object.put("title", myUserName);
			object.put("description", content);
			JSONObject json = new JSONObject();
			json.put("filter_k_id", 51);
			json.put("filter_v_id", myUserId);
			object.put("custom_content", json);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String contentIosJson(String content) {
		try {
			JSONObject object = new JSONObject();
			object.put("title", myUserName);
			object.put("description", content);
			JSONObject json = new JSONObject();
			json.put("alert", myUserName+"："+content);
			json.put("sound", "default");
			object.put("aps", json);
			object.put("filter_k_id", 51);
			object.put("filter_v_id", myUserId);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 屏蔽联系人
	 */
	private void ShieldContactUser(int userId) {
		mLoadingDialog.setMessage("正在屏蔽...");
		mLoadingDialog.show();
		ShieldContactUserParam param = new ShieldContactUserParam(Chat.this, userId);
		HttpStringPost task = new HttpStringPost(Chat.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				status = 1;
				isStatus = true;
				tvRight.setText("取消屏蔽");
				isRequest = false;
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
				toast("屏蔽失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				toast("屏蔽失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 取消屏蔽联系人
	 */
	private void CancelShieldContactUser(int userId) {
		mLoadingDialog.setMessage("取消屏蔽中...");
		mLoadingDialog.show();
		CancelShieldContactUserParam param = new CancelShieldContactUserParam(Chat.this, userId);
		HttpStringPost task = new HttpStringPost(Chat.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				status = 0;
				isStatus = true;
				tvRight.setText("屏蔽");
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
				toast("取消屏蔽失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				toast("取消屏蔽失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	private class DelMessageDialog extends Dialog {

		private Context context;
		private String name;
		private int position;
		private String content;
		private int id;

		public DelMessageDialog(Context context, String name, int position, String content, int id) {
			super(context);
			this.context = context;
			this.name = name;
			this.position = position;
			this.content = content;
			this.id = id;
		}

		public DelMessageDialog(Context context, int theme, String name, int position, String content, int id) {
			super(context, theme);
			this.context = context;
			this.name = name;
			this.position = position;
			this.content = content;
			this.id = id;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_del_chat_message);
			WindowManager.LayoutParams params = getWindow().getAttributes();
			params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			params.width = ViewGroup.LayoutParams.MATCH_PARENT;
			getWindow().setAttributes((WindowManager.LayoutParams) params);
			setCanceledOnTouchOutside(true);
			TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
			tvUserName.setText(name);
			TextView tvCopy = (TextView) findViewById(R.id.tvCopy);
			TextView tvDel = (TextView) findViewById(R.id.tvDel);

			tvCopy.setOnClickListener(new View.OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View arg0) {
					if (!ClickUtil.isFastClick()) {
						dismiss();
						if (Build.VERSION.SDK_INT > 11) {
							android.content.ClipboardManager cmb = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							cmb.setText(content);
						} else {
							android.text.ClipboardManager cmb = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							cmb.setText(content);
						}
						toast("复制成功");
					}
				}
			});
			tvDel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!ClickUtil.isFastClick()) {
						dismiss();
						DelMessage(id, position);
					}
				}
			});
		}
	}

	/**
	 * 删除单条私信
	 */
	private void DelMessage(int id, final int position) {
		DelMessageParam param = new DelMessageParam(Chat.this, id);
		HttpStringPost task = new HttpStringPost(Chat.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				adapter.remove(position);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void relogin(String msg) {
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				toast("删除失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				toast("删除失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 关闭软键盘
	 */
	protected void closeKeyboard() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 打开软键盘
	 */
	protected void openKeyboard() {
		delayToDo(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.showSoftInput(getCurrentFocus(), 0);
				}
			}
		}, 100);
	}

	/**
	 * 延迟操作
	 * 
	 * @param task
	 * @param delay
	 */
	protected void delayToDo(TimerTask task, long delay) {
		Timer timer = new Timer();
		timer.schedule(task, delay);
	}

	// 获取ApiKey
	private String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}
}
