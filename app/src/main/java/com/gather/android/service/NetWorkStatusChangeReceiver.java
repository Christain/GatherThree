package com.gather.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gather.android.application.GatherApplication;

public class NetWorkStatusChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		GatherApplication application = (GatherApplication) context.getApplicationContext();
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		// NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		if (!mobileInfo.isConnected() && !wifiInfo.isConnected()) {
			application.setNetWorkStatus(false);
			Toast.makeText(context, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
		} else if (mobileInfo.isConnected() || wifiInfo.isConnected()) {
			application.setNetWorkStatus(true);
			if (!PushManager.isConnected(context) && !PushManager.isPushEnabled(context)) {
				PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(context, "api_key"));
			}
			// Toast.makeText(context, "网络恢复正常", Toast.LENGTH_SHORT).show();
		}
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

}
