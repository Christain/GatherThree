package com.gather.android.http;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.gather.android.constant.Constant;
import com.gather.android.preference.AppPreference;

public class HttpStringGet extends Request<String> {

	private ErrorListener errorListener;
	private String url;
	private long startTime;

	private ResponseListener listener;
	private boolean isSuccess;
	private String cookies;
	private Context context;

	public HttpStringGet(Context context, String url, ResponseListener listener, ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		this.context = context;
		this.listener = listener;
		this.isSuccess = false;
		this.errorListener = errorListener;
		if (Constant.SHOW_LOG) {
			this.startTime = System.currentTimeMillis();
			this.url = url;
		}
//		cookies = AppPreference.getCookie(context);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}
		cookies = null;
		cookies = AppPreference.getCookie(context);
		if (cookies != null) {
			StringBuilder sb = new StringBuilder();
            sb.append(cookies);
            if (headers.containsKey("Cookie")) {
                sb.append(";");
                sb.append(headers.get("Cookie"));
            }
            headers.put("Cookie", sb.toString());
			return headers;
		}
		return super.getHeaders();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		Response<String> superResponse = netWorkResponse(response);
		Map<String, String> responseHeaders = response.headers;
		if (responseHeaders.containsKey("Set-Cookie")) {
			String rawCookies = responseHeaders.get("Set-Cookie");
			if (rawCookies != null && rawCookies.length() > 0) {
				// PHPSESSID=j73tr12pvuirgjben8dfq76kb2; path=/;
				AppPreference.updateCookie(context, rawCookies.substring(0, rawCookies.indexOf(";")));
				// cookies = rawCookies;
			}
		}
		return superResponse;
	}

//	@Override
//	public RetryPolicy getRetryPolicy() {
//		RetryPolicy retryPolicy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//		return retryPolicy;
//	}

	private Response<String> netWorkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(String response) {
		JSONObject object = null;
		String msg = null;
		try {
			object = new JSONObject(response);
			int code = object.getInt("code");
			msg = object.getString("msg");
			if (code == 0) {
				isSuccess = true;
				listener.success(code, msg, object.getString("body"));
			} else if (code == 5) {
				isSuccess = false;
				msg = "请重新登录";
				listener.relogin(msg);
			} else {
				isSuccess = false;
				listener.error(code, msg);
			}
			msg = null;
		} catch (JSONException e) {
			isSuccess = false;
			msg = null;
			object = null;
			listener.error(-1, "服务器返回错误");
			e.printStackTrace();
		}
		if (Constant.SHOW_LOG) {
			StringBuffer sb = new StringBuffer();
			sb.append("------------------------------------start----------------------------------\n");
			sb.append("URL---> ");
			sb.append(url);
			sb.append("\n");
			if (cookies != null) {
				sb.append("cookie---> ");
				sb.append(cookies);
			}
			sb.append("\n");
			sb.append("\n>>> Response: ");
			if (object != null) {
				sb.append("\n" + object.toString());
			} else {
				sb.append("\n" + response);
			}
			sb.append("\n--------------------------------- ");
			double time = (System.currentTimeMillis() - startTime);
			sb.append(time);
			sb.append("ms ----------------------------------");
			if (isSuccess) {
				Log.i("Request", sb.toString());
			} else {
				Log.e("Request", sb.toString());
			}
			sb = null;
		}
		object = null;
	}

	@Override
	public void deliverError(VolleyError error) {
		if (Constant.SHOW_LOG) {
			StringBuffer sb = new StringBuffer();
			sb.append("-----------------------------------start----------------------------------\n");
			sb.append("URL---> ");
			sb.append(url);
			sb.append("\n");
			if (cookies != null) {
				sb.append("cookie---> ");
				sb.append(cookies);
			}
			sb.append("\n");
			sb.append("\n>>> ErrorCode:  ");
			if (error.networkResponse != null) {
				sb.append(error.networkResponse.statusCode);
			} else {
				sb.append(error.getMessage());
			}
			sb.append("\n--------------------------------- ");
			double time = (System.currentTimeMillis() - startTime);
			sb.append(time);
			sb.append("ms ----------------------------------");
			Log.e("Request", sb.toString());
			sb = null;
		}
		if (error.getMessage() == null) {
			error.setMessage("访问失败，请检查网络连接状态");
		} else {
			if (error.getMessage().contains("java.net")) {
				error.setMessage("网络连接失败~~请检查网络状态");
			}
		}
		errorListener.onErrorResponse(error);
	}

}
