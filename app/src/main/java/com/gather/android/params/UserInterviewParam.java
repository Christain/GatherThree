package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 达人的专访列表
 */
public class UserInterviewParam extends StringParams {

	public UserInterviewParam(Context context, int uid, int page, int size) {
		super(context, "act/vip/interview");
		setParameter("uid", uid);
//		setParameter("page", page);
//		setParameter("size", size);
	}
}
