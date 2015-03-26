package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 收藏的活动列表
 */
public class CollectionActListParam extends StringParams {

	public CollectionActListParam(Context context, int uid, int page, int size) {
		super(context, "act/activity/lovActs");
		setParameter("uid", uid);
		setParameter("page", page);
		setParameter("size", size);
	}
}
