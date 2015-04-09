package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 收藏的活动列表
 */
public class CollectionActListParam extends BaseParams {

	public CollectionActListParam(int uid, int page, int size) {
		super("act/activity/lovActs");
        put("uid", uid);
        put("page", page);
        put("size", size);
	}
}
