package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 用户已经选择了的爱好标签
 */
public class MyLoveInterestParam extends BaseParams {

	public MyLoveInterestParam(int uid) {
		super("act/user/userLovTags");
        put("page", 1);
        put("size", 10);
        put("uid", uid);
	}

}
