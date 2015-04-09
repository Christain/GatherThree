package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

public class GetUserCenterParam extends BaseParams {
	public GetUserCenterParam(int cityId) {
		super("act/user/profile");
        put("cityId", cityId);
	}

	public void addUserId(int uid) {
        put("uid", uid);
	}
}
