package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动成员列表
 * Created by Christain on 2015/4/1.
 */
public class ActMemeberListParam extends StringParams{

    public ActMemeberListParam(Context context, int cityId, int actId, int page, int size) {
        super(context, "act/actMore/members");
        setParameter("cityId", cityId);
        setParameter("actId", actId);
        setParameter("page", page);
        setParameter("size", size);

    }
}
