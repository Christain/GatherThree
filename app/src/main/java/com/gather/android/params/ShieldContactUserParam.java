package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 屏蔽联系人
 */
public class ShieldContactUserParam extends StringParams {

	public ShieldContactUserParam(Context context, int contactId) {
		super(context, "act/message/shieldContact");
		setParameter("contactId", contactId);
	}

}
