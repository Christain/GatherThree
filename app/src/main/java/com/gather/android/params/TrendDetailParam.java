package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 某一条动态详情
 */
public class TrendDetailParam extends StringParams {

	public TrendDetailParam(Context context, int dynamicId) {
		super(context, "act/dynamic/profile");
		setParameter("dynamicId", dynamicId);
	}
}
