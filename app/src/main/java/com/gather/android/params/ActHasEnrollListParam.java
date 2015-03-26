package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 已报名活动
 */
public class ActHasEnrollListParam extends StringParams {

	public ActHasEnrollListParam(Context context, int uid ,int page, int size) {
		super(context, "act/activity/enrollActs");
		setParameter("uid", uid);
		setParameter("page", page);
		setParameter("size", size);
	}
}
