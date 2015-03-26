package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 用户私信联系人列表
 */
public class GetMessageUserListParam  extends StringParams {

	public GetMessageUserListParam(Context context,int cityId, int page, int size) {
		super(context, "act/message/contacts");
		setParameter("cityId", cityId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
