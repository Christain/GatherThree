package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 屏蔽联系人
 */
public class ShieldContactUserParam extends BaseParams {

	public ShieldContactUserParam(int contactId) {
		super("act/message/shieldContact");
        put("contactId", contactId);
	}

}
