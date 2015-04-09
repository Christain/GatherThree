package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 绑定第三方帐号
 */
public class BindThirdParam extends BaseParams {

	/**
	 * @param openidType 第三方类型：3sina，4qq
	 * @param openid
	 * @param token
	 * @param expiresIn 第三方过期时间
	 */
	public BindThirdParam(int openidType, String openid, String token, long expiresIn) {
		super("user/userInfo/setOpenid");
        put("openidType", openidType);
        put("openid", openid);
        put("token", token);
        put("expiresIn", expiresIn);
	}
}
