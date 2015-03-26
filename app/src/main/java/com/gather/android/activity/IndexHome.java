package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.fragment.HomeFragment;
import com.gather.android.fragment.MessageFragment;
import com.gather.android.fragment.TrendsFragment;
import com.gather.android.fragment.UserFragment;
import com.gather.android.manage.AppManage;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.MMAlert;

@SuppressLint("InflateParams")
public class IndexHome extends BaseActivity {

	private FragmentTabHost mTabHost;
	private Class<?> fragmentArray[] = { HomeFragment.class, TrendsFragment.class, MessageFragment.class, UserFragment.class };
	private int iconArray[] = { R.drawable.tab_host_home_click, R.drawable.tab_host_trends_click, R.drawable.tab_host_message_click, R.drawable.tab_host_user_click };
	private String titleArray[] = { "首页", "动态", "消息", "个人中心" };
	private AppManage appManage;
	private DialogChoiceBuilder dialog;
	
	@Override
	protected int layoutResId() {
		return R.layout.index_home;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		appManage = AppManage.getInstance();
		mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
		dialog = DialogChoiceBuilder.getInstance(IndexHome.this);
		setupTabView();
		addListenner();
		
	}
	
	/**
	 * 添加切换卡
	 */
	private void setupTabView() {
		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
		mTabHost.getTabWidget().setDividerDrawable(null);

		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			TabHost.TabSpec tabSpec = mTabHost.newTabSpec(titleArray[i]).setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		}
	}

	/**
	 * 切换卡点击监听
	 */
	private void addListenner() {
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId.contains("消息") || tabId.contains("个人中心")) {
					if (!AppPreference.hasLogin(IndexHome.this)) {
						mTabHost.setCurrentTab(0);
						if (dialog != null && !dialog.isShowing()) {
							dialog.setMessage("想看更多内容，现在就去登录吧？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if (PushManager.isPushEnabled(getApplicationContext())) {
										PushManager.stopWork(getApplicationContext());
									}
									GatherApplication application = (GatherApplication) getApplication();
									application.setUserInfoModel(null);
									AppPreference.clearInfo(IndexHome.this);
									Intent intent = new Intent(IndexHome.this, LoginIndex.class);
									startActivity(intent);
									dialog.dismiss();
								}
							}).show();
						}
					}
				}
			}
		});
	}

	/**
	 * 切换卡上每一个Item的生成
	 * 
	 * @param index
	 * @return
	 */
	private View getTabItemView(int index) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.tab_host_item, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
		imageView.setImageResource(iconArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.tv_icon);
		textView.setText(titleArray[index]);

		return view;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			MMAlert.showAlert(IndexHome.this, "菜单选项", new String[] { "注销帐号", "退出集合啦" }, null, new MMAlert.OnAlertSelectId() {
				public void onDismissed() {
				}

				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						if (!ClickUtil.isFastClick()) {
							if (PushManager.isPushEnabled(getApplicationContext())) {
								PushManager.stopWork(getApplicationContext());
							}
							GatherApplication application = (GatherApplication) getApplication();
							application.setUserInfoModel(null);
							AppPreference.clearInfo(IndexHome.this);
							Intent intent = new Intent(IndexHome.this, LoginIndex.class);
							startActivity(intent);
							finish();
						}
						break;
					case 1:
						if (!ClickUtil.isFastClick()) {
							appManage.exit(IndexHome.this);
						}
						break;
					}
				}
			});
		}
		return true;
	}

}
