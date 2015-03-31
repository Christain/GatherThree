package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动通知
 * Created by Christain on 2015/3/31.
 */
public class ActNotifyParam extends StringParams {

    public ActNotifyParam(Context context, int actId) {
        super(context, "act/actMore/notices");
        setParameter("actId", actId);
        setParameter("page", 1);
        setParameter("size", 50);
    }
}
