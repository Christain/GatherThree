package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 取消关注
 */
public class CancelFocusParam extends StringParams {

	public CancelFocusParam(Context context, int focusId) {
		super(context, "act/friend/delete");
		setParameter("focusId", focusId);
	}
}
