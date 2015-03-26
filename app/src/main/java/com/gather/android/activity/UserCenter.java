package com.gather.android.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.adapter.UserCenterListViewAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.NewsModelList;
import com.gather.android.model.UserInfoModel;
import com.gather.android.model.UserPhotoModel;
import com.gather.android.params.AddFocusParam;
import com.gather.android.params.CancelFocusParam;
import com.gather.android.params.GetUserCenterParam;
import com.gather.android.params.UserInterviewParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.BlurBitmapUtil;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.ListViewImagePro;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tendcloud.tenddata.TCAgent;

public class UserCenter extends SwipeBackActivity implements OnClickListener {

	private FrameLayout rlHeader;
	private ImageView ivBackground, ivBack, ivUserIcon, ivVip, ivUserSex;
	private TextView tvFocus, tvUserName, tvFocusNum, tvFansNum, tvInterview, tvTrends, tvAct;
	private FrameLayout flInterview;
	private LinearLayout llFocus, llFans, llChat;
	private ListViewImagePro listViewImagePro;
	private UserCenterListViewAdapter listViewAdapter;
	private View headerView;
	private int userId, first = 0;
	private UserInfoModel userInfoModel = null;
	private boolean isRequest = false, isMe = false;
	private boolean hasLogin;
	private GatherApplication application;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DialogTipsBuilder dialog;
	private DialogChoiceBuilder choiceBuilder;
	private LoadingDialog mLoadingDialog;
	private DisplayMetrics metrics;
	private Bitmap blurBitmap;
	private NewsModelList interviewList;

	@Override
	protected int layoutResId() {
		return R.layout.user_center;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("UID")) {
			this.userId = intent.getExtras().getInt("UID");
			if (userId == AppPreference.getUserPersistentInt(UserCenter.this, AppPreference.USER_ID)) {
				isMe = true;
			} else {
				isMe = false;
			}
			if (intent.hasExtra("MODEL")) {
				userInfoModel = (UserInfoModel) intent.getSerializableExtra("MODEL");
			}
			this.mLoadingDialog = LoadingDialog.createDialog(UserCenter.this, true);
			this.dialog = DialogTipsBuilder.getInstance(UserCenter.this);
			this.choiceBuilder = DialogChoiceBuilder.getInstance(UserCenter.this);
			this.hasLogin = AppPreference.hasLogin(UserCenter.this);
			this.application = (GatherApplication) getApplication();
			this.listViewImagePro = (ListViewImagePro) findViewById(R.id.listview);

			this.headerView = LayoutInflater.from(UserCenter.this).inflate(R.layout.item_user_center_header, null);
			this.rlHeader = (FrameLayout) headerView.findViewById(R.id.rlHeader);
			this.ivBackground = (ImageView) headerView.findViewById(R.id.ivBackground);
			this.ivUserIcon = (ImageView) headerView.findViewById(R.id.ivUserIcon);
			this.ivVip = (ImageView) headerView.findViewById(R.id.ivVip);
			this.ivUserSex = (ImageView) headerView.findViewById(R.id.ivUserSex);
			this.tvUserName = (TextView) headerView.findViewById(R.id.tvUserName);
			this.tvFocusNum = (TextView) headerView.findViewById(R.id.tvFocusNum);
			this.tvFansNum = (TextView) headerView.findViewById(R.id.tvFansNum);
			this.tvInterview = (TextView) headerView.findViewById(R.id.tvInterview);
			this.tvTrends = (TextView) headerView.findViewById(R.id.tvTrends);
			this.tvAct = (TextView) headerView.findViewById(R.id.tvAct);
			this.flInterview = (FrameLayout) headerView.findViewById(R.id.FlInterview);
			this.llFocus = (LinearLayout) headerView.findViewById(R.id.llFocus);
			this.llFans = (LinearLayout) headerView.findViewById(R.id.llFans);

			metrics = getResources().getDisplayMetrics();
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.rlHeader.getLayoutParams();
			params.width = metrics.widthPixels;
			params.height = metrics.widthPixels * 48 / 75;
			this.rlHeader.setLayoutParams(params);

			this.listViewImagePro.setHeaderView(headerView, rlHeader);
			this.listViewImagePro.addHeaderView(headerView);
			this.listViewAdapter = new UserCenterListViewAdapter(UserCenter.this, userId, userInfoModel, isMe);
			this.listViewImagePro.setAdapter(listViewAdapter);

			this.llChat = (LinearLayout) findViewById(R.id.llChat);
			this.ivBack = (ImageView) findViewById(R.id.ivBack);
			this.tvFocus = (TextView) findViewById(R.id.tvFocus);

			this.ivBack.setOnClickListener(this);
			this.tvInterview.setOnClickListener(this);
			this.tvTrends.setOnClickListener(this);
			this.tvAct.setOnClickListener(this);
			if (!isMe) {
				this.tvFocus.setOnClickListener(this);
				this.llFocus.setOnClickListener(this);
				this.llFans.setOnClickListener(this);
				this.llChat.setOnClickListener(this);
			}
			this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user_icon).showImageForEmptyUri(R.drawable.default_user_icon).showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(false).displayer(new RoundedBitmapDisplayer(180)).bitmapConfig(Bitmap.Config.RGB_565).build();
			if (application.getCityId() != 0) {
				initView();
				getUserInfo(application.getCityId());
			} else {
				toast("请先选择您所在城市，请重试");
				finish();
			}
		} else {
			toast("查看个人主页信息出错，请重试");
			finish();
		}
	}

	private void initView() {
		if (userInfoModel != null) {
			setUserInfo();
		}
	}

	/**
	 * 获取个人信息
	 */
	private void getUserInfo(int cityId) {
		isRequest = true;
		GetUserCenterParam param = new GetUserCenterParam(UserCenter.this, cityId);
		param.addUserId(userId);
		HttpStringPost task = new HttpStringPost(UserCenter.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					Gson gson = new Gson();
					userInfoModel = gson.fromJson(object.getString("user"), UserInfoModel.class);
					if (userInfoModel != null) {
						listViewAdapter.setUserInfoModel(userInfoModel);
						setUserInfo();
					} else {
						Toast.makeText(UserCenter.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(UserCenter.this, "个人信息解析失败", Toast.LENGTH_SHORT).show();
				}
				isRequest = false;
			}

			@Override
			public void relogin(String msg) {
				isRequest = false;
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				isRequest = false;
				Toast.makeText(UserCenter.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				isRequest = false;
				Toast.makeText(UserCenter.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
			}
		}, param.getParameters());
		RequestManager.addRequest(task, UserCenter.this);
	}

	/**
	 * 设置个人信息
	 */
	private void setUserInfo() {
		first++;
		if (!isMe) {
			llChat.setVisibility(View.VISIBLE);
			tvFocus.setVisibility(View.VISIBLE);
			if (userInfoModel.getIs_focus() == 0) {
				tvFocus.setText("关注");
			} else {
				tvFocus.setText("取消关注");
			}
		} else {
			llChat.setVisibility(View.GONE);
			tvFocus.setVisibility(View.GONE);
		}
		imageLoader.displayImage(userInfoModel.getHead_img_url(), ivUserIcon, options);
		new Thread(new Runnable() {
			@Override
			public void run() {
				blurBitmap = null;
				blurBitmap = BlurBitmapUtil.BoxBlurFilter(imageLoader.loadImageSync(userInfoModel.getHead_img_url()), 6f, 6f, 10);
				mHandler.sendEmptyMessage(0);
			}
		}).start();
		flInterview.setVisibility(View.GONE);
		if (userInfoModel.getIs_vip() == 0) {
			ivVip.setVisibility(View.GONE);
		} else {
			if (first > 1) {
				getInterviewList();
			}
			ivVip.setVisibility(View.VISIBLE);
		}
		tvUserName.setText(userInfoModel.getNick_name());
		if (userInfoModel.getSex() == 1) {
			ivUserSex.setImageResource(R.drawable.icon_user_center_user_sex_male);
		} else {
			ivUserSex.setImageResource(R.drawable.icon_user_center_user_sex_female);
		}
		tvFocusNum.setText(userInfoModel.getFocus_num() + "");
		tvFansNum.setText(userInfoModel.getFans_num() + "");
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				ivBackground.setImageBitmap(blurBitmap);
				break;
			}
		}

	};

	/**
	 * 取消关注
	 */
	private void CancelFocus() {
		mLoadingDialog.setMessage("取消关注...");
		mLoadingDialog.show();
		isRequest = true;
		CancelFocusParam param = new CancelFocusParam(UserCenter.this, userId);
		HttpStringPost task = new HttpStringPost(UserCenter.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				userInfoModel.setIs_focus(0);
				tvFocus.setText("关注");
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
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("取消关注失败, 请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				isRequest = false;
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("取消关注失败, 请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 加关注
	 */
	private void AddFocus() {
		isRequest = true;
		mLoadingDialog.setMessage("关注中...");
		mLoadingDialog.show();
		AddFocusParam param = new AddFocusParam(UserCenter.this, userId);
		HttpStringPost task = new HttpStringPost(UserCenter.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				TCAgent.onEvent(UserCenter.this, "关注人");
				userInfoModel.setIs_focus(1);
				tvFocus.setText("取消关注");
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
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("关注失败, 请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("关注失败, 请重试").withEffect(Effectstype.Shake).show();
				}
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 专访列表
	 */
	private void getInterviewList() {
		UserInterviewParam param = new UserInterviewParam(UserCenter.this, userId, 1, 1);
		HttpStringPost task = new HttpStringPost(UserCenter.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				Gson gson = new Gson();
				interviewList = gson.fromJson(result, NewsModelList.class);
				listViewAdapter.setInterview(interviewList);
//				if (interviewList != null && interviewList.getNews() != null && interviewList.getNews().size() > 0) {
//					flInterview.setVisibility(View.VISIBLE);
//				} else {
//					flInterview.setVisibility(View.GONE);
//				}
			}

			@Override
			public void relogin(String msg) {
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				flInterview.setVisibility(View.GONE);
				toast("专访获取失败");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				flInterview.setVisibility(View.GONE);
				toast("专访获取失败");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.tvFocus:
			if (!ClickUtil.isFastClick()) {
				if (hasLogin && userInfoModel != null) {
					if (!isRequest) {
						if (userInfoModel.getIs_focus() == 0) {
							AddFocus();
						} else {
							CancelFocus();
						}
					}
				} else {
					DialogLogin();
				}
			}
			break;
		case R.id.llFocus:
		case R.id.llFans:
			if (!ClickUtil.isFastClick() && !isMe) {
				Intent mIntent = new Intent(UserCenter.this, FriendsList.class);
				mIntent.putExtra("UID", userId);
				startActivity(mIntent);
			}
			break;
		case R.id.tvInterview:
			if (!ClickUtil.isFastClick() && interviewList != null && interviewList.getNews() != null) {
				Intent intent = new Intent(UserCenter.this, WebStrategy.class);
				intent.putExtra("MODEL", interviewList.getNews().get(0));
				startActivity(intent);
			}
			break;
		case R.id.tvTrends:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				Intent intent = new Intent(UserCenter.this, UserTrends.class);
				intent.putExtra("MODEL", userInfoModel);
				startActivity(intent);
			}
			break;
		case R.id.tvAct:
			if (!ClickUtil.isFastClick() && userInfoModel != null) {
				Intent intent = new Intent(UserCenter.this, ActRelationList.class);
				intent.putExtra("UID", userInfoModel.getUid());
				startActivity(intent);
			}
			break;
		case R.id.llChat:
			if (!ClickUtil.isFastClick()) {
				if (hasLogin && userInfoModel != null) {
					if (!isMe) {
						Intent chat = new Intent(UserCenter.this, Chat.class);
						chat.putExtra("UID", userId);
						chat.putExtra("MODEL", userInfoModel);
						startActivityForResult(chat, 101);
					}
				} else {
					DialogLogin();
				}
			}
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 100:
				if (data != null && data.hasExtra("POSITION")) {
					if (data.hasExtra("LIST")) {
						ArrayList<UserPhotoModel> picList = (ArrayList<UserPhotoModel>) data.getSerializableExtra("LIST");
						listViewAdapter.getPicAdapter().setNotifyChanged(picList);
					}
					listViewAdapter.getPicListView().scrollTo(((metrics.widthPixels - 40) / 3) * data.getExtras().getInt("POSITION"));
				}
				break;
			case 101:
				if (data != null && data.hasExtra("STATUS")) {
					userInfoModel.setIs_shield(data.getExtras().getInt("STATUS"));
				}
				break;
			}
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
					if (PushManager.isPushEnabled(getApplicationContext())) {
						PushManager.stopWork(getApplicationContext());
					}
					application.setUserInfoModel(null);
					AppPreference.clearInfo(UserCenter.this);
					Intent intent = new Intent(UserCenter.this, LoginIndex.class);
					startActivity(intent);
					choiceBuilder.dismiss();
				}
			}).show();
		}
	}

}
