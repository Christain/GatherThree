package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 完善用户资料（包括头像和兴趣）在兴趣页面调用
 *
 */
public class RegisterUploadUserInfoParam extends BaseParams {

	/**
	 * @param userPass
	 * @param nickName
	 * @param sex
	 * @param birth （例：1997-07-01）
	 * @param address(可选填)
	 * @param email(可选填)
	 * @param headImgId 头像ID
	 */
	public RegisterUploadUserInfoParam(String userPass, String nickName, int sex, String birth, String address, String email, int headImgId) {
		super("user/userInfo/regiInfo");
		if (userPass != null && userPass.length() > 2) {
			put("userPass", userPass);
		}
        put("nickName", nickName);
        put("sex", sex);
        put("birth", birth);
		if (!address.equals("")) {
            put("address", address);
		}
		if (!email.equals("")) {
            put("email", email);
		}
        put("headImgId", headImgId);
	}

}
