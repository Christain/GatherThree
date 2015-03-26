package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 删除动态（自己发布的）
 */
public class DelTrendsParam extends StringParams {

	public DelTrendsParam(Context context, int dynamicId) {
		super(context, "act/dynamic/delete");
		setParameter("dynamicId", dynamicId);
	}

}
