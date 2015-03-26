package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取用户标签
 */
public class GetUserTagsParam extends StringParams {

	/**
	 * @param context
	 * @param type 类别：1类别标签，2个性标签
	 */
	public GetUserTagsParam(Context context, int type) {
		super(context, "act/tag/userTags");
		setParameter("type", type);
		setParameter("page", 1);
		setParameter("size", 50);
	}
}
