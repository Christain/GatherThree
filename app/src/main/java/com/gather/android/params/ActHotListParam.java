package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 热门活动列表
 */
public class ActHotListParam extends BaseParams {

	public ActHotListParam(int cityId, int page, int size) {
		super("act/activity/recommendActs");
        put("cityId", cityId);
        put("page", page);
        put("size", size);
	}

}
