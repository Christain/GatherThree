package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 评论活动
 */
public class ActDetailCommentPublishParam extends StringParams {

	public ActDetailCommentPublishParam(Context context, int actId, String content) {
		super(context, "act/activity/comment");
		setParameter("actId", actId);
		setParameter("content", content);
	}

}
