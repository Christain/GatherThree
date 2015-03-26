package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 发评论
 */
public class SendCommentParam extends StringParams {

	public SendCommentParam(Context context, int dynamicId, int atId, String content) {
		super(context, "act/dynamic/addComment");
		setParameter("dynamicId", dynamicId);
		setParameter("atId", atId);
		setParameter("content", content);
	}
	
	public SendCommentParam(Context context, int dynamicId, String content) {
		super(context, "act/dynamic/addComment");
		setParameter("dynamicId", dynamicId);
		setParameter("content", content);
	}
	
}
