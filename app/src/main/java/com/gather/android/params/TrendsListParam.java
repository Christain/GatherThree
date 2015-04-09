package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 动态列表TrendsFragment
 */
public class TrendsListParam extends BaseParams {

	/**
	 * @param userId
	 * @param cityId
	 * @param page
	 * @param size
	 */
	public TrendsListParam(int userId, int cityId, int page, int size) {
		super("act/dynamic/friends");
        put("uid", userId);
        put("cityId", cityId);
        put("page", page);
        put("size", size);
	}

}
