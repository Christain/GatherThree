package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 删除单条私信
 */
public class DelMessageParam extends BaseParams {

	public DelMessageParam(int messageId) {
		super("act/message/delete");
        put("messageId", messageId);
	}

}
