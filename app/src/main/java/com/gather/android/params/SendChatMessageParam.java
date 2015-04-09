package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 发送私信
 */
public class SendChatMessageParam extends BaseParams {

	public SendChatMessageParam(int acptUserId, String content) {
		super("act/message/add");
        put("revId", acptUserId);
        put("content", content);
	}
}
