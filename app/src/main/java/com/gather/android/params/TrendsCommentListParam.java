package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取动态评论列表
 */
public class TrendsCommentListParam extends BaseParams {

	public TrendsCommentListParam(int dynamicId, int page, int size) {
		super("act/dynamic/comments");
        put("dynamicId", dynamicId);
        put("page", page);
        put("size", size);
	}

}
