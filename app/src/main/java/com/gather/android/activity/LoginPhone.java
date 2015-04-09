package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.params.LoginPhoneParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.utils.MobileUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginPhone extends SwipeBackActivity implements OnClickListener {

	private TextView tvNext, tvFindPassword, tvRegister;
	private EditText etPhone, etPassword;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	private static final int REGISTER = 1;
	private static final int FIND_PASSWORD = 2;

	@Override
	protected int layoutResId() {
		return R.layout.login_phone;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.dialog = DialogTipsBuilder.getInstance(LoginPhone.this);
		this.mLoadingDialog = LoadingDialog.createDialog(LoginPhone.this, true);
		this.etPhone = (EditText) findViewById(R.id.etPhone);
		this.etPassword = (EditText) findViewById(R.id.etPassword);
		this.tvNext = (TextView) findViewById(R.id.tvNext);
		this.tvFindPassword = (TextView) findViewById(R.id.tvFindPassword);
		this.tvRegister = (TextView) findViewById(R.id.tvRegister);

		this.tvNext.setOnClickListener(this);
		this.tvFindPassword.setOnClickListener(this);
		this.tvRegister.setOnClickListener(this);
		this.etPhone.addTextChangedListener(new phoneTextWatcher());
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.tvNext:
			if (!ClickUtil.isFastClick()) {
				if (!MobileUtil.execute(etPhone.getText().toString().trim().replace(" ", "")).equals("未知")) {
					int passwordLength = etPassword.getText().toString().length();
					if (passwordLength == 0) {
						dialog.setMessage("请输入密码").withEffect(Effectstype.Shake).show();
						return;
					}
					if (passwordLength < 6 || passwordLength > 16) {
						dialog.setMessage("密码长度应该在6~16之间").withEffect(Effectstype.Shake).show();
						return;
					}
					Login();
				} else {
					dialog.setMessage("请输入正确的电话号码").withEffect(Effectstype.Shake).show();
					return;
				}
			}
			break;
		case R.id.tvFindPassword:
			intent = new Intent(LoginPhone.this, RegisterPhone.class);
			intent.putExtra("TYPE", FIND_PASSWORD);
			startActivity(intent);
			break;
		case R.id.tvRegister:
			intent = new Intent(LoginPhone.this, RegisterPhone.class);
			intent.putExtra("TYPE", REGISTER);
			startActivity(intent);
			break;
		}
	}

	/**
	 * 登录
	 */
	private void Login() {
		mLoadingDialog.setMessage("正在登录...");
		mLoadingDialog.show();
		LoginPhoneParam param = new LoginPhoneParam(1, etPhone.getText().toString().trim().replace(" ", ""), etPassword.getText().toString().trim());
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(LoginPhone.this, "api_key"));
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.has("is_regist") && object.getInt("is_regist") == 0) {
                        Intent intent = new Intent(LoginPhone.this, RegisterData.class);
                        intent.putExtra("TYPE", AppPreference.TYPE_SELF);
                        startActivity(intent);
                    } else {
                        AppPreference.savePhoneLoginInfo(LoginPhone.this, etPhone.getText().toString().trim().replace(" ", ""));
                        Intent intent = new Intent(LoginPhone.this, IndexHome.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                switch (returnCode) {
                    case 1:
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("暂无此账号信息，请注册。").withEffect(Effectstype.Shake).show();
                        }
                        break;
                    case 2:
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage("您输入的账号或密码有误，请重新输入。").withEffect(Effectstype.Shake).show();
                        }
                        break;
                    default:
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.setMessage(errorMsg).withEffect(Effectstype.Shake).show();
                        }
                        break;
                }
            }
        });
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

	private class phoneTextWatcher implements TextWatcher {
		@Override
		public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
			String contents = str.toString();
			int length = contents.length();
			if (length == 4) {
				if (contents.substring(3).equals(new String(" "))) {
					contents = contents.substring(0, 3);
					etPhone.setText(contents);
					etPhone.setSelection(contents.length());
				} else {
					contents = contents.substring(0, 3) + " " + contents.substring(3);
					etPhone.setText(contents);
					etPhone.setSelection(contents.length());
				}
			} else if (length == 9) {
				if (contents.substring(8).equals(new String(" "))) {
					contents = contents.substring(0, 8);
					etPhone.setText(contents);
					etPhone.setSelection(contents.length());
				} else {
					contents = contents.substring(0, 8) + " " + contents.substring(8);
					etPhone.setText(contents);
					etPhone.setSelection(contents.length());
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence str, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	}

}
