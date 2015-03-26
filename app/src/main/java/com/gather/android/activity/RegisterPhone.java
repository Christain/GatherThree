package com.gather.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gather.android.R;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.LoadingDialog.OnDismissListener;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.params.FindPasswordGetNumParam;
import com.gather.android.params.RegisterPhoneGetNumParam;
import com.gather.android.utils.MobileUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

public class RegisterPhone extends SwipeBackActivity implements OnClickListener {

	private TextView tvTitle, tvNext;
	private EditText etPhone;
	private int type; // 1是注册，2是找回密码
	private boolean isRequest = false;
	private LoadingDialog mLoadingDialog;
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.register_phone;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("TYPE")) {
			type = intent.getExtras().getInt("TYPE");
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			if (type == 1) {
				tvTitle.setText("手机注册");
			} else {
				tvTitle.setText("找回密码");
			}
			this.tvNext = (TextView) findViewById(R.id.tvNext);
			this.dialog = DialogTipsBuilder.getInstance(RegisterPhone.this);
			this.mLoadingDialog = LoadingDialog.createDialog(RegisterPhone.this, true);
			this.etPhone = (EditText) findViewById(R.id.etPhone);
			this.etPhone.addTextChangedListener(new phoneTextWatcher());
			this.mLoadingDialog.setDismissListener(new OnDismissListener() {
				@Override
				public void OnDismiss() {
					isRequest = false;
				}
			});
			this.tvNext.setOnClickListener(this);

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvNext:
			if (!isRequest) {
				isRequest = true;
				String content = etPhone.getText().toString().trim();
				if (!MobileUtil.execute(content.replace(" ", "")).equals("未知")) {
					if (type == 1) {
						getIdentifyNum(content);
					} else {
						getFindPasswordIdentifyNum(content);
					}
				} else {
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请输入正确的电话号码").withEffect(Effectstype.Shake).show();
					}
					isRequest = false;
					return;
				}
			}
			break;
		}
	}

	/**
	 * 注册获取验证码
	 * @param content
	 */
	private void getIdentifyNum(String content) {
		mLoadingDialog.setMessage("正在获取...");
		mLoadingDialog.show();
		RegisterPhoneGetNumParam param = new RegisterPhoneGetNumParam(RegisterPhone.this, content.replace(" ", ""));
		HttpStringPost task = new HttpStringPost(RegisterPhone.this, param.getUrl(), new ResponseListener() {

			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Intent intent = new Intent(RegisterPhone.this, RegisterProve.class);
				intent.putExtra("TYPE", type);
				intent.putExtra("PHONE", etPhone.getText().toString().trim());
				startActivity(intent);
				finish();
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
						dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
					}
					break;
				}
			}
		}, new Response.ErrorListener() {
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
		FindPasswordGetNumParam param = new FindPasswordGetNumParam(RegisterPhone.this, content.replace(" ", ""));
		HttpStringPost task = new HttpStringPost(RegisterPhone.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Intent intent = new Intent(RegisterPhone.this, RegisterProve.class);
				intent.putExtra("TYPE", type);
				intent.putExtra("PHONE", etPhone.getText().toString().trim());
				startActivity(intent);
				finish();
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
		}, new Response.ErrorListener() {

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
