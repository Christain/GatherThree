package com.gather.android.params;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseParams;

import java.util.ArrayList;

public class FeedbackParam extends BaseParams {

	public FeedbackParam(String content, ArrayList<Integer> imgIds) {
		super("act/user/feedback");
		GatherApplication application = GatherApplication.getInstance();
		if (application.getCityId() != 0) {
            put("cityId", application.getCityId());
		}
        put("content", content);
		if (imgIds.size() > 0) {
			for (int i = 0; i < imgIds.size(); i++) {
                put("imgIds[" + i + "]", imgIds.get(i));
			}
		}
		if (application.mLocation != null && application.mLocation.getAddrStr() != null) {
            put("lon", application.mLocation.getLongitude());
            put("lat", application.mLocation.getLatitude());
            put("address", application.mLocation.getAddrStr());
		}
	}

}
