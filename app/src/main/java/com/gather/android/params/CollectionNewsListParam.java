package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 收藏的资讯列表
 */
public class CollectionNewsListParam extends StringParams {

	/**
	 * @param context
	 * @param uid
	 * @param typeId  类型id：1攻略，2回忆，3票务，4专访
	 * @param page
	 * @param size
	 */
	public CollectionNewsListParam(Context context, int uid, int typeId, int page, int size) {
		super(context, "act/news/lovNews");
		setParameter("uid", uid);
		if (typeId != 0) {
			setParameter("typeId", typeId);
		}
		setParameter("page", page);
		setParameter("size", size);
	}

}
