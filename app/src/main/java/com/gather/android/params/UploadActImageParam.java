package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.io.File;

/**
 * 活动相关图片上传（成功后返回图片ID）
 *
 */
public class UploadActImageParam extends BaseParams {

	public UploadActImageParam(File actImage) {
		super("act/actInfo/actImgUp");
        try {
            put("actImg", actImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
