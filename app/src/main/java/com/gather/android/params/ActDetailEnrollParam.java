package com.gather.android.params;

import android.content.Context;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.StringParams;

/**
 * 活动报名
 */
public class ActDetailEnrollParam extends StringParams {

	public ActDetailEnrollParam(Context context, int actId, String name, String phone, String people_num) {
		super(context, "act/activity/enroll");
		setParameter("actId", actId);
		setParameter("name", name);
		setParameter("phone", phone);
		setParameter("peopleNum", people_num);
		GatherApplication application = (GatherApplication) context.getApplicationContext();
		if (application.mLocation != null) {
			setParameter("lon", application.mLocation.getLongitude());
			setParameter("lat", application.mLocation.getLatitude());
			setParameter("address", application.mLocation.getAddrStr());
		}
	}

}
