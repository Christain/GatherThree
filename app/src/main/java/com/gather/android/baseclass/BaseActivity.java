package com.gather.android.baseclass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.activity.LoginIndex;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.http.RequestManager;
import com.gather.android.manage.AppManage;
import com.gather.android.preference.AppPreference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

public abstract class BaseActivity extends FragmentActivity {

	private Context context;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected DialogTipsBuilder loginDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutResId());
		this.context = this;
		AppManage appManage = AppManage.getInstance();
		appManage.addActivity(this);
		this.loginDialog = DialogTipsBuilder.getInstance(context);
		this.loginDialog.setCanceledOnTouchOutside(false);
		onCreateActivity(savedInstanceState);
	}

	/**
	 * 网络请求
	 * 
	 * @param request
	 */
	protected void executeRequest(Request<?> request) {
		RequestManager.addRequest(request, this);
	}
	
	/**
	 * 重新登录
	 */
	protected void needLogin(String msg){
		if (loginDialog != null && !loginDialog.isShowing()) {
			loginDialog.withDuration(300).withEffect(Effectstype.Fall).setMessage(msg).isCancelableOnTouchOutside(false).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (PushManager.isPushEnabled(context)) {
						PushManager.stopWork(context);
					}
					GatherApplication application = (GatherApplication) getApplication();
					application.setUserInfoModel(null);
					AppPreference.clearInfo(context);
					Intent intent = new Intent(context, LoginIndex.class);
					startActivity(intent);
					finishActivity();
				}
			}).setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
						return true;
					} else {
						return false;
					}
				}
			});
			loginDialog.show();
		}
	}
	
	/**
	 * 网络检查
	 */
	protected void NetWorkDialog() {
		final DialogTipsBuilder netDialog = DialogTipsBuilder.getInstance(context);
		if (netDialog != null && !netDialog.isShowing()) {
			netDialog.setMessage("最遥远的距离就是没网，请检查网络").withEffect(Effectstype.Shake).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					netDialog.dismiss();
					Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
					startActivity(intent);
				}
			}).show();
		}
	}
	
	@Override
	public void finish() {
		AppManage appManage = AppManage.getInstance();
		appManage.finishActivity(this);
	}

	public void finishActivity() {
		super.finish();
	}

	/**
	 * 退出程序
	 */
	public void exitApp() {
		AppManage appManage = AppManage.getInstance();
		appManage.exit(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TCAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		TCAgent.onPause(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		RequestManager.cancelAll(this);
	}

	/**
	 * toast message
	 * 
	 * @param text
	 */
	protected void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}


	protected abstract int layoutResId();


	protected abstract void onCreateActivity(Bundle savedInstanceState);

}
