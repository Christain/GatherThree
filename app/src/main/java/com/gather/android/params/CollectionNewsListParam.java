package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 收藏的资讯列表
 */
public class CollectionNewsListParam extends BaseParams {

	/**
	 * @param uid
	 * @param typeId  类型id：1攻略，2回忆，3票务，4专访
	 * @param page
	 * @param size
	 */
	public CollectionNewsListParam(int uid, int typeId, int page, int size) {
		super("act/news/lovNews");
        put("uid", uid);
		if (typeId != 0) {
            put("typeId", typeId);
		}
        put("page", page);
        put("size", size);
	}

}
