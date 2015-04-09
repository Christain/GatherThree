package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动详情相关（资讯，达人，攻略）
 *
 */
public class ActDetailRelationParam extends BaseParams {

	/**
	 * @param actId	  活动Id
	 * @param typeId  类型id：1攻略，2回忆，3票务，4专访
	 * @param page
	 * @param size
	 */
	public ActDetailRelationParam(int actId, int typeId, int page, int size) {
		super("act/activity/listNews");
        put("actId", actId);
        put("typeId", typeId);
        put("page", page);
        put("size", size);
	}

}
