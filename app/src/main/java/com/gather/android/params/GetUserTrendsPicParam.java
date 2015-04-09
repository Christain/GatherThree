package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取用户动态图片
 */
public class GetUserTrendsPicParam extends BaseParams {

	public GetUserTrendsPicParam(int uid, int page, int size) {
		super("act/dynamic/photos");
        put("uid", uid);
        put("page", page);
        put("size", size);
	}

}
