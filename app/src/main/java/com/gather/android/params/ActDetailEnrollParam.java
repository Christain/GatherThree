package com.gather.android.params;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseParams;

/**
 * 活动报名
 */
public class ActDetailEnrollParam extends BaseParams {

	public ActDetailEnrollParam(int actId, String name, String phone, String people_num) {
		super("act/activity/enroll");
        put("actId", actId);
        put("name", name);
        put("phone", phone);
        put("peopleNum", people_num);
		GatherApplication application = GatherApplication.getInstance();
		if (application.mLocation != null) {
            put("lon", application.mLocation.getLongitude());
            put("lat", application.mLocation.getLatitude());
            put("address", application.mLocation.getAddrStr());
		}
	}

}
