package com.gather.android.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.BindThirdParam;
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

@SuppressLint("InflateParams")
public class BindThird extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private TextView tvBindTencent, tvBindSina, tvSinaName, tvTencentName;
	private UserInfoModel model;

	/********** 第三方绑定 *************/
	private Tencent mTencent;
	private IUiListener listener;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;

	private boolean isDataChange = false;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.bind_third;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("MODEL")) {
			model = (UserInfoModel) intent.getSerializableExtra("MODEL");
			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.GONE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.tvTitle.setText("帐号绑定");
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);
			this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
			this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);

			this.tvBindTencent = (TextView) findViewById(R.id.tvBindTencent);
			this.tvBindSina = (TextView) findViewById(R.id.tvBindSina);
			this.tvSinaName = (TextView) findViewById(R.id.tvSinaName);
			this.tvSinaName.setVisibility(View.GONE);
			this.tvTencentName = (TextView) findViewById(R.id.tvTencentName);
			this.tvTencentName.setVisibility(View.GONE);

			// this.tvBindPhone.setOnClickListener(this);
			this.tvBindTencent.setOnClickListener(this);
			this.tvBindSina.setOnClickListener(this);
			this.dialog = DialogTipsBuilder.getInstance(BindThird.this);
			this.mLoadingDialog = LoadingDialog.createDialog(BindThird.this, true);

			this.init();
		} else {
			finish();
			toast("数据错误~~");
		}

	}

	private void init() {
		if (!model.getSina_openid().equals("")) {
			Oauth2AccessToken token = new Oauth2AccessToken();
			token.setUid(model.getSina_openid());
			token.setToken(model.getSina_token());
			token.setExpiresTime(model.getSina_expires_in());
			if (!token.isSessionValid()) {
				tvBindSina.setText("已过期");
				tvBindSina.setTextColor(0xFFFFFFFF);
				tvBindSina.setSelected(true);
				tvBindSina.setClickable(true);
			} else {
				tvBindSina.setText("已绑定");
				tvBindSina.setTextColor(0xFF999999);
				tvBindSina.setSelected(false);
				tvBindSina.setClickable(false);
				getSinaInfo();
			}
		} else {
			tvBindSina.setText("绑定");
			tvBindSina.setTextColor(0xFFFFFFFF);
			tvBindSina.setSelected(true);
			tvBindSina.setClickable(true);
		}

		if (!model.getQq_openid().equals("")) {
			mTencent.setOpenId(model.getQq_openid());
			mTencent.setAccessToken(model.getQq_token(), String.valueOf(model.getQq_expires_in()));
			if (!mTencent.isSessionValid()) {
				tvBindTencent.setText("已过期");
				tvBindTencent.setTextColor(0xFFFFFFFF);
				tvBindTencent.setSelected(true);
				tvBindTencent.setClickable(true);
			} else {
				tvBindTencent.setText("已绑定");
				tvBindTencent.setTextColor(0xFF999999);
				tvBindTencent.setSelected(false);
				tvBindTencent.setClickable(false);
				getTencentInfo();
			}
		} else {
			tvBindTencent.setText("绑定");
			tvBindTencent.setTextColor(0xFFFFFFFF);
			tvBindTencent.setSelected(true);
			tvBindTencent.setClickable(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.tvBindTencent:
			if (tvBindTencent.getText().toString().contains("绑定") || tvBindTencent.getText().toString().contains("已过期")) {
				if (!ClickUtil.isFastClick()) {
					IdentifyTencent();
				}
			}
			break;
		case R.id.tvBindSina:
			if (tvBindSina.getText().toString().contains("绑定") || tvBindSina.getText().toString().contains("已过期")) {
				if (!ClickUtil.isFastClick()) {
					if (checkSinaPackage()) {
						mSsoHandler = new SsoHandler(BindThird.this, mWeiboAuth);
						mSsoHandler.authorize(new AuthListener());
					} else {
						mWeiboAuth.anthorize(new AuthListener());
					}
				}
			}
			break;
		}
	}

	/**
	 * Tencent验证
	 */
	private void IdentifyTencent() {
		listener = new IUiListener() {
			@Override
			public void onCancel() {

			}

			@Override
			public void onComplete(Object arg0) {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = arg0;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onError(UiError arg0) {
				toast(arg0.toString());
			}
		};
		mTencent.login(BindThird.this, "get_simple_userinfo", listener);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				String openid = null,
				token = null,
				expires_in = null;
				try {
					JSONObject object = (JSONObject) msg.obj;
					openid = object.getString("openid");
					token = object.getString("access_token");
					expires_in = object.getString("expires_in");
					Bind(4, openid, token, Long.parseLong(expires_in));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};

	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (Constant.SHOW_LOG) {
				Log.e("aaaaaaaaaaa", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
			}
			Bind(3, mAccessToken.getUid(), mAccessToken.getToken(), mAccessToken.getExpiresTime());
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onWeiboException(WeiboException e) {

		}
	}

	/**
	 * 绑定第三方账号
	 * 
	 * @param openType
	 * @param openid
	 * @param token
	 * @param expires_in
	 */
	private void Bind(final int openType, final String openid, final String token, final long expires_in) {
		mLoadingDialog.setMessage("正在绑定");
		mLoadingDialog.show();
		BindThirdParam param = new BindThirdParam(BindThird.this, openType, openid, token, expires_in);
		HttpStringPost task = new HttpStringPost(BindThird.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				isDataChange = true;
				if (openType == 4) {
					model.setQq_openid(openid);
					model.setQq_token(token);
					model.setQq_expires_in(expires_in);
					tvBindTencent.setText("已绑定");
					tvBindTencent.setTextColor(0xFF999999);
					tvBindTencent.setSelected(false);
					tvBindTencent.setClickable(false);
					getTencentInfo();
				} else {
					model.setSina_openid(openid);
					model.setSina_token(token);
					model.setSina_expires_in(expires_in);
					tvBindSina.setText("已绑定");
					tvBindSina.setTextColor(0xFF999999);
					tvBindSina.setSelected(false);
					tvBindSina.setClickable(false);
					getSinaInfo();
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
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 获取新浪微博个人信息
	 */
	private void getSinaInfo() {
		StringRequest task = new StringRequest("https://api.weibo.com/2/users/show.json" + "?uid=" + model.getSina_openid() + "&access_token=" + model.getSina_token(), new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject object = new JSONObject(response);
					tvSinaName.setVisibility(View.VISIBLE);
					tvSinaName.setText(object.getString("screen_name"));
				} catch (JSONException e) {
					tvSinaName.setVisibility(View.GONE);
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				toast(error.getMsg());
			}
		});
		executeRequest(task);
	}

	/**
	 * 获取QQ个人信息
	 */
	private void getTencentInfo() {
		StringRequest task = new StringRequest("https://graph.qq.com/user/get_user_info?" + "oauth_consumer_key=" + Constant.TENCENT_APPID + "&access_token=" + model.getQq_token() + "&openid=" + model.getQq_openid(), new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (Constant.SHOW_LOG) {
					Log.e("response", "https://graph.qq.com/user/get_user_info?" + "oauth_consumer_key=" + Constant.TENCENT_APPID + "&access_token=" + model.getQq_token() + "&openid=" + model.getQq_openid() + "\n" + response);
				}
				try {
					JSONObject object = new JSONObject(response);
					tvTencentName.setVisibility(View.VISIBLE);
					tvTencentName.setText(object.getString("nickname"));
				} catch (JSONException e) {
					tvTencentName.setVisibility(View.GONE);
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				toast(error.getMsg());
			}
		});
		executeRequest(task);
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

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// switch (keyCode) {
	// case KeyEvent.KEYCODE_BACK:
	// if (isDataChange) {
	// Intent intent = new Intent();
	// intent.putExtra("MODEL", model);
	// setResult(RESULT_OK, intent);
	// }
	// finish();
	// break;
	// }
	// return true;
	// }

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
}
