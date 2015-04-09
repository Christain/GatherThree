package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取用户标签
 */
public class GetUserTagsParam extends BaseParams {

	/**
	 * @param type 类别：1类别标签，2个性标签
	 */
	public GetUserTagsParam(int type) {
		super("act/tag/userTags");
        put("type", type);
        put("page", 1);
        put("size", 50);
	}
}
