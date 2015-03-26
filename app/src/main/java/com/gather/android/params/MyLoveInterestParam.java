package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 用户已经选择了的爱好标签
 */
public class MyLoveInterestParam extends StringParams {

	public MyLoveInterestParam(Context context, int uid) {
		super(context, "act/user/userLovTags");
		setParameter("page", 1);
		setParameter("size", 10);
		setParameter("uid", uid);
	}

}
