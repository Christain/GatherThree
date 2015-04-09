package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 已报名活动
 */
public class ActHasEnrollListParam extends BaseParams {

	public ActHasEnrollListParam(int uid ,int page, int size) {
		super("act/activity/enrollActs");
        put("uid", uid);
        put("page", page);
        put("size", size);
	}
}
