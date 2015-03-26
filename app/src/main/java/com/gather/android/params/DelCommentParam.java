package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 删除单条评论
 */
public class DelCommentParam extends StringParams {

	public DelCommentParam(Context context, int commentId) {
		super(context, "act/dynamic/delComment");
		setParameter("commentId", commentId);
	}

}
