package com.gather.android.http;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpStringPost extends Request<String> {

	private HashMap<String, String> mMap;
	private ErrorListener errorListener;
	private ResponseListener listener;
	private long startTime;
	private String url;
	private boolean isSuccess, isLogin = false;
	private String cookies;
	private Context context;

	public HttpStringPost(Context context, String url, ResponseListener listener, ErrorListener errorListener, HashMap<String, String> map) {
		super(Method.POST, url, errorListener);
		this.context = context;
		this.listener = listener;
		this.errorListener = errorListener;
		this.isSuccess = false;
		mMap = map;
		if (Constant.SHOW_LOG) {
			startTime = System.currentTimeMillis();
			this.url = url;
		}
		this.isLogin = false;
	}
	
	public HttpStringPost(Context context, String url, ResponseListener listener, ErrorListener errorListener, HashMap<String, String> map, boolean isLogin) {
		super(Method.POST, url, errorListener);
		this.context = context;
		this.listener = listener;
		this.errorListener = errorListener;
		this.isSuccess = false;
		mMap = map;
		if (Constant.SHOW_LOG) {
			startTime = System.currentTimeMillis();
			this.url = url;
		}
		this.isLogin = true;
	}

	@Override
	protected HashMap<String, String> getParams() throws AuthFailureError {
		return mMap;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}
		cookies = AppPreference.getCookie(context);
		if (cookies != null && cookies.length() > 1) {
            headers.put("Cookie", cookies);
			return headers;
		}

		return super.getHeaders();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		Map<String, String> responseHeaders = response.headers;
		if (responseHeaders.containsKey("Set-Cookie")) {
			if (isLogin) {
				String rawCookies = responseHeaders.get("Set-Cookie");
				if (rawCookies != null && rawCookies.length() > 0) {
					AppPreference.updateCookie(context, rawCookies);
					// PHPSESSID=j73tr12pvuirgjben8dfq76kb2; path=/;
					// cookies = rawCookies;
				}
			}
		}
		Response<String> superResponse = netWorkResponse(response);
		return superResponse;
	}
	
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
		if (listener != null) {
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
				listener.error(-1, "服务器返回数据错误");
				e.printStackTrace();
			}
			if (Constant.SHOW_LOG) {
				StringBuffer sb = new StringBuffer();
                sb.append("---\n");
				sb.append("------------------------------------start----------------------------------\n");
				sb.append("URL---> ");
				sb.append(url);
				sb.append("\n");
				if (cookies != null) {
					sb.append("cookie---> ");
					sb.append(cookies);
				}
				sb.append("\n");
				if (!mMap.isEmpty()) {
					sb.append("Parameters: ");
					Iterator<Map.Entry<String, String>> iterator = mMap.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, String> entry = iterator.next();
						if (entry.getValue() instanceof String) {
							sb.append("\n   ");
							sb.append(entry.getKey());
							sb.append(" = ");
							sb.append(entry.getValue());
						}
					}
					iterator = null;
				}
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
			mMap.clear();
		}
	}

	@Override
	public void deliverError(VolleyError error) {
		if (errorListener != null) {
			if (Constant.SHOW_LOG) {
				StringBuffer sb = new StringBuffer();
                sb.append("---\n");
				sb.append("-----------------------------------start----------------------------------\n");
				sb.append("URL---> ");
				sb.append(url);
				sb.append("\n");
				if (cookies != null) {
					sb.append("cookie---> ");
					sb.append(cookies);
				}
				sb.append("\n");
				if (!mMap.isEmpty()) {
					sb.append("Parameters: ");
					Iterator<Map.Entry<String, String>> iterator = mMap.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, String> entry = iterator.next();
						if (entry.getValue() instanceof String) {
							sb.append("\n   ");
							sb.append(entry.getKey());
							sb.append(" = ");
							sb.append(entry.getValue());
						}
					}
					iterator = null;
				}
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
			mMap.clear();
		}
	}

}