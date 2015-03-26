package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取可选兴趣标签
 *
 */
public class RegisterGetInterestParam extends StringParams {

	public RegisterGetInterestParam(Context context) {
		super(context, "act/tagInfo/getSltTags");
	}

}
