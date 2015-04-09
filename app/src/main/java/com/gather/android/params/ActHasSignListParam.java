package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 已签到活动列表
 */
public class ActHasSignListParam extends BaseParams {

	public ActHasSignListParam(int uid, int page, int size) {
		super("act/activity/checkinActs");
        put("uid", uid);
        put("page", page);
        put("size", size);
	}
}
