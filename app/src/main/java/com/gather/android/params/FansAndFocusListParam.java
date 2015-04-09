package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

public class FansAndFocusListParam extends BaseParams {

	/**
	 * 获取粉丝关注列表
	 * @param uid//用户id
	 * @param cityId//城市id
	 * @param type//类型：1关注，2粉丝
	 * @param page//分页：页码（1为首页）
	 * @param size//分页：每页容量
	 */
	public FansAndFocusListParam(int uid, int cityId, int type, int page, int size) {
		super("act/friend/list");
        put("uid", uid);
        put("cityId", cityId);
        put("type", type);
        put("page", page);
        put("size", size);
	}
}
