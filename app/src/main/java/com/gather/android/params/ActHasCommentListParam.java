package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 已评论的活动列表
 */
public class ActHasCommentListParam extends BaseParams {

	public ActHasCommentListParam(int uid, int page, int size) {
		super("act/activity/commentActs");
        put("uid", uid);
        put("page", page);
        put("size", size);
	}
}
