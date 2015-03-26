package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 删除单条私信
 */
public class DelMessageParam extends StringParams {

	public DelMessageParam(Context context, int messageId) {
		super(context, "act/message/delete");
		setParameter("messageId", messageId);
	}

}
