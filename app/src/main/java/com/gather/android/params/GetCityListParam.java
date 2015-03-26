package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取城市列表
 */
public class GetCityListParam extends StringParams {

	public GetCityListParam(Context context) {
		super(context, "act/city/cities");
	}

}
