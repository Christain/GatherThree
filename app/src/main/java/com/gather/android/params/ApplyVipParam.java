package com.gather.android.params;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.model.CityListModel;
import com.gather.android.model.UserInterestModel;

import java.util.ArrayList;

/**
 * 申请成为达人
 */
public class ApplyVipParam extends BaseParams {

	public ApplyVipParam() {
		super("act/vip/apply");
		GatherApplication application = GatherApplication.getInstance();
		if (application.mLocation != null) {
            put("address", application.mLocation.getAddrStr());
            put("lon", application.mLocation.getLongitude());
            put("lat", application.mLocation.getLatitude());
		}
	}
	
	/**
	 * 真实姓名
	 * @param name
	 */
	public void setRealName(String name) {
        put("realName", name);
	}

	/**
	 * 联系电话
	 * @param phone
	 */
	public void setContactPhone(String phone) {
        put("contactPhone", phone);
	}
	
	/**
	 * 常用邮箱
	 * @param email
	 */
	public void setEmail(String email) {
        put("email", email);
	}
	
	/**
	 * 申请理由
	 * @param intro
	 */
	public void setIntro(String intro) {
        put("intro", intro);
	}
	
	/**
	 * 城市
	 * @param city
	 */
	public void setCity(ArrayList<CityListModel> city) {
		for (int i = 0; i < city.size(); i++) {
            put("cityIds[" + i + "]", city.get(i).getId());
		}
	}
	
	/**
	 * 活动标签
	 * @param actTags
	 */
	public void setActTags(ArrayList<UserInterestModel> actTags) {
		for (int i = 0; i < actTags.size(); i++) {
            put("actTagIds[" + i + "]", actTags.get(i).getId());
		}
	}
	
	/**
	 * 用户标签
	 * @param userTags
	 */
	public void setUserTags(ArrayList<UserInterestModel> userTags) {
		for (int i = 0; i < userTags.size(); i++) {
            put("userTagIds[" + i + "]", userTags.get(i).getId());
		}
	}
	
	/**
	 * 头像
	 */
	public void setImgList(ArrayList<Integer> imgs) {
		for (int i = 0; i < imgs.size(); i++) {
            put("imgIds[" + i + "]", imgs.get(i));
		}
	}
	
}
