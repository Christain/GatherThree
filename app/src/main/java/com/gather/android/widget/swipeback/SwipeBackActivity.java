
package com.gather.android.widget.swipeback;

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

import com.baidu.android.pushservice.PushManager;
import com.gather.android.activity.LoginIndex;
import com.gather.android.application.GatherApplication;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.manage.AppManage;
import com.gather.android.preference.AppPreference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

public abstract class SwipeBackActivity extends FragmentActivity implements SwipeBackActivityBase{
    private SwipeBackActivityHelper mHelper;
    private Context context;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected DialogTipsBuilder loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        setContentView(layoutResId());
		this.context = this;
		AppManage appManage = AppManage.getInstance();
		appManage.addFragmentActivity(this);
		this.loginDialog = DialogTipsBuilder.getInstance(context);
		this.loginDialog.setCanceledOnTouchOutside(false);
		onCreateActivity(savedInstanceState);
		SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
		mSwipeBackLayout.setEnableGesture(false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }
    
    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }
    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        getSwipeBackLayout().scrollToFinishActivity();
    }
    
	/**
	 * 重新登录
	 */
	protected void needLogin(String msg){
		if (loginDialog != null && !loginDialog.isShowing()) {
			loginDialog.withDuration(300).withEffect(Effectstype.Fadein).setMessage(msg).isCancelableOnTouchOutside(false).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (PushManager.isPushEnabled(getApplicationContext())) {
						PushManager.stopWork(getApplicationContext());
					}
					GatherApplication application = (GatherApplication) getApplication();
					application.setUserInfoModel(null);
					AppPreference.clearInfo(context);
					Intent intent = new Intent(context, LoginIndex.class);
					startActivity(intent);
					finish();
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
		appManage.finishFragment(this);
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
        AsyncHttpTask.getClient().cancelRequests(context, true);
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
