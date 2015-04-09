package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.util.ArrayList;

/**
 * 修改自己的爱好（个人资料）
 */
public class UploadUserInterestParam extends BaseParams {

	public UploadUserInterestParam(ArrayList<Integer> list) {
		super("act/user/updateUserLovTags");
		for (int i = 0; i < list.size(); i++) {
            put("tagIds[" + i + "]", list.get(i));
		}
	}

}
