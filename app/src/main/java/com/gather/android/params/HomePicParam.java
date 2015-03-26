package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 首页轮播图
 */
public class HomePicParam extends StringParams {

	public HomePicParam(Context context, int cityId, int page, int size) {
		super(context, "act/news/homeAdverts");
		setParameter("cityId", cityId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
