package com.gather.android.params;

import android.content.Context;

import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.StringParams;
import com.gather.android.model.ActEnrollCustomKeyModel;

import java.util.ArrayList;

/**
 * 活动提交报名表
 * Created by Christain on 2015/4/1.
 */
public class ActEnrollParam extends StringParams{

    public ActEnrollParam(Context context, int actId, String name, int sex, String birth, String phone, ArrayList<ActEnrollCustomKeyModel> list) {
        super(context, "act/actMore/enroll");
        setParameter("actId", actId);
        setParameter("name", name);
        setParameter("sex", sex);
        setParameter("birth", birth);
        setParameter("phone", phone);
        for (int i = 0; i < list.size(); i++) {
            setParameter("customKeys["+i+"][id]", list.get(i).getId());
            setParameter("customKeys["+i+"][value]", list.get(i).getContent());
        }
        GatherApplication application = (GatherApplication) context.getApplicationContext();
        if (application.mLocation != null) {
            setParameter("lon", application.mLocation.getLongitude());
            setParameter("lat", application.mLocation.getLatitude());
            setParameter("address", application.mLocation.getAddrStr());
        }
    }
}
