package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 已评论的活动列表
 */
public class ActHasCommentListParam extends StringParams {

	public ActHasCommentListParam(Context context, int uid, int page, int size) {
		super(context, "act/activity/commentActs");
		setParameter("uid", uid);
		setParameter("page", page);
		setParameter("size", size);
	}
}
