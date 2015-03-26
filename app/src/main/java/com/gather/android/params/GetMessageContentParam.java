package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取和某人的私信内容
 */
public class GetMessageContentParam extends StringParams {

	public GetMessageContentParam(Context context, int userId, int page, int size) {
		super(context, "act/message/history");
		setParameter("contactId", userId);
		setParameter("page", page);
		setParameter("size", size);
	}

}
