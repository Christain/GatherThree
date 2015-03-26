package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动评论列表
 */
public class ActCommentListParam extends StringParams {

	public ActCommentListParam(Context context, int actId, int page, int size) {
		super(context, "act/activity/comments");
		setParameter("actId", actId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
