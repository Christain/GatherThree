package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 发评论
 */
public class SendCommentParam extends BaseParams {

	public SendCommentParam(int dynamicId, int atId, String content) {
		super("act/dynamic/addComment");
        put("dynamicId", dynamicId);
        put("atId", atId);
        put("content", content);
	}
	
	public SendCommentParam(int dynamicId, String content) {
		super("act/dynamic/addComment");
        put("dynamicId", dynamicId);
        put("content", content);
	}
	
}
