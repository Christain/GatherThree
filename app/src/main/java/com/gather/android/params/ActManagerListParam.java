package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动管理员列表
 * Created by Christain on 2015/4/1.
 */
public class ActManagerListParam extends StringParams{

    public ActManagerListParam(Context context, int cityId, int actId, int page, int size) {
        super(context, "act/actMore/managers");
        setParameter("cityId", cityId);
        setParameter("actId", actId);
        setParameter("page", page);
        setParameter("size", size);
    }
}
