package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动详情
 */
public class ActDetailParam extends StringParams {

	public ActDetailParam(Context context, int actId) {
		super(context, "act/actMore/info");
		setParameter("actId", actId);
	}

}
