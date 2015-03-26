package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取用户爱好的可选择标签（个人资料爱好）
 */
public class UserLoveInterestParam extends StringParams {

	public UserLoveInterestParam(Context context) {
		super(context, "act/tag/userLovTags");
		setParameter("page", 1);
		setParameter("size", 30);
	}

}
