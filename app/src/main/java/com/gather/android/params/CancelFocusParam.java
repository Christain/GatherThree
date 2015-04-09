package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 取消关注
 */
public class CancelFocusParam extends BaseParams {

	public CancelFocusParam(int focusId) {
		super("act/friend/delete");
        put("focusId", focusId);
	}
}
