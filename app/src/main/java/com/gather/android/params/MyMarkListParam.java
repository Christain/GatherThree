package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取已关注兴趣标签
 *
 */
public class MyMarkListParam extends StringParams {

	public MyMarkListParam(Context context) {
		super(context, "act/tagInfo/getUTags");
	}

}
