package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 注册手机账号时获取验证码
 *
 */
public class RegisterPhoneGetNumParam extends BaseParams {

	public RegisterPhoneGetNumParam(String phoneNum) {
		super("user/userInfo/newPhone");
        put("phoneNum", phoneNum);
	}

}
