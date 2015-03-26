package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 百度推送帐号绑定
 */
public class BindPushParam extends StringParams {

	/**
	 * @param context
	 * @param cityId
	 * @param platform 平台：3Android，4Ios
	 * @param baiduUserId
	 * @param baiduChannelId
	 */
	public BindPushParam(Context context, int cityId, int platform, String baiduUserId, String baiduChannelId) {
		super(context, "act/user/setBaiduPush");
		if (cityId != 0) {
			setParameter("cityId", cityId);
		}
		setParameter("platform", 3);
		setParameter("baiduUserId", baiduUserId);
		setParameter("baiduChannelId", baiduChannelId);
	}
}
