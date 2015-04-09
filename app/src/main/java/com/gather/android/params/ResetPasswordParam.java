package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 修改密码
 *
 */
public class ResetPasswordParam extends BaseParams {

	public ResetPasswordParam(String newPass) {
		super("user/userInfo/phRePass");
        put("newPass", newPass);
	}

}
