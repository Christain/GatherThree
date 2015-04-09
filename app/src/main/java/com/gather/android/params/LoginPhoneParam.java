package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 手机（本平台）登录
 *
 */

public class LoginPhoneParam extends BaseParams {

	/**
	 * @param loginType 0用户名，1手机号，2email
	 * @param loginName
	 * @param userPass
	 */
	public LoginPhoneParam(int loginType, String loginName, String userPass) {
		super("user/userInfo/login");
		put("loginType", loginType);
        put("loginName", loginName);
        put("userPass", userPass);
	}

}
