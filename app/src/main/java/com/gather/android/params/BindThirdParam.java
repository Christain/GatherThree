package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 绑定第三方帐号
 */
public class BindThirdParam extends StringParams {

	/**
	 * @param openidType 第三方类型：3sina，4qq
	 * @param openid
	 * @param token
	 * @param expiresIn 第三方过期时间
	 */
	public BindThirdParam(Context context, int openidType, String openid, String token, long expiresIn) {
		super(context, "user/userInfo/setOpenid");
		setParameter("openidType", openidType);
		setParameter("openid", openid);
		setParameter("token", token);
		setParameter("expiresIn", expiresIn);
	}
}
