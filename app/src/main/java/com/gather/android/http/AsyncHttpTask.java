package com.gather.android.http;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.cookie.Cookie;

import java.util.List;

public class AsyncHttpTask {
	private static final Context context = GatherApplication.getInstance().getApplicationContext();
	
	public static final String HEADER_DEVICE_NAME = "Device-Name";
	public static final String HEADER_SYSTEM_API_VERSION = "System-Api-Version";
	public static final String HEADER_APPLICATION_API_VERSION = "App-Api-Version";
	public static final String HEADER_APPLICATION_VERSION = "App-Version";
	public static final String APPLICATION_API_VERSION = "1.0";
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static PersistentCookieStore cookieStore = new PersistentCookieStore(context);

	static {
		client.setTimeout(8000);
		client.setMaxRetriesAndTimeout(2, 2000);
		client.setUserAgent(Build.MODEL);
		client.addHeader(HEADER_DEVICE_NAME, Build.MODEL);
		client.addHeader(HEADER_SYSTEM_API_VERSION, String.valueOf(Build.VERSION.SDK_INT));
		client.addHeader(HEADER_APPLICATION_API_VERSION, APPLICATION_API_VERSION);
		client.setCookieStore(cookieStore);

		try {
			client.addHeader(HEADER_APPLICATION_VERSION,
					String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void resetCookie() {
		cookieStore.clear();
		client.setCookieStore(cookieStore);
	}

    public static AsyncHttpClient getClient() {
        return client;
    }

	public static Cookie getSessionCookie() {
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			if ("PHPSESSID".equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	private static RequestParams requestParamsWithToken(RequestParams params) {
		if (params == null) {
			params = new RequestParams();
		}
		return params;
	}

	public static void get(String domain, String uri, RequestParams params, JsonHttpResponseHandler responseHandler) {
		String url = (TextUtils.isEmpty(domain) ? "" : domain) + (TextUtils.isEmpty(uri) ? "" : uri);
		client.get(url, requestParamsWithToken(params), responseHandler);
	}

	public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
		get(null, url, requestParamsWithToken(params), responseHandler);
	}

	public static void post(String domain, String uri, RequestParams params, JsonHttpResponseHandler responseHandler) {
		String url = (TextUtils.isEmpty(domain) ? "" : domain) + (TextUtils.isEmpty(uri) ? "" : uri);
		client.post(url, requestParamsWithToken(params), responseHandler);
        if (Constant.SHOW_LOG) {
            StringBuffer sb = new StringBuffer();
            sb.append("请求参数"+ "\n");
            sb.append("url--->" + uri + "\n");
            String[] param = params.toString().split("&");
            for (int i = 0; i < param.length; i++) {
                sb.append(param[i] + "\n");
            }
            Log.i("Request", sb.toString()) ;
        }
	}

	public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
		post(null, url, requestParamsWithToken(params), responseHandler);
	}
}
