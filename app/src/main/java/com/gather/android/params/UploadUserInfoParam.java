package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.MultipartParams;
import com.gather.android.baseclass.StringParams;

/**
 * 修改个人信息
 */
public class UploadUserInfoParam extends StringParams {
	
	public UploadUserInfoParam (Context context) {
		super(context, "act/user/updateFullprofile");
	}

	public UploadUserInfoParam(Context context, String nickName, int sex, String birth, String address, String real_name, String contact_phone, int headImgId) {
		super(context, "act/user/updateFullprofile");
		setParameter("nickName", nickName);
		setParameter("sex", sex);
		if (birth.contains(" 00:00:00")) {
			birth = birth.substring(0, birth.indexOf(" "));
		}
		setParameter("birth", birth);
		setParameter("address", address);
		setParameter("realName", real_name);
		setParameter("contactPhone", contact_phone);
		if (headImgId != 0) {
			setParameter("headImgId", headImgId);
		}
	}
	
	/**
	 * 修改昵称
	 * @param nickName
	 */
	public void SaveNickName(String nickName) {
		setParameter("nickName", nickName);
	}
	
	/**
	 * 修改性别
	 * @param sex
	 */
	public void SaveSex(int sex) {
		setParameter("sex", sex);
	}
	
	/**
	 * 修改年龄
	 * @param birth
	 */
	public void SaveAge(String birth) {
		if (birth.contains(" 00:00:00")) {
			birth = birth.substring(0, birth.indexOf(" "));
		}
		setParameter("birth", birth);
	}
	
	/**
	 * 修改地址
	 * @param address
	 */
	public void SaveAddress(String address) {
		setParameter("address", address);
	}
	
	/**
	 * 修改个性签名
	 * @param brief
	 */
	public void SaveBrief(String intro) {
		setParameter("intro", intro);
	}
	
	/**
	 * 爱好
	 * @param hobby
	 */
	public void SaveHobby(String hobby) {
		setParameter("hobby", hobby);
	}
	
	/**
	 * 修改姓名
	 * @param name
	 */
	public void SaveUserName(String name) {
		setParameter("realName", name);
	}
	
	/**
	 * 修改联系电话
	 */
	public void SavePhone(String phone) {
		setParameter("contactPhone", phone);
	}
	
	/**
	 * 修改头像
	 * @param headImgId
	 */
	public void SaveUserIcon(int headImgId) {
		if (headImgId != 0) {
			setParameter("headImgId", headImgId);
		}
	}
}
