package com.gather.android.params;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseParams;

/**
 * 签到
 */
public class SignInParam extends BaseParams {

	public SignInParam(int checkinId) {
		super("act/actMore/checkin");
        put("checkinId", checkinId);
		GatherApplication app = GatherApplication.getInstance();
		if (app.mLocation != null && app.mLocation.getAddrStr() != null) {
            put("lon", app.mLocation.getLongitude());
            put("lat", app.mLocation.getLatitude());
            put("address", app.mLocation.getAddrStr());
		}
	}

}
