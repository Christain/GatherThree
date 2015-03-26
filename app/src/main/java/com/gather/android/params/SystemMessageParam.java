package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 系统消息列表
 */
public class SystemMessageParam extends StringParams {

	public SystemMessageParam(Context context, int page, int size) {
		super(context, "act/user/systemMsgs");
		setParameter("page", page);
		setParameter("size", size);
	}
}
