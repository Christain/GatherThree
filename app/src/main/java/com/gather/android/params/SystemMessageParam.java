package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 系统消息列表
 */
public class SystemMessageParam extends BaseParams {

	public SystemMessageParam(int page, int size) {
		super("act/user/systemMsgs");
        put("page", page);
        put("size", size);
	}
}
