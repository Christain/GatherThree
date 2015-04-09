package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取用户照片墙
 */
public class GetUserPhotoParam extends BaseParams {

	public GetUserPhotoParam(int uid, int page, int size) {
		super("act/user/photos");
        put("uid", uid);
        put("page", page);
        put("size", size);
	}

}
