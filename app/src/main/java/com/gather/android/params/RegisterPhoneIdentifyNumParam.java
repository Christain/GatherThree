package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 验证验证码 Ps：注册和找回密码的验证接口一样
 *
 */
public class RegisterPhoneIdentifyNumParam extends StringParams {

	/**
	 * @param type 类型：1新注册验证，2已注册验证
	 * @param phoneNum
	 * @param idfyCode
	 */
	public RegisterPhoneIdentifyNumParam(Context context,int type, String phoneNum, String idfyCode) {
		super(context, "user/userInfo/validPhCode");
		setParameter("type", type);
		setParameter("phoneNum", phoneNum);
		setParameter("idfyCode", idfyCode);
	}

}
