package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 加关注
 */
public class AddFocusParam extends BaseParams {

	public AddFocusParam(int focusId) {
		super("act/friend/add");
        put("focusId", focusId);
	}
}
