package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 第三方登录
 *
 */
public class LoginThirdParam extends BaseParams {

	/**
	 * @param loginType 0用户名，1手机号，2email，3sina，4qq
	 * @param openid
	 * @param token
	 * @param expiresIn 过期时间
	 */
	public LoginThirdParam(int loginType, String openid, String token, long expiresIn) {
		super("user/userInfo/login");
        put("loginType", loginType);
        put("openid", openid);
        put("token", token);
        put("expiresIn", expiresIn);
	}

}
