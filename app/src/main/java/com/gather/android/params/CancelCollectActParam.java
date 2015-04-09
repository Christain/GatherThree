package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 取消收藏的某个活动 
 */
public class CancelCollectActParam extends BaseParams {

	public CancelCollectActParam(int actId) {
		super("act/activity/delLov");
        put("actId", actId);
	}

}
