package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取和某人的私信内容
 */
public class GetMessageContentParam extends BaseParams {

	public GetMessageContentParam(int userId, int page, int size) {
		super("act/message/history");
        put("contactId", userId);
        put("page", page);
        put("size", size);
	}

}
