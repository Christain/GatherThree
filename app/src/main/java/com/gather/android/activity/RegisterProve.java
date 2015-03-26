package com.gather.android.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.params.FindPasswordGetNumParam;
import com.gather.android.params.LoginThirdParam;
import com.gather.android.params.RegisterPhoneGetNumParam;
import com.gather.android.params.RegisterPhoneIdentifyNumParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class RegisterProve extends SwipeBackActivity implements OnClickListener {

	private TextView tvNext, tvPhoneNum, tvTime, tvResend;
	private EditText etProveNum;
	private LinearLayout llThird, llResend, llTencent, llSina;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	private TimerTask timerTask;
	private Timer timer = new Timer();
	private int second = 60;
	private Animation anim;
	private int type; // 1是注册，2是找回密码
	private String phoneNum;
	
	private Tencent mTencent;
	private IUiListener listener;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;

	@Override
	protected int layoutResId() {
		return R.layout.register_prove;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("TYPE")) {
			type = intent.getExtras().getInt("TYPE");
			phoneNum = intent.getStringExtra("PHONE");
			this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
			this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);
			this.dialog = DialogTipsBuilder.getInstance(RegisterProve.this);
			this.mLoadingDialog = LoadingDialog.createDialog(RegisterProve.this, true);
			this.tvNext = (TextView) findViewById(R.id.tvNext);
			this.tvPhoneNum = (TextView) findViewById(R.id.tvNum);
			this.tvPhoneNum.setText("\"" + phoneNum + "\"");
			this.etProveNum = (EditText) findViewById(R.id.etProveNum);
			this.tvPhoneNum = (TextView) findViewById(R.id.tvNum);
			this.tvTime = (TextView) findViewById(R.id.tvTime);
			this.tvTime.setVisibility(View.VISIBLE);
			this.tvResend = (TextView) findViewById(R.id.tvResend);
			this.llTencent = (LinearLayout) findViewById(R.id.llTencent);
			this.llSina = (LinearLayout) findViewById(R.id.llSina);
			this.llThird = (LinearLayout) findViewById(R.id.llThird);
			this.llThird.setVisibility(View.GONE);
			this.llResend = (LinearLayout) findViewById(R.id.llResend);
			this.llResend.setBackgroundColor(0x00000000);

			this.anim = AnimationUtils.loadAnimation(RegisterProve.this, R.anim.alpha_in);
			this.tvNext.setOnClickListener(this);
			this.llResend.setOnClickListener(this);
			this.llTencent.setOnClickListener(this);
			this.llSina.setOnClickListener(this);

			this.timerTask = new TimerTask() {
				@Override
				public void run() {
					handler.sendEmptyMessage(0);
				}
			};
			timer.schedule(timerTask, 200, 1000);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (second == 1) {
				timerTask.cancel();
				tvTime.setVisibility(View.GONE);
				tvResend.setTextColor(0xffffffff);
				llResend.setBackgroundColor(0x00000000);
				if (!llThird.isShown() && type == 1) {
					llThird.startAnimation(anim);
					llThird.setVisibility(View.VISIBLE);
				}
			} else {
				second = second - 1;
				tvTime.setText(second + "s ");
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llResend:
			if (!tvTime.isShown() && !ClickUtil.isFastClick()) {
				if (type == 1) {
					getIdentifyNum(phoneNum.replace(" ", ""));
				} else {
					getFindPasswordIdentifyNum(phoneNum.replace(" ", ""));
				}
			}
			break;
		case R.id.llTencent:
			if (!ClickUtil.isFastClick()) {
				if (!AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_ID).equals("")) {
					mTencent.setOpenId(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_ID));
					mTencent.setAccessToken(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.QQ_TOKEN), String.valueOf(AppPreference.getUserPersistentLong(RegisterProve.this, AppPreference.QQ_EXPIRES)));
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
							AppPreference.saveThirdLoginInfo(RegisterProve.this, AppPreference.TYPE_QQ, openid, token, Long.parseLong(expires_in));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						TencentLogin();
					}

					@Override
					public void onError(UiError arg0) {
						toast(arg0.toString());
					}
				};
				mTencent.login(RegisterProve.this, "get_simple_userinfo", listener);
			}
			break;
		case R.id.llSina:
			if (!ClickUtil.isFastClick()) {
				Oauth2AccessToken token = new Oauth2AccessToken();
				if (!AppPreference.getUserPersistent(RegisterProve.this, AppPreference.SINA_ID).equals("")) {
					token.setUid(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.SINA_ID));
					token.setToken(AppPreference.getUserPersistent(RegisterProve.this, AppPreference.SINA_TOKEN));
					token.setExpiresTime(AppPreference.getUserPersistentLong(RegisterProve.this, AppPreference.SINA_EXPIRES));
				}
				if (checkSinaPackage()) {
					mSsoHandler = new SsoHandler(RegisterProve.this, mWeiboAuth);
					mSsoHandler.authorize(new AuthListener());
				} else {
					mWeiboAuth.anthorize(new AuthListener());
				}
			}
			break;
		case R.id.tvNext:
			if (!ClickUtil.isFastClick()) {
				if (TextUtils.isEmpty(etProveNum.getText().toString().trim())) {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入验证码").withEffect(Effectstype.Shake).show();
					}
					return;
				} else {
					IdentifyNum();
				}
			}
			break;
		}
	}

	/**
	 * 验证验证码,Ps：注册和找回密码的验证接口一样
	 */
	private void IdentifyNum() {
		mLoadingDialog.setMessage("正在验证...");
		mLoadingDialog.show();
		RegisterPhoneIdentifyNumParam param = new RegisterPhoneIdentifyNumParam(RegisterProve.this, type, phoneNum.replace(" ", ""), etProveNum.getText().toString().trim());
		HttpStringPost task = new HttpStringPost(RegisterProve.this, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (type == 1) {
					Intent intent = new Intent(RegisterProve.this, RegisterData.class);
					intent.putExtra("TYPE", AppPreference.TYPE_SELF);
					startActivity(intent);
					finishActivity();
				} else {
					Intent mIntent = new Intent(RegisterProve.this, ResetPassword.class);
					startActivity(mIntent);
					finishActivity();
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
				switch (code) {
				case 7:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("您的验证码已超时，请重新发送。").withEffect(Effectstype.Shake).show();
					}
					break;
				case 8:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("您输入的验证码不正确，请重新输入。").withEffect(Effectstype.Shake).show();
					}
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("验证失败").withEffect(Effectstype.Shake).show();
					}
					break;
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
		}, param.getParameters());
		executeRequest(task);
	}
	
	/**
	 * 注册时重新获取验证码
	 * @param content
	 */
	private void getIdentifyNum(String content) {
		mLoadingDialog.setMessage("正在获取...");
		mLoadingDialog.show();
		RegisterPhoneGetNumParam param = new RegisterPhoneGetNumParam(RegisterProve.this, content.replace(" ", ""));
		HttpStringPost task = new HttpStringPost(RegisterProve.this, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				second = 60;
				tvTime.setVisibility(View.VISIBLE);
				tvResend.setTextColor(0xffFFFFFF);
				llResend.setBackgroundColor(0x00000000);
				timerTask = new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(0);
					}
				};
				timer.schedule(timerTask, 200, 1000);
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
				switch (code) {
				case 10:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("您输入的手机号已经注册过，请直接登录。").withEffect(Effectstype.Shake).show();
					}
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("验证失败").withEffect(Effectstype.Shake).show();
					}
					break;
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
		}, param.getParameters());
		executeRequest(task);
	}
	
	/**
	 * 找回密码时获取验证码
	 *
	 */
	private void getFindPasswordIdentifyNum(String content) {
		mLoadingDialog.setMessage("正在获取...");
		mLoadingDialog.show();
		FindPasswordGetNumParam param = new FindPasswordGetNumParam(RegisterProve.this, content.replace(" ", ""));
		HttpStringPost task = new HttpStringPost(RegisterProve.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				second = 60;
				tvTime.setVisibility(View.VISIBLE);
				tvResend.setTextColor(0xffFFFFFF);
				llResend.setBackgroundColor(0x00000000);
				timerTask = new TimerTask() {
					@Override
					public void run() {
						handler.sendEmptyMessage(0);
					}
				};
				timer.schedule(timerTask, 200, 1000);
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
				switch (code) {
				case 1:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("您输入的账号未注册，请直接注册。").withEffect(Effectstype.Shake).show();
					}
					break;
				default:
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
					}
					break;
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
		}, param.getParameters());
		executeRequest(task);
	}
	
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (Constant.SHOW_LOG) {
				Log.e("aaaaaaaaaaa", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
			}
			AppPreference.saveThirdLoginInfo(RegisterProve.this, AppPreference.TYPE_SINA, mAccessToken.getUid(), mAccessToken.getToken(), mAccessToken.getExpiresTime());
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
		LoginThirdParam params = new LoginThirdParam(RegisterProve.this, 3, mAccessToken.getUid(), mAccessToken.getToken(), mAccessToken.getExpiresTime());
		HttpStringPost post = new HttpStringPost(RegisterProve.this, params.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(RegisterProve.this, RegisterData.class);
						intent.putExtra("TYPE", AppPreference.TYPE_SINA);
						startActivity(intent);
						finish();
					} else {
						AppPreference.save(RegisterProve.this, AppPreference.LOGIN_TYPE, AppPreference.TYPE_QQ);
						Intent intent = new Intent(RegisterProve.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finish();
					}
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
	 * QQ登录
	 */
	private void TencentLogin() {
		mLoadingDialog.setMessage("正在登录...");
		mLoadingDialog.show();
		LoginThirdParam params = new LoginThirdParam(RegisterProve.this, 4, mTencent.getOpenId(), mTencent.getAccessToken(), mTencent.getExpiresIn());
		HttpStringPost post = new HttpStringPost(RegisterProve.this, params.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				try {
					JSONObject object = new JSONObject(result);
					if (object.has("is_regist") && object.getInt("is_regist") == 0) {
						Intent intent = new Intent(RegisterProve.this, RegisterData.class);
						intent.putExtra("TYPE", AppPreference.TYPE_QQ);
						startActivity(intent);
						finish();
					} else {
						AppPreference.save(RegisterProve.this, AppPreference.LOGIN_TYPE, AppPreference.TYPE_QQ);
						Intent intent = new Intent(RegisterProve.this, IndexHome.class);
						startActivity(intent);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finish();
					}
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

}
