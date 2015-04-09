package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 个人动态
 */
public class UserTrendsParam extends BaseParams {

	public UserTrendsParam(int uid, int page, int size) {
		super("act/dynamic/list");
        put("uid", uid);
        put("page", page);
        put("size", size);
	}

}
