package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 收藏活动
 */
public class CollectActParam extends BaseParams {

	public CollectActParam(int actId) {
		super("act/activity/lov");
        put("actId", actId);
	}

}
