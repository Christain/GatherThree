package com.gather.android.params;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.model.ActEnrollCustomKeyModel;

import java.util.ArrayList;

/**
 * 活动提交报名表
 * Created by Christain on 2015/4/1.
 */
public class ActEnrollParam extends BaseParams{

    public ActEnrollParam(int actId, String name, int sex, String birth, String phone, ArrayList<ActEnrollCustomKeyModel> list) {
        super("act/actMore/enroll");
        put("actId", actId);
        put("name", name);
        put("sex", sex);
        put("birth", birth);
        put("phone", phone);
        for (int i = 0; i < list.size(); i++) {
            put("customKeys["+i+"][id]", list.get(i).getId());
            put("customKeys["+i+"][value]", list.get(i).getContent());
        }
        GatherApplication application = GatherApplication.getInstance();
        if (application.mLocation != null) {
            put("lon", application.mLocation.getLongitude());
            put("lat", application.mLocation.getLatitude());
            put("address", application.mLocation.getAddrStr());
        }
    }
}
