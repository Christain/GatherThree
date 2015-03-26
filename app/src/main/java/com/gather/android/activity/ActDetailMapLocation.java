package com.gather.android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.dialog.DialogChoiceBuilder;
import com.gather.android.dialog.DialogTipsBuilder;
import com.gather.android.dialog.Effectstype;
import com.gather.android.utils.ClickUtil;

public class ActDetailMapLocation extends BaseActivity implements OnClickListener {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;

	MapView mMapView;
	BaiduMap mBaiduMap;

	private ImageView ivLeft, ivRight, ivMyLocation;
	private TextView tvLeft, tvTitle, tvRight;

	boolean isNaviLoaction = false;// 是否首次定位
	private double lat, lon, myLat, myLon;
	private Marker mMarkerAct;
	private DialogTipsBuilder dialog;
	BitmapDescriptor bdAct = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_mark);

	@Override
	protected int layoutResId() {
		return R.layout.act_detail_map_location;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent.hasExtra("LAT") && intent.hasExtra("LON")) {
			this.lat = intent.getExtras().getDouble("LAT");
			this.lon = intent.getExtras().getDouble("LON");

			this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
			this.ivRight = (ImageView) findViewById(R.id.ivRight);
			this.tvLeft = (TextView) findViewById(R.id.tvLeft);
			this.tvTitle = (TextView) findViewById(R.id.tvTitle);
			this.tvRight = (TextView) findViewById(R.id.tvRight);
			this.tvLeft.setVisibility(View.GONE);
			this.ivRight.setVisibility(View.GONE);
			this.tvRight.setVisibility(View.VISIBLE);
			this.ivLeft.setVisibility(View.VISIBLE);
			this.tvTitle.setText("地图");
			this.tvRight.setText("导航");
			this.ivLeft.setImageResource(R.drawable.title_back_click_style);
			this.ivLeft.setOnClickListener(this);
			this.tvRight.setOnClickListener(this);

			this.dialog = DialogTipsBuilder.getInstance(ActDetailMapLocation.this);
			// 地图初始化
			mMapView = (MapView) findViewById(R.id.bmapView);
			this.ivMyLocation = (ImageView) findViewById(R.id.ivMyLocation);
			this.ivMyLocation.setOnClickListener(this);
			mBaiduMap = mMapView.getMap();
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
			mMapView.removeViewAt(1);
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
			// 定位初始化
			mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
//			option.setScanSpan(60000);
			mLocClient.setLocOption(option);
			mLocClient.start();

			LatLng llact = new LatLng(lat, lon);
			OverlayOptions ooA = new MarkerOptions().position(llact).icon(bdAct).zIndex(9).draggable(true);
			mMarkerAct = (Marker) (mBaiduMap.addOverlay(ooA));

			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llact);
			mBaiduMap.setMapStatus(u);
			MapStatusUpdate i = MapStatusUpdateFactory.zoomTo(16);
			mBaiduMap.animateMapStatus(i);
		} else {
			finish();
			toast("初始化地图错误");
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivLeft:
			finish();
			break;
		case R.id.tvRight:
			if (!ClickUtil.isFastClick()) {
				if (myLat != 0 && myLon != 0) {
					startNavi();
				} else {
					mLocClient.start();
					isNaviLoaction = true;
					if (dialog != null && !dialog.isShowing()) {
						dialog.setMessage("正在确定您当前位置,请稍等").withEffect(Effectstype.Shake).show();
					}
				}
			}
			break;
		case R.id.ivMyLocation:
			if (!ClickUtil.isFastClick() && myLat != 0) {
				LatLng llact = new LatLng(myLat, myLon);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llact);
				mBaiduMap.setMapStatus(u);
			}
			break;
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			myLat = location.getLatitude();
			myLon = location.getLongitude();
			if (isNaviLoaction) {
				isNaviLoaction = false;
				startNavi();
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void startNavi() {
		LatLng pt1 = new LatLng(myLat, myLon);
		LatLng pt2 = new LatLng(lat, lon);
		// 构建 导航参数
		NaviPara para = new NaviPara();
		para.startPoint = pt1;
		para.startName = "从这里开始";
		para.endPoint = pt2;
		para.endName = "到这里结束";

		try {
			BaiduMapNavigation.openBaiduMapNavi(para, this);
		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
			DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(ActDetailMapLocation.this);
			dialog.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？").withDuration(400).withEffect(Effectstype.SlideBottom).setOnClick(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					BaiduMapNavigation.getLatestBaiduMapApp(ActDetailMapLocation.this);
				}
			}).show();
		}
	}

	@Override
	protected void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mMapView != null) {
			mMapView.onResume();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if (mLocClient != null) {
			mLocClient.stop();
		}
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		if (mMapView != null) {
			mMapView.onDestroy();
			mMapView = null;
		}
		super.onDestroy();
	}

}
