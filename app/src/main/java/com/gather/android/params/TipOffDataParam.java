package com.gather.android.params;

import java.util.ArrayList;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 上传爆料线索
 */
public class TipOffDataParam extends StringParams {

	public TipOffDataParam(Context context, int cityId, ArrayList<Integer> imgIdsList, String phone, String address, String other) {
		super(context, "act/activity/brokeNews");
		setParameter("cityId", cityId);
		for (int i = 0; i < imgIdsList.size(); i++) {
			setParameter("imgIds[" + i + "]", imgIdsList.get(i));
		}
		setParameter("contactPhone", phone);
		setParameter("address", address);
		if (other != null && !other.equals("")) {
			setParameter("intro", other);
		}
	}

}
