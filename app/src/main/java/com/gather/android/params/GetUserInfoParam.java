package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取个人信息//自己的不用传uid
 */
public class GetUserInfoParam extends StringParams {

	public GetUserInfoParam(Context context, int cityId) {
		super(context, "act/user/fullprofile");
		setParameter("cityId", cityId);
	}

	public void addUserId(int uid) {
		setParameter("uid", uid);
	}
}
