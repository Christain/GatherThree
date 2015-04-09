package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 评论活动
 */
public class ActDetailCommentPublishParam extends BaseParams {

	public ActDetailCommentPublishParam(int actId, String content) {
		super("act/activity/comment");
        put("actId", actId);
        put("content", content);
	}

}
