package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

public class FansAndFocusListParam extends StringParams {

	/**
	 * 获取粉丝关注列表
	 * @param context
	 * @param uid//用户id
	 * @param cityId//城市id
	 * @param type//类型：1关注，2粉丝
	 * @param page//分页：页码（1为首页）
	 * @param size//分页：每页容量
	 */
	public FansAndFocusListParam(Context context, int uid, int cityId, int type, int page, int size) {
		super(context, "act/friend/list");
		setParameter("uid", uid);
		setParameter("cityId", cityId);
		setParameter("type", type);
		setParameter("page", page);
		setParameter("size", size);
	}
}
