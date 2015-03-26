package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动详情相关（资讯，达人，攻略）
 *
 */
public class ActDetailRelationParam extends StringParams {

	/**
	 * @param context
	 * @param actId	  活动Id
	 * @param typeId  类型id：1攻略，2回忆，3票务，4专访
	 * @param page
	 * @param size
	 */
	public ActDetailRelationParam(Context context, int actId, int typeId, int page, int size) {
		super(context, "act/activity/listNews");
		setParameter("actId", actId);
		setParameter("typeId", typeId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
