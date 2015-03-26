package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 取消对联系人的屏蔽
 */
public class CancelShieldContactUserParam extends StringParams {

	public CancelShieldContactUserParam(Context context, int contactId) {
		super(context, "act/message/delShield");
		setParameter("contactId", contactId);
	}

}
