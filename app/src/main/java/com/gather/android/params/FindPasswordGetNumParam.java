package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 找回密码时获取验证码
 *
 */
public class FindPasswordGetNumParam extends BaseParams {

	public FindPasswordGetNumParam(String phoneNum) {
		super("user/userInfo/getPhCode");
        put("phoneNum", phoneNum);
	}

}
