package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 删除私信联系人
 */
public class CancelMessageUserParam extends BaseParams {

	public CancelMessageUserParam(int contactId) {
		super("act/message/delContact");
        put("contactId", contactId);
	}

}
