package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 已签到活动列表
 */
public class ActHasSignListParam extends StringParams {

	public ActHasSignListParam(Context context, int uid, int page, int size) {
		super(context, "act/activity/checkinActs");
		setParameter("uid", uid);
		setParameter("page", page);
		setParameter("size", size);
	}
}
