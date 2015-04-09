package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 百度推送帐号绑定
 */
public class BindPushParam extends BaseParams {

	/**
	 * @param cityId
	 * @param platform 平台：3Android，4Ios
	 * @param baiduUserId
	 * @param baiduChannelId
	 */
	public BindPushParam(int cityId, int platform, String baiduUserId, String baiduChannelId) {
		super("act/user/setBaiduPush");
		if (cityId != 0) {
            put("cityId", cityId);
		}
        put("platform", 3);
        put("baiduUserId", baiduUserId);
        put("baiduChannelId", baiduChannelId);
	}
}
