package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动详情
 */
public class ActDetailParam extends BaseParams {

	public ActDetailParam(int actId) {
		super("act/actMore/info");
        put("actId", actId);
	}

}
