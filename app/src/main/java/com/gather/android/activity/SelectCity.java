package com.gather.android.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.gather.android.R;
import com.gather.android.adapter.SelectCityAdapter;
import com.gather.android.application.GatherApplication;
import com.gather.android.application.GatherApplication.LocationListener;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.HttpStringPost;
import com.gather.android.http.RequestManager;
import com.gather.android.http.ResponseListener;
import com.gather.android.model.CityList;
import com.gather.android.model.CityListModel;
import com.gather.android.model.NewsModel;
import com.gather.android.model.NewsModelList;
import com.gather.android.model.UserInfoModel;
import com.gather.android.params.BindPushParam;
import com.gather.android.params.GetCityListParam;
import com.gather.android.params.GetUserInfoParam;
import com.gather.android.params.HomePicParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.service.PushMessageReceiver;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.NoScrollListView;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

public class SelectCity extends SwipeBackActivity implements OnClickListener {

	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvTitle, tvRight;

	private LinearLayout llLocation;
	private TextView tvLocation;
	private ImageView ivSelect;
	private NoScrollListView listView;
	private ScrollView scrollView;
	private SelectCityAdapter adapter;
	private LoadingDialog mLoadingDialog;

	private CityList list;
	private boolean canClick = true;

	/**
	 * 定位
	 */
	private LocationClient mLocationClient;
	private GatherApplication application;
	private DialogTipsBuilder dialog;

	@Override
	protected int layoutResId() {
		return R.layout.select_city;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.application = (GatherApplication) getApplication();
		this.dialog = DialogTipsBuilder.getInstance(SelectCity.this);
		this.mLoadingDialog = LoadingDialog.createDialog(SelectCity.this, true);
		this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
		this.ivRight = (ImageView) findViewById(R.id.ivRight);
		this.tvLeft = (TextView) findViewById(R.id.tvLeft);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.tvLeft.setVisibility(View.GONE);
		this.ivRight.setVisibility(View.GONE);
		this.tvRight.setVisibility(View.VISIBLE);
		this.ivLeft.setVisibility(View.VISIBLE);
		this.tvTitle.setText("城市选择");
		this.tvRight.setText("确定");
		this.tvRight.setOnClickListener(this);
		this.ivLeft.setImageResource(R.drawable.title_back_click_style);
		this.ivLeft.setOnClickListener(this);

		this.llLocation = (LinearLayout) findViewById(R.id.llLocation);
		this.tvLocation = (TextView) findViewById(R.id.tvLocation);
		this.ivSelect = (ImageView) findViewById(R.id.ivSelect);
		this.scrollView = (ScrollView) findViewById(R.id.ScrollView);
		if (Build.VERSION.SDK_INT >= 9) {
			scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		this.listView = (NoScrollListView) findViewById(R.id.listview);
		this.adapter = new SelectCityAdapter(SelectCity.this);
		this.listView.setAdapter(adapter);

		this.listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				CityListModel model = adapter.getItem(position);
				if (null != model && canClick) {
					canClick = false;
					for (int i = 0; i < list.getCities().size(); i++) {
						if (list.getCities().get(i).isSelect()) {
							list.getCities().get(i).setSelect(false);
						}
					}
					if (ivSelect.isShown()) {
						ivSelect.setVisibility(View.GONE);
					}
					model.setSelect(true);
					adapter.notifyDataSetChanged();
					canClick = true;
				}
			}
		});

		this.llLocation.setOnClickListener(this);
		this.mLocationClient = ((GatherApplication) getApplication()).mLocationClient;

		this.initView();
	}

	private void initView() {
		if (application.mLocation != null && application.mLocation.getCity() != null) {
			String city = application.mLocation.getCity();
			if (city.contains("市")) {
				city = city.substring(0, city.length() - 1);
			}
			this.tvLocation.setText(city);
			city = null;
		} else {
			this.tvLocation.setText("定位中...");
		}
		this.initLocation();
		this.application.setLocationListener(new LocationListener() {
			@Override
			public void OnResultLocation(BDLocation location) {
				String city = location.getCity();
				if (city.contains("市")) {
					city = city.substring(0, city.length() - 1);
				}
				tvLocation.setText(city);
				city = null;
			}
		});
		SharedPreferences cityPreferences = SelectCity.this.getSharedPreferences("CITY_LIST", Context.MODE_PRIVATE);
		String city = cityPreferences.getString("CITY", "");
		if (!city.equals("")) {
			Gson gson = new Gson();
			list = gson.fromJson(city, CityList.class);
			for (int i = 0; i < list.getCities().size(); i++) {
				if (application.getCityId() != 0 && list.getCities().get(i).getId() == application.getCityId()) {
					list.getCities().get(i).setSelect(true);
				}
			}
			adapter.setNotifyData(list.getCities());
		}
		getCityList();
	}

	@Override
	public void onStop() {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onStop();
	}

	/**
	 * 获取城市列表
	 */
	private void getCityList() {
		GetCityListParam param = new GetCityListParam(SelectCity.this);
		HttpStringPost task = new HttpStringPost(SelectCity.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				SharedPreferences cityPreferences = SelectCity.this.getSharedPreferences("CITY_LIST", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = cityPreferences.edit();
				editor.putString("CITY", result);
				editor.commit();
				Gson gson = new Gson();
				list = gson.fromJson(result, CityList.class);
				for (int i = 0; i < list.getCities().size(); i++) {
					if (application.getCityId() != 0 && list.getCities().get(i).getId() == application.getCityId()) {
						list.getCities().get(i).setSelect(true);
					}
				}
				adapter.setNotifyData(list.getCities());
			}

			@Override
			public void relogin(String msg) {

			}

			@Override
			public void error(int code, String msg) {
				Toast.makeText(SelectCity.this, "获取城市失败", Toast.LENGTH_SHORT).show();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(SelectCity.this, "获取城市失败", Toast.LENGTH_SHORT).show();
			}
		}, param.getParameters());
		RequestManager.addRequest(task, SelectCity.this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.llLocation:
			if (!ClickUtil.isFastClick() && !tvLocation.getText().toString().contains("定位中") && !ivSelect.isShown() && canClick) {
				canClick = false;
				ivSelect.setVisibility(View.VISIBLE);
				for (int i = 0; i < list.getCities().size(); i++) {
					if (list.getCities().get(i).isSelect()) {
						list.getCities().get(i).setSelect(false);
					}
				}
				adapter.notifyDataSetChanged();
				canClick = true;
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (ivSelect.isShown()) {
					for (int i = 0; i < list.getCities().size(); i++) {
						if (list.getCities().get(i).getName().contains(tvLocation.getText().toString())) {
							if (AppPreference.hasLogin(SelectCity.this)) {
								getUserInfo(list.getCities().get(i).getId(), list.getCities().get(i).getName());
							} else {
								getHomePic(list.getCities().get(i).getId(), list.getCities().get(i).getName());
							}
						}
					}
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("您所选城市不在我们服务范围内，请重新选择").withEffect(Effectstype.Shake).show();
					}
				} else {
					for (int i = 0; i < list.getCities().size(); i++) {
						if (list.getCities().get(i).isSelect()) {
							if (AppPreference.hasLogin(SelectCity.this)) {
								getUserInfo(list.getCities().get(i).getId(), list.getCities().get(i).getName());
							} else {
								getHomePic(list.getCities().get(i).getId(), list.getCities().get(i).getName());
							}
							return;
						}
					}
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("请选择一个城市").withEffect(Effectstype.Shake).show();
					}
				}
			}
			break;
		}
	}

	/**
	 * 获取个人信息
	 */
	private void getUserInfo(final int cityId, final String name) {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("切换城市中...");
			mLoadingDialog.show();
		}
		GetUserInfoParam param = new GetUserInfoParam(SelectCity.this, cityId);
		HttpStringPost task = new HttpStringPost(SelectCity.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				try {
					JSONObject object = new JSONObject(result);
					Gson gson = new Gson();
					UserInfoModel userInfoModel = gson.fromJson(object.getString("user"), UserInfoModel.class);
					if (userInfoModel != null) {
						if (application != null) {
							application.setUserInfoModel(userInfoModel);
						}
						AppPreference.saveUserInfo(SelectCity.this, userInfoModel);
						application.setCityId(cityId);
						AppPreference.save(SelectCity.this, AppPreference.LOCATION_CITY, name);
						AppPreference.save(SelectCity.this, AppPreference.LOCATION_CITY_CODE, cityId);
						getHomePic(cityId, name);
						if (AppPreference.hasLogin(SelectCity.this) && PushMessageReceiver.baiduUserId.length() > 1 && PushMessageReceiver.baiduChannelId.length() > 1) {
							BindService(SelectCity.this, PushMessageReceiver.baiduUserId, PushMessageReceiver.baiduChannelId);
						}
					} else {
						Toast.makeText(SelectCity.this, "获取个人信息失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(SelectCity.this, "个人信息解析失败", Toast.LENGTH_SHORT).show();
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
	 * 获取首页轮播图片
	 */
	private void getHomePic(final int cityId, final String name) {
		HomePicParam param = new HomePicParam(SelectCity.this, cityId, 1, 10);
		HttpStringPost task = new HttpStringPost(SelectCity.this, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				Gson gson = new Gson();
				NewsModelList list = gson.fromJson(result, NewsModelList.class);
				ArrayList<NewsModel> imgList = null;
				if (list != null && list.getNews() != null) {
					imgList = list.getNews();
				} else {
					imgList = new ArrayList<NewsModel>();
				}
				AppPreference.save(SelectCity.this, AppPreference.LOCATION_CITY, name);
				AppPreference.save(SelectCity.this, AppPreference.LOCATION_CITY_CODE, cityId);
				application.setCityId(cityId);
				Intent intent = new Intent();
				intent.putExtra("CITY", name);
				intent.putExtra("PIC_LIST", imgList);
				setResult(RESULT_OK, intent);
				finish();
			}

			@Override
			public void relogin(String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
			}

			@Override
			public void error(int code, String msg) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (Constant.SHOW_LOG) {
					toast("获取轮播图 失败");
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (Constant.SHOW_LOG) {
					toast("获取轮播图 失败");
				}
			}
		}, param.getParameters());
		executeRequest(task);
	}

	/**
	 * 绑定推送到服务端
	 * 
	 * @param context
	 * @param baiduUserId
	 * @param baiduChannelId
	 */
	private void BindService(final Context context, String baiduUserId, String baiduChannelId) {
		BindPushParam param = new BindPushParam(context, GatherApplication.cityId, 3, baiduUserId, baiduChannelId);
		HttpStringPost task = new HttpStringPost(context, param.getUrl(), new ResponseListener() {
			@Override
			public void success(int code, String msg, String result) {
				if (Constant.SHOW_LOG) {
					Toast.makeText(context, "绑定服务成功", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void relogin(String msg) {

			}

			@Override
			public void error(int code, String msg) {
				if (Constant.SHOW_LOG) {
					Toast.makeText(context, "绑定服务失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (Constant.SHOW_LOG) {
					Toast.makeText(context, "绑定服务失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, param.getParameters());
		RequestManager.addRequest(task, context);
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

}
