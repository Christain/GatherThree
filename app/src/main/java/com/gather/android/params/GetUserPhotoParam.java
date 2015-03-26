package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 获取用户照片墙
 */
public class GetUserPhotoParam extends StringParams {

	public GetUserPhotoParam(Context context, int uid, int page, int size) {
		super(context, "act/user/photos");
		setParameter("uid", uid);
		setParameter("page", page);
		setParameter("size", size);
	}

}
