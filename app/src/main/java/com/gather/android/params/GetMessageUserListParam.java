package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 用户私信联系人列表
 */
public class GetMessageUserListParam  extends BaseParams {

	public GetMessageUserListParam(int cityId, int page, int size) {
		super("act/message/contacts");
        put("cityId", cityId);
        put("page", page);
        put("size", size);
	}

}
