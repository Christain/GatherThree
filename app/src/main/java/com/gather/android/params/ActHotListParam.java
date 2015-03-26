package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 热门活动列表
 */
public class ActHotListParam extends StringParams {

	public ActHotListParam(Context context, int cityId, int page, int size) {
		super(context, "act/activity/recommendActs");
		setParameter("cityId", cityId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
