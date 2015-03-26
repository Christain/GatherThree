package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 发送私信
 */
public class SendChatMessageParam extends StringParams {

	public SendChatMessageParam(Context context, int acptUserId, String content) {
		super(context, "act/message/add");
		setParameter("revId", acptUserId);
		setParameter("content", content);
	}
}
