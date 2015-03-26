package com.gather.android.params;

import java.util.ArrayList;

import android.content.Context;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.StringParams;
import com.gather.android.model.CityListModel;
import com.gather.android.model.UserInterestModel;

/**
 * 申请成为达人
 */
public class ApplyVipParam extends StringParams {

	public ApplyVipParam(Context context) {
		super(context, "act/vip/apply");
		GatherApplication application = (GatherApplication) context.getApplicationContext();
		if (application.mLocation != null) {
			setParameter("address", application.mLocation.getAddrStr());
			setParameter("lon", application.mLocation.getLongitude());
			setParameter("lat", application.mLocation.getLatitude());
		}
	}
	
	/**
	 * 真实姓名
	 * @param name
	 */
	public void setRealName(String name) {
		setParameter("realName", name);
	}

	/**
	 * 联系电话
	 * @param phone
	 */
	public void setContactPhone(String phone) {
		setParameter("contactPhone", phone);
	}
	
	/**
	 * 常用邮箱
	 * @param email
	 */
	public void setEmail(String email) {
		setParameter("email", email);
	}
	
	/**
	 * 申请理由
	 * @param intro
	 */
	public void setIntro(String intro) {
		setParameter("intro", intro);
	}
	
	/**
	 * 城市
	 * @param city
	 */
	public void setCity(ArrayList<CityListModel> city) {
		for (int i = 0; i < city.size(); i++) {
			setParameter("cityIds[" + i + "]", city.get(i).getId());
		}
	}
	
	/**
	 * 活动标签
	 * @param actTags
	 */
	public void setActTags(ArrayList<UserInterestModel> actTags) {
		for (int i = 0; i < actTags.size(); i++) {
			setParameter("actTagIds[" + i + "]", actTags.get(i).getId());
		}
	}
	
	/**
	 * 用户标签
	 * @param userTags
	 */
	public void setUserTags(ArrayList<UserInterestModel> userTags) {
		for (int i = 0; i < userTags.size(); i++) {
			setParameter("userTagIds[" + i + "]", userTags.get(i).getId());
		}
	}
	
	/**
	 * 头像
	 * @param city
	 */
	public void setImgList(ArrayList<Integer> imgs) {
		for (int i = 0; i < imgs.size(); i++) {
			setParameter("imgIds[" + i + "]", imgs.get(i));
		}
	}
	
}
