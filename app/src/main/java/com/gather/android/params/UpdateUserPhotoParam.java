package com.gather.android.params;

import java.util.ArrayList;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 更新用户相册列表
 */
public class UpdateUserPhotoParam extends StringParams {

	public UpdateUserPhotoParam(Context context, ArrayList<Integer> list) {
		super(context, "act/user/updatePhotos");
		for (int i = 0; i < list.size(); i++) {
			setParameter("imgIds[" + i + "]", list.get(i));
		}
	}

}
