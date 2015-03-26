package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取动态评论列表
 */
public class TrendsCommentListParam extends StringParams {

	public TrendsCommentListParam(Context context, int dynamicId, int page, int size) {
		super(context, "act/dynamic/comments");
		setParameter("dynamicId", dynamicId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
