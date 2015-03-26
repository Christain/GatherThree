package com.gather.android.params;

import java.io.File;

import android.content.Context;

import com.gather.android.baseclass.MultipartParams;

/**
 * 上传图片（除头像）
 */
public class UploadPicParam extends MultipartParams {

	public UploadPicParam(Context context, File imgFile) {
		super(context, "act/user/imgUp");
		setParameter("img", imgFile);
	}
}
