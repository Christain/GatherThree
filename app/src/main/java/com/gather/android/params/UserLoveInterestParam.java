package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取用户爱好的可选择标签（个人资料爱好）
 */
public class UserLoveInterestParam extends BaseParams {

	public UserLoveInterestParam() {
		super("act/tag/userLovTags");
        put("page", 1);
        put("size", 30);
	}

}
