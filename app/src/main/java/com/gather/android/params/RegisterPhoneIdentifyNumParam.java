package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 验证验证码 Ps：注册和找回密码的验证接口一样
 *
 */
public class RegisterPhoneIdentifyNumParam extends BaseParams {

	/**
	 * @param type 类型：1新注册验证，2已注册验证
	 * @param phoneNum
	 * @param idfyCode
	 */
	public RegisterPhoneIdentifyNumParam(int type, String phoneNum, String idfyCode) {
		super("user/userInfo/validPhCode");
        put("type", type);
        put("phoneNum", phoneNum);
        put("idfyCode", idfyCode);
	}

}
