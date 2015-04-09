package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 取消对联系人的屏蔽
 */
public class CancelShieldContactUserParam extends BaseParams {

	public CancelShieldContactUserParam(int contactId) {
		super("act/message/delShield");
        put("contactId", contactId);
	}

}
