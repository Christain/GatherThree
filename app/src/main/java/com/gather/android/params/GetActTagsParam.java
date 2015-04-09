package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取活动标签
 */
public class GetActTagsParam extends BaseParams {

	public GetActTagsParam(int cityId) {
		super("act/tag/actTags");
        put("cityId", cityId);
        put("page", 1);
        put("size", 50);
	}

}
