package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/*
 * 活动相关达人
 */
public class ActDetailVipParam extends BaseParams {

	public ActDetailVipParam(int cityId, int actId, int page, int size) {
		super("act/activity/listVips");
        put("cityId", cityId);
        put("actId", actId);
        put("page", page);
        put("size", size);
	}

}
