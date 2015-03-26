package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 取消收藏的某个活动 
 */
public class CancelCollectActParam extends StringParams {

	public CancelCollectActParam(Context context, int actId) {
		super(context, "act/activity/delLov");
		setParameter("actId", actId);
	}

}
