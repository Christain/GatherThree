package com.gather.android.activity;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.ResponseListener;
import com.gather.android.params.ResetPasswordParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;

public class ResetPassword extends SwipeBackActivity implements OnClickListener {
	
	private TextView tvNext;
	private EditText etPasswordOne, etPasswordTwo;
	private DialogTipsBuilder dialog;
	private LoadingDialog mLoadingDialog;
	
	@Override
	protected int layoutResId() {
		return R.layout.reset_password;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.mLoadingDialog = LoadingDialog.createDialog(ResetPassword.this, true);
		this.tvNext = (TextView) findViewById(R.id.tvNext);
		this.etPasswordOne = (EditText) findViewById(R.id.etPasswordOne);
		this.etPasswordTwo = (EditText) findViewById(R.id.etPasswordTwo);
		this.dialog = DialogTipsBuilder.getInstance(ResetPassword.this);
		
		this.tvNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvNext:
			if (!ClickUtil.isFastClick()) {
				final String passwordOne, passwordTwo;
				passwordOne = etPasswordOne.getText().toString().trim();
				passwordTwo = etPasswordTwo.getText().toString().trim();
				if (TextUtils.isEmpty(passwordOne)) {
					dialog.setMessage("请输入您的密码").withEffect(Effectstype.Fall).show();
					return;
				}
				if (TextUtils.isEmpty(passwordTwo)) {
					dialog.setMessage("请再次输入您的密码").withEffect(Effectstype.Fall).show();
					return;
				}
				if (passwordOne.length() < 6 || passwordTwo.length() < 6) {
					dialog.setMessage("密码长度应该在6~16之间").withEffect(Effectstype.Fall).show();
					return;
				}
				if (!passwordOne.equals(passwordTwo)) {
					dialog.setMessage("您两次输入的密码不一致").withEffect(Effectstype.Shake).show();
					return;
				}
				UpdatePassword(passwordOne);
			}
			break;
		}
	}
	
	/**
	 * 修改密码
	 * @param password
	 */
	private void UpdatePassword(String password) {
		mLoadingDialog.setMessage("正在修改...");
		mLoadingDialog.show();
		ResetPasswordParam param = new ResetPasswordParam(ResetPassword.this, password);
		HttpStringPost task = new HttpStringPost(ResetPassword.this, param.getUrl(), new ResponseListener() {
			
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				toast("修改成功");
				finishActivity();
			}
			
			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (dialog != null && !dialog.isShowing()) {
					dialog.setMessage(msg).withEffect(Effectstype.Shake).show();
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

}
