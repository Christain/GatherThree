package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动报名自定义字段
 * Created by Christain on 2015/3/31.
 */
public class EnrollCustomKeyParam extends StringParams {

    public EnrollCustomKeyParam(Context context, int actId) {
        super(context, "act/actMore/enrollCusKeys");
        setParameter("actId", actId);
        setParameter("page", 1);
        setParameter("size", 30);
    }
}
