package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 删除单条评论
 */
public class DelCommentParam extends BaseParams {

	public DelCommentParam(int commentId) {
		super("act/dynamic/delComment");
        put("commentId", commentId);
	}

}
