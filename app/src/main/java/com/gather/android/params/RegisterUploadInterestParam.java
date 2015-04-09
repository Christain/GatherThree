package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.util.ArrayList;

/**
 * 上传兴趣标签
 *
 */
public class RegisterUploadInterestParam extends BaseParams {

	public RegisterUploadInterestParam(ArrayList<Integer> tagIds) {
		super("act/tagInfo/setUTags");
		if (tagIds != null && tagIds.size() != 0) {
			for (int i = 0; i < tagIds.size(); i++) {
                put("tagIds["+i+"]", tagIds.get(i));
			}
		}
	}

}
