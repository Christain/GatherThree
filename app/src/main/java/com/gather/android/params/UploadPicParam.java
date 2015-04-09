package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.io.File;

/**
 * 上传图片（除头像）
 */
public class UploadPicParam extends BaseParams {

	public UploadPicParam(File imgFile) {
		super("act/user/imgUp");
        try {
            put("img", imgFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
