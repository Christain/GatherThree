package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 达人的专访列表
 */
public class UserInterviewParam extends BaseParams {

	public UserInterviewParam(int uid, int page, int size) {
		super("act/vip/interview");
        put("uid", uid);
//		setParameter("page", page);
//		setParameter("size", size);
	}
}
