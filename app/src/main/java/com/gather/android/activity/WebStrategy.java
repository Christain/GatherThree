package com.gather.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogShareAct;
import com.gather.android.dialog.DialogShareAct.ShareClickListener;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.NewsModel;
import com.gather.android.params.CancelCollectNewsParam;
import com.gather.android.params.CollectNewsParam;
import com.gather.android.params.CreateOrderParam;
import com.gather.android.params.NewsDetailParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tendcloud.tenddata.TCAgent;

/**
 * 网页（攻略）
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebStrategy extends SwipeBackActivity implements OnClickListener {

	private TextView tvTitle, tvCollection, tvShare;
	private ImageView ivBack;

	private LinearLayout llMenuBar, llBuy;

	private LoadingDialog mLoadingDialog;
	private DialogChoiceBuilder choiceBuilder;
	private DialogTipsBuilder dialog;
	private boolean isRequest = false;
	private boolean hasLogin = false;

	private ProgressBar progressBar;
	private WebView webView;
	private NewsModel model = null;
	private int id = 0;

	/********** 第三方绑定 *************/
	private Tencent mTencent;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;
	private IWXAPI wxApi;
	private String Share_Message = "集合啦！脱宅利器必备神器-你想要的活动，这里都有！";

	@Override
	protected int layoutResId() {
		return R.layout.web_strategy;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		if ((intent.hasExtra("MODEL")) || intent.hasExtra("ID")) {
			if (intent.hasExtra("MODEL")) {
				this.model = (NewsModel) intent.getSerializableExtra("MODEL");
			} else {
				this.id = intent.getExtras().getInt("ID");
			}
			this.hasLogin = AppPreference.hasLogin(WebStrategy.this);
			this.mLoadingDialog = LoadingDialog.createDialog(WebStrategy.this, false);
			this.choiceBuilder = DialogChoiceBuilder.getInstance(WebStrategy.this);
			this.dialog = DialogTipsBuilder.getInstance(WebStrategy.this);
			this.tvCollection = (TextView) findViewById(R.id.tvCollection);
			this.tvShare = (TextView) findViewById(R.id.tvShare);
			this.ivBack = (ImageView) findViewById(R.id.ivBack);

			this.llMenuBar = (LinearLayout) findViewById(R.id.llMenuBar);
			this.llBuy = (LinearLayout) findViewById(R.id.llBuy);

			this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
			this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);
			this.progressBar = (ProgressBar) findViewById(R.id.pbWebView);
			this.webView = (WebView) findViewById(R.id.webView);
			progressBar.setMax(100);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setSupportZoom(false);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			webView.getSettings().setRenderPriority(RenderPriority.HIGH);
			webView.setInitialScale(39);

			webView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					if (newProgress == 100) {
						progressBar.setProgress(100);
						progressBar.setVisibility(View.GONE);
					} else {
						progressBar.setProgress(newProgress);
					}
					super.onProgressChanged(view, newProgress);
				}
			});
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					progressBar.setProgress(10);
					view.loadUrl(url);
					return true;
				}
			});

			this.ivBack.setOnClickListener(this);
			this.tvCollection.setOnClickListener(this);
			this.tvShare.setOnClickListener(this);

			this.initView();
		} else {
			toast("页面信息有误");
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		if (webView != null) {
			webView.destroy();
		}
		super.onDestroy();
	}

	private void initView() {
		if (model != null) {
			switch (model.getType_id()) {
			case 1:
				tvTitle.setText("攻略详情");
				break;
			case 2:
				tvTitle.setText("记忆详情");
				break;
			case 3:
				tvTitle.setText("订购详情");
				break;
			case 4:
				tvTitle.setText("专访详情");
				break;
			}
			if (tvTitle.getText().toString().contains("订")) {
				this.llMenuBar.setVisibility(View.VISIBLE);
				this.llBuy.setOnClickListener(this);
			} else {
				this.llMenuBar.setVisibility(View.GONE);
			}
			if (tvTitle.getText().toString().contains("专访")) {
				tvCollection.setVisibility(View.GONE);
			} else {
				tvCollection.setVisibility(View.VISIBLE);
				if (model != null) {
					if (model.getIs_loved() == 0) {
						tvCollection.setText("收藏");
					} else {
						tvCollection.setText("取消收藏");
					}
				}
			}
			webView.loadUrl(model.getDetail_url());
		} else {
			getNewsDetail();
		}
	}

	/**
	 * 获取资讯详情
	 */
	private void getNewsDetail() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("加载中...");
			mLoadingDialog.show();
		}
		NewsDetailParam param = new NewsDetailParam(WebStrategy.this, id);
		HttpStringPost task = new HttpStringPost(WebStrategy.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Gson gson = new Gson();
				try {
					JSONObject object = new JSONObject(result);
					NewsModel news = gson.fromJson(object.getString("news"), NewsModel.class);
					if (news != null) {
						model = news;
						initView();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					toast("数据解析失败");
					finish();
				}
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("获取失败");
				finish();
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("获取失败");
				finish();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("获取失败");
				finish();
			}
		}, param.getParameters());
		executeRequest(task);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			if (!ClickUtil.isFastClick()) {
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					finish();
				}
			}
			break;
		case R.id.llBuy:
			if (!ClickUtil.isFastClick() && model != null && model.getType_id() == 3) {
				if (hasLogin) {
					CreateOrder(model.getPrice(), model.getTitle(), model.getIntro());
				} else {
					DialogLogin();
				}
			}
			break;
		case R.id.tvCollection:
			if (!ClickUtil.isFastClick() && model != null) {
				if (hasLogin) {
					if (!isRequest) {
						isRequest = true;
						if (model.getIs_loved() == 0) {
							CollectNews();
						} else {
							CancelCollectNews();
						}
					}
				} else {
					DialogLogin();
				}
			}
			break;
		case R.id.tvShare:
			if (!ClickUtil.isFastClick() && model != null) {
				DialogShareAct shareDialog = new DialogShareAct(WebStrategy.this, R.style.ShareDialog).setOnShareClickListener(new ShareClickListener() {
					@Override
					public void OnShareClickListener(int position) {
						switch (position) {
						case DialogShareAct.WECHAT:
							ShareToWeChat();
							break;
						case DialogShareAct.SQUARE:
							ShareToSquare();
							break;
						case DialogShareAct.ZOON:
							ShareToZone();
							break;
						case DialogShareAct.SINA:
							if (!AppPreference.getUserPersistent(WebStrategy.this, AppPreference.SINA_ID).equals("")) {
								Oauth2AccessToken token = new Oauth2AccessToken();
								token.setUid(AppPreference.getUserPersistent(WebStrategy.this, AppPreference.SINA_ID));
								token.setToken(AppPreference.getUserPersistent(WebStrategy.this, AppPreference.SINA_TOKEN));
								token.setExpiresTime(AppPreference.getUserPersistentLong(WebStrategy.this, AppPreference.SINA_EXPIRES));
								if (!token.isSessionValid()) {
									if (checkSinaPackage()) {
										mSsoHandler = new SsoHandler(WebStrategy.this, mWeiboAuth);
										mSsoHandler.authorize(new AuthListener());
									} else {
										mWeiboAuth.anthorize(new AuthListener());
									}
								} else {
									ShareToSina();
								}
							} else {
								if (checkSinaPackage()) {
									mSsoHandler = new SsoHandler(WebStrategy.this, mWeiboAuth);
									mSsoHandler.authorize(new AuthListener());
								} else {
									mWeiboAuth.anthorize(new AuthListener());
								}
							}
							break;
						}
					}
				});
				shareDialog.show();
			}
			break;
		}
	}

	/**
	 * 收藏
	 */
	private void CollectNews() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("收藏中");
			mLoadingDialog.show();
		}
		CollectNewsParam param = new CollectNewsParam(WebStrategy.this, model.getId());
		HttpStringPost task = new HttpStringPost(WebStrategy.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				model.setIs_loved(1);
				tvCollection.setText("取消收藏");
				toast("收藏成功");
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
				toast("收藏失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				toast("收藏失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 取消收藏
	 */
	private void CancelCollectNews() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("取消收藏中...");
			mLoadingDialog.show();
		}
		CancelCollectNewsParam param = new CancelCollectNewsParam(WebStrategy.this, model.getId());
		HttpStringPost task = new HttpStringPost(WebStrategy.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				model.setIs_loved(0);
				tvCollection.setText("收藏");
				toast("取消成功");
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
				toast("取消收藏失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isRequest = false;
				toast("取消收藏失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 生成订单
	 */
	private void CreateOrder(final double price, final String title, final String intro) {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("生成订单中...");
			mLoadingDialog.show();
		}
		CreateOrderParam param = new CreateOrderParam(WebStrategy.this, price, title, intro);
		HttpStringPost task = new HttpStringPost(WebStrategy.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					String num = object.getString("trade_no");
					Intent intent = new Intent(WebStrategy.this, PayView.class);
					intent.putExtra("NAME", title);
					intent.putExtra("NUM", num);
					intent.putExtra("DETAIL", intro);
					intent.putExtra("PRICE", price);
					startActivity(intent);
				} catch (JSONException e) {
					toast("订单解析失败，请重试");
					e.printStackTrace();
				}
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("生成订单失败，请重试");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("生成订单失败，请重试");
			}
		}, param.getParameters());
		executeRequest(task);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!ClickUtil.isFastClick()) {
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					finish();
				}
			}
		}
		return true;
	}

	/**
	 * 绑定新浪微博
	 */
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (Constant.SHOW_LOG) {
				Log.e("aaaaaaaaaaa", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
			}
			AppPreference.save(WebStrategy.this, AppPreference.SINA_ID, mAccessToken.getUid());
			AppPreference.save(WebStrategy.this, AppPreference.SINA_TOKEN, mAccessToken.getToken());
			AppPreference.save(WebStrategy.this, AppPreference.SINA_EXPIRES, mAccessToken.getExpiresTime());
			ShareToSina();
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onWeiboException(WeiboException e) {

		}
	}

	/**
	 * 分享到微信好友
	 */
	private void ShareToWeChat() {
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID);
		wxApi.registerApp(Constant.WE_CHAT_APPID);
		if (wxApi.isWXAppInstalled() && wxApi.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = model.getDetail_url();
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = model.getTitle();
			msg.description = Share_Message;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.setThumbImage(thumb);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			wxApi.sendReq(req);
		} else {
			if (dialog != null && !dialog.isShowing()) {
				dialog.setMessage("请下载安装新版本微信客户端").withEffect(Effectstype.Shake).show();
			}
		}
	}

	/**
	 * 分享到微信朋友圈
	 */
	private void ShareToSquare() {
		wxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID);
		wxApi.registerApp(Constant.WE_CHAT_APPID);
		if (wxApi.isWXAppInstalled() && wxApi.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = model.getDetail_url();
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = model.getTitle();
			msg.description = Share_Message;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.setThumbImage(thumb);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			wxApi.sendReq(req);
		} else {
			if (dialog != null && !dialog.isShowing()) {
				dialog.setMessage("请下载安装新版本微信客户端").withEffect(Effectstype.Shake).show();
			}
		}
	}

	/**
	 * 分享到QQ空间
	 */
	private void ShareToZone() {
		if (checkQQPackage()) {
			final Bundle params = new Bundle();
			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, Share_Message);// 必填
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, model.getDetail_url());// 必填
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, model.getTitle());// 选填
			ArrayList<String> imageUrls = new ArrayList<String>();
			imageUrls.add(model.getH_img_url());
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
			params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
			new Thread(new Runnable() {
				@Override
				public void run() {
					mTencent.shareToQzone(WebStrategy.this, params, new IUiListener() {
						@Override
						public void onError(UiError arg0) {
							if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
								mLoadingDialog.dismiss();
							}
							handler.sendEmptyMessage(0);
						}

						@Override
						public void onComplete(Object arg0) {
							if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
								mLoadingDialog.dismiss();
							}
							handler.sendEmptyMessage(1);
						}

						@Override
						public void onCancel() {
							if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
								mLoadingDialog.dismiss();
							}
						}
					});
				}
			}).start();
		} else {
			if (dialog != null && !dialog.isShowing()) {
				dialog.setMessage("请下载安装新版本手机QQ或者QQ空间").withEffect(Effectstype.Shake).show();
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				toast("QQ空间分享失败，请重试");
				break;
			case 1:
				TCAgent.onEvent(WebStrategy.this, "分享到QQ空间");
				toast("QQ空间分享成功");
				break;
			}
		}
	};

	/**
	 * 分享到新浪微博
	 */
	private void ShareToSina() {
		mLoadingDialog.setMessage("分享到新浪微博...");
		mLoadingDialog.show();
		StringRequest task = new StringRequest(Method.POST, "https://api.weibo.com/2/statuses/upload_url_text.json", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				TCAgent.onEvent(WebStrategy.this, "分享到新浪微博");
				toast("分享成功");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast(error.getMessage());
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("access_token", AppPreference.getUserPersistent(WebStrategy.this, AppPreference.SINA_TOKEN));
				params.put("status", "#" + model.getTitle() + "#" + Share_Message  + model.getDetail_url());
				params.put("url", model.getH_img_url());
				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				return params;
			}
		};
		executeRequest(task);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 检测新浪微博是否安装
	 * 
	 */
	private boolean checkSinaPackage() {
		PackageManager manager = getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.sina.weibo")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测QQ是否安装
	 * 
	 */
	private boolean checkQQPackage() {
		PackageManager manager = getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.tencent.mobileqq")) {
				return true;
			}
		}
		return false;
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
					GatherApplication application = (GatherApplication) getApplication();
					application.setUserInfoModel(null);
					AppPreference.clearInfo(WebStrategy.this);
					Intent intent = new Intent(WebStrategy.this, LoginIndex.class);
					startActivity(intent);
					choiceBuilder.dismiss();
				}
			}).show();
		}
	}
}
