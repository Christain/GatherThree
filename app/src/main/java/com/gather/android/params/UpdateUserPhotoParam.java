package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.util.ArrayList;

/**
 * 更新用户相册列表
 */
public class UpdateUserPhotoParam extends BaseParams {

	public UpdateUserPhotoParam(ArrayList<Integer> list) {
		super("act/user/updatePhotos");
		for (int i = 0; i < list.size(); i++) {
            put("imgIds[" + i + "]", list.get(i));
		}
	}

}
