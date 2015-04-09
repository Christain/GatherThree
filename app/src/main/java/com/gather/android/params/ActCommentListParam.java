package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动评论列表
 */
public class ActCommentListParam extends BaseParams {

	public ActCommentListParam(int actId, int page, int size) {
		super("act/activity/comments");
        put("actId", actId);
        put("page", page);
        put("size", size);
	}

}
