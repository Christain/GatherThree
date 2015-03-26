package com.gather.android.http;

import android.content.Context;

import com.gather.android.constant.Constant;

public class HttpGetUtil {

	private StringBuffer sb;

	public HttpGetUtil(Context context, String url) {
		sb = new StringBuffer();
		sb.append(Constant.DEFOULT_REQUEST_URL);
		sb.append(url);
	}

	public void setFirstParam(String key, Object value) {
		sb.append("?");
		sb.append(key);
		sb.append("=");
		sb.append(value);
	}

	public void setParam(String key, Object value) {
		sb.append("&");
		sb.append(key);
		sb.append("=");
		sb.append(value);
	}

	public String toString() {
		return sb.toString();
	}

}
