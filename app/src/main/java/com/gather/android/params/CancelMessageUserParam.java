package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 删除私信联系人
 */
public class CancelMessageUserParam extends StringParams {

	public CancelMessageUserParam(Context context, int contactId) {
		super(context, "act/message/delContact");
		setParameter("contactId", contactId);
	}

}
