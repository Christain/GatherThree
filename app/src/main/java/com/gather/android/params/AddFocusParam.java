package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 加关注
 */
public class AddFocusParam extends StringParams {

	public AddFocusParam(Context context, int focusId) {
		super(context, "act/friend/add");
		setParameter("focusId", focusId);
	}
}
