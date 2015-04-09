package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 某一条动态详情
 */
public class TrendDetailParam extends BaseParams {

	public TrendDetailParam(int dynamicId) {
		super("act/dynamic/profile");
        put("dynamicId", dynamicId);
	}
}
