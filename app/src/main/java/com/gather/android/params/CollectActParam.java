package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 收藏活动
 */
public class CollectActParam extends StringParams {

	public CollectActParam(Context context, int actId) {
		super(context, "act/activity/lov");
		setParameter("actId", actId);
	}

}
