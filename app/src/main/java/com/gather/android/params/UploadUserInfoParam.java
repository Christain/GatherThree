package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 修改个人信息
 */
public class UploadUserInfoParam extends BaseParams {
	
	public UploadUserInfoParam () {
		super("act/user/updateFullprofile");
	}

	public UploadUserInfoParam(String nickName, int sex, String birth, String address, String real_name, String contact_phone, int headImgId) {
		super("act/user/updateFullprofile");
        put("nickName", nickName);
        put("sex", sex);
		if (birth.contains(" 00:00:00")) {
			birth = birth.substring(0, birth.indexOf(" "));
		}
        put("birth", birth);
        put("address", address);
        put("realName", real_name);
        put("contactPhone", contact_phone);
		if (headImgId != 0) {
            put("headImgId", headImgId);
		}
	}
	
	/**
	 * 修改昵称
	 * @param nickName
	 */
	public void SaveNickName(String nickName) {
        put("nickName", nickName);
	}
	
	/**
	 * 修改性别
	 * @param sex
	 */
	public void SaveSex(int sex) {
        put("sex", sex);
	}
	
	/**
	 * 修改年龄
	 * @param birth
	 */
	public void SaveAge(String birth) {
		if (birth.contains(" 00:00:00")) {
			birth = birth.substring(0, birth.indexOf(" "));
		}
        put("birth", birth);
	}
	
	/**
	 * 修改地址
	 * @param address
	 */
	public void SaveAddress(String address) {
        put("address", address);
	}
	
	/**
	 * 修改个性签名
	 * @param intro
	 */
	public void SaveBrief(String intro) {
        put("intro", intro);
	}
	
	/**
	 * 爱好
	 * @param hobby
	 */
	public void SaveHobby(String hobby) {
        put("hobby", hobby);
	}
	
	/**
	 * 修改姓名
	 * @param name
	 */
	public void SaveUserName(String name) {
        put("realName", name);
	}
	
	/**
	 * 修改联系电话
	 */
	public void SavePhone(String phone) {
        put("contactPhone", phone);
	}
	
	/**
	 * 修改头像
	 * @param headImgId
	 */
	public void SaveUserIcon(int headImgId) {
		if (headImgId != 0) {
            put("headImgId", headImgId);
		}
	}
}
