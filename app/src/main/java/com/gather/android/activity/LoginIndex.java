package com.gather.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.params.LoginThirdParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoginIndex extends BaseActivity implements OnClickListener {

	private LinearLayout llPhone, llWechat, llSina, llTencent;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;
	private IWXAPI wxApi;
	private ImageView ivFinish;

	private Tencent mTencent;
	private IUiListener listener;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;
	private boolean no_exit = false;// 不退出应用，返回没有登录的主页

	@Override
	protected int layoutResId() {
		return R.layout.login_index;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("NO_EXIT")) {
			no_exit = true;
		} else {
			no_exit = false;
		}
		this.dialog = DialogTipsBuilder.getInstance(LoginIndex.this);
		this.mLoadingDialog = LoadingDialog.createDialog(LoginIndex.this, true);
		this.llPhone = (LinearLayout) findViewById(R.id.llPhone);
		this.llWechat = (LinearLayout) findViewById(R.id.llWechat);
		this.llSina = (LinearLayout) findViewById(R.id.llSina);
		this.llTencent = (LinearLayout) findViewById(R.id.llTencent);
		this.ivFinish = (ImageView) findViewById(R.id.ivFinish);

		this.llPhone.setOnClickListener(this);
		this.llWechat.setOnClickListener(this);
		this.llSina.setOnClickListener(this);
		this.llTencent.setOnClickListener(this);
		this.ivFinish.setOnClickListener(this);

		this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
		this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.llPhone:
			if (!ClickUtil.isFastClick()) {
				intent = new Intent(LoginIndex.this, LoginPhone.class);
				startActivity(intent);
			}
			break;
		case R.id.llTencent:
			if (!ClickUtil.isFastClick()) {
				if (!AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_ID).equals("")) {
					mTencent.setOpenId(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_ID));
					mTencent.setAccessToken(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.QQ_TOKEN), String.valueOf(AppPreference.getUserPersistentLong(LoginIndex.this, AppPreference.QQ_EXPIRES)));
				}
				listener = new IUiListener() {
					@Override
					public void onCancel() {

					}

					@Override
					public void onComplete(Object arg0) {
						try {
							JSONObject object = (JSONObject) arg0;
							String openid = object.getString("openid");
							String token = object.getString("access_token");
							String expires_in = object.getString("expires_in");
							AppPreference.saveThirdLoginInfo(LoginIndex.this, AppPreference.TYPE_QQ, openid, token, Long.parseLong(expires_in));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (Constant.SHOW_LOG) {
							Log.e("aaaaaaaaaaaa", arg0.toString());
						}
						TencentLogin();
					}

					@Override
					public void onError(UiError arg0) {
						toast(arg0.toString());
					}
				};
				mTencent.login(LoginIndex.this, "get_simple_userinfo", listener);
			}
			break;
		case R.id.llSina:
			if (!ClickUtil.isFastClick()) {
				Oauth2AccessToken token = new Oauth2AccessToken();
				if (!AppPreference.getUserPersistent(LoginIndex.this, AppPreference.SINA_ID).equals("")) {
					token.setUid(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.SINA_ID));
					token.setToken(AppPreference.getUserPersistent(LoginIndex.this, AppPreference.SINA_TOKEN));
					token.setExpiresTime(AppPreference.getUserPersistentLong(LoginIndex.this, AppPreference.SINA_EXPIRES));
				}
				if (checkSinaPackage()) {
					mSsoHandler = new SsoHandler(LoginIndex.this, mWeiboAuth);
					mSsoHandler.authorize(new AuthListener());
				} else {
					mWeiboAuth.anthorize(new AuthListener());
				}
			}
			break;
		case R.id.llWechat:
			if (!ClickUtil.isFastClick()) {
				wxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID);
				wxApi.registerApp(Constant.WE_CHAT_APPID);
				if (wxApi.isWXAppInstalled() && wxApi.isWXAppSupportAPI()) {
					Intent mIntent = new Intent(LoginIndex.this, WXEntryActivity.class);
					mIntent.putExtra("TYPE", "LOGIN");
					startActivity(mIntent);
				} else {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请下载安装新版本微信客户端").withEffect(Effectstype.Shake).show();
					}
				}
			}
			break;
		case R.id.ivFinish:
			if (!ClickUtil.isFastClick()) {
				if (!no_exit) {
					exitApp();
				} else {
					finish();
				}
			}
			break;
		}
	}

	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (Constant.SHOW_LOG) {
				Log.e("aaaaaaaaaaa", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
			}
			AppPreference.saveThirdLoginInfo(LoginIndex.this, AppPreference.TYPE_SINA, mAccessToken.getUid(), mAccessToken.getToken(), mAccessToken.getExpiresTime());
			SinaLogin();
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onWeiboException(WeiboException e) {

		}
	}

	/**
	 * 新浪微博登录
	 */
	private void SinaLogin() {
		mLoadingDialog.setMessage("正在登录...");
		mLoadingDialog.show();
		LoginThirdParam params = new LoginThirdParam(LoginIndex.this, 3, mAccessToken.getUid(), mAccessToken.getToken(), mAccessToken.getExpiresTime());
		HttpStringPost post = new HttpStringPost(LoginIndex.this, params.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(LoginIndex.this, RegisterData.class);
						intent.putExtra("TYPE", AppPreference.TYPE_SINA);
						startActivity(intent);
					} else {
						AppPreference.save(LoginIndex.this, AppPreference.LOGIN_TYPE, AppPreference.TYPE_SINA);
						Intent intent = new Intent(LoginIndex.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finishActivity();
					}
					PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(LoginIndex.this, "api_key"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
				}
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
				}
			}
		}, params.getParameters(), true);
		executeRequest(post);
	}

	/**
	 * QQ登录
	 */
	private void TencentLogin() {
		mLoadingDialog.setMessage("正在登录...");
		mLoadingDialog.show();
		LoginThirdParam params = new LoginThirdParam(LoginIndex.this, 4, mTencent.getOpenId(), mTencent.getAccessToken(), mTencent.getExpiresIn());
		HttpStringPost post = new HttpStringPost(LoginIndex.this, params.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(LoginIndex.this, RegisterData.class);
						intent.putExtra("TYPE", AppPreference.TYPE_QQ);
						startActivity(intent);
					} else {
						AppPreference.save(LoginIndex.this, AppPreference.LOGIN_TYPE, AppPreference.TYPE_QQ);
						Intent intent = new Intent(LoginIndex.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finishActivity();
					}
					PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(LoginIndex.this, "api_key"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
				}
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				needLogin(msg);
			}

		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
				}
			}
		}, params.getParameters());
		executeRequest(post);
	}

	/**
	 * 微信登录
	 */
	private void WechatLogin(String openId, String access_token, long expires_in) {
		mLoadingDialog.setMessage("正在微信登录...");
		mLoadingDialog.show();
		LoginThirdParam params = new LoginThirdParam(LoginIndex.this, 5, openId, access_token, expires_in);
		HttpStringPost post = new HttpStringPost(LoginIndex.this, params.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(LoginIndex.this, RegisterData.class);
						intent.putExtra("TYPE", AppPreference.TYPE_WECHAT);
						startActivity(intent);
					} else {
						AppPreference.save(LoginIndex.this, AppPreference.LOGIN_TYPE, AppPreference.TYPE_WECHAT);
						Intent intent = new Intent(LoginIndex.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finishActivity();
					}
					PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(LoginIndex.this, "api_key"));
				} catch (JSONException e) {
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
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(error.getMsg()).withEffect(Effectstype.Shake).show();
				}
			}
		}, params.getParameters());
		executeRequest(post);
	}

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
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

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (null != intent && intent.hasExtra("CODE")) {
			getWeChatAccessToken(intent.getStringExtra("CODE"));
		}
		// setIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				mTencent.handleLoginData(data, listener);
			}
		}
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/***
	 * 获取微信access_token
	 */
	private void getWeChatAccessToken(String code) {
		StringRequest task = new StringRequest(Method.GET, getAccessToken_Url(code), new Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.e("wechat", response);
				try {
					JSONObject object = new JSONObject(response);
					if (object.has("access_token")) {
						WechatLogin(object.getString("openid"), object.getString("access_token"), object.getLong("expires_in"));
					} else {
						if (object.has("errcode")) {
							toast(object.getString("errmsg"));
						}
					}
				} catch (JSONException e) {
					toast("微信数据解析失败");
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage("微信登录失败，请重试").withEffect(Effectstype.Shake).show();
				}
			}
		});
		executeRequest(task);
	}

	private String getAccessToken_Url(String code) {
		StringBuffer sb = new StringBuffer();
		sb.append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=");
		sb.append(Constant.WE_CHAT_APPID);
		sb.append("&secret=07b59c13cb5447cb8efb9d8d6ff785e4");
		sb.append("&code=");
		sb.append(code);
		sb.append("&grant_type=authorization_code");
		return sb.toString();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!no_exit) {
				exitApp();
			} else {
				finish();
			}
		}
		return true;
	}
}
