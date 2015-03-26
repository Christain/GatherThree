package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取用户动态图片
 */
public class GetUserTrendsPicParam extends StringParams {

	public GetUserTrendsPicParam(Context context, int uid, int page, int size) {
		super(context, "act/dynamic/photos");
		setParameter("uid", uid);
		setParameter("page", page);
		setParameter("size", size);
	}

}
