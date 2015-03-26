package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 动态列表TrendsFragment
 */
public class TrendsListParam extends StringParams {

	/**
	 * @param context
	 * @param userId
	 * @param cityId
	 * @param page
	 * @param size
	 */
	public TrendsListParam(Context context, int userId, int cityId, int page, int size) {
		super(context, "act/dynamic/friends");
		setParameter("uid", userId);
		setParameter("cityId", cityId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
