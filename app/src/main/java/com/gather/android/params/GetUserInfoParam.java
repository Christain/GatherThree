package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取个人信息//自己的不用传uid
 */
public class GetUserInfoParam extends BaseParams {

	public GetUserInfoParam(int cityId) {
		super("act/user/fullprofile");
        put("cityId", cityId);
	}

	public void addUserId(int uid) {
        put("uid", uid);
	}
}
