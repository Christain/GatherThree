package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.manage.ExceptionManage;
import com.gather.android.manage.PhoneManage;
import com.gather.android.params.GetActTagsParam;
import com.gather.android.params.GetCityListParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.service.PushMessageReceiver;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.tendcloud.tenddata.TCAgent;

@SuppressLint("HandlerLeak")
public class StartActivity extends SwipeBackActivity implements Runnable {

	private static final long START_DURATION = 2500;// 启动页持续时间
	private static final int START_PROGRESS_OVER = 12;
	private boolean needLogin = false, needGuide;
	private static final String LOGIN_TIMES = "LOGIN_TIMES";
	private GatherApplication application;

	/**
	 * 定位
	 */
	private LocationClient mLocationClient;
	

	@Override
	protected int layoutResId() {
		return R.layout.start;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		application = (GatherApplication) getApplication();
		GatherApplication.cityId = 0;
		PushMessageReceiver.baiduUserId = "";
		PushMessageReceiver.baiduChannelId = "";

		if (checkNetwork()) {
			/**
			 * 定位
			 */
			this.mLocationClient = ((GatherApplication) getApplication()).mLocationClient;
			this.initLocation();
			getCityList();
			getActMarkList();
		}

		ExceptionManage.startInstance(getApplication());// 启动错误管理

		/**
		 * 推送绑定
		 */
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(StartActivity.this, "api_key"));

		new Thread(this).start();
	}

	/**
	 * 检查网络状态
	 */
	private boolean checkNetwork() {
		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = conn.getActiveNetworkInfo();
		if (net != null && net.isConnected()) {
			application.setNetWorkStatus(true);
			return true;
		}
		application.setNetWorkStatus(false);
		return false;
	}

	/**
	 * 获取城市列表
	 */
	private void getCityList() {
		GetCityListParam param = new GetCityListParam(StartActivity.this);
		HttpStringPost task = new HttpStringPost(StartActivity.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				SharedPreferences cityPreferences = StartActivity.this.getSharedPreferences("CITY_LIST", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = cityPreferences.edit();
				editor.putString("CITY", result);
				editor.commit();
			}

			@Override
			public void relogin(String msg) {

			}

			@Override
			public void error(int code, String msg) {
				if (Constant.SHOW_LOG) {
					Toast.makeText(StartActivity.this, "获取城市失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (Constant.SHOW_LOG) {
					Toast.makeText(StartActivity.this, "获取城市失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, param.getParameters());
		RequestManager.addRequest(task, StartActivity.this);
	}

	/**
	 * 获取活动标签列表
	 */
	private void getActMarkList() {
		GetActTagsParam param = new GetActTagsParam(StartActivity.this, application.getCityId());
		HttpStringPost task = new HttpStringPost(StartActivity.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				SharedPreferences cityPreferences = StartActivity.this.getSharedPreferences("ACT_MARK_LIST_"+ application.getCityId(), Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = cityPreferences.edit();
				editor.putString("MARK", result);
				editor.commit();
			}

			@Override
			public void relogin(String msg) {

			}

			@Override
			public void error(int code, String msg) {

			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}, param.getParameters());
		RequestManager.addRequest(task, StartActivity.this);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		TCAgent.onResume(this);
	}

	@Override
	public void onStop() {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		TCAgent.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	/**
	 * 初始化定位
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(0);// 设置发起定位请求的间隔时间为5000ms
		option.setAddrType("all");
		option.setIsNeedAddress(true);// 是否需要地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		// 初始化操作----------------------------------------------
		if (PhoneManage.isSdCardExit()) {
			Constant.checkPath();
		} else {
			handler.sendEmptyMessage(0);
		}

		// 登录判断
		int sid = AppPreference.getUserPersistentInt(this, AppPreference.LOGIN_TYPE);
		int isregist = AppPreference.getUserPersistentInt(this, AppPreference.IS_REGISTER);
		if (sid != 0 && isregist == 0) {
			needLogin = true;
		} else {
			needLogin = false;
		}

		// 登录次数判断，用于是否显示导航页
		SharedPreferences timePreferences = StartActivity.this.getSharedPreferences(LOGIN_TIMES, Context.MODE_PRIVATE);
		int times = timePreferences.getInt("TIMES", 0);
		if (times == 0) {
			needGuide = true;
			SharedPreferences.Editor editor = timePreferences.edit();
			editor.putInt("TIMES", 1);
			editor.commit();
		} else {
			needGuide = false;
		}

		// 初始化结束-----------------------------------------------
		long endTime = System.currentTimeMillis();
		long diffTime = endTime - startTime;
		while (diffTime <= START_DURATION) {
			diffTime = System.currentTimeMillis() - startTime;
			/** 线程等待 **/
			Thread.yield();
		}
		handler.sendEmptyMessage(START_PROGRESS_OVER);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_PROGRESS_OVER:
				Intent intent = null;
				if (needGuide) {
					intent = new Intent(StartActivity.this, GuideActivity.class);
				} else {
					if (needLogin) {
						intent = new Intent(StartActivity.this, LoginIndex.class);
					} else {// 跳首页
						intent = new Intent(StartActivity.this, IndexHome.class);
					}
				}
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				break;
			case 0:
				Toast.makeText(StartActivity.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}
