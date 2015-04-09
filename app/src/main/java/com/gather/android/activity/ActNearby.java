package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.application.GatherApplication.LocationErrorListener;
import com.gather.android.application.GatherApplication.LocationListener;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.UserInterestList;
import com.gather.android.model.UserInterestModel;
import com.gather.android.params.GetActTagsParam;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.google.gson.Gson;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 附近活动地图展示
 */
@SuppressLint("DefaultLocale")
public class ActNearby extends SwipeBackActivity implements OnClickListener, CloudListener {

	private ImageView ivBack;
	private TextView tvTitle, tvRight;
	private ArrayList<TextView> mTextList;

	private ArrayList<UserInterestModel> tagList = new ArrayList<UserInterestModel>();
	private LinearLayout linearLayout;
	private RelativeLayout rlBar;

	private LoadingDialog mLoadingDialog;
	/**
	 * 定位
	 */
	private LocationClient mLocationClient;
	private GatherApplication application;

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private MyLocationConfiguration.LocationMode mCurrentMode;
	private ImageView ivMyLocation;

	private double lat = 0, lon = 0;

	@Override
	protected int layoutResId() {
		return R.layout.act_nearby;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		this.mLoadingDialog = LoadingDialog.createDialog(ActNearby.this, false);
		this.application = (GatherApplication) getApplication();
		this.ivBack = (ImageView) findViewById(R.id.ivBack);
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvTitle.setText("活动地图");
		this.tvRight = (TextView) findViewById(R.id.tvRight);
		this.rlBar = (RelativeLayout) findViewById(R.id.rlBar);
		this.linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);
		this.ivMyLocation = (ImageView) findViewById(R.id.ivMyLocation);

		CloudManager.getInstance().init(ActNearby.this);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mMapView.removeViewAt(1);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
		int childCount = mMapView.getChildCount();
		View zoom = null;
		for (int i = 0; i < childCount; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				zoom = child;
				break;
			}
		}
		zoom.setVisibility(View.GONE);

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		this.ivBack.setOnClickListener(this);
		this.tvRight.setOnClickListener(this);
		this.ivMyLocation.setOnClickListener(this);
		this.mLocationClient = ((GatherApplication) getApplication()).mLocationClient;

		this.initView();
	}

	private void initView() {
		SharedPreferences actMarkPreferences = getSharedPreferences("ACT_MARK_LIST_" + application.getCityId(), Context.MODE_PRIVATE);
		String actMark = actMarkPreferences.getString("MARK", "");
		if (!actMark.equals("")) {
			Gson gson = new Gson();
			UserInterestList list = gson.fromJson(actMark, UserInterestList.class);
			if (list != null) {
				tagList = list.getTags();
				if (tagList.size() == 0 || tagList.size() == 1) {
					toast("没有满足条件的活动");
					finish();
				} else {
					rlBar.setVisibility(View.VISIBLE);
					mTextList = new ArrayList<TextView>();
					ColorStateList csl = (ColorStateList) getBaseContext().getResources().getColorStateList(R.drawable.friends_list_tab_text_color);
					for (int i = 0; i < tagList.size(); i++) {
						TextView textView = new TextView(ActNearby.this);
						textView.setText(tagList.get(i).getName());
						textView.setGravity(Gravity.CENTER);
						if (i == 0) {
							textView.setSelected(true);
						} else {
							textView.setSelected(false);
						}
						textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
						textView.setTextColor(csl);
						textView.setOnClickListener(new TabOnClickListener(i));
						textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
						mTextList.add(textView);
						linearLayout.addView(textView);
					}
					if (application.mLocation != null && application.mLocation.getCity() != null) {
						MyLocationData locData = new MyLocationData.Builder().accuracy(application.mLocation.getRadius()).direction(100).latitude(application.mLocation.getLatitude()).longitude(application.mLocation.getLongitude()).build();
						mBaiduMap.setMyLocationData(locData);
						this.lat = application.mLocation.getLatitude();
						this.lon = application.mLocation.getLongitude();
						if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
							mLoadingDialog.setMessage("获取中...");
							mLoadingDialog.show();
						}
						NearbySearchInfo info = new NearbySearchInfo();
						info.ak = "pKwRXmjD4zwKYBbrK0olawyt";
						if (Constant.SHOW_LOG) {
							info.geoTableId = 94390;
						} else {
							info.geoTableId = 94392;
						}
						info.radius = 50000;
						info.pageIndex = 0;
						info.pageSize = 50;
						info.filter = "act_tag_id:" + tagList.get(0).getId() + "," + tagList.get(0).getId();
						// info.filter = "act_t_status:1,3";
						info.location = lon + "," + lat;
						CloudManager.getInstance().nearbySearch(info);
					} else {
						if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
							mLoadingDialog.setMessage("正在定位中...");
							mLoadingDialog.show();
						}
						this.initLocation();
						this.application.setLocationListener(new LocationListener() {
							@Override
							public void OnResultLocation(BDLocation location) {
								if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
									mLoadingDialog.dismiss();
								}
								MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
								mBaiduMap.setMyLocationData(locData);
								lat = location.getLatitude();
								lon = location.getLongitude();
								if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
									mLoadingDialog.setMessage("获取中...");
									mLoadingDialog.show();
								}
								NearbySearchInfo info = new NearbySearchInfo();
								info.ak = "pKwRXmjD4zwKYBbrK0olawyt";
								if (Constant.SHOW_LOG) {
									info.geoTableId = 94390;
								} else {
									info.geoTableId = 94392;
								}
								info.radius = 50000;
								info.pageIndex = 0;
								info.pageSize = 50;
								info.filter = "act_tag_id:" + tagList.get(0).getId() + "," + tagList.get(0).getId();
								info.location = lon + "," + lat;
								CloudManager.getInstance().nearbySearch(info);
							}
						});
						this.application.setErrorLocationListener(new LocationErrorListener() {
							@Override
							public void OnErrorLocation() {
								if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
									mLoadingDialog.dismiss();
								}
								toast("定位失败，请重试");
								finish();
							}
						});
					}
				}
			} else {
				toast("没有满足条件的活动");
				finish();
			}
		} else {
			getActMarkList();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		case R.id.ivMyLocation:
			if (!ClickUtil.isFastClick() && lat != 0) {
				LatLng llact = new LatLng(lat, lon);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llact);
				mBaiduMap.setMapStatus(u);
			}
			break;
		}
	}

	/**
	 * 标签点击监听
	 */
	private class TabOnClickListener implements OnClickListener {
		private int index = 0;

		public TabOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			for (int i = 0; i < mTextList.size(); i++) {
				if (i == index) {
					mTextList.get(i).setSelected(true);
				} else {
					mTextList.get(i).setSelected(false);
				}
				if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
					mLoadingDialog.setMessage("更新中...");
					mLoadingDialog.show();
				}
				NearbySearchInfo info = new NearbySearchInfo();
				info.ak = "pKwRXmjD4zwKYBbrK0olawyt";
				if (Constant.SHOW_LOG) {
					info.geoTableId = 94390;
				} else {
					info.geoTableId = 94392;
				}
				info.radius = 50000;
				info.pageIndex = 0;
				info.pageSize = 50;
				info.filter = "act_tag_id:" + tagList.get(index).getId() + "," + tagList.get(index).getId();
				info.location = lon + "," + lat;
				CloudManager.getInstance().nearbySearch(info);
			}
		}
	};

	/**
	 * 获取活动标签列表
	 */
	private void getActMarkList() {
		if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
			mLoadingDialog.setMessage("加载中...");
			mLoadingDialog.show();
		}
		GetActTagsParam param = new GetActTagsParam(application.getCityId());
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                SharedPreferences cityPreferences = ActNearby.this.getSharedPreferences("ACT_MARK_LIST_" + application.getCityId(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = cityPreferences.edit();
                editor.putString("MARK", result);
                editor.commit();
                initView();
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
                toast("获取活动标签出错");
                finish();
            }
        });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMapView != null) {
			mMapView.onDestroy();
		}
		CloudManager.getInstance().destroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mMapView != null) {
			mMapView.onPause();
		}
	}

	@Override
	public void onStop() {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();
		}
	}

	public void onGetDetailSearchResult(DetailSearchResult result, int error) {

	}

	public void onGetSearchResult(final CloudSearchResult result, int error) {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
		mBaiduMap.clear();
		if (result != null && result.poiList != null && result.poiList.size() > 0) {
			BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_mark);
			Builder builder = new Builder();
			for (int i = 0; i < result.size; i++) {
				CloudPoiInfo info = result.poiList.get(i);
				LatLng ll = new LatLng(info.latitude, info.longitude);
				Bundle bundle = new Bundle();
				bundle.putInt("position", i);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll).extraInfo(bundle);
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
				info = null;
			}
			mBaiduMap.setOnMarkerClickListener(new OnMarkerMapClickListener(result));
			LatLngBounds bounds = builder.build();
			MapStatusUpdate i = MapStatusUpdateFactory.zoomTo(12);
			mBaiduMap.animateMapStatus(i);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u);
		} else {
			toast("附近没有这类活动信息");
		}
	}

	private class OnMarkerMapClickListener implements OnMarkerClickListener {

		private CloudSearchResult result;

		public OnMarkerMapClickListener(CloudSearchResult result) {
			this.result = result;
		}

		@SuppressLint("InflateParams")
		@Override
		public boolean onMarkerClick(Marker marker) {
			mBaiduMap.hideInfoWindow();
			int position = marker.getExtraInfo().getInt("position");
			CloudPoiInfo info = result.poiList.get(position);
			LatLng ll = new LatLng(info.latitude, info.longitude);
			View popview = LayoutInflater.from(ActNearby.this).inflate(R.layout.marker_pop, null);
			TextView textV = (TextView) popview.findViewById(R.id.text_pop);
			Map<String, Object> map = new HashMap<String, Object>();
			map = info.extras;
			textV.setText((String) map.get("act_title"));
			final int act_id = (Integer) map.get("act_id");
			InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(popview), ll, -70, new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick() {
					mBaiduMap.hideInfoWindow();
					Intent intent = new Intent(ActNearby.this, ActDetail.class);
					intent.putExtra("ID", act_id);
					startActivity(intent);
				}
			});
			mBaiduMap.showInfoWindow(infoWindow);
			return true;
		}
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
