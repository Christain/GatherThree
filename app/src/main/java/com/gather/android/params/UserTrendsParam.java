package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 个人动态
 */
public class UserTrendsParam extends StringParams {

	public UserTrendsParam(Context context, int uid, int page, int size) {
		super(context, "act/dynamic/list");
		setParameter("uid", uid);
		setParameter("page", page);
		setParameter("size", size);
	}

}
