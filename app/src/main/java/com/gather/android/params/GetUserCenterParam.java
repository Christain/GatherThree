package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

public class GetUserCenterParam extends StringParams{
	public GetUserCenterParam(Context context, int cityId) {
		super(context, "act/user/profile");
		setParameter("cityId", cityId);
	}

	public void addUserId(int uid) {
		setParameter("uid", uid);
	}
}
