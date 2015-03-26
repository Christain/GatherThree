package com.gather.android.baseclass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.android.pushservice.PushManager;
import com.gather.android.activity.LoginIndex;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.preference.AppPreference;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseFragment extends Fragment {

	protected Context context;
	protected int widthPixels, heightPixels;
	protected float density;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected DialogTipsBuilder loginDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		this.density = metrics.density;
		this.widthPixels = metrics.widthPixels;
		this.heightPixels = metrics.heightPixels;
		this.loginDialog = DialogTipsBuilder.getInstance(getActivity());
		this.loginDialog.setCanceledOnTouchOutside(false);
		OnCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.context = getActivity();
		return OnCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		OnActivityCreated(savedInstanceState);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		OnSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	protected int dip2px(int dip) {
		return (int) (density * dip + 0.5f);
	}

	/**
	 * 重新登录
	 */
	protected void needLogin(String msg) {
		if (loginDialog != null && !loginDialog.isShowing()) {
			loginDialog.withDuration(300).withEffect(Effectstype.Fall).setMessage(msg).isCancelableOnTouchOutside(false).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (PushManager.isPushEnabled(context)) {
						PushManager.stopWork(context);
					}
					GatherApplication application = (GatherApplication) context.getApplicationContext();
					application.setUserInfoModel(null);
					AppPreference.clearInfo(context);
					Intent intent = new Intent(context, LoginIndex.class);
					startActivity(intent);
					getActivity().finish();
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
		final DialogTipsBuilder netDialog = DialogTipsBuilder.getInstance(getActivity());
		if (netDialog != null && !netDialog.isShowing()) {
			netDialog.setMessage("最遥远的距离就是没网，请检查网络").withEffect(Effectstype.Shake).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					netDialog.dismiss();
					Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
					getActivity().startActivity(intent);
				}
			}).show();
		}
	}

	/**
	 * toast message
	 * 
	 * @param text
	 */
	protected void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create页面内容
	 */
	protected abstract void OnCreate(Bundle savedInstanceState);

	/**
	 * createView
	 */
	protected abstract View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	/**
	 * 保存页面数据
	 */
	protected abstract void OnSaveInstanceState(Bundle outState);

	/**
	 * activityCreated
	 */
	protected abstract void OnActivityCreated(Bundle savedInstanceState);

}
