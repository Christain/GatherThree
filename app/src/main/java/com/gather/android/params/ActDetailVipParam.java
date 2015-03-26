package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/*
 * 活动相关达人
 */
public class ActDetailVipParam extends StringParams {

	public ActDetailVipParam(Context context, int cityId, int actId, int page, int size) {
		super(context, "act/activity/listVips");
		setParameter("cityId", cityId);
		setParameter("actId", actId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
