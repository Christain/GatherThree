package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.io.File;

/**
 * 上传头像，成功后返回头像Id，然后才是完善资料
 *
 */
public class RegisterUploadPhotoParam extends BaseParams {

	public RegisterUploadPhotoParam(File photo) {
		super("user/userInfo/headImgUp");
        try {
            put("headImg", photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
