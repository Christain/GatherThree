package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取活动标签
 */
public class GetActTagsParam extends StringParams {

	public GetActTagsParam(Context context, int cityId) {
		super(context, "act/tag/actTags");
		setParameter("cityId", cityId);
		setParameter("page", 1);
		setParameter("size", 50);
	}

}
