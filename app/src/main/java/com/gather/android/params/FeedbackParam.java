package com.gather.android.params;

import java.util.ArrayList;

import android.content.Context;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.StringParams;

public class FeedbackParam extends StringParams {

	public FeedbackParam(Context context, String content, ArrayList<Integer> imgIds) {
		super(context, "act/user/feedback");
		GatherApplication application = (GatherApplication) context.getApplicationContext();
		if (application.getCityId() != 0) {
			setParameter("cityId", application.getCityId());
		}
		setParameter("content", content);
		if (imgIds.size() > 0) {
			for (int i = 0; i < imgIds.size(); i++) {
				setParameter("imgIds[" + i + "]", imgIds.get(i));
			}
		}
		if (application.mLocation != null && application.mLocation.getAddrStr() != null) {
			setParameter("lon", application.mLocation.getLongitude());
			setParameter("lat", application.mLocation.getLatitude());
			setParameter("address", application.mLocation.getAddrStr());
		}
	}

}
