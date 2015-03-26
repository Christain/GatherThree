package com.gather.android.params;

import android.content.Context;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.StringParams;

/**
 * 签到
 */
public class SignInParam extends StringParams {

	public SignInParam(Context context, int actId) {
		super(context, "/act/activity/checkin");
		setParameter("actId", actId);
		GatherApplication app = (GatherApplication) context.getApplicationContext();
		if (app.mLocation != null && app.mLocation.getAddrStr() != null) {
			setParameter("lon", app.mLocation.getLongitude());
			setParameter("lat", app.mLocation.getLatitude());
			setParameter("address", app.mLocation.getAddrStr());
		}
	}

}
