package com.gather.android.application;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.gather.android.constant.Constant;
import com.gather.android.constant.Constant.Config;
import com.gather.android.model.UserInfoModel;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tendcloud.tenddata.TCAgent;

import java.io.File;

public class GatherApplication extends FrontiaApplication {
    private static GatherApplication instance;
	public boolean isDown;
	public boolean isRun;

	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	public BDLocation mLocation = null;
	private LocationListener listener;
	private LocationErrorListener errorLocationListener;

	private UserInfoModel userInfoModel; // 用户信息Model
	public static int cityId = 0; // 当前城市ID
	private boolean hasNetWork; // 检查网络状态

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();
        instance = this;
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getApplicationContext());
		SDKInitializer.initialize(this);
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		initImageLoader(getApplicationContext());


		/**
		 * 初始化统计
		 */
		TCAgent.init(this);
	}

    public static GatherApplication getInstance(){
        if (instance == null) {
            instance = new GatherApplication();
        }
        return instance;
    }

	public static void initImageLoader(Context context) {
		if (Constant.SHOW_LOG) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.MIN_PRIORITY + 3).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).memoryCache(new LruMemoryCache(2 * 1024 * 1024)).tasksProcessingOrder(QueueProcessingType.LIFO).threadPoolSize(5).diskCache(new UnlimitedDiscCache(new File(Constant.IMAGE_CACHE_DIR_PATH)))// 自定义缓存路径
				.writeDebugLogs().build();
			ImageLoader.getInstance().init(config);
		} else {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.MIN_PRIORITY + 3).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).memoryCache(new LruMemoryCache(2 * 1024 * 1024)).tasksProcessingOrder(QueueProcessingType.LIFO).threadPoolSize(5).diskCache(new UnlimitedDiscCache(new File(Constant.IMAGE_CACHE_DIR_PATH)))// 自定义缓存路径
				.build();
			ImageLoader.getInstance().init(config);
		}
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				String city = location.getCity();
				if (city != null) {
					if (Constant.SHOW_LOG) {
						Toast.makeText(getApplicationContext(), "定位成功", Toast.LENGTH_SHORT).show();
					}
					mLocation = location;
					if (city.contains("市")) {
						city = city.substring(0, city.length() - 1);
					}
					city = null;
					if (Constant.SHOW_LOG) {
						StringBuffer sb = new StringBuffer(256);
						sb.append("\nerror code : ");
						sb.append(location.getLocType());
						sb.append("\nlatitude : ");
						sb.append(location.getLatitude());
						sb.append("\nlontitude : ");
						sb.append(location.getLongitude());
						sb.append("\nradius : ");
						sb.append(location.getRadius());
						if (location.getLocType() == BDLocation.TypeGpsLocation) {
							sb.append("\nspeed : ");
							sb.append(location.getSpeed());
							sb.append("\nsatellite : ");
							sb.append(location.getSatelliteNumber());
							sb.append("\ndirection : ");
							sb.append("\naddr : ");
							sb.append(location.getAddrStr());
							sb.append(location.getDirection());
						} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
							sb.append("\naddr : ");
							sb.append(location.getAddrStr());
							// 运营商信息
							sb.append("\noperationers : ");
							sb.append(location.getOperators());
						}
						Log.i("*********定位信息*********", sb.toString());
					}
					if (listener != null) {
						listener.OnResultLocation(location);
					}
				} else {
					if (errorLocationListener != null) {
						errorLocationListener.OnErrorLocation();
					}
					if (Constant.SHOW_LOG) {
						Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				if (errorLocationListener != null) {
					errorLocationListener.OnErrorLocation();
				}
				if (Constant.SHOW_LOG) {
					Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void setUserInfoModel(UserInfoModel model) {
		this.userInfoModel = model;
	}

	public UserInfoModel getUserInfoModel() {
		return userInfoModel;
	}

	public void setCityId(int cityId) {
		GatherApplication.cityId = cityId;
	}

	public int getCityId() {
		return cityId;
	}

	public interface LocationListener {
		public void OnResultLocation(BDLocation location);
	}
	
	public interface LocationErrorListener {
		public void OnErrorLocation();
	}

	public void setLocationListener(LocationListener listener) {
		this.listener = listener;
	}
	
	public void setErrorLocationListener(LocationErrorListener errorLocationListener) {
		this.errorLocationListener = errorLocationListener;
	}

	public void setNetWorkStatus(boolean hasNetWork) {
		this.hasNetWork = hasNetWork;
	}

	public boolean getNetWorkStatus() {
		return hasNetWork;
	}
	
}
