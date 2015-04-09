package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.util.ArrayList;

/**
 * 上传爆料线索
 */
public class TipOffDataParam extends BaseParams {

	public TipOffDataParam(int cityId, ArrayList<Integer> imgIdsList, String phone, String address, String other) {
		super("act/activity/brokeNews");
        put("cityId", cityId);
		for (int i = 0; i < imgIdsList.size(); i++) {
            put("imgIds[" + i + "]", imgIdsList.get(i));
		}
        put("contactPhone", phone);
        put("address", address);
		if (other != null && !other.equals("")) {
            put("intro", other);
		}
	}

}
