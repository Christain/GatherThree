package com.gather.android.params;

import java.util.ArrayList;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 修改自己的爱好（个人资料）
 */
public class UploadUserInterestParam extends StringParams {

	public UploadUserInterestParam(Context context, ArrayList<Integer> list) {
		super(context, "act/user/updateUserLovTags");
		for (int i = 0; i < list.size(); i++) {
			setParameter("tagIds[" + i + "]", list.get(i));
		}
	}

}
